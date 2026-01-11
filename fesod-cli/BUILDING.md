# Apache Fesod CLI - 构建指南

## 系统要求

- JDK 8 或更高版本
- Maven 3.6+
- 至少 2GB 可用内存

## 构建步骤

### 1. 克隆项目

```bash
git clone https://github.com/apache/fesod.git
cd fesod
```

### 2. 构建整个项目

```bash
mvn clean install -DskipTests
```

### 3. 构建 CLI 工具

```bash
cd fesod-cli
mvn clean package
```

### 4. 验证构建结果

构建完成后，在 `target/` 目录中会生成以下文件：

- `fesod-cli-2.0.0.jar` - 可执行的 fat JAR
- `fesod-cli-2.0.0-bin.tar.gz` - Linux/macOS 分发包
- `fesod-cli-2.0.0-bin.zip` - Windows 分发包

## 测试构建

### 运行单元测试

```bash
mvn test
```

### 运行集成测试

```bash
mvn verify
```

## 分发包结构

解压分发包后，目录结构如下：

```
fesod-cli-2.0.0/
├── bin/
│   ├── fesod-cli          # Unix/Linux/macOS 启动脚本
│   └── fesod-cli.bat      # Windows 启动脚本
├── conf/
│   ├── logback.xml        # 日志配置
│   ├── application.properties  # 应用配置
│   └── default-config.yaml     # 默认配置模板
├── lib/
│   └── fesod-cli-2.0.0.jar    # 主程序 JAR (Fat JAR)
├── licenses/              # 第三方许可证
├── LICENSE                # Apache 许可证
├── NOTICE                 # 版权声明
├── DISCLAIMER             # 免责声明
└── README.md              # 用户文档
```

## 使用分发包

### Linux/macOS

```bash
tar -xzf fesod-cli-2.0.0-bin.tar.gz
cd fesod-cli-2.0.0
./bin/fesod-cli --help
```

### Windows

```cmd
unzip fesod-cli-2.0.0-bin.zip
cd fesod-cli-2.0.0
bin\fesod-cli.bat --help
```

## 开发环境设置

### IDE 配置

推荐使用 IntelliJ IDEA 或 Eclipse 进行开发。

1. 导入项目：`File > Open` 选择项目根目录
2. 确保 JDK 8+ 已配置
3. Maven 自动导入依赖

### 调试 CLI

```bash
# 在 IDE 中运行
java -cp target/classes org.apache.fesod.cli.FesodCli --help

# 或使用 Maven
mvn exec:java -Dexec.mainClass="org.apache.fesod.cli.FesodCli" -Dexec.args="--help"
```

## 故障排除

### 常见构建问题

1. **内存不足**
   ```bash
   export MAVEN_OPTS="-Xmx2g -Xms1g"
   ```

2. **JDK 版本问题**
   ```bash
   java -version  # 确认 JDK 8+
   mvn -version   # 确认 Maven 3.6+
   ```

3. **依赖下载失败**
   ```bash
   mvn clean install -U  # 强制更新依赖
   ```

### 运行时问题

1. **Java 未找到**
   - 确保 JAVA_HOME 已设置
   - 或将 Java 添加到 PATH

2. **权限问题**
   ```bash
   chmod +x bin/fesod-cli  # Linux/macOS
   ```

## 贡献指南

1. 遵循现有的代码风格
2. 添加适当的单元测试
3. 更新文档（README.md 和 BUILDING.md）
4. 提交前运行完整测试套件

## 许可证

本项目采用 Apache License 2.0 许可证。
