import os
import gc
import cv2
import numpy as np

# import math
import torch

# import torch.nn as nn
import torchvision.transforms as torchvision_T
from torchvision.models.segmentation import deeplabv3_resnet50
from torchvision.models.segmentation import deeplabv3_mobilenet_v3_large
import imutils

# from math import ceil
# from collections import defaultdict
from ultralytics import YOLO
import argparse
import json
import time


device = torch.device("cuda" if torch.cuda.is_available() else "cpu")


# ============================================ CUT BACKGROUND =======================================


def order_points(pts):
    rect = np.zeros((4, 2), dtype="float32")
    pts = np.array(pts)
    s = pts.sum(axis=1)
    rect[0] = pts[np.argmin(s)]
    rect[2] = pts[np.argmax(s)]

    diff = np.diff(pts, axis=1)
    rect[1] = pts[np.argmin(diff)]
    rect[3] = pts[np.argmax(diff)]
    return rect.astype("int").tolist()


def find_dest(pts):
    (tl, tr, br, bl) = pts
    widthA = np.sqrt(((br[0] - bl[0]) ** 2) + ((br[1] - bl[1]) ** 2))
    widthB = np.sqrt(((tr[0] - tl[0]) ** 2) + ((tr[1] - tl[1]) ** 2))
    maxWidth = max(int(widthA), int(widthB))
    heightA = np.sqrt(((tr[0] - br[0]) ** 2) + ((tr[1] - br[1]) ** 2))
    heightB = np.sqrt(((tl[0] - bl[0]) ** 2) + ((tl[1] - bl[1]) ** 2))
    maxHeight = max(int(heightA), int(heightB))
    destination_corners = [[0, 0], [maxWidth, 0],
                           [maxWidth, maxHeight], [0, maxHeight]]
    return order_points(destination_corners)


def image_preproces_transforms(
    mean=(0.4611, 0.4359, 0.3905), std=(0.2193, 0.2150, 0.2109)
):
    common_transforms = torchvision_T.Compose(
        [
            torchvision_T.ToTensor(),
            torchvision_T.Normalize(mean, std),
        ]
    )

    return common_transforms


def load_model(num_classes=1, model_name="r50", checkpoint_path=None, device=None):
    if model_name == "mbv3":
        model = deeplabv3_mobilenet_v3_large(num_classes=num_classes)
    else:
        model = deeplabv3_resnet50(num_classes=num_classes)

    model.to(device)
    checkpoints = torch.load(checkpoint_path, map_location=device)
    model.load_state_dict(checkpoints, strict=False)
    model.eval()

    _ = model(torch.randn((2, 3, 384, 384)))

    return model


CHECKPOINT_MODEL_PATH = r"Model/model_r50_iou_mix_2C020.pth"

trained_model = load_model(
    num_classes=2,
    model_name="r50",
    checkpoint_path=CHECKPOINT_MODEL_PATH,
    device=device,
)
preprocess_transforms = image_preproces_transforms()


