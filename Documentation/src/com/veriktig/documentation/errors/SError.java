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

import java.util.List;

import com.veriktig.documentation.errors.generated.Description;
import com.veriktig.documentation.errors.generated.Error;
import com.veriktig.documentation.errors.generated.SeeAlso;
import com.veriktig.documentation.errors.generated.SeverityType;
import com.veriktig.documentation.errors.generated.WhatNext;

/**
 * @author fred
 *
 */
public class SError extends Error {
    private String prefix;
    private Error error;
    
    public SError(String prefix, Error error) {
        this.prefix = prefix;
        this.error = error;
    }
    public String getName() {
        return(new String(prefix + error.getNumber()));
    }
    @Override
    public String getHelp() {
        return error.getHelp();
    }
    @Override
    public List<Description> getDesc() {
        return error.getDesc();
    }
    @Override
    public List<WhatNext> getWhatNext() {
        return error.getWhatNext();
    }
    @Override
    public List<SeeAlso> getSeeAlso() {
        return error.getSeeAlso();
    }
    @Override
    public int getNumber() {
        return error.getNumber();
    }
    @Override
    public SeverityType getSeverity() {
        return error.getSeverity();
    }
}
