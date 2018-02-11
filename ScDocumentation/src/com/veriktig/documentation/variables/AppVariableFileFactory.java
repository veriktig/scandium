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

package com.veriktig.documentation.variables;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;
import com.veriktig.documentation.Main;

public class AppVariableFileFactory extends FileFactory {
    static Map<String, String> result = new LinkedHashMap<String, String>();

	public static void make(Set<VariableByBundle> xmlSet) throws FactoryException {
        boolean file_exists = true;
        Map<String, String> all_variables = new HashMap<String, String>();

		for (VariableByBundle vb : xmlSet) {
			Map<String, String> variables = vb.getVariables();
            all_variables.putAll(variables); 
		}

        // Sort by key
        all_variables.entrySet().stream()
                .sorted(Map.Entry.<String, String>comparingByKey())
                .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

        File fd = new File(Main.workPath + "/" + Main.uiPath + "/" + Main.uiFile);
        if (!fd.exists()) {
            try {
                file_exists = fd.createNewFile();
            } catch (IOException ioe) {
                System.err.println("ERROR: IOException creating " + fd.getName());
            } catch (SecurityException se) {
                System.err.println("ERROR: SecurityException creating " + fd.getName());
            }
            buildTemplate(fd);
        } else {
            updateFile(fd);
        }
	}

	private static void buildTemplate(File fd) {
		
		FileFactory ff = new FileFactory();
		ff.createOut(fd, false);
		ff.printIndented(0, FileFactory.copyright);
		ff.printIndented(0, "");
		ff.printIndented(0, "package " + Main.uiPackage + ";");
		ff.printIndented(0, "");
		ff.printIndented(0, "import com.veriktig.scandium.api.SCAPI;");
		ff.printIndented(0, "");
		ff.printIndented(0, "public class AppVariables {");
		ff.printIndented(1, "public static void create() {");
		ff.printIndented(0, FileFactory.startSection);
		buildVariables(ff);
		ff.printIndented(0, FileFactory.endSection);
		ff.printIndented(1, "}");
		ff.printIndented(0, "}");
        ff.done();
	}
	
	private static void updateFile(File fd) {
        List<String> precache = new ArrayList<String>();
        List<String> postcache = new ArrayList<String>();
        boolean preFlag = true;
        boolean postFlag = false;
        FileFactory ff = new FileFactory();
        
        // Only replace the lines between the // START and // END
        try {
            BufferedReader in = new BufferedReader(new FileReader(fd));
            String inLine;
			while ((inLine = in.readLine()) != null) {
                if (inLine.contains(FileFactory.startSection)) {
                    preFlag = false;
                    precache.add(inLine);
                }
                if (inLine.contains(FileFactory.endSection)) {
                    postFlag = true;
                }
                if (preFlag) {
                    precache.add(inLine);
                }
                if (postFlag) {
                    postcache.add(inLine);
                }
            }
            ff.createOut(fd, false);
            for (String pre : precache) {
            	ff.print(pre);
            }
            buildVariables(ff);
            for (String post : postcache) {
                ff.print(post);
            }
            ff.done();
            in.close();
        } catch (IOException e) {
            System.err.println("ERROR: IOException processing " + fd.getName());
        }
	}
	
	private static void buildVariables(FileFactory ff) {
        for (Map.Entry<String, String> entry : result.entrySet()) {
            ff.printIndented(2, "SCAPI.setAppVariable(\"" + entry.getKey() + "\", \"" + entry.getValue() + "\");");
        }
	}
}
