package guldilin.errors;

public class EntryNotFound extends Exception{
    public EntryNotFound() {
        super(ErrorMessage.NOT_FOUND);
    }

    public EntryNotFound(String m) {
        super(m);
    }
}
