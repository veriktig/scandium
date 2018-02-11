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

package com.veriktig.scandium.api.cmd;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclObject;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

import com.veriktig.scandium.api.SCAPI;
import com.veriktig.scandium.api.annotations.TclCommandName;
import com.veriktig.scandium.api.errors.ScException;
import com.veriktig.scandium.api.help.ScHelpFormatter;
import com.veriktig.scandium.api.help.ScParser;
import com.veriktig.scandium.api.state.InternalState;

@TclCommandName("printvar")
public class PrintvarCmd implements Command {
    @Override
    public void cmdProc(Interp interp, TclObject[] argv) throws ScException {
// START AUTOMATICALLY GENERATED SECTION
        CommandLine cmd = null;
        Option op = null;
        OptionGroup opg = null;
        Options options = new Options();
        String[] arg_array;
        List<String> args = new ArrayList<String>();

        int count = 0;
        for (TclObject to : argv) {
            if (count++ == 0) continue;
            args.add(to.toString());
        }
        arg_array = args.toArray(new String[args.size()]);

        op = new Option("null", true, "Pattern for filtering names. Supports * and ? wildcards.");
        op.setArgName("pattern");
        op.setRequired(false);
        options.addOption(op);

        opg = new OptionGroup();
        op = new Option("user_defined",false, "Show only user defined variables..");
        op.setRequired(false);
        opg.addOption(op);
        op = new Option("application",false, "Show only variables defined by the application.");
        op.setRequired(false);
        opg.addOption(op);
        options.addOptionGroup(opg);
        CommandLineParser parser = new ScParser();
        try {
            cmd = parser.parse(options, arg_array);
        } catch (ParseException pe) {
            HelpFormatter formatter = new ScHelpFormatter();
            String header = new String("Displays the value of variables." + "\n\n");
            String footer = new String("\nPlease report issues to support@veriktig.com\n");
            formatter.printHelp("printvar", header, options, footer, true);
            throw new ScException("API-1000", null);
        }
// END AUTOMATICALLY GENERATED SECTION
        Map<String, String> app_variables = InternalState.getAppVariables();
        Map<String, String> user_variables = InternalState.getUserVariables();

        if (cmd.hasOption("null")) {
            String pattern = cmd.getOptionValue("null");
            if (cmd.hasOption("application")) {
                for (Map.Entry<String, String> var : app_variables.entrySet()) {
                    if (SCAPI.match(var.getKey(), pattern)) {
                        System.out.println(var.getKey() + " = " + var.getValue());
                    }
                }
            } else if (cmd.hasOption("user_defined")) {
                for (Map.Entry<String, String> var : user_variables.entrySet()) {
                    if (SCAPI.match(var.getKey(), pattern)) {
                        System.out.println(var.getKey() + " = " + var.getValue());
                    }
                }
            } else {
                for (Map.Entry<String, String> var : app_variables.entrySet()) {
                    if (SCAPI.match(var.getKey(), pattern)) {
                        System.out.println(var.getKey() + " = " + var.getValue());
                    }
                }
                for (Map.Entry<String, String> var : user_variables.entrySet()) {
                    if (SCAPI.match(var.getKey(), pattern)) {
                        System.out.println(var.getKey() + " = " + var.getValue());
                    }
                }
            }
            System.out.flush();
        } else {
            // No arguments, print all variables
            if (cmd.hasOption("application")) {
                for (Map.Entry<String, String> var : app_variables.entrySet()) {
                    System.out.println(var.getKey() + " = " + var.getValue());
                }
            } else if (cmd.hasOption("user_defined")) {
                for (Map.Entry<String, String> var : user_variables.entrySet()) {
                    System.out.println(var.getKey() + " = " + var.getValue());
                }
            } else {
                for (Map.Entry<String, String> var : app_variables.entrySet()) {
                    System.out.println(var.getKey() + " = " + var.getValue());
                }
                for (Map.Entry<String, String> var : user_variables.entrySet()) {
                    System.out.println(var.getKey() + " = " + var.getValue());
                }
            }
            System.out.flush();
        }
        interp.setResult("");
    }
}
