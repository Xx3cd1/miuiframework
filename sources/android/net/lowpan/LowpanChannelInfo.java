package android.net.lowpan;

import android.net.wifi.WifiInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Objects;

public class LowpanChannelInfo implements Parcelable {
    public static final Creator<LowpanChannelInfo> CREATOR = new Creator<LowpanChannelInfo>() {
        public LowpanChannelInfo createFromParcel(Parcel in) {
            LowpanChannelInfo info = new LowpanChannelInfo();
            info.mIndex = in.readInt();
            info.mName = in.readString();
            info.mSpectrumCenterFrequency = in.readFloat();
            info.mSpectrumBandwidth = in.readFloat();
            info.mMaxTransmitPower = in.readInt();
            info.mIsMaskedByRegulatoryDomain = in.readBoolean();
            return info;
        }

        public LowpanChannelInfo[] newArray(int size) {
            return new LowpanChannelInfo[size];
        }
    };
    public static final float UNKNOWN_BANDWIDTH = 0.0f;
    public static final float UNKNOWN_FREQUENCY = 0.0f;
    public static final int UNKNOWN_POWER = Integer.MAX_VALUE;
    private int mIndex;
    private boolean mIsMaskedByRegulatoryDomain;
    private int mMaxTransmitPower;
    private String mName;
    private float mSpectrumBandwidth;
    private float mSpectrumCenterFrequency;

    /* synthetic */ LowpanChannelInfo(AnonymousClass1 x0) {
        this();
    }

    public static LowpanChannelInfo getChannelInfoForIeee802154Page0(int index) {
        LowpanChannelInfo info = new LowpanChannelInfo();
        if (index < 0) {
            info = null;
        } else if (index == 0) {
            info.mSpectrumCenterFrequency = 8.6830003E8f;
            info.mSpectrumBandwidth = 600000.0f;
        } else if (index < 11) {
            info.mSpectrumCenterFrequency = (((float) index) * 2000000.0f) + 9.04E8f;
            info.mSpectrumBandwidth = 0.0f;
        } else if (index < 26) {
            info.mSpectrumCenterFrequency = (((float) index) * 5000000.0f) + 2.34999987E9f;
            info.mSpectrumBandwidth = 2000000.0f;
        } else {
            info = null;
        }
        info.mName = Integer.toString(index);
        return info;
    }

    private LowpanChannelInfo() {
        this.mIndex = 0;
        this.mName = null;
        this.mSpectrumCenterFrequency = 0.0f;
        this.mSpectrumBandwidth = 0.0f;
        this.mMaxTransmitPower = Integer.MAX_VALUE;
        this.mIsMaskedByRegulatoryDomain = false;
    }

    private LowpanChannelInfo(int index, String name, float cf, float bw) {
        this.mIndex = 0;
        this.mName = null;
        this.mSpectrumCenterFrequency = 0.0f;
        this.mSpectrumBandwidth = 0.0f;
        this.mMaxTransmitPower = Integer.MAX_VALUE;
        this.mIsMaskedByRegulatoryDomain = false;
        this.mIndex = index;
        this.mName = name;
        this.mSpectrumCenterFrequency = cf;
        this.mSpectrumBandwidth = bw;
    }

    public String getName() {
        return this.mName;
    }

    public int getIndex() {
        return this.mIndex;
    }

    public int getMaxTransmitPower() {
        return this.mMaxTransmitPower;
    }

    public boolean isMaskedByRegulatoryDomain() {
        return this.mIsMaskedByRegulatoryDomain;
    }

    public float getSpectrumCenterFrequency() {
        return this.mSpectrumCenterFrequency;
    }

    public float getSpectrumBandwidth() {
        return this.mSpectrumBandwidth;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("Channel ");
        sb.append(this.mIndex);
        String str = this.mName;
        if (!(str == null || str.equals(Integer.toString(this.mIndex)))) {
            sb.append(" (");
            sb.append(this.mName);
            sb.append(")");
        }
        float f = this.mSpectrumCenterFrequency;
        int i = (f > 0.0f ? 1 : (f == 0.0f ? 0 : -1));
        String str2 = WifiInfo.FREQUENCY_UNITS;
        String str3 = "kHz";
        String str4 = "GHz";
        if (i > 0) {
            String str5 = ", SpectrumCenterFrequency: ";
            if (f > 1.0E9f) {
                sb.append(str5);
                sb.append(this.mSpectrumCenterFrequency / 1.0E9f);
                sb.append(str4);
            } else if (f > 1000000.0f) {
                sb.append(str5);
                sb.append(this.mSpectrumCenterFrequency / 1000000.0f);
                sb.append(str2);
            } else {
                sb.append(str5);
                sb.append(this.mSpectrumCenterFrequency / 1000.0f);
                sb.append(str3);
            }
        }
        f = this.mSpectrumBandwidth;
        if (f > 0.0f) {
            String str6 = ", SpectrumBandwidth: ";
            if (f > 1.0E9f) {
                sb.append(str6);
                sb.append(this.mSpectrumBandwidth / 1.0E9f);
                sb.append(str4);
            } else if (f > 1000000.0f) {
                sb.append(str6);
                sb.append(this.mSpectrumBandwidth / 1000000.0f);
                sb.append(str2);
            } else {
                sb.append(str6);
                sb.append(this.mSpectrumBandwidth / 1000.0f);
                sb.append(str3);
            }
        }
        if (this.mMaxTransmitPower != Integer.MAX_VALUE) {
            sb.append(", MaxTransmitPower: ");
            sb.append(this.mMaxTransmitPower);
            sb.append("dBm");
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof LowpanChannelInfo)) {
            return false;
        }
        LowpanChannelInfo rhs = (LowpanChannelInfo) obj;
        if (Objects.equals(this.mName, rhs.mName) && this.mIndex == rhs.mIndex && this.mIsMaskedByRegulatoryDomain == rhs.mIsMaskedByRegulatoryDomain && this.mSpectrumCenterFrequency == rhs.mSpectrumCenterFrequency && this.mSpectrumBandwidth == rhs.mSpectrumBandwidth && this.mMaxTransmitPower == rhs.mMaxTransmitPower) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.mName, Integer.valueOf(this.mIndex), Boolean.valueOf(this.mIsMaskedByRegulatoryDomain), Float.valueOf(this.mSpectrumCenterFrequency), Float.valueOf(this.mSpectrumBandwidth), Integer.valueOf(this.mMaxTransmitPower)});
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mIndex);
        dest.writeString(this.mName);
        dest.writeFloat(this.mSpectrumCenterFrequency);
        dest.writeFloat(this.mSpectrumBandwidth);
        dest.writeInt(this.mMaxTransmitPower);
        dest.writeBoolean(this.mIsMaskedByRegulatoryDomain);
    }
}
