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
package com.vmware.demo.db.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
@Entity
public class IdentityProvider implements Serializable {
	private static final long serialVersionUID = -8912945153640636885L;
	
	@Id
	@GeneratedValue
	protected int id;
	
	@Lob 
	@Column(name="metaData", length=4096)
	private String metaData;
	private String horizonUrl;
	
	public IdentityProvider() {
	}

	
	public int getId() {
		return id;
	}

	public String getMetaData() {
		return metaData;
	}

	public void setMetaData(String metaData) {
		this.metaData = metaData;
	}

	public String getHorizonUrl() {
		return horizonUrl;
	}

	public void setHorizonUrl(String horizonUrl) {
		this.horizonUrl = horizonUrl;
	}	
	
}
