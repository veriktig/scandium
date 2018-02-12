package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class JoinCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/join.test";
		tclTestResource(resName);
	}
}
