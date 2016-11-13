#!/bin/bash
cd ..

export JAVA_CP_OPTS="-classpath bin/waldo-exif-store.jar:bin/dependency-jars/*"
export JAVA_MAIN_CLASS="waldo.exifstore.App"

nohup java $JAVA_CP_OPTS $JAVA_MAIN_CLASS