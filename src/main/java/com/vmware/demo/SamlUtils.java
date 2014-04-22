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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.joda.time.DateTime;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.core.NameIDType;
import org.opensaml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml2.metadata.NameIDFormat;
import org.opensaml.saml2.metadata.SingleLogoutService;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.saml2.metadata.impl.EntitiesDescriptorImpl;
import org.opensaml.saml2.metadata.impl.EntityDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.EntityDescriptorImpl;
import org.opensaml.saml2.metadata.impl.IDPSSODescriptorBuilder;
import org.opensaml.saml2.metadata.impl.IDPSSODescriptorImpl;
import org.opensaml.saml2.metadata.impl.KeyDescriptorBuilder;
import org.opensaml.saml2.metadata.impl.KeyDescriptorImpl;
import org.opensaml.saml2.metadata.impl.NameIDFormatBuilder;
import org.opensaml.saml2.metadata.impl.SingleLogoutServiceBuilder;
import org.opensaml.saml2.metadata.impl.SingleSignOnServiceBuilder;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.ParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.signature.KeyInfo;
import org.opensaml.xml.signature.X509Data;
import org.opensaml.xml.signature.impl.KeyInfoBuilder;
import org.opensaml.xml.signature.impl.X509CertificateBuilder;
import org.opensaml.xml.signature.impl.X509DataBuilder;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


public final class SamlUtils {

    private SamlUtils() {} // Hiding utility-class constructor.

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public static X509Certificate parsePemCertificate(String cert) throws SamlException {

        if (null==cert) {
            return null;
        }

        if (!cert.contains(SamlConstants.BEGIN_CERT)) {
            cert = convertCertToPemFormat(cert);
        }

        StringReader reader = new StringReader(cert);
        PEMReader pem = new PEMReader(reader);
        Object obj = null;
        try {
            obj = pem.readObject();
        } catch (IOException e) {
            throw new SamlException("Cannot parse certificate.", e);
        }

        if (obj instanceof X509Certificate) {
            return (X509Certificate) obj;
        }
        return null;
    }

    public static KeyPair parsePemKey(String key) throws SamlException {
        if (null==key) {
            return null;
        }

        // Check if it looks like valid PEM, if not try to convert
        if (!key.contains(SamlConstants.BEGIN_PRIVATE)) {
            key = convertKeyToPemFormat(key);
        }

        StringReader reader = new StringReader(key);
        PEMReader pem = new PEMReader(reader);
        Object obj;
        try {
            obj = pem.readObject();
        } catch (IOException e) {
            throw new SamlException("Cannot read converted key.", e);
        }

        if (obj instanceof KeyPair) {
            return (KeyPair) obj;
        }
        throw new SamlException("Failed to convert key.");
    }

    private static String formatPEMString(final String head, final String foot, final String indata){
        StringBuilder pem = new StringBuilder(head);
        pem.append("\n");

        String data;
        if (indata != null) {
            data = indata.replaceAll("\\s+","");
        } else {
            data = "";
        }
        int lineLength = 64;
        int dataLen = data.length();
        int si = 0;
        int ei = lineLength;

        while (si < dataLen) {
            if (ei > dataLen) {
                ei = dataLen;
            }

            pem.append(data.substring(si, ei));
            pem.append("\n");
            si = ei;
            ei += lineLength;
        }

        pem.append(foot);

        return pem.toString();
    }

    public static String convertKeyToPemFormat(String key) {
        return formatPEMString(SamlConstants.BEGIN_PRIVATE_FULL, SamlConstants.END_PRIVATE_FULL, key);
    }

    public static String convertCertToPemFormat(String cert) {
        return formatPEMString(SamlConstants.BEGIN_CERT_FULL, SamlConstants.END_CERT_FULL, cert);
    }

    /**
     * Convert the given private key into a PEM formatted key string, but
     * strip newlines, header and footer (raw base64).
     *
     * @param key
     * @return String representing key in PEM format
     */
    public static String convertKeyToString(Key key) {
        String keyString = null;
        try
        {
            StringWriter strWriter = new StringWriter();
            PEMWriter pemWriter = new PEMWriter(strWriter);
            pemWriter.writeObject(key);
            pemWriter.close();
            String temp = new String(strWriter.getBuffer());
            keyString = temp.replace(SamlConstants.BEGIN_PRIVATE_FULL, "");
            keyString = keyString.replace(SamlConstants.END_PRIVATE_FULL, "");
            keyString = keyString.replace("\r\n", "");
        }
        catch (IOException ioEx) {
           // TODO: Error handling
            return null;
        }
        return keyString;
    }

