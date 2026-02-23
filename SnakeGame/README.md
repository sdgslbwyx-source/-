# 贪吃蛇游戏 Android APK 构建指南

这是一个简单的贪吃蛇游戏 Android 应用项目。请按照以下步骤构建 APK 文件。

## 环境要求

1. **Java JDK 8 或更高版本**
   - 下载地址：https://www.oracle.com/java/technologies/javase-jdk11-downloads.html
   - 安装后设置 JAVA_HOME 环境变量

2. **Android Studio**（推荐）或 **Android SDK 命令行工具**
   - 下载地址：https://developer.android.com/studio
   - 安装时确保包含 Android SDK

## 构建步骤

### 方法一：使用 Android Studio（最简单）

1. 打开 Android Studio
2. 选择 "Open an Existing Project"
3. 浏览到 `C:\Users\Tao\Desktop\SnakeGame` 文件夹并打开
4. 等待项目同步完成（Gradle 构建）
5. 连接 Android 设备或启动模拟器
6. 点击菜单栏的 `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
7. 构建完成后，点击右下角的 "Build" 输出面板中的链接找到 APK 文件
   - 通常路径：`app/build/outputs/apk/debug/app-debug.apk`

### 方法二：使用命令行（需要配置环境变量）

1. 设置环境变量：
   - `JAVA_HOME`：指向 JDK 安装目录
   - `ANDROID_HOME`：指向 Android SDK 目录
   - 将 `%JAVA_HOME%\bin` 和 `%ANDROID_HOME%\tools`、`%ANDROID_HOME%\platform-tools` 添加到 PATH

2. 打开命令提示符，进入项目目录：
   ```
   cd C:\Users\Tao\Desktop\SnakeGame
   ```

3. 运行 Gradle 构建命令：
   ```
   .\gradlew.bat assembleDebug
   ```
   或使用 PowerShell：
   ```
   .\gradlew assembleDebug
   ```

4. 构建完成后，APK 文件位于：
   ```
   app\build\outputs\apk\debug\app-debug.apk
   ```

## 游戏说明

- 游戏是全屏的贪吃蛇游戏
- 通过触摸屏幕控制蛇的移动方向（触摸屏幕左侧/右侧/上方/下方）
- 吃到红色食物增加分数
- 撞到墙壁或自身游戏结束
- 游戏结束后触摸屏幕重新开始

## 项目结构

```
SnakeGame/
├── app/
│   ├── src/main/java/com/example/snake/
│   │   ├── MainActivity.java
│   │   └── GameView.java
│   ├── src/main/res/
│   │   ├── layout/
│   │   ├── values/
│   │   └── drawable/
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

## 注意事项

- 如果构建失败，请确保 Android SDK 版本与 `build.gradle` 中的配置一致
- 可能需要同意 Android SDK 许可证，运行 `.\gradlew.bat --licenses` 接受所有许可证
- 首次构建可能需要下载依赖，请保持网络连接

## 快速测试

如果你有 Android 设备，可以通过 USB 调试直接安装：
1. 在设备上启用开发者选项和 USB 调试
2. 连接设备到电脑
3. 在 Android Studio 中点击运行按钮，或使用命令：
   ```
   adb install app\build\outputs\apk\debug\app-debug.apk
   ```

## 方法三：使用 GitHub Actions 在线构建（无需本地环境）

如果你有 GitHub 账户，可以使用在线构建功能：

1. 在 GitHub 上创建一个新的仓库（例如 `snake-game`）
2. 将桌面上的 `SnakeGame` 文件夹中的所有文件上传到仓库
3. 进入仓库的 **Actions** 标签页
4. 点击 **Build Android APK** workflow，然后点击 **Run workflow**
5. 等待构建完成（大约 3-5 分钟）
6. 完成后，在 **Actions** 页面找到完成的 workflow，下载 **snake-game-apk**  artifact

项目已包含 `.github/workflows/build.yml` 配置文件，会自动构建 APK。

## 上传到 GitHub 的步骤

1. 访问 https://github.com 并登录
2. 点击右上角 **+** → **New repository**
3. 输入仓库名（如 `snake-game`），选择 **Public** 或 **Private**
4. 不要勾选 "Initialize this repository with a README"
5. 点击 **Create repository**
6. 按照 GitHub 的提示上传文件：
   ```
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/你的用户名/snake-game.git
   git push -u origin main
   ```
   或者使用 GitHub Desktop 或网页上传工具

祝你游戏愉快！