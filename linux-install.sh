#!/bin/bash

# Shoutout to the author of this script: https://github.com/anjannair

# Set these values so the installer can still run in color
COL_NC='\e[0m' # No Color
COL_LIGHT_GREEN='\e[1;32m'
COL_LIGHT_RED='\e[1;31m'
TICK="[${COL_LIGHT_GREEN}✓${COL_NC}]"
CROSS="[${COL_LIGHT_RED}✗${COL_NC}]"
INFO="[i]"

# Note - This script is just for installation and not tweaked for updating the application

printf "$INFO Checking for existing Java installation...\n"
if type -p java; then
    printf "$TICK Java already installed. Looks like you're good to go!\n"
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    printf "$TICK Java already installed. Looks like you're good to go!\n"
else
    printf "$CROSS Java installation was not found. Please install Java and try again.\nLink: https://www.oracle.com/java/technologies/downloads/#java18 \n"
    exit 0

fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" > "17.0.1" ]]; then
        printf "$TICK Congratulations! You have a compatible version of Java installed.\n"
    else
        printf "$CROSS Your version of Java is not compatible. Please install Java version 17.0.2 or higher and try again.\nLink: https://www.oracle.com/java/technologies/downloads/#java18 \n"
        exit 0
    fi
fi

printf "${TICK} All checks passed. Proceeding with installation.\n"

printf "${INFO} Installing the latest version of Keep My Password Desktop\nThis would require root privileges.\n"
# Change this every time you update Keep My Password Desktop
latestVersion="3.1.0"
# Download the latest version of Keep My Password Desktop using the GitHub releases links
# There could be a better way to do this using regex but since recent releases did not have a Linux version
# I decided to use the GitHub releases links to download the latest version of the desktop application
sudo mkdir -p /opt/KeepMyPassword
sudo wget https://github.com/xEdziu/KeepMyPassword-Desktop/releases/download/v"$latestVersion"/KeepMyPassword-Desktop-"$latestVersion"-linux.jar -P /opt/KeepMyPassword
sudo wget https://raw.githubusercontent.com/xEdziu/KeepMyPassword-Desktop/main/src/main/resources/me/goral/keepmypassworddesktop/images/access-32.png -P /opt/KeepMyPassword
echo "[Desktop Entry]
Type=Application
Encoding=UTF-8
Name=Keep My Password
Comment=Keep My Password
Exec=java -jar /opt/KeepMyPassword/KeepMyPassword-Desktop-$latestVersion-linux.jar
Terminal=false
Icon=/opt/KeepMyPassword/access-32.png" > KeepMyPassword.desktop
sudo mv KeepMyPassword.desktop /usr/share/applications/

printf "$TICK Keep My Password Desktop installed successfully!\nYou can start it from the Applications menu.\n"