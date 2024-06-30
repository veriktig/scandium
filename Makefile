all: submodule jaxb odftoolkit jdepz docs launcher api ui

submodule:
	git submodule init
	git submodule update

jaxb:
	cd external/jaxb-ri/jaxb-ri; mvn clean install

odftoolkit:
	cd external/odftoolkit; mvn clean install

jdepz:
	cd external/jdepz; make

docs:
	cd Documentation; make; make clean

launcher:
	cd ScLauncher; make

api:
	cd ScAPI; make

ui:
	cd ScUI; make
