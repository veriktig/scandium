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
 * UnsetCmd.java
 *
 * Copyright (c) 1997 Cornell University.
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: UnsetCmd.java,v 1.3 2009/07/08 15:49:25 rszulgo Exp $
 *
 */

package tcl.lang.cmd;

import com.veriktig.scandium.api.state.InternalState;

import tcl.lang.Command;
import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;
import tcl.lang.TclNumArgsException;
import tcl.lang.TclObject;

/**
 * This class implements the built-in "unset" command in Tcl.
 */

public class UnsetCmd implements Command {
	/**
	 * Tcl_UnsetObjCmd -> UnsetCmd.cmdProc
	 * 
	 * Unsets Tcl variable (s). See Tcl user documentation * for details.
	 * 
	 * @exception TclException
	 *                If tries to unset a variable that does not exist.
	 */

	public void cmdProc(Interp interp, TclObject[] objv) throws TclException {
		int firstArg = 1;
		String opt;
		boolean noComplain = false;

		if (objv.length < 2) {
			throw new TclNumArgsException(interp, 1, objv,
					"?-nocomplain? ?--? ?varName varName ...?");
		}

		/*
		 * Simple, restrictive argument parsing. The only options are -- and
		 * -nocomplain (which must come first and be given exactly to be an
		 * option).
		 */

		opt = objv[firstArg].toString();

		if (opt.startsWith("-")) {
			if ("-nocomplain".equals(opt)) {
				noComplain = true;
				opt = objv[++firstArg].toString();
			}
			if ("--".equals(opt)) {
				firstArg++;
			}
		}
		for (int i = firstArg; i < objv.length; i++) {
			try {
				InternalState.removeUserVariable(objv[i].toString());
				interp.unsetVar(objv[i], noComplain ? 0 : TCL.LEAVE_ERR_MSG);
			} catch (TclException e) {
				if (!noComplain) {
					throw e;
				} else {
					interp.resetResult();
				}
			}
		}

		return;
	}
}
