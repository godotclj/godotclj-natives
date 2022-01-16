#!/usr/bin/env bash
set -euxo pipefail

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

cd $DIR/.. && clj -M -e "(require 'godotclj.runner) (godotclj.runner/start \"-v\")"
