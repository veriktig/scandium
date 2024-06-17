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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleWiring;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.finders.CommandFinder;
import com.veriktig.scandium.api.providers.TclCommandProvider;
import com.veriktig.scandium.api.service.TclCommandService;
import com.veriktig.scandium.api.state.AppVariables;
import com.veriktig.scandium.api.state.InternalState;
import com.veriktig.scandium.ui.shell.TclShell;
import com.veriktig.scandium.launcher.Main;

import tcl.lang.Interp;

public class Activator implements BundleActivator {
    ServiceRegistration<?> scsServiceRegistration;
    TclCommandTracker tracker;

    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        String scriptFile = null;
        String cli_commands = null;
        
        // Start the UI last
        context.getBundle().adapt(BundleStartLevel.class).setStartLevel(Integer.MAX_VALUE);

        // Get the base package name
        String BASE_PACKAGE = this.getClass().getPackage().getName();
        
        // Get the command line arguments
        ServiceReference<?> ref = context.getServiceReference("com.veriktig.scandium.launcher.Main");
        if (ref != null) {
            Main temp = (Main) context.getService(ref);
            Hashtable<String, Object> cli = temp.getCommandLineArguments();
            if (cli != null) {
                scriptFile = (String) cli.get("file");
                cli_commands = (String) cli.get("command");
            }
        }
        Interp interp = new Interp();
        Interp.context = context;
        InternalState.setInterp(interp);
        
        // Create application variables
        AppVariables.create();
        
        // Find my own commands and start a TclCommandProvider service.
        Collection<TclCommand> commands = CommandFinder.findCommands(context.getBundle(), BASE_PACKAGE);
        ClassLoader cl = context.getBundle().adapt(BundleWiring.class).getClassLoader();

        // Now start the service
        TclCommandService scs = new TclCommandService(cl, commands);
        scsServiceRegistration = context.registerService(TclCommandProvider.class.getName(), scs, null);
        
        // Start the ServiceTracker to monitor TclCommandProvider's
        tracker = new TclCommandTracker(context, TclCommandProvider.class.getName());
        tracker.setInterp(interp);
        tracker.open();
        
        // Store the bundles and their versions
        Bundle[] bundles = context.getBundles();
        Map<String, String> bundle_versions = new HashMap<String, String>();
        for (Bundle bb : bundles) {
            bundle_versions.put(bb.getSymbolicName(), bb.getVersion().toString());
        }
        InternalState.setBundleVersions(bundle_versions);
        
        // Now start the shell.
        new TclShell(interp, scriptFile, cli_commands);
    }
    
    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        scsServiceRegistration.unregister();
    }

}
