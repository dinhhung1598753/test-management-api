import { Form, Input, Button } from "antd";
import { BsFillPlusCircleFill } from "react-icons/bs";
import { FaMinusCircle } from "react-icons/fa";
import "./SubjectInfo.scss";
import React from "react";
const SubjectInfo = ({
	onFinish,
	initialValues,
	infoHeader,
	btnText,
	loading,
}) => {
	const [chapterForms, setChapterForms] = React.useState([]);
	const addChapter = () => {
		const newForm = (
			<div className="chapter-item">
				<Form.Item
					name={`order-${chapterForms.length}`}
					label="Order"
					rules={[
						{
							required: true,
							message: "Please input the order of this chapter!",
						},
						{
							pattern: /^[1-9]\d*$/,
							message:
								"Please enter a positive number for order!",
						},
					]}
				>
					<Input placeholder="Enter the order of this chapter" />
				</Form.Item>
				<Form.Item
					name={`title-${chapterForms.length}`}
					label="Title"
					rules={[
						{
							required: true,
							message: "Please input the title of this chapter!",
						},
					]}
				>
					<Input placeholder="Enter the title of this chapter" />
				</Form.Item>
			</div>
		);
		setChapterForms([...chapterForms, newForm]);
		console.log(chapterForms);
	};
	const handlRemoveChapter = (item) => {
		setChapterForms(
			chapterForms.filter(
				(_, index) => index !== chapterForms.indexOf(item)
			)
		);
	};
	return (
		<div className="subject-info">
			<p className="info-header">{infoHeader}</p>
			<Form
				name="info-subject-form"
				className="info-subject-form"
				initialValues={initialValues}
				onFinish={onFinish}
			>
				<div className="info-subject-header">Thông tin học phần</div>
				<Form.Item
					name="code"
					label="Code"
					colon={true}
					rules={[
						{
							required: true,
							message: "Please input the code!",
						},
					]}
				>
					<Input placeholder="Enter the code" />
				</Form.Item>
				<Form.Item
					name="title"
					label="Title"
					colon={true}
					rules={[
						{
							required: true,
							message: "Please input the title!",
						},
					]}
				>
					<Input placeholder="Enter user name" />
				</Form.Item>
				<Form.Item
					name="description"
					label="Description"
					colon={true}
					rules={[
						{
							required: true,
							message: "Please input the description!",
						},
					]}
				>
					<Input placeholder="Enter the description" />
				</Form.Item>
				<Form.Item
					name="credit"
					label="Credit"
					colon={true}
					rules={[
						{
							required: true,
							message: "Please input the credit!",
						},
						{
							pattern: /^[1-9]\d*$/,
							message:
								"Please enter a positive number for credit!",
						},
					]}
				>
					<Input placeholder="Enter the credit" />
				</Form.Item>
				<div className="chapter-add">
					<div className="a-chapter-add-header">
						<div className="chapter-add-header-title">Nội dung</div>
						<BsFillPlusCircleFill
							style={{
								color: "#8c1515",
								cursor: "pointer",
							}}
							onClick={addChapter}
							size={24}
						/>
					</div>
					{chapterForms.map((form, index) => (
						<Form
							key={`chapter${index}`}
							className="chapter-add-item"
						>
							{form}
							<FaMinusCircle
								style={{ color: "#8c1515", cursor: "pointer" }}
								size={30}
								className="minus-chapter-item"
								onClick={() => handlRemoveChapter(form)}
							/>
						</Form>
					))}
				</div>
				<Form.Item className="btn-info">
					<Button
						type="primary"
						htmlType="submit"
						loading={loading}
						style={{ width: 150, height: 50 }}
					>
						{btnText}
					</Button>
				</Form.Item>
			</Form>
		</div>
	);
};
export default SubjectInfo;
