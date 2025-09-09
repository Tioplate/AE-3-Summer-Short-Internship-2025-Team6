<template>
  <div class="map-container">
    <h2>避難所マップ管理</h2>
    
    <div class="map-controls">
      <div class="control-group">
        <label>表示フィルター:</label>
        <select v-model="mapFilter">
          <option value="all">すべて表示</option>
          <option value="urgent">緊急度高</option>
          <option value="needs-supplies">物資不足</option>
          <option value="full">満員</option>
        </select>
      </div>
      
      <div class="control-group">
        <button @click="refreshMap" class="refresh-btn">🔄 マップ更新</button>
        <button @click="exportData" class="export-btn">📊 データエクスポート</button>
      </div>
    </div>

    <div class="map-wrapper">
      <div id="admin-map" class="map"></div>
    </div>

    <div class="shelter-summary">
      <h3>避難所統計</h3>
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ shelterStats.total }}</div>
          <div class="stat-label">総避難所数</div>
        </div>
        <div class="stat-card urgent">
          <div class="stat-number">{{ shelterStats.urgent }}</div>
          <div class="stat-label">緊急対応必要</div>
        </div>
        <div class="stat-card warning">
          <div class="stat-number">{{ shelterStats.needsSupplies }}</div>
          <div class="stat-label">物資不足</div>
        </div>
        <div class="stat-card full">
          <div class="stat-number">{{ shelterStats.full }}</div>
          <div class="stat-label">満員</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import L from 'leaflet'
import 'leaflet/dist/leaflet.css'

const mapFilter = ref('all')
let map: L.Map | null = null

const shelterStats = ref({
  total: 15,
  urgent: 3,
  needsSupplies: 7,
  full: 2
})

const initMap = () => {
  if (!map) {
    map = L.map('admin-map').setView([35.6762, 139.6503], 12)
    
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© OpenStreetMap contributors'
    }).addTo(map)

    addShelterMarkers()
  }
}

const addShelterMarkers = () => {
  if (!map) return

  const sampleShelters = [
    { id: 1, name: '中央小学校', lat: 35.6762, lng: 139.6503, status: 'urgent', occupancy: 80 },
    { id: 2, name: '市民体育館', lat: 35.6800, lng: 139.6600, status: 'normal', occupancy: 45 },
    { id: 3, name: '北部コミュニティセンター', lat: 35.6900, lng: 139.6400, status: 'needs-supplies', occupancy: 60 },
  ]

  sampleShelters.forEach(shelter => {
    const color = getMarkerColor(shelter.status)
    const marker = L.circleMarker([shelter.lat, shelter.lng], {
      radius: 10,
      fillColor: color,
      color: '#000',
      weight: 2,
      fillOpacity: 0.8
    }).addTo(map!)

    marker.bindPopup(`
      <div>
        <h4>${shelter.name}</h4>
        <p>状態: ${getStatusText(shelter.status)}</p>
        <p>収容率: ${shelter.occupancy}%</p>
      </div>
    `)
  })
}

const getMarkerColor = (status: string) => {
  switch (status) {
    case 'urgent': return '#FF5722'
    case 'needs-supplies': return '#FF9800'
    case 'full': return '#9C27B0'
    default: return '#4CAF50'
  }
}

const getStatusText = (status: string) => {
  switch (status) {
    case 'urgent': return '緊急対応必要'
    case 'needs-supplies': return '物資不足'
    case 'full': return '満員'
    default: return '正常'
  }
}

const refreshMap = () => {
  if (map) {
    map.eachLayer(layer => {
      if (layer instanceof L.CircleMarker) {
        map!.removeLayer(layer)
      }
    })
    addShelterMarkers()
  }
}

const exportData = () => {
  console.log('Exporting shelter data...')
  alert('データをエクスポートしました')
}

onMounted(() => {
  setTimeout(initMap, 100)
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped>
.map-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.map-container h2 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.map-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.control-group {
  display: flex;
  align-items: center;
  gap: 10px;
}

.control-group label {
  font-weight: 500;
  color: #555;
}

.control-group select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.refresh-btn, .export-btn {
  padding: 8px 16px;
  border: 1px solid #007bff;
  background: #007bff;
  color: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.refresh-btn:hover, .export-btn:hover {
  background: #0056b3;
  border-color: #0056b3;
}

.export-btn {
  background: #28a745;
  border-color: #28a745;
}

.export-btn:hover {
  background: #218838;
  border-color: #218838;
}

.map-wrapper {
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.map {
  height: 400px;
  width: 100%;
}

.shelter-summary h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.stat-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-card.urgent {
  border-left: 4px solid #FF5722;
}

.stat-card.warning {
  border-left: 4px solid #FF9800;
}

.stat-card.full {
  border-left: 4px solid #9C27B0;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  color: #333;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #666;
  font-weight: 500;
}

@media (max-width: 768px) {
  .map-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .control-group {
    justify-content: space-between;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>