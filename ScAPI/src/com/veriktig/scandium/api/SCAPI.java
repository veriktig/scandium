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

package com.veriktig.scandium.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.veriktig.scandium.api.state.InternalState;

import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;

/**
 * @author fred
 *
 */
public class SCAPI {
    // Return values from commands
    public static final int ERROR = 0;
    public static final int SUCCESS = 1;
    
    // Used for arguments without and option name in commands.
    public static final String NULL_OPTION = "null";

    // Error types
    public static enum ErrorType {
        INFO("info"),
        WARNING("warning"),
        ERROR("error"),
        SEVERE("severe"),
        FATAL("fatal");
        
        String name;
        ErrorType(String name) { this.name = name; }
        public String toString() { return name; }
    };
    
    /**
     * Alias for setting application variables so it easy to find the
     * definitions. Documentation uses this to check the XML definitions
     * against the get/set in the Java source code.
     * 
     * @param key Name of the variable
     * @param value Value of the variable
     * @return void
     */
    public static void setAppVariable(String key, String value) {
        try {
            Interp interp = InternalState.getInterp();
            InternalState.addAppVariable(key, value);
            interp.setVar(key, value, TCL.GLOBAL_ONLY);
        } catch (TclException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Expand command options that are presented as lists.
     * A Tcl list needs to be expanded by whitespace, while
     * simple lists are expanded by number of args.
     * 
     * @param args Array of strings from list or string
     * @return List<String> of the expanded args
     */
    public static List<String> expandLists(String[] args) {
        List<String> list = new ArrayList<String>();

        if (args == null) {
            return list;
        } else {
            if (args.length == 1) {
                Scanner ws = new Scanner(args[0]);
                while (ws.hasNext()) {
                    list.add(ws.next());
                }
                ws.close();
                return list;
            } else {
                for (String ss : args) {
                    list.add(ss);
                }
                return list;
            }
        }
    }
    
    /**
     * String matching using the wildcards * and ?.
     * 
     * @param text String to test
     * @param pattern Pattern to test against (can contain wildcards * and ?)
     * @return true if the text matches the pattern
     */
    public static boolean match(String text, String pattern) {
        return text.matches(pattern.replace("?", ".?").replace("*", ".*?"));
    }
}
