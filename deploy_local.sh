# mvn -P local clean install tomcat7:redeploy -e
CATALINA_BASE=/Users/sergeizhuravlev/Documents/tomcatf

rm $CATALINA_BASE/webapps/webclient.war
rm -rf $CATALINA_BASE/webapps/webclient

rm -rf $CATALINA_BASE/logs/*

cp ./webclient/target/webclient.war $CATALINA_BASE/webapps

