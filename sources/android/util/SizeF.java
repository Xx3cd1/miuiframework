package android.util;

import com.android.internal.util.Preconditions;

public final class SizeF {
    private final float mHeight;
    private final float mWidth;

    public SizeF(float width, float height) {
        this.mWidth = Preconditions.checkArgumentFinite(width, "width");
        this.mHeight = Preconditions.checkArgumentFinite(height, "height");
    }

    public float getWidth() {
        return this.mWidth;
    }

    public float getHeight() {
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
        if (!(obj instanceof SizeF)) {
            return false;
        }
        SizeF other = (SizeF) obj;
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

    private static NumberFormatException invalidSizeF(String s) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid SizeF: \"");
        stringBuilder.append(s);
        stringBuilder.append("\"");
        throw new NumberFormatException(stringBuilder.toString());
    }

    public static SizeF parseSizeF(String string) throws NumberFormatException {
        Preconditions.checkNotNull(string, "string must not be null");
        int sep_ix = string.indexOf(42);
        if (sep_ix < 0) {
            sep_ix = string.indexOf(120);
        }
        if (sep_ix >= 0) {
            try {
                return new SizeF(Float.parseFloat(string.substring(0, sep_ix)), Float.parseFloat(string.substring(sep_ix + 1)));
            } catch (NumberFormatException e) {
                throw invalidSizeF(string);
            } catch (IllegalArgumentException e2) {
                throw invalidSizeF(string);
            }
        }
        throw invalidSizeF(string);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.mWidth) ^ Float.floatToIntBits(this.mHeight);
    }
}
