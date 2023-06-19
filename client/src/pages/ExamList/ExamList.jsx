import { UploadOutlined } from "@ant-design/icons";
import { Button, Form, Input, Modal, Upload, message } from "antd";
import { useState } from "react";
import useAI from "../../hooks/useAI";
import "./ExamList.scss";
import HeaderSelect from "./HeaderSelect";
import TableResult from "./TableResult";

const formItemLayout = {
  labelCol: {
    span: 6,
  },
  wrapperCol: {
    span: 14,
  },
};
const getBase64 = (file) =>
  new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = (error) => reject(error);
  });

const ExamList = () => {
  const [urlImg, setUrlImg] = useState();
  const [numberAnswer, setNumberAnswer] = useState(120);
  const { getModelAI, resultAI, loading } = useAI();

  const [previewOpen, setPreviewOpen] = useState(false);
  const [previewImage, setPreviewImage] = useState("");
  const [previewTitle, setPreviewTitle] = useState("");
  const handleCancel = () => setPreviewOpen(false);

  const uploadButton = (
    <div>
      <Button icon={<UploadOutlined />}>Click to Upload</Button>
    </div>
  );
  const props = {
    name: "files",
    listType: "picture",
    action: "http://localhost:8000/api/v1/student-test/uploads?exam-class=exam-class1",
    beforeUpload: (file) => {
      const isPNG =
        file.type === "image/png" || file.type === "image/jpg" || file.type === "image/jpeg";
      if (!isPNG) {
        message.error(`${file.name} is not a image file`);
      }
      return isPNG || Upload.LIST_IGNORE;
    },
    onChange(info) {
      if (info.file.status !== "uploading") {
        setUrlImg(info.file.name);
      }
      if (info.file.status === "done") {
        message.success(`${info.file.name} file uploaded successfully`);
      } else if (info.file.status === "error") {
        message.error(`${info.file.name} file upload exist or connection is interrupted`);
      }
    },
  };
  const handlePreview = async (file) => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }
    setPreviewImage(file.url || file.preview);
    setPreviewOpen(true);
    setPreviewTitle(file.name || file.url.substring(file.url.lastIndexOf("/") + 1));
  };

  const onFinish = (values) => {
    setNumberAnswer(values.numberAnswer);
    if (urlImg) {
      getModelAI({
        pathImg: urlImg,
        numberAnswer: values.numberAnswer,
      });
    }
  };

  const uploadBlock = (
    <div>
      <Upload {...props} onPreview={handlePreview}>
        {uploadButton}
      </Upload>
      <Modal open={previewOpen} title={previewTitle} footer={null} onCancel={handleCancel}>
        <img
          alt="example"
          style={{
            width: "100%",
          }}
          src={previewImage}
        />
      </Modal>
    </div>
  );

  return (
    <div className="exam-list-wrapper">
      <div className="header-exam-list">
        <h2>Chấm điểm tự động</h2>
      </div>
      <HeaderSelect />
      <div className="content-exam-list">
        <Form name="validate_other" {...formItemLayout} onFinish={onFinish}>
          <div className="upload">
            <Form.Item name="pathImg">
              <div>{uploadBlock}</div>
            </Form.Item>
          </div>
          <div className="number-answer">
            <Form.Item
              name="numberAnswer"
              label="Number Answer"
              rules={[
                {
                  required: true,
                },
              ]}
            >
              <Input type="number" />
            </Form.Item>
          </div>
          <Button type="primary" htmlType="submit" loading={loading} style={{ width: "100px" }}>
            Submit
          </Button>
        </Form>
        <div className="result-ai">
          <TableResult resultAI={resultAI} numberAnswer={numberAnswer} />
        </div>
      </div>
    </div>
  );
};

export default ExamList;
