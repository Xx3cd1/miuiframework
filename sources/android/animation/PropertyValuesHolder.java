package android.animation;

import android.animation.Keyframes.FloatKeyframes;
import android.animation.Keyframes.IntKeyframes;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.FloatProperty;
import android.util.IntProperty;
import android.util.Log;
import android.util.PathParser.PathData;
import android.util.Property;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

public class PropertyValuesHolder implements Cloneable {
    private static Class[] DOUBLE_VARIANTS = new Class[]{Double.TYPE, Double.class, Float.TYPE, Integer.TYPE, Float.class, Integer.class};
    private static Class[] FLOAT_VARIANTS = new Class[]{Float.TYPE, Float.class, Double.TYPE, Integer.TYPE, Double.class, Integer.class};
    private static Class[] INTEGER_VARIANTS = new Class[]{Integer.TYPE, Integer.class, Float.TYPE, Double.TYPE, Float.class, Double.class};
    private static final TypeEvaluator sFloatEvaluator = new FloatEvaluator();
    private static final HashMap<Class, HashMap<String, Method>> sGetterPropertyMap = new HashMap();
    private static final TypeEvaluator sIntEvaluator = new IntEvaluator();
    private static final HashMap<Class, HashMap<String, Method>> sSetterPropertyMap = new HashMap();
    private Object mAnimatedValue;
    private TypeConverter mConverter;
    private TypeEvaluator mEvaluator;
    private Method mGetter;
    Keyframes mKeyframes;
    protected Property mProperty;
    String mPropertyName;
    Method mSetter;
    final Object[] mTmpValueArray;
    Class mValueType;

    static class FloatPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
        float mFloatAnimatedValue;
        FloatKeyframes mFloatKeyframes;
        private FloatProperty mFloatProperty;
        long mJniSetter;

        public FloatPropertyValuesHolder(String propertyName, FloatKeyframes keyframes) {
            super(propertyName, null);
            this.mValueType = Float.TYPE;
            this.mKeyframes = keyframes;
            this.mFloatKeyframes = keyframes;
        }

