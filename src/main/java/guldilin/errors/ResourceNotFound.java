package guldilin.errors;

public class ResourceNotFound extends Exception{
    public ResourceNotFound() {
        super(ErrorMessage.NOT_FOUND);
    }

    public ResourceNotFound(String message) {
        super(message);
    }
}
