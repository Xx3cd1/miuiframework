package com.android.internal.telecom;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IPhoneAccountSuggestionService extends IInterface {

    public static class Default implements IPhoneAccountSuggestionService {
        public void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback callback, String number) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IPhoneAccountSuggestionService {
        private static final String DESCRIPTOR = "com.android.internal.telecom.IPhoneAccountSuggestionService";
        static final int TRANSACTION_onAccountSuggestionRequest = 1;

        private static class Proxy implements IPhoneAccountSuggestionService {
            public static IPhoneAccountSuggestionService sDefaultImpl;
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback callback, String number) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    _data.writeString(number);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().onAccountSuggestionRequest(callback, number);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IPhoneAccountSuggestionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IPhoneAccountSuggestionService)) {
                return new Proxy(obj);
            }
            return (IPhoneAccountSuggestionService) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            if (transactionCode != 1) {
                return null;
            }
            return "onAccountSuggestionRequest";
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code == 1) {
                data.enforceInterface(descriptor);
                onAccountSuggestionRequest(com.android.internal.telecom.IPhoneAccountSuggestionCallback.Stub.asInterface(data.readStrongBinder()), data.readString());
                return true;
            } else if (code != IBinder.INTERFACE_TRANSACTION) {
                return super.onTransact(code, data, reply, flags);
            } else {
                reply.writeString(descriptor);
                return true;
            }
        }

        public static boolean setDefaultImpl(IPhoneAccountSuggestionService impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IPhoneAccountSuggestionService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void onAccountSuggestionRequest(IPhoneAccountSuggestionCallback iPhoneAccountSuggestionCallback, String str) throws RemoteException;
}
