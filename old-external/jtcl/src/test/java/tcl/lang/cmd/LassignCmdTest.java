package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class LassignCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/lassign.test";
		tclTestResource(resName);
	}
}