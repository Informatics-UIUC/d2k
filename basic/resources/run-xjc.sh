#!/bin/sh

#setenv JAVA_HOME /home/mcgrath/jdk1.5.0_04
#setenv CATALINA_HOME /home/mcgrath/apache-tomcat-5.5.12
#setenv JAVA_OPTS "-Xms64m -Xmx128m -verbose" 

JAVA_HOME=/opt/jdk1.5.0_07
export JAVA_HOME

cd /home/mcgrath/workspace/basicxxxpost412
sh /home/mcgrath/JAXB/jaxb-ri-20060426/bin/xjc.sh -npa -p ncsa.d2k.modules.core.io.proxy -d /home/mcgrath/workspace/basicxxxpost412/src resources/cacheManager.xsd

