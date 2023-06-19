import { Teacher } from "@/types";
import { api } from "@/apis";

export const useTeacherStore = defineStore("teacher", () => {
  const teachers = ref<Teacher[]>([]);
  const isCreating = ref(false);

  const getTeachers = async () => {
    const res = await api.get("/teacher/list").catch((err) => {
      console.log(err);
      return null;
    });
    teachers.value = res?.data || [];
  };

  const createTeacher = async (
    fullName: string,
    code: string,
    username: string,
    password: string,
    birthday: string,
    gender: string,
    phoneNumber: string,
    email: string
  ) => {
    const res = await api
      .post("/teacher/add", {
        fullName,
        code,
        username,
        password,
        birthday,
        gender,
        phoneNumber,
        email,
      })
      .catch((err) => {});
  };

  const updateById = async (
    id: number,
    fullName: string,
    code: string,
    birthday: string,
    gender: string,
    phoneNumber: string,
    email: string
  ) => {
    const res = await api
      .put(`/teacher/update/${id}`, {
        fullName,
        code,
        birthday,
        gender,
        phoneNumber,
        email,
      })
      .catch((err) => {});
    return res;
  };

  const deleteById = async (id: number) => {
    const res = await api.delete(`teacher/disable/${id}`).catch(() => null);
    if (res !== null) {
      const deletedItemIndex = teachers.value.findIndex(
        (item) => item.id === id
      );
      if (deletedItemIndex > -1) {
        teachers.value.splice(deletedItemIndex, 1);
      }
    }
  };
  return {
    teachers,
    isCreating,
    getTeachers,
    createTeacher,
    updateById,
    deleteById,
  };
});
