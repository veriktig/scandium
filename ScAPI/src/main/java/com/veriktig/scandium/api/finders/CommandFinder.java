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

package com.veriktig.scandium.api.finders;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

import com.veriktig.scandium.api.TclCommand;
import com.veriktig.scandium.api.annotations.TclCommandName;

abstract public class CommandFinder {

    public static Collection<TclCommand> findCommands(Bundle bundle, String base_name) {
        Collection<TclCommand> commands = new HashSet<TclCommand>();
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        String pkg = new String(base_name + ".cmd");

        Collection<String> classes = bundleWiring.listResources(pkg.replace('.','/'), "*.class", BundleWiring.FINDENTRIES_RECURSE);
        for (String klass : classes) {
            try {
                String klsName = klass.replaceAll(".class", "");
                String clsName = klsName.replace('/', '.');
                Class<?> commandClass = bundle.loadClass(clsName);
                if (commandClass.isAnnotationPresent(TclCommandName.class)) {
                    Annotation annotation = commandClass.getAnnotation(TclCommandName.class);
                    TclCommandName name = (TclCommandName) annotation;
                    TclCommand command = new TclCommand(name.value(), clsName);
                    boolean success = commands.add(command);
                    if (!success) System.err.println("Unable to add command (" + name.value() + ")");
                }
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return commands;
    }
    
}
