<template>
  <div class="supporter-container">
    <header class="header">
      <h1>支援者マップ - 避難所一覧</h1>
      <button @click="goBack" class="back-btn">← ログイン画面に戻る</button>
    </header>

    <div class="map-container">
      <div id="map" ref="mapContainer"></div>
      
      <div class="legend">
        <h3>避難所の状況</h3>
        <div class="legend-item">
          <span class="marker red"></span>
          <span>緊急支援が必要</span>
        </div>
        <div class="legend-item">
          <span class="marker yellow"></span>
          <span>支援が必要</span>
        </div>
        <div class="legend-item">
          <span class="marker green"></span>
          <span>状況良好</span>
        </div>
      </div>
    </div>

    <div class="shelter-list">
      <h2>避難所一覧</h2>
      <div class="shelter-cards">
        <div 
          v-for="shelter in shelters" 
          :key="shelter.id"
          class="shelter-card"
          @click="viewShelterDetail(shelter.id)"
        >
          <div class="shelter-header">
            <h3>{{ shelter.name }}</h3>
            <span 
              class="status-badge"
              :style="{
                background: getProgressColor(getProgress(shelter.id)),
                color: '#fff'
              }"
            >
              {{ getProgressUrgencyText(getProgress(shelter.id)) }}
            </span>
          </div>
          
          <div class="shelter-info">
            <div class="info-item">
              <span class="label">収容人数:</span>
              <span class="value">{{ shelter.currentCapacity }} / {{ shelter.maxCapacity }}人</span>
            </div>
            <!-- 必要支援金額と進捗ゲージ -->
            <div class="info-item">
              <span class="label">必要支援金額:</span>
              <span class="value">¥{{ getNeededAmount(shelter.id) }}</span>
            </div>
            <div class="info-item">
              <span class="label">支援進捗:</span>
              <span class="value">¥{{ shelter.currentSupport }} / ¥{{ getNeededAmount(shelter.id) }}</span>
            </div>
            <div class="progress-bar">
              <!-- <div class="progress" :style="{ width: getProgress(shelter.id) + '%' }"></div> -->
              <div 
                class="progress"
                :style="{
                  width: getProgress(shelter.id) + '%',
                  background: getProgressColor(getProgress(shelter.id))
                }"
              ></div>
            </div>
            <div class="info-item">
              <span class="label">進捗率:</span>
              <span class="value">{{ getProgress(shelter.id).toFixed(1) }}%</span>
            </div>
          </div>
          
          <div class="shelter-requests">
            <h4>主な物資要請</h4>
            <div class="request-tags">
              <span 
                v-for="request in shelter.topRequests" 
                :key="request"
                class="request-tag"
              >
                {{ request }}
              </span>
            </div>
          </div>
          
          <button class="view-detail-btn">
            詳細を見る →
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">

import { onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import L from 'leaflet'
import { shelters } from '../stores/shelters'
import axios from 'axios'

const router = useRouter()
const mapContainer = ref<HTMLElement>()
let map: L.Map

const items = [
  { name: 'ミネラルウォーター', price : 100 },
  { name: 'おにぎり', price: 150 },
  { name: 'パン', price: 120 },
  { name: 'マスク', price: 200 },
  { name: 'タオル', price: 150 },
  { name: '非常食', price: 300 },
  { name: '毛布', price: 250 },
  { name: 'おむつ', price: 180 },
  { name: '救急セット', price: 500 },
  { name: '衣類', price: 400 },
  { name: '衛生用品', price: 250 },
  { name: '離乳食', price: 300 },
  { name: '常備薬', price: 450 },
  { name: '乾電池', price: 200 }
]

// 進捗率に応じた色を返す関数
const getProgressColor = (progress: number) => {
  if (progress <= 30) return '#e53935'; // 赤
  if (progress <= 60) return '#fbc02d'; // 黄
  return '#43a047'; // 緑
}


const getProgressUrgencyText = (progress: number) => {
  if (progress <= 30) return '緊急支援必要'; // 赤
  if (progress <= 60) return '支援必要';     // 黄
  return '状況良好';                        // 緑
}




// --- 価格取得ロジック切り替え ---

// ▼ダミーデータ版（ローカルitems配列から価格取得）
const getItemPrice = (name: string) => {
  const item = items.find(i => i.name === name)
  return item ? item.price : 0
}


// 必要支援金額を計算する関数
const getNeededAmount = (shelterID: number) => {
  const shelter = shelters.find(s => s.id === shelterID)
  let total = 0
  if (!shelter || !shelter.topRequests || !shelter.requestQuantities) return 0
  shelter.topRequests.forEach(name => {
    const quantity = shelter.requestQuantities[name] || 0
    const price = getItemPrice(name)
    if (price == null || isNaN(price)) return
    total += price * quantity
  })
  if (isNaN(total) || total == null) return 0
  return total
}

// 進捗率を計算する関数
const getProgress = (shelterID: number): number => {
  const shelter = shelters.find(s => s.id === shelterID)
  if (!shelter || typeof shelter.currentSupport !== 'number') return 0
  const needed = getNeededAmount(shelterID)
  if (needed === 0) return 0
  const progress = Math.min((shelter.currentSupport / needed) * 100, 100)
  return progress
}

const viewShelterDetail = (shelterId: string) => {
  router.push(`/shelter/${shelterId}`)
}

const goBack = () => {
  router.push('/')
}

const initMap = () => {
  if (!mapContainer.value) return

  map = L.map(mapContainer.value).setView([35.6762, 139.6503], 14)
  
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© OpenStreetMap contributors'
  }).addTo(map)

  shelters.forEach(shelter => {
    const progress = getProgress(shelter.id)
    const color = getProgressColor(progress) // 進捗率に応じた色
    const markerHtml = `
  <div style="
    position: relative;
    width: 25px;
    height: 35px;
    background: transparent;
  ">
    <div style="
      width: 25px;
      height: 25px;
      background: ${color};
      border-radius: 50%;
      border: 3px solid white;
      box-shadow: 0 2px 4px rgba(0,0,0,0.3);
      position: absolute;
      top: 0;
      left: 0;
      z-index: 2;
    "></div>
    <div style="
      position: absolute;
      left: 50%;
      bottom: 0;
      transform: translateX(-50%);
      width: 0;
      height: 0;
      border-left: 7px solid transparent;
      border-right: 7px solid transparent;
      border-top: 12px solid ${color};
    "></div>
  </div>
    `
    
    const customIcon = L.divIcon({
      html: markerHtml,
      iconSize: [0, 0],      // ← 高さ15pxに変更
      iconAnchor: [12, 35]     // ← 下端中央に変更
    })
    
    const marker = L.marker([shelter.lat, shelter.lng], { icon: customIcon }).addTo(map)
    
    const popupContent = `
      <div>
        <h3>${shelter.name}</h3>
        <p><strong>収容:</strong> ${shelter.currentCapacity}/${shelter.maxCapacity}人</p>
        <p><strong>支援進捗:</strong> ¥${shelter.currentSupport} / ¥${getNeededAmount(shelter.id)}</p>
        <div class="progress-bar" style="
          background: #e0e0e0;
          border-radius: 4px;
          height: 8px;
          overflow: hidden;
          margin: 10px 0;
        ">
          <div class="progress" style="
            height: 100%;
            background: ${color};
            width: ${progress}%;
            transition: width 0.4s;
          "></div>
        </div>
        <p><strong>進捗率:</strong> ${progress.toFixed(1)}%</p>
        <button onclick="window.viewShelterFromMap('${shelter.id}')" style="
          background: #2196F3;
          color: white;
          border: none;
          padding: 8px 16px;
          border-radius: 4px;
          cursor: pointer;
          margin-top: 8px;
        ">詳細を見る</button>
      </div>
    `
    
    marker.bindPopup(popupContent)
  })
}

onMounted(() => {
  setTimeout(() => {
    initMap()
  }, 100)
  
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
.marker.yellow { background-color: orange; }
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

.progress-bar {
  background: #e0e0e0;
  border-radius: 4px;
  height: 8px;
  overflow: hidden;
  margin: 10px 0;
}

.progress {
  height: 100%;
  width: 0;
  transition: width 0.4s;
  /* 色はJSで動的に指定 */
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