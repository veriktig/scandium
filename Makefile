all: submodule jaxb odftoolkit jdepz docs launcher api ui test jaxb-install
update: jaxb odftoolkit jdepz docs launcher api ui test jaxb-install
bundles: launcher api ui test

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

test:
	cd ScTest; make

jaxb-install:
	cd demo/bundle; unlink jaxb-osgi-4.0.6-SNAPSHOT.jar; ln -s ../../external/jaxb-ri/jaxb-ri/bundles/osgi/osgi/target/jaxb-osgi-4.0.6-SNAPSHOT.jar .

run:
	cd demo; rm -rf felix-cache; java --enable-native-access=ALL-UNNAMED -jar ScLauncher-1.0.0.jar

clobber:
	cd demo; rm -rf .scandium_history felix-cache *.jar; cd bundle; rm -rf *.jar;
	cd Documentation; make clobber
