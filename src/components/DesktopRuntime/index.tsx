import { getIntl } from '@umijs/max';
import { Card, Descriptions, Tag, Typography } from 'antd';
import { useEffect, useState } from 'react';
import type { ZToolsAppInfo } from '../../../types/electron';

/**
 * 展示桌面端运行时信息。
 * 通过 preload 注入的 window.ztools 判断是否运行在 Electron 容器中，
 * 浏览器环境下不渲染任何内容。
 */
const DesktopRuntime: React.FC = () => {
  const bridge = typeof window === 'undefined' ? undefined : window.ztools;
  const [info, setInfo] = useState<ZToolsAppInfo>();

  useEffect(() => {
    if (!bridge) return;

    let active = true;
    bridge
      .getAppInfo()
      .then((data) => {
        if (active) setInfo(data);
      })
      .catch(() => {
        // 主进程不可用时保持空状态，不影响页面其它内容
      });

    return () => {
      active = false;
    };
  }, [bridge]);

  if (!bridge) return null;

  const intl = getIntl();

  return (
    <Card
      size="small"
      title={intl.formatMessage({
        id: 'pages.welcome.desktop.title',
        defaultMessage: 'Desktop runtime',
      })}
      extra={<Tag color="blue">Electron</Tag>}
    >
      {info ? (
        <Descriptions
          size="small"
          column={1}
          colon={false}
          items={[
            {
              key: 'app',
              label: 'App',
              children: `${info.name} v${info.version}`,
            },
            { key: 'electron', label: 'Electron', children: info.electron },
            { key: 'chrome', label: 'Chromium', children: info.chrome },
            { key: 'node', label: 'Node.js', children: info.node },
            {
              key: 'platform',
              label: 'Platform',
              children: `${info.platform} / ${info.arch}`,
            },
          ]}
        />
      ) : (
        <Typography.Text type="secondary">
          {intl.formatMessage({
            id: 'pages.welcome.desktop.loading',
            defaultMessage: 'Connecting to main process…',
          })}
        </Typography.Text>
      )}
    </Card>
  );
};

export default DesktopRuntime;
