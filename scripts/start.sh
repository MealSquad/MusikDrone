#!/bin/bash

# Kill current running process
if [ $(pgrep -f SCREEN) ]; then
        kill -9 $(pgrep -f SCREEN)
        screen -wipe
fi

# Timestamp
echo "*************************************"
echo "Starting MusikDrone $(date)"
echo "*************************************"

cd /home/ec2-user/

# Make gradlew executable
sudo chmod +x gradlew

# Clean build space
echo "Cleaning workspace"
sudo ./gradlew clean

# Build
echo "Building workspace"
sudo ./gradlew build

# Run
screen -dmS MusikDrone
sleep .5
screen -S MusikDrone -p 0 -X stuff $'sudo ./gradlew run\n'
