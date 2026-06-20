package com.aoe4.advisor.data

import com.aoe4.advisor.model.Age
import com.aoe4.advisor.model.ArmyUnit
import com.aoe4.advisor.model.BuildOrder
import com.aoe4.advisor.model.Civ
import com.aoe4.advisor.model.Dlc
import com.aoe4.advisor.model.Difficulty
import com.aoe4.advisor.model.LandmarkPick
import com.aoe4.advisor.model.Tier
import com.aoe4.advisor.model.UnitRole
import com.aoe4.advisor.model.UnitRole.CAVALRY
import com.aoe4.advisor.model.UnitRole.ECONOMY
import com.aoe4.advisor.model.UnitRole.GUNPOWDER
import com.aoe4.advisor.model.UnitRole.HERO
import com.aoe4.advisor.model.UnitRole.INFANTRY
import com.aoe4.advisor.model.UnitRole.NAVAL
import com.aoe4.advisor.model.UnitRole.RANGED
import com.aoe4.advisor.model.UnitRole.RELIGIOUS
import com.aoe4.advisor.model.UnitRole.SIEGE

/**
 * The bundled AoE4 civilization dataset (all 23 civilizations, June 2026 meta).
 *
 * Sources, confidence and known uncertainties are documented in REPORT.md at the repo
 * root. Tier and win-rate figures reflect aoe4world Conqueror stats for patch 16.2
 * (June 2026); landmark/unit recommendations are stable competitive picks. Tiers for the
 * newest variant civs are lower-confidence (sparse public data) — see the report.
 */
object CivData {

    // Small builders keep the data block readable.
    private fun lm(age: Age, best: String, alternative: String?, why: String) =
        LandmarkPick(age, best, alternative, why)

    private fun u(name: String, role: UnitRole, unique: Boolean, recommended: Boolean, note: String) =
        ArmyUnit(name, role, unique, recommended, note)

    val civs: List<Civ> = listOf(

        // ───────────────────────────── BASE GAME (2021) ─────────────────────────────

        Civ(
            id = "english",
            name = "English",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.S,
            difficulty = Difficulty.EASY,
            beginnerFriendly = true,
            accent = 0xFFB23A3A,
            tagline = "Defensive economy and massed longbows",
            overview = "England rewards a safe, economic style — a strong food economy and the " +
                "longest-ranged archer in the game backed by defensive structures. The easiest " +
                "civ to learn and a top pick at every level.",
            winRateConqueror = 52.4,
            pickRateConqueror = 6.3,
            agingMechanic = null,
            bonuses = listOf(
                "Longbowmen out-range generic archers and can plant defensive Palings (stakes) against melee and cavalry.",
                "Town Centers, Towers and Keeps fire extra arrows; the Network of Castles buffs attack speed when enemies are near.",
                "Farms are cheaper, giving a strong, low-micro food economy.",
                "Abbey of Kings can revive the King and heal nearby units."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Council Hall", "Abbey of Kings",
                    "Trains Longbowmen roughly twice as fast — the engine of England's archer-heavy game."),
                lm(Age.CASTLE, "White Tower", "King's Palace",
                    "A powerful defensive keep that anchors the Network of Castles; King's Palace is the eco / extra-TC pick."),
                lm(Age.IMPERIAL, "Wynguard Palace", "Berkshire Palace",
                    "Produces the free composite 'Wynguard Army', offsetting England's weaker late-game gold.")
            ),
            units = listOf(
                u("Longbowman", RANGED, unique = true, recommended = true,
                    "Cheap, long-ranged archer; near friendly buildings gains a big attack-speed/range boost. The backbone of the army."),
                u("Spearman", INFANTRY, unique = false, recommended = true,
                    "Protects Longbows from cavalry — the classic spear screen."),
                u("Springald", SIEGE, unique = false, recommended = true,
                    "Anti-siege and anti-large; standard part of the late-game push."),
                u("Wynguard Footmen / Rangers", INFANTRY, unique = true, recommended = false,
                    "Elite composite army spawned by the Wynguard Palace landmark.")
            ),
            coreArmy = "Longbowmen (damage core) + Spearmen (anti-cavalry screen) + Springalds/Mangonels (siege). " +
                "Turtle behind towers, then out-shoot from a superior economy.",
            build = BuildOrder(
                opening = "Fast Feudal into a 2nd Town Center (~6:30–7:30) for a dual-TC economy boom, with light Longbow + Spear pressure to deny aggression.",
                winCondition = "Out-economy from two TCs and grind the opponent down with massed Longbows behind a defensive network."
            )
        ),

