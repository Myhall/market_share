#!/usr/bin/env bash
set -euo pipefail

project_root="$(cd "$(dirname "$0")/.." && pwd)"
repo_root="${project_root}/.mvn/repository"

artifacts=(
  "${repo_root}/com/opencsv/opencsv/5.9/opencsv-5.9.jar"
  "${repo_root}/org/apache/commons/commons-lang3/3.13.0/commons-lang3-3.13.0.jar"
  "${repo_root}/org/apache/commons/commons-text/1.11.0/commons-text-1.11.0.jar"
  "${repo_root}/commons-beanutils/commons-beanutils/1.9.4/commons-beanutils-1.9.4.jar"
  "${repo_root}/org/apache/commons/commons-collections4/4.4/commons-collections4-4.4.jar"
  "${repo_root}/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar"
  "${repo_root}/commons-logging/commons-logging/1.2/commons-logging-1.2.jar"
)

for artifact in "${artifacts[@]}"; do
  if [[ ! -f "${artifact}" ]]; then
    printf 'Missing dependency jar: %s\n' "${artifact}" >&2
    exit 1
  fi
done

classpath=''
for artifact in "${artifacts[@]}"; do
  if [[ -z "${classpath}" ]]; then
    classpath="${artifact}"
  else
    classpath="${classpath}:${artifact}"
  fi
done

printf '%s\n' "${classpath}"
