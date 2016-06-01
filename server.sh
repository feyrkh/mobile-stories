cd target/classes
java -jar ../coven-1.0.0-SNAPSHOT.jar db migrate config.yml
java -jar ../coven-1.0.0-SNAPSHOT.jar server config.yml