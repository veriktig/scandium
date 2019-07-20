package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class LsetCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/lset.test";
		tclTestResource(resName);
	}
	
	public void testLsetComp() throws Exception {
		String resName = "/tcl/lang/cmd/lsetComp.test";
		tclTestResource(resName);
	}
}
