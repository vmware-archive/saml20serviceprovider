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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.vmware.demo.db.dao.OrganizationHandler;

/**
 * Handles requests for the application home page.
 */
@Controller
public class ListController {
	
	private static final Logger logger = LoggerFactory.getLogger(ListController.class);

	@Autowired
	private OrganizationHandler organizationHandler;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/setup", method = RequestMethod.GET)
	public String home(HttpServletRequest request, Locale locale, Model model, String action, Integer id) {
		logger.info("List");
		
		if ("delete".equals(action) && null != id) {
			organizationHandler.delete(id);
		}
		model.addAttribute("identityProviders", organizationHandler.getAllIdentityProviders());
		model.addAttribute("spMetaDataUsername", generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"));
		model.addAttribute("spMetaDataEmail", generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"));
		return "list";
	}
	@RequestMapping(value = "/setup", method = RequestMethod.POST)
	public String post(HttpServletRequest request, Locale locale, Model model, String action, Integer id) {
		logger.info("List POST");
		
		if ("delete".equals(action) && null != id) {
			organizationHandler.delete(id);
		}
		model.addAttribute("identityProviders", organizationHandler.getAllIdentityProviders());
		model.addAttribute("spMetaDataUsername", generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"));
		model.addAttribute("spMetaDataEmail", generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"));
		return "list";
	}
	
	
	
	
	
	
	public static String generateMetaData(HttpServletRequest request, String nameIdFormat) {
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
"      <md:NameIDFormat>"+nameIdFormat+"</md:NameIDFormat>\n"+
"	   <md:AttributeConsumingService isDefault=\"true\" index=\"1\"><md:ServiceName xml:lang=\"en\">Service Provider</md:ServiceName>\n" +
"      <md:RequestedAttribute NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:basic\" Name=\"firstName\" FriendlyName=\"firstName\"></md:RequestedAttribute>\n"+
"      <md:RequestedAttribute NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:basic\" Name=\"lastName\" FriendlyName=\"lastName\"></md:RequestedAttribute>\n"+
"      </md:AttributeConsumingService>\n"+
"      <md:AssertionConsumerService Binding=\"urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST\" Location=\""+url+"\" index=\"0\" isDefault=\"true\"/>\n"+
"   </md:SPSSODescriptor>\n"+
"</md:EntityDescriptor>";
 
		return metaData;
	}
	
	public static String getURLWithContextPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
	}
}
