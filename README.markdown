vc-Wiki
========================================

## Building and running

### Prerequisites

* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) 1.7 or later

### Building WAR file

```
./gradlew --daemon war
```

### Running on Jetty for development

```
# Start
./gradlew jettyRun &
# Stop
./gradlew jettyStop
```
