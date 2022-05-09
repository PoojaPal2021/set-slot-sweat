#! /bin/bash
############################################################################################
#                            Gym Reservations App
#
# Script for building and running tests for the middle layer microservices of a
# Gym Reservation App
#
# In order for the microservices to run you need to have:
#
#   1. Java: At least Java 11
#   2. JAVA_HOME environment variable should be set
#
# This script uses the Maven Wrapper (mvnw) that comes with Spring Boot, so
# you don't need to install maven.
#
# We skip the tests when building the project; some of the test don't pass
# since the project is still under development.
#
# This script executes the following actions:
#
#   1. Build both user-management and gym-reservation microservices.
#   2. Run tests for user-management service. Has no dependency.
#   3. Run tests for gym-reservation service. This requires the user-management
#      to be running, so we start it as a background process. This process gets
#      killed at the end of the tests.
#
############################################################################################

############################################################################################
#                               BUILD THE MICROSERVICES                                    #
############################################################################################

# Check that JAVA_HOME is set
if [ -z ${JAVA_HOME+x} ]; then
    echo "JAVA_HOME is not set. Please set it to JDK with Java 11 or greater."
    exit 1
fi

cd users
./mvnw clean package -Dmaven.test.skip=true

cd ../reservations
./mvnw clean package -Dmaven.test.skip=true

############################################################################################
#                               RUN user-magement tests                                    #
############################################################################################

cd ../users
./mvnw test

############################################################################################
#                             RUN gym-reservation tests                                    #
############################################################################################

# user-management service must be running for this tests to run, so we start the service
# in the background.
# We save the process PID so we can kill it at the of the test
java -jar target/user-management-0.0.1-SNAPSHOT.jar &> /dev/null &
SERVICE_PID=$!

cd ../reservations
./mvnw test

# Kil the user-management process at the end of the tests
kill $SERVICE_PID

exit 0