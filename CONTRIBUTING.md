# Contributing to Distributed Storage System

Thank you for your interest in contributing to our Distributed Storage System! This document provides guidelines and information for contributors.

## 🤝 How to Contribute

### Before You Start
1. **Fork the repository** to your GitHub account
2. **Clone your fork** locally
3. **Create a feature branch** from `main`
4. **Set up the development environment** (see Quick Start in README)

### Development Workflow

1. **Create a new branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**
   - Follow the coding standards below
   - Add tests for new functionality
   - Update documentation if needed

3. **Test your changes**
   ```bash
   # Run all tests
   mvn test
   
   # Run specific module tests
   mvn test -f spring-boot-api/pom.xml
   ```

4. **Commit your changes**
   ```bash
   git commit -m "feat: add new storage feature"
   ```

5. **Push to your fork**
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**
   - Use the PR template
   - Describe your changes clearly
   - Link any related issues

## 📋 Pull Request Guidelines

### PR Template
```markdown
## Description
Brief description of the changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Testing
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Manual testing completed

## Checklist
- [ ] Code follows style guidelines
- [ ] Self-review completed
- [ ] Documentation updated
- [ ] No breaking changes (or documented)
```

### Commit Message Format
We use [Conventional Commits](https://www.conventionalcommits.org/):

```
type(scope): description

Examples:
feat(api): add new storage endpoint
fix(core): resolve raft consensus issue
docs(readme): update installation instructions
test(storage): add unit tests for cache service
```

## 🛠️ Development Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- IDE (IntelliJ IDEA, VS Code, etc.)

### Local Development
```bash
# Clone and setup
git clone https://github.com/yourusername/distributed-storage-system.git
cd distributed-storage-system

# Build core components
mvn clean install -f distribute-java-core/pom.xml
mvn clean install -f distribute-java-cluster/pom.xml
mvn clean install -f spring-boot-api/pom.xml

# Start development services
docker-compose up -d redis
mvn spring-boot:run -f spring-boot-api/pom.xml -Dspring-boot.run.profiles=dev
cd frontend && npm start
```

## 📝 Coding Standards

### Java (Spring Boot)
- Use **Java 17** features
- Follow **Spring Boot best practices**
- Use **Lombok** for boilerplate reduction
- Write **comprehensive unit tests**
- Use **meaningful variable names**
- Add **JavaDoc** for public methods

### TypeScript/React
- Use **TypeScript** for type safety
- Follow **React hooks** patterns
- Use **functional components**
- Implement **proper error handling**
- Write **component tests**

### General
- **Keep functions small** and focused
- **Use meaningful names** for variables and functions
- **Add comments** for complex logic
- **Handle errors** gracefully
- **Write tests** for new features

## 🧪 Testing Guidelines

### Unit Tests
- **Coverage target**: 80%+
- **Test naming**: `should_expectedBehavior_when_stateUnderTest`
- **Mock external dependencies**
- **Test edge cases**

### Integration Tests
- **Test API endpoints**
- **Verify database operations**
- **Test error scenarios**
- **Performance testing**

### Example Test
```java
@Test
void should_returnUser_when_validCredentialsProvided() {
    // Given
    LoginRequest request = new LoginRequest("admin", "password");
    
    // When
    ApiResponse<String> response = authService.login(request);
    
    // Then
    assertThat(response.isSuccess()).isTrue();
    assertThat(response.getData()).isNotNull();
}
```

## 🔍 Code Review Process

### What We Look For
- **Code quality** and readability
- **Test coverage** and quality
- **Performance** considerations
- **Security** implications
- **Documentation** updates

### Review Checklist
- [ ] Code follows style guidelines
- [ ] Tests are comprehensive
- [ ] No security vulnerabilities
- [ ] Documentation is updated
- [ ] Performance is acceptable
- [ ] Error handling is proper

## 🐛 Bug Reports

### Bug Report Template
```markdown
## Bug Description
Clear description of the issue

## Steps to Reproduce
1. Step 1
2. Step 2
3. Step 3

## Expected Behavior
What should happen

## Actual Behavior
What actually happens

## Environment
- OS: [e.g., macOS 12.0]
- Java: [e.g., 17.0.1]
- Node.js: [e.g., 18.0.0]

## Additional Information
Screenshots, logs, etc.
```

## 💡 Feature Requests

### Feature Request Template
```markdown
## Feature Description
Clear description of the requested feature

## Use Case
Why this feature is needed

## Proposed Solution
How you think it should be implemented

## Alternatives Considered
Other approaches you've considered

## Additional Information
Any other relevant details
```

## 📚 Documentation

### What to Document
- **API endpoints** and usage
- **Configuration** options
- **Deployment** procedures
- **Troubleshooting** guides
- **Architecture** decisions

### Documentation Standards
- Use **clear, concise language**
- Include **code examples**
- Add **screenshots** when helpful
- Keep **up to date** with code changes

## 🏷️ Issue Labels

We use the following labels:
- `bug` - Something isn't working
- `enhancement` - New feature or request
- `documentation` - Improvements to documentation
- `good first issue` - Good for newcomers
- `help wanted` - Extra attention is needed
- `priority: high` - Urgent issues
- `priority: low` - Nice to have

## 🎉 Recognition

Contributors will be recognized in:
- **README.md** contributors section
- **Release notes**
- **Project documentation**

## 📞 Getting Help

- **GitHub Issues**: For bugs and feature requests
- **GitHub Discussions**: For questions and ideas
- **Wiki**: For detailed documentation
- **Email**: For private matters

## 📄 License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to our Distributed Storage System! 🚀 