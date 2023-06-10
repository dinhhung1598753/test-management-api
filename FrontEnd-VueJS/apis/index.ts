import axios from "axios";

interface Session {
  accessToken: string;
  refreshToken: string;
}

const MAX_RETRY_ATTEMPTS = 1;

const createAPI = (baseURL: string) => {
  const instance = axios.create({
    baseURL: baseURL, //Ex: "https://some-domain.com/api/",
    timeout: 1000,
    headers: {
      "Access-Control-Allow-Origin": "*",
      "Access-Control-Allow-Credentials": true,
    },
  });

  // Add access token to header
  //   instance.interceptors.request.use(
  //     (config) => {
  //       const session: Session = JSON.parse(
  //         localStorage.getItem("session") || ""
  //       );
  //       config.headers.Authorization = `Bearer ${session?.accessToken}`;
  //       return config;
  //     },
  //     (error) => Promise.reject(error)
  //   );

  //   instance.interceptors.response.use(
  //     (response) => response,
  //     async (error) => {
  //       const { config, response } = error;
  //       console.log(1111, error);
  //       // Retry the request if it returns a 401 error and has retry attempts remaining
  //       if (
  //         response.status === 401 &&
  //         config &&
  //         config.retryCount < MAX_RETRY_ATTEMPTS
  //       ) {
  //         config.retryCount = config.retryCount || 0;
  //         config.retryCount++;

  //         const result = await refreshTokenFn();

  //         if (result?.accessToken) {
  //           config.headers.Authorization = `Bearer ${result?.accessToken}`;
  //         }

  //         const retryConfig = { ...config };

  //         return instance.request(retryConfig);
  //       }

  //       return Promise.reject(error);
  //     }
  //   );
  return instance;
};

const runtimeConfig = useRuntimeConfig();
export const api = createAPI(runtimeConfig.public.API_BASE_URL);

export const authApi = axios.create({
  baseURL: runtimeConfig.public.API_BASE_URL, //Ex: "https://some-domain.com/api/",
  timeout: 1000,
  headers: {
    "Access-Control-Allow-Origin": "*",
    "Access-Control-Allow-Methods": "GET,PUT,POST,DELETE,PATCH,OPTIONS",
  },
});

const refreshTokenFn = async () => {
  const session: Session = JSON.parse(localStorage.getItem("session") || "");

  try {
    const response: any = await authApi.post("/auth/refresh-token", {
      refreshToken: session?.refreshToken,
    });

    const { accessToken, refreshToken } = response.data;

    if (!session?.accessToken) {
      localStorage.removeItem("session");
    }

    localStorage.setItem(
      "session",
      JSON.stringify({ accessToken, refreshToken })
    );

    return session;
  } catch (error) {
    localStorage.removeItem("session");
    return null;
  }
};
