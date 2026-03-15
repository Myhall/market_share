#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
mvn -q -DskipTests compile
runtime_classpath=$(./scripts/classpath.sh)
java -cp "target/classes:${runtime_classpath}" com.idc.interview.MarketShareDemo
