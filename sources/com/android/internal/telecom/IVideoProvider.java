package com.android.internal.telecom;

import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.telecom.VideoProfile;
import android.view.Surface;

public interface IVideoProvider extends IInterface {

    public static class Default implements IVideoProvider {
        public void addVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
        }

        public void removeVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
        }

        public void setCamera(String cameraId, String mCallingPackageName, int targetSdkVersion) throws RemoteException {
        }

        public void setPreviewSurface(Surface surface) throws RemoteException {
        }

        public void setDisplaySurface(Surface surface) throws RemoteException {
        }

        public void setDeviceOrientation(int rotation) throws RemoteException {
        }

        public void setZoom(float value) throws RemoteException {
        }

        public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
        }

        public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
        }

        public void requestCameraCapabilities() throws RemoteException {
        }

        public void requestCallDataUsage() throws RemoteException {
        }

        public void setPauseImage(Uri uri) throws RemoteException {
        }

        public IBinder asBinder() {
            return null;
        }
    }

    public static abstract class Stub extends Binder implements IVideoProvider {
        private static final String DESCRIPTOR = "com.android.internal.telecom.IVideoProvider";
        static final int TRANSACTION_addVideoCallback = 1;
        static final int TRANSACTION_removeVideoCallback = 2;
        static final int TRANSACTION_requestCallDataUsage = 11;
        static final int TRANSACTION_requestCameraCapabilities = 10;
        static final int TRANSACTION_sendSessionModifyRequest = 8;
        static final int TRANSACTION_sendSessionModifyResponse = 9;
        static final int TRANSACTION_setCamera = 3;
        static final int TRANSACTION_setDeviceOrientation = 6;
        static final int TRANSACTION_setDisplaySurface = 5;
        static final int TRANSACTION_setPauseImage = 12;
        static final int TRANSACTION_setPreviewSurface = 4;
        static final int TRANSACTION_setZoom = 7;

        private static class Proxy implements IVideoProvider {
            public static IVideoProvider sDefaultImpl;
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

            public void addVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(videoCallbackBinder);
                    if (this.mRemote.transact(1, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().addVideoCallback(videoCallbackBinder);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void removeVideoCallback(IBinder videoCallbackBinder) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(videoCallbackBinder);
                    if (this.mRemote.transact(2, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().removeVideoCallback(videoCallbackBinder);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setCamera(String cameraId, String mCallingPackageName, int targetSdkVersion) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(cameraId);
                    _data.writeString(mCallingPackageName);
                    _data.writeInt(targetSdkVersion);
                    if (this.mRemote.transact(3, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setCamera(cameraId, mCallingPackageName, targetSdkVersion);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setPreviewSurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (surface != null) {
                        _data.writeInt(1);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(4, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setPreviewSurface(surface);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setDisplaySurface(Surface surface) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (surface != null) {
                        _data.writeInt(1);
                        surface.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(5, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setDisplaySurface(surface);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setDeviceOrientation(int rotation) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(rotation);
                    if (this.mRemote.transact(6, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setDeviceOrientation(rotation);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setZoom(float value) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeFloat(value);
                    if (this.mRemote.transact(7, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setZoom(value);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void sendSessionModifyRequest(VideoProfile fromProfile, VideoProfile toProfile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (fromProfile != null) {
                        _data.writeInt(1);
                        fromProfile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (toProfile != null) {
                        _data.writeInt(1);
                        toProfile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(8, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().sendSessionModifyRequest(fromProfile, toProfile);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void sendSessionModifyResponse(VideoProfile responseProfile) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (responseProfile != null) {
                        _data.writeInt(1);
                        responseProfile.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(9, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().sendSessionModifyResponse(responseProfile);
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void requestCameraCapabilities() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(10, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().requestCameraCapabilities();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void requestCallDataUsage() throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (this.mRemote.transact(11, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().requestCallDataUsage();
                    }
                } finally {
                    _data.recycle();
                }
            }

            public void setPauseImage(Uri uri) throws RemoteException {
                Parcel _data = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    if (uri != null) {
                        _data.writeInt(1);
                        uri.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    if (this.mRemote.transact(12, _data, null, 1) || Stub.getDefaultImpl() == null) {
                        _data.recycle();
                    } else {
                        Stub.getDefaultImpl().setPauseImage(uri);
                    }
                } finally {
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IVideoProvider asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IVideoProvider)) {
                return new Proxy(obj);
            }
            return (IVideoProvider) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "addVideoCallback";
                case 2:
                    return "removeVideoCallback";
                case 3:
                    return "setCamera";
                case 4:
                    return "setPreviewSurface";
                case 5:
                    return "setDisplaySurface";
                case 6:
                    return "setDeviceOrientation";
                case 7:
                    return "setZoom";
                case 8:
                    return "sendSessionModifyRequest";
                case 9:
                    return "sendSessionModifyResponse";
                case 10:
                    return "requestCameraCapabilities";
                case 11:
                    return "requestCallDataUsage";
                case 12:
                    return "setPauseImage";
                default:
                    return null;
            }
        }

        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            String descriptor = DESCRIPTOR;
            if (code != IBinder.INTERFACE_TRANSACTION) {
                Surface _arg0;
                VideoProfile _arg02;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        addVideoCallback(data.readStrongBinder());
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        removeVideoCallback(data.readStrongBinder());
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        setCamera(data.readString(), data.readString(), data.readInt());
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (Surface) Surface.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        setPreviewSurface(_arg0);
                        return true;
                    case 5:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg0 = (Surface) Surface.CREATOR.createFromParcel(data);
                        } else {
                            _arg0 = null;
                        }
                        setDisplaySurface(_arg0);
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        setDeviceOrientation(data.readInt());
                        return true;
                    case 7:
                        data.enforceInterface(descriptor);
                        setZoom(data.readFloat());
                        return true;
                    case 8:
                        VideoProfile _arg1;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (VideoProfile) VideoProfile.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        if (data.readInt() != 0) {
                            _arg1 = (VideoProfile) VideoProfile.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        sendSessionModifyRequest(_arg02, _arg1);
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg02 = (VideoProfile) VideoProfile.CREATOR.createFromParcel(data);
                        } else {
                            _arg02 = null;
                        }
                        sendSessionModifyResponse(_arg02);
                        return true;
                    case 10:
                        data.enforceInterface(descriptor);
                        requestCameraCapabilities();
                        return true;
                    case 11:
                        data.enforceInterface(descriptor);
                        requestCallDataUsage();
                        return true;
                    case 12:
                        Uri _arg03;
                        data.enforceInterface(descriptor);
                        if (data.readInt() != 0) {
                            _arg03 = (Uri) Uri.CREATOR.createFromParcel(data);
                        } else {
                            _arg03 = null;
                        }
                        setPauseImage(_arg03);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IVideoProvider impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IVideoProvider getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    void addVideoCallback(IBinder iBinder) throws RemoteException;

    void removeVideoCallback(IBinder iBinder) throws RemoteException;

    void requestCallDataUsage() throws RemoteException;

    void requestCameraCapabilities() throws RemoteException;

    void sendSessionModifyRequest(VideoProfile videoProfile, VideoProfile videoProfile2) throws RemoteException;

    void sendSessionModifyResponse(VideoProfile videoProfile) throws RemoteException;

    void setCamera(String str, String str2, int i) throws RemoteException;

    void setDeviceOrientation(int i) throws RemoteException;

    void setDisplaySurface(Surface surface) throws RemoteException;

    void setPauseImage(Uri uri) throws RemoteException;

    void setPreviewSurface(Surface surface) throws RemoteException;

    void setZoom(float f) throws RemoteException;
}
