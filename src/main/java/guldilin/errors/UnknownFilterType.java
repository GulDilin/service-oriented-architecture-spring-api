package guldilin.errors;

public class UnknownFilterType extends Exception{
    public UnknownFilterType() {
        super(ErrorMessage.FILTER_TYPE_NOT_FOUND);
    }

    public UnknownFilterType(String message) {
        super(message);
    }
}
