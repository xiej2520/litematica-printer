# Minecraft 1.15.2 target

1.15.2 is one more preprocessor node below 1.18.2. It shares the normal sources;
upstream code paths for other versions are unchanged.

## Dependencies

Built against xiej2520's forks (`origin/1.15.2/xiej` for the MaSa mods). The rewritten
native `fabric/1.15.2-xiej` line is a different API and is not a supported target:

| Repository | Commit | Version |
| --- | --- | --- |
| malilib | `a60741e1526d78fe82c192e1783bfdf929e2c7ce` | `0.18.4+xiej.0` |
| litematica | `04e4ef77459058be85f305d7d81595b4d668e1f6` | `0.9.1-1.15.2-dev+xiej.9` |
| tweakeroo | `7a8dff7f4628723e2907f91ea4d622f2a9c748dc` | `0.20.999-sakura.13-1.15.2-xiej.8` |
| quickshulker (`origin/1.15`) | `f3205640deb7afa477c674410dcd1a2e08df49b5` | `1.4.0+xiej.2` |

The 1.15.2 target resolves `fi.dy.masa` and `net.kyrptonaught` only from Maven
local, so publish the forks there first (`publishToMavenLocal` in each fork).
`tools/build-1.15.2-dependencies.sh` does that for the commits above, building
them from the local checkouts (`masa/<mod>`, `../quickshulker`; `FORK_GRADLE` =
Gradle 8.14.4, `JAVA_HOME` = Java 21). Pass `-Dmaven.repo.local=<dir>` to both
to use another directory.

```sh
TARGET_MC_VERSIONS=1.15.2 ./gradlew :1.15.2:assemble
```

## How the port works

- **Java 8.** 1.15.2 compiles as Java 17, like 1.18.2.
  [JvmDowngrader](https://github.com/unimined/JvmDowngrader) rewrites the jar to
  Java 8 (records, `List.of`, `Optional.isEmpty`, …) before `remapJar`. Mixin
  configs use `JAVA_8`.
- **Missing Minecraft methods.** Calls such as `state.is(Block)`,
  `player.getInventory()` or `menu.getCarried()` stay as written.
  `compat/legacy/LegacyApi` declares them; Loom injects those interfaces into the
  1.15.2 classes at compile time (`versions/1.15.2/legacy-interfaces.json`), and the
  mixins in `mixin/printer/mc/legacy` add them at runtime. Method names are the
  1.18.2 ones, since 1.15.2 sources are preprocessed from 1.18.2.
- **Renames** go in `versions/mapping-1.15.2-1.18.2.txt` (for example
  `LayeredCauldronBlock`/`WATER_CAULDRON` → the single 1.15.2 `CauldronBlock`).
- **Everything else** is `//#if MC >= 11802` / `//#if MC < 11802` in place.
  Static factories that cannot be injected go through `Vec3Utils` and
  `ContainerSyncPlan.same`.
- **Container clicks.** 1.15.2 has no menu state IDs. `compat/legacy/LegacyMenuProtocol`
  sends the old click packet with a deliberately wrong expected stack, which makes
  the server resend the whole menu, and counts observed updates as revisions.

## Not available on 1.15.2

Blocks/features newer than 1.15 (amethyst, candles, crafter, cave vines, soul fire, …).

<ai>Litematica's pick-block "avoid damageable/tools" options are in the fork since `xiej.10`.
The printer honors them, because it picks slots through Litematica's `canPickToSlot`.</ai>

## Status

Compiles, downgrades and remaps; every class in the jar is Java 8 (major 52).
The other 16 targets still compile and the 26.1 test suite passes. The unit
tests do not compile for 1.15.2 (their fixtures construct newer Minecraft
classes), so the 1.15.2 target skips compiling and running them.

Checked headlessly on Java 8 with Fabric Loader 0.19.1 against the pinned forks,
Quick Shulker, ModMenu 1.10.7 and Fabric API 0.28.5: every mixin applies, every class
links, all bytecode references resolve, static initializers and mod initialization run,
and `LegacyApi` runs on real objects. Mod initialization ran on an unconstructed
`Minecraft` instance, and Minecraft's `main` was never entered.

JvmDowngrader's generated record `toString` calls `StringBuilder.append(byte)`, which
does not exist, so 1.15.2 records with `byte` components need an explicit `toString`.

The user reports it works in game (2026-10-31); which features were exercised was not
stated. Audit details: `masa/docs/ports/1.15.2/litematica-printer.md`. Watch especially: container sync (legacy click protocol), beacon unlock, mining speed,
pick-block slots. Tweakeroo's `DISABLE_BLOCK_BREAK_COOLDOWN` integration is off because
the pinned fork lacks that option.

<ai>After each `LegacyMenuProtocol` click the 1.15.2 server ignores further container clicks until
the client acknowledges the rejected one, about one round trip. The printer's transfers send one
click and wait for the reply. A click from another mod or the player in that window is dropped,
and the resent menu reverts it on the client. Not observed in game.

Checked headlessly on 2026-11-24 against Litematica `xiej.12` (the commit pinned above) with
Schematic Preview, Tech Utils and TweakerMore installed: same result as the check above.</ai>
