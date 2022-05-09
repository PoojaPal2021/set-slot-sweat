#! /bin/bash
############################################################################################
#                    Script to start Gym Reservations App
#
# This script starts the middle layer microservices of the Gym Reservation App.
#
# In order for the microservices to run you need to have:
#
#   1. Java: At least Java 11
#   2. JAVA_HOME environment variable should be set
#
# user-management service must be running for gym-reservations to work, so we start it
# in the background.
#
############################################################################################

############################################################################################
#                             RUN MICROSERVICES                                            #
############################################################################################

# This is a trap for the kill signal so the user-management process running in the
# background gets kill at the we ctrl+c its parent process (this script).
trap 'kill $BGPID; exit' INT

# Color constants
PURPLE='\033[0;35m'
GREEN='\033[0;32m'
NC='\033[0m' # No color

# Start user-management in the background
echo -e "${GREEN}============================================================================${NC}"
echo -e "${GREEN}|           START USER MANAGEMENT SERVICE IN THE BACKGROUND                |${NC}"
echo -e "${GREEN}============================================================================${NC}"

java -jar users/target/user-management-0.0.1-SNAPSHOT.jar &> /dev/null &
BGPID=$!

# Start second process
echo -e "${PURPLE}User Management Service successfully started ...${NC}"
echo
echo -e "${GREEN}============================================================================${NC}"
echo -e "${GREEN}|           START GYM RESERVATIONS SERVICE IN THE FOREGROUND               |${NC}"
echo -e "${GREEN}============================================================================${NC}"
java -jar reservations/target/gym-reservations-0.0.1-SNAPSHOT.jar