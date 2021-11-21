package guldilin.errors;

public class FilterTypeNotFound extends Exception{
    public FilterTypeNotFound() {
        super(ErrorMessage.FILTER_TYPE_NOT_FOUND);
    }

    public FilterTypeNotFound(String message) {
        super(message);
    }
}
