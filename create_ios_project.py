import os
import re
import json
import xml.etree.ElementTree as ET
from PIL import Image

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
IOS_DIR = os.path.join(BASE_DIR, "ios")
EV_DUZENI_DIR = os.path.join(IOS_DIR, "EvDuzeni")

# Ensure subdirectories
subdirs = [
    "App",
    "Theme",
    "Models",
    "Services",
    "ViewModels",
    "Views",
    "Views/Today",
    "Views/Plan",
    "Views/Rooms",
    "Views/Settings",
    "Views/Onboarding",
    "Views/Components",
    "Resources",
    "Resources/Assets.xcassets",
    "Resources/Assets.xcassets/AppIcon.appiconset"
]

for sd in subdirs:
    os.makedirs(os.path.join(EV_DUZENI_DIR, sd), exist_ok=True)

print("Created iOS directories.")

# 1. Generate AppIcon (1024x1024)
icon_src = os.path.join(BASE_DIR, "PlayStore", "ikon-512.png")
icon_dst = os.path.join(EV_DUZENI_DIR, "Resources", "Assets.xcassets", "AppIcon.appiconset", "icon-1024.png")
if os.path.exists(icon_src):
    img = Image.open(icon_src)
    img_1024 = img.resize((1024, 1024), Image.Resampling.LANCZOS)
    img_1024.save(icon_dst, "PNG")
    print("Generated 1024x1024 AppIcon.")

# AppIcon Contents.json
appicon_contents = {
    "images": [
        {
            "filename": "icon-1024.png",
            "idiom": "universal",
            "platform": "ios",
            "size": "1024x1024"
        }
    ],
    "info": {
        "author": "xcode",
        "version": 1
    }
}
with open(os.path.join(EV_DUZENI_DIR, "Resources", "Assets.xcassets", "AppIcon.appiconset", "Contents.json"), "w", encoding="utf-8") as f:
    json.dump(appicon_contents, f, indent=2)

# Assets.xcassets Contents.json
xcassets_contents = {
    "info": {
        "author": "xcode",
        "version": 1
    }
}
with open(os.path.join(EV_DUZENI_DIR, "Resources", "Assets.xcassets", "Contents.json"), "w", encoding="utf-8") as f:
    json.dump(xcassets_contents, f, indent=2)

# 2. Localizations (parse all Android strings.xml -> iOS Localizable.strings)
LANG_MAP = {
    "values": "en",
    "values-tr": "tr",
    "values-ar": "ar",
    "values-de": "de",
    "values-es": "es",
    "values-fil": "fil",
    "values-fr": "fr",
    "values-hi": "hi",
    "values-id": "id",
    "values-in": "id",
    "values-it": "it",
    "values-ja": "ja",
    "values-ko": "ko",
    "values-nl": "nl",
    "values-pl": "pl",
    "values-pt": "pt-PT",
    "values-ru": "ru",
    "values-th": "th",
    "values-tl": "fil",
    "values-uk": "uk",
    "values-ur": "ur",
    "values-vi": "vi",
    "values-zh": "zh-Hans",
    "values-zh-rTW": "zh-Hant"
}

def clean_android_string(val):
    if not val:
        return ""
    # Android escape cleanups
    val = val.replace(r"\'", "'").replace(r'\"', '"')
    val = val.replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
    # Format specifiers
    val = re.sub(r"%(\d+)\$d", r"%d", val)
    val = re.sub(r"%(\d+)\$s", r"%@", val)
    val = val.replace("%s", "%@")
    # Escape quotes for .strings format
    val = val.replace('"', r'\"')
    return val

res_dir = os.path.join(BASE_DIR, "app", "src", "main", "res")
processed_locales = set()

for dname, ios_locale in LANG_MAP.items():
    if ios_locale in processed_locales and dname in ["values-in", "values-tl"]:
        continue
    xml_path = os.path.join(res_dir, dname, "strings.xml")
    if not os.path.exists(xml_path):
        continue

    lproj_dir = os.path.join(EV_DUZENI_DIR, "Resources", f"{ios_locale}.lproj")
    os.makedirs(lproj_dir, exist_ok=True)
    strings_file = os.path.join(lproj_dir, "Localizable.strings")

    try:
        tree = ET.parse(xml_path)
        root = tree.getroot()
        lines = []
        for elem in root.findall("string"):
            name = elem.get("name")
            text = elem.text or ""
            # Also handle inline tags if any
            if len(elem) > 0:
                text = ET.tostring(elem, encoding="unicode", method="text")
            cleaned = clean_android_string(text.strip())
            lines.append(f'"{name}" = "{cleaned}";\n')
        
        with open(strings_file, "w", encoding="utf-8") as sf:
            sf.writelines(lines)
        processed_locales.add(ios_locale)
        print(f"Generated {ios_locale}.lproj ({len(lines)} keys)")
    except Exception as e:
        print(f"Error parsing {xml_path}: {e}")

print(f"Total locales processed: {len(processed_locales)}")
