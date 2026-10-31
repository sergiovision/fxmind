@echo off
rem Builds FXMind with the bundled Maven wrapper. Requires JDK 25+.
call "%~dp0mvnw.cmd" -B -T 1C clean install -Pproduction %*
