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
public final class SamlConstants {

    private SamlConstants() {} // Hiding utility-class constructor.

    // Endpoints for Saml
    public static final String METADATA_IDP_ENDPOINT = "/metadata/idp.xml";
    public static final String LOGOUT_ENDPOINT = "/logout/";
    public static final String SSO_ENDPOINT = "/authenticate/";

    // Strings for PEM formatting
    public static final CharSequence BEGIN_CERT = "BEGIN CERTIFICATE";
    public static final CharSequence BEGIN_PRIVATE = "BEGIN RSA PRIVATE KEY";
    public static final String BEGIN_PRIVATE_FULL = "-----BEGIN RSA PRIVATE KEY-----";
    public static final String END_PRIVATE_FULL = "-----END RSA PRIVATE KEY-----";
    public static final String BEGIN_CERT_FULL = "-----BEGIN CERTIFICATE-----";
    public static final String END_CERT_FULL = "-----END CERTIFICATE-----";
}
