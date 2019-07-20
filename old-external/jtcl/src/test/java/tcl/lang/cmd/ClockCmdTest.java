package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class ClockCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/clock.test";
		tclTestResource(resName);
	}
}
