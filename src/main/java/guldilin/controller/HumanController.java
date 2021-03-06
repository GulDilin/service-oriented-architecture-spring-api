package guldilin.controller;


import guldilin.dto.EntityListDTO;
import guldilin.dto.HumanDTO;
import guldilin.entity.Human;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "/api/human", produces = "application/json")
public class HumanController {
    private final CrudController<Human, HumanDTO> crudController;

    public HumanController() {
        CrudRepositoryImpl<Human> repository = new CrudRepositoryImpl<>(Human.class);
        crudController = new CrudController<>(
                Human.class,
                HumanDTO.class,
                repository,
                Human::getFilterableFields,
                this::mapToEntity);
    }

    private Human mapToEntity(HumanDTO dto) {
        return Human.builder()
                .id(dto.getId())
                .birthday(dto.getBirthday())
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
    @GetMapping("/{id}")
    public HumanDTO getItemById(@PathVariable Integer id) {
        return (HumanDTO) crudController.getById(id);
    }

    @SneakyThrows
    @PostMapping()
    public HumanDTO createItem(@RequestBody HumanDTO humanDTO) {
        return (HumanDTO) crudController.createItem(humanDTO);
    }

    @SneakyThrows
    @PutMapping("/{id}")
    public HumanDTO replaceItem(@PathVariable Integer id, @RequestBody HumanDTO humanDTO) {
        return (HumanDTO) crudController.replaceItem(id, humanDTO);
    }

    @SneakyThrows
    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteItem(@PathVariable Integer id) {
        crudController.deleteItem(id);
    }
}
