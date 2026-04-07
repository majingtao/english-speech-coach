#!/usr/bin/env python3
"""Parse Flyers word list text into flyers-wordlist.json"""

import json
import re
import os

RAW_TEXT = r"""A
a.m. (for time)
across prep
act v
actor n
actually adv
adventure n
after adv + conj
ago adv
agree v
air n
airport n
alone adj
already adv
also adv
amazing adj + excl
ambulance n
anyone pron
anything pron
anywhere adv
appear v
April n
arrive v
art n
artist n
as adv
as ... as adv
astronaut n
at the moment adv
August n
autumn (US fall) n
away adv
B
backpack (UK rucksack) n
bandage n
bank n
beetle n
before adv + conj
begin v
believe v
belt n
Betty n
bicycle n
bin n
biscuit (US cookie) n
bit n
bored adj
borrow v
bracelet n
break v
bridge n
broken adj
brush n + v
burn v
business n
businessman/woman n
butter n
butterfly n
by myself adv
by yourself adv
C
calendar n
camel n
camp v
card n
cartoon n
castle n
cave n
century n
cereal n
channel n
chat v
cheap adj
chemist('s) n
chess n
chopsticks n
club n
collect v
college n
comb n + v
competition n
concert n
conversation n
cooker n
cookie (UK biscuit) n
corner n
costume n
could (for possibility) v
creature n
crown n
cushion n
cut v
cycle v
D
dark adj
date (as in time) n
David n
dear (as in Dear Harry) adj
December n
decide v
deep adj
delicious adj
desert n
design n + v
designer n
diary n
dictionary n
dinosaur n
disappear v
drum n
during prep
E
each det + pron
eagle n
early adj + adv
Earth n
east n
elbow n
else adv
Emma n
empty adj
end v
engine n
engineer n
enormous adj
enough adj + pron
enter (a competition) v
entrance n
envelope n
environment n
ever adv
everywhere adv
excellent adj + excl
excited adj
exit n
expensive adj
explain v
explore v
extinct adj
F
factory n
fall (UK autumn) n
fall over v
far adj + adv
fast adj + adv
February n
feel v
festival n
fetch v
a few det
file (as in open and close a file) n
find out v
finger n
finish v
fire n
fire engine (US fire truck) n
fire fighter n
fire station n
flag n
flashlight (UK torch) n
flour n
fog n
foggy adj
follow v
for prep of time
forget v
fork n
Frank n
fridge n
friendly adj
frightening adj
front adj + n
full adj
fur n
furry adj
future n
G
gate n
geography n
George n
get to v
glass adj
glove n
glue n + v
go away! excl
go out v
gold adj + n
golf n
group n
guess n + v
gym n
H
half adj + n
happen v
hard adj + adv
Harry n
hate v
hear v
heavy adj
Helen n
high adj
hill n
history n
hole n
Holly n
honey n
hope v
horrible adj
hotel n
hour n
how long adv + int
hurry v
husband n
I
if conj
if you want! excl
important adj
improve v
in a minute! excl
information n
insect n
instead adv
instrument n
interested adj
interesting adj
invent v
invitation n
J
jam n
January n
job n
join (a club) v
journalist n
journey n
July n
June n
just adv
K
Katy n
keep v
key n
kilometre (US kilometer) n
kind adj
king n
knee n
knife n
L
land v
language n
large adj
late adj + adv
later adv
lazy adj
leave v
left (as in direction) adj + n
let v
letter (as in mail) n
lie (as in lie down) v
lift (ride) n
lift v
light adj + n
a little adv + det
London n
look after v
look like v
lovely adj
low adj
lucky adj
M
magazine n
make sure v
manager n
March n
married adj
match (football) n
maths (US math) n
May n
may v
meal n
mechanic n
medicine n
meet v
meeting n
member n
metal adj + n
Michael n
midday n
middle n + adj
midnight n
might v
million n
mind v
minute n
missing adj
mix v
money n
month n
motorway n
much adv + det + pron
museum n
N
necklace n
nest n
news n
newspaper n
next adj + adv
no problem! excl
noisy adj
no-one pron
north n
November n
nowhere adv
O
ocean n
October n
octopus n
of course adv
office n
Oliver n
olives n
once adv
online adj
other det + pron
oven n
over adv + prep
P
p.m. (for time)
pajamas (UK pyjamas) n
passenger n
past n + prep
path n
pepper n
perhaps adv
photographer n
piece n
pilot n
pizza n
planet n
plastic adj + n
platform n
pleased adj
pocket n
police officer n
police station n
pond n
poor adj
pop music n
popular adj
post v
post office n
postcard n
prefer v
prepare v
prize n
problem n
programme (US program) n
project n
pull v
push v
puzzle n
pyjamas (US pajamas) n
pyramid n
Q
quarter n
queen n
quite adv
quiz n
R
race n + v
racing (car; bike) adj
railway n
ready adj
remember v
repair v
repeat v
restaurant n
rich adj
Richard n
right adj
right (as in direction) n
ring n
Robert n
rock music n
rocket n
rucksack (US backpack) n
S
salt n
same adj
Sarah n
save v
science n
scissors n
score n
screen n
search n + v
secret n
sell v
September n
several adj
shampoo n
shelf n
should v
silver adj + n
since prep
singer n
ski n + v
skyscraper n
sledge n + v
smell n + v
snack n
snowball n
snowboard n
snowboarding n
snowman n
so adv + conj
soap n
soft adj
somewhere adv
soon adv
Sophia n
sore adj
sound n + v
south n
space n
spaceship n
speak v
special adj
spend v
spoon n
spot n
spotted adj
spring n
stadium n
stage (theatre) n
stamp n
stay v
step n
still adv
stone n
storm n
straight on adv
strange adj
strawberry n
stream n
stripe n
striped adj
student n
study v
subject n
such det
suddenly adv
sugar n
suitcase n
summer n
sunglasses n
sure adj
surname n
surprise n
swan n
swing n + v
T
take (as in time e.g. it takes 20 minutes) v
taste n + v
taxi n
team n
telephone n
tent n
thank v
theatre (US theater) n
thousand n
through prep
tidy adj + v
time n
timetable n
toe n
together adv
tomorrow adv + n
tonight adv + n
torch (US flashlight) n
tortoise n
touch v
tour n
traffic n
trainers n
tune n
turn v
turn off v
turn on v
twice adv
tyre (US tire) n
U
umbrella n
unfriendly adj
unhappy adj
uniform n
university n
unkind adj
untidy adj
until prep
unusual adj
use v
usually adv
V
view n
violin n
visit v
volleyball n
W
waiter n
warm adj
way n
west n
wheel n
while conj
whisper v
whistle v
wife n
wifi n
wild adj
will v
William n
win n
wing n
winner n
winter n
wish n + v
without prep
wonderful adj
wood n
wool n
worried adj
X
x-ray n
Y
yet adv
yoghurt n
you're welcome! excl
Z
zero n
"""

