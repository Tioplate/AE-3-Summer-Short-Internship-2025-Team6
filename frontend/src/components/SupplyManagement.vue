<template>
  <div class="supply-management">
    <div class="section-header">
      <h2>物資申請管理</h2>
      <div class="status-filter">
        <select v-model="statusFilter">
          <option value="all">すべて</option>
          <option value="pending">申請中</option>
          <option value="approved">承認済み</option>
          <option value="delivered">配送済み</option>
        </select>
      </div>
    </div>

    <div class="shelter-requests">
      <div 
        v-for="shelter in filteredShelters" 
        :key="shelter.id"
        class="shelter-card"
      >
        <div class="shelter-header">
          <h3>{{ shelter.name }}</h3>
          <span :class="['urgency', shelter.urgency]">
            {{ getUrgencyText(shelter.urgency) }}
          </span>
        </div>
        
        <div class="shelter-info">
          <p><strong>担当者:</strong> {{ shelter.manager }}</p>
          <p><strong>連絡先:</strong> {{ shelter.contact }}</p>
          <p><strong>申請日時:</strong> {{ formatDate(shelter.requestDate) }}</p>
        </div>

        <div class="supply-requests">
          <h4>申請物資一覧</h4>
          <div class="request-list">
            <div 
              v-for="request in shelter.requests" 
              :key="request.id"
              class="request-item"
            >
              <div class="request-info">
                <span class="item-name">{{ request.itemName }}</span>
                <span class="quantity">{{ request.quantity }}{{ request.unit }}</span>
                <span :class="['status', request.status]">{{ getStatusText(request.status) }}</span>
              </div>
              <div class="request-actions">
                <button 
                  v-if="request.status === 'pending'"
                  @click="approveRequest(shelter.id, request.id)"
                  class="approve-btn"
                >
                  承認
                </button>
                <button 
                  v-if="request.status === 'approved'"
                  @click="markDelivered(shelter.id, request.id)"
                  class="deliver-btn"
                >
                  配送完了
                </button>
                <button 
                  @click="viewRequestDetail(request)"
                  class="detail-btn"
                >
                  詳細
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 物資申請詳細モーダル -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <h3>物資申請詳細</h3>
        <div class="detail-content">
          <div class="detail-row">
            <span class="label">物資名:</span>
            <span>{{ selectedRequest.itemName }}</span>
          </div>
          <div class="detail-row">
            <span class="label">数量:</span>
            <span>{{ selectedRequest.quantity }}{{ selectedRequest.unit }}</span>
          </div>
          <div class="detail-row">
            <span class="label">優先度:</span>
            <span>{{ selectedRequest.priority }}</span>
          </div>
          <div class="detail-row">
            <span class="label">申請理由:</span>
            <span>{{ selectedRequest.reason }}</span>
          </div>
          <div class="detail-row" v-if="selectedRequest.notes">
            <span class="label">備考:</span>
            <span>{{ selectedRequest.notes }}</span>
          </div>
        </div>
        <div class="modal-actions">
          <button @click="closeModal" class="close-btn">閉じる</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useSuppliesStore, type SupplyRequest, type ShelterRequest } from '@/stores/supplies'

const suppliesStore = useSuppliesStore()

const statusFilter = ref('all')
const showDetailModal = ref(false)

const selectedRequest = ref<SupplyRequest>({
  id: 0,
  itemName: '',
  quantity: 0,
  unit: '',
  status: 'pending',
  priority: '',
  reason: '',
  notes: ''
})

// 初期データをストアに設定
if (suppliesStore.shelters.length === 0) {
  suppliesStore.shelters.push(
    {
      id: 1,
      name: '中央小学校',
      manager: '田中太郎',
      contact: '090-1234-5678',
      urgency: 'high',
      requestDate: '2024-01-15 14:30',
      requests: [
        {
          id: 101,
          itemName: '非常用パン',
          quantity: 200,
          unit: '個',
          status: 'pending',
          priority: '緊急',
          reason: '避難者数が増加し、食料が不足しています'
        },
        {
          id: 102,
          itemName: 'ペットボトル水',
          quantity: 500,
          unit: '本',
          status: 'approved',
          priority: '緊急',
          reason: '水道が復旧しておらず、飲料水が必要です'
        },
        {
          id: 103,
          itemName: '毛布',
          quantity: 100,
          unit: '枚',
          status: 'delivered',
          priority: '普通',
          reason: '夜間の冷え込みが厳しく、暖房器具が不足'
        }
      ]
    },
    {
      id: 2,
      name: '市民体育館',
      manager: '佐藤花子',
      contact: '090-2345-6789',
      urgency: 'medium',
      requestDate: '2024-01-15 16:45',
      requests: [
        {
          id: 201,
          itemName: 'マスク',
          quantity: 300,
          unit: '枚',
          status: 'pending',
          priority: '普通',
          reason: '感染症対策のため必要です'
        },
        {
          id: 202,
          itemName: '消毒用アルコール',
          quantity: 10,
          unit: '本',
          status: 'pending',
          priority: '普通',
          reason: '手指消毒用として使用します'
        }
      ]
    },
    {
      id: 3,
      name: '北部コミュニティセンター',
      manager: '鈴木一郎',
      contact: '090-3456-7890',
      urgency: 'low',
      requestDate: '2024-01-16 09:15',
      requests: [
        {
          id: 301,
          itemName: '紙おむつ',
          quantity: 50,
          unit: 'パック',
          status: 'approved',
          priority: '普通',
          reason: '乳幼児の避難者がいるため必要です',
          notes: 'Mサイズ中心でお願いします'
        }
      ]
    }
  )
}

