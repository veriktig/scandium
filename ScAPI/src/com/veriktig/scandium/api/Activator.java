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

import java.util.Collection;
import java.util.ListResourceBundle;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.finders.CommandFinder;
import com.veriktig.scandium.api.finders.ErrorFinder;
import com.veriktig.scandium.api.finders.HelpFinder;
import com.veriktig.scandium.api.providers.TclCommandProvider;
import com.veriktig.scandium.api.service.TclCommandService;
import com.veriktig.scandium.api.state.InternalState;

/**
 * 
 */
public class Activator implements BundleActivator {	
	ServiceRegistration<?> apiServiceRegistration;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
        // Get the base package name
        String BASE_PACKAGE = this.getClass().getPackage().getName();

		// Find commands
    	Collection<TclCommand> commands = CommandFinder.findCommands(context.getBundle(), BASE_PACKAGE);
    	ClassLoader cl = context.getBundle().adapt(BundleWiring.class).getClassLoader();

    	// Save bundle and classloader for use by commands
    	//InternalState.setClassLoader(cl);
		//InternalState.setBundle(context.getBundle());
		
		// Errors
		ListResourceBundle erb = (ListResourceBundle) ErrorFinder.findErrors(context.getBundle(), BASE_PACKAGE);
		InternalState.addErrors(erb);
		
        // Help
        ListResourceBundle rb = (ListResourceBundle) HelpFinder.findHelp(context.getBundle(), BASE_PACKAGE);
        InternalState.addHelp(rb);
		
		// Now start the service
    	TclCommandService scc = new TclCommandService(cl, commands);
		apiServiceRegistration = context.registerService(TclCommandProvider.class.getName(), scc, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		apiServiceRegistration.unregister();
	}
}
