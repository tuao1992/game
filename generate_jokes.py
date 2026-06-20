"""Generate joke candidates and write them to jokes.txt.

Usage: python3 generate_jokes.py [count]   (default 500)

The combinatorial generators give us volume; the hand-written seed list is
where the genuinely good ones live, and the top 5 are curated by hand from
there. Multi-variable templates keep the unique space well above 10k so we
can produce that many *distinct* jokes.
"""

import sys
import random

random.seed(7)

# --- Hand-written seed jokes (the strong material) -------------------------
SEEDS = [
    "I told my wife she was drawing her eyebrows too high. She looked surprised.",
    "I bought the world's worst thesaurus yesterday. Not only is it terrible, it's terrible.",
    "I entered ten puns in a contest hoping one would win. Sadly, no pun in ten did.",
    "Parallel lines have so much in common. It's a shame they'll never meet.",
    "I have a fear of speed bumps, but I'm slowly getting over it.",
    "My therapist says I have a preoccupation with revenge. We'll see about that.",
    "I'm reading a book about anti-gravity. It's impossible to put down.",
    "I used to hate facial hair, but then it grew on me.",
    "Why don't scientists trust atoms? Because they make up everything.",
    "I'm on a seafood diet. I see food and I eat it.",
    "Why did the scarecrow win an award? He was outstanding in his field.",
    "Autocorrect can go straight to he'll.",
    "I told a chemistry joke but got no reaction.",
    "What do you call a fish with no eyes? A fsh.",
    "Why don't skeletons fight each other? They don't have the guts.",
    "I named my dog 'Five Miles' so I can tell people I walk Five Miles every day.",
    "A book just fell on my head. I've only got my shelf to blame.",
    "Time flies like an arrow. Fruit flies like a banana.",
    "Did you hear about the claustrophobic astronaut? He just needed a little space.",
    "I would avoid the sushi if I was you. It's a little fishy.",
    "Singing in the shower is fun until you get soap in your mouth. Then it's a soap opera.",
    "My boss told me to have a good day, so I went home.",
    "What's the best thing about Switzerland? I don't know, but the flag is a big plus.",
    "I couldn't figure out why the baseball kept getting larger. Then it hit me.",
    "Why did the bicycle fall over? It was two-tired.",
]

# --- Word pools (widened so the unique space exceeds 10k) ------------------
ANIMALS = ["cow", "duck", "octopus", "penguin", "sloth", "llama", "shark",
           "owl", "frog", "crab", "moth", "goat", "bee", "snail", "wolf",
           "otter", "raccoon", "hedgehog", "walrus", "gecko", "ferret",
           "badger", "lobster", "pelican", "mongoose", "narwhal", "hamster",
           "beaver", "jaguar", "koala"]
PROFESSIONS = ["accountant", "wizard", "barista", "plumber", "philosopher",
               "DJ", "pirate", "librarian", "ghost", "lawyer", "clown",
               "scientist", "chef", "lifeguard", "magician", "electrician",
               "beekeeper", "sculptor", "referee", "astronaut", "locksmith",
               "florist", "banker", "surgeon", "juggler", "archaeologist",
               "cartographer", "blacksmith", "mime", "auctioneer"]
ADJ = ["sad", "sneaky", "lazy", "ambitious", "tiny", "enormous", "polite",
       "suspicious", "broke", "anxious", "confident", "retired", "sleepy",
       "dramatic", "frugal", "reckless", "wholesome", "grumpy", "optimistic",
       "nervous", "smug", "clumsy", "fearless", "restless", "sarcastic"]
TOPICS = ["math", "the ocean", "Mondays", "WiFi", "gravity", "coffee",
          "the gym", "taxes", "cheese", "the moon", "ghosts", "Bluetooth",
          "jazz", "traffic", "deadlines", "weather", "spreadsheets",
          "parking", "laundry", "leftovers", "alarms", "dial-up",
          "daylight savings", "group chats", "small talk"]
PLACES = ["library", "bakery", "gym", "courtroom", "spaceship", "aquarium",
          "lighthouse", "laundromat", "observatory", "dentist's office",
          "casino", "bowling alley", "art gallery", "submarine", "ski lodge",
          "planetarium", "taco truck", "hardware store", "opera house",
          "escape room"]
ACTIVITIES = ["yodeling", "competitive napping", "interpretive dance",
              "sourdough baking", "parkour", "beekeeping", "juggling",
              "speed chess", "ice sculpting", "the kazoo", "sword swallowing",
              "tightrope walking", "extreme couponing", "birdwatching",
              "ghost hunting", "miming", "taxidermy", "competitive whistling",
              "foraging", "fencing", "pottery", "breakdancing", "falconry",
              "stand-up comedy", "geocaching"]
RELATIVES = ["uncle", "grandma", "cousin", "nephew", "mother-in-law",
             "stepdad", "great-aunt", "second cousin", "brother", "niece",
             "godfather", "roommate"]


def why_did(_):
    return (f"Why did the {random.choice(ADJ)} {random.choice(ANIMALS)} "
            f"become a {random.choice(PROFESSIONS)}? It finally found its calling.")


def walked_into(_):
    return (f"A {random.choice(ADJ)} {random.choice(ANIMALS)} walked into a "
            f"{random.choice(PLACES)}. The {random.choice(PROFESSIONS)} "
            f"didn't even look up.")


def relative_tried(_):
    return (f"My {random.choice(RELATIVES)} took up {random.choice(ACTIVITIES)} "
            f"to impress a {random.choice(PROFESSIONS)}. Bold strategy, honestly.")


def two_things(_):
    x = random.choice(TOPICS)
    y = random.choice(TOPICS)
    while y == x:
        y = random.choice(TOPICS)
    return f"{x.capitalize()} and {y} have one thing in common: nobody asked."


GENERATORS = [why_did, walked_into, relative_tried, two_things]


def main():
    count = int(sys.argv[1]) if len(sys.argv) > 1 else 500

    jokes = list(SEEDS)[:count]
    seen = set(j.lower() for j in jokes)
    attempts = 0
    cap = count * 50  # generous headroom; unique space is far larger than 10k
    while len(jokes) < count and attempts < cap:
        attempts += 1
        joke = random.choice(GENERATORS)(None)
        key = joke.lower()
        if key not in seen:
            seen.add(key)
            jokes.append(joke)

    with open("jokes.txt", "w") as f:
        for i, joke in enumerate(jokes, 1):
            f.write(f"{i:05d}. {joke}\n")

    print(f"Generated {len(jokes)} jokes -> jokes.txt (after {attempts} draws)")


if __name__ == "__main__":
    main()
