#!/bin/sh

############################################################################
# JHOVE2 - Next-generation architecture for format-aware characterization
# Copyright 2009 by The Regents of the University of California, Ithaka
# Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
# University.
# All rights reserved.
#
# jhove2_doc.sh  - JHOVE2 Reportable documentation utility. The
# properties of the reportable (name, identifier, properties, messages) are
# determined by reflection of the class.
#
# usage: org.jhove2.app.util.JHOVE2Doc reportableBeanName ...
#
# NOTE: Edit the following lines to refer to the proper file system paths
#       of the JHOVE2 installation directory and the Java command

JAVA=/usr/bin/java
JHOVE2_HOME=/usr/jhove2-1.9.5

CP=$JHOVE2_HOME/lib/jhove2-1.9.5.jar:$JHOVE2_HOME/lib/aopalliance-1.0.jar:$JHOVE2_HOME/lib/commons-logging-1.1.1.jar:$JHOVE2_HOME/lib/commons-logging-api-1.1.jar:$JHOVE2_HOME/lib/jdom-1.0.jar:$JHOVE2_HOME/lib/junit-4.4.jar:$JHOVE2_HOME/lib/log4j-1.2.14.jar:$JHOVE2_HOME/lib/jargs-1.0.jar:$JHOVE2_HOME/lib/spring-beans-2.5.6.jar:$JHOVE2_HOME/lib/spring-context-2.5.6.jar:$JHOVE2_HOME/lib/spring-core-2.5.6.jar:$JHOVE2_HOME/lib/spring-test-2.5.6.jar:$JHOVE2_HOME/lib/soap-2.3.1.jar:$JHOVE2_HOME/lib/xercesImpl-2.9.1.jar:$JHOVE2_HOME/lib/xml-apis-1.3.04.jar:$JHOVE2_HOME/lib/xml-resolver-1.2.jar:$JHOVE2_HOME/config:$JHOVE2_HOME/config/droid

# NOTE: Nothing below this line should be edited
############################################################################

ARGS=""
for ARG do
    ARGS="$ARGS $ARG"
done

${JAVA} -classpath $CP org.jhove2.app.util.JHOVE2Doc $ARGS