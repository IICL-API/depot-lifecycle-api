#!/usr/bin/env sh
# Builds the Redoc API reference into _site/ for GitHub Pages.
#
# Usage: ./build-docs.sh          (requires Node.js/npm; downloads the redoc package via npm)
#
# The site self-hosts the Redoc bundle, so it does not depend on any externally
# hosted script. Rendering happens in the browser from the spec file.
set -eu
cd "$(dirname "$0")"

# Pinned so local and CI builds match. Stay on the Redoc 2.x line: Redoc 3 is a
# different renderer (per-page routing, collapsed responses, no pathInMiddlePanel /
# expandResponses options). Redoc 2 sends no telemetry.
REDOC_VERSION=2.5.3

# Newest versioned spec in the repo root (e.g. depot-lifecycle-openapi-2.2.9.yaml).
SPEC=$(ls depot-lifecycle-openapi-*.yaml | sort -V | tail -n 1)

rm -rf _site
mkdir -p _site

# Fetch the redoc package and copy out the standalone browser bundle and licenses.
PKG_DIR=$(mktemp -d)
npm pack "redoc@${REDOC_VERSION}" --pack-destination "$PKG_DIR" --silent >/dev/null
tar -xzf "$PKG_DIR"/redoc-*.tgz -C "$PKG_DIR" \
  package/bundles/redoc.standalone.js \
  package/bundles/redoc.standalone.js.LICENSE.txt \
  package/LICENSE
cp "$PKG_DIR/package/bundles/redoc.standalone.js" _site/redoc.standalone.js
cp "$PKG_DIR/package/bundles/redoc.standalone.js.LICENSE.txt" _site/redoc.standalone.js.LICENSE.txt
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
    <link href="https://fonts.googleapis.com/css?family=Montserrat:300,400,700|Roboto:300,400,700" rel="stylesheet" />
    <style>
      body { margin: 0; padding: 0; }
      /* Hide the "API docs by Redocly" promo link in the sidebar footer. */
      a[href="https://redocly.com/redoc/"] { display: none !important; }
    </style>
  </head>
  <body>
    <div id="redoc"></div>
    <!-- Redoc ${REDOC_VERSION}, self-hosted; see redoc.LICENSE and redoc.standalone.js.LICENSE.txt -->
    <script src="redoc.standalone.js"></script>
    <script>
      Redoc.init(
        "${SPEC}",
        {
          pathInMiddlePanel: true,
          expandResponses: "all",
          showObjectSchemaExamples: true,
          showExtensions: true
        },
        document.getElementById("redoc"),
        function () {
          // Remove the promo link entirely so its logo is not fetched from redocly's CDN.
          try {
            document.querySelectorAll('a[href="https://redocly.com/redoc/"]').forEach(function (a) { a.remove(); });
          } catch (e) { /* cosmetic only */ }
        }
      );
    </script>
  </body>
</html>
HTML

echo "Built ${SPEC} with redoc ${REDOC_VERSION} -> _site/"
