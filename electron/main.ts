import { join } from 'node:path';
import { app, BrowserWindow, ipcMain, shell } from 'electron';

/** 开发模式下默认连接的 Umi dev server 地址 */
const DEFAULT_DEV_SERVER_URL = 'http://127.0.0.1:8000';
const devServerUrl =
  process.env.ELECTRON_RENDERER_URL ??
  (process.env.NODE_ENV === 'development' ? DEFAULT_DEV_SERVER_URL : '');

/** 未打包时视为开发模式 */
const isDev = !app.isPackaged;

/** 仅在开发模式且指定了 dev server 时才连接 HMR 页面，否则加载本地构建产物 */
const useDevServer = isDev && devServerUrl !== '';

/** 冒烟测试模式下，页面加载完成后自动退出，用于 CI 校验 */
const isSmokeTest = process.env.ZTOOLS_SMOKE_TEST === '1';

let mainWindow: BrowserWindow | null = null;

function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1440,
    height: 900,
    minWidth: 1024,
    minHeight: 720,
    show: false,
    title: 'ZTools',
    backgroundColor: '#f5f5f5',
    autoHideMenuBar: true,
    webPreferences: {
      preload: join(__dirname, 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false,
      sandbox: true,
    },
  });

  mainWindow.once('ready-to-show', () => {
    mainWindow?.show();
  });

  mainWindow.on('closed', () => {
    mainWindow = null;
  });

  // 站外链接交给系统浏览器打开，避免在应用内弹出新窗口
  mainWindow.webContents.setWindowOpenHandler(({ url }) => {
    if (url.startsWith('http://') || url.startsWith('https://')) {
      void shell.openExternal(url);
    }
    return { action: 'deny' };
  });

  if (isSmokeTest) {
    mainWindow.webContents.on('did-finish-load', () => {
      console.log('[smoke] renderer loaded');
      app.exit(0);
    });
    mainWindow.webContents.on('did-fail-load', (_event, code, desc, url) => {
      console.error(`[smoke] renderer failed: ${code} ${desc} ${url}`);
      app.exit(1);
    });
    mainWindow.webContents.on('render-process-gone', (_event, details) => {
      console.error(`[smoke] renderer gone: ${details.reason}`);
      app.exit(1);
    });
  }

  if (useDevServer) {
    void mainWindow.loadURL(devServerUrl);
  } else {
    void mainWindow.loadFile(join(__dirname, '../dist/index.html'));
  }
}

function registerIpcHandlers() {
  ipcMain.handle('app:info', () => ({
    name: app.getName(),
    version: app.getVersion(),
    electron: process.versions.electron,
    chrome: process.versions.chrome,
    node: process.versions.node,
    v8: process.versions.v8,
    platform: process.platform,
    arch: process.arch,
    packaged: app.isPackaged,
  }));
}

// 单实例：重复启动时聚焦已有窗口
if (!app.requestSingleInstanceLock()) {
  app.quit();
} else {
  app.on('second-instance', () => {
    if (!mainWindow) return;
    if (mainWindow.isMinimized()) mainWindow.restore();
    mainWindow.focus();
  });

  app.whenReady().then(() => {
    app.setAppUserModelId('com.ztools.app');
    registerIpcHandlers();
    createWindow();

    app.on('activate', () => {
      if (BrowserWindow.getAllWindows().length === 0) createWindow();
    });
  });

  app.on('window-all-closed', () => {
    if (process.platform !== 'darwin') app.quit();
  });
}
