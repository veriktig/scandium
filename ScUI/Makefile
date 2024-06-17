# Dependencies
CLASSPATH = ../ScLauncher/generated/ScLauncher.jar:../ScAPI/generated/ScAPI-1.2.0.jar

VERSION=1.0.0

FILELIST=src_list
OUTPUT=generated/classes
JAR=generated/ScUI-${VERSION}.jar

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
