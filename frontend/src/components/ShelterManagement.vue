<template>
  <div class="shelter-management">
    <div class="section-header">
      <h2>避難所登録・管理</h2>
      <button @click="showAddModal = true" class="add-btn">➕ 新規避難所登録</button>
    </div>

    <div class="shelter-list">
      <div class="shelter-card" v-for="shelter in shelters" :key="shelter.id">
        <div class="shelter-info">
          <h3>{{ shelter.name }}</h3>
          <div class="info-row">
            <span class="label">住所:</span>
            <span>{{ shelter.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">収容人数:</span>
            <span>{{ shelter.currentOccupancy }} / {{ shelter.maxCapacity }} 人</span>
          </div>
          <div class="info-row">
            <span class="label">状態:</span>
            <span :class="['status', shelter.status]">{{ getStatusText(shelter.status) }}</span>
          </div>
          <div class="info-row">
            <span class="label">担当者:</span>
            <span>{{ shelter.manager }}</span>
          </div>
        </div>
        
        <div class="shelter-actions">
          <button @click="editShelter(shelter)" class="edit-btn">✏️ 編集</button>
          <button @click="viewDetails(shelter)" class="detail-btn">👁️ 詳細</button>
          <button @click="deleteShelter(shelter.id)" class="delete-btn">🗑️ 削除</button>
        </div>
      </div>
    </div>

    <!-- 新規登録モーダル -->
    <div v-if="showAddModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h3>新規避難所登録</h3>
        <form @submit.prevent="addShelter">
          <div class="form-group">
            <label>避難所名 *</label>
            <input v-model="newShelter.name" type="text" required>
          </div>
          
          <div class="form-group">
            <label>住所 *</label>
            <input v-model="newShelter.address" type="text" required>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>緯度 *</label>
              <input v-model.number="newShelter.lat" type="number" step="0.000001" required>
            </div>
            
            <div class="form-group">
              <label>経度 *</label>
              <input v-model.number="newShelter.lng" type="number" step="0.000001" required>
            </div>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>最大収容人数 *</label>
              <input v-model.number="newShelter.maxCapacity" type="number" required>
            </div>
            
            <div class="form-group">
              <label>現在の収容人数</label>
              <input v-model.number="newShelter.currentOccupancy" type="number">
            </div>
          </div>
          
          <div class="form-group">
            <label>担当者名 *</label>
            <input v-model="newShelter.manager" type="text" required>
          </div>
          
          <div class="form-group">
            <label>担当者連絡先</label>
            <input v-model="newShelter.contact" type="text">
          </div>
          
          <div class="form-group">
            <label>備考</label>
            <textarea v-model="newShelter.notes" rows="3"></textarea>
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">キャンセル</button>
            <button type="submit" class="submit-btn">登録</button>
          </div>
        </form>
      </div>
    </div>

    <!-- 編集モーダル -->
    <div v-if="showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h3>避難所情報編集</h3>
        <form @submit.prevent="updateShelter">
          <div class="form-group">
            <label>避難所名 *</label>
            <input v-model="editingShelter.name" type="text" required>
          </div>
          
          <div class="form-group">
            <label>住所 *</label>
            <input v-model="editingShelter.address" type="text" required>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>最大収容人数 *</label>
              <input v-model.number="editingShelter.maxCapacity" type="number" required>
            </div>
            
            <div class="form-group">
              <label>現在の収容人数</label>
              <input v-model.number="editingShelter.currentOccupancy" type="number">
            </div>
          </div>
          
          <div class="form-group">
            <label>状態</label>
            <select v-model="editingShelter.status">
              <option value="normal">正常</option>
              <option value="urgent">緊急対応必要</option>
              <option value="needs-supplies">物資不足</option>
              <option value="full">満員</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>担当者名 *</label>
            <input v-model="editingShelter.manager" type="text" required>
          </div>
          
          <div class="form-group">
            <label>担当者連絡先</label>
            <input v-model="editingShelter.contact" type="text">
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">キャンセル</button>
            <button type="submit" class="submit-btn">更新</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

interface Shelter {
  id: number
  name: string
  address: string
  lat: number
  lng: number
  maxCapacity: number
  currentOccupancy: number
  status: 'normal' | 'urgent' | 'needs-supplies' | 'full'
  manager: string
  contact: string
  notes?: string
}

const showAddModal = ref(false)
const showEditModal = ref(false)

const newShelter = ref({
  name: '',
  address: '',
  lat: 35.6762,
  lng: 139.6503,
  maxCapacity: 100,
  currentOccupancy: 0,
  manager: '',
  contact: '',
  notes: ''
})

const editingShelter = ref<Shelter>({
  id: 0,
  name: '',
  address: '',
  lat: 0,
  lng: 0,
  maxCapacity: 0,
  currentOccupancy: 0,
  status: 'normal',
  manager: '',
  contact: ''
})

const shelters = ref<Shelter[]>([
  {
    id: 1,
    name: '中央小学校',
    address: '東京都渋谷区○○1-2-3',
    lat: 35.6762,
    lng: 139.6503,
    maxCapacity: 200,
    currentOccupancy: 160,
    status: 'urgent',
    manager: '田中太郎',
    contact: '090-1234-5678'
  },
  {
    id: 2,
    name: '市民体育館',
    address: '東京都渋谷区○○2-3-4',
    lat: 35.6800,
    lng: 139.6600,
    maxCapacity: 300,
    currentOccupancy: 135,
    status: 'normal',
    manager: '佐藤花子',
    contact: '090-2345-6789'
  },
  {
    id: 3,
    name: '北部コミュニティセンター',
    address: '東京都渋谷区○○3-4-5',
    lat: 35.6900,
    lng: 139.6400,
    maxCapacity: 150,
    currentOccupancy: 90,
    status: 'needs-supplies',
    manager: '鈴木一郎',
    contact: '090-3456-7890'
  }
])

const getStatusText = (status: string) => {
  switch (status) {
    case 'urgent': return '緊急対応必要'
    case 'needs-supplies': return '物資不足'
    case 'full': return '満員'
    default: return '正常'
  }
}

const addShelter = () => {
  const shelter: Shelter = {
    id: Date.now(),
    ...newShelter.value,
    status: 'normal'
  }
  shelters.value.push(shelter)
  closeModal()
  resetNewShelter()
}

const editShelter = (shelter: Shelter) => {
  editingShelter.value = { ...shelter }
  showEditModal.value = true
}

const updateShelter = () => {
  const index = shelters.value.findIndex(s => s.id === editingShelter.value.id)
  if (index !== -1) {
    shelters.value[index] = { ...editingShelter.value }
  }
  closeModal()
}

const deleteShelter = (id: number) => {
  if (confirm('この避難所を削除してもよろしいですか？')) {
    shelters.value = shelters.value.filter(s => s.id !== id)
  }
}

const viewDetails = (shelter: Shelter) => {
  alert(`避難所詳細: ${shelter.name}\n収容状況: ${shelter.currentOccupancy}/${shelter.maxCapacity}人`)
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
}

const resetNewShelter = () => {
  newShelter.value = {
    name: '',
    address: '',
    lat: 35.6762,
    lng: 139.6503,
    maxCapacity: 100,
    currentOccupancy: 0,
    manager: '',
    contact: '',
    notes: ''
  }
}
</script>

<style scoped>
.shelter-management {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
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
  font-size: 20px;
  font-weight: 600;
}

.add-btn {
  padding: 10px 20px;
  background: #28a745;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.add-btn:hover {
  background: #218838;
}

.shelter-list {
  display: grid;
  gap: 20px;
}

.shelter-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  transition: all 0.3s ease;
}

.shelter-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.shelter-info h3 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.info-row {
  display: flex;
  margin-bottom: 8px;
  align-items: center;
}

.label {
  font-weight: 500;
  color: #555;
  min-width: 100px;
  margin-right: 10px;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status.normal {
  background: #d4edda;
  color: #155724;
}

.status.urgent {
  background: #f8d7da;
  color: #721c24;
}

.status.needs-supplies {
  background: #fff3cd;
  color: #856404;
}

.status.full {
  background: #e2e3e5;
  color: #383d41;
}

.shelter-actions {
  display: flex;
  gap: 8px;
  flex-shrink: 0;
}

.edit-btn, .detail-btn, .delete-btn {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s ease;
}

.edit-btn:hover {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.detail-btn:hover {
  background: #28a745;
  color: white;
  border-color: #28a745;
}

.delete-btn:hover {
  background: #dc3545;
  color: white;
  border-color: #dc3545;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 8px;
  padding: 30px;
  max-width: 600px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.modal h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.form-group {
  margin-bottom: 15px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
  color: #555;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  box-sizing: border-box;
}

.form-group textarea {
  resize: vertical;
}

.modal-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 20px;
}

.cancel-btn, .submit-btn {
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.cancel-btn {
  background: #6c757d;
  color: white;
  border: 1px solid #6c757d;
}

.cancel-btn:hover {
  background: #5a6268;
  border-color: #5a6268;
}

.submit-btn {
  background: #007bff;
  color: white;
  border: 1px solid #007bff;
}

.submit-btn:hover {
  background: #0056b3;
  border-color: #0056b3;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .shelter-card {
    flex-direction: column;
    gap: 15px;
  }
  
  .shelter-actions {
    justify-content: flex-start;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .modal {
    margin: 10px;
    padding: 20px;
  }
}
</style>