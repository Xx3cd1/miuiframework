package android.apex;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class ApexSessionInfo implements Parcelable {
    public static final Creator<ApexSessionInfo> CREATOR = new Creator<ApexSessionInfo>() {
        public ApexSessionInfo createFromParcel(Parcel _aidl_source) {
            ApexSessionInfo _aidl_out = new ApexSessionInfo();
            _aidl_out.readFromParcel(_aidl_source);
            return _aidl_out;
        }

        public ApexSessionInfo[] newArray(int _aidl_size) {
            return new ApexSessionInfo[_aidl_size];
        }
    };
    public boolean isActivated;
    public boolean isActivationFailed;
    public boolean isRollbackFailed;
    public boolean isRollbackInProgress;
    public boolean isRolledBack;
    public boolean isStaged;
    public boolean isSuccess;
    public boolean isUnknown;
    public boolean isVerified;
    public int sessionId;

    public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.writeInt(0);
        _aidl_parcel.writeInt(this.sessionId);
        _aidl_parcel.writeInt(this.isUnknown);
        _aidl_parcel.writeInt(this.isVerified);
        _aidl_parcel.writeInt(this.isStaged);
        _aidl_parcel.writeInt(this.isActivated);
        _aidl_parcel.writeInt(this.isRollbackInProgress);
        _aidl_parcel.writeInt(this.isActivationFailed);
        _aidl_parcel.writeInt(this.isSuccess);
        _aidl_parcel.writeInt(this.isRolledBack);
        _aidl_parcel.writeInt(this.isRollbackFailed);
        int _aidl_end_pos = _aidl_parcel.dataPosition();
        _aidl_parcel.setDataPosition(_aidl_start_pos);
        _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
        _aidl_parcel.setDataPosition(_aidl_end_pos);
    }

    public final void readFromParcel(Parcel _aidl_parcel) {
        int _aidl_start_pos = _aidl_parcel.dataPosition();
        int _aidl_parcelable_size = _aidl_parcel.readInt();
        if (_aidl_parcelable_size >= 0) {
            try {
                this.sessionId = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos < _aidl_parcelable_size) {
                    boolean z = true;
                    this.isUnknown = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isVerified = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isStaged = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isActivated = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isRollbackInProgress = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isActivationFailed = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isSuccess = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    this.isRolledBack = _aidl_parcel.readInt() != 0;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                        return;
                    }
                    if (_aidl_parcel.readInt() == 0) {
                        z = false;
                    }
                    this.isRollbackFailed = z;
                    if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    } else {
                        _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    }
                }
            } finally {
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            }
        }
    }

    public int describeContents() {
        return 0;
    }
}
