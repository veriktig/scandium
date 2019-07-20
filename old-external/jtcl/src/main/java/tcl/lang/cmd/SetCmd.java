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

/*
 * SetCmd.java --
 *
 *	Implements the built-in "set" Tcl command.
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: SetCmd.java,v 1.2 1999/05/09 01:23:19 dejong Exp $
 *
 */

package tcl.lang.cmd;

import com.veriktig.scandium.api.state.InternalState;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TclException;
import tcl.lang.TclNumArgsException;
import tcl.lang.TclObject;

/*
 * This class implements the built-in "set" command in Tcl.
 */

public class SetCmd implements Command {

	/*
	 * ----------------------------------------------------------------------
	 * 
	 * cmdProc --
	 * 
	 * This procedure is invoked as part of the Command interface to process the
	 * "set" Tcl command. See the user documentation for details on what it
	 * does.
	 * 
	 * Results: None.
	 * 
	 * Side effects: See the user documentation.
	 * 
	 * ----------------------------------------------------------------------
	 */

	public void cmdProc(Interp interp, // Current interpreter.
			TclObject argv[]) // Argument list.
			throws TclException // A standard Tcl exception.
	{
		final boolean debug = false;

		if (argv.length == 2) {
			if (debug) {
				System.out.println("getting value of \"" + argv[1].toString()
						+ "\"");
				System.out.flush();
			}
			interp.setResult(interp.getVar(argv[1], 0));
		} else if (argv.length == 3) {
			if (debug) {
				System.out.println("setting value of \"" + argv[1].toString()
						+ "\" to \"" + argv[2].toString() + "\"");
				System.out.flush();
			}
			if (!argv[1].toString().equals("")) {
				// If it is an AppVariable, change it there, else add a UserVariable
				if (InternalState.isAppVariable(argv[1].toString())) {
					InternalState.replaceAppVariable(argv[1].toString(), argv[2].toString());
				} else {
					InternalState.addUserVariable(argv[1].toString(), argv[2].toString());
				}
			}	
			interp.setResult(interp.setVar(argv[1], argv[2], 0));
		} else {
			throw new TclNumArgsException(interp, 1, argv, "varName ?newValue?");
		}
	}

} // end SetCmd