    /**
     * Generate a RSA key pair (default size SamlUtils.keySize specified as 2048 bits)
     *
     * @return the new KeyPair
     * @throws SamlException
     */
    public static KeyPair generateKey(int keySize) throws SamlException
    {
        KeyPair pair = null;

        try
        {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(keySize);
            pair = keyGen.genKeyPair();
        }
        catch (Exception e) {
            throw new SamlException("Failed to generate RSA signing key.", e);
        }

        return pair;
    }

    /*
     * Convert the x509 object to a PEM format.
     *
     * @return the new certicate in base64 format with PEM wrapping
     */
    public static String convertToPemFormat(X509Certificate cert) throws SamlException {
        try
        {
            byte[]cert64 = Base64.encodeBase64(cert.getEncoded());
            String strCert  = new String(cert64, SamlGenerator.ENC_UTF8);
            return convertCertToPemFormat(strCert);
        }
        catch (Exception e) {
            throw new SamlException("Failed to create PEM certificate from cert.", e);
        }
    }

    /**
     * Generate a new self-signed certificate for a given keypair.
     *
     * @param pubKey - organization's public key
     * @param privKey - organization's private key
     * @param orgName - organization's name
     * @return the new certicate in base64 format (NO PEM wrapping)
     * @throws SamlException
     */
    public static String generateCertPEM(KeyPair key, String issuer) throws SamlException {
        String pemCert = null;

        try
        {
            X509Certificate binCert = generateCert(key, issuer);
            byte[]cert64 = Base64.encodeBase64(binCert.getEncoded());
            pemCert  = new String(cert64, SamlGenerator.ENC_UTF8);
        }
        catch (Exception e) {
            throw new SamlException("Unable to generate PEM certificate from key, issuer = " + issuer, e);
        }

        return pemCert;

    }

    /**
     * Generate a public x509 cert, based on a key.
     *
     * @param key KeyPair used to generate public Cert, private key in KeyPair not exposed.
     * @param issuer If generating an SSL Cert, issuer needs to match hostname
     * @return
     * @throws SamlException
     */
    public static X509Certificate generateCert(KeyPair key, String issuer) throws SamlException
    {
        X509Certificate binCert;
        try
        {
            X509V3CertificateGenerator  v3CertGen = new X509V3CertificateGenerator();

            // create the certificate - version 3
            v3CertGen.reset();
            v3CertGen.setSerialNumber(BigInteger.valueOf(1));
            v3CertGen.setIssuerDN(new X509Principal(issuer));
            v3CertGen.setNotBefore(new Date(System.currentTimeMillis()));
            v3CertGen.setNotAfter(new Date(System.currentTimeMillis() + (1000L * 60 * 60 * 24 * 365 * 10)));  //10 years
            v3CertGen.setSubjectDN(new X509Principal(issuer));
            v3CertGen.setPublicKey(key.getPublic());
            v3CertGen.setSignatureAlgorithm("SHA1WithRSAEncryption");

            // add the extensions
            v3CertGen.addExtension(org.bouncycastle.asn1.x509.X509Extensions.BasicConstraints, false, new BasicConstraints(true));

            // generate the actual cert
            binCert = v3CertGen.generate(key.getPrivate());

            // check the cert
            binCert.checkValidity(new Date());
            binCert.verify(key.getPublic());
        }
        catch (Exception e) {
            throw new SamlException("Failed to generate certificate.", e);
        }

        return binCert;
    }

