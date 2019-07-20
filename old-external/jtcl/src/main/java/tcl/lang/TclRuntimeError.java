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
 * TclRuntimeError.java
 *
 * Copyright (c) 1997 Sun Microsystems, Inc.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: TclRuntimeError.java,v 1.1.1.1 1998/10/14 21:09:14 cvsadmin Exp $
 *
 */

package tcl.lang;

/**
 * Signals that a unrecoverable run-time error in the interpreter. Similar to
 * the panic() function in C.
 */
public class TclRuntimeError extends RuntimeException {
	private static final long serialVersionUID = 1L;
	/**
	 * Constructs a TclRuntimeError with the specified detail message.
	 * 
	 * @param s
	 *            the detail message.
	 */
	public TclRuntimeError(String s) {
		super(s);
	}

    /**
     * Constructs a TclRuntimeError with the specified detail message and
     * cause.
     *
     * @param msg   the detail message.
     * @param cause the cause.
     */
    public TclRuntimeError(String msg, Throwable cause) {
        super(msg, cause);
}
}
