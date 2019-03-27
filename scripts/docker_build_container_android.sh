#!/usr/bin/env bash
set -xeuo pipefail

docker build -f Android.Dockerfile -t eventando/android .