package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class PwdCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/pwd.test";
		tclTestResource(resName);
	}
}
