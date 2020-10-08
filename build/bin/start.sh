JAVA_PATH=/home/insoft/jdk1.8.0_191/bin/java

ROOT_PATH=/home/insoft/undertow

ACTIVE_PROFILE=run

API_PATH=$ROOT_PATH/lib/undertow.jar

JAVA_OPTS="-server -Dhome=$ROOT_PATH -Dspring.profiles.active=$ACTIVE_PROFILE"
JAVA_OPTS="$JAVA_OPTS -Dfile.encoding=UTF-8"
JAVA_OPTS="$JAVA_OPTS -Xms512m -Xmx1024m -XX:+HeapDumpOnOutOfMemoryError"

$JAVA_PATH $JAVA_OPTS -jar $API_PATH

#Properties Launcher 로 실행하는 방법 추가
#$JAVA_PATH $JAVA_OPTS -cp $API_PATH -Dloader.path=BOOT-INF/classes,BOOT-INF/lib,file:$ROOT_PATH/lib org.springframework.boot.loader.PropertiesLauncher &
