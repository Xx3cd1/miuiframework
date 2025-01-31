package android.telephony;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import miui.telephony.PhoneDebug;

public class SmsCbLocation implements Parcelable {
    public static final Creator<SmsCbLocation> CREATOR = new Creator<SmsCbLocation>() {
        public SmsCbLocation createFromParcel(Parcel in) {
            return new SmsCbLocation(in);
        }

        public SmsCbLocation[] newArray(int size) {
            return new SmsCbLocation[size];
        }
    };
    private final int mCid;
    private final int mLac;
    private final String mPlmn;

    public SmsCbLocation() {
        this.mPlmn = "";
        this.mLac = -1;
        this.mCid = -1;
    }

    public SmsCbLocation(String plmn) {
        this.mPlmn = plmn;
        this.mLac = -1;
        this.mCid = -1;
    }

    public SmsCbLocation(String plmn, int lac, int cid) {
        this.mPlmn = plmn;
        this.mLac = lac;
        this.mCid = cid;
    }

    public SmsCbLocation(Parcel in) {
        this.mPlmn = in.readString();
        this.mLac = in.readInt();
        this.mCid = in.readInt();
    }

    public String getPlmn() {
        return this.mPlmn;
    }

    public int getLac() {
        return this.mLac;
    }

    public int getCid() {
        return this.mCid;
    }

    public int hashCode() {
        return (((this.mPlmn.hashCode() * 31) + this.mLac) * 31) + this.mCid;
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof SmsCbLocation)) {
            return false;
        }
        SmsCbLocation other = (SmsCbLocation) o;
        if (!(this.mPlmn.equals(other.mPlmn) && this.mLac == other.mLac && this.mCid == other.mCid)) {
            z = false;
        }
        return z;
    }

    public String toString() {
        StringBuilder stringBuilder;
        if (PhoneDebug.VDBG) {
            stringBuilder = new StringBuilder();
            stringBuilder.append('[');
            stringBuilder.append(this.mPlmn);
            stringBuilder.append(',');
            stringBuilder.append(this.mLac);
            stringBuilder.append(',');
            stringBuilder.append(this.mCid);
            stringBuilder.append(']');
            return stringBuilder.toString();
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append('[');
        stringBuilder.append(this.mPlmn);
        stringBuilder.append(']');
        return stringBuilder.toString();
    }

    public boolean isInLocationArea(SmsCbLocation area) {
        int i = this.mCid;
        if (i != -1 && i != area.mCid) {
            return false;
        }
        i = this.mLac;
        if (i == -1 || i == area.mLac) {
            return this.mPlmn.equals(area.mPlmn);
        }
        return false;
    }

    public boolean isInLocationArea(String plmn, int lac, int cid) {
        if (!this.mPlmn.equals(plmn)) {
            return false;
        }
        int i = this.mLac;
        if (i != -1 && i != lac) {
            return false;
        }
        i = this.mCid;
        if (i == -1 || i == cid) {
            return true;
        }
        return false;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mPlmn);
        dest.writeInt(this.mLac);
        dest.writeInt(this.mCid);
    }

    public int describeContents() {
        return 0;
    }
}