        Civ(
            id = "french",
            name = "French",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.A,
            difficulty = Difficulty.EASY,
            beginnerFriendly = true,
            accent = 0xFF2E5BB0,
            tagline = "Snowballing economy and relentless cavalry",
            overview = "France ages up fast and discounts its economy and military buildings, " +
                "fuelling early Royal Knight aggression that snowballs. Straightforward and strong " +
                "— a great first 'aggressive' civ.",
            winRateConqueror = 50.7,
            pickRateConqueror = 10.2,
            agingMechanic = null,
            bonuses = listOf(
                "Economy and military buildings/upgrades get progressively cheaper (drop-off buildings cost 25 less wood).",
                "Royal Knights deal bonus charge damage and are available early.",
                "Traders are more efficient and the Chamber of Commerce generates passive gold.",
                "Faster production from military buildings."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "School of Cavalry", "Chamber of Commerce",
                    "+20% Stable production speed — pumps out Royal Knights for early pressure; Chamber of Commerce is the trade/eco alternative."),
                lm(Age.CASTLE, "Royal Institute", "Guild Hall",
                    "Cheaper research that ignores age requirements; houses French unique techs like Royal Bloodlines."),
                lm(Age.IMPERIAL, "Red Palace", "College of Artillery",
                    "A fortress with crossbow/cannon emplacements for a near-impregnable position.")
            ),
            units = listOf(
                u("Royal Knight", CAVALRY, unique = true, recommended = true,
                    "Hits harder after a charge; snipes archers and siege and applies constant Feudal/Castle pressure."),
                u("Arbalétrier", RANGED, unique = true, recommended = true,
                    "Heavy crossbow that shreds armored units — the Castle/Imperial backbone."),
                u("Royal Ribauldequin", SIEGE, unique = true, recommended = false,
                    "Organ-gun artillery that deletes massed unarmored units."),
                u("Galleass", NAVAL, unique = true, recommended = false,
                    "Premium French warship for water maps.")
            ),
            coreArmy = "Royal Knights (pressure + protect) + Arbalétriers (anti-armor) + a Royal Ribauldequin or two. " +
                "Watch for braced Spearmen against the knights.",
            build = BuildOrder(
                opening = "Fast Feudal (the eco discount lets France hit Feudal early) straight into Royal Knight aggression; a 2-TC boom is the greedier alternative.",
                winCondition = "Use cavalry pressure to deny the opponent while the discounted economy snowballs into a stronger army."
            )
        ),

        Civ(
            id = "hre",
            name = "Holy Roman Empire",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.C,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = true,
            accent = 0xFFCBA22B,
            tagline = "Prelate-boosted economy and tanky infantry",
            overview = "The Holy Roman Empire uses Prelates to supercharge villager gather rates " +
                "and relics to fund a powerful infantry army. Strong Feudal timings into a " +
                "relic-fuelled boom.",
            winRateConqueror = 48.6,
            pickRateConqueror = 2.6,
            agingMechanic = null,
            bonuses = listOf(
                "Prelates inspire villagers for a big gather-rate boost and appear early.",
                "Relics garrisoned in buildings (e.g. Regnitz Cathedral) generate large amounts of gold.",
                "Strong, upgrade-friendly infantry (Men-at-Arms, Landsknecht).",
                "Faster Sacred Site capture."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Aachen Chapel", "Meinwerk Palace",
                    "A garrisoned Prelate inspires a huge radius of villagers for a massive economy snowball; Meinwerk Palace is the aggressive Men-at-Arms timing."),
                lm(Age.CASTLE, "Regnitz Cathedral", "Burgrave Palace",
                    "Relics here generate +100% gold — a top-tier economy; Burgrave Palace is an all-in infantry-production timing."),
                lm(Age.IMPERIAL, "Elzbach Palace", "Palace of Swabia",
                    "A defensive keep with damage reduction that pairs with HRE's influence bonuses.")
            ),
            units = listOf(
                u("Landsknecht", INFANTRY, unique = true, recommended = true,
                    "Two-handed swordsman with splash damage — devastating against clumped armies."),
                u("Men-at-Arms", INFANTRY, unique = false, recommended = true,
                    "Strong, cheap-to-upgrade frontline; excellent in Feudal."),
                u("Prelate", RELIGIOUS, unique = true, recommended = true,
                    "Inspires villagers (eco) and heals/buffs the army; central to HRE's identity."),
                u("Handcannoneer", GUNPOWDER, unique = false, recommended = false,
                    "Anti-armor ranged punch for the late game.")
            ),
            coreArmy = "Men-at-Arms + Landsknecht (splash) + Crossbows/Handcannoneers, with Prelates healing behind. " +
                "Tank in front, splash the middle, shoot from the back.",
            build = BuildOrder(
                opening = "Either an early Men-at-Arms + Ram pressure timing, or a Prelate-boosted Fast Castle into a relic economy.",
                winCondition = "Convert the Prelate/relic economy lead into a strong, well-upgraded infantry army."
            )
        ),

        Civ(
            id = "mongols",
            name = "Mongols",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.S,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFFB5651D,
            tagline = "Nomadic mobility and relentless raiding",
            overview = "The Mongols have no houses, can pack up and relocate every building, and " +
                "dominate through mobility and early aggression. Very high skill ceiling, top reward.",
            winRateConqueror = 52.5,
            pickRateConqueror = 4.7,
            agingMechanic = "Mongol landmarks can be packed up and relocated like any other building.",
            bonuses = listOf(
                "No houses needed — start at maximum population.",
                "All buildings can be packed up and moved across the map.",
                "Igniting enemy buildings plunders extra food and gold.",
                "Ovoo generates stone and boosts production; the Khan provides scouting and buff arrows."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Silver Tree", "Deer Stones",
                    "A market that builds Traders faster and cheaper for a strong trade economy; Deer Stones (Yam speed aura) suits mobility/aggression."),
                lm(Age.CASTLE, "Kurultai", "Steppe Redoubt",
                    "Khan-linked landmark that heals and grants +25% damage to nearby units in fights; Steppe Redoubt is the economy pick — choose by playstyle."),
                lm(Age.IMPERIAL, "White Stupa", "Khaganate Palace",
                    "Acts as an Ovoo producing ~240 stone/min for an endless economy; Khaganate Palace spawns free composite armies.")
            ),
            units = listOf(
                u("Mangudai", RANGED, unique = true, recommended = true,
                    "Mounted archer that fires while moving — the signature hit-and-run unit. Fragile, so protect from Spears/Camels."),
                u("Khan", HERO, unique = true, recommended = true,
                    "Hero that scouts and fires signal arrows to buff nearby units."),
                u("Horseman", CAVALRY, unique = false, recommended = false,
                    "Cheap anti-cavalry to screen the Mangudai."),
                u("Traction Trebuchet", SIEGE, unique = true, recommended = false,
                    "Mobile siege that packs up and moves with the army.")
            ),
            coreArmy = "Mangudai + Springald/Mangonel mobility ball, with Horsemen or Knights to screen. " +
                "Constantly raid, relocate, and never sit still.",
            build = BuildOrder(
                opening = "Ultra-fast Feudal with early cavalry aggression and tower/trade harassment; relocate production as needed.",
                winCondition = "Win the map through mobility, raiding and Khan-buffed armies before the opponent stabilizes."
            )
        ),

        Civ(
            id = "rus",
            name = "Rus",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.B,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = true,
            accent = 0xFF2E8B57,
            tagline = "Hunting-bounty economy into cavalry and gunpowder",
            overview = "Rus turns hunting into a unique gold economy, booms hard, then fields " +
                "strong cavalry, Streltsy gunpowder and powerful siege. Forgiving and " +
                "beginner-friendly.",
            winRateConqueror = 49.5,
            pickRateConqueror = 4.3,
            agingMechanic = null,
            bonuses = listOf(
                "Hunting deer/wildlife grants a gold bounty — a unique food-to-gold economy.",
                "Hunting Cabins generate passive gold and boost nearby gathering.",
                "Wooden defenses (palisades, fortresses) have roughly double HP.",
                "Scout and mining bounties add extra resources."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Golden Gate", "Kremlin",
                    "Enables favourable 1.5x resource trades plus passive resources for an economy lead; Kremlin is the defensive wooden-fortress option vs early rushes."),
                lm(Age.CASTLE, "Abbey of the Trinity", "High Trade House",
                    "Cheap Warrior Monks and religious upgrades for a military spike; High Trade House is the greedy gold-economy pick."),
                lm(Age.IMPERIAL, "High Armory", "Spasskaya Tower",
                    "Cheaper siege and siege techs; Spasskaya Tower is the defensive keep.")
            ),
            units = listOf(
                u("Streltsy", GUNPOWDER, unique = true, recommended = true,
                    "Cheap handcannon that fires faster while stationary; melts armored Knights and Men-at-Arms — one of the best Imperial units."),
                u("Warrior Monk", RELIGIOUS, unique = true, recommended = true,
                    "Mounted monk that heals and buffs (Saint's Blessing); dominates relic/sacred-site play."),
                u("Knight", CAVALRY, unique = false, recommended = true,
                    "Solid heavy-cavalry core."),
                u("Horse Archer", RANGED, unique = false, recommended = false,
                    "Mobile harassment to complement Knights.")
            ),
            coreArmy = "Knights + Horse Archers/Streltsy + heavy Siege. Boom on hunt gold, then field a dominant late-game army.",
            build = BuildOrder(
                opening = "Fast Castle or a Hunting Cabin boom, using scout/hunt bounty gold to power ahead in economy.",
                winCondition = "Snowball the bounty economy into a strong Castle/Imperial army with excellent siege."
            )
        ),

        Civ(
            id = "delhi",
            name = "Delhi Sultanate",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.C,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFF1F8A8A,
            tagline = "Free (but slow) research, scholars and war elephants",
            overview = "Delhi researches every technology for free — but slowly — and uses " +
                "Scholars to speed it up. A tech-hoarding civ that snowballs into a War Elephant " +
                "deathball. High management.",
            winRateConqueror = 47.5,
            pickRateConqueror = 2.3,
            agingMechanic = null,
            bonuses = listOf(
                "All technologies are free, but research takes a long time.",
                "Scholars (from Mosques) accelerate research while garrisoned and influence Sacred Sites.",
                "Infantry build defensive structures instead of villagers.",
                "Powerful War Elephants (melee + siege)."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Dome of the Faith", "Tower of Victory",
                    "Scholars at half cost — Scholars are the engine of Delhi's free-research economy; Tower of Victory gives a +15% attack-speed aura for aggression."),
                lm(Age.CASTLE, "House of Learning", "Compound of the Defence",
                    "Unlocks Delhi's deep unique tech tree; Compound of the Defence is the stone-discount turtle option."),
                lm(Age.IMPERIAL, "Palace of the Sultan", "Hisar Academy",
                    "Automatically produces Tower War Elephants; garrison Scholars to speed it up.")
            ),
            units = listOf(
                u("War Elephant", CAVALRY, unique = true, recommended = true,
                    "Enormous-HP melee unit that demolishes buildings and anchors a deathball. Countered by Spears + massed arrows."),
                u("Scholar", RELIGIOUS, unique = true, recommended = true,
                    "Researches free tech and boosts Sacred Sites — the civ's economic identity."),
                u("Tower Elephant", RANGED, unique = true, recommended = false,
                    "Elephant carrying archers; a mobile fortress."),
                u("Ghazi Raider", CAVALRY, unique = true, recommended = false,
                    "Aggressive cavalry option for raids.")
            ),
            coreArmy = "War Elephants + Spearmen/Crossbows + siege, backed by free-tech scaling. Slow but overwhelming once online.",
            build = BuildOrder(
                opening = "Scholar boom — garrison Mosques to research for free, take a Sacred Site, and tech up safely.",
                winCondition = "Reach a free-tech lead and roll over the opponent with elephants and a fully-upgraded army."
            )
        ),

        Civ(
            id = "chinese",
            name = "Chinese",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.C,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFFC0392B,
            tagline = "Dynasty-switching economy with the widest roster",
            overview = "China builds two landmarks per age to switch Dynasties (Tang/Song/Yuan/Ming), " +
                "each unlocking new units and bonuses, all funded by a tax economy. The broadest " +
                "toolkit in the game, but management-heavy.",
            winRateConqueror = 47.5,
            pickRateConqueror = 3.5,
            agingMechanic = "Building BOTH landmarks of an age switches Dynasty for extra bonuses, so Chinese players often build both.",
            bonuses = listOf(
                "Switch Dynasties (Tang/Song/Yuan/Ming) by building both landmarks of an age, each unlocking unique units/buildings.",
                "Imperial Officials collect taxes for a strong supplementary economy.",
                "Villages/houses support extra population and supervisors.",
                "Villagers build defenses twice as fast; strong gunpowder and siege."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Barbican of the Sun", "Imperial Academy",
                    "Immediate defensive value (long-range hand cannon + garrison); build the Imperial Academy too to reach Song Dynasty."),
                lm(Age.CASTLE, "Astronomical Clocktower", "Imperial Palace",
                    "A Siege Workshop producing siege with +50% HP — superb under Imperial Official supervision."),
                lm(Age.IMPERIAL, "Spirit Way", "Great Wall Gatehouse",
                    "Lets all buildings train previous-Dynasty elite units at −30% cost; Great Wall Gatehouse is the defensive wall option.")
            ),
            units = listOf(
                u("Zhuge Nu", RANGED, unique = true, recommended = true,
                    "Repeating crossbow with burst volleys that melt low-armor units (Song Dynasty)."),
                u("Nest of Bees", SIEGE, unique = true, recommended = true,
                    "Rocket-artillery siege with strong area damage."),
                u("Grenadier", GUNPOWDER, unique = true, recommended = true,
                    "AoE grenades effective against both armored and unarmored clumps (Ming)."),
                u("Fire Lancer", CAVALRY, unique = true, recommended = false,
                    "Hard-hitting charge cavalry (Yuan).")
            ),
            coreArmy = "Zhuge Nu + Nest of Bees (+ Grenadiers/Fire Lancers). Play safe early, then leverage dynasty bonuses and superior siege/economy.",
            build = BuildOrder(
                opening = "Fast Feudal via the dynasty system; play defensively early (China is vulnerable before it stabilizes).",
                winCondition = "Out-economy through taxes and dynasties, then win with gunpowder and heavy siege."
            )
        ),

        Civ(
            id = "abbasid",
            name = "Abbasid Dynasty",
            variantOf = null,
            dlc = Dlc.BASE,
            tier = Tier.B,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFFC9A227,
            tagline = "House of Wisdom tech tree, camels and Golden Ages",
            overview = "The Abbasids age up by extending a single House of Wisdom, choosing from " +
                "four wings, and trigger Golden Ages for sweeping bonuses. Signature camels debuff " +
                "enemy cavalry. Reworked and flexible.",
            winRateConqueror = 49.2,
            pickRateConqueror = 5.6,
            agingMechanic = "No dual landmarks — age up by adding wings to the House of Wisdom (Economic/Military/Trade/Culture); each unlocks bonuses and powers Golden Ages.",
            bonuses = listOf(
                "House of Wisdom wings each grant bonuses and trigger a Golden Age (faster gathering/research/production).",
                "Infantry build Rams and Siege Towers for free.",
                "Camel Archers and Camel Riders reduce nearby enemy cavalry damage ('Camel Unease').",
                "A recent rework added an Administrative Wing and new economy/siege techs."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Economic Wing", "Military Wing",
                    "A fast Golden-Age economy is the standard tempo opener; the Military Wing instead enables early camel-archer pressure."),
                lm(Age.CASTLE, "Culture Wing", "Trade Wing",
                    "Culture for cheaper/faster research and faith; Trade for gold-heavy maps."),
                lm(Age.IMPERIAL, "Remaining Wing", null,
                    "Complete the House of Wisdom for the full set of Golden-Age bonuses.")
            ),
            units = listOf(
                u("Camel Archer", RANGED, unique = true, recommended = true,
                    "Mounted archer whose aura weakens enemy cavalry; thrashes Spearmen and light infantry with hit-and-run."),
                u("Ghulam", INFANTRY, unique = true, recommended = true,
                    "Fast, double-striking Man-at-Arms replacement; a strong frontline."),
                u("Camel Rider", CAVALRY, unique = true, recommended = false,
                    "Melee camel that also debuffs enemy cavalry.")
            ),
            coreArmy = "Camel Archers + Ghulam/Spearmen + Camel Riders. The anti-cavalry aura makes Abbasid excellent against knight-heavy civs.",
            build = BuildOrder(
                opening = "Camel-archer Fast Feudal, or an early house/worker-speed boom into the Economic Wing Golden Age.",
                winCondition = "Use camels to neutralize enemy cavalry while the Golden-Age economy out-produces them."
            )
        ),

        // ─────────────────────────── FREE EXPANSION (2022) ──────────────────────────

        Civ(
            id = "ottomans",
            name = "Ottomans",
            variantOf = null,
            dlc = Dlc.FREE_EXPANSION,
            tier = Tier.D,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFF8E1B1B,
            tagline = "Free unit production from Military Schools and siege",
            overview = "The Ottomans passively produce units from Military Schools and earn Vizier " +
                "Points for powerful council techs, snowballing into a siege-heavy army. Strong " +
                "concept, currently underpowered at the top after nerfs.",
            winRateConqueror = 45.1,
            pickRateConqueror = 3.0,
            agingMechanic = null,
            bonuses = listOf(
                "Military Schools continuously produce one unit type for free.",
                "Earn Vizier Points (from training units / aging up) to unlock Imperial Council techs.",
                "Gunpowder and siege available a little earlier; strong Great Bombards.",
                "Mehter drummers buff the army's attack speed and armor."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Twin Minaret Medrese", null,
                    "Strong economy/tech option (best for an eco-greedy opening); the aggressive landmark instead suits Military-School pressure — pro preference varies."),
                lm(Age.CASTLE, "Istanbul Imperial Palace", "Mehmed Imperial Armory",
                    "Doubles Imperial Council XP to snowball Vizier points; Mehmed Imperial Armory trains siege cheaply for siege timings."),
                lm(Age.IMPERIAL, "Sea Gate Castle", null,
                    "Choose by map and strategy; the Ottoman Imperial picks are siege/defense oriented.")
            ),
            units = listOf(
                u("Sipahi", CAVALRY, unique = true, recommended = true,
                    "Durable cavalry that anchors Ottoman aggression."),
                u("Mehter", INFANTRY, unique = true, recommended = true,
                    "Drummer that buffs nearby units' attack speed and armor — a force multiplier."),
                u("Janissary", GUNPOWDER, unique = true, recommended = true,
                    "Hard-hitting gunpowder infantry."),
                u("Great Bombard", SIEGE, unique = true, recommended = false,
                    "Devastating siege cannon.")
            ),
            coreArmy = "Sipahi + gunpowder/siege + Mehter support, topped up for free by Military Schools.",
            build = BuildOrder(
                opening = "Dark-Age Military School harassment (Spears onto enemy gold) into a second school for archers, then a siege timing.",
                winCondition = "Snowball free production and Vizier techs into a Mehter-buffed siege army."
            )
        ),

        Civ(
            id = "malians",
            name = "Malians",
            variantOf = null,
            dlc = Dlc.FREE_EXPANSION,
            tier = Tier.A,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFFD4A017,
            tagline = "Gold and cattle economy with stealthy harassment",
            overview = "Malians run a powerful gold economy from Pit Mines and cattle, field cheap " +
                "spammable cavalry and stealthy Musofadi ambushers, and avoid pitched fights in " +
                "favour of raids. Strong on land maps.",
            winRateConqueror = 50.9,
            pickRateConqueror = 3.6,
            agingMechanic = null,
            bonuses = listOf(
                "Pit Mines generate passive gold from gold/resource veins.",
                "Cattle act as a stockpiled, growable food source herded to ranches.",
                "Cheaper buildings (built partly from wood).",
                "Musofadi infantry can enter stealth for ambushes; strong trade/canoe raids."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Saharan Trade Network", "Grand Fulani Corral",
                    "Improved toll outposts tax passing traders for food + gold — the signature eco engine; Grand Fulani Corral leans into cattle food (Mansa Quarry is the steady gold/stone alternative)."),
                lm(Age.CASTLE, "Farimba Garrison", "Fort of the Huntress",
                    "Unlocks improved, cheaper military for army strength; Fort of the Huntress adds stealth infantry and defense for raiding play."),
                lm(Age.IMPERIAL, "Grand Fulani Keep", null,
                    "Defensive/military landmark to support the late game.")
            ),
            units = listOf(
                u("Sofa", CAVALRY, unique = true, recommended = true,
                    "Cheap (~60 gold) spammable cavalry for mass raids and numbers."),
                u("Musofadi Warrior", INFANTRY, unique = true, recommended = true,
                    "Stealthy ambusher that strikes from concealment."),
                u("Javelin Thrower", RANGED, unique = true, recommended = true,
                    "Skirmisher with bonus range/damage that counters archers (replaces the Crossbow)."),
                u("Donso", INFANTRY, unique = true, recommended = false,
                    "Spear-and-shield anti-cavalry infantry.")
            ),
            coreArmy = "Sofa + Javelin Thrower + Donso/Musofadi, hit-and-run focused (units have low armor, so avoid head-on fights). Endless cheap units funded by gold.",
            build = BuildOrder(
                opening = "Gold/cattle economy boom (Pit Mines + Cattle) — lower-risk than extra Town Centers — into Musofadi harassment.",
                winCondition = "Out-gold the opponent and bleed them with raids and ambushes rather than pitched battles."
            )
        ),

        // ───────────────────────── THE SULTANS ASCEND (2023) ────────────────────────

        Civ(
            id = "japanese",
            name = "Japanese",
            variantOf = null,
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.A,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFF3B5BA9,
            tagline = "Flexible samurai with Bannerman buffs and a Daimyo economy",
            overview = "Japan buffs its buildings and units through Shinto Yorishiro and Bannerman " +
                "auras, runs a Daimyo farm economy, and fields a flexible samurai army with the " +
                "unique Shinobi saboteur.",
            winRateConqueror = 51.4,
            pickRateConqueror = 4.0,
            agingMechanic = null,
            bonuses = listOf(
                "Town Centers upgrade into Daimyo Manors/Shogunate Castles that boost nearby farms and unlock Bannermen.",
                "Bannermen project auras that buff melee infantry, ranged infantry or cavalry.",
                "Shinto Priests place Yorishiro sacred objects in buildings for special bonuses.",
                "Shinobi spies disguise as villagers and sabotage; buildings upgrade in tiers as you advance."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Koka Township", "Kura Storehouse",
                    "Utility/economy landmark; Kura Storehouse adds resource flexibility — picks are flexible and map-dependent."),
                lm(Age.CASTLE, "Floating Gate", "Temple of Equality",
                    "Floating Gate provides Shinto Priests/Yorishiro power; Temple of Equality is the military-discount option."),
                lm(Age.IMPERIAL, "Castle of the Crow", "Tanegashima Gunsmith",
                    "Castle of the Crow for defense/production; Tanegashima Gunsmith leans into gunpowder (Ozutsu/handcannon).")
            ),
            units = listOf(
                u("Mounted Samurai", CAVALRY, unique = true, recommended = true,
                    "Strong, flexible cavalry; excellent against other cavalry — staple of the Castle/Imperial army."),
                u("Yumi Ashigaru", RANGED, unique = true, recommended = true,
                    "Archer replacement; the ranged backbone, buffed by the Yumi Bannerman."),
                u("Shinobi", INFANTRY, unique = true, recommended = true,
                    "Disguised saboteur that can disable enemy Town Centers — a unique map-control tool."),
                u("Onna-Musha", RANGED, unique = true, recommended = false,
                    "Fast horse archer with strong DPS against armor.")
            ),
            coreArmy = "Mounted Samurai + (Veteran) Yumi Ashigaru, add Onna-Musha for mobility and Shinobi for sabotage. Take sacred sites and lean on buffs + economy.",
            build = BuildOrder(
                opening = "Flexible Feudal/Castle using Yorishiro to buff key buildings and a Daimyo farm economy.",
                winCondition = "Leverage buffed buildings/economy and Shinobi disruption into a strong, adaptable samurai army."
            )
        ),

        Civ(
            id = "byzantines",
            name = "Byzantines",
            variantOf = null,
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.C,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFF7E57C2,
            tagline = "Adaptive toolbox: mercenaries, cisterns and Greek Fire",
            overview = "The Byzantines are the ultimate flexible civ — hire other civs' units as " +
                "mercenaries with Olive Oil, boost the economy with Cistern/Aqueduct networks, and " +
                "burn enemies with Greek Fire. High skill, very versatile.",
            winRateConqueror = 48.5,
            pickRateConqueror = 5.7,
            agingMechanic = null,
            bonuses = listOf(
                "Olive Oil (a 5th resource) hires Mercenaries — borrowed units from other civilizations.",
                "Cistern + Aqueduct networks boost nearby villager gather rates and grant 'influence' that buffs buildings.",
                "Greek Fire (Cheirosiphons, upgraded Dromons, Trebuchets) deals damage-over-time.",
                "Flexible use of captured/landmark structures; signature Cataphracts."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Grand Winery", "Imperial Hippodrome",
                    "+30% food gather aura and Olive Oil production to fuel mercenaries; the Imperial Hippodrome is the cavalry-aggression pick."),
                lm(Age.CASTLE, "Imperial Hippodrome", "Cistern of the First Hill",
                    "A strong production/cavalry landmark; the Cistern of the First Hill is widely considered the weak option."),
                lm(Age.IMPERIAL, "Foreign Engineering Company", "Golden Horn Tower",
                    "Lets you buy siege with Olive Oil (siege the Byzantines otherwise lack); Golden Horn Tower auto-recruits free mercenaries.")
            ),
            units = listOf(
                u("Cataphract", CAVALRY, unique = true, recommended = true,
                    "Durable shock cavalry that trample-damages on the charge."),
                u("Varangian Guard", INFANTRY, unique = true, recommended = true,
                    "Elite axe infantry with a berserk mode — a heavy melee finisher."),
                u("Cheirosiphon", SIEGE, unique = true, recommended = true,
                    "Flamethrower with splash — great vs infantry and siege."),
                u("Mercenaries", INFANTRY, unique = true, recommended = false,
                    "Hire other civs' units with Olive Oil to plug any gap in the army.")
            ),
            coreArmy = "Cataphracts + Limitanei/Varangian Guard + Cheirosiphon, supplemented by Mercenaries. Defensive Cistern economy into a flexible, hard-to-counter mix.",
            build = BuildOrder(
                opening = "Defensive Cistern/Aqueduct infrastructure boom, keeping mercenary options open.",
                winCondition = "Out-sustain with the Cistern economy and adapt the army (mercenaries + Greek Fire) to beat any composition."
            )
        ),

        Civ(
            id = "ayyubids",
            name = "Ayyubids",
            variantOf = "Abbasid Dynasty",
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.C,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFFB8860B,
            tagline = "Choice-driven House of Wisdom and flexible camels",
            overview = "An Abbasid variant rebuilt around deep customization: every House of Wisdom " +
                "wing offers a choice of two bonuses, and the Desert Raider camel swaps between " +
                "ranged and melee. Extremely flexible, complex to master.",
            winRateConqueror = 47.9,
            pickRateConqueror = 3.2,
            agingMechanic = "Age up via four House of Wisdom wings (Economic/Military/Trade/Culture); each wing offers a CHOICE between two bonuses, added in any order.",
            bonuses = listOf(
                "Each House of Wisdom wing offers a choice between two distinct bonuses per age.",
                "Golden Age (from building in House of Wisdom influence) speeds gathering/research/production and adds fire armor to buildings.",
                "The Desert Raider camel swaps between ranged and melee stance on a short cooldown.",
                "Strong siege options (Tower of the Sultan, Manjaniq trebuchet)."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Culture → Advancement", "Culture → Logistics",
                    "Advancement gives cheaper/faster age-ups for a fast tempo; Logistics adds Dervishes + Mass Heal for army support."),
                lm(Age.CASTLE, "Military → Reinforcements", "Military → Master Smiths",
                    "Reinforcements produces free Desert Raiders; Master Smiths grants instant blacksmith techs."),
                lm(Age.IMPERIAL, "Trade → Bazaar", "Trade → Advisors",
                    "Bazaar lets you hire units/buy resources; Advisors (Atabeg) garrison to buff production buildings.")
            ),
            units = listOf(
                u("Desert Raider", CAVALRY, unique = true, recommended = true,
                    "Flexible camel that toggles ranged/melee; strong anti-cavalry and harassment."),
                u("Ghulam", INFANTRY, unique = true, recommended = true,
                    "Fast, double-striking infantry frontline."),
                u("Manjaniq", SIEGE, unique = true, recommended = false,
                    "Powerful trebuchet-class siege."),
                u("Dervish", RELIGIOUS, unique = true, recommended = false,
                    "Support unit with Mass Heal (via the Logistics wing).")
            ),
            coreArmy = "Desert Raiders + Ghulam + siege, tailored by your House of Wisdom wing choices and Golden Age timing. Camels counter cavalry-heavy civs.",
            build = BuildOrder(
                opening = "Pick the Advancement wing for fast tempo, or Logistics for army support; flexible camel-based Feudal/Castle.",
                winCondition = "Specialize via wing choices and out-flex the opponent with camels, siege and Golden-Age economy."
            )
        ),

        Civ(
            id = "jeannedarc",
            name = "Jeanne d'Arc",
            variantOf = "French",
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.A,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFF5C7CCB,
            tagline = "A single hero unit that levels up and transforms",
            overview = "A French variant built around the Jeanne d'Arc hero, who gains XP, levels " +
                "through all four ages, and branches into a melee or ranged powerhouse. Retains " +
                "French economic strengths; snowballs around keeping Jeanne alive and fed with kills.",
            winRateConqueror = 52.0,
            pickRateConqueror = 2.2,
            agingMechanic = "Uses the same landmarks as France; the unique power is the leveling Jeanne d'Arc hero, not different landmarks.",
            bonuses = listOf(
                "The Jeanne d'Arc hero gains XP from combat and transforms across all four ages (melee or ranged path).",
                "Jeanne's Companions provide an early elite squad.",
                "Retains French economic discounts and strong cavalry.",
                "Rally-Call and powerful active hero abilities."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "School of Cavalry", "Chamber of Commerce",
                    "Pumps cavalry for early pressure (shares the French roster)."),
                lm(Age.CASTLE, "Royal Institute", "Guild Hall",
                    "Cheaper research and French unique techs."),
                lm(Age.IMPERIAL, "Red Palace", "College of Artillery",
                    "Fortress with crossbow/cannon emplacements.")
            ),
            units = listOf(
                u("Jeanne d'Arc (hero)", HERO, unique = true, recommended = true,
                    "Leveling hero: melee path → Mounted Jeanne, ranged path → Blast Cannon Jeanne. Becomes a one-unit army at max level."),
                u("Jeanne's Companions", INFANTRY, unique = true, recommended = true,
                    "Champion companions that upgrade to Elite as Jeanne levels."),
                u("Royal Knight", CAVALRY, unique = true, recommended = false,
                    "French heavy cavalry."),
                u("Arbalétrier", RANGED, unique = true, recommended = false,
                    "French heavy crossbow for anti-armor.")
            ),
            coreArmy = "Leveled Jeanne + Companions + Royal Knights/Arbalétriers. The win condition is rushing Jeanne's XP through fights and snowballing.",
            build = BuildOrder(
                opening = "French-style cavalry opening built around getting Jeanne into safe fights to level her quickly.",
                winCondition = "Snowball the leveling hero and French economy into an unstoppable mid/late game."
            )
        ),

        Civ(
            id = "orderofthedragon",
            name = "Order of the Dragon",
            variantOf = "Holy Roman Empire",
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.B,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFF7B241C,
            tagline = "Quality over quantity — elite Gilded units",
            overview = "An HRE variant that fields fewer but far stronger 'Gilded' units (and " +
                "faster-gathering Gilded villagers). Low unit count means less micro, but it is " +
                "population- and economy-inefficient. Solid on land maps.",
            winRateConqueror = 50.0,
            pickRateConqueror = 4.6,
            agingMechanic = "Shares the HRE landmark roster; the twist is Gilded units rather than different landmarks.",
            bonuses = listOf(
                "Trains Gilded units — premium versions of infantry/cavalry with much stronger stats (but higher cost, pop and train time).",
                "Gilded Villagers gather faster (but cost more).",
                "Shares HRE's relic/Prelate economy and landmarks.",
                "Small, elite armies that out-trade unit-for-unit."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Aachen Chapel", "Meinwerk Palace",
                    "Prelate inspiration to power the Gilded-villager economy."),
                lm(Age.CASTLE, "Regnitz Cathedral", "Burgrave Palace",
                    "Relics generate large gold (~160/min) to afford expensive Gilded units."),
                lm(Age.IMPERIAL, "Elzbach Palace", "Palace of Swabia",
                    "Defensive keep with damage reduction.")
            ),
            units = listOf(
                u("Gilded Landsknecht", INFANTRY, unique = true, recommended = true,
                    "Premium splash infantry that can erase clumped armies."),
                u("Gilded Knight", CAVALRY, unique = true, recommended = true,
                    "High-survivability heavy cavalry."),
                u("Gilded Man-at-Arms", INFANTRY, unique = true, recommended = true,
                    "Tanky elite frontline."),
                u("Gilded Crossbowman", RANGED, unique = true, recommended = false,
                    "Extra range and bonus vs heavy units.")
            ),
            coreArmy = "A small elite ball: Gilded Men-at-Arms + Gilded Landsknecht + Gilded Crossbow/Knight. Out-trade with quality, but protect your fewer, costlier units.",
            build = BuildOrder(
                opening = "HRE-style Prelate economy into Gilded units; pick fights you win on quality.",
                winCondition = "Win decisive fights with elite Gilded units before the population/economy inefficiency catches up."
            )
        ),

        Civ(
            id = "zhuxi",
            name = "Zhu Xi's Legacy",
            variantOf = "Chinese",
            dlc = Dlc.SULTANS_ASCEND,
            tier = Tier.S,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFF1E8449,
            tagline = "Administration-heavy Chinese variant; the best water civ",
            overview = "A Chinese variant centered on Imperial Officials who 'Supervise' buildings " +
                "for massive production/research speed, with an early Age-2 unique unit and strong " +
                "water play. Management-heavy but top-tier.",
            winRateConqueror = 52.4,
            pickRateConqueror = 4.7,
            agingMechanic = "Retains the Chinese dynasty system but uses a different landmark set; Imperial Officials 'Supervise' buildings for big boosts.",
            bonuses = listOf(
                "Starts with one fewer villager but a free Imperial Official.",
                "Imperial Officials Supervise: +150% production/research speed at military/research buildings and +20% villager drop-off.",
                "Unique techs (Mount Lu Academy, Zhu Xi's Library) massively upgrade Imperial Officials.",
                "Strong on water maps; the Palace Guard is available unusually early (Age 2)."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Meditation Gardens", null,
                    "Generates resources from nearby veins/bushes (up to +60%), but income drops when enemies are near — the standard greedy pick."),
                lm(Age.CASTLE, "Mount Lu Academy", "Jiangnan Tower",
                    "Imperial Officials collect tax 2x plus 20% as food (economy); Jiangnan Tower spawns free armies for military momentum."),
                lm(Age.IMPERIAL, "Zhu Xi's Library", "Temple of the Sun",
                    "Five unique techs (research two) for big power spikes; Temple of the Sun is a toggleable global military/defensive buff.")
            ),
            units = listOf(
                u("Palace Guard", INFANTRY, unique = true, recommended = true,
                    "Fast infantry available in Age 2 — enables rare early unique-unit aggression and mass."),
                u("Imperial Guard / Yuan Raider", CAVALRY, unique = true, recommended = true,
                    "Powerful cavalry via Dynastic Protectors to complement the infantry."),
                u("Zhuge Nu", RANGED, unique = true, recommended = true,
                    "Burst-fire repeating crossbow (shared Chinese roster)."),
                u("Nest of Bees", SIEGE, unique = true, recommended = false,
                    "Rocket-artillery siege for the late game.")
            ),
            coreArmy = "Mass Palace Guard + Zhuge Nu/Grenadier early, transitioning to Imperial Guard/Yuan Raider + Nest of Bees. Supervise everything for tempo.",
            build = BuildOrder(
                opening = "Early Palace Guard pressure (Age-2 unique unit) or a 2-TC boom; strong water openings.",
                winCondition = "Use Supervised production and tech spikes to out-tempo and out-produce the opponent."
            )
        ),

        // ─────────────────── KNIGHTS OF CROSS AND ROSE (Apr 2025) ───────────────────

        Civ(
            id = "knightstemplar",
            name = "Knights Templar",
            variantOf = "French",
            dlc = Dlc.KNIGHTS_CROSS_ROSE,
            tier = Tier.A,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFFB03A2E,
            tagline = "Crusader alliances, pilgrims and fortresses",
            overview = "A French variant that plays nothing like France: choose an ally each age " +
                "for permanent bonuses and unique units, fund a Pilgrim gold economy to Sacred " +
                "Sites, and field discounted siege from Feudal fortresses. Distinctive and " +
                "economy-strong; the most-picked of the newer civs.",
            winRateConqueror = 51.2,
            pickRateConqueror = 8.5,
            agingMechanic = "Commanderie age-up: choose 1 of 3 ALLIES per age (e.g. Teutonic Order, Genoa, Poland, Castille), each giving a permanent bonus + a unique unit. Fortresses act as Feudal landmarks.",
            bonuses = listOf(
                "Commanderie alliances grant a permanent bonus and a unique unit each age.",
                "Pilgrims generate gold travelling from the Templar HQ to Sacred Sites.",
                "Fortresses are available in Feudal and act as landmarks that guide Pilgrims.",
                "Counterweight Trebuchets fire an extra projectile; siege costs 25% less wood."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Fortress + first ally", null,
                    "Fortresses anchor the map and guide Pilgrims; pick the ally that fits your plan."),
                lm(Age.CASTLE, "Second ally (Commanderie)", null,
                    "Stack a second alliance bonus and unique unit."),
                lm(Age.IMPERIAL, "Third ally (Commanderie)", null,
                    "Complete your alliance set for the late game.")
            ),
            units = listOf(
                u("Templar Brother", CAVALRY, unique = true, recommended = true,
                    "Elite heavy-cavalry core."),
                u("Alliance units", INFANTRY, unique = true, recommended = true,
                    "Each alliance unlocks a unique unit (Teutonic/Genoese/Polish/Castilian options)."),
                u("Counterweight Trebuchet", SIEGE, unique = true, recommended = false,
                    "Fires an extra projectile; deployable on Fortresses.")
            ),
            coreArmy = "Templar Brothers + alliance units + discounted siege/trebuchets, funded by Pilgrim gold and fortress map control.",
            build = BuildOrder(
                opening = "Secure Sacred Sites and run Pilgrims for gold; pick alliances to match the matchup.",
                winCondition = "Snowball Pilgrim gold and Commanderie bonuses into a fortress-backed cavalry/siege army."
            )
        ),

        Civ(
            id = "houseoflancaster",
            name = "House of Lancaster",
            variantOf = "English",
            dlc = Dlc.KNIGHTS_CROSS_ROSE,
            tier = Tier.A,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = false,
            accent = 0xFFC23B5A,
            tagline = "Keep-centric English with longbow volleys",
            overview = "An English variant that fights around Active Keeps which buff its melee and " +
                "cavalry, with Yeoman archers delivering AoE volleys. Defensive and keep-focused.",
            winRateConqueror = 51.4,
            pickRateConqueror = 4.1,
            agingMechanic = null,
            bonuses = listOf(
                "Active Keeps buff Demilancers and Earl's Guards (up to +4 damage, +6 with Berkshire Palace).",
                "The 'A House Unified' landmark grants free Earl's Guards per keep.",
                "Wynguard Palace trains units in cheap batches.",
                "Yeoman archers fire a Synchronized Shot AoE volley."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Keep-centric landmark", null,
                    "Lean into the keep-centric defensive plan early."),
                lm(Age.CASTLE, "A House Unified", null,
                    "Grants free Earl's Guards per keep — the signature power spike."),
                lm(Age.IMPERIAL, "Berkshire Palace", "Wynguard Palace",
                    "Boosts keep buffs to +6; Wynguard Palace instead trains Demilancers/Ribauldequins in cheap batches.")
            ),
            units = listOf(
                u("Yeoman", RANGED, unique = true, recommended = true,
                    "Archer with a Synchronized Shot AoE volley — big burst damage."),
                u("Earl's Guard", INFANTRY, unique = true, recommended = true,
                    "Man-at-Arms with thrown daggers, buffed by keeps."),
                u("Demilancer", CAVALRY, unique = true, recommended = true,
                    "Tanky landmark cavalry."),
                u("Lord of Lancaster", HERO, unique = true, recommended = false,
                    "Infantry hero with a +5% HP aura.")
            ),
            coreArmy = "Yeoman longbow mass + Earl's Guard/Demilancer, anchored on Active Keeps. Defend, then spike with Yeoman volleys.",
            build = BuildOrder(
                opening = "Defensive keep play, building Active Keeps to buff melee and cavalry.",
                winCondition = "Out-defend and burst the enemy with keep-buffed units and Yeoman volleys."
            )
        ),

        // ───────────────────── DYNASTIES OF THE EAST (Nov 2025) ─────────────────────

        Civ(
            id = "goldenhorde",
            name = "Golden Horde",
            variantOf = "Mongols",
            dlc = Dlc.DYNASTIES_OF_THE_EAST,
            tier = Tier.C,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = true,
            accent = 0xFFC59A2E,
            tagline = "Single-landmark nomadic conquest",
            overview = "A Mongol variant built around one mobile core landmark (the Golden Tent) " +
                "and territory control via Outpost edicts. Aggressive, batch-produced cavalry; " +
                "forgiving at lower levels.",
            winRateConqueror = 47.6,
            pickRateConqueror = 4.5,
            agingMechanic = "The Golden Tent is the ONLY landmark — age-up upgrades are chosen there; it spreads Edicts to Fortified Outposts to control territory.",
            bonuses = listOf(
                "Most units (incl. villagers/traders) are produced in batches of 2.",
                "Starts at max population; Stables are available in the Dark Age.",
                "Unique technologies cost only Stone.",
                "The Golden Tent recruits Batu Khan + Torguuds and spreads Edicts to Outposts."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Golden Tent (age-up choice)", null,
                    "Your only landmark; choose the age-up upgrade that fits your plan."),
                lm(Age.CASTLE, "Golden Tent (age-up choice)", null,
                    "Continue selecting upgrades at the Tent."),
                lm(Age.IMPERIAL, "Golden Tent (age-up choice)", null,
                    "Final Tent upgrades for the late game.")
            ),
            units = listOf(
                u("Torguud", CAVALRY, unique = true, recommended = true,
                    "Heavy cavalry recruited from the Golden Tent."),
                u("Horse Archer", RANGED, unique = false, recommended = true,
                    "Mobile mounted harassment (Mongol-style)."),
                u("Batu Khan", HERO, unique = true, recommended = false,
                    "Hero recruited from the Golden Tent.")
            ),
            coreArmy = "Mass mobile cavalry/horse archers backed by Torguud heavy cavalry; raid and control territory with Outpost edicts.",
            build = BuildOrder(
                opening = "Early raiding tempo using batch-produced cavalry and Outpost edicts for map control.",
                winCondition = "Win the mid-game through mobility, raids and territory control."
            )
        ),

        Civ(
            id = "macedonian",
            name = "Macedonian Dynasty",
            variantOf = "Byzantines",
            dlc = Dlc.DYNASTIES_OF_THE_EAST,
            tier = Tier.B,
            difficulty = Difficulty.INTERMEDIATE,
            beginnerFriendly = true,
            accent = 0xFF8E44AD,
            tagline = "Defensive Greco-Norse with a Silver economy",
            overview = "A Byzantine variant with a Varangian (Norse mercenary) infantry theme and a " +
                "5th resource, Silver. Defensive and sustain-focused; out-grinds opponents and " +
                "replaces fallen units.",
            winRateConqueror = 50.2,
            pickRateConqueror = 5.3,
            agingMechanic = null,
            bonuses = listOf(
                "Villagers gather a 5th resource, Silver, while mining Gold/Stone (spent at the Varangian Arsenal).",
                "Runestones grant victory buffs.",
                "Replace fallen units at the Varangian Warcamp.",
                "Unique buildings: Varangian Arsenal/Stronghold/Warcamp."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Varangian / eco landmark", null,
                    "Develop the Silver economy and Varangian infrastructure."),
                lm(Age.CASTLE, "Varangian Stronghold", null,
                    "Anchor the defensive Varangian theme."),
                lm(Age.IMPERIAL, "Golden Horn Tower", null,
                    "Defensive landmark with crossbow emplacements, huge line-of-sight (reveals stealth) and free Crossbowmen over time.")
            ),
            units = listOf(
                u("Varangian Guard", INFANTRY, unique = true, recommended = true,
                    "Elite Norse axe infantry front line."),
                u("Crossbowman", RANGED, unique = false, recommended = true,
                    "Free/buffed crossbows from the Golden Horn Tower."),
                u("Cataphract", CAVALRY, unique = true, recommended = false,
                    "Byzantine shock cavalry (shared theme).")
            ),
            coreArmy = "Varangian heavy infantry + buffed Crossbowmen, sustained by the Silver economy and unit replacement.",
            build = BuildOrder(
                opening = "Defensive boom around the Silver economy and Varangian buildings.",
                winCondition = "Out-sustain and grind the opponent down behind defenses."
            )
        ),

        Civ(
            id = "sengoku",
            name = "Sengoku Daimyo",
            variantOf = "Japanese",
            dlc = Dlc.DYNASTIES_OF_THE_EAST,
            tier = Tier.D,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFF4A69BD,
            tagline = "Sengoku-era clan specialization",
            overview = "A Japanese variant centered on dedicating Daimyo Estates to one of three " +
                "clans — Hojo (melee), Oda (ranged), Takeda (cavalry) — for clan-specific bonuses " +
                "and a deep unique roster. Flexible but execution-heavy, and currently weak after " +
                "repeated nerfs.",
            winRateConqueror = 42.5,
            pickRateConqueror = 1.2,
            agingMechanic = "Dedicate Daimyo Estates to one of three clans (Hojo/Oda/Takeda) for clan-specific bonuses; otherwise uses Japanese-style mechanics.",
            bonuses = listOf(
                "Daimyo Estates dedicate to one of three clans: Hojo (melee infantry), Oda (ranged infantry), Takeda (cavalry).",
                "Each clan grants clan-specific bonuses.",
                "Deep unique roster (Ikko-Ikki Monk, Naginata/Kanabo Samurai, Yari Cavalry, Tanegashima Ashigaru, Ozutsu).",
                "Shares Japanese building-upgrade and sacred mechanics."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Clan / eco landmark", null,
                    "Set your clan direction early."),
                lm(Age.CASTLE, "Clan military landmark", null,
                    "Reinforce your chosen clan's army."),
                lm(Age.IMPERIAL, "Gunpowder / defensive landmark", null,
                    "Round out the late game (e.g. Ozutsu/Tanegashima line).")
            ),
            units = listOf(
                u("Yari Cavalry", CAVALRY, unique = true, recommended = true,
                    "Takeda clan mass cavalry."),
                u("Tanegashima Ashigaru", GUNPOWDER, unique = true, recommended = true,
                    "Oda clan gunpowder line."),
                u("Naginata Samurai", INFANTRY, unique = true, recommended = false,
                    "Hojo clan melee samurai."),
                u("Ozutsu", GUNPOWDER, unique = true, recommended = false,
                    "Heavy handheld-cannon ranged unit.")
            ),
            coreArmy = "Tailor to your clan: Takeda → mass Yari Cavalry; Oda → Yumi/Tanegashima gunpowder; Hojo → samurai melee.",
            build = BuildOrder(
                opening = "Commit to a clan early (Hojo/Oda/Takeda) and build around its bonuses.",
                winCondition = "Spike with your specialized clan army — but note the civ is currently underpowered."
            )
        ),

        Civ(
            id = "tughlaq",
            name = "Tughlaq Dynasty",
            variantOf = "Delhi Sultanate",
            dlc = Dlc.DYNASTIES_OF_THE_EAST,
            tier = Tier.S,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFF17A589,
            tagline = "Governor forts and elephant deathballs",
            overview = "A Delhi variant with a modular Governor system: Tughlaqabad Forts each " +
                "appoint a Governor for stacking bonuses, backing an elephant-and-fortress army on " +
                "Delhi's free-tech engine. Strong but niche, and a top performer at high level.",
            winRateConqueror = 52.4,
            pickRateConqueror = 2.4,
            agingMechanic = "Governor system — Tughlaqabad Forts each appoint 1 of 6 Governors (one active per fort) for stacking civ bonuses. Standard Delhi landmark choices otherwise apply.",
            bonuses = listOf(
                "Tughlaqabad Forts appoint Governors (1 of 6 each) for stacking bonuses.",
                "Unique Worker Elephant and Ballista Elephant.",
                "Retains Delhi's free-but-slow research and Scholars.",
                "Fortress-centric, tech-snowball play."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Tower of Victory", "Dome of the Faith",
                    "Attack-speed aura for aggression vs the Scholar-cost economy (Delhi roster)."),
                lm(Age.CASTLE, "House of Learning", "Compound of the Defender",
                    "Unlocks the unique tech tree; the Compound is the defensive option."),
                lm(Age.IMPERIAL, "Palace of the Sultan", "Hisar Academy",
                    "Auto-produces Tower War Elephants.")
            ),
            units = listOf(
                u("Ballista Elephant", SIEGE, unique = true, recommended = true,
                    "Elephant with a mounted ballista — a strong ranged elephant."),
                u("War Elephant", CAVALRY, unique = true, recommended = true,
                    "Delhi's signature deathball anchor."),
                u("Worker Elephant", ECONOMY, unique = true, recommended = false,
                    "Economy elephant unique to Tughlaq."),
                u("Scholar", RELIGIOUS, unique = true, recommended = false,
                    "Free-tech engine (shared Delhi).")
            ),
            coreArmy = "Ballista/War Elephants + Spearmen/Crossbows + siege, stacked with Governor bonuses and free tech.",
            build = BuildOrder(
                opening = "Fortress + Governor setup into a Delhi-style scholar/free-tech snowball.",
                winCondition = "Stack Governor bonuses and free tech into an elephant deathball."
            )
        ),

        // ───────────────────────── YUE FEI'S LEGACY (May 2026) ──────────────────────

        Civ(
            id = "jin",
            name = "Jin Dynasty",
            variantOf = null, // classification disputed; plays as a standalone Jurchen civ
            dlc = Dlc.YUE_FEI,
            tier = Tier.C,
            difficulty = Difficulty.HARD,
            beginnerFriendly = false,
            accent = 0xFFA93226,
            tagline = "Mobile Jurchen cavalry and gunpowder",
            overview = "The newest civ (Yue Fei's Legacy, May 2026): a hyper-mobile Jurchen civ " +
                "with mounted villagers, settlement-capturing Emissaries and tower-factories, " +
                "snowballing raids into a late-game gunpowder siege storm. High skill floor. " +
                "Often grouped with China but plays as a standalone civ.",
            winRateConqueror = 47.7,
            pickRateConqueror = 3.5,
            agingMechanic = "A bespoke kit (not a simple Chinese reskin): Meng'an Mouke Keeps are towers that auto-produce units, and it ages up via its own landmarks.",
            bonuses = listOf(
                "Mounted Villagers move about twice as fast.",
                "Emissaries capture neutral settlements into Tributary States for passive gold + bonuses.",
                "Meng'an Mouke Keeps are towers that automatically produce units.",
                "Horse Grasslands synergize with Stables to boost military production."
            ),
            landmarks = listOf(
                lm(Age.FEUDAL, "Economy / keep landmark", null,
                    "Develop the mounted-villager economy and keeps."),
                lm(Age.CASTLE, "Military landmark", null,
                    "Reinforce cavalry/gunpowder production."),
                lm(Age.IMPERIAL, "Siege landmark", null,
                    "Power the late-game gunpowder siege.")
            ),
            units = listOf(
                u("Iron Pagoda", CAVALRY, unique = true, recommended = true,
                    "Heavy charge-cavalry core."),
                u("Mounted Grenadier", GUNPOWDER, unique = true, recommended = true,
                    "Ranged cavalry harassment."),
                u("Eruptor", SIEGE, unique = true, recommended = true,
                    "Fire-lance siege against buildings and formations."),
                u("Bed Crossbow", SIEGE, unique = true, recommended = false,
                    "Long-range siege crossbow.")
            ),
            coreArmy = "Iron Pagoda + Mounted Grenadiers transitioning into Eruptors/Bed Crossbow siege. Raid early, storm late. Weak vs dense defensive infantry.",
            build = BuildOrder(
                opening = "Mobile raiding economy (mounted villagers + Emissary settlement captures) into cavalry pressure.",
                winCondition = "Snowball map control and economy into an overwhelming gunpowder-siege push."
            )
        ),
    )
}
