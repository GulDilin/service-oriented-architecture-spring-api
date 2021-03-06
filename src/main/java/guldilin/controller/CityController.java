package guldilin.controller;

import guldilin.dto.CityDTO;
import guldilin.dto.EntityListDTO;
import guldilin.entity.City;
import guldilin.entity.Coordinates;
import guldilin.entity.Human;
import guldilin.errors.ErrorMessage;
import guldilin.errors.ValidationException;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Objects;

@RestController
@RequestMapping(value = "/api/city", produces = "application/json")
public class CityController {
    private final CrudRepositoryImpl<Human> repositoryHuman;
    private final CrudRepositoryImpl<Coordinates> repositoryCoordinates;
    private final CrudController<City, CityDTO> crudController;

    public CityController() {
        CrudRepositoryImpl<City> repository = new CrudRepositoryImpl<>(City.class);
        repositoryHuman = new CrudRepositoryImpl<>(Human.class);
        repositoryCoordinates = new CrudRepositoryImpl<>(Coordinates.class);
        crudController = new CrudController<>(
                City.class,
                CityDTO.class,
                repository,
                City::getFilterableFields,
                this::mapToEntity);
    }

    @SneakyThrows
    private City mapToEntity(CityDTO dto) {
        return City.builder()
                .id(dto.getId())
                .name(dto.getName())
                .coordinates(Objects.isNull(dto.getCoordinates()) ? null : repositoryCoordinates
                        .findById(dto.getCoordinates()).orElseThrow(() -> {
                            HashMap<String, String> errorsMap = new HashMap<>();
                            errorsMap.put("coordinates", ErrorMessage.NOT_FOUND);
                            return new ValidationException(errorsMap);
                        }))
                .area(dto.getArea())
                .population(dto.getPopulation())
                .metersAboveSeaLevel(dto.getMetersAboveSeaLevel())
                .populationDensity(dto.getPopulationDensity())
                .carCode(dto.getCarCode())
                .climate(dto.getClimate())
                .governor(Objects.isNull(dto.getGovernor()) ? null : repositoryHuman
                        .findById(dto.getGovernor()).orElseThrow(() -> {
                            HashMap<String, String> errorsMap = new HashMap<>();
                            errorsMap.put("governor", ErrorMessage.NOT_FOUND);
                            return new ValidationException(errorsMap);
                        }))
                .build();
    }

    @SneakyThrows
    @GetMapping
    public EntityListDTO getItems(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false) String[] sorting,
            HttpServletRequest request
    ) {
        System.out.println("Get all request"); // to check load balancing
        return crudController.getItems(limit, offset, sorting, request);
    }

    @SneakyThrows
    @GetMapping("/{id}")
    public CityDTO getItemById(@PathVariable Integer id) {
        //TODO: refactor with creation mapToDto method in controller to avoid cast
        return(CityDTO) crudController.getById(id);
    }

    @SneakyThrows
    @PostMapping()
    public CityDTO createItem(@RequestBody CityDTO cityDTO) {
        return (CityDTO) crudController.createItem(cityDTO);
    }

    @SneakyThrows
    @PutMapping("/{id}")
    public CityDTO replaceItem(@PathVariable Integer id, @RequestBody CityDTO cityDTO) {
        return (CityDTO) crudController.replaceItem(id, cityDTO);
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Integer id) {
        crudController.deleteItem(id);
    }
}
