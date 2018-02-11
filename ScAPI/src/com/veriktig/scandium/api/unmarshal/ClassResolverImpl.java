/*
 * Copyright 2018 Veriktig, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.veriktig.scandium.api.unmarshal;

import com.sun.xml.internal.bind.api.ClassResolver;

/**
 * 
 */
public class ClassResolverImpl extends ClassResolver {

    public ClassResolverImpl() {
    }
    
    /* (non-Javadoc)
     * @see com.sun.xml.internal.bind.api.ClassResolver#resolveElementName(java.lang.String, java.lang.String)
     */
    @Override
    public Class<?> resolveElementName(String arg0, String arg1)
            throws Exception {
        System.out.println(arg0 + " " + arg1);
        return null;
    }

}
