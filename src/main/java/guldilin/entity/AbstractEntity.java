package guldilin.entity;

import guldilin.utils.FilterableField;
import lombok.*;

import java.util.Collections;
import java.util.List;

@Data
public abstract class AbstractEntity implements Mappable, Filterable {
    public static List<FilterableField<?>> getFilterableFields() {
        return Collections.emptyList();
    }

    private Integer id;

}
