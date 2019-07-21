#
#  Copyright 2018 Veriktig, Inc.
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

GENSRC = *.java
GENERATED = generated
GENSRCS = $(GENSRC:%=$(BASE_PATH)/$(GENERATED)/%)

JAVA_HOME = /usr

JAVA = $(JAVA_HOME)/bin/java
JAVAC = $(JAVA_HOME)/bin/javac
JAVADOC = $(JAVA_HOME)/bin/javadoc

XJC_HOME = ../external/jaxb-ri/jaxb-ri/xjc/target
MODS_HOME = $(XJC_HOME)/mods
MODS = $(XJC_HOME)/jaxb-xjc-2.3.2-SNAPSHOT.jar:$(MODS_HOME)/ant-1.10.2.jar:$(MODS_HOME)/ant-launcher-1.10.2.jar:$(MODS_HOME)/FastInfoset-1.2.16.jar:$(MODS_HOME)/jakarta.activation-api-1.2.1.jar:$(MODS_HOME)/relaxng-datatype-2.3.2.jar:$(MODS_HOME)/txw2-2.3.2-SNAPSHOT.jar:$(MODS_HOME)/dtd-parser-1.4.1.jar:$(MODS_HOME)/istack-commons-tools-3.0.8.jar:$(MODS_HOME)/junit-4.12.jar:$(MODS_HOME)/stax-ex-1.8.1.jar:$(MODS_HOME)/args4j-1.0.jar:$(MODS_HOME)/hamcrest-core-1.3.jar:$(MODS_HOME)/jakarta.xml.bind-api-2.3.2.jar:$(MODS_HOME)/resolver-20050927.jar:$(MODS_HOME)/xsom-2.3.2.jar:$(MODS_HOME)/codemodel-2.3.2.jar:$(MODS_HOME)/istack-commons-runtime-3.0.8.jar:$(MODS_HOME)/jaxb-runtime-2.3.2-SNAPSHOT.jar:$(MODS_HOME)/rngom-2.3.2.jar

XJC = java --module-path "${MODS}" -m com.sun.tools.xjc/com.sun.tools.xjc.XJCFacade
LINT = -Xlint:deprecation -Xlint:unchecked

ODFDOM = ../external/odftoolkit/odfdom/target/odfdom-java-0.9.0-incubating-SNAPSHOT-jar-with-dependencies.jar
SIMPLE_ODF = ../external/odftoolkit/simple/target/simple-odf-0.9.0-incubating-SNAPSHOT-jar-with-dependencies.jar
ODF_PATH = "$(ODFDOM):$(SIMPLE_ODF)"

PROGRAM = $(CLASSES)/$(MAIN).class
