package pl.rarytas.rarytas_restaurantside.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.User;

import java.io.IOException;


@Slf4j
@WebFilter(filterName = "AdminFilter", urlPatterns = {"/restaurant/cms/*"})
public class AdminFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        log.info("Initializing authorisation filter");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        HttpSession session = request.getSession();

        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user.isAdmin()) {
                chain.doFilter(request, response);
            } else {
                log.error(user.getEmail() + " tried to access restaurant CMS - access denied");
                response.sendRedirect(request.getContextPath() + "/restaurant");
            }
        } else {
            log.debug("No user session found, redirecting to login page");
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
