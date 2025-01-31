package android.os;

import android.annotation.SystemApi;
import android.annotation.UnsupportedAppUsage;
import android.app.ActivityThread;
import android.app.Application;
import android.content.Context;
import android.media.MediaDrm;
import android.media.midi.MidiDeviceInfo;
import android.os.IDeviceIdentifiersPolicyService.Stub;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.telephony.TelephonyProperties;
import dalvik.system.VMRuntime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Build {
    public static final String BOARD = getString("ro.product.board");
    public static final String BOOTLOADER = getString("ro.bootloader");
    public static final String BRAND = getString("ro.product.brand");
    @Deprecated
    public static final String CPU_ABI;
    @Deprecated
    public static final String CPU_ABI2;
    public static final String DEVICE = getString("ro.product.device");
    public static final String DISPLAY = getString("ro.build.display.id");
    public static final String FINGERPRINT = deriveFingerprint();
    public static final String HARDWARE = getString("ro.hardware");
    public static final String HOST = getString("ro.build.host");
    public static final String ID = getString("ro.build.id");
    public static final boolean IS_CONTAINER = SystemProperties.getBoolean("ro.boot.container", false);
    @UnsupportedAppUsage
    public static final boolean IS_DEBUGGABLE;
    public static final boolean IS_EMULATOR = getString("ro.kernel.qemu").equals("1");
    public static final boolean IS_ENG = "eng".equals(TYPE);
    public static final boolean IS_TREBLE_ENABLED = SystemProperties.getBoolean("ro.treble.enabled", false);
    public static final boolean IS_USER = "user".equals(TYPE);
    public static final boolean IS_USERDEBUG = "userdebug".equals(TYPE);
    public static final String MANUFACTURER = getString("ro.product.manufacturer");
    public static final String MODEL = getString("ro.product.model");
    @SystemApi
    public static final boolean PERMISSIONS_REVIEW_REQUIRED = true;
    public static final String PRODUCT = getString("ro.product.name");
    @Deprecated
    public static final String RADIO = getString(TelephonyProperties.PROPERTY_BASEBAND_VERSION);
    @Deprecated
    public static final String SERIAL = getString("no.such.thing");
    public static final String[] SUPPORTED_32_BIT_ABIS;
    public static final String[] SUPPORTED_64_BIT_ABIS;
    public static final String[] SUPPORTED_ABIS;
    private static final String TAG = "Build";
    public static final String TAGS = getString("ro.build.tags");
    public static final long TIME = (getLong("ro.build.date.utc") * 1000);
    public static final String TYPE = getString("ro.build.type");
    public static final String UNKNOWN = "unknown";
    public static final String USER = getString("ro.build.user");

    public static class Partition {
        public static final String PARTITION_NAME_SYSTEM = "system";
        private final String mFingerprint;
        private final String mName;
        private final long mTimeMs;

        private Partition(String name, String fingerprint, long timeMs) {
            this.mName = name;
            this.mFingerprint = fingerprint;
            this.mTimeMs = timeMs;
        }

        public String getName() {
            return this.mName;
        }

        public String getFingerprint() {
            return this.mFingerprint;
        }

        public long getBuildTimeMillis() {
            return this.mTimeMs;
        }

        public boolean equals(Object o) {
            boolean z = false;
            if (!(o instanceof Partition)) {
                return false;
            }
            Partition op = (Partition) o;
            if (this.mName.equals(op.mName) && this.mFingerprint.equals(op.mFingerprint) && this.mTimeMs == op.mTimeMs) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.mName, this.mFingerprint, Long.valueOf(this.mTimeMs)});
        }
    }

    public static class VERSION {
        @UnsupportedAppUsage
        public static final String[] ACTIVE_CODENAMES;
        private static final String[] ALL_CODENAMES = Build.getStringList("ro.build.version.all_codenames", ",");
        public static final String BASE_OS;
        public static final String CODENAME = Build.getString("ro.build.version.codename");
        public static final int FIRST_SDK_INT = SystemProperties.getInt("ro.product.first_api_level", 0);
        public static final String INCREMENTAL = Build.getString("ro.build.version.incremental");
        public static final int MIN_SUPPORTED_TARGET_SDK_INT = SystemProperties.getInt("ro.build.version.min_supported_target_sdk", 0);
        @SystemApi
        public static final String PREVIEW_SDK_FINGERPRINT;
        public static final int PREVIEW_SDK_INT = SystemProperties.getInt("ro.build.version.preview_sdk", 0);
        public static final String RELEASE = Build.getString("ro.build.version.release");
        public static final int RESOURCES_SDK_INT = (SDK_INT + ACTIVE_CODENAMES.length);
        @Deprecated
        public static final String SDK;
        public static final int SDK_INT;
        public static final String SECURITY_PATCH;

        static {
            String str = "";
            BASE_OS = SystemProperties.get("ro.build.version.base_os", str);
            SECURITY_PATCH = SystemProperties.get("ro.build.version.security_patch", str);
            str = "ro.build.version.sdk";
            SDK = Build.getString(str);
            SDK_INT = SystemProperties.getInt(str, 0);
            str = "REL";
            PREVIEW_SDK_FINGERPRINT = SystemProperties.get("ro.build.version.preview_sdk_fingerprint", str);
            ACTIVE_CODENAMES = str.equals(ALL_CODENAMES[0]) ? new String[0] : ALL_CODENAMES;
        }
    }

    public static class VERSION_CODES {
        public static final int BASE = 1;
        public static final int BASE_1_1 = 2;
        public static final int CUPCAKE = 3;
        public static final int CUR_DEVELOPMENT = 10000;
        public static final int DONUT = 4;
        public static final int ECLAIR = 5;
        public static final int ECLAIR_0_1 = 6;
        public static final int ECLAIR_MR1 = 7;
        public static final int FROYO = 8;
        public static final int GINGERBREAD = 9;
        public static final int GINGERBREAD_MR1 = 10;
        public static final int HONEYCOMB = 11;
        public static final int HONEYCOMB_MR1 = 12;
        public static final int HONEYCOMB_MR2 = 13;
        public static final int ICE_CREAM_SANDWICH = 14;
        public static final int ICE_CREAM_SANDWICH_MR1 = 15;
        public static final int JELLY_BEAN = 16;
        public static final int JELLY_BEAN_MR1 = 17;
        public static final int JELLY_BEAN_MR2 = 18;
        public static final int KITKAT = 19;
        public static final int KITKAT_WATCH = 20;
        public static final int L = 21;
        public static final int LOLLIPOP = 21;
        public static final int LOLLIPOP_MR1 = 22;
        public static final int M = 23;
        public static final int N = 24;
        public static final int N_MR1 = 25;
        public static final int O = 26;
        public static final int O_MR1 = 27;
        public static final int P = 28;
        public static final int Q = 29;
    }

    static {
        String[] abiList;
        String str = ",";
        SUPPORTED_ABIS = getStringList("ro.product.cpu.abilist", str);
        SUPPORTED_32_BIT_ABIS = getStringList("ro.product.cpu.abilist32", str);
        SUPPORTED_64_BIT_ABIS = getStringList("ro.product.cpu.abilist64", str);
        if (VMRuntime.getRuntime().is64Bit()) {
            abiList = SUPPORTED_64_BIT_ABIS;
        } else {
            abiList = SUPPORTED_32_BIT_ABIS;
        }
        CPU_ABI = abiList[0];
        boolean z = true;
        if (abiList.length > 1) {
            CPU_ABI2 = abiList[1];
        } else {
            CPU_ABI2 = "";
        }
        if (SystemProperties.getInt("ro.debuggable", 0) != 1) {
            z = false;
        }
        IS_DEBUGGABLE = z;
    }

    public static String getSerial() {
        IDeviceIdentifiersPolicyService service = Stub.asInterface(ServiceManager.getService(Context.DEVICE_IDENTIFIERS_SERVICE));
        try {
            Application application = ActivityThread.currentApplication();
            return service.getSerialForPackage(application != null ? application.getPackageName() : null);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
            return "unknown";
        }
    }

    public static boolean is64BitAbi(String abi) {
        return VMRuntime.is64BitAbi(abi);
    }

    private static String deriveFingerprint() {
        String finger = SystemProperties.get("ro.build.fingerprint");
        if (!TextUtils.isEmpty(finger)) {
            return finger;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getString("ro.product.brand"));
        stringBuilder.append('/');
        stringBuilder.append(getString("ro.product.name"));
        stringBuilder.append('/');
        stringBuilder.append(getString("ro.product.device"));
        stringBuilder.append(':');
        stringBuilder.append(getString("ro.build.version.release"));
        stringBuilder.append('/');
        stringBuilder.append(getString("ro.build.id"));
        stringBuilder.append('/');
        stringBuilder.append(getString("ro.build.version.incremental"));
        stringBuilder.append(':');
        stringBuilder.append(getString("ro.build.type"));
        stringBuilder.append('/');
        stringBuilder.append(getString("ro.build.tags"));
        return stringBuilder.toString();
    }

    public static void ensureFingerprintProperty() {
        String str = "ro.build.fingerprint";
        if (TextUtils.isEmpty(SystemProperties.get(str))) {
            try {
                SystemProperties.set(str, FINGERPRINT);
            } catch (IllegalArgumentException e) {
                Slog.e(TAG, "Failed to set fingerprint property", e);
            }
        }
    }

    public static boolean isBuildConsistent() {
        boolean z = true;
        if (IS_ENG) {
            return true;
        }
        boolean z2 = IS_TREBLE_ENABLED;
        String str = TAG;
        if (z2) {
            int result = VintfObject.verifyWithoutAvb();
            if (result != 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Vendor interface is incompatible, error=");
                stringBuilder.append(String.valueOf(result));
                Slog.e(str, stringBuilder.toString());
            }
            if (result != 0) {
                z = false;
            }
            return z;
        }
        String system = SystemProperties.get("ro.build.fingerprint");
        String vendor = SystemProperties.get("ro.vendor.build.fingerprint");
        String bootimage = SystemProperties.get("ro.bootimage.build.fingerprint");
        String requiredBootloader = SystemProperties.get("ro.build.expect.bootloader");
        String currentBootloader = SystemProperties.get("ro.bootloader");
        String requiredRadio = SystemProperties.get("ro.build.expect.baseband");
        String currentRadio = SystemProperties.get(TelephonyProperties.PROPERTY_BASEBAND_VERSION);
        if (TextUtils.isEmpty(system)) {
            Slog.e(str, "Required ro.build.fingerprint is empty!");
            return false;
        } else if (TextUtils.isEmpty(vendor) || Objects.equals(system, vendor)) {
            return true;
        } else {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Mismatched fingerprints; system reported ");
            stringBuilder2.append(system);
            stringBuilder2.append(" but vendor reported ");
            stringBuilder2.append(vendor);
            Slog.e(str, stringBuilder2.toString());
            return false;
        }
    }

    public static List<Partition> getFingerprintedPartitions() {
        ArrayList<Partition> partitions = new ArrayList();
        for (String name : new String[]{"bootimage", "odm", MidiDeviceInfo.PROPERTY_PRODUCT, "product_services", "system", MediaDrm.PROPERTY_VENDOR}) {
            StringBuilder stringBuilder = new StringBuilder();
            String str = "ro.";
            stringBuilder.append(str);
            stringBuilder.append(name);
            stringBuilder.append(".build.fingerprint");
            String fingerprint = SystemProperties.get(stringBuilder.toString());
            if (!TextUtils.isEmpty(fingerprint)) {
                stringBuilder = new StringBuilder();
                stringBuilder.append(str);
                stringBuilder.append(name);
                stringBuilder.append(".build.date.utc");
                partitions.add(new Partition(name, fingerprint, getLong(stringBuilder.toString()) * 1000));
            }
        }
        return partitions;
    }

    public static String getRadioVersion() {
        String propVal = SystemProperties.get(TelephonyProperties.PROPERTY_BASEBAND_VERSION);
        return TextUtils.isEmpty(propVal) ? null : propVal;
    }

    @UnsupportedAppUsage
    private static String getString(String property) {
        return SystemProperties.get(property, "unknown");
    }

    private static String[] getStringList(String property, String separator) {
        String value = SystemProperties.get(property);
        if (value.isEmpty()) {
            return new String[0];
        }
        return value.split(separator);
    }

    @UnsupportedAppUsage
    private static long getLong(String property) {
        try {
            return Long.parseLong(SystemProperties.get(property));
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
