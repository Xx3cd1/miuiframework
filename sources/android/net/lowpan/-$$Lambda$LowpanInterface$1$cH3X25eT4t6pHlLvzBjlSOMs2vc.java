package android.net.lowpan;

import android.net.IpPrefix;
import android.net.lowpan.LowpanInterface.Callback;

/* compiled from: lambda */
public final /* synthetic */ class -$$Lambda$LowpanInterface$1$cH3X25eT4t6pHlLvzBjlSOMs2vc implements Runnable {
    private final /* synthetic */ Callback f$0;
    private final /* synthetic */ IpPrefix f$1;

    public /* synthetic */ -$$Lambda$LowpanInterface$1$cH3X25eT4t6pHlLvzBjlSOMs2vc(Callback callback, IpPrefix ipPrefix) {
        this.f$0 = callback;
        this.f$1 = ipPrefix;
    }

    public final void run() {
        this.f$0.onLinkNetworkRemoved(this.f$1);
    }
}
