import sys
from PIL import Image

src_path = r"C:\Users\Lenovo\.gemini\antigravity-ide\brain\687bd2c6-57cc-413e-88ed-a0045bb29910\feature_graphic_en_1791528412155.jpg"
img = Image.open(src_path)

# Resize/crop to exact 1024x500
target_w, target_h = 1024, 500
img_ratio = img.width / img.height
target_ratio = target_w / target_h

if img_ratio > target_ratio:
    new_w = int(img.height * target_ratio)
    left = (img.width - new_w) // 2
    img = img.crop((left, 0, left + new_w, img.height))
else:
    new_h = int(img.width / target_ratio)
    top = (img.height - new_h) // 2
    img = img.crop((0, top, img.width, top + new_h))

img = img.resize((target_w, target_h), Image.Resampling.LANCZOS)

out1 = r"C:\Users\Lenovo\Downloads\ozellik_grafigi_1024x500_en.png"
img.save(out1, "PNG")
print("Saved:", out1, img.size)
