package com.mediatek.changeain;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import com.android.internal.util.HexDump;
import com.mediatek.changeain.ShcySelectData;
import java.util.ArrayList;
//import vendor.mediatek.hardware.nvram.V1_0.INvram;
import android.os.IBinder;
import android.os.ServiceManager;
import com.mediatek.changeanimation.NvRAMAgent;



/* loaded from: classes.dex */
public class RemoteService extends Service {
    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new MyBinder(this, null);
    }

    /* loaded from: classes.dex */
    private class MyBinder extends ShcySelectData.Stub {
        /* synthetic */ MyBinder(RemoteService this$0, MyBinder myBinder) {
            this();
        }

        private MyBinder() {
        }

        @Override // com.mediatek.changeain.ShcySelectData
        public void setChangeainData(String data) throws RemoteException {
        }

        @Override // com.mediatek.changeain.ShcySelectData
        public String getChangeainData() throws RemoteException {
            return SystemProperties.get("persist.sys.changeain");
        }

        @Override // com.mediatek.changeain.ShcySelectData
        public void updateSelectData(String data) throws RemoteException {
            if ("0".equals(data)) {
                SystemProperties.set("persist.sys.changeain", "1");
                writeNvData(119, getByteValue(1));
                SystemProperties.set("persist.sys.rom", "1");
                writeNvData(120, getByteValue(1));
                SystemProperties.set("persist.sys.ram", "1");
                writeNvData(121, getByteValue(1));
                SystemProperties.set("persist.sys.signaltype", "1");
                writeNvData(122, getByteValue(1));
                SystemProperties.set("persist.sys.camfront", "1");
                writeNvData(124, getByteValue(1));
                SystemProperties.set("persist.sys.camback", "1");
                writeNvData(123, getByteValue(1));
                SystemProperties.set("persist.sys.cyversion", "1");
                writeNvData(125, getByteValue(1));
                SystemProperties.set("persist.sys.cycore", "1");
                writeNvData(126, getByteValue(1));
                SystemProperties.set("persist.sys.cycpu", "1");
                writeNvData(127, getByteValue(1));
                SystemProperties.set("persist.sys.cyimei", "2");
                writeNvData(128, getByteValue(2));
            }
        }

        private byte getByteValue(int position) {
            return (byte) position;
        }

        public void writeNvData(int position, byte c) {
            try {
                IBinder binder = ServiceManager.getService("NvRAMAgent");
            	NvRAMAgent agent = NvRAMAgent.Stub.asInterface(binder);
                byte[] buffArr = agent.readFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO");
                //byte[] buffArr = HexDump.hexStringToByteArray(buffString.substring(0, buffString.length() - 1));
                if (position < buffArr.length) {
                    buffArr[position] = c;
                }
                ///ArrayList<Byte> dataArray = new ArrayList<>(buffArr.length);
                ///for (int i = 0; i < buffArr.length; i++) {
                //    dataArray.add(i, new Byte(buffArr[i]));
                //}
                agent.writeFileByName("/data/nvram/APCFG/APRDEB/PRODUCT_INFO", buffArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
