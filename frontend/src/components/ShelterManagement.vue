<template>
  <div class="shelter-management">
    <div class="section-header">
      <h2>避難所登録・管理</h2>
      <button @click="showAddModal = true" class="add-btn">➕ 新規避難所登録</button>
    </div>

    <div class="shelter-list">
      <div class="shelter-card" v-for="shelter in shelters" :key="shelter.shelterId">
        <div class="shelter-info">
          <h3>{{ shelter.shelterName }}</h3>
          <div class="info-row">
            <span class="label">住所:</span>
            <span>{{ shelter.address }}</span>
          </div>
          <div class="info-row">
            <span class="label">収容人数:</span>
            <span>{{ shelter.shelterCur }} / {{ shelter.shelterCap }} 人</span>
          </div>
          <div class="info-row">
            <span class="label">状態:</span>
            <span :class="['status', shelter.status]">{{ getStatusText(shelter.status) }}</span>
          </div>
          <div class="info-row">
            <span class="label">担当者:</span>
            <span>{{ shelter.adminId }}</span>
          </div>
        </div>
        
        <div class="shelter-actions">
          <button @click="editShelter(shelter)" class="edit-btn">✏️ 編集</button>
          <button @click="viewDetails(shelter)" class="detail-btn">👁️ 詳細</button>
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
            <input v-model="newShelter.shelterName" type="text" required>
          </div>
          
          <div class="form-group">
            <label>住所 *</label>
            <input v-model="newShelter.address" type="text" required>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>最大収容人数 *</label>
              <input v-model.number="newShelter.shelterCap" type="number" required>
            </div>
            
            <div class="form-group">
              <label>現在の収容人数</label>
              <input v-model.number="newShelter.shelterCur" type="number">
            </div>
          </div>
          
          <div class="form-group">
            <label>担当者連絡先</label>
            <input v-model="newShelter.contact" type="text">
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
            <input v-model="editingShelter.shelterName" type="text" required>
          </div>
          
          <div class="form-group">
            <label>住所 *</label>
            <input v-model="editingShelter.address" type="text" required>
          </div>
          
          <div class="form-row">
            <div class="form-group">
              <label>最大収容人数 *</label>
              <input v-model.number="editingShelter.shelterCap" type="number" required>
            </div>
            
            <div class="form-group">
              <label>現在の収容人数</label>
              <input v-model.number="editingShelter.shelterCur" type="number">
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
            <label>担当者連絡先</label>
            <input v-model="editingShelter.contact" type="text">
          </div>
          
          <div class="modal-actions">
            <button type="button" @click="closeModal" class="cancel-btn">キャンセル</button>
            <button type="submit" @click="updateSubmit" class="submit-btn">更新</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const mapApiKey = import.meta.env.VITE_GOOGLE_MAP_LATLNG_API_KEY
const backUrl = import.meta.env.VITE_BACK_URL
interface Shelter {
  shelterId: string
  shelterName: string
  address: string
  lat: number
  lng: number
  shelterCap: number
  shelterCur: number
  status: 'normal' | 'urgent' | 'needs-supplies' | 'full'
  adminId: string
  contact: string
  notes?: string
}

const showAddModal = ref(false)
const showEditModal = ref(false)

const newShelter = ref({
  shelterName: '',
  address: '',
  lat: 0,
  lng: 0,
  shelterCap: 100,
  shelterCur: 0,
  adminId: '',
  contact: '',
  status: 'normal'
})

const editingShelter = ref<Shelter>({
  shelterId: '',
  shelterName: '',
  address: '',
  lat: 0,
  lng: 0,
  shelterCap: 0,
  shelterCur: 0,
  status: 'normal',
  adminId: '',
  contact: ''
})

