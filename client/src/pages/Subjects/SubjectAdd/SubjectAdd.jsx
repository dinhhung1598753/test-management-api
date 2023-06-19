import React, { useState } from "react";
import SubjectInfo from "../../../components/SubjectInfo/SubjectInfo";
import useNotify from "../../../hooks/useNotify";
import { addSubjectsService } from "../../../services/subjectsService";
const SubjectAdd = () => {
	const [loading, setLoading] = useState(false);
	const notify = useNotify();
	const onFinish = (values) => {
		setLoading(true);
		addSubjectsService(values, (res) => {setLoading(false);
		notify.success("Thêm học phần thành công!")},
		(error) => {
			setLoading(false);
			notify.error("Lỗi thêm học phần!");
		});
		console.log(values);
	};
	return (
		<SubjectInfo
			infoHeader="Thêm học phần"
			btnText="Thêm"
			initialValues={{ remember: false }}
			onFinish={onFinish}
			loading={loading}
		/>
	);
};
export default SubjectAdd;
