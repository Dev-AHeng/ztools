/** 与 electron/preload.ts 中通过 contextBridge 暴露的对象保持一致 */
export interface ZToolsAppInfo {
  name: string;
  version: string;
  electron: string;
  chrome: string;
  node: string;
  v8: string;
  platform: string;
  arch: string;
  packaged: boolean;
}

export interface ZToolsBridge {
  isElectron: true;
  platform: string;
  getAppInfo: () => Promise<ZToolsAppInfo>;
}

declare global {
  interface Window {
    /** 仅在 Electron 容器中由 preload 注入，浏览器环境为 undefined */
    ztools?: ZToolsBridge;
  }
}
