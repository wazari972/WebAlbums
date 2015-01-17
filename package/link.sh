#!/bin/bash
# http://stackoverflow.com/a/5027832/341106
# both $1 and $2 are absolute paths
# returns $2 relative to $1

target=$1
source=$2

target=$1
common_part=$(dirname $source)
back=
while [ "${target#$common_part}" = "${target}" ]; do
  common_part=$(dirname $common_part)
  back="../${back}"
done

good_target=${back}${target#$common_part/}

cmd="ln -s $good_target $source -f"
echo "  $cmd"
$cmd
