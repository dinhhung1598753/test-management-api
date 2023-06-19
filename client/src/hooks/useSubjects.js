import { useState } from "react";
import { getAllSubjectsService } from "../services/subjectsService";
import useNotify from "./useNotify";

const useSubjects = () => {
  const notify = useNotify();
  const [allSubjects, setAllSubjects] = useState([]);
  const [tableLoading, setTableLoading] = useState(true);

  const getAllSubjects = (payload = {}) => {
    setTableLoading(true);
    getAllSubjectsService(
      payload,
      (res) => {
        console.log("success");
        setAllSubjects(res.data);
        setTableLoading(false);
      },
      (err) => {
        console.log("failed");
        setTableLoading(false);
        if (err.response.status === 401) {
          notify.warning(err.response.data.message || "Permission denied");
        }
        if (err.response.status === 404) {
          notify.warning(err.response.data.message || "No information in database");
        }
      }
    );
  };

  return {
    allSubjects,
    getAllSubjects,
    tableLoading,
  };
};

export default useSubjects;
