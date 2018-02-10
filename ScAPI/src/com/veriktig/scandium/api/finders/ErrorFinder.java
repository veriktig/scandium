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

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;

import org.osgi.framework.Bundle;
import org.osgi.framework.wiring.BundleWiring;

abstract public class ErrorFinder {
	protected final static String ERROR_FILE = "Errors";

    public static ResourceBundle findErrors(Bundle bundle, String base_name) {
    	Locale currentLocale = Locale.getDefault();
		BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
		ClassLoader cl = bundleWiring.getClassLoader();
		String bundleName = new String(base_name + ".errors." + ERROR_FILE);
		ResourceBundle.Control rbc = ResourceBundle.Control.getControl(Control.FORMAT_CLASS);
        ResourceBundle rb = ResourceBundle.getBundle(bundleName, currentLocale, cl, rbc);
        return rb;
	}
	
}