def extract(image_true=None, trained_model=None, image_size=384, BUFFER=10):
    global preprocess_transforms

    IMAGE_SIZE = image_size
    half = IMAGE_SIZE // 2

    imH, imW, C = image_true.shape

    image_model = cv2.resize(
        image_true, (IMAGE_SIZE, IMAGE_SIZE), interpolation=cv2.INTER_NEAREST
    )

    scale_x = imW / IMAGE_SIZE
    scale_y = imH / IMAGE_SIZE

    image_model = preprocess_transforms(image_model)
    image_model = torch.unsqueeze(image_model, dim=0)

    with torch.no_grad():
        out = trained_model(image_model)["out"].cpu()

    del image_model
    gc.collect()

    out = (
        torch.argmax(out, dim=1, keepdims=True)
        .permute(0, 2, 3, 1)[0]
        .numpy()
        .squeeze()
        .astype(np.int32)
    )
    r_H, r_W = out.shape

    _out_extended = np.zeros(
        (IMAGE_SIZE + r_H, IMAGE_SIZE + r_W), dtype=out.dtype)
    _out_extended[half: half + IMAGE_SIZE, half: half + IMAGE_SIZE] = out * 255
    out = _out_extended.copy()

    del _out_extended
    gc.collect()

    # Edge Detection.
    canny = cv2.Canny(out.astype(np.uint8), 225, 255)
    canny = cv2.dilate(canny, cv2.getStructuringElement(
        cv2.MORPH_ELLIPSE, (5, 5)))
    contours, _ = cv2.findContours(canny, cv2.RETR_LIST, cv2.CHAIN_APPROX_NONE)
    page = sorted(contours, key=cv2.contourArea, reverse=True)[0]

    # ==========================================
    epsilon = 0.02 * cv2.arcLength(page, True)
    corners = cv2.approxPolyDP(page, epsilon, True)

    corners = np.concatenate(corners).astype(np.float32)

    corners[:, 0] -= half
    corners[:, 1] -= half

    corners[:, 0] *= scale_x
    corners[:, 1] *= scale_y

    # check if corners are inside.
    # if not find smallest enclosing box, expand_image then extract document
    # else extract document

    if not (
        np.all(corners.min(axis=0) >= (0, 0))
        and np.all(corners.max(axis=0) <= (imW, imH))
    ):
        left_pad, top_pad, right_pad, bottom_pad = 0, 0, 0, 0

        rect = cv2.minAreaRect(corners.reshape((-1, 1, 2)))
        box = cv2.boxPoints(rect)
        box_corners = np.int32(box)
        #     box_corners = minimum_bounding_rectangle(corners)

        box_x_min = np.min(box_corners[:, 0])
        box_x_max = np.max(box_corners[:, 0])
        box_y_min = np.min(box_corners[:, 1])
        box_y_max = np.max(box_corners[:, 1])

        # Find corner point which doesn't satify the image constraint
        # and record the amount of shift required to make the box
        # corner satisfy the constraint
        if box_x_min <= 0:
            left_pad = abs(box_x_min) + BUFFER

        if box_x_max >= imW:
            right_pad = (box_x_max - imW) + BUFFER

        if box_y_min <= 0:
            top_pad = abs(box_y_min) + BUFFER

        if box_y_max >= imH:
            bottom_pad = (box_y_max - imH) + BUFFER

        # new image with additional zeros pixels
        image_extended = np.zeros(
            (top_pad + bottom_pad + imH, left_pad + right_pad + imW, C),
            dtype=image_true.dtype,
        )

        # adjust original image within the new 'image_extended'
        image_extended[
            top_pad: top_pad + imH, left_pad: left_pad + imW, :
        ] = image_true
        image_extended = image_extended.astype(np.float32)

        # shifting 'box_corners' the required amount
        box_corners[:, 0] += left_pad
        box_corners[:, 1] += top_pad

        corners = box_corners
        image_true = image_extended

    corners = sorted(corners.tolist())
    corners = order_points(corners)
    destination_corners = find_dest(corners)
    M = cv2.getPerspectiveTransform(
        np.float32(corners), np.float32(destination_corners)
    )

    final = cv2.warpPerspective(
        image_true,
        M,
        (destination_corners[2][0], destination_corners[2][1]),
        flags=cv2.INTER_LANCZOS4,
    )
    final = np.clip(final, a_min=0.0, a_max=255.0)

    return final


# ============================================ GET ANWSER =======================================


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


def remove_elements_info(arr):
    result = []
    i = 0
    while i < len(arr):
        item = arr[i]
        result.append(item)
        j = i + 1
        while j < len(arr) and abs(item[1] - arr[j][1]) <= 5:
            if arr[j][3] > item[3]:
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
        while j < len(arr) and abs(item[0] - arr[j][0]) <= 5:
            if arr[j][3] > item[3]:
                result.pop()  # Loại bỏ phần tử đã thêm trước đó
                break
            j += 1
        i = j
    return result


def get_x(s):
    return s[1][0]


def get_x_ver1(s):
    s = cv2.boundingRect(s)
    return s[0] * s[1]


