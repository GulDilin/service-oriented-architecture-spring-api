package guldilin.errors;

public class UnsupportedContentType extends Exception {
    public UnsupportedContentType() {
        super(ErrorMessage.CONTENT_TYPE_NOT_SUPPORTED);
    }

    public UnsupportedContentType(String message) {
        super(message);
    }
}
