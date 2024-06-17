# Dependencies
CLASSPATH=../ScLauncher/generated/ScLauncher.jar

VERSION=1.2.0

FILELIST=src_list
OUTPUT=generated/classes
JAR=generated/ScAPI-${VERSION}.jar

all: deps

list: 
	find -L ./src/main/java -name \*.java -print > ${FILELIST}

compile: list
	${RM} -rf ${OUTPUT}/*
	javac -d ${OUTPUT} -cp ${CLASSPATH} @${FILELIST}
	${RM} ${FILELIST}

jar: compile
	jar --create --file ${JAR} -C ${OUTPUT} .

deps: jar
	jdeps --ignore-missing-deps --generate-module-info . ${JAR}
