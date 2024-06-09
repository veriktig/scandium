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


import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclObject;

import java.util.LinkedHashMap;
import java.util.Map;

import com.veriktig.scandium.api.SCAPI;
import com.veriktig.scandium.api.annotations.TclCommandName;
import com.veriktig.scandium.api.errors.ScException;
import com.veriktig.scandium.api.help.ScHelpFormatter;
import com.veriktig.scandium.api.help.ScParser;
import com.veriktig.scandium.api.state.InternalState;

@TclCommandName("print_versions")
public class PrintVersionsCmd implements Command {
    @Override
    public void cmdProc(Interp interp, TclObject[] argv) throws ScException {
// START AUTOMATICALLY GENERATED SECTION
// END AUTOMATICALLY GENERATED SECTION
        Map<String, String> result = new LinkedHashMap<String, String>();
        Map<String, String> bundle_versions = InternalState.getBundleVersions();
        
        bundle_versions.entrySet().stream()
        .sorted(Map.Entry.<String, String>comparingByKey())
        .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

        for (Map.Entry<String, String> entry : result.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
    }
}
