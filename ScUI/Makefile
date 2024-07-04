# Dependencies
CLASSPATH = ../ScLauncher/generated/ScLauncher-1.0.0.jar:../ScAPI/generated/ScAPI-1.2.0.jar

BUNDLE_VERSION=1.0.0
SYMBOLIC_NAME=com.veriktig.scandium.ui

FILELIST=src_list
OUTPUT=generated/classes
JAR=generated/ScUI-${BUNDLE_VERSION}.jar

all: install

version:
	cd src; cat MANIFEST.template | sed -e "s/__NAME__/${SYMBOLIC_NAME}/" > MANIFEST.name
	cd src; cat MANIFEST.name | sed -e "s/__VERSION__/${BUNDLE_VERSION}/" > MANIFEST.fragment
	cd src; rm MANIFEST.name

list: version
	find -L ./src/main/java -name \*.java -print > ${FILELIST}

compile: list
	${RM} -rf ${OUTPUT}/*
	javac -d ${OUTPUT} -cp ${CLASSPATH} @${FILELIST}
	${RM} ${FILELIST}

jar: compile
	jar --create --file ${JAR} -C ${OUTPUT} .

deps: jar
	${RM} -rf generated/MANIFEST.osgi
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF
	java -jar ../external/jdepz/jdepz.jar --osgi ${BUNDLE_VERSION} ${JAR} > generated/MANIFEST.osgi
	cat src/MANIFEST.fragment generated/MANIFEST.osgi > src/main/resources/META-INF/MANIFEST.MF
	jar --update --file ${JAR} -m src/main/resources/META-INF/MANIFEST.MF

clean: deps
	cd src; ${RM} -rf com MANIFEST.fragment
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF

install: clean
	cp ${JAR} ../demo/bundle
