import { api } from "@/apis";

export const getTeachers = async () => {
  const res = await api.get("/teacher/list").catch((err) => {
    console.log(err);
    return null;
  });
  return res;
};
