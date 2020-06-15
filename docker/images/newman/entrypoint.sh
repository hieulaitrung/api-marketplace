#!/usr/bin/env bash

./wait-for-it.sh $HEALTH_CHECK --timeout=300 -- ./newman.sh