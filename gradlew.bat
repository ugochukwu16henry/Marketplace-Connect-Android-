@ECHO OFF
SETLOCAL

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
set APP_HOME=%DIRNAME%

set CLASSPATH=%APP_HOME%\gradle\wrapper\gradle-wrapper.jar

set _JAVACMD=%JAVA_HOME%\bin\java.exe
if not "%JAVA_HOME%" == "" if exist "%_JAVACMD%" goto execute

set _JAVACMD=java.exe
%_JAVACMD% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto execute

echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
goto fail

:execute
"%_JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
if "%ERRORLEVEL%" == "0" goto end

:fail
exit /b 1

:end
ENDLOCAL
exit /b 0
