<template>
  <div class="supporter-container">
    <header class="header">
      <h1>支援状況マップ</h1>
      <button @click="goBack" class="back-btn">← ログイン画面に戻る</button>
      <button @click="goHome" class="home-btn">← 物資要請画面に戻る</button>
    </header>

    <div class="map-container">
      <div id="map" ref="mapContainer"></div>
      
      <div class="legend">
        <h3>避難所の状況</h3>
        <div class="legend-item">
          <span class="marker red"></span>
          <span>緊急対応必要</span>
        </div>
        <div class="legend-item">
          <span class="marker yellow"></span>
          <span>物資不足</span>
        </div>
        <div class="legend-item">
          <span class="marker orange"></span>
          <span>満員</span>
        </div>
        <div class="legend-item">
          <span class="marker green"></span>
          <span>正常</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import L from 'leaflet'
import axios from "axios";
import {ElMessage} from "element-plus";

const backUrl = import.meta.env.VITE_BACK_URL || 'http://localhost:3000'
const mapApiKey = import.meta.env.VITE_GOOGLE_MAP_LATLNG_API_KEY
const router = useRouter()
const mapContainer = ref<HTMLElement>()
let map: L.Map

interface Shelter {
  shelterId: string
  shelterName: string
  lat: number
  lng: number
  address: string
  adminId: string
  shelterCap: number
  shelterCur: number
  moneyCur: number
  moneyReq: number
  status: string
  contact: string
}

const shelters = ref<Shelter[]>([])
// const shelters = [
//   {
//     id: 'shelter1',
//     name: '中央小学校',
//     lat: 35.6762,
//     lng: 139.6503,
//     urgency: 'urgent',
//     currentCapacity: 180,
//     maxCapacity: 200,
//     recentRequests: 15,
//     urgentRequests: 8,
//     topRequests: ['ミネラルウォーター', '離乳食', '毛布', '常備薬']
//   },
//   {
//     id: 'shelter2',
//     name: '市民体育館',
//     lat: 35.6712,
//     lng: 139.6533,
//     urgency: 'needs-supplies',
//     currentCapacity: 90,
//     maxCapacity: 150,
//     recentRequests: 8,
//     urgentRequests: 3,
//     topRequests: ['おにぎり', 'タオル', '乾電池']
//   },
//   {
//     id: 'shelter3',
//     name: '総合公園体育館',
//     lat: 35.6792,
//     lng: 139.6473,
//     urgency: 'normal',
//     currentCapacity: 45,
//     maxCapacity: 100,
//     recentRequests: 4,
//     urgentRequests: 1,
//     topRequests: ['パン', 'マスク']
//   }
// ]

const getUrgencyText = (urgency: string) => {
  const map: Record<string, string> = {
    urgent: '緊急対応必要',
    'needs-supplies': '物資不足',
    full: '満員',
    normal: '正常'
  }
  return map[urgency] || '正常'
}

const getMarkerColor = (urgency: string) => {
  const colors: Record<string, string> = {
    urgent: 'red',
    'needs-supplies': 'orange',
    full: 'yellow',
    normal: 'green'
  }
  return colors[urgency] || 'green'
}

const viewShelterDetail = (shelterId: string) => {
  router.push(`/shelter/${shelterId}`)
}

const goBack = () => {
  localStorage.clear()
  router.push('/login')
}
const goHome = () => {
  router.push('/evacuee')
}

const initMap = () => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value).setView([35.6762, 139.6503], 14)
  
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map)

  shelters.value.forEach(shelter => {
    const color = getMarkerColor(shelter.status)
    const markerHtml = `
      <div style="
        background-color: ${color};
        width: 25px;
        height: 25px;
        border-radius: 50%;
        border: 3px solid white;
        box-shadow: 0 2px 4px rgba(0,0,0,0.3);
        display: flex;
        align-items: center;
        justify-content: center;
        color: white;
        font-weight: bold;
        font-size: 12px;
      ">
      </div>
    `

    const customIcon = L.divIcon({
      html: markerHtml,
      iconSize: [25, 25],
      iconAnchor: [12, 12]
    })
    
    const marker = L.marker([shelter.lat, shelter.lng], { icon: customIcon }).addTo(map)
    
    const popupContent = `
      <div>
        <h3>${shelter.shelterName}</h3>
        <p><strong>収容:</strong> ${shelter.shelterCur}/${shelter.shelterCap}人</p>

      </div>
    `
    
    marker.bindPopup(popupContent)
  })
}

onMounted(async () => {
  setTimeout(() => {
    initMap()
  }, 100)
  try {
    const res = await axios.get(backUrl + '/shelter/list')
    shelters.value = res.data // 假设后端返回的是避难所数组
    //shelters
  } catch (e) {
    ElMessage.error('避難所データの取得に失敗しました')
    return
  }
  // Global function for popup button
  ;(window as any).viewShelterFromMap = (shelterId: string) => {
    viewShelterDetail(shelterId)
  }
})
</script>

<style scoped>
.supporter-container {
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

.back-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.back-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}
.home-btn {
  background: #FF6B35;
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.map-container {
  position: relative;
  height: 400px;
  margin: 20px;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

#map {
  width: 100%;
  height: 100%;
}

.legend {
  position: absolute;
  top: 10px;
  right: 10px;
  background: white;
  padding: 15px;
  border-radius: 5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}

.legend h3 {
  margin: 0 0 10px 0;
  font-size: 14px;
}

.legend-item {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  font-size: 12px;
}

.marker {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 8px;
  border: 2px solid white;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.marker.red { background-color: red; }
.marker.yellow { background-color: yellow; }
.marker.orange { background-color: orange; }
.marker.green { background-color: green; }

.shelter-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.shelter-list h2 {
  color: #333;
  margin-bottom: 20px;
}

.shelter-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.shelter-card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
}

.shelter-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
}

.shelter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.shelter-header h3 {
  margin: 0;
  color: #333;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-high {
  background: #ffebee;
  color: #c62828;
}

.status-medium {
  background: #fff3e0;
  color: #f57c00;
}

.status-low {
  background: #e8f5e8;
  color: #2e7d32;
}

.shelter-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 5px;
}

.label {
  font-weight: 500;
  color: #666;
}

.value {
  color: #333;
}

.urgent-count {
  color: #c62828;
  font-weight: 600;
}

.shelter-requests h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  color: #333;
}

.request-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  margin-bottom: 15px;
}

.request-tag {
  background: #e3f2fd;
  color: #1976d2;
  padding: 3px 8px;
  border-radius: 3px;
  font-size: 12px;
}

.view-detail-btn {
  width: 100%;
  background: #2196F3;
  color: white;
  border: none;
  padding: 10px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
}

.view-detail-btn:hover {
  background: #1976D2;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .map-container {
    margin: 10px;
    height: 300px;
  }
  
  .legend {
    position: relative;
    margin-top: 10px;
  }
  
  .shelter-cards {
    grid-template-columns: 1fr;
  }
  
  .shelter-list {
    padding: 10px;
  }
}
</style>

<style>
@import 'leaflet/dist/leaflet.css';
</style>