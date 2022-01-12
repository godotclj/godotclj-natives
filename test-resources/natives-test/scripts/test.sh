#!/usr/bin/env bash

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

export JAVA_TOOL_OPTIONS=-Dgodotclj.config.path=$DIR/../godotclj-test.edn

cd $DIR/.. && clj -M -e "(require 'godotclj.runner) (godotclj.runner/start \"-v\")"
