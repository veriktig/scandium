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

public class IfCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
			// widespread, pesky "invoked from within" instead of "while executing" in error message,
			// couled with difference between compiled and non-compiled version of 'if'
            /*
			"if-1.3", "if-1.10", "if-2.4", "if-3.4", "if-5.3", "if-5.10", "if-6.4", "if-7.4",
            */
			// this difference in expr error message would take significant coding to fix
            /*
			"if-10.6"
            */
		}));
			
		String resName = "/tcl/lang/cmd/if.test";
		tclTestResource(resName, expectedFailureList);
	}
}
