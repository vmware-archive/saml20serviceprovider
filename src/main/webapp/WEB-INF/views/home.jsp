<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

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
	<title>SAML Service Provider</title>
</head>
<body class="basic">
<div class="header">
<div class="header-inner">
<h1>SAML Service Provider</h1>
</div>
</div>
<p class="error">${errmsg}</p>
<p class="success">${successmsg}</p>

<c:if test="${action eq 'setup'}" >
<form method="post">
	<p>
	Demo application that you can use to generate a SAML request for the Horizon Application Manager system.  
	Before running the test, you need to prime this test with the web address of your specific organization, 
	so that the signing certificate can be extracted from the meta-data. 
	</p>
	<div class="field">
		<div class="field-label">Horizon Application Manager URL:</div>
		<div class="field-input"><input type="text" name="idpUri" size="80" value="${idpUri}" /></div>
	</div>
	<input type="hidden" value="setup" name="action" />
	<div class="field-submit">
		<input class="button" type="submit" value="Setup Test"/>
	</div>
	<p>If manually creating the App in Horizon Application Manager, you can use the following meta-data and relay state below.</p>
	<div class="field">
		<div class="field-label">Relay State:</div>
		<div class="field-input"><input type="text" name="RelayState" size="80" value="${RelayState}" /></div>
	</div>
	<div class="field">
		<div class="field-label">Meta Data:</div>
		<div class="field-input"><textarea cols="80" rows="40" name="empty">${metaData}</textarea></div>
	</div>
	
</form>
</c:if>

<c:if test="${action eq 'setupcert'}" >
<form method="post">
	<p>
	Demo application that you can use to generate a SAML request for the Horizon Application Manager system.  
	Before running the test, you need to prime this test with the web address of your specific organization, 
	so that the signing certificate can be extracted from the meta-data. 
	</p>
	<div class="field">
		<div class="field-label">Horizon Application Manager URL:</div>
		<div class="field-input"><input type="text" name="idpUri" size="80" value="${idpUri}" /></div>
	</div>
	<div class="field">
		<div class="field-label">SAML Certificate:</div>
		<div class="field-input"><textarea name="samlCert" cols="55" rows="10">${samlCert}</textarea></div>
	</div>
	<input type="hidden" value="setup" name="action" />
	<div class="field-submit">
		<input class="button" type="submit" value="Setup Test"/>
	</div>
</form>
</c:if>

<c:if test="${action eq 'generaterequest'}" >
<form method="post">
<p>Demo application that you can use to generate a SAML request for an identity provider.  The resulting form can be posted to the identity provider in the next step, which should send a SAML response back after a user has been authenticated.</p>
	<input type="hidden" name="idpUri" value="${idpUri}" />
	<div class="field"><div class="field-label">nameIdFormat:</div> <div class="field-input"><input name="nameIdFormat" type="text" size="160" value="${nameIdFormat}" /></div><div class="comment">Leave blank to let service decide.</div></div>
	<div class="field"><div class="field-label">consumeUrl:</div> <div class="field-input"><input name="consumeUrl" type="text" size="160" value="${consumeUrl}" /></div></div>
	<div class="field-submit">
		<input class="button" type="submit" value="Generate Request"/>
	</div>
	
	 <div class="field">
		 <div class="field-label">IDP ID:</div>
		<div class="field-input"><input name="i" type="text" size="10" value="${i}" /></div>
	</div>
	<div class="field">
		<div class="field-label">SP ID:</div>
		<div class="field-input"><input name="s" type="text" size="10" value="${s}" /></div>
	</div>
	<input type="hidden" value="generaterequest" name="action" />
	<div class="field-submit">
		<input class="button" type="submit" value="Build Url"/>
	</div>	
</form>
</c:if>

<c:if test="${action eq 'sendrequest'}" >
<p>Below you can issue a POST of a generated SAML request (SP initiated launch), or click on one of the two links that will trigger an identity provider launch.</p>
<form action="${consumeUrl}" method="post">
	<div class="field"><div class="field-label">authnRequest:</div> <div class="field-input"><input name="SAMLRequest" type="text" size="160" value="${authnRequest}" /></div></div>
	<div class="field"><div class="field-label">RelayState:</div> <div class="field-input"><input name="RelayState" type="text" size="160" value="${RelayState}" /></div></div>
	<div class="field-submit"><input class="button" type="submit" value="Send Request"/></div>
</form>
<div class="field"><div class="field-label">Application Launch:</div> <div class="field-input"><a href="${idpUri}/SAAS/API/1.0/GET/apps/launch/app/${s}" target="_blank">/GET/apps/launch/app/${s}</a></div></div> 
<div class="field"><div class="field-label">Federation Request:</div> <div class="field-input"><a href="${idpUri}/SAAS/API/1.0/GET/federation/request?s=${s}" target="_blank">/GET/federation/request?s=${s}</a></div></div> 
</c:if>

<c:if test="${!empty SAMLResponse}" >
<form method="get">
<h2>Debug Information</h2>
	<c:if test="${!empty TARGET}" >
		<div class="field"><div class="field-label">TARGET:</div> <div class="field-input"><input name="TARGET" type="text" size="160" value="${TARGET}" /></div></div>
	</c:if>
	<c:if test="${empty TARGET}" >
		<div class="field"><div class="field-label">RelayState:</div> <div class="field-input"><input name="RelayState" type="text" size="160" value="${RelayState}" /></div></div>
	</c:if>
	<div class="field">
		<div class="field-label">SAML Response:</div>
		<div class="field-input"><textarea cols="80" rows="15" name="empty">${SAMLResponse}</textarea></div>
	</div>
</form>
</c:if>

</body>
</html>
