package android.print;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.print.PrintAttributes.Margins;
import android.print.PrintAttributes.MediaSize;
import android.print.PrintAttributes.Resolution;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;

public final class PrinterCapabilitiesInfo implements Parcelable {
    public static final Creator<PrinterCapabilitiesInfo> CREATOR = new Creator<PrinterCapabilitiesInfo>() {
        public PrinterCapabilitiesInfo createFromParcel(Parcel parcel) {
            return new PrinterCapabilitiesInfo(parcel, null);
        }

        public PrinterCapabilitiesInfo[] newArray(int size) {
            return new PrinterCapabilitiesInfo[size];
        }
    };
    private static final Margins DEFAULT_MARGINS = new Margins(0, 0, 0, 0);
    public static final int DEFAULT_UNDEFINED = -1;
    private static final int PROPERTY_COLOR_MODE = 2;
    private static final int PROPERTY_COUNT = 4;
    private static final int PROPERTY_DUPLEX_MODE = 3;
    private static final int PROPERTY_MEDIA_SIZE = 0;
    private static final int PROPERTY_RESOLUTION = 1;
    private int mColorModes;
    private final int[] mDefaults;
    private int mDuplexModes;
    private List<MediaSize> mMediaSizes;
    private Margins mMinMargins;
    private List<Resolution> mResolutions;

    public static final class Builder {
        private final PrinterCapabilitiesInfo mPrototype;

        public Builder(PrinterId printerId) {
            if (printerId != null) {
                this.mPrototype = new PrinterCapabilitiesInfo();
                return;
            }
            throw new IllegalArgumentException("printerId cannot be null.");
        }

        public Builder addMediaSize(MediaSize mediaSize, boolean isDefault) {
            if (this.mPrototype.mMediaSizes == null) {
                this.mPrototype.mMediaSizes = new ArrayList();
            }
            int insertionIndex = this.mPrototype.mMediaSizes.size();
            this.mPrototype.mMediaSizes.add(mediaSize);
            if (isDefault) {
                throwIfDefaultAlreadySpecified(0);
                this.mPrototype.mDefaults[0] = insertionIndex;
            }
            return this;
        }

        public Builder addResolution(Resolution resolution, boolean isDefault) {
            if (this.mPrototype.mResolutions == null) {
                this.mPrototype.mResolutions = new ArrayList();
            }
            int insertionIndex = this.mPrototype.mResolutions.size();
            this.mPrototype.mResolutions.add(resolution);
            if (isDefault) {
                throwIfDefaultAlreadySpecified(1);
                this.mPrototype.mDefaults[1] = insertionIndex;
            }
            return this;
        }

        public Builder setMinMargins(Margins margins) {
            if (margins != null) {
                this.mPrototype.mMinMargins = margins;
                return this;
            }
            throw new IllegalArgumentException("margins cannot be null");
        }

        public Builder setColorModes(int colorModes, int defaultColorMode) {
            PrinterCapabilitiesInfo.enforceValidMask(colorModes, -$$Lambda$PrinterCapabilitiesInfo$Builder$dbsSt8pZfd6hqZ6hGCnpzhPK6Uk.INSTANCE);
            PrintAttributes.enforceValidColorMode(defaultColorMode);
            this.mPrototype.mColorModes = colorModes;
            this.mPrototype.mDefaults[2] = defaultColorMode;
            return this;
        }

        public Builder setDuplexModes(int duplexModes, int defaultDuplexMode) {
            PrinterCapabilitiesInfo.enforceValidMask(duplexModes, -$$Lambda$PrinterCapabilitiesInfo$Builder$gsgXbNHGWpWENdPzemgHcCY8HnE.INSTANCE);
            PrintAttributes.enforceValidDuplexMode(defaultDuplexMode);
            this.mPrototype.mDuplexModes = duplexModes;
            this.mPrototype.mDefaults[3] = defaultDuplexMode;
            return this;
        }

        public PrinterCapabilitiesInfo build() {
            if (this.mPrototype.mMediaSizes == null || this.mPrototype.mMediaSizes.isEmpty()) {
                throw new IllegalStateException("No media size specified.");
            } else if (this.mPrototype.mDefaults[0] == -1) {
                throw new IllegalStateException("No default media size specified.");
            } else if (this.mPrototype.mResolutions == null || this.mPrototype.mResolutions.isEmpty()) {
                throw new IllegalStateException("No resolution specified.");
            } else if (this.mPrototype.mDefaults[1] == -1) {
                throw new IllegalStateException("No default resolution specified.");
            } else if (this.mPrototype.mColorModes == 0) {
                throw new IllegalStateException("No color mode specified.");
            } else if (this.mPrototype.mDefaults[2] != -1) {
                if (this.mPrototype.mDuplexModes == 0) {
                    setDuplexModes(1, 1);
                }
                if (this.mPrototype.mMinMargins != null) {
                    return this.mPrototype;
                }
                throw new IllegalArgumentException("margins cannot be null");
            } else {
                throw new IllegalStateException("No default color mode specified.");
            }
        }

