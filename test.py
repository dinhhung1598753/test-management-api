# Open images and ensure RGB
from PIL import Image

im1 = Image.open("./images/answer_sheets/handle-exam-class1/u15.jpg").convert("RGB")
im2 = Image.open("./images/answer_sheets/handle-exam-class1/mdt.jpg").convert("RGB")
im3 = Image.open("./images/answer_sheets/handle-exam-class1/cauhoi1.jpg").convert("RGB")
im4 = Image.open("./images/answer_sheets/handle-exam-class1/cauhoi2.jpg").convert("RGB")
im5 = Image.open("./images/answer_sheets/handle-exam-class1/cauhoi3.jpg").convert("RGB")

# Paste im1 onto background
im1.paste(im2, (550, 0))
im1.paste(im3, (75, 510))
im1.paste(im4, (380, 510))
im1.paste(im5, (690, 510))
im1.save("anhghep.jpg")
