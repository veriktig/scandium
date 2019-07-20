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
XJC = java -jar ../external/jaxb-ri/jaxb-ri/bundles/xjc/target/jaxb-xjc-2.3.2-SNAPSHOT.jar
LINT = -Xlint:deprecation -Xlint:unchecked

ODFDOM = ../external/odftoolkit/odfdom/target/odfdom-java-0.9.0-incubating-SNAPSHOT-jar-with-dependencies.jar
SIMPLE_ODF = ../external/odftoolkit/simple/target/simple-odf-0.9.0-incubating-SNAPSHOT-jar-with-dependencies.jar
ODF = $(ODFDOM):$(SIMPLE_ODF)

PROGRAM = $(CLASSES)/$(MAIN).class
