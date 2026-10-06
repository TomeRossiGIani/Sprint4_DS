#!/usr/bin/env bash
set -euo pipefail
curl --fail --request POST http://localhost:8080/api/meetings \
  --header 'Content-Type: application/json' \
  --data @examples/demo-request.json
