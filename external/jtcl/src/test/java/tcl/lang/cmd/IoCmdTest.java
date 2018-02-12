package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class IoCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/ioCmd.test";
		tclTestResource(resName);
	}
}