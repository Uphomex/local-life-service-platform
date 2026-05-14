
# 本地生活服务平台 - Git使用指南

## 📋 目录

- [一、Git基础概念](#一git基础概念)
  - [1.1 什么是Git](#11-什么是git)
  - [1.2 Git工作流程](#12-git工作流程)
  - [1.3 常用术语](#13-常用术语)
- [二、Git环境配置](#二git环境配置)
  - [2.1 安装Git](#21-安装git)
  - [2.2 配置用户信息](#22-配置用户信息)
  - [2.3 配置SSH密钥](#23-配置ssh密钥)
- [三、Git基本操作](#三git基本操作)
  - [3.1 初始化仓库](#31-初始化仓库)
  - [3.2 暂存与提交](#32-暂存与提交)
  - [3.3 查看状态](#33-查看状态)
  - [3.4 查看历史](#34-查看历史)
- [四、分支管理](#四分支管理)
  - [4.1 创建分支](#41-创建分支)
  - [4.2 切换分支](#42-切换分支)
  - [4.3 合并分支](#43-合并分支)
  - [4.4 删除分支](#44-删除分支)
- [五、远程仓库操作](#五远程仓库操作)
  - [5.1 添加远程仓库](#51-添加远程仓库)
  - [5.2 推送代码](#52-推送代码)
  - [5.3 拉取代码](#53-拉取代码)
  - [5.4 克隆仓库](#54-克隆仓库)
- [六、解决冲突](#六解决冲突)
  - [6.1 冲突产生原因](#61-冲突产生原因)
  - [6.2 冲突解决步骤](#62-冲突解决步骤)
  - [6.3 冲突解决示例](#63-冲突解决示例)
- [七、项目Git实践](#七项目git实践)
  - [7.1 分支策略](#71-分支策略)
  - [7.2 提交规范](#72-提交规范)
  - [7.3 操作流程](#73-操作流程)

---

## 一、Git基础概念

### 1.1 什么是Git

Git是一个分布式版本控制系统，用于跟踪文件变化、管理代码版本、支持多人协作开发。

**核心特点：**
- **分布式**：每个开发者都有完整的仓库副本
- **版本控制**：记录每一次代码变更
- **分支管理**：支持并行开发
- **协作支持**：多人同时开发同一项目

### 1.2 Git工作流程

```
工作区 (Working Directory)
       │
       ▼
暂存区 (Staging Area)
       │
       ▼
本地仓库 (Local Repository)
       │
       ▼
远程仓库 (Remote Repository)
```

### 1.3 常用术语

| 术语 | 说明 |
|------|------|
| Repository | 仓库，存放项目代码和版本历史 |
| Commit | 提交，一次代码变更的记录 |
| Branch | 分支，独立的开发线 |
| Master/Main | 主分支，稳定版本所在分支 |
| Merge | 合并，将一个分支的变更合并到另一个分支 |
| Push | 推送，将本地代码推送到远程仓库 |
| Pull | 拉取，从远程仓库获取代码 |
| Clone | 克隆，从远程仓库复制完整仓库到本地 |

---

## 二、Git环境配置

### 2.1 安装Git

**Windows系统：**
1. 下载地址：https://git-scm.com/download/win
2. 运行安装程序，按默认选项安装

**Linux系统：**
```bash
sudo apt update
sudo apt install git
```

**验证安装：**
```bash
git --version
```

### 2.2 配置用户信息

```bash
# 配置用户名
git config --global user.name "Your Name"

# 配置邮箱
git config --global user.email "your.email@example.com"

# 查看配置
git config --list
```

### 2.3 配置SSH密钥

**生成SSH密钥：**
```bash
ssh-keygen -t ed25519 -C "your.email@example.com"
```

**查看公钥：**
```bash
cat ~/.ssh/id_ed25519.pub
```

**添加到GitHub：**
1. 登录GitHub
2. 进入Settings → SSH and GPG keys
3. 点击New SSH key
4. 粘贴公钥内容

---

## 三、Git基本操作

### 3.1 初始化仓库

```bash
# 创建项目目录
mkdir local-life-service-platform
cd local-life-service-platform

# 初始化Git仓库
git init
```

### 3.2 暂存与提交

```bash
# 查看当前目录状态
git status

# 暂存所有文件
git add .

# 暂存指定文件
git add filename.java

# 提交暂存的文件
git commit -m "commit message"

# 跳过暂存区直接提交
git commit -am "commit message"
```

### 3.3 查看状态

```bash
# 查看工作区状态
git status

# 查看文件变更
git diff

# 查看暂存区变更
git diff --cached
```

### 3.4 查看历史

```bash
# 查看提交历史
git log

# 简洁格式查看
git log --oneline

# 查看指定次数的提交
git log -5

# 查看详细变更
git log -p
```

---

## 四、分支管理

### 4.1 创建分支

```bash
# 创建分支
git branch feature/login

# 创建并切换分支
git checkout -b feature/login
```

### 4.2 切换分支

```bash
# 切换到指定分支
git checkout main

# 切换到上一个分支
git checkout -
```

### 4.3 合并分支

```bash
# 切换到目标分支
git checkout main

# 合并feature分支
git merge feature/login
```

### 4.4 删除分支

```bash
# 删除已合并的分支
git branch -d feature/login

# 强制删除分支
git branch -D feature/login
```

---

## 五、远程仓库操作

### 5.1 添加远程仓库

```bash
# 添加远程仓库
git remote add origin git@github.com:username/local-life-service-platform.git

# 查看远程仓库
git remote -v
```

### 5.2 推送代码

```bash
# 推送到远程仓库（首次推送）
git push -u origin main

# 推送到指定分支
git push origin feature/login
```

### 5.3 拉取代码

```bash
# 拉取并合并
git pull origin main

# 只拉取不合并
git fetch origin main
```

### 5.4 克隆仓库

```bash
# 克隆仓库
git clone git@github.com:username/local-life-service-platform.git

# 克隆到指定目录
git clone git@github.com:username/local-life-service-platform.git my-project
```

---

## 六、解决冲突

### 6.1 冲突产生原因

当两个分支修改了同一文件的同一部分时，Git无法自动合并，会产生冲突。

### 6.2 冲突解决步骤

1. **执行合并操作**
2. **查看冲突文件**
3. **手动编辑冲突内容**
4. **标记冲突已解决**
5. **提交合并结果**

### 6.3 冲突解决示例

```bash
# 尝试合并分支
git merge feature/login

# 查看冲突文件
git status

# 编辑冲突文件，解决冲突
# 冲突标记：
# <<<<<<< HEAD
# 主分支内容
# =======
# feature分支内容
# >>>>>>> feature/login

# 解决后暂存文件
git add conflicted-file.java

# 完成合并提交
git commit
```

---

## 七、项目Git实践

### 7.1 分支策略

**推荐分支结构：**

| 分支 | 用途 |
|------|------|
| main | 主分支，稳定版本 |
| develop | 开发分支，集成所有功能 |
| feature/* | 功能分支，开发新功能 |
| bugfix/* | 修复分支，修复bug |
| hotfix/* | 紧急修复分支 |

### 7.2 提交规范

**提交信息格式：**

```
type(scope): description

body

footer
```

**type说明：**
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试
- chore: 构建/工具

**示例：**
```
feat(user): 实现用户登录功能

- 添加登录API接口
- 实现JWT认证
- 添加密码加密

closes #123
```

### 7.3 操作流程

**开发新功能：**

```bash
# 1. 从develop分支创建feature分支
git checkout develop
git checkout -b feature/new-feature

# 2. 开发代码
# ...编写代码...

# 3. 提交代码
git add .
git commit -m "feat: 实现新功能"

# 4. 推送到远程
git push origin feature/new-feature

# 5. 创建Pull Request
# 在GitHub上创建PR到develop分支

# 6. 合并后删除分支
git checkout develop
git pull origin develop
git branch -d feature/new-feature
```

**修复bug：**

```bash
# 1. 创建bugfix分支
git checkout develop
git checkout -b bugfix/fix-issue

# 2. 修复代码
# ...修复bug...

# 3. 提交并推送
git add .
git commit -m "fix: 修复xxx问题"
git push origin bugfix/fix-issue

# 4. 合并到develop
git checkout develop
git merge bugfix/fix-issue
```

---

## 📌 常用命令速查

| 命令 | 说明 |
|------|------|
| `git init` | 初始化仓库 |
| `git add .` | 暂存所有文件 |
| `git commit -m "msg"` | 提交代码 |
| `git push origin main` | 推送到远程 |
| `git pull origin main` | 拉取代码 |
| `git branch` | 查看分支 |
| `git checkout -b branch` | 创建并切换分支 |
| `git merge branch` | 合并分支 |
| `git log` | 查看提交历史 |
| `git status` | 查看状态 |
| `git clone url` | 克隆仓库 |

---

## 📌 项目提交流程

**首次提交项目：**

```bash
# 1. 进入项目目录
cd local-life-service-platform

# 2. 初始化Git
git init

# 3. 添加文件
git add .

# 4. 提交
git commit -m "feat: 初始化本地生活服务平台项目

- 后端Spring Boot项目
- 前端Vue 3项目
- 完整的业务模块"

# 5. 添加远程仓库
git remote add origin git@github.com:your-username/local-life-service-platform.git

# 6. 推送到GitHub
git push -u origin main
```
