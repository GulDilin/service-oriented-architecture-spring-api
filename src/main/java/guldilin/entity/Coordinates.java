package guldilin.entity;

import guldilin.dto.CoordinatesDTO;
import guldilin.errors.ErrorMessage;
import guldilin.utils.FilterActionType;
import guldilin.utils.FilterableField;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name="coordinates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Coordinates extends AbstractEntity {
    public static List<FilterableField<?>> getFilterableFields() {
        return Arrays.asList(
                new FilterableField<>(Long.class, FilterActionType.COMPARABLE, "id", Long::parseLong),
                new FilterableField<>(Long.class, FilterActionType.COMPARABLE, "x", Long::parseLong),
                new FilterableField<>(Long.class, FilterActionType.COMPARABLE, "y", Long::parseLong)
        );
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "x")
    private Long x;

    @Column(name = "y")
    @Min(value = -667, message = ErrorMessage.MIN_N667)
    private Integer y;

    @Override
    public CoordinatesDTO mapToDTO() {
        return new CoordinatesDTO(this);
    }
}
