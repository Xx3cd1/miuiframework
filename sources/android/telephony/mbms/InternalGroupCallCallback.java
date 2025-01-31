package android.telephony.mbms;

import android.os.Binder;
import android.telephony.mbms.IGroupCallCallback.Stub;
import java.util.concurrent.Executor;

public class InternalGroupCallCallback extends Stub {
    private final GroupCallCallback mAppCallback;
    private final Executor mExecutor;
    private volatile boolean mIsStopped = false;

    public InternalGroupCallCallback(GroupCallCallback appCallback, Executor executor) {
        this.mAppCallback = appCallback;
        this.mExecutor = executor;
    }

    public void onError(final int errorCode, final String message) {
        if (!this.mIsStopped) {
            long token = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() {
                    public void run() {
                        InternalGroupCallCallback.this.mAppCallback.onError(errorCode, message);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public void onGroupCallStateChanged(final int state, final int reason) {
        if (!this.mIsStopped) {
            long token = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() {
                    public void run() {
                        InternalGroupCallCallback.this.mAppCallback.onGroupCallStateChanged(state, reason);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public void onBroadcastSignalStrengthUpdated(final int signalStrength) {
        if (!this.mIsStopped) {
            long token = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(new Runnable() {
                    public void run() {
                        InternalGroupCallCallback.this.mAppCallback.onBroadcastSignalStrengthUpdated(signalStrength);
                    }
                });
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }
    }

    public void stop() {
        this.mIsStopped = true;
    }
}
