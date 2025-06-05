@echo off
echo Compiling and Running Login Application...

:: Set paths
set PROJECT_LIB=lib\mysql-connector-j-8.2.0.jar
set BIN_DIR=build\classes
set SRC_DIR=src
set MAIN_CLASS=loginandsignup.LoginAndSignUp

:: Create build directory if it doesn't exist
if not exist %BIN_DIR% mkdir %BIN_DIR%

:: Compile with MySQL connector in classpath
echo Compiling Java files...
javac -d %BIN_DIR% -cp "%PROJECT_LIB%" %SRC_DIR%\loginandsignup\*.java

:: Run with MySQL connector in classpath
echo Starting application...
java -cp "%BIN_DIR%;%PROJECT_LIB%" %MAIN_CLASS%

pause 