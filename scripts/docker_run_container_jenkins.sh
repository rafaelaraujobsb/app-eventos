#!/usr/bin/env bash
set -xeuo pipefail

docker run --name container-jenkins -p 8080:8080 -p 8081:50000 eventando/jenkins