    /**
     * Parse an XML string into an XMLObject
     * @param messageXML
     * @return
     * @throws Exception
     * @throws SamlException
     */
    public static XMLObject unmarshallMessage(String messageXML) throws SamlException {
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(messageXML.getBytes(SamlGenerator.CHARSET_UTF8));
            ParserPool parserPool = new BasicParserPool();
            Document messageDoc = parserPool.parse(bais);
            Element messageElem = messageDoc.getDocumentElement();

            Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(messageElem);
            if (unmarshaller == null) {
                throw new SamlException("Unable to unmarshall message, no unmarshaller registered for element " + XMLHelper.getNodeQName(messageElem));
            }

            return unmarshaller.unmarshall(messageElem);
        } catch (XMLParserException e) {
            throw new SamlException("Unable to parse message into a DOM", e);
        } catch (UnmarshallingException e) {
            throw new SamlException("Unable to unmarshall message from its DOM", e);
        }
    }

    /**
     * Parse and XMLObject into an XML string
     * @param xmlObj
     * @return
     * @throws SamlException
     */
    public static String marshallObject(XMLObject xmlObj) throws SamlException {
        if (xmlObj == null) {
            return null;
        }

        Marshaller marshaller = org.opensaml.Configuration.getMarshallerFactory().getMarshaller(xmlObj);
        Element assertionDOM;
        try {
            assertionDOM = marshaller.marshall(xmlObj);
        } catch (MarshallingException e) {
            throw new SamlException(e.getMessage(), e);
        }

        StringWriter writer = new StringWriter();
        XMLHelper.writeNode(assertionDOM, writer);

        return writer.toString();
    }

    /**
     * Parse an XML string into a metadata object.
     *
     * @param metadataXML
     * @return
     * @throws Exception
     * @throws SamlException
     */
    public static EntityDescriptorImpl parseMetaData(String metadataXML) throws SamlException {
        XMLObject message = unmarshallMessage(metadataXML);
        if (message instanceof EntitiesDescriptor) {
            EntitiesDescriptorImpl entitiesDesc = (EntitiesDescriptorImpl) message;
            List<EntityDescriptor> descriptors = entitiesDesc.getEntityDescriptors();
            if (descriptors != null && descriptors.size() > 0) {
                return (EntityDescriptorImpl)descriptors.get(0);
            }
            throw new SamlException("Unable to parse metadata, no EntityDescriptor found");
        } else if (message instanceof EntityDescriptor) {
            return (EntityDescriptorImpl)message;
        } else {
            throw new SamlException("Unable to parse metadata, no EntityDescriptor found");
        }
    }

    public static String generateIdpMetaData(String server, String cert) throws SamlException {

        XMLObjectBuilderFactory builderFactory;
        EntityDescriptorBuilder entityBuilder;
        IDPSSODescriptorBuilder idpBuilder;
        SingleLogoutServiceBuilder sloBuilder;
        SingleSignOnServiceBuilder ssoBuilder;
        NameIDFormatBuilder idBuilder;

        builderFactory = Configuration.getBuilderFactory();

        entityBuilder = (EntityDescriptorBuilder) builderFactory.getBuilder(EntityDescriptor.DEFAULT_ELEMENT_NAME);
        sloBuilder = (SingleLogoutServiceBuilder) builderFactory.getBuilder(SingleLogoutService.DEFAULT_ELEMENT_NAME);
        ssoBuilder = (SingleSignOnServiceBuilder) builderFactory.getBuilder(SingleSignOnService.DEFAULT_ELEMENT_NAME);
        idpBuilder = (IDPSSODescriptorBuilder) builderFactory.getBuilder(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        idBuilder = (NameIDFormatBuilder) builderFactory.getBuilder(NameIDFormat.DEFAULT_ELEMENT_NAME);

        // Location of meta-data
        EntityDescriptor entityDesc = entityBuilder.buildObject();
        entityDesc.setEntityID(server + SamlConstants.METADATA_IDP_ENDPOINT);

        // Logout REST url
        SingleLogoutService slo = sloBuilder.buildObject();
        slo.setLocation(server + SamlConstants.LOGOUT_ENDPOINT);
        slo.setResponseLocation(server + SamlConstants.LOGOUT_ENDPOINT);
        slo.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

        // Where to POST the SAML requests
        SingleSignOnService sso = ssoBuilder.buildObject();
        sso.setLocation(server + SamlConstants.SSO_ENDPOINT);
        sso.setResponseLocation(server + SamlConstants.SSO_ENDPOINT);
        sso.setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);

        IDPSSODescriptor idpDesc = idpBuilder.buildObject();
        idpDesc.setWantAuthnRequestsSigned(true);
        idpDesc.getSingleLogoutServices().add(slo);
        idpDesc.getSingleSignOnServices().add(sso);

        // We only support SAML20 UNSPECIFIED type of NameID (username only)
        NameIDFormat id = idBuilder.buildObject();
        id.setFormat(NameIDType.UNSPECIFIED);
        idpDesc.getNameIDFormats().add(id);
        idpDesc.addSupportedProtocol(SAMLConstants.SAML20P_NS);

        // Setup the assertion signing certificate
        KeyDescriptor keyDesc = getSigningKeyDescriptor(cert, builderFactory);
        idpDesc.getKeyDescriptors().add(keyDesc);
        keyDesc = getEncryptionKeyDescriptor(cert, builderFactory);
        idpDesc.getKeyDescriptors().add(keyDesc);

        // Hookup the entity as configured IDP
        entityDesc.getRoleDescriptors().add(idpDesc);

        // cache for 1 month
        DateTime cacheUntil = new DateTime().plusMonths(1);
        entityDesc.setValidUntil(cacheUntil);
        entityDesc.setCacheDuration(2551443840L);

        // convert to XML string
        return SamlUtils.marshallObject(entityDesc);
    }

    private static KeyDescriptor getSigningKeyDescriptor(String cert, XMLObjectBuilderFactory builderFactory) {
        KeyDescriptorBuilder keyBuilder;
        KeyInfoBuilder keyInfoBuilder;
        X509DataBuilder x509Builder;
        X509CertificateBuilder certBuilder;

        keyBuilder = (KeyDescriptorBuilder) builderFactory.getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        keyInfoBuilder = (KeyInfoBuilder) builderFactory.getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME);
        x509Builder = (X509DataBuilder) builderFactory.getBuilder(X509Data.DEFAULT_ELEMENT_NAME);
        certBuilder = (X509CertificateBuilder) builderFactory.getBuilder(org.opensaml.xml.signature.X509Certificate.DEFAULT_ELEMENT_NAME);

        KeyInfo keyInfo = keyInfoBuilder.buildObject();
        X509Data x509Data = x509Builder.buildObject();
        org.opensaml.xml.signature.X509Certificate x509Cert = certBuilder.buildObject();


        x509Cert.setValue(cert);
        x509Data.getX509Certificates().add(x509Cert);
        keyInfo.getX509Datas().add(x509Data);

        KeyDescriptor keyDesc = keyBuilder.buildObject();
        keyDesc.setKeyInfo(keyInfo);
        keyDesc.setUse(UsageType.SIGNING);

        return keyDesc;
    }

    private static KeyDescriptor getEncryptionKeyDescriptor(String cert, XMLObjectBuilderFactory builderFactory) {
        KeyDescriptorBuilder keyBuilder;
        KeyInfoBuilder keyInfoBuilder;
        X509DataBuilder x509Builder;
        X509CertificateBuilder certBuilder;

        keyBuilder = (KeyDescriptorBuilder) builderFactory.getBuilder(KeyDescriptor.DEFAULT_ELEMENT_NAME);
        keyInfoBuilder = (KeyInfoBuilder) builderFactory.getBuilder(KeyInfo.DEFAULT_ELEMENT_NAME);
        x509Builder = (X509DataBuilder) builderFactory.getBuilder(X509Data.DEFAULT_ELEMENT_NAME);
        certBuilder = (X509CertificateBuilder) builderFactory.getBuilder(org.opensaml.xml.signature.X509Certificate.DEFAULT_ELEMENT_NAME);

        KeyInfo keyInfo = keyInfoBuilder.buildObject();
        X509Data x509Data = x509Builder.buildObject();
        org.opensaml.xml.signature.X509Certificate x509Cert = certBuilder.buildObject();


        x509Cert.setValue(cert);
        x509Data.getX509Certificates().add(x509Cert);
        keyInfo.getX509Datas().add(x509Data);

        KeyDescriptor keyDesc = keyBuilder.buildObject();
        keyDesc.setKeyInfo(keyInfo);
        keyDesc.setUse(UsageType.ENCRYPTION);

        return keyDesc;
    }

	public static String extractCert(String metaData) throws SamlException {
		EntityDescriptorImpl md;
		try {
			md = parseMetaData(metaData);
		} catch (SamlException e) {
            throw new SamlException("Cannot parse meta data.", e);
		}
		
		IDPSSODescriptorImpl idp = (IDPSSODescriptorImpl) md.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);

		java.util.List<KeyDescriptor> keyList = idp.getKeyDescriptors();

		KeyDescriptorImpl keyDesc = (KeyDescriptorImpl) keyList.get(0);

		// Get the KeyInfo node
		KeyInfo keyInfo = keyDesc.getKeyInfo();

		// Get the list of certificates
		java.util.List<X509Data> x509List = keyInfo.getX509Datas();

		// Pull out the first x509 data element
		X509Data x509Data = x509List.get(0);

		// Now the certificates
		List<org.opensaml.xml.signature.X509Certificate> x509CertList = x509Data.getX509Certificates();

		// finally the certificate
		org.opensaml.xml.signature.X509Certificate x509Cert = x509CertList.get(0);
		
		return x509Cert.getValue();
	}

	public static String validate(String metaData) throws SamlException {

		EntityDescriptorImpl md;
		try {
			md = parseMetaData(metaData);
		} catch (SamlException e) {
            throw new SamlException("Cannot parse meta data.", e);
		}
		return md.getEntityID();
	}
}