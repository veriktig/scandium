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

package com.veriktig.scandium.api.service;

import java.util.Collection;
import java.util.HashSet;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.providers.TclCommandProvider;

public class TclCommandService implements TclCommandProvider {
	private Collection<TclCommand> commands = new HashSet<TclCommand>();
	private ClassLoader cl;
	
	public TclCommandService(ClassLoader cl, Collection<TclCommand> cmds) {
		this.cl = cl;
		this.commands.addAll(cmds);
	}

	@Override
	public Collection<TclCommand> getCommands() {
    	return commands;	
	}

	@Override
	public ClassLoader getClassLoader() {
		return cl;
	}
}