EXCLUDE_NAMES = {
    "Betty", "David", "Emma", "Frank", "George", "Harry", "Helen",
    "Holly", "Katy", "Michael", "Oliver", "Richard", "Robert",
    "Sarah", "Sophia", "William"
}

# Also exclude place names that are just "n"
EXCLUDE_PLACES = {"London"}

THEMES = {
    "Meet the Flyers": ["dictionary", "letter", "flag", "stripe", "umbrella", "sunglasses", "ring", "necklace", "gold", "uniform", "glove", "metal", "key", "pocket", "silver", "rucksack", "bicycle", "telephone", "backpack", "belt"],
    "Autumn/Fall": ["salt", "pepper", "meal", "honey", "jam", "burn", "piece", "pizza", "medicine", "fall over", "cut", "fridge", "chemist", "flour"],
    "Flyers Fun Day": ["concert", "singer", "drum", "crown", "octopus", "violin", "instrument", "king", "queen", "stage"],
    "Winter": ["snowman", "snowboarding", "chess", "married", "channel", "cartoon"],
    "Flyers Party": ["invitation", "costume", "cushion", "prize", "butterfly", "bracelet"],
    "Spring": ["environment", "nest", "beetle", "insect", "swan", "stream", "pond", "eagle", "fur", "wing"],
    "Flyers Adventure": ["adventure", "castle", "cave", "desert", "dinosaur", "extinct", "pyramid", "spaceship", "planet", "rocket"],
    "Summer": ["hotel", "passenger", "suitcase", "platform", "railway", "ticket", "tour", "pilot"],
    "Exam Day": ["timetable", "score", "problem", "quiz", "puzzle"],
}

