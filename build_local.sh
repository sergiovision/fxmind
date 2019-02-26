# set path to your JAVA 8 HOME path. Builds properly with JAVA 1.8
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home
# export JAVA_HOME
M2_HOME=/opt/local/share/java/maven3
MAVEN_OPTS="-Xmx2048m -XX:+CMSClassUnloadingEnabled"
export M2_HOME
export MAVEN_OPTS
mvn -T 2C -P local clean install 