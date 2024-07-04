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

package com.veriktig.scandium.internal.test;

import java.util.Collection;
import java.util.ListResourceBundle;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.finders.CommandFinder;
import com.veriktig.scandium.api.finders.HelpFinder;
import com.veriktig.scandium.api.providers.TclCommandProvider;
import com.veriktig.scandium.api.service.TclCommandService;
import com.veriktig.scandium.internal.test.state.TestInternalState;
import com.veriktig.scandium.api.state.InternalState;

/**
 * 
 */
public class Activator implements BundleActivator {
    ServiceRegistration<?> sccServiceRegistration;
    
    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        // Get the base package name
        String BASE_PACKAGE = this.getClass().getPackage().getName();

        // Find commands
        Bundle bundle = context.getBundle();
        TestInternalState.setBundle(bundle);
        Collection<TclCommand> commands = CommandFinder.findCommands(bundle, BASE_PACKAGE);

        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        ClassLoader cl = bundleWiring.getClassLoader();

        // Find test classes
        String pkg = new String("tcl.lang.cmd");
        Collection<String> classes = bundleWiring.listResources(pkg.replace('.','/'), "*.class", BundleWiring.FINDENTRIES_RECURSE);
        TestInternalState.setTestClasses(classes);

        // Find test resources
        pkg = new String("resources.tcl.lang.cmd");
        Collection<String> tests = bundleWiring.listResources(pkg.replace('.','/'), "*.test", BundleWiring.FINDENTRIES_RECURSE);
        TestInternalState.setTestResources(tests);
        
        // Help
        ListResourceBundle rb = (ListResourceBundle) HelpFinder.findHelp(context.getBundle(), BASE_PACKAGE);
        InternalState.addHelp(rb);
        
        // Save bundle and classloader for use by commands
        TestInternalState.setClassLoader(cl);
        
        // Now start the service
        TclCommandService scc = new TclCommandService(cl, commands);
        sccServiceRegistration = context.registerService(TclCommandProvider.class.getName(), scc, null);
    }
    
    /*
     * (non-Javadoc)
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        sccServiceRegistration.unregister();
    }


}
