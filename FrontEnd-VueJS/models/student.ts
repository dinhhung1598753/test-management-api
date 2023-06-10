import { api } from "@/apis";

export const getStudents = async () => {
  const res = await api.get("/student/list").catch((err) => {
    console.log(err);
    return null;
  });
  return res;
};
