package guldilin.controller;


import guldilin.dto.CoordinatesDTO;
import guldilin.dto.EntityListDTO;
import guldilin.entity.Coordinates;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value="/api/coordinates", produces = "application/json")
public class CoordinatesController {
    private final CrudController<Coordinates, CoordinatesDTO> crudController;

    public CoordinatesController() {
        CrudRepositoryImpl<Coordinates> repository = new CrudRepositoryImpl<>(Coordinates.class);
        crudController = new CrudController<>(
                Coordinates.class,
                CoordinatesDTO.class,
                repository,
                Coordinates::getFilterableFields,
                this::mapToEntity);
    }

    private Coordinates mapToEntity(CoordinatesDTO dto) {
        return Coordinates.builder()
                .id(dto.getId())
                .x(dto.getX())
                .y(dto.getY())
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
        return crudController.getItems(limit, offset, sorting, request);
    }

    @SneakyThrows
    @GetMapping("{id}")
    public CoordinatesDTO getItemById(@PathVariable Integer id) {
        return (CoordinatesDTO) crudController.getById(id);
    }

    @SneakyThrows
    @PostMapping()
    public CoordinatesDTO createItem(@RequestBody CoordinatesDTO coordinatesDTO) {
        return (CoordinatesDTO) crudController.createItem(coordinatesDTO);
    }

    @SneakyThrows
    @PutMapping("{id}")
    public CoordinatesDTO replaceItem(@PathVariable Integer id, @RequestBody CoordinatesDTO coordinatesDTO) {
        return (CoordinatesDTO) crudController.replaceItem(id, coordinatesDTO);
    }

    @SneakyThrows
    @DeleteMapping("{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Integer id) {
        crudController.deleteItem(id);
    }
}
