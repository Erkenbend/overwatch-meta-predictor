package predictor.exception;


public class ResourceReaderException extends Exception {
    public ResourceReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceReaderException(String message) {
        super(message);
    }
}