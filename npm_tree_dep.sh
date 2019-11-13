#!/usr/bin/env bash

[[ -z $(command -v jq) ]] && {
    echo "please install jq in your system https://stedolan.github.io/jq/"
    exit 1
}

[[ -z $(command -v tree) ]] && {
    echo "please install tree in your system https://linux.die.net/man/1/tree"
    exit 1
}

package="${1}"
version="${2}"

[[ -z "${package}" || -z "${version}" ]] && {
  echo "Usage: npm_dep_tree.sh <package> <version>"
  exit 1
}

set -euo pipefail
host="localhost:8081"

result=$(curl "${host}"/tree/"${package}"/"${version}")

tempfile=$(mktemp)
root=$(echo "${result}" | jq -r '(.name + "-" + .version)')
dependencies=$(echo "${result}" | jq -r '.dependencies')

echo "${root}" >> "${tempfile}"

write_dependencies() {
  local json="${1}"
  local prefix="${2}"

  for row in $(echo "${json}" | jq -r '.[] | @base64'); do
     dependency=$(echo "${row}" | base64 --decode)
     name_version=$(echo "${dependency}" | jq -r '(.name + "-" + .version)')

     new_prefix="${prefix}"/"${name_version}"
     echo "${new_prefix}" >> "${tempfile}"
     next_dependencies=$(echo "${dependency}" | jq -r '.dependencies')

     if [ "${next_dependencies}" != '[]' ]; then
       write_dependencies "${next_dependencies}" "${new_prefix}"
     fi
  done
}

write_dependencies "${dependencies}" "${root}"

tree --fromfile "${tempfile}"
