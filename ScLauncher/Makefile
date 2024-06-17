# Dependencies
CLASSPATH=generated/classes

VERSION=1.0.0
MAIN_CLASS=com.veriktig.scandium.launcher.Main

FILELIST=src_list
OUTPUT=${CLASSPATH}
JAR=generated/ScLauncher-${VERSION}.jar

all: deps

list: 
	find -L ./src/main/java -name \*.java -print > ${FILELIST}

compile: list
	${RM} -rf ${OUTPUT}/*
	javac -d ${OUTPUT} -cp ${CLASSPATH} @${FILELIST}
	${RM} ${FILELIST}

jar: compile
	jar --create --file ${JAR} -e ${MAIN_CLASS} -C src/main/resources .
	jar --update --file ${JAR} -C ${OUTPUT} .
	jar --update --file ${JAR} -C src/main/java .

deps: jar
	jdeps --ignore-missing-deps --generate-module-info . ${JAR}
