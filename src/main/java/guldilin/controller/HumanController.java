package guldilin.controller;


import guldilin.dto.HumanDTO;
import guldilin.entity.Human;
import guldilin.repository.implementation.CrudRepositoryImpl;
import lombok.SneakyThrows;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/api/human/*")
public class HumanController extends HttpServlet {
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
