<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- Header -->
<%@ include file="../header.jsp" %>
<!-- End of Header -->

<div id="content-wrapper" class="d-flex flex-column">
    <!-- Main Content -->
    <div id="content">
        <!-- Topbar -->
        <nav class="navbar navbar-expand navbar-light bg-white topbar mb-4 static-top shadow">
            <!-- Sidebar Toggle (Topbar) -->
            <button id="sidebarToggleTop" class="btn btn-link d-md-none rounded-circle mr-3">
                <i class="fa fa-bars"></i>
            </button>
        </nav>
        <!-- End of Topbar -->
        <!-- Begin Page Content -->
        <div class="container-fluid">
            <!-- Page Heading -->
            <div class="d-sm-flex align-items-center justify-content-between mb-4">
                <a href="${pageContext.request.contextPath}/admin/users">
                    <button type="submit"
                            style="outline: none; font-size: 1.1rem;"
                            class="button-list">
                        Powrót
                    </button>
                </a>
            </div>
            <!-- /.container-fluid -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Dodaj użytkownika</h6>
                </div>
                <div class="card-body">
                    <form:form method="POST"
                               action="/admin/users/add"
                               modelAttribute="user">
                        <div class="form-group">
                            <label for="name">Login <br>
                                <form:input path="username"
                                            class="form-control"/>
                            </label>
                            <form:errors path="username" cssClass="validation"/>
                        </div>
                        <div class="form-group">
                            <label for="name">Imię <br>
                                <form:input path="name"
                                            class="form-control"/>
                            </label>
                            <form:errors path="name" cssClass="validation"/>
                        </div>
                        <div class="form-group">
                            <label for="name">Nazwisko <br>
                                <form:input path="surname"
                                            class="form-control"/>
                            </label>
                            <form:errors path="surname" cssClass="validation"/>
                        </div>
                        <div class="form-group">
                            <label for="email">Email <br>
                                    <form:input path="email"
                                                class="form-control"/>
                                    <form:errors path="email" cssClass="validation"/>
                                <c:if test="${emailExists==true}">
                                <p class="validation" style="font-size: 0.7rem">Użytkownik z podanym adresem email już istnieje.</p>
                                </c:if>
                        </div>
                        <div class="form-group">
                            <label for="roles">Role i uprawnienia:</label>
                            <div class="roles-checkboxes">
                                <form:checkboxes path="roles"
                                             items="${roles}"
                                             itemValue="id"
                                             itemLabel="displayedName"/>
                            </div>
                            <form:errors path="roles" cssClass="validation"/>
                        </div>
                        <div class="form-group">
                            <label for="phoneNumber">Numer telefonu <br>
                                    <form:input path="phoneNumber"
                                                class="form-control"/>
                                    <form:errors path="phoneNumber" cssClass="validation"/>
                        </div>
                        <div class="form-group">
                            <label for="password">Hasło <br>
                                <form:password path="password"
                                               class="form-control"/>
                            </label>
                            <form:errors cssClass="validation" path="password"/>
                        </div>
                        <div class="form-group">
                            <label for="repeatedPassword">Powtórz hasło <br>
                                <form:password path="repeatedPassword"
                                               class="form-control"/>
                            </label>
                            <c:if test="${passwordsNotMatch==true}">
                                <p class="validation" style="font-size: 0.7rem">Hasła nie są identyczne</p>
                            </c:if>
                        </div>
                        <button type="submit" class="btn btn-primary">Zapisz</button>
                    </form:form>
                    <c:if test="${isCreated==true}">
                        <p class="confirmation">Użytkownik utworzony pomyślnie.</p>
                    </c:if>
                </div>
            </div>
        </div>
        <!-- End of Main Content -->
    </div>

    <!-- Footer -->
    <%@ include file="../footer.jsp" %>
    <!-- End of Footer -->

    <!-- End of Content -->
</div>
<!-- End of Content Wrapper -->
</div>
<!-- End of Page Wrapper -->

</body>
</html>