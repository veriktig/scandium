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

public class EncodingCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		LinkedList<String> expectedFailureList = new LinkedList<String>(Arrays.asList( new String[] {
				// These fail because of the description at http://www8.plala.or.jp/tkubota1/unicode-symbols-map.html
				// In the non-Unicode world, these codes map onto the same JIS X 0208 codepoints.  But in Unicode,
				// Cpe982's 0x81 0x91 maps to U+FFE0, while Shift_JIS's 0x81 0x91 and Euc-JP's 0xA1 0xF1 map on 
				// to U+00A2.  All three of those map to JIS X 0208 0x2171 and actually are the same "cents" sign.
				// When Java encodes Cpe982, 0x81 0x91 -> U+FFE0, and then decodes U+FFE0 to Shift_JIS, it's an invalid
				// character.  It seems that C Tcl either doesn't go through Unicode, or has hacked the maps.
                /*
				"encoding-25.2",
				"encoding-25.3",
				"encoding-25.4",
				"encoding-25.5",
				"encoding-25.9",
				"encoding-25.13"	
                */
	        }));
		String resName = "/tcl/lang/cmd/encoding.test";
		tclTestResource(TCLTEST_NAMEOFEXECUTABLE, resName, expectedFailureList);
	}
}
