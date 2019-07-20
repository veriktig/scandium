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
 * PackageNameException.java
 *
 * Copyright (c) 2006 Moses DeJong
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 * 
 * RCS: @(#) $Id: PackageNameException.java,v 1.1 2006/04/13 07:36:50 mdejong Exp $
 *
 */

package tcl.lang;

/**
 * This exception is thrown by the TclClassLoader when an attempt to load a
 * class from any package that starts with the java.* or tcl.* prefix is made.
 * Classes in these packages can be loaded with the system class loader, but not
 * the TclClassLoader.
 */

public class PackageNameException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	String className;

	public PackageNameException(String msg, String className) {
		super(msg);
		this.className = className;
	}
}
