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
package com.vmware.demo.db.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.vmware.demo.db.entity.IdentityProvider;

@Component
public class OrganizationHandler {
	@PersistenceContext
	private EntityManager entityManager;

	@Transactional
	public void save(IdentityProvider idp) {
		if (idp.getId()==0) {
			entityManager.persist(idp);
		} else {
			entityManager.merge(idp);
		}
	}

	@Transactional
	public List<IdentityProvider> getAllIdentityProviders() {
		TypedQuery<IdentityProvider> query = entityManager.createQuery("SELECT idp FROM IdentityProvider idp ORDER BY idp.id", IdentityProvider.class);
		return query.getResultList();
	}
	
	@Transactional
	public IdentityProvider load(Integer id) {
		IdentityProvider idp = entityManager.find(IdentityProvider.class, id);
		return idp;
	}

	@Transactional
	public void delete(Integer id) {
		IdentityProvider idp = load(id);
		if (null!=idp) {
			entityManager.remove(entityManager.find(IdentityProvider.class, id));
		}
	}
}
