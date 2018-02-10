/**
 * Copyright 2018 Veriktig, Inc.
 *
 * Changes: Add command line option support. lint cleanup.
 *
 */

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.veriktig.scandium.launcher;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

/**
 * <p>
 * This class is the default way to instantiate and execute the framework. It is not
 * intended to be the only way to instantiate and execute the framework; rather, it is
 * one example of how to do so. When embedding the framework in a host application,
 * this class can serve as a simple guide of how to do so. It may even be
 * worthwhile to reuse some of its property handling capabilities.
 * </p>
**/
public class Main
{
    /**
     * Switch for specifying Tcl script file
    **/
    public final String FILE_SWITCH = "-f";
    public final String COMMAND_SWITCH = "-x";

    /**
     * Switch for specifying bundle directory.
    **/
    public final String BUNDLE_DIR_SWITCH = "-b";

    /**
     * The property name used to specify whether the launcher should
     * install a shutdown hook.
    **/
    public final String SHUTDOWN_HOOK_PROP = "felix.shutdown.hook";
    /**
     * The property name used to specify an URL to the system
     * property file.
    **/
    public final String SYSTEM_PROPERTIES_PROP = "felix.system.properties";
    /**
     * The default name used for the system properties file.
    **/
    public final String SYSTEM_PROPERTIES_FILE_VALUE = "system.properties";
    /**
     * The property name used to specify an URL to the configuration
     * property file to be used for the created the framework instance.
    **/
    public final String CONFIG_PROPERTIES_PROP = "felix.config.properties";
    /**
     * The default name used for the configuration properties file.
    **/
    public final String CONFIG_PROPERTIES_FILE_VALUE = "config.properties";
    /**
     * Name of the configuration directory.
     */
    public final String CONFIG_DIRECTORY = "conf";

    private Framework m_fwk = null;
    private Hashtable<String, Object> script = new Hashtable<String, Object>();
    private boolean expectScriptFile = false;
    private boolean expectCommands = false;
    private String scriptFile = null;
    private String commands = null;

