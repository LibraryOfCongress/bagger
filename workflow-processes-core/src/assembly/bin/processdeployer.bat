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
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of agruments (up to the command line limit, anyway).
set ARGS=
:Win9xApp
if %1a==a goto endInit
	set ARGS=%ARGS% %1
	shift
goto Win9xApp

:endInit
"%JAVA%" -Xmx%MAXMEM% -classpath ../lib/classworlds-1.1.jar -Dclassworlds.conf=processdeployer.classworlds.conf -Dapp.home=.. org.codehaus.classworlds.Launcher %ARGS%

:end

