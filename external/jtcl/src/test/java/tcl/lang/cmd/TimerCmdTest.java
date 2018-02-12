package tcl.lang.cmd;

import com.veriktig.scandium.internal.test.TclCmdTest;

public class TimerCmdTest extends TclCmdTest {
	public void testCmd() throws Exception {
		String resName = "/tcl/lang/cmd/timer.test";
		tclTestResource(resName);
	}
}