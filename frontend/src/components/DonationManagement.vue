<template>
  <div class="donation-management">
    <h2>届いた支援物資管理</h2>
    
    <div class="received-supplies">
      <div 
        v-for="item in receivedSupplies" 
        :key="item.id"
        class="supply-card"
      >
        <div class="supply-header">
          <h3>{{ item.itemName }}</h3>
          <span :class="['status', item.status]">{{ getStatusText(item.status) }}</span>
        </div>
        
        <div class="supply-info">
          <p><strong>避難所:</strong> {{ item.shelterName }}</p>
          <p><strong>到着日時:</strong> {{ formatDate(item.deliveryDate) }}</p>
          <p><strong>申請個数:</strong> {{ item.requestedQuantity }}{{ item.unit }}</p>
        </div>

        <div class="quantity-input">
          <label>実際の個数:</label>
          <input 
            :value="item.actualQuantity" 
            @input="updateQuantity(item.id, ($event.target as HTMLInputElement).value)"
            type="number" 
            :min="0"
            class="quantity-field"
          />
          <span class="unit">{{ item.unit }}</span>
        </div>

        <div class="actions">
          <button 
            v-if="item.status === 'pending_confirmation'"
            @click="confirmReceipt(item.id)"
            class="confirm-btn"
          >
            ✓ 受取確認
          </button>
          <button 
            @click="viewDetail(item)"
            class="detail-btn"
          >
            詳細
          </button>
        </div>
      </div>
    </div>

    <div v-if="receivedSupplies.length === 0" class="empty-message">
      <p>現在、届いた支援物資はありません。</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useSuppliesStore, type ReceivedSupply } from '@/stores/supplies'

const suppliesStore = useSuppliesStore()

// サンプルデータを初期化
if (suppliesStore.receivedSupplies.length === 0) {
  suppliesStore.receivedSupplies.push(
    {
      id: 1,
      itemName: '毛布',
      requestedQuantity: 100,
      actualQuantity: 100,
      unit: '枚',
      shelterName: '中央小学校',
      deliveryDate: '2024-01-15T10:30:00Z',
      status: 'pending_confirmation'
    },
    {
      id: 2,
      itemName: 'ペットボトル水',
      requestedQuantity: 500,
      actualQuantity: 480,
      unit: '本',
      shelterName: '市民体育館',
      deliveryDate: '2024-01-16T14:45:00Z',
      status: 'confirmed'
    }
  )
}

const receivedSupplies = computed(() => suppliesStore.receivedSupplies)

const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending_confirmation: '確認待ち',
    confirmed: '確認済み'
  }
  return texts[status] || status
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return `${date.getFullYear()}年${date.getMonth() + 1}月${date.getDate()}日 ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

const updateQuantity = (itemId: number, value: string) => {
  const quantity = parseInt(value) || 0
  suppliesStore.updateActualQuantity(itemId, quantity)
}

const confirmReceipt = (itemId: number) => {
  suppliesStore.confirmReceipt(itemId)
}

const viewDetail = (item: ReceivedSupply) => {
  console.log('詳細表示:', item)
  alert(`詳細情報:\n\n物資名: ${item.itemName}\n避難所: ${item.shelterName}\n申請個数: ${item.requestedQuantity}${item.unit}\n実際個数: ${item.actualQuantity}${item.unit}\n到着日時: ${formatDate(item.deliveryDate)}`)
}
</script>

<style scoped>
.donation-management {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.donation-management h2 {
  color: #333;
  margin-bottom: 20px;
  font-size: 20px;
  font-weight: 600;
}

.received-supplies {
  display: grid;
  gap: 20px;
}

.supply-card {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  background: #fafafa;
}

.supply-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.supply-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.status {
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.status.pending_confirmation {
  background: #fff3cd;
  color: #856404;
}

.status.confirmed {
  background: #d4edda;
  color: #155724;
}

.supply-info {
  margin-bottom: 20px;
  background: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.supply-info p {
  margin: 5px 0;
  font-size: 14px;
  color: #555;
}

.quantity-input {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  background: white;
  padding: 15px;
  border-radius: 6px;
  border: 1px solid #e9ecef;
}

.quantity-input label {
  font-weight: 500;
  color: #333;
  min-width: 80px;
}

.quantity-field {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  width: 100px;
  text-align: right;
}

.unit {
  color: #666;
  font-size: 14px;
}

.actions {
  display: flex;
  gap: 10px;
}

.confirm-btn, .detail-btn {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.confirm-btn {
  background: #28a745;
  color: white;
  border-color: #28a745;
}

.confirm-btn:hover {
  background: #218838;
  border-color: #218838;
}

.detail-btn {
  background: white;
  color: #333;
}

.detail-btn:hover {
  background: #f8f9fa;
}

.empty-message {
  text-align: center;
  padding: 40px 20px;
  color: #666;
  font-size: 16px;
}

@media (max-width: 768px) {
  .supply-header {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
  
  .quantity-input {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .actions {
    flex-direction: column;
  }
}
</style>