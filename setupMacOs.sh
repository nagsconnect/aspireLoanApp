#!/bin/bash

# Function to check if a command is available
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check if Homebrew is installed, and install if not
if ! command_exists brew; then
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install.sh)"
fi

# Check if Java 17 is installed, and install if not
if ! command_exists java; then
    brew install openjdk@17
    echo "Java 17 installed."
else
    echo "Java 17 already installed."
fi

# Check if Maven is installed, and install if not
if ! command_exists mvn; then
    brew install maven
    echo "Maven installed."
else
    echo "Maven already installed."
fi

# Clone the GitHub repository to the default path
cd ~  # Change to home directory or specify another directory
if [ -d "aspireLoanApp" ]; then
    echo "Repository aspireLoanApp already exists."
else
    git clone https://github.com/nagsconnect/aspireLoanApp.git
    echo "Repository aspireLoanApp cloned."
fi

# Change to the repository directory
cd aspireLoanApp

# Run Maven commands
mvn clean -e install