package android.net.wifi.p2p.nsd;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.text.format.DateFormat;
import java.util.Locale;

public class WifiP2pServiceRequest implements Parcelable {
    @UnsupportedAppUsage
    public static final Creator<WifiP2pServiceRequest> CREATOR = new Creator<WifiP2pServiceRequest>() {
        public WifiP2pServiceRequest createFromParcel(Parcel in) {
            return new WifiP2pServiceRequest(in.readInt(), in.readInt(), in.readInt(), in.readString(), null);
        }

        public WifiP2pServiceRequest[] newArray(int size) {
            return new WifiP2pServiceRequest[size];
        }
    };
    private int mLength;
    private int mProtocolType;
    private String mQuery;
    private int mTransId;

    /* synthetic */ WifiP2pServiceRequest(int x0, int x1, int x2, String x3, AnonymousClass1 x4) {
        this(x0, x1, x2, x3);
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    protected WifiP2pServiceRequest(int protocolType, String query) {
        validateQuery(query);
        this.mProtocolType = protocolType;
        this.mQuery = query;
        if (query != null) {
            this.mLength = (query.length() / 2) + 2;
        } else {
            this.mLength = 2;
        }
    }

    private WifiP2pServiceRequest(int serviceType, int length, int transId, String query) {
        this.mProtocolType = serviceType;
        this.mLength = length;
        this.mTransId = transId;
        this.mQuery = query;
    }

    public int getTransactionId() {
        return this.mTransId;
    }

    public void setTransactionId(int id) {
        this.mTransId = id;
    }

    public String getSupplicantQuery() {
        StringBuffer sb = new StringBuffer();
        String str = "%02x";
        sb.append(String.format(Locale.US, str, new Object[]{Integer.valueOf(this.mLength & 255)}));
        sb.append(String.format(Locale.US, str, new Object[]{Integer.valueOf((this.mLength >> 8) & 255)}));
        sb.append(String.format(Locale.US, str, new Object[]{Integer.valueOf(this.mProtocolType)}));
        sb.append(String.format(Locale.US, str, new Object[]{Integer.valueOf(this.mTransId)}));
        String str2 = this.mQuery;
        if (str2 != null) {
            sb.append(str2);
        }
        return sb.toString();
    }

    private void validateQuery(String query) {
        if (query != null) {
            StringBuilder stringBuilder;
            if (query.length() % 2 == 1) {
                stringBuilder = new StringBuilder();
                stringBuilder.append("query size is invalid. query=");
                stringBuilder.append(query);
                throw new IllegalArgumentException(stringBuilder.toString());
            } else if (query.length() / 2 <= 65535) {
                query = query.toLowerCase(Locale.ROOT);
                for (char c : query.toCharArray()) {
                    if ((c < '0' || c > '9') && (c < DateFormat.AM_PM || c > 'f')) {
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append("query should be hex string. query=");
                        stringBuilder2.append(query);
                        throw new IllegalArgumentException(stringBuilder2.toString());
                    }
                }
            } else {
                stringBuilder = new StringBuilder();
                stringBuilder.append("query size is too large. len=");
                stringBuilder.append(query.length());
                throw new IllegalArgumentException(stringBuilder.toString());
            }
        }
    }

    public static WifiP2pServiceRequest newInstance(int protocolType, String queryData) {
        return new WifiP2pServiceRequest(protocolType, queryData);
    }

    public static WifiP2pServiceRequest newInstance(int protocolType) {
        return new WifiP2pServiceRequest(protocolType, null);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof WifiP2pServiceRequest)) {
            return false;
        }
        WifiP2pServiceRequest req = (WifiP2pServiceRequest) o;
        if (req.mProtocolType != this.mProtocolType || req.mLength != this.mLength) {
            return false;
        }
        if (req.mQuery == null && this.mQuery == null) {
            return true;
        }
        String str = req.mQuery;
        if (str != null) {
            return str.equals(this.mQuery);
        }
        return false;
    }

    public int hashCode() {
        int i = ((((17 * 31) + this.mProtocolType) * 31) + this.mLength) * 31;
        String str = this.mQuery;
        return i + (str == null ? 0 : str.hashCode());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mProtocolType);
        dest.writeInt(this.mLength);
        dest.writeInt(this.mTransId);
        dest.writeString(this.mQuery);
    }
}
