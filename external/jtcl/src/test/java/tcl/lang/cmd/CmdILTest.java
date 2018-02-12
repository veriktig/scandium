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

public class CmdILTest  extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList(new String[] {
				// These fail because JTCL uses a different sorting algorithm than C Tcl.
				// Identical elements, according to the lsort comparison method, might be swapped
				// in JTCL relative to C Tcl - for example 'lsort -index 0 {{a b} {a c}}' can sort into either order
				// and be correctly sorted.  
				// Also, the compare function might not be called in the same order,
				// which makes cmdIL-3.15 fail.
                /*
				"cmdIL-1.6", "cmdIL-1.23", "cmdIL-3.15" 
                */
			}));
		String resName = "/tcl/lang/cmd/cmdIL.test";
		tclTestResource(resName, expectedFailureList);
	}
}
