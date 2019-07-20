package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class HistoryCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/history.test";
		tclTestResource(resName);
	}
}
