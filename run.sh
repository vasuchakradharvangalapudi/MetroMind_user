#!/usr/bin/env bash
# MetroMind - build and run (macOS / Linux). Needs JDK 11 or newer.
cd "$(dirname "$0")"
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java") || { echo "Compilation failed"; exit 1; }
echo "Open http://localhost:8080 in your browser"
java -cp out metromind.Main 8080
