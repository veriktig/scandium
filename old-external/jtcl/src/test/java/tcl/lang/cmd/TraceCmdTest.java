package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class TraceCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/trace.test";
		tclTestResource(resName);
	}
}
