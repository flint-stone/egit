How to compile the project?
Use command line: javac -cp ./lib/commons-logging-1.1.3.jar:./lib/log4j-1.2.17.jar -d bin src/edu/uiuc/cs/cs425/*.java

How to run the program

For KVServer side with port:
java -cp ./bin:./lib/commons-logging-1.1.3.jar:./lib/log4j-1.2.17.jar edu.uiuc.cs.cs425.myKV.KVServer server.config $Gossipport $CommandPort 

For Command Client
java -cp ./bin:./lib/log4j-1.2.17.jar:./lib/commons-logging-1.1.3.jar edu.uiuc.cs.cs425.myKV.CommandClient.CommandClient $KVserverIP $CommandPort

For Xgrep 

For Server side with port:
java -cp ./bin edu.uiuc.cs.cs425.Server port
For Client side, please
1. configure the config/server.config file with line like
   machine 192.168.1.100 8080 machine.1.log
2. run Client program
   java -cp ./bin edu.uiuc.cs.cs425.Client config/server.config