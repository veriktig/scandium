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

include Makefile.j
include Makefile.files

SRC = src
BASE_PACKAGE = com.veriktig.documentation
BASE_SRC = $(SRC)/com/veriktig/documentation

MAIN = $(BASE_PACKAGE).Main
CLASSES = classes
PROGRAM = $(CLASSES)/$(MAIN).class
OUTPUT = output
PACKAGE = $(BASE_PACKAGE).generated

COMMON_SRCS = $(COMMON_SRC:%=$(BASE_SRC)/%)
ERRORS_SRCS = $(ERRORS_SRC:%=$(BASE_SRC)/errors/%)
HELP_SRCS = $(HELP_SRC:%=$(BASE_SRC)/help/%)
VARIABLES_SRCS = $(VARIABLES_SRC:%=$(BASE_SRC)/variables/%)
SRCS = $(COMMON_SRCS) $(HELP_SRCS) $(ERRORS_SRCS) $(VARIABLES_SRCS)

GENERATED = \
	$(BASE_SRC)/errors/generated/*.java \
	$(BASE_SRC)/help/generated/*.java \
	$(BASE_SRC)/variables/generated/*.java

.PHONY: all
all:
	$(MAKE) help
	$(MAKE) errors
	$(MAKE) variables
	$(MAKE) docs

help: SRC_PACKAGE=$(BASE_PACKAGE).help
help: SRC_PATH=$(BASE_SRC)/help
help: GEN_PACKAGE = $(SRC_PACKAGE).generated
help: GEN_PATH = $(SRC_PATH)/generated
help: SCHEMA="schema/Help.xsd"
help: $(GENSRCS)

errors: SRC_PACKAGE=$(BASE_PACKAGE).errors
errors: SRC_PATH=$(BASE_SRC)/errors
errors: GEN_PACKAGE = $(SRC_PACKAGE).generated
errors: GEN_PATH = $(SRC_PATH)/generated
errors: SCHEMA="schema/Errors.xsd"
errors: $(GENSRCS)

variables: SRC_PACKAGE=$(BASE_PACKAGE).variables
variables: SRC_PATH=$(BASE_SRC)/variables
variables: GEN_PACKAGE = $(SRC_PACKAGE).generated
variables: GEN_PATH = $(SRC_PATH)/generated
variables: SCHEMA="schema/Variables.xsd"
variables: $(GENSRCS)

$(GENSRCS): $(SCHEMA)
	$(XJC) $(SCHEMA) -p $(GEN_PACKAGE) -d $(SRC)

$(CLASSES):
	mkdir -p $@

$(PROGRAM): $(SRCS) $(CLASSES)
	$(JAVAC) $(LINT) -d $(CLASSES) -cp $(XJC_CLASSPATH):$(ODF) $(SRCS) $(GENERATED)

docs: $(PROGRAM)
	$(JAVA) -cp $(XJC_CLASSPATH):$(ODF):$(CLASSES) $(MAIN) $(OUTPUT)

CLEANFILES = \
	$(GENERATED)	\
	$(CLASSES)/*

CLOBBERFILES = \
	$(OUTPUT)/*

clean::
	/bin/rm -rf $(CLEANFILES)

clobber:: clean
	/bin/rm -rf $(CLOBBERFILES)
