package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class StringCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/string.test";
		tclTestResource(resName);
	}
	
	public void testStringComp() throws Exception {
		String resName = "/tcl/lang/cmd/stringComp.test";
		tclTestResource(resName);
	}
}
