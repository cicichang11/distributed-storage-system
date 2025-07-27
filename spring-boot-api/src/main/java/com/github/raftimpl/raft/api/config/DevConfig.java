package com.github.raftimpl.raft.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Development configuration - empty for now
 */
@Configuration
@Profile("dev")
public class DevConfig {
    // Empty configuration - components are excluded via ComponentScan filters
} 