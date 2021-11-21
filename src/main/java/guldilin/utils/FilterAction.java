package guldilin.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FilterAction {
    private FilterableField filterableField;
    private FilterType filterType;
    private String value;
}
