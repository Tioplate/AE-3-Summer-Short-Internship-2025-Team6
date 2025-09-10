import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export interface SupplyRequest {
  id: number
  itemName: string
  quantity: number
  unit: string
  status: 'pending' | 'approved' | 'delivered'
  priority: string
  reason: string
  notes?: string
}

export interface ShelterRequest {
  id: number
  name: string
  manager: string
  contact: string
  urgency: 'high' | 'medium' | 'low'
  requestDate: string
  requests: SupplyRequest[]
}

export interface ReceivedSupply {
  id: number
  itemName: string
  requestedQuantity: number
  actualQuantity: number
  unit: string
  shelterName: string
  deliveryDate: string
  status: 'pending_confirmation' | 'confirmed'
}

export const useSuppliesStore = defineStore('supplies', () => {
  const shelters = ref<ShelterRequest[]>([])
  const receivedSupplies = ref<ReceivedSupply[]>([])

  // 物資申請の承認
  const approveRequest = (shelterId: number, requestId: number) => {
    const shelter = shelters.value.find(s => s.id === shelterId)
    if (shelter) {
      const request = shelter.requests.find(r => r.id === requestId)
      if (request) {
        request.status = 'approved'
      }
    }
  }

  // 配送完了の処理
  const markDelivered = (shelterId: number, requestId: number) => {
    const shelter = shelters.value.find(s => s.id === shelterId)
    if (shelter) {
      const request = shelter.requests.find(r => r.id === requestId)
      if (request) {
        request.status = 'delivered'
        addToReceivedSupplies(request, shelter)
      }
    }
  }

  // 届いた支援物資への追加
  const addToReceivedSupplies = (request: SupplyRequest, shelter: ShelterRequest) => {
    const receivedItem: ReceivedSupply = {
      id: Date.now(),
      itemName: request.itemName,
      requestedQuantity: request.quantity,
      actualQuantity: request.quantity, // デフォルトは申請個数と同じ
      unit: request.unit,
      shelterName: shelter.name,
      deliveryDate: new Date().toISOString(),
      status: 'pending_confirmation'
    }
    
    receivedSupplies.value.push(receivedItem)
  }

  // 受取確認
  const confirmReceipt = (itemId: number) => {
    const item = receivedSupplies.value.find(supply => supply.id === itemId)
    if (item) {
      item.status = 'confirmed'
    }
  }

  // 実際の個数を更新
  const updateActualQuantity = (itemId: number, quantity: number) => {
    const item = receivedSupplies.value.find(supply => supply.id === itemId)
    if (item) {
      item.actualQuantity = quantity
    }
  }

  // 計算されたプロパティ
  const pendingRequests = computed(() => {
    return shelters.value.reduce((total, shelter) => {
      return total + shelter.requests.filter(req => req.status === 'pending').length
    }, 0)
  })

  const deliveredRequests = computed(() => {
    return shelters.value.reduce((total, shelter) => {
      return total + shelter.requests.filter(req => req.status === 'delivered').length
    }, 0)
  })

  const unconfirmedSupplies = computed(() => {
    return receivedSupplies.value.filter(supply => supply.status === 'pending_confirmation').length
  })

  return {
    shelters,
    receivedSupplies,
    approveRequest,
    markDelivered,
    addToReceivedSupplies,
    confirmReceipt,
    updateActualQuantity,
    pendingRequests,
    deliveredRequests,
    unconfirmedSupplies
  }
})