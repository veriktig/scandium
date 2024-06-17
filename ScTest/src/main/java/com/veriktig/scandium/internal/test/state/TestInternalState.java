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

package com.veriktig.scandium.internal.test.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.osgi.framework.Bundle;

/**
 * Create a singleton with an enum.
 */
public enum TestInternalState {
    INSTANCE;
    
    // These are internal.
    private static Bundle bundle;
    private static ClassLoader classLoader;
    private static Collection<String> testClasses = new HashSet<String>();
    private static Collection<String> testResources = new HashSet<String>();
    
    public static void setBundle(Bundle bnd) {
        bundle = bnd;
    }
    public static Bundle getBundle() {
        return bundle;
    }
    public static void setClassLoader(ClassLoader ldr) {
        classLoader = ldr;
    }
    public static ClassLoader getClassLoader() {
        return classLoader;
    }
    public static void setTestClasses(Collection<String> classes) {
        testClasses.addAll(classes);
    }
    public static void setTestResources(Collection<String> resources) {
        testResources.addAll(resources);
    }
    public static Collection<String> getTestClasses() {
        List<String> list = new ArrayList<String>(testClasses);
        Collections.sort(list);
        return (list);
    }
    public static Collection<String> getTestResources() {
        return (testResources);
    }
}
