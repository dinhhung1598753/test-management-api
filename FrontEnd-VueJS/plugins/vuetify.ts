import { createVuetify, ThemeDefinition } from "vuetify";

const defaultTheme: ThemeDefinition = {
  dark: false,
  colors: {
    primary: "#2196f3",
    background: "#f9f9f7",
  },
};

export default defineNuxtPlugin((nuxtApp) => {
  const vuetify = createVuetify({
    // eslint-disable-next-line @typescript-eslint/ban-ts-comment
    // @ts-ignore
    icons: {
      defaultSet: "mdi",
    },
    theme: {
      defaultTheme: "defaultTheme",
      themes: {
        defaultTheme,
      },
    },
    defaults: {
      VBtn: {
        color: "primary",
        minWidth: 40,
        minHeight: 32,
      },
      VTextField: {
        hideDetails: "auto",
        color: "primary",
        variant: "outlined",
        density: "compact",
      },
      VSelect: {
        hideDetails: "auto",
        clearable: true,
        color: "primary",
        density: "compact",
      },
      VRadioGroup: {
        hideDetails: "auto",
        color: "primary",
      },
      VAutocomplete: {
        hideDetails: "auto",
        color: "primary",
      },
      VRadio: {
        hideDetails: "auto",
        color: "primary",
      },
      VCheckbox: {
        hideDetails: "auto",
        color: "primary",
      },
      VRow: {
        justify: "center",
        align: "center",
        dense: true,
      },
      VCard: {
        color: "background",
      },
      VTextarea: {
        hideDetails: "auto",
        color: "primary",
        variant: "outlined",
      },
    },
  });

  nuxtApp.vueApp.use(vuetify);
});
