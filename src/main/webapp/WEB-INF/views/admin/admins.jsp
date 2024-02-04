<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!-- Header -->
<%@ include file="header.jsp" %>
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
                <a href="${pageContext.request.contextPath}/admin/users/add"
                   class="d-none d-sm-inline-block btn btn-sm btn-primary shadow-sm"
                   style="font-size: 1.1rem;"><i
                        class="fas fa-download fa-sm text-white-50"></i> Dodaj użytkownika
                </a>
            </div>
            <!-- /.container-fluid -->
            <div class="card shadow mb-4">
                <div class="card-header py-3">
                    <h6 class="m-0 font-weight-bold text-primary">Wszyscy użytkownicy</h6>
                </div>
                <div class="card-body">
                    <div class="table-responsive">
                        <div id="dataTable_wrapper" class="dataTables_wrapper dt-bootstrap4">
                            <div class="row">
                                <div class="col-sm-12">
                                    <table class="table table-bordered dataTable" id="dataTable" width="100%"
                                           cellspacing="0" role="grid" aria-describedby="dataTable_info"
                                           style="width: 100%; border-collapse: collapse; border-left: none;">
                                        <thead>
                                        <tr role="row">
                                            <th tabindex="0" aria-controls="dataTable"
                                                rowspan="1" colspan="1"
                                                aria-label="Name: activate to sort column descending"
                                                style="width: auto; text-align: center" aria-sort="ascending">Id
                                            </th>
                                            <th tabindex="0" aria-controls="dataTable" rowspan="1"
                                                colspan="1"
                                                aria-label="Position: activate to sort column ascending"
                                                style="width: auto">Nazwa użytkownika
                                            </th>
                                            <th tabindex="0" aria-controls="dataTable" rowspan="1"
                                                colspan="1"
                                                aria-label="Office: activate to sort column ascending"
                                                style="width: auto">Email
                                            </th>
                                            <th tabindex="0" aria-controls="dataTable" rowspan="1"
                                                colspan="1"
                                                aria-label="Office: activate to sort column ascending"
                                                style="width: auto">Rola
                                            </th>
                                            <th tabindex="0" aria-controls="dataTable" rowspan="1"
                                                colspan="1" aria-label="Age: activate to sort column ascending"
                                                style="width: auto;">Akcja
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach items="${users}" var="user">
                                            <tr role="row">
                                                <td style="text-align: center"><c:out
                                                        value='${user.id}'/></td>
                                                <td><c:out value='${user.username}'/></td>
                                                <td><c:out value='${user.email}'/></td>
                                                <td><c:out value='${user.roles.toString()}'/></td>
                                                <td>
                                                    <form style="all: unset;"
                                                          method="POST"
                                                          action="${pageContext.request.contextPath}/admin/users/show">
                                                        <input type="hidden" value="${user.id}" name="id">
                                                        <button type="submit"
                                                                style="outline: none;"
                                                                class="button-list">
                                                            Pokaż
                                                        </button>
                                                    </form>
                                                    <form style="all: unset;"
                                                          method="POST"
                                                          action="${pageContext.request.contextPath}/admin/users/edit">
                                                        <button
                                                                type="submit"
                                                                style="outline: none;"
                                                                class="button-list"
                                                                name="id"
                                                                value="${user.id}">
                                                            Edytuj
                                                        </button>
                                                    </form>
                                                    <c:if test="${currentAdminId!=user.id}">
                                                        <form style="all: unset;"
                                                              method="POST"
                                                              action="${pageContext.request.contextPath}/admin/users/delete">
                                                            <button
                                                                    type="submit"
                                                                    style="outline: none; background: tomato;"
                                                                    class="button-list"
                                                                    name="id"
                                                                    value="${user.id}">
                                                                Usuń
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- End of Main Content -->
    </div>

    <!-- Footer -->
    <%@ include file="footer.jsp" %>
    <!-- End of Footer -->

    <!-- End of Content -->
</div>
<!-- End of Content Wrapper -->
</div>
<!-- End of Page Wrapper -->

</body>

</html>