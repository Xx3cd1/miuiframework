package android.filterpacks.videoproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import java.util.Arrays;

public class BackDropperFilter extends Filter {
    private static final float DEFAULT_ACCEPT_STDDEV = 0.85f;
    private static final float DEFAULT_ADAPT_RATE_BG = 0.0f;
    private static final float DEFAULT_ADAPT_RATE_FG = 0.0f;
    private static final String DEFAULT_AUTO_WB_SCALE = "0.25";
    private static final float[] DEFAULT_BG_FIT_TRANSFORM = new float[]{1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final float DEFAULT_EXPOSURE_CHANGE = 1.0f;
    private static final int DEFAULT_HIER_LRG_EXPONENT = 3;
    private static final float DEFAULT_HIER_LRG_SCALE = 0.7f;
    private static final int DEFAULT_HIER_MID_EXPONENT = 2;
    private static final float DEFAULT_HIER_MID_SCALE = 0.6f;
    private static final int DEFAULT_HIER_SML_EXPONENT = 0;
    private static final float DEFAULT_HIER_SML_SCALE = 0.5f;
    private static final float DEFAULT_LEARNING_ADAPT_RATE = 0.2f;
    private static final int DEFAULT_LEARNING_DONE_THRESHOLD = 20;
    private static final int DEFAULT_LEARNING_DURATION = 40;
    private static final int DEFAULT_LEARNING_VERIFY_DURATION = 10;
    private static final float DEFAULT_MASK_BLEND_BG = 0.65f;
    private static final float DEFAULT_MASK_BLEND_FG = 0.95f;
    private static final int DEFAULT_MASK_HEIGHT_EXPONENT = 8;
    private static final float DEFAULT_MASK_VERIFY_RATE = 0.25f;
    private static final int DEFAULT_MASK_WIDTH_EXPONENT = 8;
    private static final float DEFAULT_UV_SCALE_FACTOR = 1.35f;
    private static final float DEFAULT_WHITE_BALANCE_BLUE_CHANGE = 0.0f;
    private static final float DEFAULT_WHITE_BALANCE_RED_CHANGE = 0.0f;
    private static final int DEFAULT_WHITE_BALANCE_TOGGLE = 0;
    private static final float DEFAULT_Y_SCALE_FACTOR = 0.4f;
    private static final String DISTANCE_STORAGE_SCALE = "0.6";
    private static final String MASK_SMOOTH_EXPONENT = "2.0";
    private static final String MIN_VARIANCE = "3.0";
    private static final String RGB_TO_YUV_MATRIX = "0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 ";
    private static final String TAG = "BackDropperFilter";
    private static final String VARIANCE_STORAGE_SCALE = "5.0";
    private static final String mAutomaticWhiteBalance = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float pyramid_depth;\nuniform bool autowb_toggle;\nvarying vec2 v_texcoord;\nvoid main() {\n   vec4 mean_video = texture2D(tex_sampler_0, v_texcoord, pyramid_depth);\n   vec4 mean_bg = texture2D(tex_sampler_1, v_texcoord, pyramid_depth);\n   float green_normalizer = mean_video.g / mean_bg.g;\n   vec4 adjusted_value = vec4(mean_bg.r / mean_video.r * green_normalizer, 1., \n                         mean_bg.b / mean_video.b * green_normalizer, 1.) * auto_wb_scale; \n   gl_FragColor = autowb_toggle ? adjusted_value : vec4(auto_wb_scale);\n}\n";
    private static final String mBgDistanceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n\n  float dist_y = gauss_dist_y(fg.r, mean.r, variance.r);\n  float dist_uv = gauss_dist_uv(fg.gb, mean.gb, variance.gb);\n  gl_FragColor = vec4(0.5*fg.rg, dist_scale*dist_y, dist_scale*dist_uv);\n}\n";
    private static final String mBgMaskShader = "uniform sampler2D tex_sampler_0;\nuniform float accept_variance;\nuniform vec2 yuv_weights;\nuniform float scale_lrg;\nuniform float scale_mid;\nuniform float scale_sml;\nuniform float exp_lrg;\nuniform float exp_mid;\nuniform float exp_sml;\nvarying vec2 v_texcoord;\nbool is_fg(vec2 dist_yc, float accept_variance) {\n  return ( dot(yuv_weights, dist_yc) >= accept_variance );\n}\nvoid main() {\n  vec4 dist_lrg_sc = texture2D(tex_sampler_0, v_texcoord, exp_lrg);\n  vec4 dist_mid_sc = texture2D(tex_sampler_0, v_texcoord, exp_mid);\n  vec4 dist_sml_sc = texture2D(tex_sampler_0, v_texcoord, exp_sml);\n  vec2 dist_lrg = inv_dist_scale * dist_lrg_sc.ba;\n  vec2 dist_mid = inv_dist_scale * dist_mid_sc.ba;\n  vec2 dist_sml = inv_dist_scale * dist_sml_sc.ba;\n  vec2 norm_dist = 0.75 * dist_sml / accept_variance;\n  bool is_fg_lrg = is_fg(dist_lrg, accept_variance * scale_lrg);\n  bool is_fg_mid = is_fg_lrg || is_fg(dist_mid, accept_variance * scale_mid);\n  float is_fg_sml =\n      float(is_fg_mid || is_fg(dist_sml, accept_variance * scale_sml));\n  float alpha = 0.5 * is_fg_sml + 0.3 * float(is_fg_mid) + 0.2 * float(is_fg_lrg);\n  gl_FragColor = vec4(alpha, norm_dist, is_fg_sml);\n}\n";
    private static final String mBgSubtractForceShader = "  vec4 ghost_rgb = (fg_adjusted * 0.7 + vec4(0.3,0.3,0.4,0.))*0.65 + \n                   0.35*bg_rgb;\n  float glow_start = 0.75 * mask_blend_bg; \n  float glow_max   = mask_blend_bg; \n  gl_FragColor = mask.a < glow_start ? bg_rgb : \n                 mask.a < glow_max ? mix(bg_rgb, vec4(0.9,0.9,1.0,1.0), \n                                     (mask.a - glow_start) / (glow_max - glow_start) ) : \n                 mask.a < mask_blend_fg ? mix(vec4(0.9,0.9,1.0,1.0), ghost_rgb, \n                                    (mask.a - glow_max) / (mask_blend_fg - glow_max) ) : \n                 ghost_rgb;\n}\n";
    private static final String mBgSubtractShader = "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n";
    private static final String[] mDebugOutputNames = new String[]{"debug1", "debug2"};
    private static final String[] mInputNames;
    private static final String mMaskVerifyShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float verify_rate;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 lastmask = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  float newmask = mix(lastmask.a, mask.a, verify_rate);\n  gl_FragColor = vec4(0., 0., 0., newmask);\n}\n";
    private static final String[] mOutputNames;
    private static String mSharedUtilShader = "precision mediump float;\nuniform float fg_adapt_rate;\nuniform float bg_adapt_rate;\nconst mat4 coeff_yuv = mat4(0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 );\nconst float dist_scale = 0.6;\nconst float inv_dist_scale = 1. / dist_scale;\nconst float var_scale=5.0;\nconst float inv_var_scale = 1. / var_scale;\nconst float min_variance = inv_var_scale *3.0/ 256.;\nconst float auto_wb_scale = 0.25;\n\nfloat gauss_dist_y(float y, float mean, float variance) {\n  float dist = (y - mean) * (y - mean) / variance;\n  return dist;\n}\nfloat gauss_dist_uv(vec2 uv, vec2 mean, vec2 variance) {\n  vec2 dist = (uv - mean) * (uv - mean) / variance;\n  return dist.r + dist.g;\n}\nfloat local_adapt_rate(float alpha) {\n  return mix(bg_adapt_rate, fg_adapt_rate, alpha);\n}\n\n";
    private static final String mUpdateBgModelMeanShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 new_mean = mix(mean, fg, alpha);\n  gl_FragColor = new_mean;\n}\n";
    private static final String mUpdateBgModelVarianceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_3, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 cur_variance = (fg-mean)*(fg-mean);\n  vec4 new_variance = mix(variance, cur_variance, alpha);\n  new_variance = max(new_variance, vec4(min_variance));\n  gl_FragColor = var_scale * new_variance;\n}\n";
    private final int BACKGROUND_FILL_CROP = 2;
    private final int BACKGROUND_FIT = 1;
    private final int BACKGROUND_STRETCH = 0;
    private ShaderProgram copyShaderProgram;
    private boolean isOpen;
    @GenerateFieldPort(hasDefault = true, name = "acceptStddev")
    private float mAcceptStddev = DEFAULT_ACCEPT_STDDEV;
    @GenerateFieldPort(hasDefault = true, name = "adaptRateBg")
    private float mAdaptRateBg = 0.0f;
    @GenerateFieldPort(hasDefault = true, name = "adaptRateFg")
    private float mAdaptRateFg = 0.0f;
    @GenerateFieldPort(hasDefault = true, name = "learningAdaptRate")
    private float mAdaptRateLearning = 0.2f;
    private GLFrame mAutoWB;
    @GenerateFieldPort(hasDefault = true, name = "autowbToggle")
    private int mAutoWBToggle = 0;
    private ShaderProgram mAutomaticWhiteBalanceProgram;
    private MutableFrameFormat mAverageFormat;
    @GenerateFieldPort(hasDefault = true, name = "backgroundFitMode")
    private int mBackgroundFitMode = 2;
    private boolean mBackgroundFitModeChanged;
    private ShaderProgram mBgDistProgram;
    private GLFrame mBgInput;
    private ShaderProgram mBgMaskProgram;
    private GLFrame[] mBgMean;
    private ShaderProgram mBgSubtractProgram;
    private ShaderProgram mBgUpdateMeanProgram;
    private ShaderProgram mBgUpdateVarianceProgram;
    private GLFrame[] mBgVariance;
    @GenerateFieldPort(hasDefault = true, name = "chromaScale")
    private float mChromaScale = DEFAULT_UV_SCALE_FACTOR;
    private ShaderProgram mCopyOutProgram;
    private GLFrame mDistance;
    @GenerateFieldPort(hasDefault = true, name = "exposureChange")
    private float mExposureChange = 1.0f;
    private int mFrameCount;
    @GenerateFieldPort(hasDefault = true, name = "hierLrgExp")
    private int mHierarchyLrgExp = 3;
    @GenerateFieldPort(hasDefault = true, name = "hierLrgScale")
    private float mHierarchyLrgScale = DEFAULT_HIER_LRG_SCALE;
    @GenerateFieldPort(hasDefault = true, name = "hierMidExp")
    private int mHierarchyMidExp = 2;
    @GenerateFieldPort(hasDefault = true, name = "hierMidScale")
    private float mHierarchyMidScale = 0.6f;
    @GenerateFieldPort(hasDefault = true, name = "hierSmlExp")
    private int mHierarchySmlExp = 0;
    @GenerateFieldPort(hasDefault = true, name = "hierSmlScale")
    private float mHierarchySmlScale = 0.5f;
    @GenerateFieldPort(hasDefault = true, name = "learningDoneListener")
    private LearningDoneListener mLearningDoneListener = null;
    @GenerateFieldPort(hasDefault = true, name = "learningDuration")
    private int mLearningDuration = 40;
    @GenerateFieldPort(hasDefault = true, name = "learningVerifyDuration")
    private int mLearningVerifyDuration = 10;
    private final boolean mLogVerbose;
    @GenerateFieldPort(hasDefault = true, name = "lumScale")
    private float mLumScale = 0.4f;
    private GLFrame mMask;
    private GLFrame mMaskAverage;
    @GenerateFieldPort(hasDefault = true, name = "maskBg")
    private float mMaskBg = DEFAULT_MASK_BLEND_BG;
    @GenerateFieldPort(hasDefault = true, name = "maskFg")
    private float mMaskFg = DEFAULT_MASK_BLEND_FG;
    private MutableFrameFormat mMaskFormat;
    @GenerateFieldPort(hasDefault = true, name = "maskHeightExp")
    private int mMaskHeightExp = 8;
    private GLFrame[] mMaskVerify;
    private ShaderProgram mMaskVerifyProgram;
    @GenerateFieldPort(hasDefault = true, name = "maskWidthExp")
    private int mMaskWidthExp = 8;
    private MutableFrameFormat mMemoryFormat;
    @GenerateFieldPort(hasDefault = true, name = "mirrorBg")
    private boolean mMirrorBg = false;
    @GenerateFieldPort(hasDefault = true, name = "orientation")
    private int mOrientation = 0;
    private FrameFormat mOutputFormat;
    private boolean mPingPong;
    @GenerateFinalPort(hasDefault = true, name = "provideDebugOutputs")
    private boolean mProvideDebugOutputs = false;
    private int mPyramidDepth;
    private float mRelativeAspect;
    private boolean mStartLearning;
    private int mSubsampleLevel;
    @GenerateFieldPort(hasDefault = true, name = "useTheForce")
    private boolean mUseTheForce = false;
    @GenerateFieldPort(hasDefault = true, name = "maskVerifyRate")
    private float mVerifyRate = 0.25f;
    private GLFrame mVideoInput;
    @GenerateFieldPort(hasDefault = true, name = "whitebalanceblueChange")
    private float mWhiteBalanceBlueChange = 0.0f;
    @GenerateFieldPort(hasDefault = true, name = "whitebalanceredChange")
    private float mWhiteBalanceRedChange = 0.0f;
    private long startTime = -1;

    public interface LearningDoneListener {
        void onLearningDone(BackDropperFilter backDropperFilter);
    }

    static {
        String str = "video";
        mInputNames = new String[]{str, "background"};
        mOutputNames = new String[]{str};
    }

    public BackDropperFilter(String name) {
        super(name);
        String str = TAG;
        this.mLogVerbose = Log.isLoggable(str, 2);
        String adjStr = SystemProperties.get("ro.media.effect.bgdropper.adj");
        if (adjStr.length() > 0) {
            try {
                this.mAcceptStddev += Float.parseFloat(adjStr);
                if (this.mLogVerbose) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Adjusting accept threshold by ");
                    stringBuilder.append(adjStr);
                    stringBuilder.append(", now ");
                    stringBuilder.append(this.mAcceptStddev);
                    Log.v(str, stringBuilder.toString());
                }
            } catch (NumberFormatException e) {
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("Badly formatted property ro.media.effect.bgdropper.adj: ");
                stringBuilder2.append(adjStr);
                Log.e(str, stringBuilder2.toString());
            }
        }
    }

    public void setupPorts() {
        String inputName;
        int i = 0;
        FrameFormat imageFormat = ImageFormat.create(3, 0);
        for (String inputName2 : mInputNames) {
            addMaskedInputPort(inputName2, imageFormat);
        }
        String[] strArr = mOutputNames;
        int length = strArr.length;
        int i2 = 0;
        while (true) {
            inputName2 = "video";
            if (i2 >= length) {
                break;
            }
            addOutputBasedOnInput(strArr[i2], inputName2);
            i2++;
        }
        if (this.mProvideDebugOutputs) {
            strArr = mDebugOutputNames;
            length = strArr.length;
            while (i < length) {
                addOutputBasedOnInput(strArr[i], inputName2);
                i++;
            }
        }
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat format = inputFormat.mutableCopy();
        if (!Arrays.asList(mOutputNames).contains(portName)) {
            format.setDimensions(0, 0);
        }
        return format;
    }

    private boolean createMemoryFormat(FrameFormat inputFormat) {
        if (this.mMemoryFormat != null) {
            return false;
        }
        if (inputFormat.getWidth() == 0 || inputFormat.getHeight() == 0) {
            throw new RuntimeException("Attempting to process input frame with unknown size");
        }
        this.mMaskFormat = inputFormat.mutableCopy();
        int maskWidth = (int) Math.pow(2.0d, (double) this.mMaskWidthExp);
        int maskHeight = (int) Math.pow(2.0d, (double) this.mMaskHeightExp);
        this.mMaskFormat.setDimensions(maskWidth, maskHeight);
        this.mPyramidDepth = Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        this.mMemoryFormat = this.mMaskFormat.mutableCopy();
        int widthExp = Math.max(this.mMaskWidthExp, pyramidLevel(inputFormat.getWidth()));
        int heightExp = Math.max(this.mMaskHeightExp, pyramidLevel(inputFormat.getHeight()));
        this.mPyramidDepth = Math.max(widthExp, heightExp);
        int memWidth = Math.max(maskWidth, (int) Math.pow(2.0d, (double) widthExp));
        int memHeight = Math.max(maskHeight, (int) Math.pow(2.0d, (double) heightExp));
        this.mMemoryFormat.setDimensions(memWidth, memHeight);
        this.mSubsampleLevel = this.mPyramidDepth - Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        if (this.mLogVerbose) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Mask frames size ");
            stringBuilder.append(maskWidth);
            String str = " x ";
            stringBuilder.append(str);
            stringBuilder.append(maskHeight);
            String stringBuilder2 = stringBuilder.toString();
            String str2 = TAG;
            Log.v(str2, stringBuilder2);
            stringBuilder = new StringBuilder();
            stringBuilder.append("Pyramid levels ");
            stringBuilder.append(widthExp);
            stringBuilder.append(str);
            stringBuilder.append(heightExp);
            Log.v(str2, stringBuilder.toString());
            stringBuilder = new StringBuilder();
            stringBuilder.append("Memory frames size ");
            stringBuilder.append(memWidth);
            stringBuilder.append(str);
            stringBuilder.append(memHeight);
            Log.v(str2, stringBuilder.toString());
        }
        this.mAverageFormat = inputFormat.mutableCopy();
        this.mAverageFormat.setDimensions(1, 1);
        return true;
    }

    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Preparing BackDropperFilter!");
        }
        this.mBgMean = new GLFrame[2];
        this.mBgVariance = new GLFrame[2];
        this.mMaskVerify = new GLFrame[2];
        this.copyShaderProgram = ShaderProgram.createIdentity(context);
    }

    private void allocateFrames(FrameFormat inputFormat, FilterContext context) {
        if (createMemoryFormat(inputFormat)) {
            int i;
            boolean z = this.mLogVerbose;
            String str = TAG;
            if (z) {
                Log.v(str, "Allocating BackDropperFilter frames");
            }
            int numBytes = this.mMaskFormat.getSize();
            byte[] initialBgMean = new byte[numBytes];
            byte[] initialBgVariance = new byte[numBytes];
            byte[] initialMaskVerify = new byte[numBytes];
            for (i = 0; i < numBytes; i++) {
                initialBgMean[i] = Byte.MIN_VALUE;
                initialBgVariance[i] = (byte) 10;
                initialMaskVerify[i] = (byte) 0;
            }
            for (i = 0; i < 2; i++) {
                this.mBgMean[i] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
                this.mBgMean[i].setData(initialBgMean, 0, numBytes);
                this.mBgVariance[i] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
                this.mBgVariance[i].setData(initialBgVariance, 0, numBytes);
                this.mMaskVerify[i] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
                this.mMaskVerify[i].setData(initialMaskVerify, 0, numBytes);
            }
            if (this.mLogVerbose) {
                Log.v(str, "Done allocating texture for Mean and Variance objects!");
            }
            this.mDistance = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mMask = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mAutoWB = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
            this.mVideoInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
            this.mBgInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
            this.mMaskAverage = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(mSharedUtilShader);
            stringBuilder.append(mBgDistanceShader);
            this.mBgDistProgram = new ShaderProgram(context, stringBuilder.toString());
            String str2 = "subsample_level";
            this.mBgDistProgram.setHostValue(str2, Float.valueOf((float) this.mSubsampleLevel));
            stringBuilder = new StringBuilder();
            stringBuilder.append(mSharedUtilShader);
            stringBuilder.append(mBgMaskShader);
            this.mBgMaskProgram = new ShaderProgram(context, stringBuilder.toString());
            ShaderProgram shaderProgram = this.mBgMaskProgram;
            float f = this.mAcceptStddev;
            shaderProgram.setHostValue("accept_variance", Float.valueOf(f * f));
            this.mBgMaskProgram.setHostValue("yuv_weights", new float[]{this.mLumScale, this.mChromaScale});
            this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
            this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
            this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
            this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchyLrgExp)));
            this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchyMidExp)));
            this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchySmlExp)));
            boolean z2 = this.mUseTheForce;
            String str3 = mBgSubtractShader;
            StringBuilder stringBuilder2;
            if (z2) {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(mSharedUtilShader);
                stringBuilder2.append(str3);
                stringBuilder2.append(mBgSubtractForceShader);
                this.mBgSubtractProgram = new ShaderProgram(context, stringBuilder2.toString());
            } else {
                stringBuilder2 = new StringBuilder();
                stringBuilder2.append(mSharedUtilShader);
                stringBuilder2.append(str3);
                stringBuilder2.append("}\n");
                this.mBgSubtractProgram = new ShaderProgram(context, stringBuilder2.toString());
            }
            this.mBgSubtractProgram.setHostValue("bg_fit_transform", DEFAULT_BG_FIT_TRANSFORM);
            this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
            this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
            this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
            this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
            this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append(mSharedUtilShader);
            stringBuilder3.append(mUpdateBgModelMeanShader);
            this.mBgUpdateMeanProgram = new ShaderProgram(context, stringBuilder3.toString());
            this.mBgUpdateMeanProgram.setHostValue(str2, Float.valueOf((float) this.mSubsampleLevel));
            stringBuilder3 = new StringBuilder();
            stringBuilder3.append(mSharedUtilShader);
            stringBuilder3.append(mUpdateBgModelVarianceShader);
            this.mBgUpdateVarianceProgram = new ShaderProgram(context, stringBuilder3.toString());
            this.mBgUpdateVarianceProgram.setHostValue(str2, Float.valueOf((float) this.mSubsampleLevel));
            this.mCopyOutProgram = ShaderProgram.createIdentity(context);
            StringBuilder stringBuilder4 = new StringBuilder();
            stringBuilder4.append(mSharedUtilShader);
            stringBuilder4.append(mAutomaticWhiteBalance);
            this.mAutomaticWhiteBalanceProgram = new ShaderProgram(context, stringBuilder4.toString());
            this.mAutomaticWhiteBalanceProgram.setHostValue("pyramid_depth", Float.valueOf((float) this.mPyramidDepth));
            this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
            stringBuilder4 = new StringBuilder();
            stringBuilder4.append(mSharedUtilShader);
            stringBuilder4.append(mMaskVerifyShader);
            this.mMaskVerifyProgram = new ShaderProgram(context, stringBuilder4.toString());
            this.mMaskVerifyProgram.setHostValue("verify_rate", Float.valueOf(this.mVerifyRate));
            if (this.mLogVerbose) {
                StringBuilder stringBuilder5 = new StringBuilder();
                stringBuilder5.append("Shader width set to ");
                stringBuilder5.append(this.mMemoryFormat.getWidth());
                Log.v(str, stringBuilder5.toString());
            }
            this.mRelativeAspect = 1.0f;
            this.mFrameCount = 0;
            this.mStartLearning = true;
        }
    }

    public void process(FilterContext context) {
        Frame[] maskVerifyInputs;
        GLFrame[] gLFrameArr;
        Frame output;
        String str = "video";
        Frame video = pullInput(str);
        Frame background = pullInput("background");
        allocateFrames(video.getFormat(), context);
        boolean z = this.mStartLearning;
        String str2 = TAG;
        String str3 = "fg_adapt_rate";
        String str4 = "bg_adapt_rate";
        if (z) {
            if (this.mLogVerbose) {
                Log.v(str2, "Starting learning");
            }
            this.mBgUpdateMeanProgram.setHostValue(str4, Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateMeanProgram.setHostValue(str3, Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue(str4, Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue(str3, Float.valueOf(this.mAdaptRateLearning));
            this.mFrameCount = 0;
        }
        z = this.mPingPong;
        int inputIndex = z ^ 1;
        boolean outputIndex = z;
        this.mPingPong = z ^ 1;
        updateBgScaling(video, background, this.mBackgroundFitModeChanged);
        this.mBackgroundFitModeChanged = false;
        this.copyShaderProgram.process(video, (Frame) this.mVideoInput);
        this.copyShaderProgram.process(background, (Frame) this.mBgInput);
        this.mVideoInput.generateMipMap();
        this.mVideoInput.setTextureParameter(10241, 9985);
        this.mBgInput.generateMipMap();
        this.mBgInput.setTextureParameter(10241, 9985);
        if (this.mStartLearning) {
            this.copyShaderProgram.process((Frame) this.mVideoInput, this.mBgMean[inputIndex]);
            this.mStartLearning = false;
        }
        this.mBgDistProgram.process(new Frame[]{this.mVideoInput, this.mBgMean[inputIndex], this.mBgVariance[inputIndex]}, this.mDistance);
        this.mDistance.generateMipMap();
        this.mDistance.setTextureParameter(10241, 9985);
        this.mBgMaskProgram.process((Frame) this.mDistance, (Frame) this.mMask);
        this.mMask.generateMipMap();
        this.mMask.setTextureParameter(10241, 9985);
        this.mAutomaticWhiteBalanceProgram.process(new Frame[]{this.mVideoInput, this.mBgInput}, this.mAutoWB);
        if (this.mFrameCount <= this.mLearningDuration) {
            pushOutput(str, video);
            int i = this.mFrameCount;
            int i2 = this.mLearningDuration;
            int i3 = this.mLearningVerifyDuration;
            if (i == i2 - i3) {
                this.copyShaderProgram.process((Frame) this.mMask, this.mMaskVerify[outputIndex]);
                this.mBgUpdateMeanProgram.setHostValue(str4, Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateMeanProgram.setHostValue(str3, Float.valueOf(this.mAdaptRateFg));
                this.mBgUpdateVarianceProgram.setHostValue(str4, Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateVarianceProgram.setHostValue(str3, Float.valueOf(this.mAdaptRateFg));
            } else if (i > i2 - i3) {
                maskVerifyInputs = new Frame[2];
                gLFrameArr = this.mMaskVerify;
                maskVerifyInputs[0] = gLFrameArr[inputIndex];
                maskVerifyInputs[1] = this.mMask;
                this.mMaskVerifyProgram.process(maskVerifyInputs, gLFrameArr[outputIndex]);
                this.mMaskVerify[outputIndex].generateMipMap();
                this.mMaskVerify[outputIndex].setTextureParameter(10241, 9985);
            }
            if (this.mFrameCount == this.mLearningDuration) {
                this.copyShaderProgram.process(this.mMaskVerify[outputIndex], (Frame) this.mMaskAverage);
                int bi = this.mMaskAverage.getData().array()[3] & 255;
                if (this.mLogVerbose) {
                    Log.v(str2, String.format("Mask_average is %d, threshold is %d", new Object[]{Integer.valueOf(bi), Integer.valueOf(20)}));
                }
                if (bi >= 20) {
                    this.mStartLearning = true;
                } else {
                    if (this.mLogVerbose) {
                        Log.v(str2, "Learning done");
                    }
                    LearningDoneListener learningDoneListener = this.mLearningDoneListener;
                    if (learningDoneListener != null) {
                        learningDoneListener.onLearningDone(this);
                    }
                }
            }
        } else {
            output = context.getFrameManager().newFrame(video.getFormat());
            this.mBgSubtractProgram.process(new Frame[]{video, background, this.mMask, this.mAutoWB}, output);
            pushOutput(str, output);
            output.release();
        }
        if (this.mFrameCount < this.mLearningDuration - this.mLearningVerifyDuration || ((double) this.mAdaptRateBg) > 0.0d || ((double) this.mAdaptRateFg) > 0.0d) {
            maskVerifyInputs = new Frame[3];
            gLFrameArr = this.mBgMean;
            maskVerifyInputs[1] = gLFrameArr[inputIndex];
            maskVerifyInputs[2] = this.mMask;
            this.mBgUpdateMeanProgram.process(maskVerifyInputs, gLFrameArr[outputIndex]);
            this.mBgMean[outputIndex].generateMipMap();
            this.mBgMean[outputIndex].setTextureParameter(10241, 9985);
            varianceUpdateInputs = new Frame[4];
            GLFrame[] gLFrameArr2 = this.mBgVariance;
            varianceUpdateInputs[2] = gLFrameArr2[inputIndex];
            varianceUpdateInputs[3] = this.mMask;
            this.mBgUpdateVarianceProgram.process(varianceUpdateInputs, gLFrameArr2[outputIndex]);
            this.mBgVariance[outputIndex].generateMipMap();
            this.mBgVariance[outputIndex].setTextureParameter(10241, 9985);
        }
        if (this.mProvideDebugOutputs) {
            Frame dbg1 = context.getFrameManager().newFrame(video.getFormat());
            this.mCopyOutProgram.process(video, dbg1);
            pushOutput("debug1", dbg1);
            dbg1.release();
            output = context.getFrameManager().newFrame(this.mMemoryFormat);
            this.mCopyOutProgram.process((Frame) this.mMask, output);
            pushOutput("debug2", output);
            output.release();
        }
        this.mFrameCount++;
        if (!this.mLogVerbose || this.mFrameCount % 30 != 0) {
            return;
        }
        if (this.startTime == -1) {
            context.getGLEnvironment().activate();
            GLES20.glFinish();
            this.startTime = SystemClock.elapsedRealtime();
            return;
        }
        context.getGLEnvironment().activate();
        GLES20.glFinish();
        long endTime = SystemClock.elapsedRealtime();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Avg. frame duration: ");
        String str5 = "%.2f";
        stringBuilder.append(String.format(str5, new Object[]{Double.valueOf(((double) (endTime - this.startTime)) / 30.0d)}));
        stringBuilder.append(" ms. Avg. fps: ");
        stringBuilder.append(String.format(str5, new Object[]{Double.valueOf(1000.0d / (((double) (endTime - this.startTime)) / 30.0d))}));
        Log.v(str2, stringBuilder.toString());
        this.startTime = endTime;
    }

    public void close(FilterContext context) {
        if (this.mMemoryFormat != null) {
            if (this.mLogVerbose) {
                Log.v(TAG, "Filter Closing!");
            }
            for (int i = 0; i < 2; i++) {
                this.mBgMean[i].release();
                this.mBgVariance[i].release();
                this.mMaskVerify[i].release();
            }
            this.mDistance.release();
            this.mMask.release();
            this.mAutoWB.release();
            this.mVideoInput.release();
            this.mBgInput.release();
            this.mMaskAverage.release();
            this.mMemoryFormat = null;
        }
    }

    public synchronized void relearn() {
        this.mStartLearning = true;
    }

    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("backgroundFitMode")) {
            this.mBackgroundFitModeChanged = true;
        } else if (name.equals("acceptStddev")) {
            ShaderProgram shaderProgram = this.mBgMaskProgram;
            float f = this.mAcceptStddev;
            shaderProgram.setHostValue("accept_variance", Float.valueOf(f * f));
        } else if (name.equals("hierLrgScale")) {
            this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        } else if (name.equals("hierMidScale")) {
            this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        } else if (name.equals("hierSmlScale")) {
            this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        } else if (name.equals("hierLrgExp")) {
            this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchyLrgExp)));
        } else if (name.equals("hierMidExp")) {
            this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchyMidExp)));
        } else if (name.equals("hierSmlExp")) {
            this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf((float) (this.mSubsampleLevel + this.mHierarchySmlExp)));
        } else if (name.equals("lumScale") || name.equals("chromaScale")) {
            this.mBgMaskProgram.setHostValue("yuv_weights", new float[]{this.mLumScale, this.mChromaScale});
        } else if (name.equals("maskBg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        } else if (name.equals("maskFg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        } else if (name.equals("exposureChange")) {
            this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        } else if (name.equals("whitebalanceredChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        } else if (name.equals("whitebalanceblueChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        } else if (name.equals("autowbToggle")) {
            this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
        }
    }

    private void updateBgScaling(Frame video, Frame background, boolean fitModeChanged) {
        float currentRelativeAspect = (((float) video.getFormat().getWidth()) / ((float) video.getFormat().getHeight())) / (((float) background.getFormat().getWidth()) / ((float) background.getFormat().getHeight()));
        if (currentRelativeAspect != this.mRelativeAspect || fitModeChanged) {
            this.mRelativeAspect = currentRelativeAspect;
            float xMin = 0.0f;
            float xWidth = 1.0f;
            float yMin = 0.0f;
            float yWidth = 1.0f;
            int i = this.mBackgroundFitMode;
            if (i != 0) {
                float f;
                if (i == 1) {
                    f = this.mRelativeAspect;
                    if (f > 1.0f) {
                        xMin = 0.5f - (f * 0.5f);
                        xWidth = f * 1.0f;
                    } else {
                        yMin = 0.5f - (0.5f / f);
                        yWidth = 1.0f / f;
                    }
                } else if (i == 2) {
                    f = this.mRelativeAspect;
                    if (f > 1.0f) {
                        yMin = 0.5f - (0.5f / f);
                        yWidth = 1.0f / f;
                    } else {
                        xMin = 0.5f - (f * 0.5f);
                        xWidth = this.mRelativeAspect;
                    }
                }
            }
            boolean z = this.mMirrorBg;
            String str = TAG;
            if (z) {
                if (this.mLogVerbose) {
                    Log.v(str, "Mirroring the background!");
                }
                i = this.mOrientation;
                if (i == 0 || i == 180) {
                    xWidth = -xWidth;
                    xMin = 1.0f - xMin;
                } else {
                    yWidth = -yWidth;
                    yMin = 1.0f - yMin;
                }
            }
            if (this.mLogVerbose) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("bgTransform: xMin, yMin, xWidth, yWidth : ");
                stringBuilder.append(xMin);
                String str2 = ", ";
                stringBuilder.append(str2);
                stringBuilder.append(yMin);
                stringBuilder.append(str2);
                stringBuilder.append(xWidth);
                stringBuilder.append(str2);
                stringBuilder.append(yWidth);
                stringBuilder.append(", mRelAspRatio = ");
                stringBuilder.append(this.mRelativeAspect);
                Log.v(str, stringBuilder.toString());
            }
            this.mBgSubtractProgram.setHostValue("bg_fit_transform", new float[]{xWidth, 0.0f, 0.0f, 0.0f, yWidth, 0.0f, xMin, yMin, 1.0f});
        }
    }

    private int pyramidLevel(int size) {
        return ((int) Math.floor(Math.log10((double) size) / Math.log10(2.0d))) - 1;
    }
}
