# Dependencies
CLASSPATH=generated/classes

VERSION=1.0.0
MAIN_CLASS=com.veriktig.scandium.launcher.Main
SYMBOLIC_NAME=com.veriktig.scandium.launcher

FILELIST=src_list
OUTPUT=${CLASSPATH}
JAR=generated/ScLauncher-${VERSION}.jar

all: install

version:
	cd ../external/felix-dev/framework/src/main/java/org/apache/felix/framework/util; cat FelixConstants.template | sed -e "s/__NAME__/${SYMBOLIC_NAME}/g" > FelixConstants.name
	cd ../external/felix-dev/framework/src/main/java/org/apache/felix/framework/util; cat FelixConstants.name | sed -e "s/__VERSION__/${VERSION}/g" > FelixConstants.java
	cd ../external/felix-dev/framework/src/main/java/org/apache/felix/framework/util; rm FelixConstants.name

list: version
	find -L ./src/main/java -name \*.java -print > ${FILELIST}

compile: list
	${RM} -rf ${OUTPUT}/*
	javac -d ${OUTPUT} -cp ${CLASSPATH} @${FILELIST}
	${RM} ${FILELIST}

jar: compile
	jar --create --file ${JAR} -M -e ${MAIN_CLASS} -C src/main/resources .
	jar --update --file ${JAR} -C ${OUTPUT} .
	jar --update --file ${JAR} -C src/main/java .

deps: jar
	cat src/MANIFEST.template > src/main/resources/META-INF/MANIFEST.MF
	jar --update --file ${JAR} -m src/main/resources/META-INF/MANIFEST.MF
	cd ../external/felix-dev/framework/src/main/java/org/apache/felix/framework/util; rm FelixConstants.java

clean: deps
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF

install: clean
	cp ${JAR} ../demo
