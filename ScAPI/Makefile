# Dependencies
CLASSPATH=../ScLauncher/generated/ScLauncher-1.0.0.jar

BUNDLE_NAME=ScAPI
BUNDLE_VERSION=1.2.0

FILELIST=src_list
OUTPUT=generated/classes
JAR=generated/ScAPI-${BUNDLE_VERSION}.jar

all: clean

list: 
	find -L ./src/main/java -name \*.java -print > ${FILELIST}

compile: list
	${RM} -rf ${OUTPUT}/*
	javac -d ${OUTPUT} -cp ${CLASSPATH} @${FILELIST}
	${RM} ${FILELIST}

jar: compile
	jar --create --file ${JAR} -C ${OUTPUT} .
	jar --update --file ${JAR} -C src/main/resources .

deps: jar
	${RM} -rf generated/MANIFEST.osgi
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF
	java -jar ../external/jdepz/jdepz.jar --osgi ${BUNDLE_VERSION} ${JAR} > generated/MANIFEST.osgi
	cat src/MANIFEST.template generated/MANIFEST.osgi > src/main/resources/META-INF/MANIFEST.MF
	jar --update --file ${JAR} -m src/main/resources/META-INF/MANIFEST.MF

clean: deps
	cd src; ${RM} -rf com
