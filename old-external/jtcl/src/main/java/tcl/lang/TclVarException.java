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
 * TclNumArgsException.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: TclVarException.java,v 1.1.1.1 1998/10/14 21:09:19 cvsadmin Exp $
 *
 */

package tcl.lang;

/**
 * This exception is used to report variable errors in Tcl.
 */

public class TclVarException extends TclException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates an exception with the appropiate Tcl error message to indicate an
	 * error with variable access.
	 * 
	 * @param interp
	 *            currrent interpreter.
	 * @param name1
	 *            first part of a variable name.
	 * @param name2
	 *            second part of a variable name. May be null.
	 * @param operation
	 *            either "read" or "set".
	 * @param reason
	 *            a string message to explain why the operation fails..
	 */

	public TclVarException(Interp interp, String name1, String name2,
			String operation, String reason) {
		super(TCL.ERROR);
		if (interp != null) {
			interp.resetResult();
			if (name2 == null) {
				interp.setResult("can't " + operation + " \"" + name1 + "\": "
						+ reason);
			} else {
				interp.setResult("can't " + operation + " \"" + name1 + "("
						+ name2 + ")\": " + reason);
			}
		}
	}
}
