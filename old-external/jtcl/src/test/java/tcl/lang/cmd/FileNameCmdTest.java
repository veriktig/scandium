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

public class FileNameCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
				/*
				 * These tests fail because 'file link -symbolic' doesn't work in Java.  If someday we add
				 * platform-specific native code, file link -symbolic and glob -types l can be fixes, and these
				 * test should work.
				 */
                /*
				"filename-11.17.2",
				"filename-11.17.3",
				"filename-11.17.4",
	            "filename-11.17.7",
	            "filename-11.17.8" 
                */
	        }));
		
		String resName = "/tcl/lang/cmd/fileName.test";
		
		// mostly tests the 'glob' command and some 'file ...' tests
		tclTestResource(resName,expectedFailureList);
	}
}
