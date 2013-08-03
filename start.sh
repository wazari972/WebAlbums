#!/bin/sh

# configuration paths are relative, so jump to WA's root directory first
cd $(dirname $0)

exec java -Dfile.encoding=UTF-8 -jar ./dist/WebAlbums3-Bootstrap.jar $*
