package android.net;

import android.net.DnsResolver.Callback;
import android.net.DnsResolver.DnsException;
import android.system.ErrnoException;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$DnsResolver$h2SsAzA5_rVr-mzxppK8PJLQe98 implements Runnable {
    private final /* synthetic */ Callback f$0;
    private final /* synthetic */ ErrnoException f$1;

    public /* synthetic */ -$$Lambda$DnsResolver$h2SsAzA5_rVr-mzxppK8PJLQe98(Callback callback, ErrnoException errnoException) {
        this.f$0 = callback;
        this.f$1 = errnoException;
    }

    public final void run() {
        this.f$0.onError(new DnsException(1, this.f$1));
    }
}
