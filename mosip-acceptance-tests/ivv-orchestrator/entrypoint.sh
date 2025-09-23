#!/bin/bash

files=$( find /tmp/scenarios -type f )

if [[ $DEFAULT_SCENARIOS == "true" ]]; then
  for file in $files; do
    domain=$(printenv installation-domain)
    dir_name="$mountPathForScenario/scenarios/";
    file_name=$(basename $file | sed "s/env/$domain/g")
    echo "DIRECTORY and MOUNT PATH : $dir_name/$file_name";
    mkdir -p $dir_name;
    cp "$file" "$dir_name/$file_name";
  done
fi

## Run DSL Orchestrator
java --version
java -jar -Denv.user="$USER" -Denv.endpoint="$ENDPOINT" -Denv.testLevel="$TESTLEVEL" dslrig-ivv-orchestrator-*-jar-with-dependencies.jar
