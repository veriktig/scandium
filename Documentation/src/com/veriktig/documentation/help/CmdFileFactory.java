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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.veriktig.documentation.FactoryException;
import com.veriktig.documentation.FileFactory;
import com.veriktig.documentation.Main;
import com.veriktig.documentation.help.generated.Help;
import com.veriktig.documentation.help.generated.HelpPackage;
import com.veriktig.documentation.help.generated.Option;
import com.veriktig.documentation.help.generated.OptionGroup;

public class CmdFileFactory extends FileFactory {
    public static void make(List<HelpByBundle> pkgList) throws FactoryException {
        for (HelpByBundle bb : pkgList) {
            String bundle = bb.getBundle();
            HelpPackage helpPkg = bb.getHelp();
            String pkg = helpPkg.getPackage();
            pkg = pkg.replace(".", "/");
            pkg = pkg.replace("help", "cmd");
            List<Help> helpList = helpPkg.getHelp();
            for (Help help : helpList) { 
                String name = camelCase(help.getName());
                String filename = new String(Main.workPath + "/" + bundle + "/src/" + pkg + "/" + name + "Cmd.java");
                File fd = new File(filename);
                if (fd.exists()) {
                    updateFile(fd, help);
                } else {
                    buildTemplate(fd, helpPkg.getPackage(), name, help);
                }
            }
        }
    }

    private static void buildTemplate(File fd, String pkg, String name, Help help) {
        List<Option> optionList = help.getOption();
        List<OptionGroup> groupList = help.getOptionGroup();
        pkg = pkg.replace("help", "cmd");        
        
        FileFactory ff = new FileFactory();
        ff.createOut(fd, false);
        ff.printIndented(0, FileFactory.copyright);
        ff.printIndented(0, "");
        ff.printIndented(0, "package " + pkg + ";");
        ff.printIndented(0, "");
        if ((optionList.size() > 0 || groupList.size() > 0)) {
            ff.printIndented(0, "import java.util.List;");
            ff.printIndented(0, "import java.util.ArrayList;");
        }
        ff.printIndented(0, "");
        ff.printIndented(0, "import tcl.lang.Command;");
        ff.printIndented(0, "import tcl.lang.Interp;");
        ff.printIndented(0, "import tcl.lang.TclObject;");

        if ((optionList.size() > 0 || groupList.size() > 0)) {
            ff.printIndented(0, "");
            ff.printIndented(0, "import org.apache.commons.cli.Option;");
            ff.printIndented(0, "import org.apache.commons.cli.Options;");
            if (groupList.size() > 0) {
                ff.printIndented(0, "import org.apache.commons.cli.OptionGroup;");
            }
            ff.printIndented(0, "import org.apache.commons.cli.CommandLine;");
            ff.printIndented(0, "import org.apache.commons.cli.CommandLineParser;");
            ff.printIndented(0, "import org.apache.commons.cli.HelpFormatter;");
            ff.printIndented(0, "import org.apache.commons.cli.ParseException;");
        }
        ff.printIndented(0, "");
        ff.printIndented(0, "import com.veriktig.scandium.api.SCAPI;");
        ff.printIndented(0, "import com.veriktig.scandium.api.annotations.TclCommandName;");
        ff.printIndented(0, "import com.veriktig.scandium.api.errors.ScException;");
        ff.printIndented(0, "import com.veriktig.scandium.api.help.ScParser;");
        ff.printIndented(0, "import com.veriktig.scandium.api.help.ScHelpFormatter;");
        ff.printIndented(0, "");
        ff.printIndented(0, "@TclCommandName(\"" + help.getName() + "\")");
        ff.printIndented(0, "public class " + name + "Cmd implements Command {");
        ff.printIndented(1, "@Override");
        ff.printIndented(1, "public void cmdProc(Interp interp, TclObject[] argv) throws ScException {");
        ff.printIndented(0, FileFactory.startSection);
        buildOptions(ff, help);
        ff.printIndented(0, FileFactory.endSection);
        ff.printIndented(1, "}");
        ff.printIndented(0, "}");
        ff.done();
    }
    
