package android.content;

import android.accounts.Account;
import android.annotation.UnsupportedAppUsage;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public class SyncRequest implements Parcelable {
    public static final Creator<SyncRequest> CREATOR = new Creator<SyncRequest>() {
        public SyncRequest createFromParcel(Parcel in) {
            return new SyncRequest(in, null);
        }

        public SyncRequest[] newArray(int size) {
            return new SyncRequest[size];
        }
    };
    private static final String TAG = "SyncRequest";
    @UnsupportedAppUsage
    private final Account mAccountToSync;
    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    private final String mAuthority;
    private final boolean mDisallowMetered;
    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    private final Bundle mExtras;
    private final boolean mIsAuthority;
    private final boolean mIsExpedited;
    @UnsupportedAppUsage
    private final boolean mIsPeriodic;
    private final long mSyncFlexTimeSecs;
    @UnsupportedAppUsage
    private final long mSyncRunTimeSecs;

    public static class Builder {
        private static final int SYNC_TARGET_ADAPTER = 2;
        private static final int SYNC_TARGET_UNKNOWN = 0;
        private static final int SYNC_TYPE_ONCE = 2;
        private static final int SYNC_TYPE_PERIODIC = 1;
        private static final int SYNC_TYPE_UNKNOWN = 0;
        private Account mAccount;
        private String mAuthority;
        private Bundle mCustomExtras;
        private boolean mDisallowMetered;
        private boolean mExpedited;
        private boolean mIgnoreBackoff;
        private boolean mIgnoreSettings;
        private boolean mIsManual;
        private boolean mNoRetry;
        private boolean mRequiresCharging;
        private Bundle mSyncConfigExtras;
        private long mSyncFlexTimeSecs;
        private long mSyncRunTimeSecs;
        private int mSyncTarget = 0;
        private int mSyncType = 0;

        public Builder syncOnce() {
            if (this.mSyncType == 0) {
                this.mSyncType = 2;
                setupInterval(0, 0);
                return this;
            }
            throw new IllegalArgumentException("Sync type has already been defined.");
        }

        public Builder syncPeriodic(long pollFrequency, long beforeSeconds) {
            if (this.mSyncType == 0) {
                this.mSyncType = 1;
                setupInterval(pollFrequency, beforeSeconds);
                return this;
            }
            throw new IllegalArgumentException("Sync type has already been defined.");
        }

        private void setupInterval(long at, long before) {
            if (before <= at) {
                this.mSyncRunTimeSecs = at;
                this.mSyncFlexTimeSecs = before;
                return;
            }
            throw new IllegalArgumentException("Specified run time for the sync must be after the specified flex time.");
        }

        public Builder setDisallowMetered(boolean disallow) {
            if (this.mIgnoreSettings && disallow) {
                throw new IllegalArgumentException("setDisallowMetered(true) after having specified that settings are ignored.");
            }
            this.mDisallowMetered = disallow;
            return this;
        }

        public Builder setRequiresCharging(boolean requiresCharging) {
            this.mRequiresCharging = requiresCharging;
            return this;
        }

        public Builder setSyncAdapter(Account account, String authority) {
            if (this.mSyncTarget != 0) {
                throw new IllegalArgumentException("Sync target has already been defined.");
            } else if (authority == null || authority.length() != 0) {
                this.mSyncTarget = 2;
                this.mAccount = account;
                this.mAuthority = authority;
                return this;
            } else {
                throw new IllegalArgumentException("Authority must be non-empty");
            }
        }

        public Builder setExtras(Bundle bundle) {
            this.mCustomExtras = bundle;
            return this;
        }

        public Builder setNoRetry(boolean noRetry) {
            this.mNoRetry = noRetry;
            return this;
        }

        public Builder setIgnoreSettings(boolean ignoreSettings) {
            if (this.mDisallowMetered && ignoreSettings) {
                throw new IllegalArgumentException("setIgnoreSettings(true) after having specified sync settings with this builder.");
            }
            this.mIgnoreSettings = ignoreSettings;
            return this;
        }

        public Builder setIgnoreBackoff(boolean ignoreBackoff) {
            this.mIgnoreBackoff = ignoreBackoff;
            return this;
        }

        public Builder setManual(boolean isManual) {
            this.mIsManual = isManual;
            return this;
        }

        public Builder setExpedited(boolean expedited) {
            this.mExpedited = expedited;
            return this;
        }

        public SyncRequest build() {
            ContentResolver.validateSyncExtrasBundle(this.mCustomExtras);
            if (this.mCustomExtras == null) {
                this.mCustomExtras = new Bundle();
            }
            this.mSyncConfigExtras = new Bundle();
            boolean z = this.mIgnoreBackoff;
            String str = ContentResolver.SYNC_EXTRAS_IGNORE_BACKOFF;
            if (z) {
                this.mSyncConfigExtras.putBoolean(str, true);
            }
            if (this.mDisallowMetered) {
                this.mSyncConfigExtras.putBoolean("allow_metered", true);
            }
            if (this.mRequiresCharging) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_REQUIRE_CHARGING, true);
            }
            z = this.mIgnoreSettings;
            String str2 = ContentResolver.SYNC_EXTRAS_IGNORE_SETTINGS;
            if (z) {
                this.mSyncConfigExtras.putBoolean(str2, true);
            }
            if (this.mNoRetry) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_DO_NOT_RETRY, true);
            }
            if (this.mExpedited) {
                this.mSyncConfigExtras.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            }
            if (this.mIsManual) {
                this.mSyncConfigExtras.putBoolean(str, true);
                this.mSyncConfigExtras.putBoolean(str2, true);
            }
            if (this.mSyncType == 1 && (ContentResolver.invalidPeriodicExtras(this.mCustomExtras) || ContentResolver.invalidPeriodicExtras(this.mSyncConfigExtras))) {
                throw new IllegalArgumentException("Illegal extras were set");
            } else if (this.mSyncTarget != 0) {
                return new SyncRequest(this);
            } else {
                throw new IllegalArgumentException("Must specify an adapter with setSyncAdapter(Account, String");
            }
        }
    }

    /* synthetic */ SyncRequest(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public boolean isPeriodic() {
        return this.mIsPeriodic;
    }

    public boolean isExpedited() {
        return this.mIsExpedited;
    }

    public Account getAccount() {
        return this.mAccountToSync;
    }

    public String getProvider() {
        return this.mAuthority;
    }

    public Bundle getBundle() {
        return this.mExtras;
    }

    public long getSyncFlexTime() {
        return this.mSyncFlexTimeSecs;
    }

    public long getSyncRunTime() {
        return this.mSyncRunTimeSecs;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeBundle(this.mExtras);
        parcel.writeLong(this.mSyncFlexTimeSecs);
        parcel.writeLong(this.mSyncRunTimeSecs);
        parcel.writeInt(this.mIsPeriodic);
        parcel.writeInt(this.mDisallowMetered);
        parcel.writeInt(this.mIsAuthority);
        parcel.writeInt(this.mIsExpedited);
        parcel.writeParcelable(this.mAccountToSync, flags);
        parcel.writeString(this.mAuthority);
    }

    private SyncRequest(Parcel in) {
        boolean z = true;
        this.mExtras = Bundle.setDefusable(in.readBundle(), true);
        this.mSyncFlexTimeSecs = in.readLong();
        this.mSyncRunTimeSecs = in.readLong();
        this.mIsPeriodic = in.readInt() != 0;
        this.mDisallowMetered = in.readInt() != 0;
        this.mIsAuthority = in.readInt() != 0;
        if (in.readInt() == 0) {
            z = false;
        }
        this.mIsExpedited = z;
        this.mAccountToSync = (Account) in.readParcelable(null);
        this.mAuthority = in.readString();
    }

    protected SyncRequest(Builder b) {
        this.mSyncFlexTimeSecs = b.mSyncFlexTimeSecs;
        this.mSyncRunTimeSecs = b.mSyncRunTimeSecs;
        this.mAccountToSync = b.mAccount;
        this.mAuthority = b.mAuthority;
        boolean z = false;
        this.mIsPeriodic = b.mSyncType == 1;
        if (b.mSyncTarget == 2) {
            z = true;
        }
        this.mIsAuthority = z;
        this.mIsExpedited = b.mExpedited;
        this.mExtras = new Bundle(b.mCustomExtras);
        this.mExtras.putAll(b.mSyncConfigExtras);
        this.mDisallowMetered = b.mDisallowMetered;
    }
}
