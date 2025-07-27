package com.github.raftimpl.raft.service.impl;

import com.github.raftimpl.raft.ConsensusNode;
import com.github.raftimpl.raft.ClusterNode;
import com.github.raftimpl.raft.proto.RaftProto;
import com.github.raftimpl.raft.service.RaftClientService;
import com.github.raftimpl.raft.util.ConfigurationUtils;
import com.googlecode.protobuf.format.JsonFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * raft集群配置变更接口实现类
 */
public class RaftClientServiceImpl implements RaftClientService {

    private static final Logger LOG = LoggerFactory.getLogger(RaftClientServiceImpl.class);
    private static final JsonFormat jsonFormat = new JsonFormat();

    private ConsensusNode raftNode;
    private Lock lock = new ReentrantLock();

    public RaftClientServiceImpl(ConsensusNode raftNode) {
        this.raftNode = raftNode;
    }

    @Override
    public RaftProto.GetLeaderResponse getNowLeader(RaftProto.GetLeaderRequest request) {
        RaftProto.GetLeaderResponse.Builder responseBuilder = RaftProto.GetLeaderResponse.newBuilder();
        responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_SUCCESS);
        RaftProto.Endpoint.Builder endPointBuilder = RaftProto.Endpoint.newBuilder();
        Lock raftLock = raftNode.getLock();
        raftLock.lock();
        try {
            int leaderId = raftNode.getLeaderId();
            if (leaderId == 0) {
                responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_FAIL);
            } else if (leaderId == raftNode.getLocalServer().getServerId()) {
                endPointBuilder.setHost(raftNode.getLocalServer().getEndpoint().getHost());
                endPointBuilder.setPort(raftNode.getLocalServer().getEndpoint().getPort());
                responseBuilder.setLeader(endPointBuilder.build());
            } else {
                RaftProto.Configuration config = raftNode.getConfig();
                for (RaftProto.Server server : config.getServersList()) {
                    if (server.getServerId() == leaderId) {
                        responseBuilder.setLeader(server.getEndpoint());
                        break;
                    }
                }
            }
        } finally {
            raftLock.unlock();
        }
        RaftProto.GetLeaderResponse response = responseBuilder.build();
        return response;
    }

    @Override
    public RaftProto.GetConfigurationResponse getConfig(RaftProto.GetConfigurationRequest request) {
        RaftProto.GetConfigurationResponse.Builder responseBuilder
                = RaftProto.GetConfigurationResponse.newBuilder();
        Lock raftLock = raftNode.getLock();
        raftLock.lock();
        try {
            RaftProto.Configuration configuration = raftNode.getConfig();
            RaftProto.Server leader = ConfigurationUtils.getStorageServer(configuration, raftNode.getLeaderId());
            responseBuilder.setLeader(leader);
            responseBuilder.addAllServers(configuration.getServersList());
            responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_SUCCESS);
        } finally {
            raftLock.unlock();
        }
        RaftProto.GetConfigurationResponse response = responseBuilder.build();
        return response;
    }

    @Override
    public RaftProto.AddPeersResponse addStoragePeers(RaftProto.AddPeersRequest request) {
        RaftProto.AddPeersResponse.Builder responseBuilder = RaftProto.AddPeersResponse.newBuilder();
        responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_FAIL);
        
        if (request.getServersCount() == 0
                || request.getServersCount() % 2 != 0) {
            LOG.warn("added server's size can only multiple of 2");
            responseBuilder.setResMsg("added server's size can only multiple of 2");
            return responseBuilder.build();
        }
        
        for (RaftProto.Server server : request.getServersList()) {
            if (raftNode.getPeerMap().containsKey(server.getServerId())) {
                LOG.warn("already be added/adding to configuration");
                responseBuilder.setResMsg("already be added/adding to configuration");
                return responseBuilder.build();
            }
        }
        
        List<ClusterNode> requestPeers = new ArrayList<>(request.getServersCount());
        for (RaftProto.Server server : request.getServersList()) {
            final ClusterNode peer = new ClusterNode(server);
            peer.setNextIndex(1);
            requestPeers.add(peer);
            raftNode.getPeerMap().putIfAbsent(server.getServerId(), peer);
            raftNode.getExecutorService().submit(new Runnable() {
                @Override
                public void run() {
                    raftNode.appendEntries(peer);
                }
            });
        }

        int catchUpNum = 0;
        raftNode.getLock().lock();
        try {
            while (catchUpNum < requestPeers.size()) {
                try {
                    raftNode.getCatchUpCondition().await();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                catchUpNum = 0;
                for (ClusterNode peer : requestPeers) {
                    if (peer.isCatchUp()) {
                        catchUpNum++;
                    }
                }
                if (catchUpNum == requestPeers.size()) {
                    break;
                }
            }
        } finally {
            raftNode.getLock().unlock();
        }

        if (catchUpNum == requestPeers.size()) {
            raftNode.getLock().lock();
            byte[] configurationData;
            RaftProto.Configuration newConfiguration;
            try {
                newConfiguration = RaftProto.Configuration.newBuilder(raftNode.getConfig())
                        .addAllServers(request.getServersList()).build();
                configurationData = newConfiguration.toByteArray();
            } finally {
                raftNode.getLock().unlock();
            }
            boolean success = raftNode.replicate(configurationData, RaftProto.EntryType.ENTRY_TYPE_CONFIGURATION);
            if (success) {
                responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_SUCCESS);
            }
        }
        
        if (responseBuilder.getResCode() != RaftProto.ResCode.RES_CODE_SUCCESS) {
            raftNode.getLock().lock();
            try {
                for (ClusterNode peer : requestPeers) {
                    peer.getRpcClient().stop();
                    raftNode.getPeerMap().remove(peer.getStorageServer().getServerId());
                }
            } finally {
                raftNode.getLock().unlock();
            }
        }

        RaftProto.AddPeersResponse response = responseBuilder.build();
        LOG.info("addPeers request={} resCode={}",
                jsonFormat.printToString(request), response.getResCode());

        return response;
    }

    @Override
    public RaftProto.RemovePeersResponse removeStoragePeers(RaftProto.RemovePeersRequest request) {
        RaftProto.RemovePeersResponse.Builder responseBuilder = RaftProto.RemovePeersResponse.newBuilder();
        responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_FAIL);
        if (request.getServersCount() == 0
                || request.getServersCount() % 2 != 0) {
            LOG.warn("removed server's size can only multiple of 2");
            responseBuilder.setResMsg("removed server's size can only multiple of 2");
            return responseBuilder.build();
        }

        // check request peers exist
        for (RaftProto.Server server : request.getServersList()) {
            if (!raftNode.getPeerMap().containsKey(server.getServerId())) {
                LOG.warn("peer={} not exist", server.getServerId());
                responseBuilder.setResMsg("peer not exist");
                return responseBuilder.build();
            }
        }

        raftNode.getLock().lock();
        byte[] configurationData;
        RaftProto.Configuration newConfiguration;
        try {
            newConfiguration = ConfigurationUtils.removeStorageServers(
                    raftNode.getConfig(), request.getServersList());
            LOG.debug("newConfiguration={}", jsonFormat.printToString(newConfiguration));
            configurationData = newConfiguration.toByteArray();
        } finally {
            raftNode.getLock().unlock();
        }
        boolean success = raftNode.replicate(configurationData, RaftProto.EntryType.ENTRY_TYPE_CONFIGURATION);
        if (success) {
            responseBuilder.setResCode(RaftProto.ResCode.RES_CODE_SUCCESS);
        }

        LOG.info("removePeers request={} resCode={}",
                jsonFormat.printToString(request), responseBuilder.getResCode());

        return responseBuilder.build();
    }
}
