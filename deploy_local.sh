#!/usr/bin/env bash
# Runs the executable Spring Boot jar produced by build_local.sh.
# Configuration comes from environment variables (see .env.example); `docker compose up` is the
# alternative that also starts MySQL.
set -euo pipefail
cd "$(dirname "$0")"

if [[ -f .env ]]; then
  set -a; source .env; set +a
fi

# Run from a private copy: a rebuild rewriting webclient/target/fxmind.jar under a running JVM
# breaks lazy class loading (NoClassDefFoundError) and with it request handling such as login.
RUN_DIR="${TMPDIR:-/tmp}/fxmind-run"
mkdir -p "$RUN_DIR"
cp webclient/target/fxmind.jar "$RUN_DIR/fxmind.jar"

exec java -XX:+UseZGC -jar "$RUN_DIR/fxmind.jar" "$@"
