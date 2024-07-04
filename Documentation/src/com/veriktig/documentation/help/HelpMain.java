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

package com.veriktig.documentation.help;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.Main;
import com.veriktig.documentation.Unmarshal;
import com.veriktig.documentation.help.generated.Help;
import com.veriktig.documentation.help.generated.HelpPackage;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class HelpMain {
    private static Set<CommandByBundle> workSet = new HashSet<CommandByBundle>();
    private static Set<CommandByBundle> xmlSet = new HashSet<CommandByBundle>();
    private static List<HelpByLocale> localeList = new ArrayList<HelpByLocale>();
    private static List<HelpByBundle> en_US_List = new ArrayList<HelpByBundle>();
    
    public static void make(File schemaFile, String xmlPath) {
        buildHelpClassFiles(schemaFile, xmlPath);
        buildCmdFiles();
        buildDocuments();
        check();
    }

    private static void buildHelpClassFiles(File schemaFile, String xmlPath) {
        String genPath = new String(HelpMain.class.getPackage().getName() + ".generated");

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

        File[] files = f.listFiles(xmlFilter);
        for (File file : files) {
            // Validate the XML against the schema
            Unmarshal um = new Unmarshal(schemaFile, file, genPath, Main.tmpPath);
            HelpPackage pkg = null;
            try {
                pkg = (HelpPackage)um.unmarshal();
            } catch (FactoryException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            String base_name = file.getName().replace(".xml", "");
            int underscore = base_name.indexOf("_");
            String language_locale = base_name.substring(underscore);
            String bundle = base_name.substring(0, underscore);
            if (language_locale.equals("_en_US")) {
                en_US_List.add(new HelpByBundle(bundle, pkg));
            }

            String java_class = new String("Help" + language_locale);
            String java_filename = new String(java_class + ".java");

            Set<Summary> summaries = new HashSet<Summary>();
            try {
                // Get summaries
                List<Help> helpList = pkg.getHelp();
                for (Help hh : helpList) {
                    summaries.add(new Summary(hh.getName(), hh.getHelp()));
                }
                // Create List of Help grouped by language_locale
                localeList.add(new HelpByLocale(language_locale, helpList));
                
                // Use bundle and getPackage to create file path
                File fd2 = new File(Main.workPath + "/" + bundle + "/src/main/java/" + pkg.getPackage().replace(".", "/") + "/" + java_filename);
                HelpClassFileFactory.make(fd2, pkg.getPackage(), java_class, summaries);
                Set<String> commands = new HashSet<String>();
                for (Summary summary : summaries) {
                    commands.add(summary.getName());
                }
                xmlSet.add(new CommandByBundle(bundle, commands));
                System.out.println("Built help document and Java class for " + bundle.toString() + ".");
            } catch (Exception e) {
                System.err.println("ERROR: Unable to create output from: " + file.getName());
                 return;
            }
        }
    }
        
    private static void buildDocuments() {
        // Find all the locales.
        Set<String> localeSet = new HashSet<String>();        
        for (HelpByLocale hh : localeList) {
            localeSet.add(hh.getLocale());
        }
        // Go through the locales and combine the commands
        for (String ll : localeSet) {
            List<Help> helpList = new ArrayList<Help>();
            for (HelpByLocale gg : localeList) {
                if (gg.getLocale().equals(ll)) {
                    helpList.addAll(gg.getHelp());
                }
            }
            String document = new String("Help" + ll + ".odt");
            File fd = new File(Main.outputPath + "/" + document);
            try {
                fd.createNewFile();
                ODFileFactory.make(fd,  helpList);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }        
    }
    
    private static void buildCmdFiles() {
        try {
            CmdFileFactory.make(en_US_List);
        } catch (FactoryException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
        
    private static void check() {
        // cd to workPath and search for TclCommand annotations
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
             Set<String> commands = new HashSet<String>();
            // Skip bundles with no commands
             if (bundle.toString().contains("Launcher")) continue;
             // Walk the tree looking for cmd package
             Path start = bundle.toPath();
             try {
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                    boolean look_here;
                    
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        if (look_here) {
                            if (file.toString().contains("Cmd")) {
                                String command = grep(file);
                                commands.add(command);
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        if (dir.toString().contains("/src/main/java/") && dir.toString().contains("/cmd") && !dir.toString().contains("tcl")) {
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
             workSet.add(new CommandByBundle(nb, commands));
         }
         
         diff(workSet, xmlSet);
         
         System.out.println("");
             
        return;
    }
    
    private static String grep(Path file) {
        try {
            List<String> lines = Files.readAllLines(file);
            for (String line : lines) {
                if (line.contains("@TclCommandName")) {
                    line = line.replace("@TclCommandName(\"", "");
                    line = line.replace("\")", "");
                    return (line);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private static void diff(Set<CommandByBundle> work, Set<CommandByBundle> xml) {
        // Compare bundles
        Set<String> xb = new HashSet<String>();
        for (CommandByBundle x : xml) {
            xb.add(x.getBundle());
        }
        Set<String> wb = new HashSet<String>();
        for (CommandByBundle w : work) {
            wb.add(w.getBundle());
        }
        compare("", wb, xb);
        
        // Compare commands
        for (CommandByBundle w : work) {
            for (CommandByBundle x : xml) {
                if (x.getBundle().equals(w.getBundle())) {
                    Set<String> xl = x.getCommands();
                    Set<String> wl = w.getCommands();
                    compare(x.getBundle(), wl, xl);
                }
            }
        }
    }
    
    private static void compare(String prefix, Set<String> work, Set<String> xml) {
        Set<String> intersect = new HashSet<String>();
        boolean command = true;
        
        if (prefix.equals("")) command = false;
        
        // In workspace but not in XML
        intersect = new HashSet<String>(work);
        intersect.removeAll(xml);
        if (intersect.size() != 0) {
            for (String ss : intersect) {
                if (!command) {
                    System.out.println("WARNING: " + ss + " in workspace but not in XML.");
                } else {
                    System.out.println("\t> " + ss + " in workspace but not in XML.");
                }
            }
        }
        // In XML but not in workspace
        intersect = new HashSet<String>(xml);
        intersect.removeAll(work);
        if (intersect.size() != 0) {
            for (String ss : intersect) {
                if (!command) {
                    System.out.println("WARNING: " + ss + " in XML but not in workspace.");
                } else {
                    System.out.println("\t< " + ss + " in XML but not in workspace.");
                }
            }
        }
    }
}
