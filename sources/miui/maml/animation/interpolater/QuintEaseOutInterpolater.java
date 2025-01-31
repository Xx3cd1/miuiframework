package miui.maml.animation.interpolater;

import android.view.animation.Interpolator;

public class QuintEaseOutInterpolater implements Interpolator {
    public float getInterpolation(float t) {
        float f = t - 1.0f;
        t = f;
        return ((((f * t) * t) * t) * t) + 1.0f;
    }
}
