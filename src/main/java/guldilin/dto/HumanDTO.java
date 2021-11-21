package guldilin.dto;

import guldilin.entity.Human;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HumanDTO extends AbstractDTO {
    private Integer id;
    private Date birthday;

    public HumanDTO(Human human) {
        this.id = human.getId();
        this.birthday = human.getBirthday();
    }
}
