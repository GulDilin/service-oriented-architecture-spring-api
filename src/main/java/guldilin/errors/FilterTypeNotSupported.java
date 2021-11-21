package guldilin.errors;

public class FilterTypeNotSupported extends Exception{
    public FilterTypeNotSupported() {
        super(ErrorMessage.FILTER_TYPE_NOT_SUPPORTED);
    }

    public FilterTypeNotSupported(String message) {
        super(message);
    }
}
