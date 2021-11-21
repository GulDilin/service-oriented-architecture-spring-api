package guldilin.entity;

import guldilin.dto.HumanDTO;
import guldilin.errors.ErrorCode;
import guldilin.errors.ValidationException;
import guldilin.utils.DateParserFactory;
import guldilin.utils.FilterActionType;
import guldilin.utils.FilterableField;
import lombok.*;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity(name="human")
@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Human extends AbstractEntity {
    public static List<FilterableField<?>> getFilterableFields() {
        final String dateFormat = "yyyy-MM-dd.HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        return Arrays.asList(
                new FilterableField<>(Integer.class, FilterActionType.COMPARABLE, "id", Integer::parseInt),
                new FilterableField<>(Date.class, FilterActionType.COMPARABLE, "birthday",
                        s -> DateParserFactory.parseDate(s, "birthday"))
        );
    }
    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "birthday")
    private Date birthday;

    @Override
    public HumanDTO mapToDTO() {
        return new HumanDTO(this);
    }
}
