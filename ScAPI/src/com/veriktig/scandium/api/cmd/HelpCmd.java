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
import java.util.ArrayList;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclObject;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
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

@TclCommandName("help")
public class HelpCmd implements Command {
    @Override
    public void cmdProc(Interp interp, TclObject[] argv) throws ScException {
// START AUTOMATICALLY GENERATED SECTION
        CommandLine cmd = null;
        Option op = null;
        Options options = new Options();
        String[] arg_array;
        List<String> args = new ArrayList<String>();

        int count = 0;
        for (TclObject to : argv) {
            if (count++ == 0) continue;
            args.add(to.toString());
        }
        arg_array = args.toArray(new String[args.size()]);

        op = new Option("null", true, "Name of command(s) to display help about.");
        op.setArgName("command_name");
        op.setRequired(true);
        op.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(op);

        CommandLineParser parser = new ScParser();
        try {
            cmd = parser.parse(options, arg_array);
        } catch (ParseException pe) {
            HelpFormatter formatter = new ScHelpFormatter();
            String header = new String("Displays a concise description of a command." + "\n\n");
            String footer = new String("\nPlease report issues to support@veriktig.com\n");
            formatter.printHelp("help", header, options, footer, true);
            throw new ScException("API-1000", null);
        }
// END AUTOMATICALLY GENERATED SECTION
        String[] command_list = cmd.getOptionValues("null");
        List<String> commands = SCAPI.expandLists(command_list);
        
    	boolean foundHelp = false;
        for (String ss : commands) {
            String help = InternalState.getHelp(ss);
            if (help != null) {
            	System.out.println(ss + ": " + help);
            	foundHelp = true;
            } else {
            	System.out.println(ss + ": Unknown command.");
            	continue;
            }
        }
        
        if (foundHelp) {
            interp.setResult(SCAPI.SUCCESS);
        } else {
            interp.setResult(SCAPI.ERROR);
        }
    }
}