    /**
     * <p>
     * This method performs the main task of constructing an framework instance
     * and starting its execution. The following functions are performed
     * when invoked:
     * </p>
     * <ol>
     *   <li><i><b>Examine and verify command-line arguments.</b></i> The launcher
     *       accepts a "<tt>-b</tt>" command line switch to set the bundle auto-deploy
     *       directory and a single argument to set the bundle cache directory.
     *   </li>
     *   <li><i><b>Read the system properties file.</b></i> This is a file
     *       containing properties to be pushed into <tt>System.setProperty()</tt>
     *       before starting the framework. This mechanism is mainly shorthand
     *       for people starting the framework from the command line to avoid having
     *       to specify a bunch of <tt>-D</tt> system property definitions.
     *       The only properties defined in this file that will impact the framework's
     *       behavior are the those concerning setting HTTP proxies, such as
     *       <tt>http.proxyHost</tt>, <tt>http.proxyPort</tt>, and
     *       <tt>http.proxyAuth</tt>. Generally speaking, the framework does
     *       not use system properties at all.
     *   </li>
     *   <li><i><b>Read the framework's configuration property file.</b></i> This is
     *       a file containing properties used to configure the framework
     *       instance and to pass configuration information into
     *       bundles installed into the framework instance. The configuration
     *       property file is called <tt>config.properties</tt> by default
     *       and is located in the <tt>conf/</tt> directory of the Felix
     *       installation directory, which is the parent directory of the
     *       directory containing the <tt>felix.jar</tt> file. It is possible
     *       to use a different location for the property file by specifying
     *       the desired URL using the <tt>felix.config.properties</tt>
     *       system property; this should be set using the <tt>-D</tt> syntax
     *       when executing the JVM. If the <tt>config.properties</tt> file
     *       cannot be found, then default values are used for all configuration
     *       properties. Refer to the
     *       <a href="Felix.html#Felix(java.util.Map)"><tt>Felix</tt></a>
     *       constructor documentation for more information on framework
     *       configuration properties.
     *   </li>
     *   <li><i><b>Copy configuration properties specified as system properties
     *       into the set of configuration properties.</b></i> Even though the
     *       Felix framework does not consult system properties for configuration
     *       information, sometimes it is convenient to specify them on the command
     *       line when launching Felix. To make this possible, the Felix launcher
     *       copies any configuration properties specified as system properties
     *       into the set of configuration properties passed into Felix.
     *   </li>
     *   <li><i><b>Add shutdown hook.</b></i> To make sure the framework shutdowns
     *       cleanly, the launcher installs a shutdown hook; this can be disabled
     *       with the <tt>felix.shutdown.hook</tt> configuration property.
     *   </li>
     *   <li><i><b>Create and initialize a framework instance.</b></i> The OSGi standard
     *       <tt>FrameworkFactory</tt> is retrieved from <tt>META-INF/services</tt>
     *       and used to create a framework instance with the configuration properties.
     *   </li>
     *   <li><i><b>Auto-deploy bundles.</b></i> All bundles in the auto-deploy
     *       directory are deployed into the framework instance.
     *   </li>
     *   <li><i><b>Start the framework.</b></i> The framework is started and
     *       the launcher thread waits for the framework to shutdown.
     *   </li>
     * </ol>
     * <p>
     * It should be noted that simply starting an instance of the framework is not
     * enough to create an interactive session with it. It is necessary to install
     * and start bundles that provide a some means to interact with the framework;
     * this is generally done by bundles in the auto-deploy directory or specifying
     * an "auto-start" property in the configuration property file. If no bundles
     * providing a means to interact with the framework are installed or if the
     * configuration property file cannot be found, the framework will appear to
     * be hung or deadlocked. This is not the case, it is executing correctly,
     * there is just no way to interact with it.
     * </p>
     * <p>
     * The launcher provides two ways to deploy bundles into a framework at
     * startup, which have associated configuration properties:
     * </p>
     * <ul>
     *   <li>Bundle auto-deploy - Automatically deploys all bundles from a
     *       specified directory, controlled by the following configuration
     *       properties:
     *     <ul>
     *       <li><tt>felix.auto.deploy.dir</tt> - Specifies the auto-deploy directory
     *           from which bundles are automatically deploy at framework startup.
     *           The default is the <tt>bundle/</tt> directory of the current directory.
     *       </li>
     *       <li><tt>felix.auto.deploy.action</tt> - Specifies the auto-deploy actions
     *           to be found on bundle JAR files found in the auto-deploy directory.
     *           The possible actions are <tt>install</tt>, <tt>update</tt>,
     *           <tt>start</tt>, and <tt>uninstall</tt>. If no actions are specified,
     *           then the auto-deploy directory is not processed. There is no default
     *           value for this property.
     *       </li>
     *     </ul>
     *   </li>
     *   <li>Bundle auto-properties - Configuration properties which specify URLs
     *       to bundles to install/start:
     *     <ul>
     *       <li><tt>felix.auto.install.N</tt> - Space-delimited list of bundle
     *           URLs to automatically install when the framework is started,
     *           where <tt>N</tt> is the start level into which the bundle will be
     *           installed (e.g., felix.auto.install.2).
     *       </li>
     *       <li><tt>felix.auto.start.N</tt> - Space-delimited list of bundle URLs
     *           to automatically install and start when the framework is started,
     *           where <tt>N</tt> is the start level into which the bundle will be
     *           installed (e.g., felix.auto.start.2).
     *       </li>
     *     </ul>
     *   </li>
     * </ul>
     * <p>
     * These properties should be specified in the <tt>config.properties</tt>
     * so that they can be processed by the launcher during the framework
     * startup process.
     * </p>
     * @param args Accepts arguments to set the auto-deploy directory and/or
     *        the bundle cache directory.
     * @throws Exception If an error occurs.
    **/
    public static void main(String[] args) throws Exception
    {
        Main target = new Main(args);
        target.run();
    }

