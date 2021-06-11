# adds proper transparent padding to a 32x32 banner image

from PIL import Image, ImageDraw, ImageFilter

background = Image.open('back.png')

'''
test = Image.open('img/11.png')
# test = test.resize((16, 16))
out = background.copy()
out.paste(test, (4, 20))
out.paste(test, (46, 20))  # todo: this should be mirrored left/right
out.save('out/test.png', quality=95)
'''

import os

all = []

directory_in_str = "img"
directory = os.fsencode(directory_in_str)

for file in os.listdir(directory):
    filename = os.fsdecode(file)
    if filename.endswith(".png"):
        read = Image.open('img/' + filename)
        out = background.copy()
        out.paste(read, (4, 20))
        out.paste(read, (46, 20))  # todo: this should be mirrored left/right
        out.save('out/' + filename, quality=95)
        all.append(filename.split(".")[0])
print(all)