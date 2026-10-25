import { contextBridge, ipcRenderer } from 'electron';

/** 主进程通过 IPC 返回的运行时信息，结构见 electron/main.ts */
export interface AppInfo {
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

/** 暴露给渲染进程的桥接对象，通过 window.ztools 访问 */
const ztools = {
  isElectron: true as const,
  platform: process.platform,
  getAppInfo: (): Promise<AppInfo> => ipcRenderer.invoke('app:info'),
};

contextBridge.exposeInMainWorld('ztools', ztools);

export type ZToolsBridge = typeof ztools;
