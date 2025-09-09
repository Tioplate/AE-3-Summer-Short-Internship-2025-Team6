<template>
  <div class="admin-container">
    <header class="admin-header">
      <h1>運営管理画面</h1>
      <nav class="admin-nav">
        <button 
          @click="activeTab = 'map'" 
          :class="['nav-btn', { active: activeTab === 'map' }]"
        >
          マップ管理
        </button>
        <button 
          @click="activeTab = 'shelters'" 
          :class="['nav-btn', { active: activeTab === 'shelters' }]"
        >
          避難所管理
        </button>
        <button 
          @click="activeTab = 'supplies'" 
          :class="['nav-btn', { active: activeTab === 'supplies' }]"
        >
          物資管理
        </button>
        <button 
          @click="activeTab = 'donations'" 
          :class="['nav-btn', { active: activeTab === 'donations' }]"
        >
          支援物資
        </button>
        <button @click="logout" class="logout-btn">ログアウト</button>
      </nav>
    </header>

    <main class="admin-main">
      <AdminMapView v-if="activeTab === 'map'" />
      <ShelterManagement v-if="activeTab === 'shelters'" />
      <SupplyManagement v-if="activeTab === 'supplies'" />
      <DonationManagement v-if="activeTab === 'donations'" />
    </main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import AdminMapView from '../components/AdminMapView.vue'
import ShelterManagement from '../components/ShelterManagement.vue'
import SupplyManagement from '../components/SupplyManagement.vue'
import DonationManagement from '../components/DonationManagement.vue'

const router = useRouter()
const activeTab = ref('map')

const logout = () => {
  router.push('/')
}
</script>

<style scoped>
.admin-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.admin-header {
  background: white;
  border-bottom: 1px solid #e0e0e0;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.admin-header h1 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 24px;
  font-weight: 600;
}

.admin-nav {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.nav-btn {
  padding: 10px 20px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  font-weight: 500;
}

.nav-btn:hover {
  background: #f8f9fa;
  border-color: #007bff;
}

.nav-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.logout-btn {
  padding: 10px 20px;
  border: 1px solid #dc3545;
  background: #dc3545;
  color: white;
  border-radius: 6px;
  cursor: pointer;
  margin-left: auto;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.logout-btn:hover {
  background: #c82333;
  border-color: #c82333;
}

.admin-main {
  padding: 20px;
}

@media (max-width: 768px) {
  .admin-nav {
    flex-direction: column;
  }
  
  .logout-btn {
    margin-left: 0;
    margin-top: 10px;
  }
}
</style>