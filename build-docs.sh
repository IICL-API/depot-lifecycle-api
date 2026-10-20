#!/usr/bin/env sh
# Builds the Redoc API reference into _site/ for GitHub Pages.
#
# Usage: ./build-docs.sh          (requires Node.js/npm; downloads the redoc package via npm)
#
# The site self-hosts the Redoc bundle, so it does not depend on any externally
# hosted script. Rendering happens in the browser from the spec file; nothing is
# prerendered, so there is no server/client markup mismatch to hydrate.
set -eu
cd "$(dirname "$0")"

# Pinned so local and CI builds match. Redoc 3 is an ES module and must be loaded
# with <script type="module">; Redoc 2 options such as expandResponses no longer exist.
REDOC_VERSION=3.0.1

# Newest versioned spec in the repo root (e.g. depot-lifecycle-openapi-2.2.9.yaml).
SPEC=$(ls depot-lifecycle-openapi-*.yaml | sort -V | tail -n 1)

rm -rf _site
mkdir -p _site

# Fetch the redoc package and copy out the standalone browser bundle.
PKG_DIR=$(mktemp -d)
npm pack "redoc@${REDOC_VERSION}" --pack-destination "$PKG_DIR" --silent >/dev/null
tar -xzf "$PKG_DIR"/redoc-*.tgz -C "$PKG_DIR" package/bundles/redoc.standalone.js package/LICENSE
cp "$PKG_DIR/package/bundles/redoc.standalone.js" _site/redoc.standalone.js
cp "$PKG_DIR/package/LICENSE" _site/redoc.LICENSE
rm -rf "$PKG_DIR"

# Keep the raw spec(s) downloadable at the same URLs as before.
cp depot-lifecycle-openapi-*.yaml _site/

cat > _site/index.html <<HTML
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Depot Lifecycle | API Reference</title>
    <style>
      body { margin: 0; padding: 0; }
      /* Hide the "API docs by Redocly" promo link in the sidebar footer. */
      a[href="https://redocly.com/redoc/"] { display: none !important; }
    </style>
  </head>
  <body>
    <redoc spec-url="${SPEC}" layout="stacked" show-extensions="true" disable-telemetry="true"></redoc>
    <!-- Redoc ${REDOC_VERSION}, self-hosted; see redoc.LICENSE -->
    <script type="module" src="redoc.standalone.js"></script>
  </body>
</html>
HTML

echo "Built ${SPEC} with redoc ${REDOC_VERSION} -> _site/"
