/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.persistence.access.xml;

import java.util.Date;

public class XMLFieldSub extends XMLAbstractMappedSuperProperty {

    private Date crtDate;
    
    @Override
    public Date getCreateDate() {
        return crtDate;
    }

    @Override
    public void setCreateDate(Date date) {
        crtDate = date;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof XMLFieldSub) {
            XMLFieldSub ps = (XMLFieldSub)obj;
            return super.equals(obj) &&
                   crtDate.equals(ps.getCreateDate());
        }
        return false;
    }
}
