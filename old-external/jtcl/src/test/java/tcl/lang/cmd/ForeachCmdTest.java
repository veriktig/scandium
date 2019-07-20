package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class ForeachCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/foreach.test";
		tclTestResource(resName);
	}
}
