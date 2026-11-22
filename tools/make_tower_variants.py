#!/usr/bin/env python3
"""
Builds the village crossbow tower structures from the hand-built plains tower.

Reads  <dir>/crossbow_tower_plains.nbt  and rewrites it, plus writes the taiga, snowy, desert and savanna
variants next to it by swapping the wood and stone for each biome. The golem block gets its crossbow and
arrows and a SpawnGolem flag, so the golem is created when the chunk loads (worldgen does not run onPlace).

Usage: make_tower_variants.py <structure/village dir>      (needs: pip install nbtlib)
"""
import sys, copy
from pathlib import Path
import nbtlib
from nbtlib import Compound, List, String, Int, Byte

WOODS = {'taiga': 'spruce', 'snowy': 'spruce', 'savanna': 'acacia'}

def wood_map(wood):
    return {'minecraft:oak_' + k: 'minecraft:%s_%s' % (wood, k) for k in ('log', 'planks', 'stairs', 'slab', 'fence')}

SANDSTONE = {
    'minecraft:oak_log': 'minecraft:cut_sandstone', 'minecraft:oak_planks': 'minecraft:smooth_sandstone',
    'minecraft:oak_stairs': 'minecraft:smooth_sandstone_stairs', 'minecraft:oak_slab': 'minecraft:cut_sandstone_slab',
    'minecraft:cobblestone': 'minecraft:sandstone', 'minecraft:mossy_cobblestone': 'minecraft:cut_sandstone',
    'minecraft:cobblestone_stairs': 'minecraft:sandstone_stairs',
}

def convert(root, mapping):
    for entry in root['palette']:
        name = str(entry['Name'])
        if name not in mapping:
            continue
        new = mapping[name]
        if name == 'minecraft:oak_log' and not new.endswith('_log') and 'Properties' in entry:
            del entry['Properties']  # cut sandstone has no axis
        entry['Name'] = String(new)
    return root

def desert_fences(root):
    """Fences become sandstone walls, which use none/low connections and an 'up' flag."""
    for entry in root['palette']:
        if str(entry['Name']) == 'minecraft:oak_fence':
            p = entry['Properties']
            walls = {k: String('low' if str(p[k]) == 'true' else 'none') for k in ('east', 'north', 'south', 'west')}
            entry['Name'] = String('minecraft:sandstone_wall')
            entry['Properties'] = Compound({**walls, 'up': String('true'), 'waterlogged': p['waterlogged']})

def arm_golem(root):
    for b in root['blocks']:
        if 'nbt' in b and str(b['nbt'].get('id')) == 'blockysiege:crossbow_golem_block_entity':
            def item(slot, id_, count):
                return Compound({'Slot': Byte(slot), 'id': String(id_), 'count': Int(count)})
            # The golem fires a random stack from its ammo slots, so give it a mix of arrow stacks.
            b['nbt']['inventory'] = Compound({'Size': Int(10), 'Items': List[Compound]([
                item(0, 'minecraft:arrow', 24), item(1, 'minecraft:arrow', 16), item(2, 'minecraft:arrow', 8),
                item(9, 'minecraft:crossbow', 1)])})
            b['nbt']['SpawnGolem'] = Byte(1)

def main(directory):
    d = Path(directory)
    base_path = d / 'crossbow_tower_plains.nbt'
    base = nbtlib.load(base_path)
    arm_golem(base)
    base.save(base_path, gzipped=True)
    for biome, wood in WOODS.items():
        convert(copy.deepcopy(base), wood_map(wood)).save(d / ('crossbow_tower_%s.nbt' % biome), gzipped=True)
    desert = convert(copy.deepcopy(base), SANDSTONE)
    desert_fences(desert)
    desert.save(d / 'crossbow_tower_desert.nbt', gzipped=True)

if __name__ == '__main__':
    main(sys.argv[1])
