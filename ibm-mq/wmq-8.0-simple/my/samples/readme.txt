1) Examples
credit - https://hursleyonwmq.wordpress.com/2007/05/29/simplest-sample-applications-using-websphere-mq-jms/

Simple Point-to-point application using WebSphere MQ JMS
http://www.java2s.com/Code/Jar/j/Downloadjavaxjmsapi20jar.htm

javac -cp "lib/*:" SimplePTP.java
javac -cp "lib/*:" SimplePubSub.java

java  -cp "lib/*:" SimplePTP     
java  -cp "lib/*:" SimplePubSub

2) IBM MQ jars for weblogic app server

${WEBLOGIC_HOME}/user_projects/domains/<your_domain>/lib/com.ibm.mq.jmqi-7.0.1.4.jar
${WEBLOGIC_HOME}/user_projects/domains/<your_domain>/lib/com.ibm.mqjms-7.0.1.4.jar
${WEBLOGIC_HOME}/user_projects/domains/<your_domain>/lib/com.ibm.disthub2.dhbcore-7.0.1.4.jar

${WEBLOGIC_HOME}/wlserver/server/lib/wljaccutil-ibm.jar (installed by default)

Maven

    <!-- IBM MQ -->
    <dependency>
        <groupId>com.ibm</groupId>
        <artifactId>com.ibm.mqjms</artifactId>
    </dependency>
    <dependency>
        <groupId>com.ibm</groupId>
        <artifactId>com.ibm.mq.jmqi</artifactId>
    </dependency>
    <dependency>
        <groupId>com.ibm</groupId>
        <artifactId>com.ibm.disthub2.dhbcore</artifactId>
    </dependency>


