"""Generate 500 joke candidates and write them to jokes.txt.

The combinatorial generators give us volume ("do this 500 times"); the
hand-written seed list is where the genuinely good ones live, and the top 5
are curated by hand from there.
"""

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

# --- Combinatorial generators (the volume) ---------------------------------
ANIMALS = ["cow", "duck", "octopus", "penguin", "sloth", "llama", "shark",
           "owl", "frog", "crab", "moth", "goat", "bee", "snail", "wolf"]
PROFESSIONS = ["accountant", "wizard", "barista", "plumber", "philosopher",
               "DJ", "pirate", "librarian", "ghost", "lawyer", "clown",
               "scientist", "chef", "lifeguard", "magician"]
ADJ = ["sad", "sneaky", "lazy", "ambitious", "tiny", "enormous", "polite",
       "suspicious", "broke", "anxious", "confident", "retired"]
TOPICS = ["math", "the ocean", "Mondays", "WiFi", "gravity", "coffee",
          "the gym", "taxes", "cheese", "the moon", "ghosts", "Bluetooth"]


def why_did(_):
    a = random.choice(ANIMALS)
    p = random.choice(PROFESSIONS)
    return f"Why did the {a} become a {p}? It finally found its calling."


def call_a(_):
    adj = random.choice(ADJ)
    a = random.choice(ANIMALS)
    return f"What do you call a {adj} {a}? A {adj}-case scenario."


def two_things(_):
    x = random.choice(TOPICS)
    y = random.choice(TOPICS)
    while y == x:
        y = random.choice(TOPICS)
    return f"{x.capitalize()} and {y} have one thing in common: nobody asked."


def knock(_):
    p = random.choice(PROFESSIONS)
    return f"Knock knock. Who's there? A {p}. A {p} who? Exactly, nobody remembers."


GENERATORS = [why_did, call_a, two_things, knock]


def main():
    jokes = list(SEEDS)
    seen = set(j.lower() for j in jokes)
    attempts = 0
    while len(jokes) < 500 and attempts < 20000:
        attempts += 1
        joke = random.choice(GENERATORS)(None)
        key = joke.lower()
        if key not in seen:
            seen.add(key)
            jokes.append(joke)

    with open("jokes.txt", "w") as f:
        for i, joke in enumerate(jokes, 1):
            f.write(f"{i:03d}. {joke}\n")

    print(f"Generated {len(jokes)} jokes -> jokes.txt")


if __name__ == "__main__":
    main()
