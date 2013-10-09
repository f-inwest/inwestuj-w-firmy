#!/bin/sh
echo "Building HTML pages from templates using M4..."
LANG_LOCALE=pl-pl
LANG_FILE=lang-${LANG_LOCALE}.m4
echo "Using locale=${LANG_LOCALE} langfile=${LANG_FILE}"
PAGES=`find . -name '*page.m4'|sed 's/^[^a-zA-Z0-9_-]*//;s/[.]m4$//'`
for i in $PAGES; do m4 -Dlangfile=${LANG_FILE} ${i}.m4 > ../${i}.html; done
echo "... done!"

