<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html lang="pl">

<!-- Header -->
<%@ include file="header.jsp" %>
<!-- End of Header -->

<body class="bg-gradient-primary">

<div class="container">

    <!-- Outer Row -->
    <div class="row justify-content-center">

        <div class="col-xl-10 col-lg-12 col-md-9">

            <div class="card o-hidden border-0 shadow-lg my-5">
                <div class="card-body p-2">
                    <!-- Nested Row within Card Body -->
                    <div class="row">
                        <div class="col-lg-12">
                            <div class="p-5">
                                <div class="text-center">
                                    <h1 class="h4 text-gray-900 mb-4">Witaj, zaloguj się!</h1>
                                </div>
                                <form:form class="user"
                                           action="${pageContext.request.contextPath}/login"
                                           method="POST"
                                           modelAttribute="user">
                                    <div class="form-group">
                                        <form:input class="form-control form-control-user"
                                                    placeholder="Wpisz login..."
                                                    path="username"/>
                                    </div>
                                    <div class="form-group">
                                        <form:password class="form-control form-control-user"
                                                       placeholder="Podaj hasło..."
                                                       path="password"/>
                                    </div>
                                    <button type="submit" class="btn btn-primary btn-user btn-block"
                                            style="font-size: 1.1rem;">Zaloguj się
                                    </button>
                                </form:form>
                                <c:if test="${param.error != null}">
                                    <p class="validation" style="font-size: 0.8rem">
                                        Niepoprawny email lub hasło, spróbuj ponownie
                                    </p>
                                </c:if>
                                <c:if test="${param.logout != null}">
                                    <p style="font-size: 0.8rem; color: green">
                                        Wylogowano pomyślnie
                                    </p>
                                </c:if>
                                <hr>
                                <div class="text-center">
                                    <a class="small" href="${pageContext.request.contextPath}/register">Stwórz
                                        nowe
                                        konto</a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>

    </div>

</div>

<!-- Footer -->
<%@ include file="footer.jsp" %>
<!-- End of Footer -->

</body>

</html>