package guldilin.entity;

import guldilin.dto.CityDTO;
import guldilin.errors.ErrorMessage;
import guldilin.errors.ValidationException;
import guldilin.utils.DateParserFactory;
import guldilin.utils.FilterActionType;
import guldilin.utils.FilterableField;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "city")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class City extends AbstractEntity {
    public static List<FilterableField<?>> getFilterableFields() {
        return Arrays.asList(
                new FilterableField<>(Long.class, FilterActionType.COMPARABLE, "id", Long::parseLong),
                new FilterableField<>(String.class, FilterActionType.CONTAINS, "name", s -> s),
                new FilterableField<>(Date.class, FilterActionType.COMPARABLE, "creationDate",
                        s -> DateParserFactory.parseDate(s, "creationDate")),
                new FilterableField<>(Float.class, FilterActionType.COMPARABLE, "metersAboveSeaLevel", Float::parseFloat),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "area", Integer::parseInt),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "population", Integer::parseInt),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "populationDensity", Integer::parseInt),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "carCode", Integer::parseInt),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "coordinates", Integer::parseInt),
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "governor", Integer::parseInt),
                new FilterableField<>(Climate.class, FilterActionType.EQUAL_ONLY, "climate", s -> {
                    try {
                        return Climate.valueOf(s);
                    } catch (IllegalArgumentException exc) {
                        HashMap<String, String> errors = new HashMap<>();
                        errors.put("climate", ErrorMessage.ENUM_CONSTANT_NOT_FOUND);
                        exc.initCause(new ValidationException(errors));
                        throw exc;
                    }
                })
        );
    }

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = ErrorMessage.NOT_BLANK)
    private String name;

    @ManyToOne
    @JoinColumn(name = "coordinates_id", nullable = false)
    private Coordinates coordinates;

    @Column(name = "created", nullable = false, updatable = false)
    @CreationTimestamp
    private Timestamp creationDate;


    @Column(name = "area", nullable = false)
    @NotNull(message = ErrorMessage.NOT_NULL)
    @Min(value = 0, message = ErrorMessage.MIN_0)
    private Integer area;

    @Column(name = "population", nullable = false)
    @NotNull(message = ErrorMessage.NOT_NULL)
    @Min(value = 0, message = ErrorMessage.MIN_0)
    private Integer population;

    @Column(name = "meters_above_sea_level")
    private Float metersAboveSeaLevel;

    @Column(name = "population_density")
    @Min(value = 0, message = ErrorMessage.MIN_0)
    private Integer populationDensity;

    @Column(name = "car_code")
    @Min(value = 0, message = ErrorMessage.MIN_0)
    @Max(value = 1000, message = ErrorMessage.MAX_1000)
    private Integer carCode;

    @Column(name = "climate", nullable = false)
    @NotNull(message = ErrorMessage.NOT_NULL)
    @Enumerated(EnumType.STRING)
    private Climate climate;

    @ManyToOne
    @JoinColumn(name = "governor_id")
    private Human governor;

    @Override
    public CityDTO mapToDTO() {
        return new CityDTO(this);
    }
}
