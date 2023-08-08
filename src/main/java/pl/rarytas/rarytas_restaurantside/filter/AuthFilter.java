package pl.rarytas.rarytas_restaurantside.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Enumeration;


@Slf4j
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/restaurant/*"})
public class AuthFilter implements Filter {

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

            Enumeration<String> attributeNames = session.getAttributeNames();

            boolean userAttributeFound = false;

            while (attributeNames.hasMoreElements()) {
                String attributeName = attributeNames.nextElement();

                if ("user".equals(attributeName)) {
                    userAttributeFound = true;
                    break;
                }
            }

            if (userAttributeFound) {
                chain.doFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login");
            }

        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
