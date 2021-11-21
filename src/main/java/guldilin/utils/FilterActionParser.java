package guldilin.utils;

import guldilin.errors.FilterTypeNotFound;
import guldilin.errors.FilterTypeNotSupported;

import java.util.Arrays;

public class FilterActionParser {
    public static FilterAction parse(String actionString, FilterableField<?> filterableField)
            throws FilterTypeNotFound, FilterTypeNotSupported {
        String[] parts = actionString.split(":", 2);
        FilterActionType actionType = filterableField.getActionType();
        FilterType filterType;
        String value;
        if (!actionString.contains(":")) {
            filterType = FilterType.EQUALS;
            value = parts.length < 1 ? "" : parts[0];
        } else {
            value = parts.length < 2 ? "" : parts[1];
            filterType = Arrays.stream(FilterType.values())
                    .filter(e -> e.getKey().equals(parts[0]))
                    .findFirst()
                    .orElseThrow(FilterTypeNotFound::new);

        }
        switch (actionType) {
            case COMPARABLE:
                if (filterType == FilterType.CONTAINS)
                    throw new FilterTypeNotSupported();
                break;
            case CONTAINS:
                if (filterType != FilterType.EQUALS && filterType != FilterType.CONTAINS)
                    throw new FilterTypeNotSupported();
                break;
            case EQUAL_ONLY:
                if (filterType != FilterType.EQUALS) throw new FilterTypeNotSupported();
                break;
        }
        return FilterAction.builder()
                .filterType(filterType)
                .filterableField(filterableField)
                .value(value)
                .build();
    }
}
