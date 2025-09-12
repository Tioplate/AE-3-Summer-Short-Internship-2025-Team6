<template>
  <div class="shelter-detail-container">
    <header class="header">
      <h1>{{ shelter?.shelterName }} - 物資要請詳細</h1>
      <div class="header-buttons">
        <button @click="goBack" class="back-btn">
          <span class="icon">←</span> マップに戻る
        </button>
        <button @click="goToDonation" class="donation-btn">
          <span class="icon">💝</span> 支援・寄付
        </button>
      </div>
    </header>

    <div class="content" v-if="shelter">
      <div class="shelter-overview">
        <div class="overview-card">
          <h2>避難所の状況</h2>
          <div class="status-grid">
            <div class="status-item">
              <span class="label">収容状況</span>
              <span class="value">
                {{ shelter.shelterCur }} / {{ shelter.shelterCap }}人
                <span class="percentage">({{ Math.round((shelter.currentCapacity / shelter.maxCapacity) * 100) }}%)</span>
              </span>
            </div>
            <div class="status-item">
              <span class="label">緊急度</span>
              <span class="value">
                <span class="status-badge" :class="`status-${shelter.urgency}`">
                  {{ getUrgencyText(shelter.status) }}
                </span>
              </span>
            </div>
            <div class="status-item">
              <span class="label">最終更新</span>
              <span class="value">{{ shelter.lastUpdated }}</span>
            </div>
            <div class="status-item">
              <span class="label">運営責任者</span>
              <span class="value">{{ shelter.adminId }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="requests-section">
        <div class="section-header">
          <h2>物資要請一覧</h2>
          <div class="filter-buttons">
            <button 
              v-for="filter in filters" 
              :key="filter.value"
              @click="selectedFilter = filter.value"
              :class="['filter-btn', { active: selectedFilter === filter.value }]"
            >
              {{ filter.label }}
              <span class="count">({{ getFilteredRequests(filter.value).length }})</span>
            </button>
          </div>
        </div>

        <div class="requests-grid">
          <div 
            v-for="request in getFilteredRequests(selectedFilter)" 
            :key="request.id"
            class="request-card"
          >
            <div class="request-header">
              <h3>{{ request.itemName }}</h3>
            </div>
            
            <div class="request-details">
              <div class="detail-item">
                <span class="label">届いた量:</span>
                <span class="value quantity">{{ request.quantity }} {{ request.unit }}</span>
              </div>
              <div class="detail-item">
                <span class="label  ">到着日時:</span>
              </div>
            </div>

            <div class="support-count">
              <span class="support-text">この要請への支援者数: </span>
              <span class="support-number">{{ request.supportCount }}人</span>
            </div>
          </div>
        </div>

        <div class="free-requests">
          <h3>自由記入による要請</h3>
          <div class="free-request-list">
            <div 
              v-for="freeRequest in freeRequests" 
              :key="freeRequest.id"
              class="free-request-card"
            >
              <div class="free-request-header">
                <span class="timestamp">{{ freeRequest.timestamp }}</span>
                <span class="requester">投稿者: {{ freeRequest.requester }}</span>
              </div>
              <div class="free-request-content">
                {{ freeRequest.content }}
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="statistics-section">
        <h2>要請統計</h2>
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-number">{{ totalRequests }}</div>
            <div class="stat-label">総要請数</div>
          </div>
          <div class="stat-card urgent">
            <div class="stat-number">{{ urgentRequests }}</div>
            <div class="stat-label">緊急要請</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ totalSupporters }}</div>
            <div class="stat-label">支援表明者</div>
          </div>
          <div class="stat-card">
            <div class="stat-number">{{ Math.round(averageResponseTime) }}h</div>
            <div class="stat-label">平均対応時間</div>
          </div>
        </div>
      </div>
    </div>
    <div v-else class="loading">
      <p>避難所情報を読み込んでいます...</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, onBeforeMount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { shelterGoodsList } from '../stores/shelterGoods'
import { shelters } from "../stores/shelters.ts";
import axios from "axios";
import {ElMessage} from "element-plus";

const router = useRouter()
const route = useRoute()

const selectedFilter = ref('all')
const shelter = ref<any>(null)
const shelterId = ref<string>('');

const filters = [
  { value: 'all', label: 'すべて' },
  { value: 'urgent', label: '緊急' },
  { value: 'important', label: '重要' },
  { value: 'normal', label: '通常' },
  { value: 'delivered', label: '届いた物資' }
]

