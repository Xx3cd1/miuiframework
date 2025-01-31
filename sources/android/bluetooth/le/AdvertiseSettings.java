package android.bluetooth.le;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class AdvertiseSettings implements Parcelable {
    public static final int ADVERTISE_MODE_BALANCED = 1;
    public static final int ADVERTISE_MODE_LOW_LATENCY = 2;
    public static final int ADVERTISE_MODE_LOW_POWER = 0;
    public static final int ADVERTISE_TX_POWER_HIGH = 3;
    public static final int ADVERTISE_TX_POWER_LOW = 1;
    public static final int ADVERTISE_TX_POWER_MEDIUM = 2;
    public static final int ADVERTISE_TX_POWER_ULTRA_LOW = 0;
    public static final Creator<AdvertiseSettings> CREATOR = new Creator<AdvertiseSettings>() {
        public AdvertiseSettings[] newArray(int size) {
            return new AdvertiseSettings[size];
        }

        public AdvertiseSettings createFromParcel(Parcel in) {
            return new AdvertiseSettings(in, null);
        }
    };
    private static final int LIMITED_ADVERTISING_MAX_MILLIS = 180000;
    private final boolean mAdvertiseConnectable;
    private final int mAdvertiseMode;
    private final int mAdvertiseTimeoutMillis;
    private final int mAdvertiseTxPowerLevel;

    public static final class Builder {
        private boolean mConnectable = true;
        private int mMode = 0;
        private int mTimeoutMillis = 0;
        private int mTxPowerLevel = 2;

        public Builder setAdvertiseMode(int advertiseMode) {
            if (advertiseMode < 0 || advertiseMode > 2) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unknown mode ");
                stringBuilder.append(advertiseMode);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.mMode = advertiseMode;
            return this;
        }

        public Builder setTxPowerLevel(int txPowerLevel) {
            if (txPowerLevel < 0 || txPowerLevel > 3) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("unknown tx power level ");
                stringBuilder.append(txPowerLevel);
                throw new IllegalArgumentException(stringBuilder.toString());
            }
            this.mTxPowerLevel = txPowerLevel;
            return this;
        }

        public Builder setConnectable(boolean connectable) {
            this.mConnectable = connectable;
            return this;
        }

        public Builder setTimeout(int timeoutMillis) {
            if (timeoutMillis < 0 || timeoutMillis > AdvertiseSettings.LIMITED_ADVERTISING_MAX_MILLIS) {
                throw new IllegalArgumentException("timeoutMillis invalid (must be 0-180000 milliseconds)");
            }
            this.mTimeoutMillis = timeoutMillis;
            return this;
        }

        public AdvertiseSettings build() {
            return new AdvertiseSettings(this.mMode, this.mTxPowerLevel, this.mConnectable, this.mTimeoutMillis, null);
        }
    }

    /* synthetic */ AdvertiseSettings(int x0, int x1, boolean x2, int x3, AnonymousClass1 x4) {
        this(x0, x1, x2, x3);
    }

    /* synthetic */ AdvertiseSettings(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    private AdvertiseSettings(int advertiseMode, int advertiseTxPowerLevel, boolean advertiseConnectable, int advertiseTimeout) {
        this.mAdvertiseMode = advertiseMode;
        this.mAdvertiseTxPowerLevel = advertiseTxPowerLevel;
        this.mAdvertiseConnectable = advertiseConnectable;
        this.mAdvertiseTimeoutMillis = advertiseTimeout;
    }

    private AdvertiseSettings(Parcel in) {
        this.mAdvertiseMode = in.readInt();
        this.mAdvertiseTxPowerLevel = in.readInt();
        this.mAdvertiseConnectable = in.readInt() != 0;
        this.mAdvertiseTimeoutMillis = in.readInt();
    }

    public int getMode() {
        return this.mAdvertiseMode;
    }

    public int getTxPowerLevel() {
        return this.mAdvertiseTxPowerLevel;
    }

    public boolean isConnectable() {
        return this.mAdvertiseConnectable;
    }

    public int getTimeout() {
        return this.mAdvertiseTimeoutMillis;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Settings [mAdvertiseMode=");
        stringBuilder.append(this.mAdvertiseMode);
        stringBuilder.append(", mAdvertiseTxPowerLevel=");
        stringBuilder.append(this.mAdvertiseTxPowerLevel);
        stringBuilder.append(", mAdvertiseConnectable=");
        stringBuilder.append(this.mAdvertiseConnectable);
        stringBuilder.append(", mAdvertiseTimeoutMillis=");
        stringBuilder.append(this.mAdvertiseTimeoutMillis);
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAdvertiseMode);
        dest.writeInt(this.mAdvertiseTxPowerLevel);
        dest.writeInt(this.mAdvertiseConnectable);
        dest.writeInt(this.mAdvertiseTimeoutMillis);
    }
}
