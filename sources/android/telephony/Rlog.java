package android.telephony;

import android.annotation.UnsupportedAppUsage;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Rlog {
    private static final boolean USER_BUILD = Build.IS_USER;

    private Rlog() {
    }

    @UnsupportedAppUsage
    public static int v(String tag, String msg) {
        return Log.println_native(1, 2, tag, msg);
    }

    public static int v(String tag, String msg, Throwable tr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(msg);
        stringBuilder.append(10);
        stringBuilder.append(Log.getStackTraceString(tr));
        return Log.println_native(1, 2, tag, stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public static int d(String tag, String msg) {
        return Log.println_native(1, 3, tag, msg);
    }

    @UnsupportedAppUsage
    public static int d(String tag, String msg, Throwable tr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(msg);
        stringBuilder.append(10);
        stringBuilder.append(Log.getStackTraceString(tr));
        return Log.println_native(1, 3, tag, stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public static int i(String tag, String msg) {
        return Log.println_native(1, 4, tag, msg);
    }

    @UnsupportedAppUsage(maxTargetSdk = 28, trackingBug = 115609023)
    public static int i(String tag, String msg, Throwable tr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(msg);
        stringBuilder.append(10);
        stringBuilder.append(Log.getStackTraceString(tr));
        return Log.println_native(1, 4, tag, stringBuilder.toString());
    }

    @UnsupportedAppUsage
    public static int w(String tag, String msg) {
        return Log.println_native(1, 5, tag, msg);
    }

    @UnsupportedAppUsage
    public static int w(String tag, String msg, Throwable tr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(msg);
        stringBuilder.append(10);
        stringBuilder.append(Log.getStackTraceString(tr));
        return Log.println_native(1, 5, tag, stringBuilder.toString());
    }

    public static int w(String tag, Throwable tr) {
        return Log.println_native(1, 5, tag, Log.getStackTraceString(tr));
    }

    @UnsupportedAppUsage
    public static int e(String tag, String msg) {
        return Log.println_native(1, 6, tag, msg);
    }

    @UnsupportedAppUsage
    public static int e(String tag, String msg, Throwable tr) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(msg);
        stringBuilder.append(10);
        stringBuilder.append(Log.getStackTraceString(tr));
        return Log.println_native(1, 6, tag, stringBuilder.toString());
    }

    public static int println(int priority, String tag, String msg) {
        return Log.println_native(1, priority, tag, msg);
    }

    public static boolean isLoggable(String tag, int level) {
        return Log.isLoggable(tag, level);
    }

    public static String pii(String tag, Object pii) {
        String val = String.valueOf(pii);
        if (pii == null || TextUtils.isEmpty(val) || isLoggable(tag, 2)) {
            return val;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(secureHash(val.getBytes()));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static String pii(boolean enablePiiLogging, Object pii) {
        String val = String.valueOf(pii);
        if (pii == null || TextUtils.isEmpty(val) || enablePiiLogging) {
            return val;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        stringBuilder.append(secureHash(val.getBytes()));
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    private static String secureHash(byte[] input) {
        if (USER_BUILD) {
            return "****";
        }
        try {
            return Base64.encodeToString(MessageDigest.getInstance(KeyProperties.DIGEST_SHA1).digest(input), 11);
        } catch (NoSuchAlgorithmException e) {
            return "####";
        }
    }
}
