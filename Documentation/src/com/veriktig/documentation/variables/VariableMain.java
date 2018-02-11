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

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.Main;
import com.veriktig.documentation.Unmarshal;
import com.veriktig.documentation.variables.VariableByBundle;
import com.veriktig.documentation.variables.generated.Variable;
import com.veriktig.documentation.variables.generated.VariablePackage;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class VariableMain {
    private static String genPath;
    private static Set<VariableByBundle> setSet = new HashSet<VariableByBundle>();
    private static Set<VariableByBundle> getSet = new HashSet<VariableByBundle>();
    private static Set<VariableByBundle> xmlSet = new HashSet<VariableByBundle>();
    
    public static void make(File schemaFile, String xmlPath) {

        genPath = new String(VariableMain.class.getPackage().getName() + ".generated");

        // Get all the .xml's in xmlPath
        File f = new File(xmlPath);

        FilenameFilter xmlFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowercaseName = name.toLowerCase();
                if (lowercaseName.endsWith(".xml")) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        List<VariableByLocale> localeList = new ArrayList<VariableByLocale>();
        File[] files = f.listFiles(xmlFilter);
        for (File file : files) {
            // Validate the XML against the schema
            Unmarshal um = new Unmarshal(schemaFile, file, genPath, Main.tmpPath);
            VariablePackage pkg = null;
            try {
                pkg = (VariablePackage)um.unmarshal();
            } catch (FactoryException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String base_name = file.getName().replace(".xml", "");
            int underscore = base_name.indexOf("_");
            String language_locale = base_name.substring(underscore);
            String bundle = base_name.substring(0, underscore);
            String core_name = bundle.replaceFirst("Sc", "");
            String prefix = new String(core_name.toUpperCase() + "-");

            Map<String, String> summaries = new HashMap<String, String>();
            try {
                // Get summaries
                List<Variable> varList = pkg.getVariable();
                for (Variable hh : varList) {
                    summaries.put(hh.getName(), hh.getDefault());
                }
                // Create List of Variables grouped by language_locale
                localeList.add(new VariableByLocale(prefix, language_locale, varList));
                xmlSet.add(new VariableByBundle(bundle, summaries));
                System.out.println("Built variable document for " + bundle.toString() + ".");
            } catch (Exception e) {
                System.err.println("ERROR: Unable to create output from: " + file.getName());
                 return;
            }
        }

        // Find all the locales.
        Set<String> localeSet = new HashSet<String>();        
        for (VariableByLocale hh : localeList) {
            localeSet.add(hh.getLocale());
        }
        // Go through the locales and combine the commands
        for (String ll : localeSet) {
            List<Variable> varList = new ArrayList<Variable>();
            for (VariableByLocale gg : localeList) {
                if (gg.getLocale().equals(ll)) {
                    varList.addAll(gg.getVariables());
                }
            }
            String document = new String("Variables" + ll + ".odt");
            File fd = new File(Main.outputPath + "/" + document);
            try {
                fd.createNewFile();
                ODFileFactory.make(fd, varList);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // Create variables in ScUI
        try {
            AppVariableFileFactory.make(xmlSet);
        } catch (Exception e) {
            System.err.println("ERROR: Unable to create AppVariables.java");
            return;
        }
        
        // cd to workPath and search for Java files
        File work = new File(Main.workPath);

        FilenameFilter bundleFilter = new FilenameFilter() {
             public boolean accept(File dir, String name) {
                 if (name.startsWith("Sc")) {
                     return true;
                 } else {
                     return false;
                 }
             }
         };

         File[] bundles = work.listFiles(bundleFilter);
         for (File bundle : bundles) {
             Map<String, String> sets = new HashMap<String, String>();
             Map<String, String> gets = new HashMap<String, String>();

            // Skip bundles with no commands
             if (bundle.toString().contains("Test")) continue;
             if (bundle.toString().contains("Launcher")) continue;

            Set<FileVisitOption> options = new HashSet<FileVisitOption>();
            options.add(FileVisitOption.FOLLOW_LINKS);

             // Walk the tree looking for Java source files
             Path start = bundle.toPath();
             try {
                Files.walkFileTree(start, options, Integer.MAX_VALUE, new SimpleFileVisitor<Path>() {
                    boolean look_here;
                    
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (look_here) {
                            if (file.toString().endsWith(".java")) {
                                Map<String, String> var = findSets(file);
                                sets.putAll(var);
                                Map<String, String> var2 = findGets(file);
                                gets.putAll(var2);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir.toString().contains("/src/")) {
                            look_here = true;
                        } else {
                            look_here = false;
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                System.err.println("ERROR: Walking " + bundle.toString());
                return;
            }
             String nb = bundle.toString().replace(Main.workPath + "/", "");
             setSet.add(new VariableByBundle(nb, sets));
             getSet.add(new VariableByBundle(nb, gets));
         }
         
         diff("set", setSet, xmlSet);
         diff("get", getSet, xmlSet);
         System.out.println("");
             
        return;
    }
    
    private static Map<String, String> findSets(Path file) {
        try {
            Map<String, String> vars = new HashMap<String, String>();
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.contains("SCAPI.setAppVariable(")) {
                    int key_start = line.indexOf("\"");
                    int key_end = line.indexOf("\"", key_start + 1);
                    String kk = line.substring(key_start + 1, key_end);
                    int value_start = line.indexOf("\"", key_end + 1);
                    int value_end = line.indexOf("\"", value_start + 1);
                    String vv = line.substring(value_start + 1, value_end);
                    vars.put(kk, vv);
                }
            }
            return vars;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * XXX TODO
     * Add the following check:
     * 1) name in InternalState.getAppVariable()
     */
    private static Map<String, String> findGets(Path file) {
        try {
            Map<String, String> vars = new HashMap<String, String>();
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.contains("InternalState.getAppVariable(")) {
                    int key_start = line.indexOf("\"");
                    int key_end = line.indexOf("\"", key_start + 1);
                    String kk = line.substring(key_start + 1, key_end);
                    vars.put(kk, "");
                }
            }
            return vars;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private static void diff(String type, Set<VariableByBundle> work, Set<VariableByBundle> xml) {
        Set<String> megaw = new HashSet<String>();
        Set<String> megax = new HashSet<String>();
        
        // Compare commands - Catches definitions in XML but not workspace
        for (VariableByBundle w : work) {
            for (VariableByBundle x : xml) {
                Map<String, String> xl = x.getVariables();
                Map<String, String> wl = w.getVariables();
                for (String wkv : wl.keySet()) {
                    megaw.add(wkv);
                }
                for (String xkv : xl.keySet()) {
                    megax.add(xkv);
                }
            }
        }
        // Definitions in workspace but not in XML
        Set<String> intersect = new HashSet<String>(megaw);
        intersect.removeAll(megax);
        if (intersect.size() != 0) {
            for (String ss : intersect) {
                System.out.println("\t> " + type + " " + ss + " in workspace but not in XML.");
            }
        }

        // Definitions in XML but not in workspace
        intersect = new HashSet<String>(megax);
        intersect.removeAll(megaw);
        if (intersect.size() != 0) {
            for (String ss : intersect) {
                System.out.println("\t> " + type + " " + ss + " in XML but not in workspace.");
            }
        }
    }
}
