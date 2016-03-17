JAVA_HOME="C:/Program Files/Java/jdk1.8.0_73/jre"

mvn clean
mvn dependency:copy-dependencies
mvn

cp target/skin-analysis-1.0.0-SNAPSHOT.jar ~/fiji/Fiji.app/jars
cp target/dependency/commons-csv-1.2.jar ~/fiji/Fiji.app/jars
cp target/dependency/imagej-ops-0.26.0.jar ~/fiji/Fiji.app/jars



#~/fiji/Fiji.app/ImageJ-linux64 --debugger=8000
~/fiji/Fiji.app/ImageJ-win64.exe --debugger=8000

