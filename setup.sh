#!/bin/bash
echo "clearing current build..."
rm -rf build target
echo "compiling project"
mvn compile
mkdir build
cp target/lib/* build
cp -R target/classes/com build
CLASSPATH=$(find ~+/build -name '*.jar' | tr '\n' ':')
echo "java -classpath '$(pwd)/build:${CLASSPATH:0:-1}' com.hawolt.interactive.SoundcloudCLI \"\$@\"" > scdl.sh
chmod +x scdl.sh
