package guldilin.controller;

import guldilin.dto.CityDTO;
import guldilin.entity.City;
import guldilin.entity.Coordinates;
import guldilin.entity.Human;
import guldilin.errors.ErrorMessage;
import guldilin.errors.ValidationException;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Objects;

@WebServlet("/api/city/*")
public class CityController extends HttpServlet {
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        crudController.doGet(request, response);
    }

    @SneakyThrows
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        crudController.doPost(request, response);
    }

    @SneakyThrows
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) {
        crudController.doPut(request, response);
    }

    @SneakyThrows
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
        crudController.doDelete(request);
    }
}