POS_TAGS = {"n", "v", "adj", "adv", "prep", "conj", "det", "pron", "int", "excl"}


def parse_line(line, current_letter):
    """Parse a single word list line. Returns dict or None."""
    line = line.strip()
    if not line:
        return None

    # Single uppercase letter = section header
    if re.match(r'^[A-Z]$', line):
        return {"_letter": line}

    # Skip "Grammatical key" or page numbers
    if line.startswith("Grammatical key") or re.match(r'^\d+$', line):
        return None

    variant = None
    context = None
    word = None
    pos_str = ""

    # Extract variant in parentheses like (UK rucksack) or (US fall)
    variant_match = re.search(r'\((UK|US)\s+([^)]+)\)', line)
    if variant_match:
        variant_prefix = variant_match.group(1)
        variant_word = variant_match.group(2)
        variant = f"{variant_prefix} {variant_word}"
        line = line[:variant_match.start()] + line[variant_match.end():]
        line = re.sub(r'\s+', ' ', line).strip()

    # Extract context in parentheses like (as in time), (for possibility), (football), etc.
    context_match = re.search(r'\(([^)]+)\)', line)
    if context_match:
        context = context_match.group(1)
        line = line[:context_match.start()] + line[context_match.end():]
        line = re.sub(r'\s+', ' ', line).strip()

    # Now extract POS from the end
    # POS tokens are at the end, possibly joined with +
    # e.g. "across prep", "brush n + v", "amazing adj + excl"
    # Also handle "prep of time" -> just "prep"
    # Split from right, collecting POS tokens
    tokens = line.split()

    # Find where POS starts from the right
    pos_parts = []
    i = len(tokens) - 1
    while i >= 0:
        t = tokens[i]
        if t == '+':
            i -= 1
            continue
        if t in POS_TAGS:
            pos_parts.insert(0, t)
            i -= 1
        elif t == "of" and i + 1 < len(tokens) and tokens[i + 1] == "time":
            # "for prep of time" -> skip "of time"
            i -= 1
        elif t == "time" and i > 0 and tokens[i - 1] == "of":
            i -= 1
            continue
        else:
            break

    if pos_parts:
        word = ' '.join(tokens[:i + 1])
        pos_str = ', '.join(pos_parts)
    else:
        word = line
        pos_str = ""

    if not word:
        return None

    # Check exclusions
    if word in EXCLUDE_NAMES and pos_str == "n":
        return None
    if word in EXCLUDE_PLACES and pos_str == "n":
        return None

    # Handle chemist('s) -> chemist's
    word = word.replace("('s)", "'s")

    entry = {"word": word, "pos": pos_str, "letter": current_letter}
    if variant:
        entry["variant"] = variant
    return entry


def main():
    lines = RAW_TEXT.strip().split('\n')
    words = []
    current_letter = ""

    for line in lines:
        result = parse_line(line, current_letter)
        if result is None:
            continue
        if "_letter" in result:
            current_letter = result["_letter"]
            continue
        words.append(result)

    output = {
        "words": words,
        "themes": THEMES,
    }

    out_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "flyers-wordlist.json")
    with open(out_path, 'w', encoding='utf-8') as f:
        json.dump(output, f, indent=2, ensure_ascii=False)

    print(f"Wrote {len(words)} words to {out_path}")
    # Print some stats
    letters = {}
    for w in words:
        letters[w["letter"]] = letters.get(w["letter"], 0) + 1
    for letter in sorted(letters):
        print(f"  {letter}: {letters[letter]} words")
    print(f"  Themes: {len(THEMES)}")
    variants = [w for w in words if "variant" in w]
    print(f"  Words with variants: {len(variants)}")


if __name__ == "__main__":
    main()
