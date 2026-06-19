# Weldrite CPVC Master — Web Design System

A React + TypeScript component library that recreates the **Weldrite CPVC
Master** Android game's UI for the web, so designs built in
[Claude Design](https://claude.ai/design) use the game's real, on-brand parts.

The visual language is lifted directly from the game:

- **Palette** — `app/src/main/java/com/weldrite/cpvcmaster/ui/Widgets.kt` (`Palette`)
- **Brand props / badges** — `.../gfx/Decor.kt` (logo, cement can, rank shields, stars)
- **Ranks & chapter accents** — `.../data/Models.kt`

## Components

`Button` · `Toggle` · `Panel` · `Chip` · `ProgressBar` · `StarRating` ·
`RankBadge` · `Gauge` · `Logo` · `CementCan` · `Tagline` · `WorkshopBackground`

Tokens (`--wr-*` CSS custom properties) and component styles ship in
`src/tokens.css` + `src/styles.css`; the build concatenates them into
`dist/weldrite.css`.

## Build

```sh
npm --prefix design-system install
npm --prefix design-system run build   # → dist/index.es.js, dist/weldrite.css, dist/**/*.d.ts
```

`dist/` and `node_modules/` are gitignored and rebuilt from source.

## Finishing the Claude Design sync (the upload step)

The design system and an upload-ready bundle were prepared with the
`/design-sync` skill. The upload itself needs the **Claude Design** tool
(`DesignSync`), which was **not available in the build environment**. To finish
it, run from a session that has Claude Design connected — local **Claude Code**
or **claude.ai/code**:

```
/design-login        # if prompted to authenticate
/design-sync
```

`/design-sync` reads the committed `.design-sync/config.json` (package shape,
`@weldrite/design-system`, global `WeldriteDS`), rebuilds the bundle, re-verifies
the committed previews, creates a new Claude Design project, and uploads. There
is no `projectId` yet — the first run creates the project and records it.

Everything the sync reuses is committed: `.design-sync/config.json`,
`.design-sync/conventions.md` (the design-agent guidance), `.design-sync/NOTES.md`
(re-sync gotchas), and `.design-sync/previews/*.tsx` (the verified preview
stories).

## Usage

```tsx
import { WorkshopBackground, Logo, Tagline, Button } from '@weldrite/design-system';
import '@weldrite/design-system/styles.css';

export const MainMenu = () => (
  <WorkshopBackground minHeight={520}>
    <Logo size={72} />
    <Tagline variant="primary" />
    <Button variant="primary" size="lg" fullWidth>PLAY</Button>
  </WorkshopBackground>
);
```
