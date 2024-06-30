# Dependencies
CLASSPATH=generated/classes

VERSION=1.0.0
MAIN_CLASS=com.veriktig.scandium.launcher.Main

FILELIST=src_list
OUTPUT=${CLASSPATH}
JAR=generated/ScLauncher-${VERSION}.jar

all: clean

list: 
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

clean: deps
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF
