#!/bin/bash

# Note - This script is just for installation and not tweaked for updating the application

printf "Checking for existing Java installation...\n"
if type -p java; then
    printf "Java already installed. Looks like you're good to go!\n"
    _java=java
elif [[ -n "$JAVA_HOME" ]] && [[ -x "$JAVA_HOME/bin/java" ]]; then
    printf "Java already installed. Looks like you're good to go!\n"
else
    printf "Java installation was not found. Please install Java and try again.\nLink: https://www.oracle.com/java/technologies/downloads/#java18 \n\n"
    exit 0

fi

if [[ "$_java" ]]; then
    version=$("$_java" -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$version" > "17.0.2" ]]; then
        printf "Congratulations! You have a compatible version of Java installed.\n"
    else
        printf "Your version of Java is not compatible. Please install Java version 17.0.2 or higher and try again.\n"
        exit 0
    fi
fi

printf "All checks passed. Proceeding with installation.\n"

printf "Installing the latest version of Keep My Password Desktop\nThis would require root privileges.\n"
# Change this every time you update Keep My Password Desktop
latestVersion="3.0.0"
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
