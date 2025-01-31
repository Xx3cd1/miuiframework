package android.content;

import android.accounts.Account;
import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SyncInfo implements Parcelable {
    @UnsupportedAppUsage
    public static final Creator<SyncInfo> CREATOR = new Creator<SyncInfo>() {
        public SyncInfo createFromParcel(Parcel in) {
            return new SyncInfo(in);
        }

        public SyncInfo[] newArray(int size) {
            return new SyncInfo[size];
        }
    };
    private static final Account REDACTED_ACCOUNT;
    public final Account account;
    public final String authority;
    @UnsupportedAppUsage
    public final int authorityId;
    public final long startTime;

    static {
        String str = "*****";
        REDACTED_ACCOUNT = new Account(str, str);
    }

    public static SyncInfo createAccountRedacted(int authorityId, String authority, long startTime) {
        return new SyncInfo(authorityId, REDACTED_ACCOUNT, authority, startTime);
    }

    @UnsupportedAppUsage
    public SyncInfo(int authorityId, Account account, String authority, long startTime) {
        this.authorityId = authorityId;
        this.account = account;
        this.authority = authority;
        this.startTime = startTime;
    }

    public SyncInfo(SyncInfo other) {
        this.authorityId = other.authorityId;
        this.account = new Account(other.account.name, other.account.type);
        this.authority = other.authority;
        this.startTime = other.startTime;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.authorityId);
        parcel.writeParcelable(this.account, flags);
        parcel.writeString(this.authority);
        parcel.writeLong(this.startTime);
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    SyncInfo(Parcel parcel) {
        this.authorityId = parcel.readInt();
        this.account = (Account) parcel.readParcelable(Account.class.getClassLoader());
        this.authority = parcel.readString();
        this.startTime = parcel.readLong();
    }
}
