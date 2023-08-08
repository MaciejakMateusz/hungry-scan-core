<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
    <title>Dodaj danie</title>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
</head>
<body>

<h2>Dodaj nowe danie</h2>

<form:form method="POST"
           action="/restaurant/cms/items/add"
           modelAttribute="menuItem"
           enctype="multipart/form-data">
    <div>
        <label for="name"> Nazwa:
            <form:input path="name"/>
        </label>
    </div>
    <div>
        <label for="description"> Opis:
            <form:input path="description"/>
        </label>
    </div>
    <div>
        <label for="ingredients"> Składniki:
            <form:input path="ingredients"/>
        </label>
    </div>
    <div>
        <label for="price"> Cena:
            <form:input path="price"/>
        </label>
    </div>
    <div>
        <label for="category"> Kategoria:
            <form:radiobuttons path="category"
                               items="${categories}"
                               itemValue="id"
                               itemLabel="name"/>
        </label>
    </div>
    <div>
        <label for="imageFile"> Zdjęcie:
            <form:input type="file"
                        path="imageFile"
                        accept=".png"/>
        </label>
    </div>
    <div>
        <button type="submit">Dodaj danie</button>
    </div>
</form:form>

<a href="${pageContext.request.contextPath}/restaurant/cms/items"><button>Powrót</button></a>

</body>
</html>
