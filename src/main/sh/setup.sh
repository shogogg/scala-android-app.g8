#!/bin/sh
#
# This is a workaround of missing dependency on scripted-sbt against
# scala 2.9.1 and sbt 0.12.4.

wget -P ./target http://repo.typesafe.com/typesafe/ivy-releases/org.scala-sbt/scripted-sbt/0.12.4/jars/scripted-sbt.jar

mvn install:install-file \
  -Dfile=./target/scripted-sbt.jar \
  -DgroupId=org.scala-sbt \
  -DartifactId=scripted-sbt_2.9.1 \
  -Dversion=0.12.4 \
  -Dpackaging=jar
