package guldilin.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FilterType {
    LESS_THAN("lt"),
    GREATER_THAN("gt"),
    GREATER_THAN_OR_EQUALS("gte"),
    LESS_THAN_OR_EQUALS("lte"),
    EQUALS("eq"),
    CONTAINS("in"),
    IS_NULL("isnull");

    private final String key;
}
