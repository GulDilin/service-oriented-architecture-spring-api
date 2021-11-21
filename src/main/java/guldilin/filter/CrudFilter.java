package guldilin.filter;


import guldilin.errors.UnsupportedContentType;
import guldilin.errors.UnsupportedMethod;
import guldilin.errors.ValidationException;
import guldilin.errors.ErrorMessage;
import lombok.SneakyThrows;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

@WebFilter({"/api/coordinates/*", "/api/city/*", "/api/human/*"})
public class CrudFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @SneakyThrows
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String method = request.getMethod().toLowerCase(Locale.ROOT);
        String contendType = "application/json";
        if (Arrays.asList("post", "put").contains(method) &&
                (request.getContentType() == null ||
                        !request.getContentType().equals(contendType))) throw new UnsupportedContentType();

        if (Arrays.asList("delete", "put").contains(method) &&
                request.getPathInfo() == null) {
            throw new UnsupportedMethod();
        }

        if (method.equals("post") && request.getPathInfo() != null) {
            throw new UnsupportedMethod();
        }

        if (Arrays.asList("get", "delete", "put").contains(method) && request.getPathInfo() != null) {
            try {
                Integer id = Integer.parseInt(request.getPathInfo().replaceAll("^/", ""));
                request.setAttribute("id", id);
            } catch (NumberFormatException e) {
                HashMap<String, String> errorsMap = new HashMap<>();
                errorsMap.put("id", ErrorMessage.IS_INTEGER);
                throw new ValidationException(errorsMap);
            }
        }
        response.setContentType(contendType);

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
