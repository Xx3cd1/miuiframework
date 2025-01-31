package android.app.servertransaction;

import android.app.ClientTransactionHandler;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.Trace;

public class DestroyActivityItem extends ActivityLifecycleItem {
    public static final Creator<DestroyActivityItem> CREATOR = new Creator<DestroyActivityItem>() {
        public DestroyActivityItem createFromParcel(Parcel in) {
            return new DestroyActivityItem(in, null);
        }

        public DestroyActivityItem[] newArray(int size) {
            return new DestroyActivityItem[size];
        }
    };
    private int mConfigChanges;
    private boolean mFinished;

    /* synthetic */ DestroyActivityItem(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public void preExecute(ClientTransactionHandler client, IBinder token) {
        client.getActivitiesToBeDestroyed().put(token, this);
    }

    public void execute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64, "activityDestroy");
        client.handleDestroyActivity(token, this.mFinished, this.mConfigChanges, false, "DestroyActivityItem");
        Trace.traceEnd(64);
    }

    public int getTargetState() {
        return 6;
    }

    private DestroyActivityItem() {
    }

    public static DestroyActivityItem obtain(boolean finished, int configChanges) {
        DestroyActivityItem instance = (DestroyActivityItem) ObjectPool.obtain(DestroyActivityItem.class);
        if (instance == null) {
            instance = new DestroyActivityItem();
        }
        instance.mFinished = finished;
        instance.mConfigChanges = configChanges;
        return instance;
    }

    public void recycle() {
        super.recycle();
        this.mFinished = false;
        this.mConfigChanges = 0;
        ObjectPool.recycle(this);
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mFinished);
        dest.writeInt(this.mConfigChanges);
    }

    private DestroyActivityItem(Parcel in) {
        this.mFinished = in.readBoolean();
        this.mConfigChanges = in.readInt();
    }

    public boolean equals(Object o) {
        boolean z = true;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DestroyActivityItem other = (DestroyActivityItem) o;
        if (!(this.mFinished == other.mFinished && this.mConfigChanges == other.mConfigChanges)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        return (((17 * 31) + this.mFinished) * 31) + this.mConfigChanges;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("DestroyActivityItem{finished=");
        stringBuilder.append(this.mFinished);
        stringBuilder.append(",mConfigChanges=");
        stringBuilder.append(this.mConfigChanges);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }
}
