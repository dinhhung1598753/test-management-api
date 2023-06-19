import React, {useState} from "react";
import SubjectInfo from "../../../components/SubjectInfo/SubjectInfo";
import { useSelector } from "react-redux";
import useNotify from "../../../hooks/useNotify";
import { updateSubjectsService } from "../../../services/subjectsService";
const SubjectEdit = () => {
	const [loading, setLoading] = useState(false);
  const { selectedItem } = useSelector((state) => state.appReducer);
	const notify = useNotify();
	const onFinish = (value) => {
		setLoading(true);
		updateSubjectsService(
			selectedItem ? selectedItem.id : null,
			value,
			(res) => {
				setLoading(false);
				notify.success("Cập nhật thông tin học phần thành công!");
			},
			(error) => {
				setLoading(false);
				notify.error("Lỗi cập nhật thông tin học phần!");
			}
		);
	};
  return (
    <SubjectInfo
      infoHeader="Sửa thông tin học phần"
      btnText="Cập nhật"
      initialValues={{
        remember: false,
        title: selectedItem ? selectedItem.title : null,
        code: selectedItem ? selectedItem.code : null,
        description: selectedItem ? selectedItem.description : null,
        credit: selectedItem ? selectedItem.credit : null,
      }}
			loading = {loading}
			onFinish={onFinish}
    />
  );
};
export default SubjectEdit;
