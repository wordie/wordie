#!/bin/sh

echo "Removing existing files..."
rm -rf ../deployment/deployment/binaries/*
echo "...done."

echo "Building and moving..."
lein uberjar && mv target/wordie.jar ../deployment/deployment/binaries/
echo "...done."

