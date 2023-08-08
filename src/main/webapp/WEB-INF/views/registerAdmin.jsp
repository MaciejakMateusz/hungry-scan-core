<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
                                    <h1 class="h4 text-gray-900 mb-4">Rejestracja nowego administratora</h1>
                                </div>
                                <form:form class="user"
                                           action="${pageContext.request.contextPath}/registerAdmin"
                                           method="POST"
                                           modelAttribute="user">
                                    <form:hidden path="admin" value="true"/>
                                    <div class="form-group">
                                        <form:input
                                                path="firstName"
                                                class="form-control form-control-user"
                                                placeholder="Wpisz imię..."/>
                                        <form:errors path="firstName" cssClass="validation"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input
                                                path="lastName"
                                                class="form-control form-control-user"
                                                placeholder="Wpisz nazwisko..."/>
                                        <form:errors path="lastName" cssClass="validation"/>
                                    </div>
                                    <div class="form-group">
                                        <form:input
                                                path="email"
                                                type="email"
                                                class="form-control form-control-user"
                                                aria-describedby="emailHelp"
                                                placeholder="Wpisz email..."/>
                                        <form:errors path="email" cssClass="validation"/>
                                        <c:if test="${userExists==true}">
                                            <p class="validation">Użytkownik z podanym adresem email już istnieje.</p>
                                        </c:if>
                                    </div>
                                    <div class="form-group">
                                        <form:password
                                                path="password"
                                                class="form-control form-control-user"
                                                placeholder="Wpisz hasło..."/>
                                        <form:errors path="password" cssClass="validation"/>
                                    </div>
                                    <div class="form-group">
                                        <form:password
                                                path="repeatedPassword"
                                                class="form-control form-control-user"
                                                placeholder="Powtórz hasło..."/>
                                        <c:if test="${passwordsNotMatch==true}">
                                            <p class="validation">Hasła nie są identyczne.</p>
                                        </c:if>
                                    </div>
                                    <button
                                            type="submit"
                                            class="btn btn-primary btn-user btn-block"
                                            style="font-size: 1.1rem">
                                        Zarejestruj administratora
                                    </button>
                                </form:form>
                                <hr>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>