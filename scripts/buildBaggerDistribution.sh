cd ../bagger-maven
mvn clean install
cp ../bagger-package/target/bagger ../../bagger_distribution/bagger-2.1.1.jar
zip bagger_distribution

