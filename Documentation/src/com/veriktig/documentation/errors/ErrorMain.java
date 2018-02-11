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

package com.veriktig.documentation.errors;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.Main;
import com.veriktig.documentation.Unmarshal;
import com.veriktig.documentation.errors.generated.Error;
import com.veriktig.documentation.errors.generated.ErrorPackage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class ErrorMain {
    private static String genPath;
    private static Set<String> workSet = new HashSet<String>();
    private static Set<String> xmlSet = new HashSet<String>();
    
    public static void make(File schemaFile, String xmlPath) {
        genPath = new String(ErrorMain.class.getPackage().getName() + ".generated");

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

        List<ErrorByLocale> localeList = new ArrayList<ErrorByLocale>();
        File[] files = f.listFiles(xmlFilter);
        for (File file : files) {
            // Validate the XML against the schema
            Unmarshal um = new Unmarshal(schemaFile, file, genPath, Main.tmpPath);
            ErrorPackage pkg = null;
			try {
				pkg = (ErrorPackage)um.unmarshal();
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

            String java_class = new String("Errors" + language_locale);
            String java_filename = new String(java_class + ".java");

            Set<Summary> summaries = new HashSet<Summary>();
            try {
                // Get summaries
                List<Error> errorList = pkg.getError();
                for (Error hh : errorList) {
                    String error_code = new String(prefix + hh.getNumber());
                    summaries.add(new Summary(error_code, hh.getSeverity().value(), hh.getHelp()));
                }
                // Create List of Help grouped by language_locale
                localeList.add(new ErrorByLocale(prefix, language_locale, errorList));
                
                // Use bundle and getPackage to create file path
                File fd2 = new File(Main.workPath + "/" + bundle + "/src/" + pkg.getPackage().replace(".", "/") + "/" + java_filename);
            	ErrorClassFileFactory.make(fd2, pkg.getPackage(), java_class, summaries);
            	Set<String> commands = new HashSet<String>();
            	for (Summary summary : summaries) {
            		commands.add(summary.getName());
            	}
                xmlSet.addAll(commands);
                System.out.println("Built error document and Java class for " + bundle.toString() + ".");
            } catch (Exception e) {
                System.err.println("ERROR: Unable to create output from: " + file.getName());
             	return;
            }
        }

        // Find all the locales.
        Set<String> localeSet = new HashSet<String>();        
        for (ErrorByLocale hh : localeList) {
        	localeSet.add(hh.getLocale());
        }
        // Go through the locales and combine the commands
        for (String ll : localeSet) {
        	List<SError> errorList = new ArrayList<SError>();
        	for (ErrorByLocale gg : localeList) {
        		if (gg.getLocale().equals(ll)) {
        			errorList.addAll(gg.getErrors());
        		}
        	}
        	String document = new String("Errors" + ll + ".odt");
        	File fd = new File(Main.outputPath + "/" + document);
        	try {
				fd.createNewFile();
	        	ODFileFactory.make(fd, errorList);
        	} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
            // Skip bundles with no commands
     		if (bundle.toString().contains("Test")) continue;
     		if (bundle.toString().contains("Launcher")) continue;
     		// Walk the tree looking for Java source files
     		Path start = bundle.toPath();
     		try {
				Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
					boolean look_here;
					
				    @Override
				    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				    	if (look_here) {
                            if (file.toString().endsWith(".java")) {
                                Set<String> error = grep(file);
                                workSet.addAll(error);
                            }
				    	}
				        return FileVisitResult.CONTINUE;
				    }
				    @Override
				    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				    	if (dir.toString().contains("/src/") && !dir.toString().contains("tcl")) {
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
     	}
     	
     	compare(workSet, xmlSet);
     	
     	System.out.println("");
     		
        return;
    }
    
    private static Set<String> grep(Path file) {
    	try {
            Set<String> errors = new HashSet<String>();
			List<String> lines = Files.readAllLines(file);
			for (String line : lines) {
				if (line.contains("new ScException(")) {
                    int start = line.indexOf("\"");
                    int end = line.indexOf("\"", start + 1);
                    String ee = line.substring(start + 1, end);
					errors.add(ee);
				}
			}
            return errors;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    private static void compare(Set<String> work, Set<String> xml) {
    	Set<String> intersect = new HashSet<String>();
    	
    	// In workspace but not in XML
    	intersect = new HashSet<String>(work);
    	intersect.removeAll(xml);
    	if (intersect.size() != 0) {
    		for (String ss : intersect) {
    			System.out.println("WARNING: " + ss + " in workspace but not in XML.");
    		}
    	}
    	// In XML but not in workspace
    	intersect = new HashSet<String>(xml);
    	intersect.removeAll(work);
    	if (intersect.size() != 0) {
    		for (String ss : intersect) {
    			System.out.println("WARNING: " + ss + " in XML but not in workspace.");
    		}
    	}
    }
}
