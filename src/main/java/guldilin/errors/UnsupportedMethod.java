package guldilin.errors;

public class UnsupportedMethod extends Exception{
    public UnsupportedMethod() {
        super(ErrorMessage.METHOD_NOT_SUPPORTED);
    }

    public UnsupportedMethod(String message) {
        super(message);
    }
}
