#!/bin/sh

############################################################################
# JHOVE2 - Next-generation architecture for format-aware characterization
# Copyright 2009 by The Regents of the University of California, Ithaka
# Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
# University.
# All rights reserved.
#
# Usage: jhove2 [-ik] [-b size] [-B Direct|NonDirect|Mapped]
#               [-d JSON|Text|XML] [-f limit] [-t temp] [-T] [-o file] file ...
#
#  -i  Show the unique formal identifiers for all reportable properties in results.
#  -k  Calculate message digests.
#  -b size     I/O buffer size (default=131072)
#  -B scope     I/O buffer type (default=Direct)
#  -d format   Results format (default=Text)
#  -f limit    Fail fast limit (default=0; no limit on the number of reported errors.
#  -t temp     Temporary directory (default=java.io.tmpdir)
#  -T  Delete temporary files
#  -o file     Output file (default=standard output unit)
#  -h  Display a help message
#  file ...    One or more files or directories to be characterized.
#
# NOTE: Edit the following lines to refer to the proper file system paths
#       of the JHOVE2 installation directory and the Java command

JAVA=/usr/bin/java
JHOVE2_HOME=/usr/jhove2

CP=$JHOVE2_HOME/lib/jhove2-1.9.5.jar:$JHOVE2_HOME\aopalliance\aopalliance\1.0\aopalliance-1.0.jar:$JHOVE2_HOME\commons-logging\commons-logging\1.1.1\commons-logging-1.1.1.jar:$JHOVE2_HOME\commons-logging\commons-logging-api\1.1\commons-logging-api-1.1.jar:$JHOVE2_HOME\jdom\jdom\1.0\jdom-1.0.jar:$JHOVE2_HOME\junit\junit\4.4\junit-4.4.jar:$JHOVE2_HOME\log4j\log4j\1.2.14\log4j-1.2.14.jar:$JHOVE2_HOME\net\sf\jargs\1.0\jargs-1.0.jar:$JHOVE2_HOME\org\springframework\spring-beans\2.5.6\spring-beans-2.5.6.jar:$JHOVE2_HOME\org\springframework\spring-context\2.5.6\spring-context-2.5.6.jar:$JHOVE2_HOME\org\springframework\spring-core\2.5.6\spring-core-2.5.6.jar:$JHOVE2_HOME\org\springframework\spring-test\2.5.6\spring-test-2.5.6.jar:$JHOVE2_HOME\soap\soap\2.3.1\soap-2.3.1.jar:$JHOVE2_HOME\xerces\xercesImpl\2.9.1\xercesImpl-2.9.1.jar:$JHOVE2_HOME\xml-apis\xml-apis\1.3.04\xml-apis-1.3.04.jar:$JHOVE2_HOME\xml-resolver\xml-resolver\1.2\xml-resolver-1.2.jar:$JHOVE2_HOME/config:$JHOVE2_HOME/config/droid

# NOTE: Nothing below this line should be edited
############################################################################

ARGS=""
for ARG do
    ARGS="$ARGS $ARG"
done

${JAVA} -classpath $CP org.jhove2.app.JHOVE2CommandLine $ARGS
