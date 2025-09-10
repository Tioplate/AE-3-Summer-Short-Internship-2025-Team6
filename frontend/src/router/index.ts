import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
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
    {
      path: '/myrequests',
      name: 'myrequests',
      component: () => import('../views/MyRequestsView.vue'),
    },
    {
      path: '/admin',
      name: 'admin',
      component: () => import('../views/AdminView.vue'),
    },
  ],
})

export default router
