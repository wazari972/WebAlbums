#! /bin/bash

set -e

TOMEE_USERS=$1

cat > $1 <<EOF
admin=${WA_ADMIN_PASSWORD}
viewer=${WA_VIEWER_PASSWORD}
EOF