const filteredShelters = computed(() => {
  if (statusFilter.value === 'all') {
    return suppliesStore.shelters
  }
  return suppliesStore.shelters.filter(shelter => 
    shelter.requests.some(request => request.status === statusFilter.value)
  ).map(shelter => ({
    ...shelter,
    requests: shelter.requests.filter(request => request.status === statusFilter.value)
  }))
})

const getUrgencyText = (urgency: string) => {
  const texts: Record<string, string> = {
    high: '緊急',
    medium: '普通',
    low: '低'
  }
  return texts[urgency] || urgency
}

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '申請中',
    approved: '承認済み',
    delivered: '配送済み'
  }
  return texts[status] || status
}

const formatDate = (dateString: string) => {
  return dateString
}

const approveRequest = (shelterId: number, requestId: number) => {
  suppliesStore.approveRequest(shelterId, requestId)
}

const markDelivered = (shelterId: number, requestId: number) => {
  suppliesStore.markDelivered(shelterId, requestId)
}

const viewRequestDetail = (request: SupplyRequest) => {
  selectedRequest.value = { ...request }
  showDetailModal.value = true
}

const closeModal = () => {
  showDetailModal.value = false
}
</script>

<style scoped>
.supply-management {
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

.status-filter select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.shelter-requests {
  display: grid;
  gap: 20px;
}

.shelter-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
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
  font-size: 18px;
  font-weight: 600;
}

.urgency {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.urgency.high {
  background: #f8d7da;
  color: #721c24;
}

.urgency.medium {
  background: #fff3cd;
  color: #856404;
}

.urgency.low {
  background: #d1ecf1;
  color: #0c5460;
}

.shelter-info {
  margin-bottom: 20px;
  background: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.shelter-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #555;
}

.supply-requests h4 {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.request-list {
  display: grid;
  gap: 10px;
}

.request-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.request-info {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.item-name {
  font-weight: 600;
  color: #333;
  min-width: 120px;
}

.quantity {
  color: #666;
  min-width: 80px;
}

.status {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
  min-width: 60px;
  text-align: center;
}

.status.pending {
  background: #fff3cd;
  color: #856404;
}

.status.approved {
  background: #d1ecf1;
  color: #0c5460;
}

.status.delivered {
  background: #d4edda;
  color: #155724;
}

.request-actions {
  display: flex;
  gap: 8px;
}

.approve-btn, .deliver-btn, .detail-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.approve-btn {
  background: #28a745;
  color: white;
  border-color: #28a745;
}

.approve-btn:hover {
  background: #218838;
  border-color: #218838;
}

.deliver-btn {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.deliver-btn:hover {
  background: #0056b3;
  border-color: #0056b3;
}

.detail-btn {
  background: white;
}

.detail-btn:hover {
  background: #f8f9fa;
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
  max-width: 500px;
  width: 90%;
}

.modal h3 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 20px;
  font-weight: 600;
}

.detail-content {
  margin-bottom: 20px;
}

.detail-row {
  display: flex;
  margin-bottom: 10px;
  align-items: flex-start;
}

.detail-row .label {
  font-weight: 500;
  color: #555;
  min-width: 100px;
  margin-right: 10px;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
}

.close-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #5a6268;
}

@media (max-width: 768px) {
  .section-header {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .request-item {
    flex-direction: column;
    gap: 15px;
    align-items: stretch;
  }
  
  .request-actions {
    justify-content: flex-start;
  }
  
  .modal {
    margin: 10px;
    padding: 20px;
  }
}
</style>