        public FloatPropertyValuesHolder(Property property, FloatKeyframes keyframes) {
            super(property, null);
            this.mValueType = Float.TYPE;
            this.mKeyframes = keyframes;
            this.mFloatKeyframes = keyframes;
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        public FloatPropertyValuesHolder(String propertyName, float... values) {
            super(propertyName, null);
            setFloatValues(values);
        }

        public FloatPropertyValuesHolder(Property property, float... values) {
            super(property, null);
            setFloatValues(values);
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) this.mProperty;
            }
        }

        public void setProperty(Property property) {
            if (property instanceof FloatProperty) {
                this.mFloatProperty = (FloatProperty) property;
            } else {
                super.setProperty(property);
            }
        }

        public void setFloatValues(float... values) {
            super.setFloatValues(values);
            this.mFloatKeyframes = (FloatKeyframes) this.mKeyframes;
        }

        /* Access modifiers changed, original: 0000 */
        public void calculateValue(float fraction) {
            this.mFloatAnimatedValue = this.mFloatKeyframes.getFloatValue(fraction);
        }

        /* Access modifiers changed, original: 0000 */
        public Object getAnimatedValue() {
            return Float.valueOf(this.mFloatAnimatedValue);
        }

        public FloatPropertyValuesHolder clone() {
            FloatPropertyValuesHolder newPVH = (FloatPropertyValuesHolder) super.clone();
            newPVH.mFloatKeyframes = (FloatKeyframes) newPVH.mKeyframes;
            return newPVH;
        }

        /* Access modifiers changed, original: 0000 */
        public void setAnimatedValue(Object target) {
            String str = "PropertyValuesHolder";
            FloatProperty floatProperty = this.mFloatProperty;
            if (floatProperty != null) {
                floatProperty.setValue(target, this.mFloatAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Float.valueOf(this.mFloatAnimatedValue));
            } else {
                long j = this.mJniSetter;
                if (j != 0) {
                    PropertyValuesHolder.nCallFloatMethod(target, j, this.mFloatAnimatedValue);
                    return;
                }
                if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = Float.valueOf(this.mFloatAnimatedValue);
                        this.mSetter.invoke(target, this.mTmpValueArray);
                    } catch (InvocationTargetException e) {
                        Log.e(str, e.toString());
                    } catch (IllegalAccessException e2) {
                        Log.e(str, e2.toString());
                    }
                }
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetter(Class targetClass) {
            if (this.mProperty == null) {
                synchronized (sJNISetterPropertyMap) {
                    HashMap<String, Long> propertyMap = (HashMap) sJNISetterPropertyMap.get(targetClass);
                    boolean wasInMap = false;
                    if (propertyMap != null) {
                        wasInMap = propertyMap.containsKey(this.mPropertyName);
                        if (wasInMap) {
                            Long jniSetter = (Long) propertyMap.get(this.mPropertyName);
                            if (jniSetter != null) {
                                this.mJniSetter = jniSetter.longValue();
                            }
                        }
                    }
                    if (!wasInMap) {
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetFloatMethod(targetClass, PropertyValuesHolder.getMethodName("set", this.mPropertyName));
                        } catch (NoSuchMethodError e) {
                        }
                        if (propertyMap == null) {
                            propertyMap = new HashMap();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                    }
                }
                if (this.mJniSetter == 0) {
                    super.setupSetter(targetClass);
                }
            }
        }
    }

    static class IntPropertyValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
        int mIntAnimatedValue;
        IntKeyframes mIntKeyframes;
        private IntProperty mIntProperty;
        long mJniSetter;

        public IntPropertyValuesHolder(String propertyName, IntKeyframes keyframes) {
            super(propertyName, null);
            this.mValueType = Integer.TYPE;
            this.mKeyframes = keyframes;
            this.mIntKeyframes = keyframes;
        }

        public IntPropertyValuesHolder(Property property, IntKeyframes keyframes) {
            super(property, null);
            this.mValueType = Integer.TYPE;
            this.mKeyframes = keyframes;
            this.mIntKeyframes = keyframes;
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        public IntPropertyValuesHolder(String propertyName, int... values) {
            super(propertyName, null);
            setIntValues(values);
        }

        public IntPropertyValuesHolder(Property property, int... values) {
            super(property, null);
            setIntValues(values);
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) this.mProperty;
            }
        }

        public void setProperty(Property property) {
            if (property instanceof IntProperty) {
                this.mIntProperty = (IntProperty) property;
            } else {
                super.setProperty(property);
            }
        }

        public void setIntValues(int... values) {
            super.setIntValues(values);
            this.mIntKeyframes = (IntKeyframes) this.mKeyframes;
        }

        /* Access modifiers changed, original: 0000 */
        public void calculateValue(float fraction) {
            this.mIntAnimatedValue = this.mIntKeyframes.getIntValue(fraction);
        }

        /* Access modifiers changed, original: 0000 */
        public Object getAnimatedValue() {
            return Integer.valueOf(this.mIntAnimatedValue);
        }

        public IntPropertyValuesHolder clone() {
            IntPropertyValuesHolder newPVH = (IntPropertyValuesHolder) super.clone();
            newPVH.mIntKeyframes = (IntKeyframes) newPVH.mKeyframes;
            return newPVH;
        }

        /* Access modifiers changed, original: 0000 */
        public void setAnimatedValue(Object target) {
            String str = "PropertyValuesHolder";
            IntProperty intProperty = this.mIntProperty;
            if (intProperty != null) {
                intProperty.setValue(target, this.mIntAnimatedValue);
            } else if (this.mProperty != null) {
                this.mProperty.set(target, Integer.valueOf(this.mIntAnimatedValue));
            } else {
                long j = this.mJniSetter;
                if (j != 0) {
                    PropertyValuesHolder.nCallIntMethod(target, j, this.mIntAnimatedValue);
                    return;
                }
                if (this.mSetter != null) {
                    try {
                        this.mTmpValueArray[0] = Integer.valueOf(this.mIntAnimatedValue);
                        this.mSetter.invoke(target, this.mTmpValueArray);
                    } catch (InvocationTargetException e) {
                        Log.e(str, e.toString());
                    } catch (IllegalAccessException e2) {
                        Log.e(str, e2.toString());
                    }
                }
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetter(Class targetClass) {
            if (this.mProperty == null) {
                synchronized (sJNISetterPropertyMap) {
                    HashMap<String, Long> propertyMap = (HashMap) sJNISetterPropertyMap.get(targetClass);
                    boolean wasInMap = false;
                    if (propertyMap != null) {
                        wasInMap = propertyMap.containsKey(this.mPropertyName);
                        if (wasInMap) {
                            Long jniSetter = (Long) propertyMap.get(this.mPropertyName);
                            if (jniSetter != null) {
                                this.mJniSetter = jniSetter.longValue();
                            }
                        }
                    }
                    if (!wasInMap) {
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetIntMethod(targetClass, PropertyValuesHolder.getMethodName("set", this.mPropertyName));
                        } catch (NoSuchMethodError e) {
                        }
                        if (propertyMap == null) {
                            propertyMap = new HashMap();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                    }
                }
                if (this.mJniSetter == 0) {
                    super.setupSetter(targetClass);
                }
            }
        }
    }

    static class MultiFloatValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
        private long mJniSetter;

        public MultiFloatValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Object... values) {
            super(propertyName, null);
            setConverter(converter);
            setObjectValues(values);
            setEvaluator(evaluator);
        }

        public MultiFloatValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Keyframes keyframes) {
            super(propertyName, null);
            setConverter(converter);
            this.mKeyframes = keyframes;
            setEvaluator(evaluator);
        }

        /* Access modifiers changed, original: 0000 */
        public void setAnimatedValue(Object target) {
            float[] values = (float[]) getAnimatedValue();
            int numParameters = values.length;
            long j = this.mJniSetter;
            if (j == 0) {
                return;
            }
            if (numParameters == 1) {
                PropertyValuesHolder.nCallFloatMethod(target, j, values[0]);
            } else if (numParameters == 2) {
                PropertyValuesHolder.nCallTwoFloatMethod(target, j, values[0], values[1]);
            } else if (numParameters != 4) {
                PropertyValuesHolder.nCallMultipleFloatMethod(target, j, values);
            } else {
                PropertyValuesHolder.nCallFourFloatMethod(target, j, values[0], values[1], values[2], values[3]);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetterAndGetter(Object target) {
            setupSetter(target.getClass());
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetter(Class targetClass) {
            if (this.mJniSetter == 0) {
                synchronized (sJNISetterPropertyMap) {
                    HashMap<String, Long> propertyMap = (HashMap) sJNISetterPropertyMap.get(targetClass);
                    boolean wasInMap = false;
                    if (propertyMap != null) {
                        wasInMap = propertyMap.containsKey(this.mPropertyName);
                        if (wasInMap) {
                            Long jniSetter = (Long) propertyMap.get(this.mPropertyName);
                            if (jniSetter != null) {
                                this.mJniSetter = jniSetter.longValue();
                            }
                        }
                    }
                    if (!wasInMap) {
                        String methodName = PropertyValuesHolder.getMethodName("set", this.mPropertyName);
                        calculateValue(0.0f);
                        int numParams = ((float[]) getAnimatedValue()).length;
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetMultipleFloatMethod(targetClass, methodName, numParams);
                        } catch (NoSuchMethodError e) {
                            try {
                                this.mJniSetter = PropertyValuesHolder.nGetMultipleFloatMethod(targetClass, this.mPropertyName, numParams);
                            } catch (NoSuchMethodError e2) {
                            }
                        }
                        if (propertyMap == null) {
                            propertyMap = new HashMap();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                    }
                }
            }
        }
    }

    static class MultiIntValuesHolder extends PropertyValuesHolder {
        private static final HashMap<Class, HashMap<String, Long>> sJNISetterPropertyMap = new HashMap();
        private long mJniSetter;

        public MultiIntValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Object... values) {
            super(propertyName, null);
            setConverter(converter);
            setObjectValues(values);
            setEvaluator(evaluator);
        }

        public MultiIntValuesHolder(String propertyName, TypeConverter converter, TypeEvaluator evaluator, Keyframes keyframes) {
            super(propertyName, null);
            setConverter(converter);
            this.mKeyframes = keyframes;
            setEvaluator(evaluator);
        }

        /* Access modifiers changed, original: 0000 */
        public void setAnimatedValue(Object target) {
            int[] values = (int[]) getAnimatedValue();
            int numParameters = values.length;
            long j = this.mJniSetter;
            if (j == 0) {
                return;
            }
            if (numParameters == 1) {
                PropertyValuesHolder.nCallIntMethod(target, j, values[0]);
            } else if (numParameters == 2) {
                PropertyValuesHolder.nCallTwoIntMethod(target, j, values[0], values[1]);
            } else if (numParameters != 4) {
                PropertyValuesHolder.nCallMultipleIntMethod(target, j, values);
            } else {
                PropertyValuesHolder.nCallFourIntMethod(target, j, values[0], values[1], values[2], values[3]);
            }
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetterAndGetter(Object target) {
            setupSetter(target.getClass());
        }

        /* Access modifiers changed, original: 0000 */
        public void setupSetter(Class targetClass) {
            if (this.mJniSetter == 0) {
                synchronized (sJNISetterPropertyMap) {
                    HashMap<String, Long> propertyMap = (HashMap) sJNISetterPropertyMap.get(targetClass);
                    boolean wasInMap = false;
                    if (propertyMap != null) {
                        wasInMap = propertyMap.containsKey(this.mPropertyName);
                        if (wasInMap) {
                            Long jniSetter = (Long) propertyMap.get(this.mPropertyName);
                            if (jniSetter != null) {
                                this.mJniSetter = jniSetter.longValue();
                            }
                        }
                    }
                    if (!wasInMap) {
                        String methodName = PropertyValuesHolder.getMethodName("set", this.mPropertyName);
                        calculateValue(0.0f);
                        int numParams = ((int[]) getAnimatedValue()).length;
                        try {
                            this.mJniSetter = PropertyValuesHolder.nGetMultipleIntMethod(targetClass, methodName, numParams);
                        } catch (NoSuchMethodError e) {
                            try {
                                this.mJniSetter = PropertyValuesHolder.nGetMultipleIntMethod(targetClass, this.mPropertyName, numParams);
                            } catch (NoSuchMethodError e2) {
                            }
                        }
                        if (propertyMap == null) {
                            propertyMap = new HashMap();
                            sJNISetterPropertyMap.put(targetClass, propertyMap);
                        }
                        propertyMap.put(this.mPropertyName, Long.valueOf(this.mJniSetter));
                    }
                }
            }
        }
    }

    private static class PointFToFloatArray extends TypeConverter<PointF, float[]> {
        private float[] mCoordinates = new float[2];

        public PointFToFloatArray() {
            super(PointF.class, float[].class);
        }

        public float[] convert(PointF value) {
            this.mCoordinates[0] = value.x;
            this.mCoordinates[1] = value.y;
            return this.mCoordinates;
        }
    }

    private static class PointFToIntArray extends TypeConverter<PointF, int[]> {
        private int[] mCoordinates = new int[2];

        public PointFToIntArray() {
            super(PointF.class, int[].class);
        }

        public int[] convert(PointF value) {
            this.mCoordinates[0] = Math.round(value.x);
            this.mCoordinates[1] = Math.round(value.y);
            return this.mCoordinates;
        }
    }

    public static class PropertyValues {
        public DataSource dataSource = null;
        public Object endValue;
        public String propertyName;
        public Object startValue;
        public Class type;

        public interface DataSource {
            Object getValueAtFraction(float f);
        }

        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("property name: ");
            stringBuilder.append(this.propertyName);
            stringBuilder.append(", type: ");
            stringBuilder.append(this.type);
            stringBuilder.append(", startValue: ");
            stringBuilder.append(this.startValue.toString());
            stringBuilder.append(", endValue: ");
            stringBuilder.append(this.endValue.toString());
            return stringBuilder.toString();
        }
    }

    private static native void nCallFloatMethod(Object obj, long j, float f);

    private static native void nCallFourFloatMethod(Object obj, long j, float f, float f2, float f3, float f4);

    private static native void nCallFourIntMethod(Object obj, long j, int i, int i2, int i3, int i4);

    private static native void nCallIntMethod(Object obj, long j, int i);

    private static native void nCallMultipleFloatMethod(Object obj, long j, float[] fArr);

    private static native void nCallMultipleIntMethod(Object obj, long j, int[] iArr);

    private static native void nCallTwoFloatMethod(Object obj, long j, float f, float f2);

    private static native void nCallTwoIntMethod(Object obj, long j, int i, int i2);

    private static native long nGetFloatMethod(Class cls, String str);

    private static native long nGetIntMethod(Class cls, String str);

    private static native long nGetMultipleFloatMethod(Class cls, String str, int i);

    private static native long nGetMultipleIntMethod(Class cls, String str, int i);

    private PropertyValuesHolder(String propertyName) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframes = null;
        this.mTmpValueArray = new Object[1];
        this.mPropertyName = propertyName;
    }

    private PropertyValuesHolder(Property property) {
        this.mSetter = null;
        this.mGetter = null;
        this.mKeyframes = null;
        this.mTmpValueArray = new Object[1];
        this.mProperty = property;
        if (property != null) {
            this.mPropertyName = property.getName();
        }
    }

    public static PropertyValuesHolder ofInt(String propertyName, int... values) {
        return new IntPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofInt(Property<?, Integer> property, int... values) {
        return new IntPropertyValuesHolder((Property) property, values);
    }

    public static PropertyValuesHolder ofMultiInt(String propertyName, int[][] values) {
        if (values.length >= 2) {
            int numParameters = 0;
            int i = 0;
            while (i < values.length) {
                if (values[i] != null) {
                    int length = values[i].length;
                    if (i == 0) {
                        numParameters = length;
                    } else if (length != numParameters) {
                        throw new IllegalArgumentException("Values must all have the same length");
                    }
                    i++;
                } else {
                    throw new IllegalArgumentException("values must not be null");
                }
            }
            return new MultiIntValuesHolder(propertyName, null, new IntArrayEvaluator(new int[numParameters]), (Object[]) values);
        }
        throw new IllegalArgumentException("At least 2 values must be supplied");
    }

    public static PropertyValuesHolder ofMultiInt(String propertyName, Path path) {
        return new MultiIntValuesHolder(propertyName, new PointFToIntArray(), null, KeyframeSet.ofPath(path));
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofMultiInt(String propertyName, TypeConverter<V, int[]> converter, TypeEvaluator<V> evaluator, V... values) {
        return new MultiIntValuesHolder(propertyName, (TypeConverter) converter, (TypeEvaluator) evaluator, (Object[]) values);
    }

    public static <T> PropertyValuesHolder ofMultiInt(String propertyName, TypeConverter<T, int[]> converter, TypeEvaluator<T> evaluator, Keyframe... values) {
        return new MultiIntValuesHolder(propertyName, (TypeConverter) converter, (TypeEvaluator) evaluator, KeyframeSet.ofKeyframe(values));
    }

    public static PropertyValuesHolder ofFloat(String propertyName, float... values) {
        return new FloatPropertyValuesHolder(propertyName, values);
    }

    public static PropertyValuesHolder ofFloat(Property<?, Float> property, float... values) {
        return new FloatPropertyValuesHolder((Property) property, values);
    }

    public static PropertyValuesHolder ofMultiFloat(String propertyName, float[][] values) {
        if (values.length >= 2) {
            int numParameters = 0;
            int i = 0;
            while (i < values.length) {
                if (values[i] != null) {
                    int length = values[i].length;
                    if (i == 0) {
                        numParameters = length;
                    } else if (length != numParameters) {
                        throw new IllegalArgumentException("Values must all have the same length");
                    }
                    i++;
                } else {
                    throw new IllegalArgumentException("values must not be null");
                }
            }
            return new MultiFloatValuesHolder(propertyName, null, new FloatArrayEvaluator(new float[numParameters]), (Object[]) values);
        }
        throw new IllegalArgumentException("At least 2 values must be supplied");
    }

    public static PropertyValuesHolder ofMultiFloat(String propertyName, Path path) {
        return new MultiFloatValuesHolder(propertyName, new PointFToFloatArray(), null, KeyframeSet.ofPath(path));
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofMultiFloat(String propertyName, TypeConverter<V, float[]> converter, TypeEvaluator<V> evaluator, V... values) {
        return new MultiFloatValuesHolder(propertyName, (TypeConverter) converter, (TypeEvaluator) evaluator, (Object[]) values);
    }

    public static <T> PropertyValuesHolder ofMultiFloat(String propertyName, TypeConverter<T, float[]> converter, TypeEvaluator<T> evaluator, Keyframe... values) {
        return new MultiFloatValuesHolder(propertyName, (TypeConverter) converter, (TypeEvaluator) evaluator, KeyframeSet.ofKeyframe(values));
    }

    public static PropertyValuesHolder ofObject(String propertyName, TypeEvaluator evaluator, Object... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static PropertyValuesHolder ofObject(String propertyName, TypeConverter<PointF, ?> converter, Path path) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mKeyframes = KeyframeSet.ofPath(path);
        pvh.mValueType = PointF.class;
        pvh.setConverter(converter);
        return pvh;
    }

    @SafeVarargs
    public static <V> PropertyValuesHolder ofObject(Property property, TypeEvaluator<V> evaluator, V... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    @SafeVarargs
    public static <T, V> PropertyValuesHolder ofObject(Property<?, V> property, TypeConverter<T, V> converter, TypeEvaluator<T> evaluator, T... values) {
        PropertyValuesHolder pvh = new PropertyValuesHolder((Property) property);
        pvh.setConverter(converter);
        pvh.setObjectValues(values);
        pvh.setEvaluator(evaluator);
        return pvh;
    }

    public static <V> PropertyValuesHolder ofObject(Property<?, V> property, TypeConverter<PointF, V> converter, Path path) {
        PropertyValuesHolder pvh = new PropertyValuesHolder((Property) property);
        pvh.mKeyframes = KeyframeSet.ofPath(path);
        pvh.mValueType = PointF.class;
        pvh.setConverter(converter);
        return pvh;
    }

    public static PropertyValuesHolder ofKeyframe(String propertyName, Keyframe... values) {
        return ofKeyframes(propertyName, KeyframeSet.ofKeyframe(values));
    }

    public static PropertyValuesHolder ofKeyframe(Property property, Keyframe... values) {
        return ofKeyframes(property, KeyframeSet.ofKeyframe(values));
    }

    static PropertyValuesHolder ofKeyframes(String propertyName, Keyframes keyframes) {
        if (keyframes instanceof IntKeyframes) {
            return new IntPropertyValuesHolder(propertyName, (IntKeyframes) keyframes);
        }
        if (keyframes instanceof FloatKeyframes) {
            return new FloatPropertyValuesHolder(propertyName, (FloatKeyframes) keyframes);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(propertyName);
        pvh.mKeyframes = keyframes;
        pvh.mValueType = keyframes.getType();
        return pvh;
    }

    static PropertyValuesHolder ofKeyframes(Property property, Keyframes keyframes) {
        if (keyframes instanceof IntKeyframes) {
            return new IntPropertyValuesHolder(property, (IntKeyframes) keyframes);
        }
        if (keyframes instanceof FloatKeyframes) {
            return new FloatPropertyValuesHolder(property, (FloatKeyframes) keyframes);
        }
        PropertyValuesHolder pvh = new PropertyValuesHolder(property);
        pvh.mKeyframes = keyframes;
        pvh.mValueType = keyframes.getType();
        return pvh;
    }

    public void setIntValues(int... values) {
        this.mValueType = Integer.TYPE;
        this.mKeyframes = KeyframeSet.ofInt(values);
    }

    public void setFloatValues(float... values) {
        this.mValueType = Float.TYPE;
        this.mKeyframes = KeyframeSet.ofFloat(values);
    }

    public void setKeyframes(Keyframe... values) {
        int numKeyframes = values.length;
        Keyframe[] keyframes = new Keyframe[Math.max(numKeyframes, 2)];
        this.mValueType = values[0].getType();
        for (int i = 0; i < numKeyframes; i++) {
            keyframes[i] = values[i];
        }
        this.mKeyframes = new KeyframeSet(keyframes);
    }

    public void setObjectValues(Object... values) {
        this.mValueType = values[0].getClass();
        this.mKeyframes = KeyframeSet.ofObject(values);
        TypeEvaluator typeEvaluator = this.mEvaluator;
        if (typeEvaluator != null) {
            this.mKeyframes.setEvaluator(typeEvaluator);
        }
    }

    public void setConverter(TypeConverter converter) {
        this.mConverter = converter;
    }

    private Method getPropertyFunction(Class targetClass, String prefix, Class valueType) {
        Method returnVal = null;
        String methodName = getMethodName(prefix, this.mPropertyName);
        if (valueType == null) {
            try {
                returnVal = targetClass.getMethod(methodName, null);
            } catch (NoSuchMethodException e) {
            }
        } else {
            Class[] typeVariants;
            Class[] args = new Class[1];
            if (valueType.equals(Float.class)) {
                typeVariants = FLOAT_VARIANTS;
            } else if (valueType.equals(Integer.class)) {
                typeVariants = INTEGER_VARIANTS;
            } else {
                typeVariants = valueType.equals(Double.class) ? DOUBLE_VARIANTS : new Class[]{valueType};
            }
            int length = typeVariants.length;
            Method returnVal2 = null;
            int returnVal3 = 0;
            while (returnVal3 < length) {
                Class typeVariant = typeVariants[returnVal3];
                args[0] = typeVariant;
                try {
                    returnVal2 = targetClass.getMethod(methodName, args);
                    if (this.mConverter == null) {
                        this.mValueType = typeVariant;
                    }
                    return returnVal2;
                } catch (NoSuchMethodException e2) {
                    returnVal3++;
                }
            }
            returnVal = returnVal2;
        }
        if (returnVal == null) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Method ");
            stringBuilder.append(getMethodName(prefix, this.mPropertyName));
            stringBuilder.append("() with type ");
            stringBuilder.append(valueType);
            stringBuilder.append(" not found on target class ");
            stringBuilder.append(targetClass);
            Log.w("PropertyValuesHolder", stringBuilder.toString());
        }
        return returnVal;
    }

    private Method setupSetterOrGetter(Class targetClass, HashMap<Class, HashMap<String, Method>> propertyMapMap, String prefix, Class valueType) {
        Method setterOrGetter = null;
        synchronized (propertyMapMap) {
            HashMap<String, Method> propertyMap = (HashMap) propertyMapMap.get(targetClass);
            boolean wasInMap = false;
            if (propertyMap != null) {
                wasInMap = propertyMap.containsKey(this.mPropertyName);
                if (wasInMap) {
                    setterOrGetter = (Method) propertyMap.get(this.mPropertyName);
                }
            }
            if (!wasInMap) {
                setterOrGetter = getPropertyFunction(targetClass, prefix, valueType);
                if (propertyMap == null) {
                    propertyMap = new HashMap();
                    propertyMapMap.put(targetClass, propertyMap);
                }
                propertyMap.put(this.mPropertyName, setterOrGetter);
            }
        }
        return setterOrGetter;
    }

    /* Access modifiers changed, original: 0000 */
    public void setupSetter(Class targetClass) {
        Class<?> propertyType = this.mConverter;
        this.mSetter = setupSetterOrGetter(targetClass, sSetterPropertyMap, "set", propertyType == null ? this.mValueType : propertyType.getTargetType());
    }

    private void setupGetter(Class targetClass) {
        this.mGetter = setupSetterOrGetter(targetClass, sGetterPropertyMap, "get", null);
    }

    /* Access modifiers changed, original: 0000 */
    public void setupSetterAndGetter(Object target) {
        List<Keyframe> keyframes;
        int keyframeCount;
        int i;
        Keyframe kf;
        String str = "PropertyValuesHolder";
        if (this.mProperty != null) {
            Object testValue = null;
            try {
                keyframes = this.mKeyframes.getKeyframes();
                keyframeCount = keyframes == null ? 0 : keyframes.size();
                for (i = 0; i < keyframeCount; i++) {
                    kf = (Keyframe) keyframes.get(i);
                    if (!kf.hasValue() || kf.valueWasSetOnStart()) {
                        if (testValue == null) {
                            testValue = convertBack(this.mProperty.get(target));
                        }
                        kf.setValue(testValue);
                        kf.setValueWasSetOnStart(true);
                    }
                }
                return;
            } catch (ClassCastException e) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("No such property (");
                stringBuilder.append(this.mProperty.getName());
                stringBuilder.append(") on target object ");
                stringBuilder.append(target);
                stringBuilder.append(". Trying reflection instead");
                Log.w(str, stringBuilder.toString());
                this.mProperty = null;
            }
        }
        if (this.mProperty == null) {
            Class targetClass = target.getClass();
            if (this.mSetter == null) {
                setupSetter(targetClass);
            }
            keyframes = this.mKeyframes.getKeyframes();
            keyframeCount = keyframes == null ? 0 : keyframes.size();
            for (i = 0; i < keyframeCount; i++) {
                kf = (Keyframe) keyframes.get(i);
                if (!kf.hasValue() || kf.valueWasSetOnStart()) {
                    if (this.mGetter == null) {
                        setupGetter(targetClass);
                        if (this.mGetter == null) {
                            return;
                        }
                    }
                    try {
                        kf.setValue(convertBack(this.mGetter.invoke(target, new Object[0])));
                        kf.setValueWasSetOnStart(true);
                    } catch (InvocationTargetException e2) {
                        Log.e(str, e2.toString());
                    } catch (IllegalAccessException e3) {
                        Log.e(str, e3.toString());
                    }
                }
            }
        }
    }

    private Object convertBack(Object value) {
        TypeConverter typeConverter = this.mConverter;
        if (typeConverter == null) {
            return value;
        }
        if (typeConverter instanceof BidirectionalTypeConverter) {
            return ((BidirectionalTypeConverter) typeConverter).convertBack(value);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Converter ");
        stringBuilder.append(this.mConverter.getClass().getName());
        stringBuilder.append(" must be a BidirectionalTypeConverter");
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private void setupValue(Object target, Keyframe kf) {
        String str = "PropertyValuesHolder";
        Property property = this.mProperty;
        if (property != null) {
            kf.setValue(convertBack(property.get(target)));
        } else {
            try {
                if (this.mGetter == null) {
                    setupGetter(target.getClass());
                    if (this.mGetter == null) {
                        return;
                    }
                }
                kf.setValue(convertBack(this.mGetter.invoke(target, new Object[0])));
            } catch (InvocationTargetException e) {
                Log.e(str, e.toString());
            } catch (IllegalAccessException e2) {
                Log.e(str, e2.toString());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void setupStartValue(Object target) {
        List<Keyframe> keyframes = this.mKeyframes.getKeyframes();
        if (!keyframes.isEmpty()) {
            setupValue(target, (Keyframe) keyframes.get(0));
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void setupEndValue(Object target) {
        List<Keyframe> keyframes = this.mKeyframes.getKeyframes();
        if (!keyframes.isEmpty()) {
            setupValue(target, (Keyframe) keyframes.get(keyframes.size() - 1));
        }
    }

    public PropertyValuesHolder clone() {
        try {
            PropertyValuesHolder newPVH = (PropertyValuesHolder) super.clone();
            newPVH.mPropertyName = this.mPropertyName;
            newPVH.mProperty = this.mProperty;
            newPVH.mKeyframes = this.mKeyframes.clone();
            newPVH.mEvaluator = this.mEvaluator;
            return newPVH;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void setAnimatedValue(Object target) {
        String str = "PropertyValuesHolder";
        Property property = this.mProperty;
        if (property != null) {
            property.set(target, getAnimatedValue());
        }
        if (this.mSetter != null) {
            try {
                this.mTmpValueArray[0] = getAnimatedValue();
                this.mSetter.invoke(target, this.mTmpValueArray);
            } catch (InvocationTargetException e) {
                Log.e(str, e.toString());
            } catch (IllegalAccessException e2) {
                Log.e(str, e2.toString());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void init() {
        TypeEvaluator typeEvaluator;
        if (this.mEvaluator == null) {
            Class cls = this.mValueType;
            if (cls == Integer.class) {
                typeEvaluator = sIntEvaluator;
            } else if (cls == Float.class) {
                typeEvaluator = sFloatEvaluator;
            } else {
                typeEvaluator = null;
            }
            this.mEvaluator = typeEvaluator;
        }
        typeEvaluator = this.mEvaluator;
        if (typeEvaluator != null) {
            this.mKeyframes.setEvaluator(typeEvaluator);
        }
    }

    public void setEvaluator(TypeEvaluator evaluator) {
        this.mEvaluator = evaluator;
        this.mKeyframes.setEvaluator(evaluator);
    }

    /* Access modifiers changed, original: 0000 */
    public void calculateValue(float fraction) {
        Object value = this.mKeyframes.getValue(fraction);
        TypeConverter typeConverter = this.mConverter;
        this.mAnimatedValue = typeConverter == null ? value : typeConverter.convert(value);
    }

    public void setPropertyName(String propertyName) {
        this.mPropertyName = propertyName;
    }

    public void setProperty(Property property) {
        this.mProperty = property;
    }

    public String getPropertyName() {
        return this.mPropertyName;
    }

    /* Access modifiers changed, original: 0000 */
    public Object getAnimatedValue() {
        return this.mAnimatedValue;
    }

    public void getPropertyValues(PropertyValues values) {
        init();
        values.propertyName = this.mPropertyName;
        values.type = this.mValueType;
        values.startValue = this.mKeyframes.getValue(0.0f);
        if (values.startValue instanceof PathData) {
            values.startValue = new PathData((PathData) values.startValue);
        }
        values.endValue = this.mKeyframes.getValue(1.0f);
        if (values.endValue instanceof PathData) {
            values.endValue = new PathData((PathData) values.endValue);
        }
        Keyframes keyframes = this.mKeyframes;
        if ((keyframes instanceof FloatKeyframesBase) || (keyframes instanceof IntKeyframesBase) || (keyframes.getKeyframes() != null && this.mKeyframes.getKeyframes().size() > 2)) {
            values.dataSource = new DataSource() {
                public Object getValueAtFraction(float fraction) {
                    return PropertyValuesHolder.this.mKeyframes.getValue(fraction);
                }
            };
        } else {
            values.dataSource = null;
        }
    }

    public Class getValueType() {
        return this.mValueType;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.mPropertyName);
        stringBuilder.append(": ");
        stringBuilder.append(this.mKeyframes.toString());
        return stringBuilder.toString();
    }

    static String getMethodName(String prefix, String propertyName) {
        if (propertyName == null || propertyName.length() == 0) {
            return prefix;
        }
        char firstLetter = Character.toUpperCase(propertyName.charAt(0));
        String theRest = propertyName.substring(1);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(prefix);
        stringBuilder.append(firstLetter);
        stringBuilder.append(theRest);
        return stringBuilder.toString();
    }
}
