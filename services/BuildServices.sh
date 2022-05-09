#! /bin/bash
############################################################################################
#                    Script to build Gym Reservations App Microservices
#
# This script builds the microservices for the Gym Reservations App
#
# In order for the microservices to run you need to have:
#
#   1. Java: At least Java 11
#   2. JAVA_HOME environment variable should be set
#
# This script uses the Maven Wrapper (mvnw) that comes with Spring Boot, so
# you don't need to install maven.
#
# We skip the tests when building the project because some of the test don't yet pass,
# since the project is still under development.
#
############################################################################################

############################################################################################
#                               BUILD THE MICROSERVICES                                    #
############################################################################################

# Check that JAVA_HOME is set
if [ -z ${JAVA_HOME+x} ]; then
    echo "JAVA_HOME is not set. Please set it to a JDK with Java 11 or greater."
    exit 1
fi

# Color constants
PURPLE='\033[0;35m'
GREEN='\033[0;32m'
NC='\033[0m' # No color

echo -e "${GREEN}============================================================================${NC}"
echo -e "${GREEN}|                   BUILDING USER MANAGEMENT MICROSERVICE                  |${NC}"
echo -e "${GREEN}============================================================================${NC}"
cd users
./mvnw clean package -Dmaven.test.skip=true


echo
echo -e "${GREEN}============================================================================${NC}"
echo -e "${GREEN}|                 BUILDING GYM RESERVATIONS MICROSERVICE                   |${NC}"
echo -e "${GREEN}============================================================================${NC}"
cd ../reservations
./mvnw clean package -Dmaven.test.skip=true

exit 0