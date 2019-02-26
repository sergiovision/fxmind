
# fxmind java


This project shows approaches in trading software using Java technologies.
New version of this application written in .NET and can be downloaded here: [XTradeServer](https://github.com/sergiovision/XTradeServer)

Forex robot written in java, apache thrift and MQL, that helps you train with Forex robot.
fxmind java

Project should be built using Java 8.

Project built using following technologies and libraries:

Java - Server and Client app.

Vaadin - Webclient website to monitor and config java part of the robot. 

Apache Thrift - for Metatrader extension and Java code to fetch data from the Java part of the code to MQL robot algorithm.

Quartz Java  - to scheduled jobs execution. Get Currencies Rates, News,  Financial data on scheduled basis using this wonderful library.

MySQL - to store financial data in DB and for trading history. 

Folder list:

businesslogic - main business logic. Fetching/storing Financial data in DB 

webclient - website written in Vaadin 7.X for java part of the robot administration and monitoring. Using this site you can access status of robot from any location just by accessing it https://yourserver/webclient. It is a .war file which can run in any java web server like Tomcat, Jboss etc.


Project developed completely as a hobby project. For learning forex trading, robots and perform automated trading.

Code tested and works stable on the server. But you still have to properly configure Forex robot algorithm and sure that your server has permanent internet connection and forex dealer is reliable.

If you need help building and setting up robot write me to my email: sergewinner @ gmail com

Allowed to use in your programs and enhance your robots, just mention my name in your products.

