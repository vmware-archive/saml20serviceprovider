/*****************************************************************
 * 
 * SAMLServiceProvider 1.0 Beta
 * 
 * Copyright (c) 2013 VMware, Inc. All Rights Reserved. 
 * 
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").  
 * You may not use this product except in compliance with the License.  
 * 
 * This product may include a number of subcomponents with separate copyright notices 
 * and license terms. Your use of these subcomponents is subject to the terms and 
 * conditions of the subcomponent's license, as noted in the LICENSE file. 
 */
package com.vmware.demo;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.demo.db.dao.OrganizationHandler;
import com.vmware.demo.db.entity.IdentityProvider;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	@Autowired
	private OrganizationHandler organizationHandler;
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private static final String COOKIE_NAME = "DemoServiceProvider";
	private static final String ATTRIBUTE_IDP_URI = "idpUri";
	private static final String ATTRIBUTE_SAML_CERT = "samlCert";
	private static final String ATTRIBUTE_ERROR_MSG = "errmsg";
	private static final String ATTRIBUTE_SUCCESS_MSG = "successmsg";
	private static final String ATTRIBUTE_SAML_CERTIFICATE = "SAMLCertificate";

	private static final String DEFAULT_SAML_CONSUMER = "https://horizon.mollocal.trcint.com";
	private static final String CONSUME_REQUEST = "/SAAS/API/1.0/POST/sso";
	private static final String METADATA_REQUEST = "/SAAS/API/1.0/GET/metadata/idp.xml";
	private static final String DEFAULT_NAMEID_FORMAT = "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress";
													 // "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified";
													 // "urn:oasis:names:tc:SAML:2.0:nameid-format:persistent"
	private static final String identityProviderId = "0";
	private static final String CONSUMER_URI = "http://serviceprovider.cloudfoundry.com/";
	private static final Object RELAY_STATE = "http://serviceprovider.cloudfoundry.com/";
	private static final String ATTRIBUTE_AUTHN_REQUEST = "authnRequest";
	private static final String ATTRIBUTE_RELAY_STATE = "RelayState";
	private static final String ATTRIBUTE_TARGET = "TARGET";
	private static final String ATTRIBUTE_META_DATA = "metaData";
	private static final String ATTRIBUTE_ACTION = "action";
	private static final String ATTRIBUTE_IDP_ID = "i";
	private static final String ATTRIBUTE_SP_ID = "s";
	
	@RequestMapping(value = "/manual", method = RequestMethod.GET)
	public String createForm(HttpServletRequest request, Locale locale, Model model) {
        if (null != request.getSession().getAttribute(ATTRIBUTE_SAML_CERTIFICATE)) {
			model.addAttribute(ATTRIBUTE_SAML_CERTIFICATE, request.getSession().getAttribute(ATTRIBUTE_SAML_CERTIFICATE));
		}
		if (null != request.getSession().getAttribute(ATTRIBUTE_IDP_URI)) {
			model.addAttribute(ATTRIBUTE_IDP_URI, request.getSession().getAttribute(ATTRIBUTE_IDP_URI));
		} else {
			String cookieValue = null;
			Cookie[] cookies = request.getCookies();
			if (null != cookies) {
				for (Cookie cookie : cookies) {
					if (COOKIE_NAME.equals(cookie.getName())) {
						cookieValue = cookie.getValue();
					}
				}
			}
			if (null != cookieValue) {
			} else {
				model.addAttribute(ATTRIBUTE_IDP_URI, DEFAULT_SAML_CONSUMER);
			}
		}
		model.addAttribute(ATTRIBUTE_META_DATA, generateMetaData(request));
		model.addAttribute(ATTRIBUTE_RELAY_STATE, getURLWithContextPath(request));
		model.addAttribute("action", "setup");
		return "home";
	}

	private String generateMetaData(HttpServletRequest request) {
		String url = getURLWithContextPath(request);
		String metaData = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
"<md:EntityDescriptor xmlns:md=\"urn:oasis:names:tc:SAML:2.0:metadata\" entityID=\""+url+"\" validUntil=\"2022-05-09T20:03:15.334Z\">\n"+
"   <md:SPSSODescriptor AuthnRequestsSigned=\"true\" WantAssertionsSigned=\"true\" protocolSupportEnumeration=\"urn:oasis:names:tc:SAML:2.0:protocol\">\n"+
"      <md:KeyDescriptor use=\"signing\">\n"+
"         <ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n"+
"            <ds:X509Data>\n"+
"               <ds:X509Certificate>MIIFBzCCA++gAwIBAgIQDJ4ihF+4VYzLxb+qASp7IzANBgkqhkiG9w0BAQUFADCBvDELMAk\n"+
"GA1UEBhMCVVMxFzAVBgNVBAoTDlZlcmlTaWduLCBJbmMuMR8wHQYDVQQLExZWZXJpU2lnbi\n"+
"BUcnVzdCBOZXR3b3JrMTswOQYDVQQLEzJUZXJtcyBvZiB1c2UgYXQgaHR0cHM6Ly93d3cud\n"+
"mVyaXNpZ24uY29tL3JwYSAoYykxMDE2MDQGA1UEAxMtVmVyaVNpZ24gQ2xhc3MgMyBJbnRl\n"+
"cm5hdGlvbmFsIFNlcnZlciBDQSAtIEczMB4XDTExMTIwNzAwMDAwMFoXDTEzMTIwNzIzNTk\n"+
"1OVowgY4xCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHFA1TYW\n"+
"4gRnJhbmNpc2NvMR0wGwYDVQQKFBRTYWxlc2ZvcmNlLmNvbSwgSW5jLjEUMBIGA1UECxQLQ\n"+
"XBwbGljYXRpb24xHTAbBgNVBAMUFHByb3h5LnNhbGVzZm9yY2UuY29tMIGfMA0GCSqGSIb3\n"+
"DQEBAQUAA4GNADCBiQKBgQDMoSWW4dBiVScWbXno3C6n2+qR/0O+eE4lzT0Y1go53Pk+Skn\n"+
"9sUu43Z+uZ8lOXDqmLiScTaB43ePbqIAUYimqCR9aYCLmSeNwhs68dsxcyDVqm5XIr2OZsr\n"+
"LikhNkKPno+0fuoyOWbA35kRxBFXL66tEYlF8ETIT6G3kqt7CGVwIDAQABo4IBszCCAa8wC\n"+
"QYDVR0TBAIwADALBgNVHQ8EBAMCBaAwQQYDVR0fBDowODA2oDSgMoYwaHR0cDovL1NWUklu\n"+
"dGwtRzMtY3JsLnZlcmlzaWduLmNvbS9TVlJJbnRsRzMuY3JsMEQGA1UdIAQ9MDswOQYLYIZ\n"+
"IAYb4RQEHFwMwKjAoBggrBgEFBQcCARYcaHR0cHM6Ly93d3cudmVyaXNpZ24uY29tL3JwYT\n"+
"AoBgNVHSUEITAfBglghkgBhvhCBAEGCCsGAQUFBwMBBggrBgEFBQcDAjByBggrBgEFBQcBA\n"+
"QRmMGQwJAYIKwYBBQUHMAGGGGh0dHA6Ly9vY3NwLnZlcmlzaWduLmNvbTA8BggrBgEFBQcw\n"+
"AoYwaHR0cDovL1NWUkludGwtRzMtYWlhLnZlcmlzaWduLmNvbS9TVlJJbnRsRzMuY2VyMG4\n"+
"GCCsGAQUFBwEMBGIwYKFeoFwwWjBYMFYWCWltYWdlL2dpZjAhMB8wBwYFKw4DAhoEFEtruS\n"+
"iWBgy70FI4mymsSweLIQUYMCYWJGh0dHA6Ly9sb2dvLnZlcmlzaWduLmNvbS92c2xvZ28xL\n"+
"mdpZjANBgkqhkiG9w0BAQUFAAOCAQEAVq0AapffwqicpyAu41f5pWDn7FPjgIt6lirqwo7t\n"+
"LRMpxFuYKIMg+wvioJQ8DJ8mNyw+JnZDPxdVjDSkE2Lb+5Z5P9vKbD833jqKP5vniMMvHRf\n"+
"tlkCqP/AI/9z6jomgQtfm3WbI7elTFJvDwA+/VdxgU86mKRpalMWDB545GxVFiO6AZ/8dvA\n"+
"poHVHTQBfrckk9JCrH++Wq3EmErKcxzsY8LItC8qCl5HtgJy160fII0ZdF8hV5vKlrHQpo9\n"+
"1L0c1pn+z5RB+kt8GIreME2rU3WEmtZglBKrlw3ik0sXL2CO/GCAzbh7YWkEfXtE3GcGh7N\n"+
"xcHB+08lZiJzKwN/yg==</ds:X509Certificate>\n"+
"            </ds:X509Data>\n"+
"         </ds:KeyInfo>\n"+
"      </md:KeyDescriptor>\n"+
"      <md:NameIDFormat>urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified</md:NameIDFormat>\n"+
"	   <md:AttributeConsumingService isDefault=\"true\" index=\"1\"><md:ServiceName xml:lang=\"en\">Service Provider</md:ServiceName>\n" +
"      <md:RequestedAttribute NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:basic\" Name=\"firstName\" FriendlyName=\"firstName\"></md:RequestedAttribute>\n"+
"      <md:RequestedAttribute NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:basic\" Name=\"lastName\" FriendlyName=\"lastName\"></md:RequestedAttribute>\n"+
"      </md:AttributeConsumingService>\n"+
"      <md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\""+url+"\" index=\"0\" isDefault=\"true\"/>\n"+
"   </md:SPSSODescriptor>\n"+
"</md:EntityDescriptor>";
 
		return metaData;
	}

	@RequestMapping(value = "/manual", method = RequestMethod.POST)
	public String generateRequest(HttpServletRequest request, HttpServletResponse response, Locale locale, Model model, String action, String SAMLResponse, String SAMLCertificate, String idpUri, String samlCert, String s, String i, String nameIdFormat, String consumeUrl) {
		String serviceProviderId = (String) request.getSession().getAttribute(ATTRIBUTE_SP_ID);

		// SETUP TEST
		if ("setup".equals(action)) {
			logger.info("Setup test");

			// Pass along the standard set
			model.addAttribute(ATTRIBUTE_IDP_ID, identityProviderId);
			model.addAttribute(ATTRIBUTE_SP_ID, serviceProviderId);
			model.addAttribute(ATTRIBUTE_IDP_URI, idpUri);
			model.addAttribute("nameIdFormat", DEFAULT_NAMEID_FORMAT);
			model.addAttribute("consumeUrl", idpUri + CONSUME_REQUEST);

			// Set in a cookie for next time you come back
			Cookie cookie = new Cookie(COOKIE_NAME, idpUri);
			response.addCookie(cookie);
			
			if (null != samlCert) {
				logger.info("Setup test using uploaded certificate.");
				try {
					if (StringUtils.isNotEmpty(samlCert)) {
						samlCert = SamlUtils.convertToPemFormat(SamlUtils.parsePemCertificate(samlCert));
					} else {
						model.addAttribute(ATTRIBUTE_ERROR_MSG, "Saml certificate not provided, no validation will be done.");
					}
				} catch (SamlException e) {
					model.addAttribute(ATTRIBUTE_ERROR_MSG, "Failed to parse certificate. "+e.getLocalizedMessage());
					model.addAttribute(ATTRIBUTE_ACTION, "setupcert");
					return "home";
				}
			} else {
				logger.info("Setup test using meta data url.");
				samlCert = SamlService.getInstance().loadSigningKeyFromMetaData(idpUri + METADATA_REQUEST);
				if (null == samlCert) {
					model.addAttribute(ATTRIBUTE_ERROR_MSG, "Failed to contact service at "+idpUri+ ", please fetch and upload certificate manually.");
					model.addAttribute(ATTRIBUTE_ACTION, "setupcert");
					return "home";
				}
			}

			// Save to session
			if (!StringUtils.isEmpty(idpUri)) {
				request.getSession().setAttribute(ATTRIBUTE_IDP_URI, idpUri);
			}
			if (!StringUtils.isEmpty(samlCert)) {
				request.getSession().setAttribute(ATTRIBUTE_SAML_CERT, samlCert);
			}
			if (!StringUtils.isEmpty(s)) {
				request.getSession().setAttribute(ATTRIBUTE_SP_ID, s);
			}

			model.addAttribute(ATTRIBUTE_ACTION, "generaterequest");
		}

		// GENERATE SAML REQUEST
		if ("generaterequest".equals(action)) {
			logger.info("Generating authnRequest");
			
			String authnRequest = SamlService.getInstance().generateSAMLRequest(CONSUMER_URI, nameIdFormat);
			
			model.addAttribute("consumeUrl"
					, consumeUrl);
			model.addAttribute(ATTRIBUTE_IDP_URI, idpUri);
			model.addAttribute(ATTRIBUTE_AUTHN_REQUEST, authnRequest);
			model.addAttribute(ATTRIBUTE_RELAY_STATE, RELAY_STATE);
			model.addAttribute(ATTRIBUTE_IDP_ID, null!=i?i:identityProviderId);
			model.addAttribute(ATTRIBUTE_SP_ID, null!=s?s:serviceProviderId);
			model.addAttribute(ATTRIBUTE_SAML_CERTIFICATE, SAMLCertificate);
			model.addAttribute(ATTRIBUTE_ACTION, "sendrequest");
		}

		// VALIDATE SAML RESPONSE
		if (null != SAMLResponse) {
			
			logger.info(SAMLResponse);
			String decodedResponse;
			String relayState;
			String target;
			try {
				samlCert = (String) request.getSession().getAttribute(ATTRIBUTE_SAML_CERT);
				relayState = (String) request.getParameter(ATTRIBUTE_RELAY_STATE);
				target = (String) request.getParameter(ATTRIBUTE_TARGET);
				model.addAttribute(ATTRIBUTE_RELAY_STATE, relayState);
				model.addAttribute(ATTRIBUTE_TARGET, target);
			
				List<IdentityProvider> identityProviders = organizationHandler.getAllIdentityProviders();
				if (null==samlCert) {
					decodedResponse = SamlService.getInstance().validateSAMLResponse(SAMLResponse, identityProviders);
				} else {
					decodedResponse = SamlService.getInstance().validateSAMLResponse(SAMLResponse, samlCert);
				}
				
				if (StringUtils.isBlank(decodedResponse)) {
					model.addAttribute(ATTRIBUTE_ERROR_MSG, "Failed to validate SAML Response");
					model.addAttribute("SAMLResponse", decodedResponse);
				} else {
					model.addAttribute(ATTRIBUTE_SUCCESS_MSG, "SAML Response validated.");
					model.addAttribute("SAMLResponse", decodedResponse);
				}
			} catch (Exception e) {
				model.addAttribute("SAMLResponse", SAMLResponse);
				model.addAttribute(ATTRIBUTE_ERROR_MSG, e.getLocalizedMessage());
			}
		}

		return "home";
	}
	
	public static String getURLWithContextPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	}

	
}
