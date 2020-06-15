#!/usr/bin/env bash
set -e;

for collection in ./collections//*; do
    newman run "$collection" --bail --environment=demo.postman_environment.json
done