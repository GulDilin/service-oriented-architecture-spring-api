package guldilin.dto;

import guldilin.entity.City;
import guldilin.entity.Climate;
import lombok.*;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityDTO extends AbstractDTO {
    private Integer id;
    private String name;
    private Integer coordinates;
    private Date creationDate;
    private Climate climate;
    private Integer area;
    private Integer population;
    private Float metersAboveSeaLevel;
    private Integer populationDensity;
    private Integer carCode;
    private Integer governor;

    public CityDTO(City city) {
        this.id = city.getId();
        this.name = city.getName();
        this.creationDate = city.getCreationDate();
        this.climate = city.getClimate();
        this.area = city.getArea();
        this.population = city.getPopulation();
        this.metersAboveSeaLevel = city.getMetersAboveSeaLevel();
        this.populationDensity = city.getPopulationDensity();
        this.carCode = city.getCarCode();

        if (city.getCoordinates() != null) {
            this.coordinates = city.getCoordinates().getId();
        }
        if (city.getGovernor() != null) {
            this.governor = city.getGovernor().getId();
        }
    }
}
