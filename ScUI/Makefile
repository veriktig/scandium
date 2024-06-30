# Dependencies
CLASSPATH = ../ScLauncher/generated/ScLauncher-1.0.0.jar:../ScAPI/generated/ScAPI-1.2.0.jar

BUNDLE_VERSION=1.0.0

FILELIST=src_list
OUTPUT=generated/classes
JAR=generated/ScUI-${BUNDLE_VERSION}.jar

all: install

list: 
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
	cat src/MANIFEST.template generated/MANIFEST.osgi > src/main/resources/META-INF/MANIFEST.MF
	jar --update --file ${JAR} -m src/main/resources/META-INF/MANIFEST.MF

clean: deps
	${RM} -rf src/main/resources/META-INF/MANIFEST.MF

install: clean
	cp ${JAR} ../demo/bundle