        private void throwIfDefaultAlreadySpecified(int propertyIndex) {
            if (this.mPrototype.mDefaults[propertyIndex] != -1) {
                throw new IllegalArgumentException("Default already specified.");
            }
        }
    }

    /* synthetic */ PrinterCapabilitiesInfo(Parcel x0, AnonymousClass1 x1) {
        this(x0);
    }

    public PrinterCapabilitiesInfo() {
        this.mMinMargins = DEFAULT_MARGINS;
        this.mDefaults = new int[4];
        Arrays.fill(this.mDefaults, -1);
    }

    public PrinterCapabilitiesInfo(PrinterCapabilitiesInfo prototype) {
        this.mMinMargins = DEFAULT_MARGINS;
        this.mDefaults = new int[4];
        copyFrom(prototype);
    }

    public void copyFrom(PrinterCapabilitiesInfo other) {
        if (this != other) {
            this.mMinMargins = other.mMinMargins;
            List list = other.mMediaSizes;
            if (list != null) {
                List list2 = this.mMediaSizes;
                if (list2 != null) {
                    list2.clear();
                    this.mMediaSizes.addAll(other.mMediaSizes);
                } else {
                    this.mMediaSizes = new ArrayList(list);
                }
            } else {
                this.mMediaSizes = null;
            }
            list = other.mResolutions;
            if (list != null) {
                List list3 = this.mResolutions;
                if (list3 != null) {
                    list3.clear();
                    this.mResolutions.addAll(other.mResolutions);
                } else {
                    this.mResolutions = new ArrayList(list);
                }
            } else {
                this.mResolutions = null;
            }
            this.mColorModes = other.mColorModes;
            this.mDuplexModes = other.mDuplexModes;
            int defaultCount = other.mDefaults.length;
            for (int i = 0; i < defaultCount; i++) {
                this.mDefaults[i] = other.mDefaults[i];
            }
        }
    }

    public List<MediaSize> getMediaSizes() {
        return Collections.unmodifiableList(this.mMediaSizes);
    }

    public List<Resolution> getResolutions() {
        return Collections.unmodifiableList(this.mResolutions);
    }

    public Margins getMinMargins() {
        return this.mMinMargins;
    }

    public int getColorModes() {
        return this.mColorModes;
    }

    public int getDuplexModes() {
        return this.mDuplexModes;
    }

    public PrintAttributes getDefaults() {
        android.print.PrintAttributes.Builder builder = new android.print.PrintAttributes.Builder();
        builder.setMinMargins(this.mMinMargins);
        int mediaSizeIndex = this.mDefaults[0];
        if (mediaSizeIndex >= 0) {
            builder.setMediaSize((MediaSize) this.mMediaSizes.get(mediaSizeIndex));
        }
        int resolutionIndex = this.mDefaults[1];
        if (resolutionIndex >= 0) {
            builder.setResolution((Resolution) this.mResolutions.get(resolutionIndex));
        }
        int colorMode = this.mDefaults[2];
        if (colorMode > 0) {
            builder.setColorMode(colorMode);
        }
        int duplexMode = this.mDefaults[3];
        if (duplexMode > 0) {
            builder.setDuplexMode(duplexMode);
        }
        return builder.build();
    }

    private static void enforceValidMask(int mask, IntConsumer enforceSingle) {
        int current = mask;
        while (current > 0) {
            int currentMode = 1 << Integer.numberOfTrailingZeros(current);
            current &= ~currentMode;
            enforceSingle.accept(currentMode);
        }
    }

    private PrinterCapabilitiesInfo(Parcel parcel) {
        this.mMinMargins = DEFAULT_MARGINS;
        this.mDefaults = new int[4];
        this.mMinMargins = (Margins) Preconditions.checkNotNull(readMargins(parcel));
        readMediaSizes(parcel);
        readResolutions(parcel);
        this.mColorModes = parcel.readInt();
        enforceValidMask(this.mColorModes, -$$Lambda$PrinterCapabilitiesInfo$2mJhwjGC7Dgi0vwDsnG83V2s6sE.INSTANCE);
        this.mDuplexModes = parcel.readInt();
        enforceValidMask(this.mDuplexModes, -$$Lambda$PrinterCapabilitiesInfo$TL1SYHyXTbqj2Nseol9bDJQOn3U.INSTANCE);
        readDefaults(parcel);
        boolean z = false;
        Preconditions.checkArgument(this.mMediaSizes.size() > this.mDefaults[0]);
        if (this.mResolutions.size() > this.mDefaults[1]) {
            z = true;
        }
        Preconditions.checkArgument(z);
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int flags) {
        writeMargins(this.mMinMargins, parcel);
        writeMediaSizes(parcel);
        writeResolutions(parcel);
        parcel.writeInt(this.mColorModes);
        parcel.writeInt(this.mDuplexModes);
        writeDefaults(parcel);
    }

