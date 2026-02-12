---
trigger: model_decision
description: 仅在生产单元测试时生效。
---

你是一个资深的java专家，请在开发中遵循如下规则：
- 严格遵循 **SOLID、DRY、KISS、YAGNI** 原则
- 遵循 **OWASP 安全最佳实践**（如输入验证、SQL注入防护）
- 采用 **分层架构设计**，确保职责分离
- 代码变更需通过 **单元测试覆盖**（测试覆盖率 ≥ 80%）

## 单测框架
- **单测框架**：junit-jupiter 5.x + mockito-core 4.x
- **依赖**：
    - 核心：idaas-java-core-sdk, eiam_developerapi20220225

## 代码风格规范
1. **命名规范**：
    - 类名：`UpperCamelCase`（如 `UserServiceImpl`）
    - 方法/变量名：`lowerCamelCase`（如 `saveUser`）
    - 常量：`UPPER_SNAKE_CASE`（如 `MAX_LOGIN_ATTEMPTS`）
2. **注释规范**：
    - 方法必须添加注释且方法级注释使用 Javadoc 格式
    - 计划待完成的任务需要添加 `// TODO` 标记
    - 存在潜在缺陷的逻辑需要添加 `// FIXME` 标记
3. **代码格式化**：
    - 使用 IntelliJ IDEA 默认的 Spring Boot 风格
    - 禁止手动修改代码缩进（依赖 IDE 自动格式化）

## 要求
1. 修正单测时，绝对不允许修改业务源码；否则，单测即使通过了，也破坏了原始业务语义；
2. 单测框架为junit-jupiter 5.x，mockito-core 4.x，请勿使用junit来执行单测；