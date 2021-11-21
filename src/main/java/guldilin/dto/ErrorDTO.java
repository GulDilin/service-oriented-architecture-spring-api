package guldilin.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO extends AbstractDTO {
    String message;
    String error;
}