    public int hashCode() {
        int result = 1 * 31;
        Margins margins = this.mMinMargins;
        int i = 0;
        int hashCode = (result + (margins == null ? 0 : margins.hashCode())) * 31;
        List list = this.mMediaSizes;
        result = (hashCode + (list == null ? 0 : list.hashCode())) * 31;
        list = this.mResolutions;
        if (list != null) {
            i = list.hashCode();
        }
        return ((((((result + i) * 31) + this.mColorModes) * 31) + this.mDuplexModes) * 31) + Arrays.hashCode(this.mDefaults);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrinterCapabilitiesInfo other = (PrinterCapabilitiesInfo) obj;
        Margins margins = this.mMinMargins;
        if (margins == null) {
            if (other.mMinMargins != null) {
                return false;
            }
        } else if (!margins.equals(other.mMinMargins)) {
            return false;
        }
        List list = this.mMediaSizes;
        if (list == null) {
            if (other.mMediaSizes != null) {
                return false;
            }
        } else if (!list.equals(other.mMediaSizes)) {
            return false;
        }
        list = this.mResolutions;
        if (list == null) {
            if (other.mResolutions != null) {
                return false;
            }
        } else if (!list.equals(other.mResolutions)) {
            return false;
        }
        if (this.mColorModes == other.mColorModes && this.mDuplexModes == other.mDuplexModes && Arrays.equals(this.mDefaults, other.mDefaults)) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrinterInfo{");
        builder.append("minMargins=");
        builder.append(this.mMinMargins);
        builder.append(", mediaSizes=");
        builder.append(this.mMediaSizes);
        builder.append(", resolutions=");
        builder.append(this.mResolutions);
        builder.append(", colorModes=");
        builder.append(colorModesToString());
        builder.append(", duplexModes=");
        builder.append(duplexModesToString());
        builder.append("\"}");
        return builder.toString();
    }

    private String colorModesToString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int colorModes = this.mColorModes;
        while (colorModes != 0) {
            int colorMode = 1 << Integer.numberOfTrailingZeros(colorModes);
            colorModes &= ~colorMode;
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(PrintAttributes.colorModeToString(colorMode));
        }
        builder.append(']');
        return builder.toString();
    }

    private String duplexModesToString() {
        StringBuilder builder = new StringBuilder();
        builder.append('[');
        int duplexModes = this.mDuplexModes;
        while (duplexModes != 0) {
            int duplexMode = 1 << Integer.numberOfTrailingZeros(duplexModes);
            duplexModes &= ~duplexMode;
            if (builder.length() > 1) {
                builder.append(", ");
            }
            builder.append(PrintAttributes.duplexModeToString(duplexMode));
        }
        builder.append(']');
        return builder.toString();
    }

    private void writeMediaSizes(Parcel parcel) {
        int mediaSizeCount = this.mMediaSizes;
        if (mediaSizeCount == 0) {
            parcel.writeInt(0);
            return;
        }
        mediaSizeCount = mediaSizeCount.size();
        parcel.writeInt(mediaSizeCount);
        for (int i = 0; i < mediaSizeCount; i++) {
            ((MediaSize) this.mMediaSizes.get(i)).writeToParcel(parcel);
        }
    }

    private void readMediaSizes(Parcel parcel) {
        int mediaSizeCount = parcel.readInt();
        if (mediaSizeCount > 0 && this.mMediaSizes == null) {
            this.mMediaSizes = new ArrayList();
        }
        for (int i = 0; i < mediaSizeCount; i++) {
            this.mMediaSizes.add(MediaSize.createFromParcel(parcel));
        }
    }

    private void writeResolutions(Parcel parcel) {
        int resolutionCount = this.mResolutions;
        if (resolutionCount == 0) {
            parcel.writeInt(0);
            return;
        }
        resolutionCount = resolutionCount.size();
        parcel.writeInt(resolutionCount);
        for (int i = 0; i < resolutionCount; i++) {
            ((Resolution) this.mResolutions.get(i)).writeToParcel(parcel);
        }
    }

    private void readResolutions(Parcel parcel) {
        int resolutionCount = parcel.readInt();
        if (resolutionCount > 0 && this.mResolutions == null) {
            this.mResolutions = new ArrayList();
        }
        for (int i = 0; i < resolutionCount; i++) {
            this.mResolutions.add(Resolution.createFromParcel(parcel));
        }
    }

    private void writeMargins(Margins margins, Parcel parcel) {
        if (margins == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(1);
        margins.writeToParcel(parcel);
    }

    private Margins readMargins(Parcel parcel) {
        return parcel.readInt() == 1 ? Margins.createFromParcel(parcel) : null;
    }

    private void readDefaults(Parcel parcel) {
        int defaultCount = parcel.readInt();
        for (int i = 0; i < defaultCount; i++) {
            this.mDefaults[i] = parcel.readInt();
        }
    }

    private void writeDefaults(Parcel parcel) {
        parcel.writeInt(defaultCount);
        for (int writeInt : this.mDefaults) {
            parcel.writeInt(writeInt);
        }
    }
}
