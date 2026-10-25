package com.mediatek.changeain;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* loaded from: classes.dex */
public interface ShcySelectData extends IInterface {
    String getChangeainData() throws RemoteException;

    void setChangeainData(String str) throws RemoteException;

    void updateSelectData(String str) throws RemoteException;

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements ShcySelectData {
        public Stub() {
            attachInterface(this, "com.mediatek.changeain.ShcySelectData");
        }

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case 1:
                    data.enforceInterface("com.mediatek.changeain.ShcySelectData");
                    String _result = getChangeainData();
                    reply.writeNoException();
                    reply.writeString(_result);
                    return true;
                case 2:
                    data.enforceInterface("com.mediatek.changeain.ShcySelectData");
                    setChangeainData(data.readString());
                    reply.writeNoException();
                    return true;
                case 3:
                    data.enforceInterface("com.mediatek.changeain.ShcySelectData");
                    updateSelectData(data.readString());
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString("com.mediatek.changeain.ShcySelectData");
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }
}
