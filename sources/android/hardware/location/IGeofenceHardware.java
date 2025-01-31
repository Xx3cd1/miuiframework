package android.hardware.location;

import android.location.IFusedGeofenceHardware;
import android.location.IGpsGeofenceHardware;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IGeofenceHardware extends IInterface {

    public static abstract class Stub extends Binder implements IGeofenceHardware {
        private static final String DESCRIPTOR = "android.hardware.location.IGeofenceHardware";
        static final int TRANSACTION_addCircularFence = 5;
        static final int TRANSACTION_getMonitoringTypes = 3;
        static final int TRANSACTION_getStatusOfMonitoringType = 4;
        static final int TRANSACTION_pauseGeofence = 7;
        static final int TRANSACTION_registerForMonitorStateChangeCallback = 9;
        static final int TRANSACTION_removeGeofence = 6;
        static final int TRANSACTION_resumeGeofence = 8;
        static final int TRANSACTION_setFusedGeofenceHardware = 2;
        static final int TRANSACTION_setGpsGeofenceHardware = 1;
        static final int TRANSACTION_unregisterForMonitorStateChangeCallback = 10;

        private static class Proxy implements IGeofenceHardware {
            public static IGeofenceHardware sDefaultImpl;
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

            public void setGpsGeofenceHardware(IGpsGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(service != null ? service.asBinder() : null);
                    if (this.mRemote.transact(1, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setGpsGeofenceHardware(service);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void setFusedGeofenceHardware(IFusedGeofenceHardware service) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongBinder(service != null ? service.asBinder() : null);
                    if (this.mRemote.transact(2, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        _reply.recycle();
                        _data.recycle();
                        return;
                    }
                    Stub.getDefaultImpl().setFusedGeofenceHardware(service);
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int[] getMonitoringTypes() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    int[] iArr = 3;
                    if (!this.mRemote.transact(3, _data, _reply, 0)) {
                        iArr = Stub.getDefaultImpl();
                        if (iArr != 0) {
                            iArr = Stub.getDefaultImpl().getMonitoringTypes();
                            return iArr;
                        }
                    }
                    _reply.readException();
                    iArr = _reply.createIntArray();
                    int[] _result = iArr;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public int getStatusOfMonitoringType(int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    int i = 4;
                    if (!this.mRemote.transact(4, _data, _reply, 0)) {
                        i = Stub.getDefaultImpl();
                        if (i != 0) {
                            i = Stub.getDefaultImpl().getStatusOfMonitoringType(monitoringType);
                            return i;
                        }
                    }
                    _reply.readException();
                    i = _reply.readInt();
                    int _result = i;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    boolean _result = true;
                    if (request != null) {
                        _data.writeInt(1);
                        request.writeToParcel(_data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    if (this.mRemote.transact(5, _data, _reply, 0) || Stub.getDefaultImpl() == null) {
                        _reply.readException();
                        if (_reply.readInt() == 0) {
                            _result = false;
                        }
                        _reply.recycle();
                        _data.recycle();
                        return _result;
                    }
                    _result = Stub.getDefaultImpl().addCircularFence(monitoringType, request, callback);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean removeGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(6, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().removeGeofence(id, monitoringType);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean pauseGeofence(int id, int monitoringType) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(7, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().pauseGeofence(id, monitoringType);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(id);
                    _data.writeInt(monitoringType);
                    _data.writeInt(monitorTransitions);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(8, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().resumeGeofence(id, monitoringType, monitorTransitions);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(9, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().registerForMonitorStateChangeCallback(monitoringType, callback);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(monitoringType);
                    _data.writeStrongBinder(callback != null ? callback.asBinder() : null);
                    boolean z = true;
                    boolean z2 = false;
                    if (!this.mRemote.transact(10, _data, _reply, 0)) {
                        z = Stub.getDefaultImpl();
                        if (z) {
                            z = Stub.getDefaultImpl().unregisterForMonitorStateChangeCallback(monitoringType, callback);
                            return z;
                        }
                    }
                    _reply.readException();
                    z = _reply.readInt();
                    if (z) {
                        z2 = true;
                    }
                    boolean _result = z2;
                    _reply.recycle();
                    _data.recycle();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IGeofenceHardware asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IGeofenceHardware)) {
                return new Proxy(obj);
            }
            return (IGeofenceHardware) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "setGpsGeofenceHardware";
                case 2:
                    return "setFusedGeofenceHardware";
                case 3:
                    return "getMonitoringTypes";
                case 4:
                    return "getStatusOfMonitoringType";
                case 5:
                    return "addCircularFence";
                case 6:
                    return "removeGeofence";
                case 7:
                    return "pauseGeofence";
                case 8:
                    return "resumeGeofence";
                case 9:
                    return "registerForMonitorStateChangeCallback";
                case 10:
                    return "unregisterForMonitorStateChangeCallback";
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
                boolean _result;
                boolean _result2;
                switch (code) {
                    case 1:
                        data.enforceInterface(descriptor);
                        setGpsGeofenceHardware(android.location.IGpsGeofenceHardware.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 2:
                        data.enforceInterface(descriptor);
                        setFusedGeofenceHardware(android.location.IFusedGeofenceHardware.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        return true;
                    case 3:
                        data.enforceInterface(descriptor);
                        int[] _result3 = getMonitoringTypes();
                        reply.writeNoException();
                        reply.writeIntArray(_result3);
                        return true;
                    case 4:
                        data.enforceInterface(descriptor);
                        int _result4 = getStatusOfMonitoringType(data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result4);
                        return true;
                    case 5:
                        GeofenceHardwareRequestParcelable _arg1;
                        data.enforceInterface(descriptor);
                        int _arg0 = data.readInt();
                        if (data.readInt() != 0) {
                            _arg1 = (GeofenceHardwareRequestParcelable) GeofenceHardwareRequestParcelable.CREATOR.createFromParcel(data);
                        } else {
                            _arg1 = null;
                        }
                        _result = addCircularFence(_arg0, _arg1, android.hardware.location.IGeofenceHardwareCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 6:
                        data.enforceInterface(descriptor);
                        _result2 = removeGeofence(data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 7:
                        data.enforceInterface(descriptor);
                        _result2 = pauseGeofence(data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 8:
                        data.enforceInterface(descriptor);
                        _result = resumeGeofence(data.readInt(), data.readInt(), data.readInt());
                        reply.writeNoException();
                        reply.writeInt(_result);
                        return true;
                    case 9:
                        data.enforceInterface(descriptor);
                        _result2 = registerForMonitorStateChangeCallback(data.readInt(), android.hardware.location.IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    case 10:
                        data.enforceInterface(descriptor);
                        _result2 = unregisterForMonitorStateChangeCallback(data.readInt(), android.hardware.location.IGeofenceHardwareMonitorCallback.Stub.asInterface(data.readStrongBinder()));
                        reply.writeNoException();
                        reply.writeInt(_result2);
                        return true;
                    default:
                        return super.onTransact(code, data, reply, flags);
                }
            }
            reply.writeString(descriptor);
            return true;
        }

        public static boolean setDefaultImpl(IGeofenceHardware impl) {
            if (Proxy.sDefaultImpl != null || impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IGeofenceHardware getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }

    public static class Default implements IGeofenceHardware {
        public void setGpsGeofenceHardware(IGpsGeofenceHardware service) throws RemoteException {
        }

        public void setFusedGeofenceHardware(IFusedGeofenceHardware service) throws RemoteException {
        }

        public int[] getMonitoringTypes() throws RemoteException {
            return null;
        }

        public int getStatusOfMonitoringType(int monitoringType) throws RemoteException {
            return 0;
        }

        public boolean addCircularFence(int monitoringType, GeofenceHardwareRequestParcelable request, IGeofenceHardwareCallback callback) throws RemoteException {
            return false;
        }

        public boolean removeGeofence(int id, int monitoringType) throws RemoteException {
            return false;
        }

        public boolean pauseGeofence(int id, int monitoringType) throws RemoteException {
            return false;
        }

        public boolean resumeGeofence(int id, int monitoringType, int monitorTransitions) throws RemoteException {
            return false;
        }

        public boolean registerForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
            return false;
        }

        public boolean unregisterForMonitorStateChangeCallback(int monitoringType, IGeofenceHardwareMonitorCallback callback) throws RemoteException {
            return false;
        }

        public IBinder asBinder() {
            return null;
        }
    }

    boolean addCircularFence(int i, GeofenceHardwareRequestParcelable geofenceHardwareRequestParcelable, IGeofenceHardwareCallback iGeofenceHardwareCallback) throws RemoteException;

    int[] getMonitoringTypes() throws RemoteException;

    int getStatusOfMonitoringType(int i) throws RemoteException;

    boolean pauseGeofence(int i, int i2) throws RemoteException;

    boolean registerForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;

    boolean removeGeofence(int i, int i2) throws RemoteException;

    boolean resumeGeofence(int i, int i2, int i3) throws RemoteException;

    void setFusedGeofenceHardware(IFusedGeofenceHardware iFusedGeofenceHardware) throws RemoteException;

    void setGpsGeofenceHardware(IGpsGeofenceHardware iGpsGeofenceHardware) throws RemoteException;

    boolean unregisterForMonitorStateChangeCallback(int i, IGeofenceHardwareMonitorCallback iGeofenceHardwareMonitorCallback) throws RemoteException;
}
