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

package com.veriktig.scandium.api;
/**
 * Convenience class for use with tcl.lang.Extension
 */
public class TclCommand {
	protected final String command;
	protected final String clsName;
	
	/**
	 * Default constructor. Requires a Tcl command name and the 
	 * dot representation of the class that implements the command.
	 * 
	 * @param command
	 * 		Tcl command name
	 * @param clsName
	 * 		Dot representation of class, e.g.
	 * 		myservice.cmd.HelloWorldCmd	
	 */
	public TclCommand(String command, String clsName) {
		this.command = command;
		this.clsName = clsName;
	}
	
	public String getName() {
		return command;
	}
	
	public String getImplClass() {
		return clsName;
	}
}
