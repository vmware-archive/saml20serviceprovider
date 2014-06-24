<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ page session="false"%>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta content="text/html; charset=iso-8859-1" http-equiv="content-type">
<meta content="text/css" http-equiv="content-style-type">
<meta name="author" content="VMware, Inc.">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache,no-store">
<meta http-equiv="expires" content="Tue, 20 Aug 1996 14:25:27 GMT">

<link rel="stylesheet" type="text/css" href="resources/base.css" />
<html>
<head>
<title>Home</title>
</head>
<body>
	<p class="error">${errmsg}</p>
	<p class="success">${successmsg}</p>
	<div class="field">
        <c:if test="${not empty identityProvider.horizonUrl}">
            <h1><c:out value="${identityProvider.horizonUrl}" /></h1>
        </c:if>
        <c:if test="${empty identityProvider.horizonUrl}">
            <h1>New Identity Provider</h1>
        </c:if>
	<h1>Identity Provider</h1>
	</div>
	<form method="POST">
		<h2>IDP Meta Data</h2>
		<p><textarea cols="80" rows="20" name="metaData">${identityProvider.metaData}</textarea></p>
		<input type="hidden" name="id" value="${identityProvider.id}" />
		
		<p><input type="submit" value="Save" /></p>
	</form>
</body>
</html>
