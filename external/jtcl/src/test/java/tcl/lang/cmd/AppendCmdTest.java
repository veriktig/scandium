package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class AppendCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/append.test";
		tclTestResource(resName);
	}
	
	public void testAppendComp() throws Exception {
		String resName = "/tcl/lang/cmd/appendComp.test";
		tclTestResource(resName);
	}
}