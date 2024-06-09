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

package com.veriktig.scandium.api.errors;

import com.veriktig.scandium.api.SCAPI;

/**
 * @author fred
 *
 */
public class ErrorMessage {
    private SCAPI.ErrorType severity;
    private String message;
    
    public ErrorMessage(SCAPI.ErrorType severity, String message) {
        this.severity = severity;
        this.message = message;
    }
    
    public SCAPI.ErrorType getSeverity() {
        return severity;
    }
    
    public String getMessage() {
        return message;
    }
}
