# Log Events Parser
This application is reading the log file with log entries in JSON format and creating server events based on that.
Log entries have mandatory fields **id**, **state**, **timestamps** and optional ones **type** and **host**. Log entries
with the same **id** and **state** are treated as duplicate. Events sreated from log entries are stored in HyperSQL
(hsqldb) database. 

## Compile project
To compile project, run maven command in the main project folder:

`mvn clean package`

## Run application
To run the application, execute this command in the **target** folder:

`java -jar log-events-parser-1.0.jar --logfile=<PATH_TO_LOG_FILE>`

The **logfile** parameter is mandatory. To run the application in debug mode execute the command:

`java -jar -Dlogging.level.com.creditsuisse.recruitment=DEBUG log-events-parser-1.0.jar --logfile=<PATH_TO_LOG_FILE>`

## Author

* **Sebastian Mielcarek** - *Java Software Developer* - [Mielcarek.S@gmail.com](mailto:Mielcarek.S@gmail.com) 