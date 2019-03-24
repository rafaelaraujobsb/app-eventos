#!/usr/bin/env bash
set -xeuo pipefail

docker build -f Jenkins.Dockerfile -t eventando/jenkins .