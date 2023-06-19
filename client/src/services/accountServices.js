import { postRequest } from "../api/apiCaller";
import { apiPath } from "../config/apiPath";
export const loginAuthenticService = async (parameters, successCallback, errorCallback) => {
  await postRequest(`${apiPath.login}`, parameters, successCallback, errorCallback, 10000);
};
export const registerService = async (parameters, successCallback, errorCallback) => {
  await postRequest(`${apiPath.register}`, parameters, successCallback, errorCallback, 10000);
};

// Refresh token
export const refreshTokenRequest = async (params, successCallback, errorCallback) => {
  await postRequest(apiPath.refreshToken, params, successCallback, errorCallback);
};
