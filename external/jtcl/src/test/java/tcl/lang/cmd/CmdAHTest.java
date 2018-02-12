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

public class CmdAHTest extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList(new String[] {
			// these fail because 'file atime', 'file mtime', 'file stat' and 'file link' are incomplete
		    // because of JVM restrictions.  Could fix with Java 1.7 or native code
            /*
			"cmdAH-20.2", "cmdAH-24.3", "cmdAH-28.3", "cmdAH-28.4", "cmdAH-28.8", "cmdAH-28.12", "cmdAH-29.4.1"
            */
		}));

		String resName = "/tcl/lang/cmd/cmdAH.test";
		tclTestResource(resName, expectedFailureList);
	}
}
