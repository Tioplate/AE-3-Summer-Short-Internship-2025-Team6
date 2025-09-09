import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
    },
    {
      path: '/evacuee',
      name: 'evacuee',
      component: () => import('../views/EvacueeView.vue'),
    },
    {
      path: '/supporter',
      name: 'supporter',
      component: () => import('../views/SupporterMapView.vue'),
    },
    {
      path: '/shelter/:id',
      name: 'shelter',
      component: () => import('../views/ShelterDetailView.vue'),
    },
    {
      path: '/donation/:id',
      name: 'donation',
      component: () => import('../views/DonationView.vue'),
    },
    {
      path: '/evacueemapview',
      name: 'evacueemapview',
      component: () => import('../views/EvacueeMapView.vue'),
    },
  ],
})

export default router
