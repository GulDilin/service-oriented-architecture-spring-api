package guldilin.controller;


import guldilin.dto.CoordinatesDTO;
import guldilin.entity.Coordinates;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/coordinates/*")
public class CoordinatesController extends HttpServlet {
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