    private static void updateFile(File fd, Help help) {
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
            buildOptions(ff, help);
            for (String post : postcache) {
                ff.print(post);
            }
            ff.done();
            in.close();
        } catch (IOException e) {
            System.err.println("ERROR: IOException processing " + fd.getName());
        }
    }
    
    private static void buildOptions(FileFactory ff, Help help) {
        List<Option> optionList = help.getOption();
        List<OptionGroup> groupList = help.getOptionGroup();
        
        if (optionList.size() > 0 || groupList.size() > 0) {
            ff.printIndented(2, "CommandLine cmd = null;");
            ff.printIndented(2, "Option op = null;");
            if (groupList.size() > 0) {
                ff.printIndented(2, "OptionGroup opg = null;");
            }
            ff.printIndented(2, "Options options = new Options();");
            ff.printIndented(2, "String[] arg_array;");
            ff.printIndented(2, "List<String> args = new ArrayList<String>();");
            ff.printIndented(0, "");
            ff.printIndented(2, "int count = 0;");
            // Convert argv to args
            ff.printIndented(2, "for (TclObject to : argv) {");
            ff.printIndented(3, "if (count++ == 0) continue;");
            ff.printIndented(3, "args.add(to.toString());");
            ff.printIndented(2, "}");
            ff.printIndented(2, "arg_array = args.toArray(new String[args.size()]);");
            ff.printIndented(0, "");
            for (Option option : optionList) {
                Boolean hasArg = option.getArgName().equals("") ? false : true;
                ff.printIndented(2, "op = new Option(\"" + option.getOption() + "\", " + hasArg.toString() + ", \"" + option.getDesc() + "\");");
                if (hasArg) {
                    ff.printIndented(2, "op.setArgName(\"" + option.getArgName() + "\");");
                }
                Boolean isRequired = option.isRequired();
                ff.printIndented(2, "op.setRequired(" + isRequired.toString() + ");");
                if (option.getType().equals("list")) {
                    ff.printIndented(2, "op.setArgs(Option.UNLIMITED_VALUES);");
                }
                ff.printIndented(2, "options.addOption(op);");
            }
            ff.printIndented(0, "");
            for (OptionGroup group : groupList) {
                ff.printIndented(2, "opg = new OptionGroup();");
                List<Option> optList = group.getOption();
                for (Option option : optList) {
                    Boolean hasArg = option.getArgName().equals("") ? false : true;
                    ff.printIndented(2, "op = new Option(\"" + option.getOption() + "\"," + hasArg.toString() + ", \"" + option.getDesc() + "\");");
                    if (hasArg) {
                        ff.printIndented(2, "op.setArgName(\"" + option.getArgName() + "\");");
                    }
                    Boolean isRequired = option.isRequired();
                    ff.printIndented(2, "op.setRequired(" + isRequired.toString() + ");");
                    ff.printIndented(2, "opg.addOption(op);");
                }
                ff.printIndented(2, "options.addOptionGroup(opg);");
            }
            ff.printIndented(2, "CommandLineParser parser = new ScParser();");
            ff.printIndented(2, "try {");
            ff.printIndented(3, "cmd = parser.parse(options, arg_array);");
            ff.printIndented(2, "} catch (ParseException pe) {");
            ff.printIndented(3, "HelpFormatter formatter = new ScHelpFormatter();");
            ff.printIndented(3, "String header = new String(\"" + help.getHelp() + "\" + \"\\n\\n\");");
            ff.printIndented(3, "String footer = new String(\"\\nPlease report issues to support@veriktig.com\\n\");");
            ff.printIndented(3, "formatter.printHelp(\"" + help.getName() + "\", header, options, footer, true);");
            ff.printIndented(3, "throw new ScException(\"API-1000\", null);");
            ff.printIndented(2, "}");
        }
    }
    
    private static String camelCase(String name) {
        String value = new String();
        
        String[] pieces = name.split("_");
        for (String ss : pieces) {
            String firstChar = ss.substring(0, 1);
            firstChar = firstChar.toUpperCase();
            String rest = ss.substring(1);
            value = value.concat(firstChar + rest);
        }
        return value;
    }
}
