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

package tcl.lang.cmd;

import java.util.Arrays;
import java.util.LinkedList;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class InterpCmdTest extends TclCmdTest {
	LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
			// different list from [interp hidden] because the 'load' command is not implemented in JTCL
            /*
			"interp-21.5",
			"interp-21.8",
            */
			// recursion limit is actually inherited by the slave interpreter as expected, but the nested proc
			// call count is one fewer in JTCL than in TCL.  My best guess is that this is due to TCL compiling
			// some of the contents of the recursive proc in these tests, so that there's no eval internal
			// to the proc (DJB)
            /*
			"interp-29.4.1",
			"interp-29.4.2"
            */
		}));
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/interp.test";
		tclTestResource(resName, expectedFailureList);
	}
}
