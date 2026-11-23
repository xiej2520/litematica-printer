#!/usr/bin/env bash
# Build existing sibling source objects without switching or editing their checkouts.
set -euo pipefail
printer_root=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")/.." && pwd)
masa_root=$(dirname -- "$printer_root")
: "${FORK_GRADLE:?Set FORK_GRADLE to a Gradle 9.9.2 executable (Loom 1.19 needs 9.8+)}"
: "${JAVA_HOME:?Set JAVA_HOME to Java 25 (Loom 1.19 needs it)}"
build_root=$(mktemp -d "$printer_root/build-forks-1.15.2-XXXXXXXX")
for mod in malilib litematica tweakeroo quickshulker; do
    repo=$masa_root/$mod
    case "$mod" in
        malilib) revision=33bd9383c3243a739bf6bce7ca56784c7f3c2a39 ;;
        litematica) revision=7b2d0fe7ff76e4120b96cc0929a5a29624868fc3 ;;
        tweakeroo) revision=0252dcaaedad53c7b11e993634ffd55430ff665e ;;
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
