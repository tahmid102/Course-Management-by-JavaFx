#!/bin/bash

echo "Starting Database Server for Student Management System..."
echo "========================================================="

# Check if Java is available
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# Navigate to project directory
cd "$(dirname "$0")"

# Check if the classes are compiled
if [ ! -d "target/classes" ] && [ ! -d "out" ]; then
    echo "Error: Project classes not found. Please compile the project first."
    echo "Run: mvn compile"
    exit 1
fi

# Set classpath
if [ -d "target/classes" ]; then
    CLASSPATH="target/classes"
elif [ -d "out" ]; then
    CLASSPATH="out"
fi

echo "Using classpath: $CLASSPATH"
echo "Starting server on port 44444..."
echo "Press Ctrl+C to stop the server"
echo ""

# Start the server
java -cp "$CLASSPATH" files.Server.DatabaseServer

echo ""
echo "Server stopped."