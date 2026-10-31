#!/usr/bin/env bash
# Builds FxHubJava with the bundled Maven wrapper. Requires JDK 25+.
set -euo pipefail
cd "$(dirname "$0")"

if [[ -z "${JAVA_HOME:-}" && -x /usr/libexec/java_home ]]; then
  JAVA_HOME="$(/usr/libexec/java_home -v 25+ 2>/dev/null || true)"
  export JAVA_HOME
fi

./mvnw -B -T 1C clean install -Pproduction "$@"