const mockRequests = [
  {
    id: 'req1',
    itemName: 'ミネラルウォーター',
    quantity: 100,
    unit: 'L',
    priority: 'urgent',
    timestamp: '2024/03/15 14:30',
    requester: '田中さん',
    description: '乳幼児がいるため、清潔な水が緊急で必要です',
    supportCount: 12
  },
  {
    id: 'req2',
    itemName: '離乳食',
    quantity: 50,
    unit: '個',
    priority: 'urgent',
    timestamp: '2024/03/15 13:45',
    requester: '佐藤さん',
    description: '生後8ヶ月の赤ちゃん用',
    supportCount: 8
  },
  {
    id: 'req3',
    itemName: '毛布',
    quantity: 30,
    unit: '枚',
    priority: 'important',
    timestamp: '2024/03/15 12:20',
    requester: '山田さん',
    description: '高齢者の方々が寒がっています',
    supportCount: 15
  },
  {
    id: 'req4',
    itemName: 'おにぎり',
    quantity: 200,
    unit: '個',
    priority: 'important',
    timestamp: '2024/03/15 11:15',
    requester: '鈴木さん',
    description: '',
    supportCount: 25
  },
  {
    id: 'req5',
    itemName: '乾電池',
    quantity: 20,
    unit: 'パック',
    priority: 'normal',
    timestamp: '2024/03/15 10:00',
    requester: '伊藤さん',
    description: 'ラジオ用の単3電池',
    supportCount: 5
  }
]

const freeRequests = [
  {
    id: 'free1',
    timestamp: '2024/03/15 15:00',
    requester: '高橋さん',
    content: 'アレルギー対応食品が必要です。小麦、卵、乳製品がダメな子供がいます。'
  },
  {
    id: 'free2',
    timestamp: '2024/03/15 14:15',
    requester: '中村さん',
    content: 'インスリン注射が必要な糖尿病患者がいます。冷蔵保存できる環境も必要です。'
  },
  {
    id: 'free3',
    timestamp: '2024/03/15 13:30',
    requester: '小林さん',
    content: 'ペット用のフードも不足しています。猫2匹、犬1匹います。'
  }
]

const mockShelters = {
  shelter1: {
    id: 'shelter1',
    name: '中央小学校',
    urgency: 'urgent',
    currentCapacity: 180,
    maxCapacity: 200,
    lastUpdated: '2024/03/15 15:30',
    manager: '校長 田中一郎'
  },
  shelter2: {
    id: 'shelter2',
    name: '市民体育館',
    urgency: 'important',
    currentCapacity: 90,
    maxCapacity: 150,
    lastUpdated: '2024/03/15 15:20',
    manager: '館長 佐藤花子'
  },
  shelter3: {
    id: 'shelter3',
    name: '総合公園体育館',
    urgency: 'normal',
    currentCapacity: 45,
    maxCapacity: 100,
    lastUpdated: '2024/03/15 15:10',
    manager: '館長 山田太郎'
  }
}
const backUrl = import.meta.env.VITE_BACK_URL;
const totalRequests = computed(() => mockRequests.length)
const urgentRequests = computed(() => mockRequests.filter(r => r.priority === 'urgent').length)
const totalSupporters = computed(() => mockRequests.reduce((sum, r) => sum + r.supportCount, 0))
const averageResponseTime = computed(() => 2.5)


const getUrgencyText = (urgency: string) => {
  const map: Record<string, string> = {
    urgent: '緊急支援必要',
    important: '支援必要',
    normal: '状況良好'
  }
  return map[urgency] || '状況良好'
}

const getPriorityText = (priority: string) => {
  const map: Record<string, string> = {
    urgent: '緊急',
    important: '重要',
    normal: '通常'
  }
  return map[priority] || '通常'
}

// const getFilteredRequests = (filter: string) => {
//   if (filter === 'all') return mockRequests
//   return mockRequests.filter(req => req.priority === filter)
// }
  // 届いた物資のダミーデータ
  const deliveredRequests = [
    {
      id: 'del1',
      itemName: 'ミネラルウォーター',
      quantity: 80,
      unit: 'L',
      deliveredAt: '2024/03/16 10:00',
      supporter: 'A',
      supportCount:20
    },
    {
      id: 'del2',
      itemName: '毛布',
      quantity: 20,
      unit: '枚',
      deliveredAt: '2024/03/16 09:30',
      supporter: 'B',
      supportCount: 15
    }
  ]

  const getFilteredRequests = (filter: string) => {
    if (filter === 'all') return mockRequests
    if (filter === 'delivered') return deliveredRequests
    return mockRequests.filter(req => req.priority === filter)
  }

