package android.drm;

public class DrmConvertedStatus {
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_INPUTDATA_ERROR = 2;
    public static final int STATUS_OK = 1;
    public final byte[] convertedData;
    public final int offset;
    public final int statusCode;

    public DrmConvertedStatus(int statusCode, byte[] convertedData, int offset) {
        if (isValidStatusCode(statusCode)) {
            this.statusCode = statusCode;
            this.convertedData = convertedData;
            this.offset = offset;
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Unsupported status code: ");
        stringBuilder.append(statusCode);
        throw new IllegalArgumentException(stringBuilder.toString());
    }

    private boolean isValidStatusCode(int statusCode) {
        return statusCode == 1 || statusCode == 2 || statusCode == 3;
    }
}
