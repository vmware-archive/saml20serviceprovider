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

public class SamlAttribute {
    private String name;
    private List<String> values;

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
