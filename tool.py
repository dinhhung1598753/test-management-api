from PIL import Image
import os


# Get string class from number class
def getClass(argument):
    if argument == 0:
        return ""
    elif argument == 1:
        return "A"
    elif argument == 2:
        return "B"
    elif argument == 3:
        return "C"
    elif argument == 4:
        return "D"
    elif argument == 5:
        return "AB"
    elif argument == 6:
        return "AC"
    elif argument == 7:
        return "AD"
    elif argument == 8:
        return "BC"
    elif argument == 9:
        return "BD"
    elif argument == 10:
        return "CD"
    elif argument == 11:
        return "ABC"
    elif argument == 12:
        return "ABD"
    elif argument == 13:
        return "ACD"
    elif argument == 14:
        return "BCD"
    elif argument == 15:
        return "ACBD"
    elif argument == 16:
        return "0"
    elif argument == 17:
        return "1"
    elif argument == 18:
        return "2"
    elif argument == 19:
        return "3"
    elif argument == 20:
        return "4"
    elif argument == 21:
        return "5"
    elif argument == 22:
        return "6"
    elif argument == 23:
        return "7"
    elif argument == 24:
        return "8"
    elif argument == 25:
        return "9"
    else:
        return ""


# Remove label duplicate
def remove_elements_info(arr):
    result = []
    i = 0
    while i < len(arr):
        item = arr[i]
        result.append(item)
        j = i + 1
        while j < len(arr) and abs(item[0] - arr[j][0]) <= 5:
            if arr[j][5] > item[5]:
                result.pop()  # Loại bỏ phần tử đã thêm trước đó
                break
            j += 1
        i = j
    return result


def remove_elements_answer(arr):
    result = []
    i = 0
    while i < len(arr):
        item = arr[i]
        result.append(item)
        j = i + 1
        while j < len(arr) and abs(item[1] - arr[j][1]) <= 5:
            if arr[j][5] > item[5]:
                result.pop()  # Loại bỏ phần tử đã thêm trước đó
                break
            j += 1
        i = j
    return result


#  Handles drawing rectangles over circled answers
def getCoordinates(x1, y1, x2, y2, class1):
    point1 = x1
    point2 = y1
    point3 = y1
    point4 = y2
    if class1 == "":
        point1 = x1
        point2 = y1
        point3 = x1
        point4 = y1
    elif class1 == "A":
        point1 = x1
        point2 = y1
        point3 = x1 + int((x2 - x1) / 4) - 15
        point4 = y1 + int((y2 - y1))
    elif class1 == "B":
        point1 = x1 + 40
        point2 = y1
        point3 = x1 + int((x2 - x1) / 4) + 25
        point4 = y1 + int((y2 - y1))
    elif class1 == "C":
        point1 = x1 + 80
        point2 = y1
        point3 = x1 + int((x2 - x1) / 4) + 70
        point4 = y1 + int((y2 - y1))
    elif class1 == "D":
        point1 = x1 + 122
        point2 = y1
        point3 = x1 + int((x2 - x1) / 4) + 113
        point4 = y1 + int((y2 - y1))
    return point1, point2, point3, point4


def getCoordinatesInfo(x1, y1, x2, y2, class1):
    point1 = x1
    point2 = y1
    point3 = y1
    point4 = y2
    if class1 == "0":
        point1 = x1
        point2 = y1
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9)
    elif class1 == "1":
        point1 = x1
        point2 = y1 + 38
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38
    elif class1 == "2":
        point1 = x1
        point2 = y1 + 38 * 2
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 2
    elif class1 == "3":
        point1 = x1
        point2 = y1 + 38 * 3
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 3
    elif class1 == "4":
        point1 = x1
        point2 = y1 + 38 * 4
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 4
    elif class1 == "5":
        point1 = x1
        point2 = y1 + 38 * 5
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 5
    elif class1 == "6":
        point1 = x1
        point2 = y1 + 38 * 6
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 6
    elif class1 == "7":
        point1 = x1
        point2 = y1 + 38 * 7
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 7
    elif class1 == "8":
        point1 = x1
        point2 = y1 + 38 * 8
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 8
    elif class1 == "9":
        point1 = x1
        point2 = y1 + 38 * 9
        point3 = x2
        point4 = y1 + int((y2 - y1) / 9) + 38 * 9
    return point1, point2, point3, point4


#  Handle the number of questions is not fixed
def getParameterNumberAnwser(numberAnswer):
    naturalParts = numberAnswer // 20
    return naturalParts


def getRemainder(numberAnswer):
    remainder = numberAnswer % 20
    return remainder


def mergeImages(filename, numberAnswer, coord_array, args):
    if numberAnswer == 20 or numberAnswer == 40 or numberAnswer == 60:
        naturalParts = getParameterNumberAnwser(numberAnswer) - 1
    else:
        naturalParts = getParameterNumberAnwser(numberAnswer)
    filename_cut = filename.split(".")[0]
    im1 = Image.open(f"./images/answer_sheets/handle-{args.input}/{filename}").convert("RGB")
    im2 = Image.open(f"./images/answer_sheets/handle-{args.input}/thongtin-{filename_cut}.jpg").convert("RGB")
    im3 = Image.open(f"./images/answer_sheets/handle-{args.input}/cautraloi1-{filename_cut}.jpg").convert("RGB")

    im1.paste(im2, (550, 0))
    im1.paste(im3, coord_array[0])
    for i in range(1, naturalParts + 1):
        im_answer = Image.open(
            f"./images/answer_sheets/handle-{args.input}/cautraloi{i + 1}-{filename_cut}.jpg"
        ).convert("RGB")
        im1.paste(im_answer, coord_array[i])

    im1.save(f"./images/answer_sheets/handle-{args.input}/handle-{filename_cut}.jpg")
    os.remove(f"./images/answer_sheets/handle-{args.input}/{filename}")
    os.remove(f"./images/answer_sheets/handle-{args.input}/thongtin-{filename_cut}.jpg")
    for i in range(naturalParts + 1):
        os.remove(f"./images/answer_sheets/handle-{args.input}/cautraloi{i + 1}-{filename_cut}.jpg")
