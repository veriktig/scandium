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

/**
 * 
 */
package com.veriktig.documentation.errors;

import java.util.ArrayList;
import java.util.List;

import com.veriktig.documentation.errors.generated.Error;

/**
 * @author fred
 *
 */
public class ErrorByLocale {
    private String locale;
    private List<SError> errors = new ArrayList<SError>();
    
    public ErrorByLocale(String prefix, String locale, List<Error> errors) {
        this.locale = locale;
        for (Error ee : errors) {
            this.errors.add(new SError(prefix, ee));
        }
    }
    
    public String getLocale() {
        return locale;
    }
    
    public List<SError> getErrors() {
        return errors;
    }
}
