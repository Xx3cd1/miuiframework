package android.filterpacks.imageproc;

import android.app.slice.SliceItem;
import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.Program;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.os.BatteryManager;
import java.util.Date;
import java.util.Random;

public class DocumentaryFilter extends Filter {
    private final String mDocumentaryShader = "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec2 seed;\nuniform float stepsize;\nuniform float inv_max_dist;\nuniform vec2 scale;\nvarying vec2 v_texcoord;\nfloat rand(vec2 loc) {\n  float theta1 = dot(loc, vec2(0.9898, 0.233));\n  float theta2 = dot(loc, vec2(12.0, 78.0));\n  float value = cos(theta1) * sin(theta2) + sin(theta1) * cos(theta2);\n  float temp = mod(197.0 * value, 1.0) + value;\n  float part1 = mod(220.0 * temp, 1.0) + temp;\n  float part2 = value * 0.5453;\n  float part3 = cos(theta1 + theta2) * 0.43758;\n  return fract(part1 + part2 + part3);\n}\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float dither = rand(v_texcoord + seed);\n  vec3 xform = clamp(2.0 * color.rgb, 0.0, 1.0);\n  vec3 temp = clamp(2.0 * (color.rgb + stepsize), 0.0, 1.0);\n  vec3 new_color = clamp(xform + (temp - xform) * (dither - 0.5), 0.0, 1.0);\n  float gray = dot(new_color, vec3(0.299, 0.587, 0.114));\n  new_color = vec3(gray, gray, gray);\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float lumen = 0.85 / (1.0 + exp((dist * inv_max_dist - 0.83) * 20.0)) + 0.15;\n  gl_FragColor = vec4(new_color * lumen, color.a);\n}\n";
    private int mHeight = 0;
    private Program mProgram;
    private Random mRandom;
    private int mTarget = 0;
    @GenerateFieldPort(hasDefault = true, name = "tile_size")
    private int mTileSize = 640;
    private int mWidth = 0;

    public DocumentaryFilter(String name) {
        super(name);
        Date date = new Date();
        this.mRandom = new Random(new Date().getTime());
    }

    public void setupPorts() {
        MutableFrameFormat create = ImageFormat.create(3);
        String str = SliceItem.FORMAT_IMAGE;
        addMaskedInputPort(str, create);
        addOutputBasedOnInput(str, str);
    }

    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        return inputFormat;
    }

    public void initProgram(FilterContext context, int target) {
        if (target == 3) {
            ShaderProgram shaderProgram = new ShaderProgram(context, "precision mediump float;\nuniform sampler2D tex_sampler_0;\nuniform vec2 seed;\nuniform float stepsize;\nuniform float inv_max_dist;\nuniform vec2 scale;\nvarying vec2 v_texcoord;\nfloat rand(vec2 loc) {\n  float theta1 = dot(loc, vec2(0.9898, 0.233));\n  float theta2 = dot(loc, vec2(12.0, 78.0));\n  float value = cos(theta1) * sin(theta2) + sin(theta1) * cos(theta2);\n  float temp = mod(197.0 * value, 1.0) + value;\n  float part1 = mod(220.0 * temp, 1.0) + temp;\n  float part2 = value * 0.5453;\n  float part3 = cos(theta1 + theta2) * 0.43758;\n  return fract(part1 + part2 + part3);\n}\nvoid main() {\n  vec4 color = texture2D(tex_sampler_0, v_texcoord);\n  float dither = rand(v_texcoord + seed);\n  vec3 xform = clamp(2.0 * color.rgb, 0.0, 1.0);\n  vec3 temp = clamp(2.0 * (color.rgb + stepsize), 0.0, 1.0);\n  vec3 new_color = clamp(xform + (temp - xform) * (dither - 0.5), 0.0, 1.0);\n  float gray = dot(new_color, vec3(0.299, 0.587, 0.114));\n  new_color = vec3(gray, gray, gray);\n  vec2 coord = v_texcoord - vec2(0.5, 0.5);\n  float dist = length(coord * scale);\n  float lumen = 0.85 / (1.0 + exp((dist * inv_max_dist - 0.83) * 20.0)) + 0.15;\n  gl_FragColor = vec4(new_color * lumen, color.a);\n}\n");
            shaderProgram.setMaximumTileSize(this.mTileSize);
            this.mProgram = shaderProgram;
            this.mTarget = target;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Filter Sharpen does not support frames of target ");
        stringBuilder.append(target);
        stringBuilder.append("!");
        throw new RuntimeException(stringBuilder.toString());
    }

    public void process(FilterContext context) {
        String str = SliceItem.FORMAT_IMAGE;
        Frame input = pullInput(str);
        FrameFormat inputFormat = input.getFormat();
        if (this.mProgram == null || inputFormat.getTarget() != this.mTarget) {
            initProgram(context, inputFormat.getTarget());
        }
        if (!(inputFormat.getWidth() == this.mWidth && inputFormat.getHeight() == this.mHeight)) {
            this.mWidth = inputFormat.getWidth();
            this.mHeight = inputFormat.getHeight();
            initParameters();
        }
        Frame output = context.getFrameManager().newFrame(inputFormat);
        this.mProgram.process(input, output);
        pushOutput(str, output);
        output.release();
    }

    private void initParameters() {
        if (this.mProgram != null) {
            float[] scale = new float[2];
            int i = this.mWidth;
            int i2 = this.mHeight;
            if (i > i2) {
                scale[0] = 1.0f;
                scale[1] = ((float) i2) / ((float) i);
            } else {
                scale[0] = ((float) i) / ((float) i2);
                scale[1] = 1.0f;
            }
            float max_dist = ((float) Math.sqrt((double) ((scale[0] * scale[0]) + (scale[1] * scale[1])))) * 0.5f;
            this.mProgram.setHostValue(BatteryManager.EXTRA_SCALE, scale);
            this.mProgram.setHostValue("inv_max_dist", Float.valueOf(1.0f / max_dist));
            this.mProgram.setHostValue("stepsize", Float.valueOf(0.003921569f));
            this.mProgram.setHostValue("seed", new float[]{this.mRandom.nextFloat(), this.mRandom.nextFloat()});
        }
    }
}
