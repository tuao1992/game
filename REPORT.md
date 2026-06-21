# Age of Empires IV — Civilizations, Best Landmarks & Units (Deep-Research Report)

**Prepared:** June 2026 · **Game state:** Patch 16.2 (16.2.10884, 18 Jun 2026) · Season 13 (*Yue Fei's Legacy*)
**Scope:** Every AoE4 civilization, its identity, best landmarks, best units, build/win-condition, and current competitive standing — plus the data sources used and an offline Android companion app (the `:aoe4` module in this repo) that surfaces all of it per‑civ.

> **How this was produced.** Six parallel web-research passes (roster/identities, landmarks, units, tier-list/meta, build orders/data-sources, and an adversarial roster-verification pass) over authoritative and community sources, then cross-checked. Claims that sources disagreed on are flagged. Tier and win-rate figures are time-sensitive; everything is tagged to the June 2026 patch.

---

## TL;DR — key findings

- **There are 23 civilizations, not 16.** This was verified two independent ways: the [aoe4world civ explorer](https://aoe4world.com/explorer/civs) enumerates 23, and the per-DLC breakdown on the Fandom wiki sums to 23. **Ship/learn the full 23.**
- **Most "new" civs are from 2025, not 2026.** Only **Jin Dynasty** (May 2026) is brand new. Six others arrived in 2025 via *Knights of Cross and Rose* (Apr 2025) and *Dynasties of the East* (Nov 2025). The "8 base + Sultans Ascend = 16" picture is the late‑2023 snapshot.
- **14 of the 23 are "variant" civs** — reworked versions of a parent civ (e.g. *Jeanne d'Arc* → French, *Golden Horde* → Mongols). Variants play very differently from their parents.
- **Top of the meta (Conqueror, patch 16.2):** Mongols, English, Zhu Xi's Legacy, Tughlaq Dynasty. **Bottom:** Ottomans and the heavily‑nerfed Sengoku Daimyo.
- **Best beginner civs:** **English** and **French** (both "Easy"), with **Rus** close behind. Macedonian Dynasty and Golden Horde are forgiving at low ELO.
- **Best free data source for an app:** the **aoe4world** open data CDN + REST API (units, landmarks, civ stats, win rates) — free, non‑commercial.
- A **next expansion, *Raiders of the North* (Vikings + Scots), is announced for fall 2026** but not yet released, so it does not change today's count of 23.

---

## 1. The roster & DLC timeline

| Release | Date | Civilizations added | Running total |
|---|---|---|---|
| Base game | Oct 2021 | English, French, Holy Roman Empire, Mongols, Rus, Delhi Sultanate, Chinese, Abbasid Dynasty | 8 |
| Free expansion | Oct 2022 | Ottomans, Malians | 10 |
| **The Sultans Ascend** | Nov 2023 | Byzantines, Japanese *(new)* + Ayyubids, Jeanne d'Arc, Order of the Dragon, Zhu Xi's Legacy *(variants)* | 16 |
| **Knights of Cross and Rose** | Apr 2025 | Knights Templar *(French)*, House of Lancaster *(English)* | 18 |
| **Dynasties of the East** | Nov 2025 | Golden Horde *(Mongols)*, Macedonian Dynasty *(Byzantines)*, Sengoku Daimyo *(Japanese)*, Tughlaq Dynasty *(Delhi)* | 22 |
| **Yue Fei's Legacy** | May 2026 | Jin Dynasty *(standalone Jurchen civ)* | **23** |

**Variant → parent map** (all high‑confidence): Ayyubids→Abbasid · Jeanne d'Arc→French · Order of the Dragon→HRE · Zhu Xi's Legacy→Chinese · Knights Templar→French · House of Lancaster→English · Golden Horde→Mongols · Macedonian Dynasty→Byzantines · Sengoku Daimyo→Japanese · Tughlaq Dynasty→Delhi Sultanate.

> **Flag:** *Jin Dynasty* is sometimes described as a Chinese variant, but the official kit (Mounted Villagers, Emissaries/Tributary States, Meng'an Mouke Keeps) is bespoke and most sources treat it as a **standalone** civ. Either way it does not change the count.

---

## 2. Meta snapshot (June 2026)

- **Season 13** launched **7 May 2026** with the *Yue Fei's Legacy* DLC (added the Jin Dynasty + campaign + maps).
- **Main balance patch:** 16.2.10604 (1 Jun 2026). **Latest patch:** 16.2.10884 (18 Jun 2026) — QoL/bugfixes only, no balance changes.
- Stats below are the 1v1 ranked snapshot for this patch from [aoe4world](https://aoe4world.com/stats/rm_solo/civilizations).

**The key insight:** the ranking changes a lot by skill bracket. Several civs that look dominant ladder‑wide are merely *forgiving* and flatten out at the top, so this report ranks by **Conqueror** (top bracket) for "best at high level," and notes the divergence.

---

## 3. Tier list (1v1, Conqueror, patch 16.2)

| Tier | Civilizations |
|---|---|
| **S** | Mongols · English · Zhu Xi's Legacy · Tughlaq Dynasty |
| **A** | Jeanne d'Arc · Japanese · Knights Templar · House of Lancaster · Malians · French |
| **B** | Macedonian Dynasty · Order of the Dragon · Rus · Abbasid Dynasty |
| **C** | Holy Roman Empire · Byzantines · Ayyubids · Jin Dynasty · Golden Horde\* · Chinese · Delhi Sultanate |
| **D** | Ottomans · Sengoku Daimyo |

\*Golden Horde is B/A‑ish at low ELO but C at Conqueror.

**Confidence:** *Medium.* These tiers are derived primarily from Conqueror win rates plus recent patch direction, because no current expert/pro tier list was machine‑readable at research time (the most-cited pro list, Jeando's, blocked automated access). Treat ±1 tier as plausible, and the **newest variant civs as lower-confidence** (mainstream tier articles still omit them).

---

## 4. Win & pick rates (1v1)

### Conqueror (top bracket — "high-level")

| Civ | Win % | Pick % |  | Civ | Win % | Pick % |
|---|---|---|---|---|---|---|
| Mongols | 52.5 | 4.7 | | Macedonian Dynasty | 50.2 | 5.3 |
| English | 52.4 | 6.3 | | Order of the Dragon | 50.0 | 4.6 |
| Tughlaq Dynasty | 52.4 | 2.4 | | Rus | 49.5 | 4.3 |
| Zhu Xi's Legacy | 52.4 | 4.7 | | Abbasid Dynasty | 49.2 | 5.6 |
| Jeanne d'Arc | 52.0 | 2.2 | | Holy Roman Empire | 48.6 | 2.6 |
| House of Lancaster | 51.4 | 4.1 | | Byzantines | 48.5 | 5.7 |
| Japanese | 51.4 | 4.0 | | Ayyubids | 47.9 | 3.2 |
| Knights Templar | 51.2 | 8.5 | | Jin Dynasty | 47.7 | 3.5 |
| Malians | 50.9 | 3.6 | | Golden Horde | 47.6 | 4.5 |
| French | 50.7 | 10.2 | | Chinese | 47.5 | 3.5 |
| | | | | Delhi Sultanate | 47.5 | 2.3 |
| | | | | Ottomans | 45.1 | 3.0 |
| | | | | Sengoku Daimyo | 42.5 | 1.2 |

*Source: [aoe4world Solo Ranked civ stats](https://aoe4world.com/stats/rm_solo/civilizations), Conqueror, ~14,955 games, June 2026.*

**Low‑ELO divergence:** ladder-wide (all ranks, ~253k games), the leaders are **Macedonian Dynasty (54.9%), Knights Templar (54.3%) and Golden Horde (53.5%)** — i.e. forgiving, beginner-friendly civs whose edge shrinks at Conqueror.

---

## 5. Per‑civ cheat sheet (best landmarks · signature units · win condition)

Landmark column = **best pick** at Feudal / Castle / Imperial. Full rationale, alternatives and build orders are in the app's detail screen and in [`CivData.kt`](aoe4/src/main/java/com/aoe4/advisor/data/CivData.kt).

| Civ (Tier) | Feudal | Castle | Imperial | Signature units | Win condition |
|---|---|---|---|---|---|
| **English** (S) | Council Hall | White Tower | Wynguard Palace | Longbowman, Spearman | Dual‑TC eco + massed longbows behind defenses |
| **French** (A) | School of Cavalry | Royal Institute | Red Palace | Royal Knight, Arbalétrier | Early knight pressure + discount‑snowball eco |
| **Holy Roman Empire** (C) | Aachen Chapel | Regnitz Cathedral | Elzbach Palace | Landsknecht, Prelate, MAA | Prelate/relic eco into tanky infantry |
| **Mongols** (S) | Silver Tree | Kurultai / Steppe Redoubt | White Stupa | Mangudai, Khan | Mobility, raiding, relocate-and-harass |
| **Rus** (B) | Golden Gate | Abbey of the Trinity | High Armory | Streltsy, Warrior Monk | Hunt‑bounty boom into cavalry + siege |
| **Delhi Sultanate** (C) | Dome of the Faith | House of Learning | Palace of the Sultan | War Elephant, Scholar | Free‑tech snowball + elephant deathball |
| **Chinese** (C) | Barbican of the Sun | Astronomical Clocktower | Spirit Way | Zhuge Nu, Nest of Bees, Grenadier | Dynasty/tax eco into gunpowder + siege |
| **Abbasid Dynasty** (B) | Economic Wing | Culture Wing | (final wing) | Camel Archer, Ghulam | Camels neutralize cavalry + Golden‑Age eco |
| **Ottomans** (D) | Twin Minaret Medrese | Istanbul Imperial Palace | Sea Gate Castle | Sipahi, Mehter, Janissary | Free Military‑School production + siege |
| **Malians** (A) | Saharan Trade Network | Farimba Garrison | Grand Fulani Keep | Sofa, Musofadi, Javelin Thrower | Gold/cattle eco + raids, avoid pitched fights |
| **Japanese** (A) | Koka Township | Floating Gate | Castle of the Crow | Mounted Samurai, Yumi, Shinobi | Buffed buildings/eco + Shinobi disruption |
| **Byzantines** (C) | Grand Winery | Imperial Hippodrome | Foreign Engineering Co. | Cataphract, Varangian Guard, Cheirosiphon | Cistern eco + mercenaries adapt to anything |
| **Ayyubids** (C) | Culture→Advancement | Military→Reinforcements | Trade→Bazaar | Desert Raider, Ghulam | Wing‑choice flexibility + camels + siege |
| **Jeanne d'Arc** (A) | School of Cavalry | Royal Institute | Red Palace | Jeanne (hero), Companions | Level the hero through fights and snowball |
| **Order of the Dragon** (B) | Aachen Chapel | Regnitz Cathedral | Elzbach Palace | Gilded Landsknecht/Knight/MAA | Win fights with elite Gilded units |
| **Zhu Xi's Legacy** (S) | Meditation Gardens | Mount Lu Academy | Zhu Xi's Library | Palace Guard, Imperial Guard, Zhuge Nu | Supervised production + tech spikes (great on water) |
| **Knights Templar** (A) | Fortress + ally | 2nd ally | 3rd ally | Templar Brother, alliance units | Pilgrim gold + Commanderie bonuses + fortresses |
| **House of Lancaster** (A) | keep landmark | A House Unified | Berkshire Palace | Yeoman, Earl's Guard, Demilancer | Keep‑buffed units + Yeoman AoE volleys |
| **Golden Horde** (C) | Golden Tent | Golden Tent | Golden Tent | Torguud, Horse Archer | Batch cavalry + Outpost‑edict map control |
| **Macedonian Dynasty** (B) | Varangian landmark | Varangian Stronghold | Golden Horn Tower | Varangian Guard, Crossbowman | Silver eco + unit replacement, out‑sustain |
| **Sengoku Daimyo** (D) | clan landmark | clan military | gunpowder landmark | Yari Cavalry, Tanegashima | Specialize a clan (Hojo/Oda/Takeda) — currently weak |
| **Tughlaq Dynasty** (S) | Tower of Victory | House of Learning | Palace of the Sultan | Ballista/War Elephant | Governor‑fort bonuses + free tech → elephants |
| **Jin Dynasty** (C) | eco/keep landmark | military landmark | siege landmark | Iron Pagoda, Mounted Grenadier, Eruptor | Mobile raid eco into gunpowder‑siege storm |

> Where a landmark name is generic ("keep landmark", "ally") the public sources did not give a single clean competitive name for that civ/age (notably the newest variants and some Ottoman/Malian Imperial picks). These are flagged in `CivData.kt` and should be refined against the live aoe4world explorer per patch.

---

## 6. The Android app (`:aoe4` module)

A native **Kotlin + Jetpack Compose** companion app, added as a new Gradle module beside the existing game. It is **fully offline** — the dataset above is bundled as type‑safe Kotlin (no network, no ads, no tracking).

**Screens**
- **Civilizations** — searchable, filterable (by tier / beginner) list of all 23 civs.
- **Civ detail** — overview, signature bonuses, **best landmarks per age (with why + alternative)**, **best units** (recommended highlighted) + core army comp, build order & win condition, plus tier/difficulty/win‑rate badges.
- **Tier list** — all civs grouped S→D, tap to jump to detail.

**Build:** `./gradlew :aoe4:assembleDebug` (release: `:aoe4:assembleRelease`). Min SDK 29, target/compile SDK 34. See the repo README for details.

---

## 7. Data sources to keep an app updated

| Source | What it offers | Free? / License |
|---|---|---|
| [aoe4world **data** CDN](https://data.aoe4world.com/) + [GitHub `aoe4world/data`](https://github.com/aoe4world/data) | Structured JSON for **all units, landmarks, techs** per civ (costs, stats, ages) | Free, **non‑commercial** (MS Game Content rules) |
| [aoe4world **API**](https://aoe4world.com/api) | Live **civ win/pick rates, matchups**, leaderboards, matches; bracket/patch filters | Free, no key for public data |
| [aoe4guides API (`jensbuehl/aoe4-guides-api`)](https://github.com/jensbuehl/aoe4-guides-api) | Community **build orders** by civ/strategy | **MIT** code (content under MS rules) |
| [Age of Empires Series Wiki (Fandom)](https://ageofempires.fandom.com/wiki/Age_of_Empires_IV) | Human‑readable civ/unit/landmark pages | **CC BY‑SA** (attribute) |
| [Official ageofempires.com](https://www.ageofempires.com/) | Canonical civ overviews + patch notes | Reference only (no public API) |

Recommended pipeline: **aoe4world data** for the static "best landmarks/units" content, **aoe4world API** stats for the "best/meta" rankings, **aoe4guides** for build orders, Fandom for flavor text (with attribution). Keep usage **non‑commercial** per Microsoft's Game Content Usage Rules.

---

## 8. Confidence & caveats (adversarial notes)

- **Roster count (23): HIGH confidence** — two independent enumerations agree. Web search auto‑summaries that say "14" or "16" are stale; do not trust them.
- **Win rates: HIGH confidence** (single authoritative source, internally consistent across brackets), but a **~3‑week‑old snapshot**; a new patch can shift them quickly.
- **Tier boundaries: MEDIUM** — derived from win rates, not a parsed pro list. **Newest variant tiers: LOW** (sparse public data).
- **Landmark/unit picks for the 12 classic civs: solid** and stable across seasons. **For the 7 newest civs and a few Imperial picks: lower** — verify on the live aoe4world explorer per patch.
- **Difficulty stars** are well-sourced for base/free civs; for Sultans‑Ascend+ civs the official pages don't show stars, so those are best‑judgment.
- This report and app are an **unofficial fan resource**; AoE4 is © Microsoft / World's Edge. Content is for non‑commercial use.

---

## 9. Sources

**Roster, civs & mechanics**
- aoe4world — [Civilizations explorer](https://aoe4world.com/explorer/civs)
- Official — [The Sultans Ascend variant deep dive](https://www.ageofempires.com/news/the-sultans-ascend-variant-civilizations-deep-dive/) · [Byzantines](https://www.ageofempires.com/games/age-of-empires-iv/civilizations/byzantines/) · [Japanese](https://www.ageofempires.com/games/age-of-empires-iv/civilizations/japanese/)
- Fandom — [Civilization (AoE IV)](https://ageofempires.fandom.com/wiki/Civilization_(Age_of_Empires_IV)) · [Yue Fei's Legacy](https://ageofempires.fandom.com/wiki/Age_of_Empires_IV:_Yue_Fei's_Legacy) · [Golden Horde](https://ageofempires.fandom.com/wiki/Golden_Horde) · [Macedonian Dynasty](https://ageofempires.fandom.com/wiki/Macedonian_Dynasty) · [Knights Templar](https://ageofempires.fandom.com/wiki/Knights_Templar) · [House of Lancaster](https://ageofempires.fandom.com/wiki/House_of_Lancaster) · [Sengoku Daimyo](https://ageofempires.fandom.com/wiki/Sengoku_Daimyo) · [Tughlaq Dynasty](https://ageofempires.fandom.com/wiki/Tughlaq_Dynasty)
- [PCGamesN — civilizations guide](https://www.pcgamesn.com/age-of-empires-4/civilizations) · [Dynasties of the East](https://www.pcgamesn.com/age-of-empires-4/dlc-dynasties-of-the-east) · [Yue Fei's Legacy launch](https://www.pcgamesn.com/age-of-empires-4/dlc-yue-feis-launch)
- [GameWatcher — Knights of Cross and Rose](https://www.gamewatcher.com/news/age-of-empires-4-knights-of-cross-and-rose-release-date)
- [Pro Game Guides — civ bonuses & difficulty](https://progameguides.com/age-of-empires/all-civilization-bonuses-strengths-and-difficulty-levels-in-age-of-empires-iv/)

**Landmarks & units**
- ScreenRant best-landmarks guides: [English](https://screenrant.com/age-empires-aoe4-english-civilization-landmarks-build-guide/) · [French](https://screenrant.com/age-empires-4-best-french-landmarks-choice-guide/) · [HRE](https://screenrant.com/age-empires-4-best-holy-roman-empire-landmarks-guide/) · [Rus](https://screenrant.com/age-empires-4-best-rus-civilization-landmarks-build-ages/) · [Delhi](https://screenrant.com/age-empires-4-best-delhi-sultanate-landmarks-guide/) · [Chinese](https://screenrant.com/age-empires-4-best-chinese-civilization-landmarks/) · [Mongols](https://screenrant.com/age-empires-4-best-mongol-civilization-landmarks/)
- [GameRant — landmarks guide](https://gamerant.com/age-of-empires-4-landmarks-guide/) · [best units](https://gamerant.com/age-of-empires-4-best-units/)
- [DiamondLobby — counter units](https://diamondlobby.com/age-of-empires-4/counter-units-aoe4/) · [AOEDB counters](https://aoedb.net/aoe4/counters/)

**Meta, tiers & patches**
- [aoe4world — Solo Ranked civilization stats](https://aoe4world.com/stats/rm_solo/civilizations) *(primary win/pick-rate data)*
- Official patches — [Update 16.1.9737 / Season 13 / Yue Fei's Legacy](https://www.ageofempires.com/news/age-of-empires-iv-update-16-1-9737-and-yue-feis-legacy-dlc-release-preview/) · [Patch 16.2.10884 (18 Jun 2026)](https://www.ageofempires.com/news/age-of-empires-iv-patch-16-2-10884/)
- [XP Gained — Patch 16.2 notes](https://xpgained.co.uk/patch-notes/age-of-empires-iv-patch-16-2-notes-june-2026)
- [Gematsu — Raiders of the North announced (Vikings + Scots, fall 2026)](https://www.gematsu.com/2026/06/age-of-empires-iv-anniversary-edition-expansion-raiders-of-the-north-announced)

**Data sources for apps**
- [aoe4world/data (GitHub)](https://github.com/aoe4world/data) · [data CDN](https://data.aoe4world.com/) · [aoe4world API](https://aoe4world.com/api) · [aoe4guides API (MIT)](https://github.com/jensbuehl/aoe4-guides-api)

*Tier-list cross-references (noted as dated/secondary): [omggamer (Nov 2025)](https://blog.omggamer.com/age-of-empires-4-tier-list/), [GamerDiscovery (Sept 2024, obsolete)](https://gamerdiscovery.com/age-of-empires-4-civilizations-tier-list/), [The GWW 2026](https://thegww.com/age-of-empires-iv-best-civilizations-ranked-for-2026-which-faction-should-you-play/).*
