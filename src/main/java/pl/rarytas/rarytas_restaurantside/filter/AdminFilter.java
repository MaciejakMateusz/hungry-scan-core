package pl.rarytas.rarytas_restaurantside.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import pl.rarytas.rarytas_restaurantside.entity.User;

import java.io.IOException;
import java.util.Enumeration;


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
            boolean userIsAdmin = user.isAdmin();

            if (userIsAdmin) {
                chain.doFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/restaurant");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
