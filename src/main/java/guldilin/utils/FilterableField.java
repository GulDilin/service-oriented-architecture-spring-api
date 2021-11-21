package guldilin.utils;

import lombok.*;

import java.util.function.Function;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FilterableField<T> {
    private Class<T> tClass;
    private FilterActionType actionType;
    private String name;
    private Function<String, T> parser = s -> (T) s;
}