    public Main(String[] args) {
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals(FILE_SWITCH)) {
                expectScriptFile = true;
            } else if (expectScriptFile) {
                scriptFile = args[i];
            }
            if (args[i].equals(COMMAND_SWITCH)) {
            	expectCommands = true;
            } else if (expectCommands) {
            	commands = args[i];
            }
        }
        if (((args.length == 2) && expectScriptFile && (scriptFile == null)) || (args.length > 2)) {
            System.out.println("Usage: [-f <script filename>]");
            System.exit(0);
        }
        if (((args.length == 2) && expectCommands && (commands == null)) || (args.length > 2)) {
            System.out.println("Usage: [-x <commands>]");
            System.exit(0);
        }
        // TODO Use scriptFile to instantiate a File, check path, existence
        if (args.length == 2 && expectScriptFile) {
            script.put("file", scriptFile);
        }
        if (args.length == 2 && expectCommands) {
        	script.put("command",  commands);
        }
    }

    public Hashtable<String, Object> getCommandLineArguments() {
        return script;
    }

    private void run() {
        // Load system properties.
        //Main.loadSystemProperties();
        loadSystemProperties();

        // Read configuration properties.
        //Map<String, String> configProps = Main.loadConfigProperties();
        Map<String, String> configProps = loadConfigProperties();
        // If no configuration properties were found, then create
        // an empty properties object.
        if (configProps == null)
        {
            System.err.println("No " + CONFIG_PROPERTIES_FILE_VALUE + " found.");
            configProps = new HashMap<String, String>();
        }

        // Copy framework properties from the system properties.
        //Main.copySystemProperties(configProps);
        copySystemProperties(configProps);

        // If there is a passed in bundle auto-deploy directory, then
        // that overwrites anything in the config file.
        //
        /*
        if (bundleDir != null)
        {
            configProps.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERTY, bundleDir);
        }

        // If there is a passed in bundle cache directory, then
        // that overwrites anything in the config file.
        if (cacheDir != null)
        {
            configProps.put(Constants.FRAMEWORK_STORAGE, cacheDir);
        }
        */

        // If enabled, register a shutdown hook to make sure the framework is
        // cleanly shutdown when the VM exits.
        /*
        String enableHook = configProps.get(SHUTDOWN_HOOK_PROP);
        if ((enableHook == null) || !enableHook.equalsIgnoreCase("false"))
        {
            Runtime.getRuntime().addShutdownHook(new Thread("Felix Shutdown Hook") {
                public void run()
                {
                    try
                    {
                        if (m_fwk != null)
                        {
                            m_fwk.stop();
                            m_fwk.waitForStop(0);
                        }
                    }
                    catch (Exception ex)
                    {
                        System.err.println("Error stopping framework: " + ex);
                    }
                }
            });
        }
        */

        try
        {
            // Create an instance of the framework.
            FrameworkFactory factory = getFrameworkFactory();
            m_fwk = factory.newFramework(configProps);
            // Initialize the framework, but don't start it yet.
            m_fwk.init();
            m_fwk.getBundleContext().registerService(Main.class.getName(), this, null);
/*
            ServiceReference<?> ref = m_fwk.getBundleContext().getServiceReference(Main.class.getName());
            Main temp = (Main) m_fwk.getBundleContext().getService(ref);
            Hashtable<String, Object> ht = temp.getCommandLineArguments();
*/            
            // Use the system bundle context to process the auto-deploy
            // and auto-install/auto-start properties.
            AutoProcessor.process(configProps, m_fwk.getBundleContext());
            FrameworkEvent event;
            do
            {
                // Start the framework.
                m_fwk.start();
                // Wait for framework to stop to exit the VM.
                event = m_fwk.waitForStop(0);
            }
            // If the framework was updated, then restart it.
            while (event.getType() == FrameworkEvent.STOPPED_UPDATE);
            // Otherwise, exit.
            System.exit(0);
        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * Simple method to parse META-INF/services file for framework factory.
     * Currently, it assumes the first non-commented line is the class name
     * of the framework factory implementation.
     * @return The created <tt>FrameworkFactory</tt> instance.
     * @throws Exception if any errors occur.
    **/
    private FrameworkFactory getFrameworkFactory() throws Exception
    {
        URL url = Main.class.getClassLoader().getResource(
            "META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        if (url != null)
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
            try
            {
                for (String s = br.readLine(); s != null; s = br.readLine())
                {
                    s = s.trim();
                    // Try to load first non-empty, non-commented line.
                    if ((s.length() > 0) && (s.charAt(0) != '#'))
                    {
                        return (FrameworkFactory) Class.forName(s).newInstance();
                    }
                }
            }
            finally
            {
                if (br != null) br.close();
            }
        }

        throw new Exception("Could not find framework factory.");
    }

    /**
     * <p>
     * Loads the properties in the system property file associated with the
     * framework installation into <tt>System.setProperty()</tt>. These properties
     * are not directly used by the framework in anyway. By default, the system
     * property file is located in the <tt>conf/</tt> directory of the Felix
     * installation directory and is called "<tt>system.properties</tt>". The
     * installation directory of Felix is assumed to be the parent directory of
     * the <tt>felix.jar</tt> file as found on the system class path property.
     * The precise file from which to load system properties can be set by
     * initializing the "<tt>felix.system.properties</tt>" system property to an
     * arbitrary URL.
     * </p>
    **/
    public void loadSystemProperties()
    {
        // The system properties file is either specified by a system
        // property or it is in the same directory as the Felix JAR file.
        // Try to load it from one of these places.

        // See if the property URL was specified as a property.
        URL propURL = null;
        String custom = System.getProperty(SYSTEM_PROPERTIES_PROP);
        if (custom != null)
        {
            try
            {
                propURL = new URL(custom);
            }
            catch (MalformedURLException ex)
            {
                System.err.print("Main: " + ex);
                return;
            }
        }
        else
        {
            // Determine where the configuration directory is by figuring
            // out where felix.jar is located on the system class path.
            File confDir = null;
            String classpath = System.getProperty("java.class.path");
            int index = classpath.toLowerCase().indexOf("felix.jar");
            int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;
            if (index >= start)
            {
                // Get the path of the felix.jar file.
                String jarLocation = classpath.substring(start, index);
                // Calculate the conf directory based on the parent
                // directory of the felix.jar directory.
                confDir = new File(
                    new File(new File(jarLocation).getAbsolutePath()).getParent(),
                    CONFIG_DIRECTORY);
            }
            else
            {
                // Can't figure it out so use the current directory as default.
                confDir = new File(System.getProperty("user.dir"), CONFIG_DIRECTORY);
            }

            try
            {
                propURL = new File(confDir, SYSTEM_PROPERTIES_FILE_VALUE).toURI().toURL();
            }
            catch (MalformedURLException ex)
            {
                System.err.print("Main: " + ex);
                return;
            }
        }

        // Read the properties file.
        Properties props = new Properties();
        InputStream is = null;
        try
        {
            is = propURL.openConnection().getInputStream();
            props.load(is);
            is.close();
        }
        catch (FileNotFoundException ex)
        {
            // Ignore file not found.
        }
        catch (Exception ex)
        {
            System.err.println(
                "Main: Error loading system properties from " + propURL);
            System.err.println("Main: " + ex);
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ex2)
            {
                // Nothing we can do.
            }
            return;
        }

        // Perform variable substitution on specified properties.
        for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements(); )
        {
            String name = (String) e.nextElement();
            System.setProperty(name,
                substVars(props.getProperty(name), name, null, null));
        }
    }

    /**
     * <p>
     * Loads the configuration properties in the configuration property file
     * associated with the framework installation; these properties
     * are accessible to the framework and to bundles and are intended
     * for configuration purposes. By default, the configuration property
     * file is located in the <tt>conf/</tt> directory of the Felix
     * installation directory and is called "<tt>config.properties</tt>".
     * The installation directory of Felix is assumed to be the parent
     * directory of the <tt>felix.jar</tt> file as found on the system class
     * path property. The precise file from which to load configuration
     * properties can be set by initializing the "<tt>felix.config.properties</tt>"
     * system property to an arbitrary URL.
     * </p>
     * @return A <tt>Properties</tt> instance or <tt>null</tt> if there was an error.
    **/
    public Map<String, String> loadConfigProperties()
    {
        // The config properties file is either specified by a system
        // property or it is in the conf/ directory of the Felix
        // installation directory.  Try to load it from one of these
        // places.

        // See if the property URL was specified as a property.
        URL propURL = null;
        String custom = System.getProperty(CONFIG_PROPERTIES_PROP);
        if (custom != null)
        {
            try
            {
                propURL = new URL(custom);
            }
            catch (MalformedURLException ex)
            {
                System.err.print("Main: " + ex);
                return null;
            }
        }
        else
        {
            // Determine where the configuration directory is by figuring
            // out where felix.jar is located on the system class path.
            File confDir = null;
            String classpath = System.getProperty("java.class.path");
            int index = classpath.toLowerCase().indexOf("ScLauncher.jar");
            int start = classpath.lastIndexOf(File.pathSeparator, index) + 1;
            if (index >= start)
            {
                // Get the path of the felix.jar file.
                String jarLocation = classpath.substring(start, index);
                // Calculate the conf directory based on the parent
                // directory of the felix.jar directory.
                confDir = new File(
                    new File(new File(jarLocation).getAbsolutePath()).getParent(),
                    CONFIG_DIRECTORY);
            }
            else
            {
                // Can't figure it out so use the current directory as default.
                confDir = new File(System.getProperty("user.dir"), CONFIG_DIRECTORY);
            }

            try
            {
                propURL = new File(confDir, CONFIG_PROPERTIES_FILE_VALUE).toURI().toURL();
            }
            catch (MalformedURLException ex)
            {
                System.err.print("Main: " + ex);
                return null;
            }
        }

        // Read the properties file.
        Properties props = new Properties();
        InputStream is = null;
        try
        {
            // Try to load config.properties.
            is = propURL.openConnection().getInputStream();
            props.load(is);
            is.close();
        }
        catch (Exception ex)
        {
            // Try to close input stream if we have one.
            try
            {
                if (is != null) is.close();
            }
            catch (IOException ex2)
            {
                // Nothing we can do.
            }

            return null;
        }

        // Perform variable substitution for system properties and
        // convert to dictionary.
        Map<String, String> map = new HashMap<String, String>();
        for (Enumeration<?> e = props.propertyNames(); e.hasMoreElements(); )
        {
            String name = (String) e.nextElement();
            map.put(name,
                substVars(props.getProperty(name), name, null, props));
        }

        return map;
    }

    public void copySystemProperties(Map<String, String> configProps)
    {
        for (Enumeration<?> e = System.getProperties().propertyNames();
             e.hasMoreElements(); )
        {
            String key = (String) e.nextElement();
            if (key.startsWith("felix.") || key.startsWith("org.osgi.framework."))
            {
                configProps.put(key, System.getProperty(key));
            }
        }
    }

    private static final String DELIM_START = "${";
    private static final String DELIM_STOP  = "}";

    /**
     * <p>
     * This method performs property variable substitution on the
     * specified value. If the specified value contains the syntax
     * <tt>${&lt;prop-name&gt;}</tt>, where <tt>&lt;prop-name&gt;</tt>
     * refers to either a configuration property or a system property,
     * then the corresponding property value is substituted for the variable
     * placeholder. Multiple variable placeholders may exist in the
     * specified value as well as nested variable placeholders, which
     * are substituted from inner most to outer most. Configuration
     * properties override system properties.
     * </p>
     * @param val The string on which to perform property substitution.
     * @param currentKey The key of the property being evaluated used to
     *        detect cycles.
     * @param cycleMap Map of variable references used to detect nested cycles.
     * @param configProps Set of configuration properties.
     * @return The value of the specified string after system property substitution.
     * @throws IllegalArgumentException If there was a syntax error in the
     *         property placeholder syntax or a recursive variable reference.
    **/
    public static String substVars(String val, String currentKey,
        Map<String, String> cycleMap, Properties configProps)
        throws IllegalArgumentException
    {
        // If there is currently no cycle map, then create
        // one for detecting cycles for this invocation.
        if (cycleMap == null)
        {
            cycleMap = new HashMap<String, String>();
        }

        // Put the current key in the cycle map.
        cycleMap.put(currentKey, currentKey);

        // Assume we have a value that is something like:
        // "leading ${foo.${bar}} middle ${baz} trailing"

        // Find the first ending '}' variable delimiter, which
        // will correspond to the first deepest nested variable
        // placeholder.
        int stopDelim = -1;
        int startDelim = -1;

        do
        {
            stopDelim = val.indexOf(DELIM_STOP, stopDelim + 1);
            // If there is no stopping delimiter, then just return
            // the value since there is no variable declared.
            if (stopDelim < 0)
            {
                return val;
            }
            // Try to find the matching start delimiter by
            // looping until we find a start delimiter that is
            // greater than the stop delimiter we have found.
            startDelim = val.indexOf(DELIM_START);
            // If there is no starting delimiter, then just return
            // the value since there is no variable declared.
            if (startDelim < 0)
            {
                return val;
            }
            while (stopDelim >= 0)
            {
                int idx = val.indexOf(DELIM_START, startDelim + DELIM_START.length());
                if ((idx < 0) || (idx > stopDelim))
                {
                    break;
                }
                else if (idx < stopDelim)
                {
                    startDelim = idx;
                }
            }
        }
        while ((startDelim > stopDelim) && (stopDelim >= 0));

        // At this point, we have found a variable placeholder so
        // we must perform a variable substitution on it.
        // Using the start and stop delimiter indices, extract
        // the first, deepest nested variable placeholder.
        String variable =
            val.substring(startDelim + DELIM_START.length(), stopDelim);

        // Verify that this is not a recursive variable reference.
        if (cycleMap.get(variable) != null)
        {
            throw new IllegalArgumentException(
                "recursive variable reference: " + variable);
        }

        // Get the value of the deepest nested variable placeholder.
        // Try to configuration properties first.
        String substValue = (configProps != null)
            ? configProps.getProperty(variable, null)
            : null;
        if (substValue == null)
        {
            // Ignore unknown property values.
            substValue = System.getProperty(variable, "");
        }

        // Remove the found variable from the cycle map, since
        // it may appear more than once in the value and we don't
        // want such situations to appear as a recursive reference.
        cycleMap.remove(variable);

        // Append the leading characters, the substituted value of
        // the variable, and the trailing characters to get the new
        // value.
        val = val.substring(0, startDelim)
            + substValue
            + val.substring(stopDelim + DELIM_STOP.length(), val.length());

        // Now perform substitution again, since there could still
        // be substitutions to make.
        val = substVars(val, currentKey, cycleMap, configProps);

        // Return the value.
        return val;
    }
}