const goBack = () => {
  router.push('/supporter')
}

const goToDonation = () => {
  router.push(`/donation/${route.params.shelterId}`)
}
onBeforeMount( () => {
  if (!route.params.shelterId) {
    ElMessage.error('避難所IDが指定されていません')
  }
  shelterId.value = route.params.shelterId as string
})

onMounted(async () => {

  //alert(shelterId.value)
  try {
    const res = await axios.get(backUrl + '/shelter/getById', {
      params: { shelterId: shelterId.value }
    })
    //shelters.value = res.data // 假设后端返回的是避难所数组
    //shelters
    //alert(res.data)
    shelter.value = shelters.value.find(s => s.shelterId === shelterId.value) || null
    if (!shelter.value) {
      ElMessage.error('指定された避難所が見つかりません')
    }
  } catch (e) {
    ElMessage.error('避難所データの取得に失敗しました')
    return
  }
  //shelter.value = (mockShelters as any)[shelterId] || mockShelters.shelter1

})

</script>

<style scoped>
.shelter-detail-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #2196F3;
  color: white;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.header-buttons {
  display: flex;
  gap: 10px;
}

.back-btn, .donation-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.donation-btn {
  background: #FF6B35;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.donation-btn:hover {
  background: #FF5722;
}
.content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.shelter-overview {
  margin-bottom: 30px;
}

.overview-card {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overview-card h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
}

.status-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.status-item .label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.status-item .value {
  font-size: 16px;
  color: #333;
}

.percentage {
  color: #666;
  font-size: 14px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-urgent {
  background: #ffebee;
  color: #c62828;
}

.status-important {
  background: #fff3e0;
  color: #f57c00;
}

.status-normal {
  background: #e8f5e8;
  color: #2e7d32;
}

.requests-section {
  margin-bottom: 30px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  margin: 0;
  color: #333;
}

.filter-buttons {
  display: flex;
  gap: 10px;
}

.filter-btn {
  padding: 8px 16px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 14px;
}

.filter-btn.active {
  background: #2196F3;
  color: white;
  border-color: #2196F3;
}

.count {
  font-size: 12px;
  margin-left: 5px;
}

.requests-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.request-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.request-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.request-header h3 {
  margin: 0;
  color: #333;
}

.priority-badge {
  padding: 3px 10px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
}

.priority-urgent {
  background: #ffebee;
  color: #c62828;
}

.priority-important {
  background: #fff3e0;
  color: #f57c00;
}

.priority-normal {
  background: #e8f5e8;
  color: #2e7d32;
}

.request-details {
  margin-bottom: 15px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.detail-item .label {
  font-weight: 500;
  color: #666;
}

.detail-item .value {
  color: #333;
}

.quantity {
  font-weight: 600;
  color: #2196F3;
}

.request-description {
  background: #f8f9fa;
  padding: 10px;
  border-radius: 5px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #555;
}

.support-count {
  display: flex;
  align-items: center;
  padding-top: 10px;
  border-top: 1px solid #eee;
}

.support-text {
  color: #666;
  font-size: 14px;
}

.support-number {
  font-weight: 600;
  color: #4CAF50;
  margin-left: 5px;
}

.free-requests {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.free-requests h3 {
  margin: 0 0 20px 0;
  color: #333;
}

.free-request-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.free-request-card {
  border: 1px solid #eee;
  border-radius: 5px;
  padding: 15px;
}

.free-request-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #666;
}

.free-request-content {
  color: #333;
  line-height: 1.5;
}

.statistics-section {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.statistics-section h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 20px;
}

.stat-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background: #f8f9fa;
}

.stat-card.urgent {
  background: #ffebee;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.stat-card.urgent .stat-number {
  color: #c62828;
}

.stat-label {
  color: #666;
  font-size: 14px;
}

.loading {
  text-align: center;
  padding: 50px;
  color: #666;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .header-buttons {
    justify-content: center;
  }
  
  .content {
    padding: 10px;
  }
  
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .filter-buttons {
    justify-content: center;
    flex-wrap: wrap;
  }
  
  .requests-grid {
    grid-template-columns: 1fr;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>