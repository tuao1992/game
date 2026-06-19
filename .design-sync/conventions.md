# Weldrite CPVC Master — design system conventions

A blue/white/red plumber-workshop UI kit that mirrors the *Weldrite CPVC
Master* mobile game. Build game-style menus, HUDs, settings and result screens
from these real components.

## Setup

- **Load the stylesheet.** All visuals come from `styles.css` (it pulls in the
  design tokens and the component rules). Nothing renders styled without it.
- **No provider/context is required** — components are plain and prop-driven.
- **Design on a dark surface.** The kit is built for the game's dark blue
  background (`--wr-bg`, `#0e2a47`); many components use white/light-blue text
  (`Logo`, `Tagline`, `RankBadge` titles, `Gauge`, `Toggle` labels). Put them on
  `<WorkshopBackground>` or any `--wr-bg`/`--wr-panel` surface — never light-on-
  light.

## Styling idiom — tokens + props, not utility classes

Style components through their **props**, and style your own layout glue with the
**`--wr-*` CSS custom properties** — never hardcode hex values.

- **Color tokens:** `--wr-blue`, `--wr-blue-dark`, `--wr-blue-light`,
  `--wr-white`, `--wr-offwhite`, `--wr-red`, `--wr-red-dark`, `--wr-green`,
  `--wr-amber`, `--wr-ink`, `--wr-panel`, `--wr-muted`, plus surfaces `--wr-bg`,
  `--wr-bg-top`.
- **Rank colors:** `--wr-rank-apprentice|junior|technician|senior|expert|master`.
- **Chapter accents:** `--wr-env-kitchen|bathroom|house|apartment|commercial|factory`
  (pass to `<WorkshopBackground accent>`).
- **Type:** `--wr-font` (heavy sans; the brand uses an Inter/Roboto-style
  geometric sans), weights `--wr-weight-regular` / `--wr-weight-bold`.
- **Radius:** `--wr-radius-sm|md|lg|pill`. **Space:** `--wr-space-1..6`.
  **Elevation:** `--wr-shadow` (the signature chunky bottom shadow), `--wr-shadow-soft`.

Component prop vocabulary (do not invent class names — there is no utility-class
system):

- `Button` — `variant` `primary|secondary|danger|success|ghost`, `size`
  `sm|md|lg`, `fullWidth`.
- `Panel` — `tone` `panel|blue|white|ink|glass`, `elevated`.
- `Chip` — `tone` `panel|blue|amber|red|green|ghost`, `icon`.
- `ProgressBar` / `Gauge` — `value` 0..1, `tone`.
- `StarRating` — `value`, `max` (default 3).
- `RankBadge` — `rank` (the six ranks above), `showLabel`.
- `Toggle` — `checked` / `onChange`. `Tagline` — `variant` `primary|secondary`.

## Where the truth lives

Read the bound `styles.css` (and the `_ds_bundle.css` it imports) for the exact
tokens and component rules, and each component's `.d.ts` / `.prompt.md` for its
full prop contract before composing.

## One idiomatic example

```tsx
import { WorkshopBackground, Logo, Tagline, Button } from '@weldrite/design-system';

export function MainMenu() {
  return (
    <WorkshopBackground minHeight={520}>
      <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 'var(--wr-space-5)' }}>
        <Logo size={72} />
        <Tagline variant="primary" />
        <Button variant="primary" size="lg" fullWidth>PLAY</Button>
        <Button variant="ghost">Settings</Button>
      </div>
    </WorkshopBackground>
  );
}
```
