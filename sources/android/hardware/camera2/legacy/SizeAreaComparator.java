package android.hardware.camera2.legacy;

import android.hardware.Camera.Size;
import com.android.internal.util.Preconditions;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SizeAreaComparator implements Comparator<Size> {
    public int compare(Size size, Size size2) {
        Preconditions.checkNotNull(size, "size must not be null");
        Preconditions.checkNotNull(size2, "size2 must not be null");
        if (size.equals(size2)) {
            return 0;
        }
        long width = (long) size.width;
        long width2 = (long) size2.width;
        long area = ((long) size.height) * width;
        long area2 = ((long) size2.height) * width2;
        int i = 1;
        if (area == area2) {
            if (width <= width2) {
                i = -1;
            }
            return i;
        }
        if (area <= area2) {
            i = -1;
        }
        return i;
    }

    public static Size findLargestByArea(List<Size> sizes) {
        Preconditions.checkNotNull(sizes, "sizes must not be null");
        return (Size) Collections.max(sizes, new SizeAreaComparator());
    }
}
