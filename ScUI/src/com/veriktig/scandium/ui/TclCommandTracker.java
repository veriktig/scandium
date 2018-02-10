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

package com.veriktig.scandium.ui;

import java.util.Collection;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.providers.TclCommandProvider;

import tcl.lang.Extension;
import tcl.lang.Interp;

public class TclCommandTracker extends ServiceTracker<Object, Object> {
	private Interp interp;
	private Collection<TclCommand> commands;
	
	public TclCommandTracker(BundleContext context, String klass) {
		super(context, klass, null);
	}
	
	public void setInterp(Interp interp) {
		this.interp = interp;
	}
	
    @SuppressWarnings("rawtypes")
	@Override
    public Object addingService(final ServiceReference reference) {
    	
    	// Get the ClassLoader and the name of the package which contains Command's.
		@SuppressWarnings("unchecked")
		TclCommandProvider newService = (TclCommandProvider) context.getService(reference);

    	// Get the ClassLoader for the commands.
    	ClassLoader cl = newService.getClassLoader();
    	
    	// Load all the commands.
    	commands = newService.getCommands();
    	for (TclCommand command : commands) {
    		Extension.loadOnDemand(cl, interp, command.getName(), command.getImplClass());
    	}

		return reference;  // XXX    	
    }
    
    @SuppressWarnings("rawtypes")
	@Override
    public void removedService(final ServiceReference reference, final Object service) {
    	System.err.println("removedService");
    	// Remove the commands from interp
    	for (TclCommand command : commands) {
    		interp.deleteCommand(command.getName());
    	}
    	context.ungetService(reference);
    }
}
