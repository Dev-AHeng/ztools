# ZTools

跨平台桌面工具箱，使用 **Electron + TypeScript + Ant Design Pro** 构建：
Electron 提供桌面容器能力，Ant Design Pro（Umi Max + React + antd）负责界面与业务开发。

## 技术栈

| 分层     | 技术                                                          |
| -------- | ------------------------------------------------------------- |
| 桌面容器 | Electron 45、TypeScript、electron-builder                     |
| 前端框架 | Ant Design Pro 6 / Umi Max 4、React 19                        |
| UI 组件  | antd 6、@ant-design/pro-components、Tailwind CSS 4            |
| 工程化   | Biome、Vitest、Husky + lint-staged、commitlint                |

## 目录结构

```
electron/            # Electron 主进程与 preload（TypeScript 源码）
  main.ts            # 创建窗口、加载页面、注册 IPC
  preload.ts         # contextBridge 暴露 window.ztools
config/              # Umi / Ant Design Pro 配置（路由、主题、代理等）
src/                 # 渲染进程（React 页面、组件、service、locales）
types/               # 全局类型声明
dist/                # 前端构建产物（已忽略）
dist-electron/       # 主进程编译产物（已忽略）
release/             # 安装包输出目录（已忽略）
```

## 环境准备

```bash
npm install
```

国内网络下载 Electron 二进制较慢时，可先设置镜像再安装：

```powershell
$env:ELECTRON_MIRROR="https://npmmirror.com/mirrors/electron/"
npm install
```

## 常用脚本

| 命令                    | 说明                                                       |
| ----------------------- | ---------------------------------------------------------- |
| `npm run dev:app`       | 同时启动 Umi dev server 与 Electron（开发推荐）             |
| `npm run dev`           | 仅启动浏览器端开发服务器（http://127.0.0.1:8000）           |
| `npm run dev:electron`  | 只启动 Electron，连接已在运行的 dev server                  |
| `npm run build`         | 构建前端静态产物到 `dist/`                                  |
| `npm run build:electron`| 编译主进程到 `dist-electron/`                               |
| `npm run build:all`     | 前端 + 主进程完整构建                                       |
| `npm run smoke:electron`| 冒烟测试：加载构建产物，成功即退出（CI 可用）               |
| `npm run package:app`   | 使用 electron-builder 生成安装包到 `release/`               |
| `npm run lint`          | Biome 检查 + TypeScript 类型检查                            |
| `npm test`              | Vitest 单元测试                                             |

## 桌面端集成要点

- **页面加载**：开发模式（`NODE_ENV=development`）加载 `http://127.0.0.1:8000`，其余情况加载 `dist/index.html`；
  可通过 `ELECTRON_RENDERER_URL` 覆盖开发地址。
- **路由与资源路径**：Electron 使用 `file://` 协议，因此生产构建采用 `history: { type: 'hash' }`
  与相对 `publicPath: './'`（配合 `runtimePublicPath`），保证异步 chunk 能正确加载。
- **安全模型**：`contextIsolation: true`、`nodeIntegration: false`、`sandbox: true`，
  渲染进程只能通过 `preload.ts` 暴露的 `window.ztools` 访问主进程能力。
- **站外链接**：`setWindowOpenHandler` 将 http(s) 链接交给系统浏览器打开。

渲染进程中调用示例：

```ts
const info = await window.ztools?.getAppInfo();
// { name, version, electron, chrome, node, v8, platform, arch, packaged }
```

`window.ztools` 仅在 Electron 容器中存在，浏览器环境下为 `undefined`，
相关类型声明见 `types/electron.d.ts`。
