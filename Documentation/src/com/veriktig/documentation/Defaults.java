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

package com.veriktig.documentation;

import java.util.Properties;

public class Defaults {

    public Defaults() {
    }

    public Properties create() {
        Properties props = new Properties();
        // Input paths
        props.setProperty("schemaPath", "schema");
        props.setProperty("errorXmlPath", "definitions/errors");
        props.setProperty("helpXmlPath", "definitions/help");
        props.setProperty("variableXmlPath", "definitions/variables");
        props.setProperty("tmpPath", "/tmp");
        props.setProperty("errorSchemaFile", "Errors.xsd");
        props.setProperty("helpSchemaFile", "Help.xsd");
        props.setProperty("variableSchemaFile", "Variables.xsd");
        // Output path
        props.setProperty("outputPath", "output");
        //
        props.setProperty("workPath", "..");
        props.setProperty("uiPackage", "com.veriktig.scandium.api.state");
        props.setProperty("uiPath", "ScAPI/src/com/veriktig/scandium/api/state");
        props.setProperty("uiFile", "AppVariables.java");
        return (props);
    }
}