const shelters = ref<Shelter[]>([
  {
    shelterId: '1',
    shelterName: '中央小学校',
    address: '東京都渋谷区○○1-2-3',
    lat: 35.6762,
    lng: 139.6503,
    shelterCap: 200,
    shelterCur: 160,
    status: 'urgent',
    adminId: '田中太郎',
    contact: '090-1234-5678'
  },
  {
    shelterId: '2',
    shelterName: '市民体育館',
    address: '東京都渋谷区○○2-3-4',
    lat: 35.6800,
    lng: 139.6600,
    shelterCap: 300,
    shelterCur: 135,
    status: 'normal',
    adminId: '佐藤花子',
    contact: '090-2345-6789'
  },
  {
    shelterId: '3',
    shelterName: '北部コミュニティセンター',
    address: '東京都渋谷区○○3-4-5',
    lat: 35.6900,
    lng: 139.6400,
    shelterCap: 150,
    shelterCur: 90,
    status: 'needs-supplies',
    adminId: '鈴木一郎',
    contact: '090-3456-7890'
  }
])
onMounted(async () => {
  try {
    const res = await axios.get(backUrl + '/shelter/list')
    res.data.forEach ((shelter: Shelter) => {
      console.log(shelter)
      console.log(res)
    })
    shelters.value = res.data // 假设后端返回的是避难所数组
    //shelters
  } catch (e) {
    ElMessage.error('避難所データの取得に失敗しました')
  }
})
const updateSubmit = async () => {
  try {
    const search = await axios.get(backUrl + '/shelter/getById', {
      params: { shelterId: editingShelter.value.shelterId }
    })
    if(search.data.length == 0){
      ElMessage.error('避難所情報の取得エラー')
      return null
    }
    const shelterData = {
      shelterId: editingShelter.value.shelterId,
      shelterName: editingShelter.value.shelterName,
      address: editingShelter.value.address,
      shelterCap: editingShelter.value.shelterCap,
      shelterCur: editingShelter.value.shelterCur,
      adminId: localStorage.getItem('userId'),
      moneyCur:search.data.moneyCur,
      moneyReq:search.data.moneyReq,
      status: editingShelter.value.status,
      contact: editingShelter.value.contact
    }
    const res = await axios.post(backUrl + '/shelter/update', editingShelter.value)
    if(res.data != 1){
      ElMessage.error('避難所情報の更新に失敗しました')
      return null
    }
    ElMessage.success('避難所情報を更新しました')
    closeModal()
  } catch (e) {
    ElMessage.error('避難所情報の更新エラー')
    return null
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
const randomUUID = () => {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, (c) => {
    const r = crypto.getRandomValues(new Uint8Array(1))[0] % 16;
    const v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  });
}
const addShelter = async () => {
  const url = `https://maps.googleapis.com/maps/api/geocode/json?address=${newShelter.value.address}&key=${mapApiKey}`
  try {
    const res = await axios.get(url)
    const data = res.data
    if (data.status === 'OK' && data.results[0]) {
      const location = data.results[0].geometry.location
      // location.lat, location.lng 即为经纬度
      newShelter.value.lat = location.lat
      newShelter.value.lng = location.lng
      const shelter: Shelter = {
        shelterId: randomUUID(),
        ...newShelter.value,
        status: 'normal'
      }
      const shelterData = {
        shelterId: shelter.shelterId,
        shelterName: shelter.shelterName,
        address: shelter.address,
        shelterCap: shelter.shelterCap,
        shelterCur: shelter.shelterCur,
        adminId: localStorage.getItem('userId'),
        moneyCur:0,
        moneyReq:0,
        status: shelter.status,
        contact: shelter.contact,
        lat: shelter.lat,
        lng: shelter.lng
      }
      const dataResponse = await axios.post(backUrl + '/shelter/insert', shelterData)
      if(dataResponse.data != 1){
        ElMessage.error('避難所の登録に失敗しました')
        return null
      }
      shelters.value.push(shelter)
      ElMessage.success('避難所を登録しました')
      closeModal()
      resetNewShelter()
    } else {
      ElMessage.error('经纬度を取得できませんでした')
      return null
    }
  } catch (e) {
    alert('经纬度取得時にエラーが発生しました')
    return null
  }
}

const editShelter = (shelter: Shelter) => {
  editingShelter.value = { ...shelter }
  showEditModal.value = true
}

const updateShelter = () => {
  const index = shelters.value.findIndex(s => s.shelterId === editingShelter.value.shelterId)
  if (index !== -1) {
    shelters.value[index] = { ...editingShelter.value }
  }
  closeModal()
}

const deleteShelter = (id: string) => {
  if (confirm('この避難所を削除してもよろしいですか？')) {
    shelters.value = shelters.value.filter(s => s.shelterId !== id)
  }
}

const viewDetails = (shelter: Shelter) => {
  alert(`避難所詳細: ${shelter.shelterName}\n収容状況: ${shelter.shelterCur}/${shelter.shelterCap}人`)
}

const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
}

const resetNewShelter = () => {
  newShelter.value = {
    shelterName: '',
    address: '',
    lat: 0,
    lng: 0,
    shelterCap: 100,
    shelterCur: 0,
    adminId: '',
    contact: '',
    status: 'normal'
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