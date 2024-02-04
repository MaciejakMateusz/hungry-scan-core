<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta name="description" content="">
    <meta name="author" content="">
    <title>Rarytas - administracja</title>

    <!-- Custom fonts for this template-->
    <link href="<c:url value="/public/theme/admin/vendor/fontawesome-free/css/all.min.css"/>" rel="stylesheet" type="text/css">
    <link href="<c:url value="https://fonts.googleapis.com/css?family=Nunito:200,200i,300,300i,400,400i,600,600i,700,700i,800,800i,900,900i"/>"
          rel="stylesheet">

    <!-- Custom styles for this template-->
    <link href="<c:url value="/public/theme/admin/css/sb-admin-2.css"/>" rel="stylesheet">
</head>
<body id="page-top">
<div id="wrapper">
    <!-- Sidebar -->
    <ul class="navbar-nav bg-gradient-primary sidebar sidebar-dark accordion" id="accordionSidebar">
        <!-- Sidebar - Brand -->
        <li>
            <a class="sidebar-brand d-flex align-items-center justify-content-center"
               href="${pageContext.request.contextPath}/admin/users">
                <div class="sidebar-brand-text mx-3">Panel administracji</div>
            </a>
        </li>

        <!-- Divider -->
        <!-- Nav Item - Dashboard -->
        <li class="nav-item active">
            <hr class="sidebar-divider my-0">
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/admin/users">
                <p class="side-panel-font">Wszyscy użytkownicy</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/admin/users/waiters">
                <p class="side-panel-font">Kelnerzy</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/admin/users/cooks">
                <p class="side-panel-font">Kucharze</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/admin/users/managers">
                <p class="side-panel-font">Menadżerowie</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/admin/users/admins">
                <p class="side-panel-font">Administratorzy</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/restaurant">
                <p class="side-panel-font">Wyjdź z panelu</p>
            </a>
            <hr class="sidebar-divider">
            <a type="s" class="nav-link" href="${pageContext.request.contextPath}/restaurant/logout">
                <p class="side-panel-font">Wyloguj się</p>
            </a>
        </li>
        <!-- Divider -->
    </ul>
    <!-- End of Sidebar -->
