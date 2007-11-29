@ECHO OFF
@REM ----------------------------------------------------------------------------
@REM Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM ----------------------------------------------------------------------------

REM If JAVA_HOME is not set, uncomment and use below
REM SET JAVA_HOME=C:\java\jre1.5.0_02

REM DO NOT EDIT BELOW THIS LINE
SET JAVA=%JAVA_HOME%\bin\java
SET MAXMEM=512m

:checkJava
if not "%JAVA_HOME%" == "" goto execute

echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto end

:execute
REM Retrieve a copy of all command line arguments to pass to the application
SET ARGS=
:while
if "%1"=="" goto loop
  set ARGS=%ARGS% %1
  shift
  goto while
:loop

"%JAVA%" -Xmx%MAXMEM% -classpath ../lib/classworlds-1.1.jar -Dclassworlds.conf=classworlds.conf -Dapp.home=.. org.codehaus.classworlds.Launcher %ARGS%

:end

