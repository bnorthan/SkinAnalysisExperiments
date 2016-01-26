mvn clean
mvn dependency:copy-dependencies
mvn

cp target/skin-analysis-1.0.0-SNAPSHOT.jar ~/fiji/Fiji.app/jars
cp target/dependency/commons-csv-1.2.jar ~/fiji/Fiji.app/jars

~/fiji/Fiji.app/ImageJ-linux64 --debugger=8000

