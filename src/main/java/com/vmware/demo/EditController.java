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
public class EditController {
	
	private static final Logger logger = LoggerFactory.getLogger(EditController.class);
	private static final String ATTRIBUTE_ERROR_MSG = "errmsg";
	
	@Autowired
	private OrganizationHandler organizationHandler;
	
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/edit", method = RequestMethod.GET)
	public String load(Locale locale, Model model, Integer id) {
		logger.info("Edit load "+id);
		
		IdentityProvider idp;
		if (0==id) {
			idp = new IdentityProvider();
			logger.debug("created idp ");
		} else {
			idp = organizationHandler.load(id);
			logger.debug("loaded idp: "+idp.getId());
		}
		model.addAttribute("identityProvider", idp);
		
		return "edit";
	}
	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String save(HttpServletRequest request, Locale locale, Model model, Integer id, String name, String horizonUrl, String metaData) {
		logger.info("Edit save "+ name);
		IdentityProvider idp;
		if (0==id ) {
			idp = new IdentityProvider();
		} else {
			idp = organizationHandler.load(id);
		}

		// Cleanup input before saving
		metaData = StringUtils.remove(metaData, '\r');
		metaData = StringUtils.remove(metaData, '\n');
		
		try {
			horizonUrl = SamlUtils.validate(metaData);
			if (null != horizonUrl) {
				idp.setMetaData(metaData);
				idp.setHorizonUrl(horizonUrl);
				organizationHandler.save(idp);
			}
		} catch (Exception e) {
			model.addAttribute(ATTRIBUTE_ERROR_MSG, e.getLocalizedMessage());
			return "edit";
		}
		model.addAttribute("identityProviders", organizationHandler.getAllIdentityProviders());
		model.addAttribute("spMetaDataUsername", ListController.generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:unspecified"));
		model.addAttribute("spMetaDataEmail", ListController.generateMetaData(request, "urn:oasis:names:tc:SAML:1.1:nameid-format:emailAddress"));
		return "list";
	}
}
