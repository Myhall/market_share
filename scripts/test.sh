#!/usr/bin/env bash
set -euo pipefail

cd "$(dirname "$0")/.."
mvn -q test-compile
test_classpath=$(./scripts/classpath.sh)
java -cp "target/classes:target/test-classes:${test_classpath}" com.idc.interview.MarketShareCheckRunner
