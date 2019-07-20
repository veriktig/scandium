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
 * ApplyCmd.java --
 *
 * Copyright (C) 2010 Neil Madden &lt;nem@cs.nott.ac.uk&gt.
 *
 * See the file "license.terms" for information on usage and redistribution of
 * this file, and for a DISCLAIMER OF ALL WARRANTIES.
 *
 * RCS: @(#) $Id$
 */

package tcl.lang.cmd;

import tcl.lang.*;

/**
 * Implementation of the [apply] command.
 *
 * @author  Neil Madden &lt;nem@cs.nott.ac.uk&gt;
 * @version $Revision$
 */
public class ApplyCmd implements Command {

	public void cmdProc(Interp interp, TclObject[] objv) throws TclException {
		if (objv.length < 2) {
			throw new TclNumArgsException(interp, 1, objv, "lambdaExpr ?arg ...?");
		}
		TclLambda.apply(interp, objv[1], objv);
	}

}
