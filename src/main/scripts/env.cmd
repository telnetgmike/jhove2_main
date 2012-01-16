@ECHO OFF
REM JHOVE2 -- Next-generation architecture for format-aware characterization.
REM Copyright 2009-2010 by The Regents of the University of California, Ithaka
REM Harbors, Inc., and The Board of Trustees of the Leland Stanford Junior
REM University.  All rights reserved.
REM
REM This file is called by all the other .cmd files in order to set JAVA,
REM JHOVE2_HOME, and CP. Edit this file if you need to modify the settings for
REM these variables or define additional variables, such as JAVA_HOME. For more
REM information, refer to the JHOVE2 User's Guide at http://jhove2.org.
REM
REM NOTE: The jhove2-<version>.jar filename jhove2-2.0.0 
REM is injected via filtering as part of the maven build process.    
REM CP is set manually until Maven bug of filtering Windows path is fixed


REM If JAVA_HOME is not set, use java.exe in execution path
if "%JAVA_HOME%" == "" (
   set JAVA=java
) else (
   set JAVA="%JAVA_HOME%\bin\java"
)

REM JHOVE2_HOME must point to home directory of JHOVE2 install.
SET JHOVE2_HOME=%~dp0

REM CP must contain a semicolon-separated list of JARs used by JHOVE2.
SET CP=%JHOVE2_HOME%\lib\jhove2-2.0.0.jar;%JHOVE2_HOME%\lib\je-4.0.103.jar;%JHOVE2_HOME%\lib\aopalliance-1.0.jar;%JHOVE2_HOME%\lib\jts-1.10.jar;%JHOVE2_HOME%\lib\commons-beanutils-1.7.0.jar;%JHOVE2_HOME%\lib\commons-logging-1.1.1.jar;%JHOVE2_HOME%\lib\commons-logging-api-1.1.jar;%JHOVE2_HOME%\lib\commons-pool-1.5.3.jar;%JHOVE2_HOME%\lib\vecmath-1.3.2.jar;%JHOVE2_HOME%\lib\jdom-1.0.jar;%JHOVE2_HOME%\lib\junit-4.4.jar;%JHOVE2_HOME%\lib\log4j-1.2.14.jar;%JHOVE2_HOME%\lib\jsr-275-1.0-beta-2.jar;%JHOVE2_HOME%\lib\jargs-1.0.jar;%JHOVE2_HOME%\lib\stringtemplate-3.2.jar;%JHOVE2_HOME%\lib\gt-api-2.6.5.jar;%JHOVE2_HOME%\lib\gt-main-2.6.5.jar;%JHOVE2_HOME%\lib\gt-metadata-2.6.5.jar;%JHOVE2_HOME%\lib\gt-referencing-2.6.5.jar;%JHOVE2_HOME%\lib\gt-shapefile-2.6.5.jar;%JHOVE2_HOME%\lib\mvel2-2.0.18.jar;%JHOVE2_HOME%\lib\geoapi-2.3-M1.jar;%JHOVE2_HOME%\lib\geoapi-pending-2.3-M1.jar;%JHOVE2_HOME%\lib\spring-beans-2.5.6.jar;%JHOVE2_HOME%\lib\spring-context-2.5.6.jar;%JHOVE2_HOME%\lib\spring-core-2.5.6.jar;%JHOVE2_HOME%\lib\spring-test-2.5.6.jar;%JHOVE2_HOME%\lib\soap-2.3.1.jar;%JHOVE2_HOME%\lib\xercesImpl-2.9.1.jar;%JHOVE2_HOME%\lib\xml-apis-1.3.04.jar;%JHOVE2_HOME%\lib\xml-resolver-1.2.jar;%JHOVE2_HOME%\config\droid;%JHOVE2_HOME%\config

SET CP=%CP%;%JHOVE2_HOME%\lib\jwat-arc-0.8.0-20120110.100649-11.jar
SET CP=%CP%;%JHOVE2_HOME%\lib\jwat-common-0.8.0-20120110.100632-11.jar
SET CP=%CP%;%JHOVE2_HOME%\lib\jwat-gzip-0.8.0-SNAPSHOT.jar
SET CP=%CP%;%JHOVE2_HOME%\lib\jwat-warc-0.8.0-20120110.100712-11.jar
