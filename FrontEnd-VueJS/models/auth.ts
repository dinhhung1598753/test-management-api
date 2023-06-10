import { authApi } from "@/apis";

export const signUp = async (
  username: string,
  email: string,
  password: string
) => {
  const res = await authApi
    .post("/auth/signup", {
      username,
      email,
      password,
    })
    .catch((err) => {});
  return res;
};

export const signin = async (
  username: string,
  password: string
) => {
  const res = await authApi
    .post("/auth/signin", {
      username,
      password,
    })
    .catch((err) => {});
  return res;
};
