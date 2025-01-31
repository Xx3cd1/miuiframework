package android.media.effect.effects;

import android.app.slice.Slice;
import android.filterfw.core.Filter;
import android.filterfw.core.OneShotScheduler;
import android.filterpacks.videoproc.BackDropperFilter;
import android.filterpacks.videoproc.BackDropperFilter.LearningDoneListener;
import android.media.effect.EffectContext;
import android.media.effect.EffectUpdateListener;
import android.media.effect.FilterGraphEffect;
import android.provider.MediaStore;

public class BackDropperEffect extends FilterGraphEffect {
    private static final String mGraphDefinition = "@import android.filterpacks.base;\n@import android.filterpacks.videoproc;\n@import android.filterpacks.videosrc;\n\n@filter GLTextureSource foreground {\n  texId = 0;\n  width = 0;\n  height = 0;\n  repeatFrame = true;\n}\n\n@filter MediaSource background {\n  sourceUrl = \"no_file_specified\";\n  waitForNewFrame = false;\n  sourceIsUrl = true;\n}\n\n@filter BackDropperFilter replacer {\n  autowbToggle = 1;\n}\n\n@filter GLTextureTarget output {\n  texId = 0;\n}\n\n@connect foreground[frame]  => replacer[video];\n@connect background[video]  => replacer[background];\n@connect replacer[video]    => output[frame];\n";
    private EffectUpdateListener mEffectListener = null;
    private LearningDoneListener mLearningListener = new LearningDoneListener() {
        public void onLearningDone(BackDropperFilter filter) {
            if (BackDropperEffect.this.mEffectListener != null) {
                BackDropperEffect.this.mEffectListener.onEffectUpdated(BackDropperEffect.this, null);
            }
        }
    };

    public BackDropperEffect(EffectContext context, String name) {
        EffectContext effectContext = context;
        String str = name;
        super(effectContext, str, mGraphDefinition, "foreground", MediaStore.EXTRA_OUTPUT, OneShotScheduler.class);
        this.mGraph.getFilter("replacer").setInputValue("learningDoneListener", this.mLearningListener);
    }

    public void setParameter(String parameterKey, Object value) {
        Filter background = "background";
        if (parameterKey.equals(Slice.SUBTYPE_SOURCE)) {
            this.mGraph.getFilter(background).setInputValue("sourceUrl", value);
            return;
        }
        String str = "context";
        if (parameterKey.equals(str)) {
            this.mGraph.getFilter(background).setInputValue(str, value);
        }
    }

    public void setUpdateListener(EffectUpdateListener listener) {
        this.mEffectListener = listener;
    }
}
