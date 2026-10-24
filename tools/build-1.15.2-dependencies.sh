#!/usr/bin/env bash
# Build existing sibling source objects without switching or editing their checkouts.
set -euo pipefail
printer_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
masa_root=$(dirname -- "$printer_root")
: "${FORK_GRADLE:?Set FORK_GRADLE to a Gradle 8.14.4 executable}"
: "${JAVA_HOME:?Set JAVA_HOME to a JDK supported by Gradle 8.14.4 (e.g. Java 21)}"
build_root=$(mktemp -d "$printer_root/build-forks-1.15.2-XXXXXXXX")
for mod in malilib litematica tweakeroo quickshulker; do
    repo=$masa_root/$mod
    case "$mod" in
        malilib) revision=a60741e1526d78fe82c192e1783bfdf929e2c7ce ;;
        litematica) revision=6e953a0947dfbef92531f5f09e199d304845e6ca ;;
        tweakeroo) revision=7a8dff7f4628723e2907f91ea4d622f2a9c748dc ;;
        quickshulker) revision=f3205640deb7afa477c674410dcd1a2e08df49b5; repo=$(dirname -- "$masa_root")/quickshulker ;;
    esac
    mkdir -p -- "$build_root/$mod"
    git -C "$repo" archive "$revision" | tar -x -C "$build_root/$mod"
    "$FORK_GRADLE" --no-daemon --max-workers=2 \
        -Dorg.gradle.java.home="$JAVA_HOME" \
        '-Dorg.gradle.jvmargs=-Xmx3G -XX:ActiveProcessorCount=2' \
        -I "$printer_root/tools/fork-dependencies.init.gradle" \
        -p "$build_root/$mod" \
        build publishMavenJavaPublicationToMavenLocal
done
printf 'Published to Maven local\nDisposable source copies: %s\n' "$build_root"
