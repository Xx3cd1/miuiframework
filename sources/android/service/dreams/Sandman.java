package android.service.dreams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.service.dreams.IDreamManager.Stub;
import android.util.Slog;
import com.android.internal.R;

public final class Sandman {
    private static final ComponentName SOMNAMBULATOR_COMPONENT = new ComponentName("com.android.systemui", "com.android.systemui.Somnambulator");
    private static final String TAG = "Sandman";

    private Sandman() {
    }

    public static boolean shouldStartDockApp(Context context, Intent intent) {
        ComponentName name = intent.resolveActivity(context.getPackageManager());
        return (name == null || name.equals(SOMNAMBULATOR_COMPONENT)) ? false : true;
    }

    public static void startDreamByUserRequest(Context context) {
        startDream(context, false);
    }

    public static void startDreamWhenDockedIfAppropriate(Context context) {
        if (isScreenSaverEnabled(context) && isScreenSaverActivatedOnDock(context)) {
            startDream(context, true);
        } else {
            Slog.i(TAG, "Dreams currently disabled for docks.");
        }
    }

    private static void startDream(Context context, boolean docked) {
        String str = TAG;
        try {
            IDreamManager dreamManagerService = Stub.asInterface(ServiceManager.getService(DreamService.DREAM_SERVICE));
            if (dreamManagerService != null && !dreamManagerService.isDreaming()) {
                if (docked) {
                    Slog.i(str, "Activating dream while docked.");
                    ((PowerManager) context.getSystemService(PowerManager.class)).wakeUp(SystemClock.uptimeMillis(), 3, "android.service.dreams:DREAM");
                } else {
                    Slog.i(str, "Activating dream by user request.");
                }
                dreamManagerService.dream();
            }
        } catch (RemoteException ex) {
            Slog.e(str, "Could not start dream when docked.", ex);
        }
    }

    private static boolean isScreenSaverEnabled(Context context) {
        if (Secure.getIntForUser(context.getContentResolver(), Secure.SCREENSAVER_ENABLED, context.getResources().getBoolean(R.bool.config_dreamsEnabledByDefault) ? 1 : 0, -2) != 0) {
            return true;
        }
        return false;
    }

    private static boolean isScreenSaverActivatedOnDock(Context context) {
        if (Secure.getIntForUser(context.getContentResolver(), Secure.SCREENSAVER_ACTIVATE_ON_DOCK, context.getResources().getBoolean(R.bool.config_dreamsActivatedOnDockByDefault) ? 1 : 0, -2) != 0) {
            return true;
        }
        return false;
    }
}
