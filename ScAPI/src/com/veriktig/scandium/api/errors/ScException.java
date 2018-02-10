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

package com.veriktig.scandium.api.errors;

import com.veriktig.scandium.api.SCAPI;
import com.veriktig.scandium.api.state.InternalState;

import tcl.lang.Interp;
import tcl.lang.TCL;
import tcl.lang.TclException;

/**
 * This exception is used to report wrong number of arguments in Tcl scripts.
 */

public class ScException extends TclException {
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a TclException with the appropiate Tcl error message for having
	 * the wring number of arguments to a Tcl command.
	 * <p>
	 * Example:
	 * 
	 * <pre>
	 * 
	 * if (argv.length != 3) {
	 * 	throw new TclNumArgsException(interp, 1, argv, &quot;option name&quot;);
	 * }
	 * </pre>
	 * 
	 * @param interp
	 *            current Interpreter.
	 * @param argc
	 *            the number of arguments to copy from the offending command to
	 *            put into the error message.
	 * @param argv
	 *            the arguments of the offending command.
	 * @param message
	 *            extra message to appear in the error message that explains the
	 *            proper usage of the command.
	 * @exception TclException
	 *                is always thrown.
	 */

	public ScException(String error_code, Object[] args) {
		super(TCL.ERROR);

		Interp interp = InternalState.getInterp();
		ErrorMessage error = InternalState.getError(error_code);
		// If getMessage() contains %M, get a stack trace and find the calling method name
		StackTraceElement[] frames = getStackTrace();
		String method = frames[0].getMethodName();
		String message = new String("");
		if (error != null) {
		    String format = error.getMessage();
		    format = format.replace("%M", method);
		    message = String.format(format, args);
		    String output = new String(error_code + " (" + error.getSeverity().toString() + "): " + message);
		    SCAPI.ErrorType etype = error.getSeverity();
		    if ((etype == SCAPI.ErrorType.ERROR) ||
			  (etype == SCAPI.ErrorType.SEVERE) ||
			  (etype == SCAPI.ErrorType.FATAL)) {
			    interp.setResult(output);
		    } else {
			    System.out.println(output);
		    }
		}
	}
}
