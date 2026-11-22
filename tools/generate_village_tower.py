#!/usr/bin/env python3
"""Generates the village crossbow tower structure (1.21.1 .nbt). Usage: generate_village_tower.py <out.nbt>"""
import gzip, struct, sys

# --- minimal NBT writer -------------------------------------------------
def s(v): b = v.encode(); return struct.pack('>H', len(b)) + b
class T:  # (tag id, payload)
    def __init__(self, i, p): self.i, self.p = i, p
def Int(v): return T(3, struct.pack('>i', v))
def Byte(v): return T(1, struct.pack('>b', v))
def Str(v): return T(8, s(v))
def List(tid, items): return T(9, bytes([tid]) + struct.pack('>i', len(items)) + b''.join(x.p for x in items))
def Comp(d): return T(10, b''.join(bytes([v.i]) + s(k) + v.p for k, v in d.items()) + b'\x00')

DATA_VERSION = 3955  # 1.21.1
W = D = 7
H = 14
blocks = {}  # (x,y,z) -> (name, props, nbt)
def put(x, y, z, name, props=None, nbt=None): blocks[(x, y, z)] = (name, props or {}, nbt)
def fill(x0, y0, z0, x1, y1, z1, name, props=None):
    for x in range(x0, x1 + 1):
        for y in range(y0, y1 + 1):
            for z in range(z0, z1 + 1):
                put(x, y, z, name, props)

COBBLE, MOSSY = 'minecraft:cobblestone', 'minecraft:mossy_cobblestone'
LOG, PLANKS = 'minecraft:oak_log', 'minecraft:oak_planks'
AIR = 'minecraft:air'
FRONT = 6  # the doorway faces +z (south); the jigsaw sits on this edge

# Base: solid cobblestone foundation, then a hollow room with a front archway.
fill(0, 0, 0, 6, 0, 6, COBBLE)
for y in (1, 2):
    fill(0, y, 0, 6, y, 6, COBBLE)
    fill(1, y, 1, 5, y, 5, AIR)
put(3, 2, FRONT, AIR)
put(3, 1, FRONT, 'minecraft:jigsaw', {'orientation': 'south_up'}, Comp({
    'id': Str('minecraft:jigsaw'), 'name': Str('minecraft:building_entrance'),
    'target': Str('minecraft:building_entrance'), 'pool': Str('minecraft:empty'),
    'joint': Str('rollable'), 'final_state': Str('minecraft:air')}))
# stair-step the base corners and weather it a little
for (x, z) in ((0, 0), (6, 0), (0, 6), (6, 6)):
    put(x, 2, z, MOSSY)
for (x, y, z) in ((1, 0, 6), (5, 0, 0), (0, 1, 3), (6, 1, 2), (2, 2, 0)):
    put(x, y, z, MOSSY)
fill(0, 3, 0, 6, 3, 6, COBBLE)

# Tower shaft: oak log corners, plank walls, hollow, a window on each side.
for y in range(4, 9):
    fill(1, y, 1, 5, y, 5, PLANKS)
    fill(2, y, 2, 4, y, 4, AIR)
    for (x, z) in ((1, 1), (5, 1), (1, 5), (5, 5)):
        put(x, y, z, LOG, {'axis': 'y'})
for (x, z) in ((3, 1), (3, 5), (1, 3), (5, 3)):
    put(x, 6, z, AIR)
# Cornice: upside-down stairs under a plank platform
fill(1, 9, 1, 5, 9, 5, PLANKS)
for x in range(0, 7):
    for z in range(0, 7):
        edge = x in (0, 6) or z in (0, 6)
        if not edge:
            continue
        if x == 0: f = 'east'
        elif x == 6: f = 'west'
        elif z == 0: f = 'south'
        else: f = 'north'
        if (x in (0, 6)) and (z in (0, 6)):
            continue
        put(x, 9, z, 'minecraft:oak_stairs', {'facing': f, 'half': 'top', 'shape': 'straight', 'waterlogged': 'false'})
# Upper turret: a 3x3 shaft of logs and planks, then a plank deck with the golem block in the middle.
fill(2, 10, 2, 4, 11, 4, PLANKS)
for y in (10, 11):
    for (x, z) in ((2, 2), (4, 2), (2, 4), (4, 4)):
        put(x, y, z, LOG, {'axis': 'y'})
fill(2, 12, 2, 4, 12, 4, PLANKS)
# The golem block holds a crossbow and arrows; its golem is spawned when the chunk loads (SpawnGolem).
inv = Comp({'Size': Int(10), 'Items': List(10, [
    Comp({'Slot': Byte(0), 'id': Str('minecraft:arrow'), 'count': Int(24)}),
    Comp({'Slot': Byte(9), 'id': Str('minecraft:crossbow'), 'count': Int(1)})])})
put(3, 12, 3, 'blockysiege:crossbow_golem_block', {}, Comp({
    'id': Str('blockysiege:crossbow_golem_block_entity'), 'inventory': inv, 'SpawnGolem': Byte(1)}))
# Railing posts at the deck corners; the golem stands in the open centre.
for (x, z) in ((2, 2), (4, 2), (2, 4), (4, 4)):
    put(x, 13, z, 'minecraft:oak_fence', {'east': 'false', 'north': 'false', 'south': 'false', 'west': 'false', 'waterlogged': 'false'})
put(3, 13, 3, AIR)

palette, pindex = [], {}
out_blocks = []
for pos in sorted(blocks):
    name, props, nbt = blocks[pos]
    key = (name, tuple(sorted(props.items())))
    if key not in pindex:
        pindex[key] = len(palette)
        e = {'Name': Str(name)}
        if props:
            e['Properties'] = Comp({k: Str(v) for k, v in props.items()})
        palette.append(Comp(e))
    b = {'pos': List(3, [Int(c) for c in pos]), 'state': Int(pindex[key])}
    if nbt:
        b['nbt'] = nbt
    out_blocks.append(Comp(b))

root = Comp({'DataVersion': Int(DATA_VERSION),
             'size': List(3, [Int(W), Int(H), Int(D)]),
             'palette': List(10, palette), 'blocks': List(10, out_blocks),
             'entities': List(10, [])})
with gzip.open(sys.argv[1], 'wb') as f:
    f.write(b'\x0a' + s('') + root.p)
print(len(blocks), 'blocks')
