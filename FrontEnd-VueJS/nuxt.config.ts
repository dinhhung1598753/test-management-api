import vuetify from "vite-plugin-vuetify";

export default defineNuxtConfig({
  // using ssr: false will not face with this issue https://github.com/nuxt/framework/issues/8825
  ssr: false,
  app: {
    head: {
      title: "test-management",
      meta: [{ name: "description", content: "test management" }],
    },
  },
  modules: [
    [
      "@pinia/nuxt",
      {
        autoImports: ["defineStore", "storeToRefs"],
      },
    ],
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    (options, nuxt) => {
      nuxt.hooks.hook("vite:extendConfig", (config: any) => {
        config.plugins?.push(
          vuetify({
            styles: {
              configFile: "./assets/scss/vuetify.settings.scss",
            },
          })
        );
      });
    },
  ],
  imports: {
    dirs: ["store"],
  },
  css: [
    "vuetify/lib/styles/main.sass",
    "@mdi/font/css/materialdesignicons.min.css",
    "@/assets/scss/app.scss",
  ],
  vite: {
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: '@import "@/assets/scss/utils/index.scss";',
        },
      },
    },
  },
  sourcemap: {
    server: false,
    client: false,
  },
  runtimeConfig: {
    public: {
      API_BASE_URL: process.env.API_BASE_URL,
    },
  },
});
