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

import java.io.File;
import java.util.Properties;

import com.veriktig.documentation.errors.ErrorMain;
import com.veriktig.documentation.help.HelpMain;
import com.veriktig.documentation.variables.VariableMain;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Main {
    private static Properties props;
    private static String propertiesFile;

    public static String outputPath;
    public static String tmpPath;
    public static String workPath;
    public static String uiPackage;
    public static String uiPath;
    public static String uiFile;
    private static String errorXmlPath;
    private static String helpXmlPath;
    private static String variableXmlPath;
    
    public static void main(String[] args) {
        // Create the default properties
        props = new Defaults().create();

        // See if the built-in defaults should be overridden
        try {
            propertiesFile = args[0];
            FileInputStream fd = new FileInputStream(propertiesFile);
            props.load(fd);
        } catch (ArrayIndexOutOfBoundsException be) {
        } catch (FileNotFoundException fnfe) {
        } catch (IOException ioe) {
            System.err.println("ERROR: Reading <" + propertiesFile + ">.");
            return;
        }

        // Common
        outputPath = new String(props.getProperty("outputPath"));
        tmpPath = new String(props.getProperty("tmpPath"));
        workPath = new String(props.getProperty("workPath"));
        uiPackage = new String(props.getProperty("uiPackage"));
        uiPath = new String(props.getProperty("uiPath"));
        uiFile = new String(props.getProperty("uiFile"));
        
        // Specific to documentation type
        errorXmlPath = new String(props.getProperty("errorXmlPath"));
        helpXmlPath = new String(props.getProperty("helpXmlPath"));
        variableXmlPath = new String(props.getProperty("variableXmlPath"));
        
        // Construct the fully resolved filenames
        String schema = new String(props.getProperty("schemaPath") + "/" + props.getProperty("errorSchemaFile"));
        File errorSchemaFile = new File(schema);
        if (!errorSchemaFile.exists()) {
            System.err.println("ERROR: Schema file <" + schema + "> does not exist.");
            return;
        }
        schema = new String(props.getProperty("schemaPath") + "/" + props.getProperty("helpSchemaFile"));
        File helpSchemaFile = new File(schema);
        if (!helpSchemaFile.exists()) {
            System.err.println("ERROR: Schema file <" + schema + "> does not exist.");
            return;
        }
        schema = new String(props.getProperty("schemaPath") + "/" + props.getProperty("variableSchemaFile"));
        File variableSchemaFile = new File(schema);
        if (!variableSchemaFile.exists()) {
            System.err.println("ERROR: Schema file <" + schema + "> does not exist.");
            return;
        }
        ErrorMain.make(errorSchemaFile, errorXmlPath);
        HelpMain.make(helpSchemaFile, helpXmlPath);
        VariableMain.make(variableSchemaFile, variableXmlPath);
    }
}
