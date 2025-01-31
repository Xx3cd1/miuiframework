package android.util;

import com.android.internal.util.Preconditions;

public final class Size {
    private final int mHeight;
    private final int mWidth;

    public Size(int width, int height) {
        this.mWidth = width;
        this.mHeight = height;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Size)) {
            return false;
        }
        Size other = (Size) obj;
        if (this.mWidth == other.mWidth && this.mHeight == other.mHeight) {
            z = true;
        }
        return z;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mWidth);
        stringBuilder.append("x");
        stringBuilder.append(this.mHeight);
        return stringBuilder.toString();
    }

    private static NumberFormatException invalidSize(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid Size: \"");
        stringBuilder.append(s);
        stringBuilder.append("\"");
        throw new NumberFormatException(stringBuilder.toString());
    }

    public static Size parseSize(String string) throws NumberFormatException {
        Preconditions.checkNotNull(string, "string must not be null");
        int sep_ix = string.indexOf(42);
        if (sep_ix < 0) {
            sep_ix = string.indexOf(120);
        }
        if (sep_ix >= 0) {
            try {
                return new Size(Integer.parseInt(string.substring(0, sep_ix)), Integer.parseInt(string.substring(sep_ix + 1)));
            } catch (NumberFormatException e) {
                throw invalidSize(string);
            }
        }
        throw invalidSize(string);
    }

    public int hashCode() {
        int i = this.mHeight;
        int i2 = this.mWidth;
        return i ^ ((i2 >>> 16) | (i2 << 16));
    }
}
