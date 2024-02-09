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
                                <c:choose>
                                    <c:when test="${error=='Forbidden'}">
                                        <p class="error-message">Nie masz uprawnień do tej części aplikacji.</p>
                                    </c:when>
                                    <c:when test="${error=='Not Found'}">
                                        <p class="error-message">Nie znaleziono takiego adresu</p>
                                    </c:when>
                                    <c:when test="${error=='Internal Server Error'}">
                                        <p class="error-message">Wystąpił nieoczekiwany błąd</p>
                                    </c:when>
                                </c:choose>
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