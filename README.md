![Alt text](./assets/ScLogo128.png)
# Project Scandium - Tcl meets microservices
A general purpose framework for Java CLI applications based on Tcl and [OSGi](https://www.osgi.org).

## Overview
The framework consists of 2 bundles and an executable jar file:
* ScLauncher - executable
* ScAPI, ScUI - bundles

ScLauncher starts the OSGi framework and passes command line arguments to ScUI.

ScAPI provides core services such as the Tcl interpreter.

ScUI finds TclCommandProvider's, extends the Tcl interpreter with new commands, services command line arguments, and starts the Tcl shell.

## Demo
For a quick demostration, clone the project, or grab the files under the demo directory.
You will need the Java8 runtime installed.

demo initially contains the following:

```c
ScLauncher-1.0.0.jar
conf
    config.properties
bundle
    ScAPI-1.2.0.jar
    ScUI-1.0.0.jar
    ScTest-1.0.0.jar
```

You can start the application using:
```c
java -jar ScLauncher-1.0.0.jar
```

You should see the prompt of the Tcl shell:
```c
sc_shell>
```

In addition to standard Tcl commands, ScAPI provides 3 commands:
* help
* printvar
* print_versions

ScTest demonstrates adding a new feature to the framework.
ScTest wraps the jtcl internal tests in a bundle and provides them with a new command called internal_test.

You can also try removing ScTest from the framework:

1. Move ScTest-1.0.0.jar to a different directory.
2. Remove the Felix cache: rm -rf felix-cache
3. java -jar ScLauncher-1.0.0.jar

Note the internal_test command is no longer available in the Tcl shell.

To return the functionality,

1. Move ScTest-1.0.0.jar back to the bundle directory
2. Remove the Felix cache: rm -rf felix-cache
3. java -jar ScLauncher-1.0.0.jar

Now the internal_test command is available again.

## Documentation First
The Documentation directory contains code to automate generating aspects of the application:

1. Help files ready for internationalization.
2. cmd classes.
3. Error messages ready for internationalization.
4. Internal variables declared by bundles.
5. Written documentation for help, errors and variables.

The definitions are written to a XML schema and provide the means to always keep the source code and written documentation in sync. The code also checks the definitions in the XML to the code, and vice versa.

Documentation uses make as its build tool.

## Building

### Tools Required
You will need the following to build:
* Java8 
* Eclipse for RCP and RAP Developers 
* Bndtools from Eclipse Marketplace
* make
* ant
* Maven

By default, Eclipse builds the bundles. You use Bndtools to release the bundles. You also may need to run ant from ScLauncher to create the executable jar under generated.

### Setup

1. Build the ODF jar.
```c
cd external
mvn package
```
2. Start Eclipse and open the Bntools perspective.
3. Create a new Bnd OSGi Workspace.
4. Open Projects from File System (ScLauncher, ScAPI, ScUI, ScTest).
5. Eclipse automatically builds the projects.
6. Use Bndtools to release bundles.
7. Use ant to generate the ScLauncher executable jar.
```c
cd ScLauncher
ant
```

### Creating a New Bundle

1. Create a new Bnd project.
2. Create a cmd subdirectory if the bundle provides Tcl commands.
3. Create XML files in the Documentation/definitions directories for commands/help, errors and variables.
4. Use make to create the cmd class templates, etc.
5. Use Eclipse to add functionality.
6. Use Bndtools to release the bundle.
7. Link the released bundle under cnf/release to the demo/bundle directory.
8. Test and have fun.

## Other Projects
Project Scandium stands on the shoulders of giants to provide its functionality.
### ScLauncher and ScAPI:
* [ANTLR](http://www.antlr.org) ANother Tool for Language Recognition. BSD-3-clause license
* [Apache Felix](http://felix.apache.org) OSGi Release 6 layer. Apache-2.0 license
* [JTcl Project](http://jtcl-project.github.io/jtcl/) Tcl. Sun Labs license
* [Apache Commons CLI](https://commons.apache.org/proper/commons-cli/) CLI support. Apache-2.0 license
* [JLine](https://github.com/jline/jline3.git) Command line editing. BSD-2-clause license
* [Java Native Access](https://github.com/java-native-access/jna.git) Native support for JLine. Apache-2.0 license

### Documentation:
* [Apache ODF Toolkit](http://incubator.apache.org/odftoolkit/index.html) ODF file output. Apache-2.0 license

### ScTest:
* [JUnit4](https://junit.org/junit4/) Unit test framework. EPL v1.0 license
* [Hamcrest](http://hamcrest.org/) JUnit support. BSD-3-clause license

### Branches
Any file modifications are stored under the external directory.

### Change Log
* 1.2.0 Changed ClassResolver for Java9
* 1.1.0 Added ANTLR v4 to ScAPI
* 1.0.0 Initial Release
