import {
  getRequest,
  postRequest,
  putRequest,
  deleteRequest,
} from "../api/apiCaller";
import { apiPath } from "../config/apiPath";
export const getAllSubjectsService = async (
  params,
  successCallback,
  errorCallback
) => {
  await getRequest(
    `${apiPath.allSubjects}`,
    params,
    successCallback,
    errorCallback
  );
};
export const updateSubjectsService = async (
  subjectId,
  params,
  successCallback,
  errorCallback
) => {
  await putRequest(
    `${apiPath.updateSubject}/${subjectId}`,
    params,
    successCallback,
    errorCallback
  );
};
export const addSubjectsService = async (
  params,
  successCallback,
  errorCallback
) => {
  await postRequest(
    `${apiPath.addSubject}`,
    params,
    successCallback,
    errorCallback
  );
};
export const deleteSubjectsService = async (
  subjectId,
  params,
  successCallback,
  errorCallback
) => {
  await deleteRequest(
    `${apiPath.deleteSubject}/${subjectId}`,
    params,
    successCallback,
    errorCallback
  );
};
export const getAllChaptersService = async (code, params, successCallback, errorCallback) => {
  await getRequest(`${apiPath.allChapters}${code}/chapter/list`, params, successCallback, errorCallback, 10000);
};
export const deleteChaptersService = async (code, params, successCallback, errorCallback) => {
  await deleteRequest(
    `${apiPath.deleteSubject}/${code}`,
    params,
    successCallback,
    errorCallback,
  );
};
export const updateChaptersService = async (code, params, successCallback, errorCallback) => {
  await putRequest(
    `${apiPath.updateSubject}/${code}`,
    params,
    successCallback,
    errorCallback,
  );
};
