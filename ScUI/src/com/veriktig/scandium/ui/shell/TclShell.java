/*
 * Copyright 2018 Veriktig, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Based on tcl.lang.Shell

package com.veriktig.scandium.ui.shell;

import tcl.lang.ConsoleThread;
import tcl.lang.Interp;
import tcl.lang.Notifier;
import tcl.lang.TCL;
import tcl.lang.TclException;
import tcl.lang.TclObject;
import tcl.lang.cmd.EncodingCmd;

public class TclShell {
	
	public TclShell(Interp interp, String scriptFile, String cli_commands) {
		String encoding = EncodingCmd.systemTclEncoding;
		
		try {
			if (scriptFile == null) {
				interp.setVar("argv0", "tcl.lang.Shell", TCL.GLOBAL_ONLY);
				interp.setVar("tcl_interactive", "1", TCL.GLOBAL_ONLY);
			} else {
				interp.setVar("argv0", scriptFile.toString(), TCL.GLOBAL_ONLY);
				interp.setVar("tcl_interactive",  "0",  TCL.GLOBAL_ONLY);
			}
		} catch (TclException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (scriptFile != null) {
			int exitCode = 0;
			try {
				String javaEncoding = EncodingCmd.getJavaName(encoding);
				if (javaEncoding==null) {
					System.err.println("unknown encoding \""
							+ encoding + "\"");
					exitCode = 2;
				} else {
					interp.evalFile(scriptFile, javaEncoding);
				}
			} catch (TclException e) {
				int code = e.getCompletionCode();
				if (code == TCL.RETURN) {
					code = interp.updateReturnInfo();
					if (code != TCL.OK) {
						System.err.println("command returned bad code: " + code);
						exitCode = 2;
					}
				} else if (code == TCL.ERROR) {
					try {
						TclObject errorInfo = interp.getVar("errorInfo",TCL.GLOBAL_ONLY);
						System.err.println(errorInfo.toString());
					} catch (TclException e1) {
						System.err.println(interp.getResult().toString());				
					}
					exitCode = 1;
				} else {
					System.err.println("command returned bad code: " + code);
					exitCode = 2;
				}
			}

			// Note that if the above interp.evalFile() returns the main
			// thread will exit. This may bring down the VM and stop
			// the execution of Tcl.
			//
			// If the script needs to handle events, it must call
			// vwait or do something similar.
			//
			// Note that the script can create AWT widgets. This will
			// start an AWT event handling thread and keep the VM up. However,
			// the interpreter thread (the same as the main thread) would
			// have exited and no Tcl scripts can be executed.

			interp.dispose();
			System.exit(exitCode);
		}
		
		if (cli_commands != null) {
			int exitCode = 0;
			try {
				interp.eval(cli_commands);
			} catch (TclException e) {
				int code = e.getCompletionCode();
				if (code == TCL.RETURN) {
					code = interp.updateReturnInfo();
					if (code != TCL.OK) {
						System.err.println("command returned bad code: " + code);
						exitCode = 2;
					}
				} else if (code == TCL.ERROR) {
					try {
						TclObject errorInfo = interp.getVar("errorInfo",TCL.GLOBAL_ONLY);
						System.err.println(errorInfo.toString());
					} catch (TclException e1) {
						System.err.println(interp.getResult().toString());				
					}
					exitCode = 1;
				} else {
					System.err.println("command returned bad code: " + code);
					exitCode = 2;
				}
				interp.dispose();
				System.exit(exitCode);
			}
		}
		
		// We are running in interactive mode. Start the ConsoleThread
		// that loops, grabbing stdin and passing it to the interp.

		ConsoleThread consoleThread = new ConsoleThread(interp);
		consoleThread.setDaemon(true);
		consoleThread.start();

		// Loop forever to handle user input events in the command line.
		// This method will loop until "exit" is called or the interp
		// is interrupted.

		Notifier notifier = interp.getNotifier();
		try {
			Notifier.processTclEvents(notifier);
		} finally {
			interp.dispose();
		}
	}
}
