package android.hardware.camera2.legacy;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.ICameraDeviceCallbacks;
import android.hardware.camera2.impl.CameraMetadataNative;
import android.hardware.camera2.impl.CaptureResultExtras;
import android.hardware.camera2.impl.PhysicalCaptureResultInfo;
import android.hardware.camera2.legacy.CameraDeviceState.CameraDeviceStateListener;
import android.hardware.camera2.legacy.LegacyExceptionUtils.BufferQueueAbandonedException;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.camera2.utils.ArrayUtils;
import android.hardware.camera2.utils.SubmitInfo;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.RemoteException;
import android.os.ServiceSpecificException;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.util.SparseArray;
import android.view.Surface;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LegacyCameraDevice implements AutoCloseable {
    private static final boolean DEBUG = false;
    private static final int GRALLOC_USAGE_HW_COMPOSER = 2048;
    private static final int GRALLOC_USAGE_HW_RENDER = 512;
    private static final int GRALLOC_USAGE_HW_TEXTURE = 256;
    private static final int GRALLOC_USAGE_HW_VIDEO_ENCODER = 65536;
    private static final int GRALLOC_USAGE_RENDERSCRIPT = 1048576;
    private static final int GRALLOC_USAGE_SW_READ_OFTEN = 3;
    private static final int ILLEGAL_VALUE = -1;
    public static final int MAX_DIMEN_FOR_ROUNDING = 1920;
    public static final int NATIVE_WINDOW_SCALING_MODE_SCALE_TO_WINDOW = 1;
    private final String TAG;
    private final Handler mCallbackHandler;
    private final HandlerThread mCallbackHandlerThread = new HandlerThread("CallbackThread");
    private final int mCameraId;
    private boolean mClosed = false;
    private SparseArray<Surface> mConfiguredSurfaces;
    private final ICameraDeviceCallbacks mDeviceCallbacks;
    private final CameraDeviceState mDeviceState = new CameraDeviceState();
    private final ConditionVariable mIdle = new ConditionVariable(true);
    private final RequestThreadManager mRequestThreadManager;
    private final Handler mResultHandler;
    private final HandlerThread mResultThread = new HandlerThread("ResultThread");
    private final CameraDeviceStateListener mStateListener = new CameraDeviceStateListener() {
        public void onError(final int errorCode, Object errorArg, final RequestHolder holder) {
            if (errorCode == 0 || errorCode == 1 || errorCode == 2) {
                LegacyCameraDevice.this.mIdle.open();
            }
            final CaptureResultExtras extras = LegacyCameraDevice.this.getExtrasFromRequest(holder, errorCode, errorArg);
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onDeviceError(errorCode, extras);
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onCameraError callback: ", e);
                    }
                }
            });
        }

        public void onConfiguring() {
        }

        public void onIdle() {
            LegacyCameraDevice.this.mIdle.open();
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onDeviceIdle();
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onCameraIdle callback: ", e);
                    }
                }
            });
        }

        public void onBusy() {
            LegacyCameraDevice.this.mIdle.close();
        }

        public void onCaptureStarted(RequestHolder holder, long timestamp) {
            final RequestHolder requestHolder = holder;
            final CaptureResultExtras access$400 = LegacyCameraDevice.this.getExtrasFromRequest(holder);
            final long j = timestamp;
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onCaptureStarted(access$400, j);
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onCameraError callback: ", e);
                    }
                }
            });
        }

        public void onRequestQueueEmpty() {
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onRequestQueueEmpty();
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onRequestQueueEmpty callback: ", e);
                    }
                }
            });
        }

        public void onCaptureResult(final CameraMetadataNative result, final RequestHolder holder) {
            final CaptureResultExtras extras = LegacyCameraDevice.this.getExtrasFromRequest(holder);
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onResultReceived(result, extras, new PhysicalCaptureResultInfo[0]);
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onCameraError callback: ", e);
                    }
                }
            });
        }

        public void onRepeatingRequestError(final long lastFrameNumber, final int repeatingRequestId) {
            LegacyCameraDevice.this.mResultHandler.post(new Runnable() {
                public void run() {
                    try {
                        LegacyCameraDevice.this.mDeviceCallbacks.onRepeatingRequestError(lastFrameNumber, repeatingRequestId);
                    } catch (RemoteException e) {
                        throw new IllegalStateException("Received remote exception during onRepeatingRequestError callback: ", e);
                    }
                }
            });
        }
    };
    private final CameraCharacteristics mStaticCharacteristics;

    private static native int nativeConnectSurface(Surface surface);

    private static native int nativeDetectSurfaceDataspace(Surface surface);

    private static native int nativeDetectSurfaceDimens(Surface surface, int[] iArr);

    private static native int nativeDetectSurfaceType(Surface surface);

    private static native int nativeDetectSurfaceUsageFlags(Surface surface);

    private static native int nativeDetectTextureDimens(SurfaceTexture surfaceTexture, int[] iArr);

    private static native int nativeDisconnectSurface(Surface surface);

    static native int nativeGetJpegFooterSize();

    private static native long nativeGetSurfaceId(Surface surface);

    private static native int nativeProduceFrame(Surface surface, byte[] bArr, int i, int i2, int i3);

    private static native int nativeSetNextTimestamp(Surface surface, long j);

    private static native int nativeSetScalingMode(Surface surface, int i);

    private static native int nativeSetSurfaceDimens(Surface surface, int i, int i2);

    private static native int nativeSetSurfaceFormat(Surface surface, int i);

    private static native int nativeSetSurfaceOrientation(Surface surface, int i, int i2);

    private CaptureResultExtras getExtrasFromRequest(RequestHolder holder) {
        return getExtrasFromRequest(holder, -1, null);
    }

    private CaptureResultExtras getExtrasFromRequest(RequestHolder holder, int errorCode, Object errorArg) {
        int errorStreamId = -1;
        if (errorCode == 5) {
            int indexOfTarget = this.mConfiguredSurfaces.indexOfValue((Surface) errorArg);
            if (indexOfTarget < 0) {
                Log.e(this.TAG, "Buffer drop error reported for unknown Surface");
            } else {
                errorStreamId = this.mConfiguredSurfaces.keyAt(indexOfTarget);
            }
        }
        if (holder == null) {
            return new CaptureResultExtras(-1, -1, -1, -1, -1, -1, -1, null);
        }
        return new CaptureResultExtras(holder.getRequestId(), holder.getSubsequeceId(), 0, 0, holder.getFrameNumber(), 1, errorStreamId, null);
    }

    static boolean needsConversion(Surface s) throws BufferQueueAbandonedException {
        int nativeType = detectSurfaceType(s);
        return nativeType == 35 || nativeType == ImageFormat.YV12 || nativeType == 17;
    }

    public LegacyCameraDevice(int cameraId, Camera camera, CameraCharacteristics characteristics, ICameraDeviceCallbacks callbacks) {
        this.mCameraId = cameraId;
        this.mDeviceCallbacks = callbacks;
        this.TAG = String.format("CameraDevice-%d-LE", new Object[]{Integer.valueOf(this.mCameraId)});
        this.mResultThread.start();
        this.mResultHandler = new Handler(this.mResultThread.getLooper());
        this.mCallbackHandlerThread.start();
        this.mCallbackHandler = new Handler(this.mCallbackHandlerThread.getLooper());
        this.mDeviceState.setCameraDeviceCallbacks(this.mCallbackHandler, this.mStateListener);
        this.mStaticCharacteristics = characteristics;
        this.mRequestThreadManager = new RequestThreadManager(cameraId, camera, characteristics, this.mDeviceState);
        this.mRequestThreadManager.start();
    }

    public int configureOutputs(SparseArray<Surface> outputs) {
        return configureOutputs(outputs, false);
    }

    public int configureOutputs(SparseArray<Surface> outputs, boolean validateSurfacesOnly) {
        SparseArray<Surface> sparseArray = outputs;
        ArrayList sizedSurfaces = new ArrayList();
        if (sparseArray != null) {
            int count = outputs.size();
            int i = 0;
            while (i < count) {
                Surface output = (Surface) sparseArray.valueAt(i);
                if (output == null) {
                    Log.e(this.TAG, "configureOutputs - null outputs are not allowed");
                    return LegacyExceptionUtils.BAD_VALUE;
                } else if (output.isValid()) {
                    StreamConfigurationMap streamConfigurations = (StreamConfigurationMap) this.mStaticCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                    try {
                        Size s = getSurfaceSize(output);
                        int surfaceType = detectSurfaceType(output);
                        boolean flexibleConsumer = isFlexibleConsumer(output);
                        Object[] sizes = streamConfigurations.getOutputSizes(surfaceType);
                        if (sizes == null) {
                            if (surfaceType == 34) {
                                sizes = streamConfigurations.getOutputSizes(35);
                            } else if (surfaceType == 33) {
                                sizes = streamConfigurations.getOutputSizes(256);
                            }
                        }
                        if (ArrayUtils.contains(sizes, (Object) s)) {
                            sizedSurfaces.add(new Pair(output, s));
                        } else {
                            String reason;
                            if (flexibleConsumer) {
                                Size findClosestSize = findClosestSize(s, sizes);
                                s = findClosestSize;
                                if (findClosestSize != null) {
                                    sizedSurfaces.add(new Pair(output, s));
                                }
                            }
                            if (sizes == null) {
                                reason = "format is invalid.";
                            } else {
                                reason = new StringBuilder();
                                reason.append("size not in valid set: ");
                                reason.append(Arrays.toString(sizes));
                                reason = reason.toString();
                            }
                            Log.e(this.TAG, String.format("Surface with size (w=%d, h=%d) and format 0x%x is not valid, %s", new Object[]{Integer.valueOf(s.getWidth()), Integer.valueOf(s.getHeight()), Integer.valueOf(surfaceType), reason}));
                            return LegacyExceptionUtils.BAD_VALUE;
                        }
                        if (!validateSurfacesOnly) {
                            setSurfaceDimens(output, s.getWidth(), s.getHeight());
                        }
                        i++;
                    } catch (BufferQueueAbandonedException e) {
                        Log.e(this.TAG, "Surface bufferqueue is abandoned, cannot configure as output: ", e);
                        return LegacyExceptionUtils.BAD_VALUE;
                    }
                } else {
                    Log.e(this.TAG, "configureOutputs - invalid output surfaces are not allowed");
                    return LegacyExceptionUtils.BAD_VALUE;
                }
            }
        }
        if (validateSurfacesOnly) {
            return 0;
        }
        boolean success = false;
        if (this.mDeviceState.setConfiguring()) {
            this.mRequestThreadManager.configure(sizedSurfaces);
            success = this.mDeviceState.setIdle();
        }
        if (!success) {
            return LegacyExceptionUtils.INVALID_OPERATION;
        }
        this.mConfiguredSurfaces = sparseArray;
        return 0;
    }

    public SubmitInfo submitRequestList(CaptureRequest[] requestList, boolean repeating) {
        if (requestList == null || requestList.length == 0) {
            String str = "submitRequestList - Empty/null requests are not allowed";
            Log.e(this.TAG, str);
            throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, str);
        }
        try {
            List<Long> surfaceIds;
            if (this.mConfiguredSurfaces == null) {
                surfaceIds = new ArrayList();
            } else {
                surfaceIds = getSurfaceIds(this.mConfiguredSurfaces);
            }
            for (CaptureRequest request : requestList) {
                String str2;
                if (request.getTargets().isEmpty()) {
                    str2 = "submitRequestList - Each request must have at least one Surface target";
                    Log.e(this.TAG, str2);
                    throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, str2);
                }
                for (Surface surface : request.getTargets()) {
                    if (surface == null) {
                        str2 = "submitRequestList - Null Surface targets are not allowed";
                        Log.e(this.TAG, str2);
                        throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, str2);
                    } else if (this.mConfiguredSurfaces == null) {
                        str2 = "submitRequestList - must configure  device with valid surfaces before submitting requests";
                        Log.e(this.TAG, str2);
                        throw new ServiceSpecificException(LegacyExceptionUtils.INVALID_OPERATION, str2);
                    } else if (!containsSurfaceId(surface, surfaceIds)) {
                        str2 = "submitRequestList - cannot use a surface that wasn't configured";
                        Log.e(this.TAG, str2);
                        throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, str2);
                    }
                }
            }
            this.mIdle.close();
            return this.mRequestThreadManager.submitCaptureRequests(requestList, repeating);
        } catch (BufferQueueAbandonedException e) {
            throw new ServiceSpecificException(LegacyExceptionUtils.BAD_VALUE, "submitRequestList - configured surface is abandoned.");
        }
    }

    public SubmitInfo submitRequest(CaptureRequest request, boolean repeating) {
        return submitRequestList(new CaptureRequest[]{request}, repeating);
    }

    public long cancelRequest(int requestId) {
        return this.mRequestThreadManager.cancelRepeating(requestId);
    }

    public void waitUntilIdle() {
        this.mIdle.block();
    }

    public long flush() {
        long lastFrame = this.mRequestThreadManager.flush();
        waitUntilIdle();
        return lastFrame;
    }

    public boolean isClosed() {
        return this.mClosed;
    }

    public void close() {
        String str = "Thread %s (%d) interrupted while quitting.";
        this.mRequestThreadManager.quit();
        this.mCallbackHandlerThread.quitSafely();
        this.mResultThread.quitSafely();
        try {
            this.mCallbackHandlerThread.join();
        } catch (InterruptedException e) {
            Log.e(this.TAG, String.format(str, new Object[]{this.mCallbackHandlerThread.getName(), Long.valueOf(this.mCallbackHandlerThread.getId())}));
        }
        try {
            this.mResultThread.join();
        } catch (InterruptedException e2) {
            Log.e(this.TAG, String.format(str, new Object[]{this.mResultThread.getName(), Long.valueOf(this.mResultThread.getId())}));
        }
        this.mClosed = true;
    }

    /* Access modifiers changed, original: protected */
    public void finalize() throws Throwable {
        try {
            close();
        } catch (ServiceSpecificException e) {
            String str = this.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Got error while trying to finalize, ignoring: ");
            stringBuilder.append(e.getMessage());
            Log.e(str, stringBuilder.toString());
        } catch (Throwable th) {
            super.finalize();
        }
        super.finalize();
    }

    static long findEuclidDistSquare(Size a, Size b) {
        long d0 = (long) (a.getWidth() - b.getWidth());
        long d1 = (long) (a.getHeight() - b.getHeight());
        return (d0 * d0) + (d1 * d1);
    }

    static Size findClosestSize(Size size, Size[] supportedSizes) {
        if (size == null || supportedSizes == null) {
            return null;
        }
        Size bestSize = null;
        for (Size s : supportedSizes) {
            if (s.equals(size)) {
                return size;
            }
            if (s.getWidth() <= MAX_DIMEN_FOR_ROUNDING && (bestSize == null || findEuclidDistSquare(size, s) < findEuclidDistSquare(bestSize, s))) {
                bestSize = s;
            }
        }
        return bestSize;
    }

    public static Size getSurfaceSize(Surface surface) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        int[] dimens = new int[2];
        LegacyExceptionUtils.throwOnError(nativeDetectSurfaceDimens(surface, dimens));
        return new Size(dimens[0], dimens[1]);
    }

    public static boolean isFlexibleConsumer(Surface output) {
        int usageFlags = detectSurfaceUsageFlags(output);
        return (usageFlags & 1114112) == 0 && (usageFlags & 2307) != 0;
    }

    public static boolean isPreviewConsumer(Surface output) {
        int usageFlags = detectSurfaceUsageFlags(output);
        boolean previewConsumer = (usageFlags & 1114115) == 0 && (usageFlags & 2816) != 0;
        try {
            int surfaceFormat = detectSurfaceType(output);
            return previewConsumer;
        } catch (BufferQueueAbandonedException e) {
            throw new IllegalArgumentException("Surface was abandoned", e);
        }
    }

    public static boolean isVideoEncoderConsumer(Surface output) {
        int usageFlags = detectSurfaceUsageFlags(output);
        boolean videoEncoderConsumer = (usageFlags & 1050883) == 0 && (usageFlags & 65536) != 0;
        try {
            int surfaceFormat = detectSurfaceType(output);
            return videoEncoderConsumer;
        } catch (BufferQueueAbandonedException e) {
            throw new IllegalArgumentException("Surface was abandoned", e);
        }
    }

    static int detectSurfaceUsageFlags(Surface surface) {
        Preconditions.checkNotNull(surface);
        return nativeDetectSurfaceUsageFlags(surface);
    }

    public static int detectSurfaceType(Surface surface) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        int surfaceType = nativeDetectSurfaceType(surface);
        if (surfaceType >= 1 && surfaceType <= 5) {
            surfaceType = 34;
        }
        return LegacyExceptionUtils.throwOnError(surfaceType);
    }

    public static int detectSurfaceDataspace(Surface surface) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        return LegacyExceptionUtils.throwOnError(nativeDetectSurfaceDataspace(surface));
    }

    static void connectSurface(Surface surface) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        LegacyExceptionUtils.throwOnError(nativeConnectSurface(surface));
    }

    static void disconnectSurface(Surface surface) throws BufferQueueAbandonedException {
        if (surface != null) {
            LegacyExceptionUtils.throwOnError(nativeDisconnectSurface(surface));
        }
    }

    static void produceFrame(Surface surface, byte[] pixelBuffer, int width, int height, int pixelFormat) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        Preconditions.checkNotNull(pixelBuffer);
        Preconditions.checkArgumentPositive(width, "width must be positive.");
        Preconditions.checkArgumentPositive(height, "height must be positive.");
        LegacyExceptionUtils.throwOnError(nativeProduceFrame(surface, pixelBuffer, width, height, pixelFormat));
    }

    static void setSurfaceFormat(Surface surface, int pixelFormat) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        LegacyExceptionUtils.throwOnError(nativeSetSurfaceFormat(surface, pixelFormat));
    }

    static void setSurfaceDimens(Surface surface, int width, int height) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        Preconditions.checkArgumentPositive(width, "width must be positive.");
        Preconditions.checkArgumentPositive(height, "height must be positive.");
        LegacyExceptionUtils.throwOnError(nativeSetSurfaceDimens(surface, width, height));
    }

    public static long getSurfaceId(Surface surface) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        try {
            return nativeGetSurfaceId(surface);
        } catch (IllegalArgumentException e) {
            throw new BufferQueueAbandonedException();
        }
    }

    static List<Long> getSurfaceIds(SparseArray<Surface> surfaces) throws BufferQueueAbandonedException {
        if (surfaces != null) {
            List<Long> surfaceIds = new ArrayList();
            int count = surfaces.size();
            int i = 0;
            while (i < count) {
                long id = getSurfaceId((Surface) surfaces.valueAt(i));
                if (id != 0) {
                    surfaceIds.add(Long.valueOf(id));
                    i++;
                } else {
                    throw new IllegalStateException("Configured surface had null native GraphicBufferProducer pointer!");
                }
            }
            return surfaceIds;
        }
        throw new NullPointerException("Null argument surfaces");
    }

    static List<Long> getSurfaceIds(Collection<Surface> surfaces) throws BufferQueueAbandonedException {
        if (surfaces != null) {
            List<Long> surfaceIds = new ArrayList();
            for (Surface s : surfaces) {
                long id = getSurfaceId(s);
                if (id != 0) {
                    surfaceIds.add(Long.valueOf(id));
                } else {
                    throw new IllegalStateException("Configured surface had null native GraphicBufferProducer pointer!");
                }
            }
            return surfaceIds;
        }
        throw new NullPointerException("Null argument surfaces");
    }

    static boolean containsSurfaceId(Surface s, Collection<Long> ids) {
        try {
            return ids.contains(Long.valueOf(getSurfaceId(s)));
        } catch (BufferQueueAbandonedException e) {
            return false;
        }
    }

    static void setSurfaceOrientation(Surface surface, int facing, int sensorOrientation) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        LegacyExceptionUtils.throwOnError(nativeSetSurfaceOrientation(surface, facing, sensorOrientation));
    }

    static Size getTextureSize(SurfaceTexture surfaceTexture) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surfaceTexture);
        int[] dimens = new int[2];
        LegacyExceptionUtils.throwOnError(nativeDetectTextureDimens(surfaceTexture, dimens));
        return new Size(dimens[0], dimens[1]);
    }

    static void setNextTimestamp(Surface surface, long timestamp) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        LegacyExceptionUtils.throwOnError(nativeSetNextTimestamp(surface, timestamp));
    }

    static void setScalingMode(Surface surface, int mode) throws BufferQueueAbandonedException {
        Preconditions.checkNotNull(surface);
        LegacyExceptionUtils.throwOnError(nativeSetScalingMode(surface, mode));
    }
}
