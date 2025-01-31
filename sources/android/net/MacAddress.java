package android.net;

import android.annotation.UnsupportedAppUsage;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.android.internal.util.BitUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.Inet6Address;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public final class MacAddress implements Parcelable {
    @UnsupportedAppUsage
    public static final MacAddress ALL_ZEROS_ADDRESS = new MacAddress(0);
    private static final MacAddress BASE_GOOGLE_MAC = fromString("da:a1:19:0:0:0");
    public static final MacAddress BROADCAST_ADDRESS = fromBytes(ETHER_ADDR_BROADCAST);
    public static final Creator<MacAddress> CREATOR = new Creator<MacAddress>() {
        public MacAddress createFromParcel(Parcel in) {
            return new MacAddress(in.readLong(), null);
        }

        public MacAddress[] newArray(int size) {
            return new MacAddress[size];
        }
    };
    private static final byte[] ETHER_ADDR_BROADCAST = addr(255, 255, 255, 255, 255, 255);
    private static final int ETHER_ADDR_LEN = 6;
    private static final long LOCALLY_ASSIGNED_MASK = fromString("2:0:0:0:0:0").mAddr;
    private static final long MULTICAST_MASK = fromString("1:0:0:0:0:0").mAddr;
    private static final long NIC_MASK = fromString("0:0:0:ff:ff:ff").mAddr;
    private static final long OUI_MASK = fromString("ff:ff:ff:0:0:0").mAddr;
    public static final int TYPE_BROADCAST = 3;
    public static final int TYPE_MULTICAST = 2;
    public static final int TYPE_UNICAST = 1;
    public static final int TYPE_UNKNOWN = 0;
    private static final long VALID_LONG_MASK = 281474976710655L;
    private final long mAddr;

    @Retention(RetentionPolicy.SOURCE)
    public @interface MacAddressType {
    }

    /* synthetic */ MacAddress(long x0, AnonymousClass1 x1) {
        this(x0);
    }

    private MacAddress(long addr) {
        this.mAddr = VALID_LONG_MASK & addr;
    }

    public int getAddressType() {
        if (equals(BROADCAST_ADDRESS)) {
            return 3;
        }
        if (isMulticastAddress()) {
            return 2;
        }
        return 1;
    }

    public boolean isMulticastAddress() {
        return (this.mAddr & MULTICAST_MASK) != 0;
    }

    public boolean isLocallyAssigned() {
        return (this.mAddr & LOCALLY_ASSIGNED_MASK) != 0;
    }

    public byte[] toByteArray() {
        return byteAddrFromLongAddr(this.mAddr);
    }

    public String toString() {
        return stringAddrFromLongAddr(this.mAddr);
    }

    public String toOuiString() {
        return String.format("%02x:%02x:%02x", new Object[]{Long.valueOf((this.mAddr >> 40) & 255), Long.valueOf((this.mAddr >> 32) & 255), Long.valueOf((this.mAddr >> 24) & 255)});
    }

    public int hashCode() {
        long j = this.mAddr;
        return (int) (j ^ (j >> 32));
    }

    public boolean equals(Object o) {
        return (o instanceof MacAddress) && ((MacAddress) o).mAddr == this.mAddr;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mAddr);
    }

    public int describeContents() {
        return 0;
    }

    public static boolean isMacAddress(byte[] addr) {
        return addr != null && addr.length == 6;
    }

    public static int macAddressType(byte[] addr) {
        if (isMacAddress(addr)) {
            return fromBytes(addr).getAddressType();
        }
        return 0;
    }

    public static byte[] byteAddrFromStringAddr(String addr) {
        Preconditions.checkNotNull(addr);
        String[] parts = addr.split(":");
        if (parts.length == 6) {
            byte[] bytes = new byte[6];
            for (int i = 0; i < 6; i++) {
                int x = Integer.valueOf(parts[i], 16).intValue();
                if (x < 0 || 255 < x) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(addr);
                    stringBuilder.append("was not a valid MAC address");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                bytes[i] = (byte) x;
            }
            return bytes;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(addr);
        stringBuilder2.append(" was not a valid MAC address");
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    public static String stringAddrFromByteAddr(byte[] addr) {
        if (!isMacAddress(addr)) {
            return null;
        }
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x", new Object[]{Byte.valueOf(addr[0]), Byte.valueOf(addr[1]), Byte.valueOf(addr[2]), Byte.valueOf(addr[3]), Byte.valueOf(addr[4]), Byte.valueOf(addr[5])});
    }

    private static byte[] byteAddrFromLongAddr(long addr) {
        byte[] bytes = new byte[6];
        int index = 6;
        while (true) {
            int index2 = index - 1;
            if (index <= 0) {
                return bytes;
            }
            bytes[index2] = (byte) ((int) addr);
            addr >>= 8;
            index = index2;
        }
    }

    private static long longAddrFromByteAddr(byte[] addr) {
        Preconditions.checkNotNull(addr);
        if (isMacAddress(addr)) {
            long longAddr = 0;
            for (byte b : addr) {
                longAddr = (longAddr << 8) + ((long) BitUtils.uint8(b));
            }
            return longAddr;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Arrays.toString(addr));
        stringBuilder.append(" was not a valid MAC address");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private static long longAddrFromStringAddr(String addr) {
        Preconditions.checkNotNull(addr);
        String[] parts = addr.split(":");
        if (parts.length == 6) {
            long longAddr = 0;
            for (String valueOf : parts) {
                int x = Integer.valueOf(valueOf, 16).intValue();
                if (x < 0 || 255 < x) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(addr);
                    stringBuilder.append("was not a valid MAC address");
                    throw new IllegalArgumentException(stringBuilder.toString());
                }
                longAddr = ((long) x) + (longAddr << 8);
            }
            return longAddr;
        }
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(addr);
        stringBuilder2.append(" was not a valid MAC address");
        throw new IllegalArgumentException(stringBuilder2.toString());
    }

    private static String stringAddrFromLongAddr(long addr) {
        return String.format("%02x:%02x:%02x:%02x:%02x:%02x", new Object[]{Long.valueOf((addr >> 40) & 255), Long.valueOf((addr >> 32) & 255), Long.valueOf((addr >> 24) & 255), Long.valueOf((addr >> 16) & 255), Long.valueOf((addr >> 8) & 255), Long.valueOf(addr & 255)});
    }

    public static MacAddress fromString(String addr) {
        return new MacAddress(longAddrFromStringAddr(addr));
    }

    public static MacAddress fromBytes(byte[] addr) {
        return new MacAddress(longAddrFromByteAddr(addr));
    }

    public static MacAddress createRandomUnicastAddressWithGoogleBase() {
        return createRandomUnicastAddress(BASE_GOOGLE_MAC, new SecureRandom());
    }

    public static MacAddress createRandomUnicastAddress() {
        return new MacAddress(((new SecureRandom().nextLong() & VALID_LONG_MASK) | LOCALLY_ASSIGNED_MASK) & (~MULTICAST_MASK));
    }

    public static MacAddress createRandomUnicastAddress(MacAddress base, Random r) {
        return new MacAddress((((base.mAddr & OUI_MASK) | (NIC_MASK & r.nextLong())) | LOCALLY_ASSIGNED_MASK) & (~MULTICAST_MASK));
    }

    private static byte[] addr(int... in) {
        if (in.length == 6) {
            byte[] out = new byte[6];
            for (int i = 0; i < 6; i++) {
                out[i] = (byte) in[i];
            }
            return out;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Arrays.toString(in));
        stringBuilder.append(" was not an array with length equal to ");
        stringBuilder.append(6);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    public boolean matches(MacAddress baseAddress, MacAddress mask) {
        Preconditions.checkNotNull(baseAddress);
        Preconditions.checkNotNull(mask);
        long j = this.mAddr;
        long j2 = mask.mAddr;
        return (j & j2) == (j2 & baseAddress.mAddr);
    }

    public Inet6Address getLinkLocalIpv6FromEui48Mac() {
        byte[] macEui48Bytes = toByteArray();
        byte[] addr = new byte[16];
        addr[0] = (byte) -2;
        addr[1] = Byte.MIN_VALUE;
        addr[8] = (byte) (macEui48Bytes[0] ^ 2);
        addr[9] = macEui48Bytes[1];
        addr[10] = macEui48Bytes[2];
        addr[11] = (byte) -1;
        addr[12] = (byte) -2;
        addr[13] = macEui48Bytes[3];
        addr[14] = macEui48Bytes[4];
        addr[15] = macEui48Bytes[5];
        Inet6Address inet6Address = null;
        try {
            inet6Address = Inet6Address.getByAddress(null, addr, 0);
            return inet6Address;
        } catch (UnknownHostException e) {
            return inet6Address;
        }
    }
}
