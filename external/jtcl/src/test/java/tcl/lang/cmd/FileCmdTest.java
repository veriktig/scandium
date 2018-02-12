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

public class FileCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
				/* These fail because of error message differences; JVM can't doesn't know exactly why
				 * copy/rename fails
				 */
                /*
				"fCmd-6.17",
				"fCmd-9.14",
                */
				/*
				 * These tests fail primarily because link creation with 'file link' is not available because
				 * Java doesn't support it.  However, Java 1.7 will support it, so we should fix it at that time,
				 * or fix it with native code.
				 */
                /*
				"fCmd-28.9",
				"fCmd-28.11",
				"fCmd-28.12",
				"fCmd-28.13",
				"fCmd-28.15.2",
				"fCmd-28.16",
				"fCmd-28.17",
				"fCmd-28.18"
                */
				
	        }));
		String resName = "/tcl/lang/cmd/fCmd.test";
		tclTestResource(resName, expectedFailureList);
	}
}
