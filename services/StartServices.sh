#! /bin/bash
############################################################################################
#                            Gym Reservations App
#
# Script for building running test for the middle layer microservices of a
# Gym Reservation App
#
# In order for the microservices to run you need to have:
#
#   1. Java: At least Java 11
#   2. JAVA_HOME environment variable should be set
#
#
# We skip the tests when building the project; some of the test don't pass
# since the project is still under development.
#
# This script executes the following actions:
#
#   1. 
#
############################################################################################

############################################################################################
#                             RUN MICROSERVICES                                            #
############################################################################################

# user-management service must be running for gym-reservations to work, so we start it
# in the background.

# This is a trap for the kill signal so the user-management process running in the
# background gets kill at the we ctrl+c its parent process (this script).
trap 'kill $BGPID; exit' INT

# Start user-management in the background
java -jar users/target/user-management-0.0.1-SNAPSHOT.jar &> /dev/null &
BGPID=$!

# Start second process
java -jar reservations/target/gym-reservations-0.0.1-SNAPSHOT.jar