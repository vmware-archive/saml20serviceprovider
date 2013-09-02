<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<meta content="text/html; charset=iso-8859-1" http-equiv="content-type">
<meta content="text/css" http-equiv="content-style-type">
<meta name="author" content="VMware, Inc.">
<meta http-equiv="Pragma" content="no-cache">
<meta http-equiv="Cache-Control" content="no-cache,no-store">
<meta http-equiv="expires" content="Tue, 20 Aug 1996 14:25:27 GMT">

<link rel="stylesheet" type="text/css" href="resources/base.css"/>
<html>
<head>
	<title>SAML20 Demo Service Provider</title>
</head>
<body>
<p class="error">${errmsg}</p>
<p class="success">${successmsg}</p>
<div class="field">
<h1>Identity Providers</h1>
</div>
<p>The following are the identity providers that are registered with this demo application.</p>
<p>
      <c:forEach var="identityProvider" items="${requestScope.identityProviders}">
        	<a href="edit?id=${identityProvider.id}"><c:out value="${identityProvider.horizonUrl}" /></a>
        	<a href="?action=delete&id=${identityProvider.id}"><img src="/resources/icon_delete.jpeg" /></a>
        	<br/>
      </c:forEach>
</p>
<p><a href="edit?id=0"><img src="/resources/icon_add.jpeg" /> Add Identity Provider</a></p>

<div class="field">
<h1>Horizon Setup</h1>
</div>
<p>Use one of the the following meta data blobs to add this service provider to you Horizon organization.</p>

<h2>Username NameID</h2>
<p><textarea cols="80" rows="5" name="spMetaData">${spMetaDataUsername}</textarea></p>

<h2>Email NameID</h2>
<p><textarea cols="80" rows="5" name="spMetaData">${spMetaDataEmail}</textarea></p>
</body>
</html>
