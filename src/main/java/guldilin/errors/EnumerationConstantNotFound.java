package guldilin.errors;

public class EnumerationConstantNotFound extends Exception{
    public EnumerationConstantNotFound() {
        super(ErrorMessage.ENUM_CONSTANT_NOT_FOUND);
    }

    public EnumerationConstantNotFound(String message) {
        super(message);
    }
}
