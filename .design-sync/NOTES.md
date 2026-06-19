# design-sync notes — Weldrite CPVC Master DS

- **What this DS is.** A web (React + TS) recreation of the *Android game's* UI,
  under `design-system/`. The source of truth for look-and-feel is the Kotlin
  game: palette in `app/.../ui/Widgets.kt` (`Palette`), brand props/badges in
  `app/.../gfx/Decor.kt`, ranks/environments in `app/.../data/Models.kt`. If the
  game's visual identity changes, update the DS to match.
- **Build:** `npm --prefix design-system run build` (cfg.buildCmd). Emits
  `design-system/dist/index.es.js` (ESM bundle), `dist/weldrite.css` (combined
  tokens+components), and `dist/**/*.d.ts`. `dist/` and `node_modules/` are
  gitignored — run `npm --prefix design-system install` then the build on a
  fresh clone before the converter.
- **Converter invocation:** `--entry ./design-system/dist/index.es.js`
  `--node-modules ./design-system/node_modules` (react/react-dom/@types/react
  live there). In the DS's own repo `node_modules/@weldrite/design-system`
  doesn't exist, hence `--entry`.
- **Tokens ship inside cssEntry on purpose.** `lib/css.mjs copyTokens` only
  pulls tokens from an *installed package*, which a self-repo DS doesn't have.
  So `build.mjs` concatenates `src/tokens.css` + `src/styles.css` into
  `dist/weldrite.css` and `cfg.cssEntry` points at it; the `:root` token block
  rides in `_ds_bundle.css`, inside the styles.css closure. Do NOT set
  `tokensGlob`/`tokensPkg` — there's no token package to point them at.
- **Fonts:** system stack only (`--wr-font`), no `@font-face`, so renders
  offline. Brand intent is a heavy geometric/neo-grotesque sans (Inter/Roboto
  family); the host app may swap `--wr-font`. If validate prints `[FONT_MISSING]`
  for the system names, set `cfg.runtimeFontPrefixes` rather than shipping fonts.
- **No `projectId` yet.** The build session had no `DesignSync` tool (Claude
  Design not provisioned in the remote web env), so no project was created. The
  FIRST upload must run from a `DesignSync`-capable session (local Claude Code
  with `/design-login`, or claude.ai/code): it creates a new project (empty →
  incremental path) and records `projectId` here.

## Re-sync risks (what can silently go stale)

- The DS is a hand-built mirror of the game, not generated from it — there is no
  automated link, so game UI changes won't propagate until someone edits
  `design-system/src`. Treat the Kotlin sources above as the spec.
- Previews import realistic copy inlined in `.design-sync/previews/*.tsx`; if
  component props change, the previews need matching edits.
- Tokens-in-cssEntry means an empty `tokens/` dir in the bundle — expected, not
  a miss. `[CSS_RUNTIME]`/empty-tokens warnings here are benign.
