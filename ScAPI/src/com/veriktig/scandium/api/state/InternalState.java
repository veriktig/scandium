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

package com.veriktig.scandium.api.state;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ResourceBundle;

import com.veriktig.scandium.api.errors.ErrorMessage;

import java.util.ListResourceBundle;
import java.util.Map;

import tcl.lang.Interp;

/**
 * @author fred
 *
 */
public enum InternalState {
    INSTANCE;
        
    private static Interp interp;
    private static Collection<ListResourceBundle> help_bundle = new HashSet<ListResourceBundle>();
    private static Collection<ListResourceBundle> error_bundle = new HashSet<ListResourceBundle>();
    private static Collection<String> suppressed_messages = new HashSet<String>();
    private static Map<String, String> user_variables = new HashMap<String, String>();
    private static Map<String, String> app_variables = new HashMap<String, String>();
    private static Map<String, String> bundle_versions = new HashMap<String, String>();
    
    public static void setInterp(Interp interp) {
        InternalState.interp = interp;
    }
    
    public static Interp getInterp() {
        return interp;
    }
    
    public static void addHelp(ListResourceBundle rb) {
        help_bundle.add(rb);
    }
    
    public static void addErrors(ListResourceBundle erb) {
        error_bundle.add(erb);
    }
    
    public static void addSuppressedMessages(Collection<String> messages) {
        suppressed_messages.addAll(messages);
    }
    
    public static void addUserVariable(String key, String value) {
        user_variables.put(key, value);
    }
    
    public static void addAppVariable(String key, String value) {
        app_variables.put(key, value);
    }
    
    public static String getUserVariable(String key) {
        return user_variables.get(key);
    }
    
    public static String getAppVariable(String key) {
        return app_variables.get(key);
    }
    
    public static Map<String, String> getUserVariables() {
        return user_variables;
    }
    
    public static Map<String, String> getAppVariables() {
        return app_variables;
    }
    
    public static boolean isAppVariable(String key) {
        if (app_variables.containsKey(key)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static void replaceAppVariable(String key, String value) {
        app_variables.replace(key, value);
    }
    
    public static void clearUserVariables() {
        user_variables.clear();
    }
    
    public static void removeUserVariable(String key) {
        user_variables.remove(key);
    }
    
    public static String getHelp(String command) {
        for (ResourceBundle help : help_bundle) {
            if (help == null) {
                return null;
            }
            if (help.containsKey(command)) {
                return help.getString(command);
            }
        }
        return null;
    }
    
    public static ErrorMessage getError(String error_code) {
        for (ResourceBundle error : error_bundle) {
            if (error == null) {
                return null;
            }
            if (error.containsKey(error_code)) {
                return (ErrorMessage) error.getObject(error_code);
            }
        }
        return null;    
    }
    public static void removeSuppressedMessages(Collection<String> messages) {
        suppressed_messages.removeAll(messages);
    }
    
    public static Collection<String> getSuppressedMessages() {
        return suppressed_messages;
    }
    
    public static void setBundleVersions(Map<String, String> versions) {
        bundle_versions = versions;
    }
    
    public static Map<String, String> getBundleVersions() {
        return bundle_versions;
    }
}
