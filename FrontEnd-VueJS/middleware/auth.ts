export default defineNuxtRouteMiddleware((to, from) => {
  const authStore = useAuthStore();
  if (authStore.currentUser.id) return;

  return navigateTo("/auth/login");
});
