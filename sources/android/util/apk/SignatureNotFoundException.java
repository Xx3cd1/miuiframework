package android.util.apk;

public class SignatureNotFoundException extends Exception {
    private static final long serialVersionUID = 1;

    public SignatureNotFoundException(String message) {
        super(message);
    }

    public SignatureNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
