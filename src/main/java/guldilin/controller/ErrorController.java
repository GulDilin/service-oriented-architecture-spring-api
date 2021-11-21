package guldilin.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import guldilin.dto.ErrorDTO;
import guldilin.dto.ValidationErrorDTO;
import guldilin.errors.*;
import guldilin.utils.GsonFactoryBuilder;

import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ErrorController extends HttpServlet {

    private final Gson gson;
    private final Map<String, ErrorCode> errorsMap;
    private final Map<String, Integer> statusesMap;

    public ErrorController() {
        this.gson = GsonFactoryBuilder.getGson();
        this.errorsMap = ErrorCodesFactory.getErrorCodesMap();
        this.statusesMap = new HashMap<>();

        this.statusesMap.put(UnsupportedMethod.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(UnsupportedContentType.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(EntryNotFound.class.getName(), HttpServletResponse.SC_NOT_FOUND);
        this.statusesMap.put(EntityNotFoundException.class.getName(), HttpServletResponse.SC_NOT_FOUND);
        this.statusesMap.put(ResourceNotFound.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(FilterTypeNotFound.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(FilterTypeNotSupported.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(UnknownFilterType.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(javax.persistence.NoResultException.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(NumberFormatException.class.getName(), HttpServletResponse.SC_BAD_REQUEST);
        this.statusesMap.put(EnumerationConstantNotFound.class.getName(), HttpServletResponse.SC_BAD_REQUEST);


    }

    protected Object handleValidationError(HttpServletResponse response, Throwable throwable) {
        ValidationException validationException = (ValidationException) throwable;
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ValidationErrorDTO.builder()
                .error(ErrorCode.VALIDATION_ERROR.name())
                .message(validationException.getFieldsErrors())
                .build();
    }

    protected Object handleConstraintViolationException(HttpServletResponse response, Throwable throwable) {
        ConstraintViolationException validationError = (ConstraintViolationException) throwable;
        Map<String, String> validationErrors = new HashMap<>();
        validationError.getConstraintViolations().forEach(
                c -> validationErrors.put(c.getPropertyPath().toString(), c.getMessage()));
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ValidationErrorDTO.builder()
                .error(ErrorCode.VALIDATION_ERROR.name())
                .message(validationErrors)
                .build();
    }

    protected Object handleDefaultError(HttpServletRequest request, HttpServletResponse response,
                                  Throwable throwable, String errorName) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        throwable.printStackTrace();

        if (this.statusesMap.containsKey(errorName)) statusCode = this.statusesMap.get(errorName);
        if (this.errorsMap.containsKey(errorName)) errorCode = this.errorsMap.get(errorName);
        response.setStatus(statusCode);
        return ErrorDTO.builder()
                .error(errorCode.name())
                .message(throwable.getMessage())
                .build();
    }

    protected Object handlePersistenceException(HttpServletRequest request, HttpServletResponse response,
                                  Throwable throwable)
            throws IOException {
        return handleCauseException(request, response, throwable);
    }

    protected Object handleCauseException(HttpServletRequest request, HttpServletResponse response,
                                                Throwable throwable)
            throws IOException {
        Exception causedException = (Exception) throwable;
        if (causedException.getCause() != null) return handleException(request, response, causedException.getCause());
        else return handleDefaultError(request, response, throwable, throwable.getClass().getName());
    }

    protected Object handleJsonException(HttpServletResponse response,
                                                Throwable throwable) {
        throwable.printStackTrace();
        JsonSyntaxException jsonSyntaxException = (JsonSyntaxException) throwable;
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        ErrorCode code = ErrorCode.JSON_SYNTAX_ERROR;
        String cause = jsonSyntaxException.getCause().getClass().getName();
        if (this.errorsMap.containsKey(cause)) code = errorsMap.get(cause);
        return ErrorDTO.builder()
                .error(code.name())
                .message(jsonSyntaxException.getCause().getMessage())
                .build();
    }


    protected Object handleException(HttpServletRequest request, HttpServletResponse response,
                                          Throwable throwable) throws IOException {
        String errorName = throwable.getClass().getName();
        switch (errorName) {
            case "guldilin.errors.ValidationException":
                return handleValidationError(response, throwable);
            case "javax.validation.ConstraintViolationException":
                return handleConstraintViolationException(response, throwable);
            case "javax.persistence.PersistenceException":
                return handlePersistenceException(request, response, throwable);
            case "com.google.gson.JsonSyntaxException":
                return handleJsonException(response, throwable);
            case "java.lang.IllegalArgumentException":
                return handleCauseException(request, response, throwable);
            default:
                return handleDefaultError(request, response, throwable, errorName);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Throwable throwable = (Throwable) request.getAttribute("javax.servlet.error.exception");
        Object dto = handleException(request, response, throwable);
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(dto));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doGet(req, resp);
    }
}