def crop_image(img):
    gray_img = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray_img, (9, 9), 0)
    img_canny = cv2.Canny(blurred, 0, 20)
    cnts = cv2.findContours(
        img_canny.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    cnts = imutils.grab_contours(cnts)
    ans_blocks = []
    x_old, y_old, w_old, h_old = 0, 0, 0, 0
    if len(cnts) > 0:
        cnts = sorted(cnts, key=get_x_ver1)
        for i, c in enumerate(cnts):
            x_curr, y_curr, w_curr, h_curr = cv2.boundingRect(c)
            if w_curr * h_curr > 180000 and w_curr * h_curr < 280000 and h_curr > 750:
                # check overlap contours
                check_xy_min = x_curr * y_curr - x_old * y_old
                check_xy_max = (x_curr + w_curr) * (y_curr + h_curr) - (
                    x_old + w_old
                ) * (y_old + h_old)
                if len(ans_blocks) == 0:
                    ans_blocks.append(
                        (
                            gray_img[
                                y_curr: y_curr + h_curr, x_curr: x_curr + w_curr
                            ],
                            [x_curr, y_curr, w_curr, h_curr],
                        )
                    )
                    x_old = x_curr
                    y_old = y_curr
                    w_old = w_curr
                    h_old = h_curr
                elif check_xy_min > 20000 and check_xy_max > 20000:
                    ans_blocks.append(
                        (
                            gray_img[
                                y_curr: y_curr + h_curr, x_curr: x_curr + w_curr
                            ],
                            [x_curr, y_curr, w_curr, h_curr],
                        )
                    )
                    x_old = x_curr
                    y_old = y_curr
                    w_old = w_curr
                    h_old = h_curr
        sorted_ans_blocks = sorted(ans_blocks, key=get_x)
        sorted_ans_blocks_resize = []
        for i, sorted_ans_block in enumerate(sorted_ans_blocks):
            img2 = sorted_ans_block[0]
            height, width = img2.shape
            ratio = height / width
            new_height = 640
            new_width = int(new_height / ratio)
            img_resize = cv2.resize(
                img2, (new_width, new_height), interpolation=cv2.INTER_AREA
            )
            sorted_ans_blocks_resize.append(img_resize)

        return sorted_ans_blocks_resize


def predictAns(img, model):
    imProcess = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
    results = model.predict(imProcess)
    data = results[0].boxes.data
    list_label = []
    for i, data in enumerate(data):
        x1 = int(data[0])
        y1 = int(data[1])
        class1 = int(data[5])
        conf = float(data[4])
        conf = round(conf, 3)
        list_label.append((y1, x1, class1, conf))
    list_label = sorted(list_label, key=lambda x: x[0])
    list_label = remove_elements_answer(list_label)
    array_answer = []
    for i, answer in enumerate(list_label):
        class_answer = getClass(answer[2])
        array_answer.append(class_answer)
    return array_answer


# ============================================ GET INFO =======================================


def crop_image_info(img):
    left = 550
    top = 0
    right = 1056
    bottom = 500
    cropped_image = img[top:bottom, left:right]
    cropped_image = cv2.convertScaleAbs(cropped_image * 255)
    gray_img = cv2.cvtColor(cropped_image, cv2.COLOR_BGR2GRAY)
    img_resize = cv2.resize(gray_img, (640, 640), interpolation=cv2.INTER_AREA)
    # cv2.imshow("img_info", img_resize)
    # cv2.waitKey(0)
    return img_resize


def predictInfo(img, model):
    imProcess = cv2.cvtColor(img, cv2.COLOR_GRAY2BGR)
    results = model.predict(imProcess)
    data = results[0].boxes.data
    list_label = []
    for i, data in enumerate(data):
        x1 = int(data[0])
        y1 = int(data[1])
        class1 = int(data[5])
        conf = float(data[4])
        conf = round(conf, 3)
        list_label.append((y1, x1, class1, conf))

    list_label = sorted(list_label, key=lambda x: x[1])
    list_label = remove_elements_info(list_label)

    dict_info = {}
    for i, info in enumerate(list_label):
        class_info = getClass(info[2])
        dict_info[f"{i+1}"] = class_info
    if len(dict_info) > 15:
        class_code = "".join(list(dict_info.values())[:6])
        student_code = "".join(list(dict_info.values())[6:-3])
        exam_code = "".join(list(dict_info.values())[-3:])
        result_info = {
            "class_code": class_code,
            "student_code": student_code,
            "exam_code": exam_code,
        }
    elif len(dict_info) < 12:
        student_code = "".join(list(dict_info.values())[:6])
        exam_code = "".join(list(dict_info.values())[-3:])
        result_info = {"student_code": student_code, "exam_code": exam_code}

    return result_info


if __name__ == "__main__":
    # ========================================= Đo thời gian ==========================
    # start_time = time.time()
    # total = sum(range(10**6))
    # ========================== PREDICT =========================================
    pWeight = "./Model/best1007.pt"
    model = YOLO(pWeight)

    if os.path.exists("result.txt"):
        os.remove("result.txt")
    if os.path.exists("data.json"):
        os.remove("data.json")

    parser = argparse.ArgumentParser(description="Process some integers.")
    parser.add_argument("input", help="input")
    args = parser.parse_args()

    path = './images/answer_sheets/exam-class1/' + args.input
    image = cv2.imread(path, cv2.IMREAD_COLOR)[:, :, ::-1]
    document = extract(image_true=image, trained_model=trained_model)
    document = document / 255.0
    img = cv2.resize(document, (1056, 1500), interpolation=cv2.INTER_AREA)

    # ========================== Cắt ảnh sbd và mdt ===============================
    img_resize = crop_image_info(img)
    result_info = predictInfo(img=img_resize, model=model)

    # ========================== Lấy đáp án ===============================
    result_answer = crop_image(cv2.convertScaleAbs(img * 255))
    list_answer = []
    for i, answer in enumerate(result_answer):
        selected_answer = predictAns(img=answer, model=model)
        list_answer = list_answer + selected_answer

    array_result = []
    for key, value in enumerate(list_answer):
        item = {"index": int(key) + 1, "answer": value}
        array_result.append(item)
    if len(result_info) == 2:
        result = {
            "student_code": result_info["student_code"],
            "exam_code": result_info["exam_code"],
            "list_answer": array_result,
        }
    elif len(result_info) == 3:
        result = {
            "class_code": result_info["class_code"],
            "student_code": result_info["student_code"],
            "exam_code": result_info["exam_code"],
            "list_answer": array_result,
        }
    # Ghi dữ liệu từ điển vào tệp tin JSON
    with open("data.json", "w") as file:
        json.dump(result, file)

    f = open("result.txt", "w")
    f.write("OK")
    f.close()

    # ========================================= Đo thời gian ==========================
    # end_time = time.time()
    # execution_time = end_time - start_time
    # print("Thời gian thực thi: ", execution_time, " giây")
