package guldilin.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
public class ValidationException extends Exception{
    private HashMap<String, String> fieldsErrors;
}
