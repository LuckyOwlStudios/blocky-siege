#!/usr/bin/env python3
"""Generates the trimmed item models (common/src/main/resources/assets/blockysiege/models/item/<item>_<material>_trim.json).

item/generated draws every layer with the same extruded faces, so a trim overlay z-fights with the base layer
when held or dropped. These models spell out the base layer's extrusion themselves and draw the overlay as
two flat quads sitting just in front of and behind the base slab. Run from the repo root.
"""
import json
from PIL import Image

MATERIALS = ["quartz", "iron", "netherite", "redstone", "copper", "gold", "emerald", "diamond", "lapis", "amethyst"]
ITEMS = {"cannon": "cannon_trim", "multi_cannon": "burst_cannon_trim", "spewer_cannon": "spewer_trim"}
RES = "common/src/main/resources/assets/blockysiege/"
DISPLAY = {
    "ground": {"rotation": [0, 0, 0], "translation": [0, 2, 0], "scale": [0.5, 0.5, 0.5]},
    "head": {"rotation": [0, 180, 0], "translation": [0, 13, 7], "scale": [1, 1, 1]},
    "thirdperson_righthand": {"rotation": [0, 0, 0], "translation": [0, 3, 1], "scale": [0.55, 0.55, 0.55]},
    "firstperson_righthand": {"rotation": [0, -90, 25], "translation": [1.13, 3.2, 1.13], "scale": [0.68, 0.68, 0.68]},
    "fixed": {"rotation": [0, 180, 0], "translation": [0, 0, 0], "scale": [1, 1, 1]},
}


def base_elements(item):
    img = Image.open(RES + f"textures/item/{item}.png").convert("RGBA")
    w, h = img.size
    solid = lambda x, y: 0 <= x < w and 0 <= y < h and img.getpixel((x, y))[3] > 0
    full = {"uv": [0, 0, 16, 16], "texture": "#layer0"}
    els = [{"from": [0, 0, 7.5], "to": [16, 16, 8.5], "faces": {"north": dict(full), "south": dict(full)}}]
    for y in range(h):
        for x in range(w):
            if not solid(x, y):
                continue
            uv = {"uv": [x, y, x + 1, y + 1], "texture": "#layer0"}
            if not solid(x, y - 1):
                els.append({"from": [x, 16 - y, 7.5], "to": [x + 1, 16 - y, 8.5], "faces": {"up": dict(uv)}})
            if not solid(x, y + 1):
                els.append({"from": [x, 15 - y, 7.5], "to": [x + 1, 15 - y, 8.5], "faces": {"down": dict(uv)}})
            if not solid(x - 1, y):
                els.append({"from": [x, 15 - y, 7.5], "to": [x, 16 - y, 8.5], "faces": {"west": dict(uv)}})
            if not solid(x + 1, y):
                els.append({"from": [x + 1, 15 - y, 7.5], "to": [x + 1, 16 - y, 8.5], "faces": {"east": dict(uv)}})
    return els


for item, overlay in ITEMS.items():
    base = base_elements(item)
    for mat in MATERIALS:
        flat = {"uv": [0, 0, 16, 16], "texture": "#layer1"}
        model = {
            "gui_light": "front",
            "textures": {"layer0": f"blockysiege:item/{item}", "layer1": f"blockysiege:trims/{overlay}_{mat}", "particle": f"blockysiege:item/{item}"},
            "elements": base + [
                {"from": [0, 0, 7.45], "to": [16, 16, 7.45], "faces": {"north": dict(flat)}},
                {"from": [0, 0, 8.55], "to": [16, 16, 8.55], "faces": {"south": dict(flat)}},
            ],
            "display": DISPLAY,
        }
        with open(RES + f"models/item/{item}_{mat}_trim.json", "w") as f:
            json.dump(model, f, separators=(",", ":"))
