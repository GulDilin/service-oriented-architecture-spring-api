package guldilin.dto;

import lombok.*;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ValidationErrorDTO extends AbstractDTO {
    Map<String, String> message;
    String error;
}
