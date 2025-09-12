import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export interface Supply {
  id: string
  name: string
  category: string
  quantity: number
  location: string
  status: 'available' | 'requested' | 'distributed'
  createdAt: string
}
export const shelters = [
  {
    id: 1,
    name: '中央小学校',
    lat: 35.6762,
    lng: 139.6503,
    urgency: 'high',
    currentCapacity: 180,
    maxCapacity: 200,
    recentRequests: 15,
    urgentRequests: 8,
    topRequests: ['ミネラルウォーター', '離乳食', '毛布', '常備薬'],
    requestQuantities: { 'ミネラルウォーター': 50, '離乳食': 20, '毛布': 30, '常備薬': 10 },
    currentSupport: 2000
  },
  {
    id: 2,
    name: '市民体育館',
    lat: 35.6712,
    lng: 139.6533,
    urgency: 'medium',
    currentCapacity: 90,
    maxCapacity: 150,
    recentRequests: 8,
    urgentRequests: 3,
    topRequests: ['おにぎり', 'タオル', '乾電池'],
    requestQuantities: { 'おにぎり': 40, 'タオル': 25, '乾電池': 30 },
    currentSupport: 8000
  },
  {
    id: 3,
    name: '総合公園体育館',
    lat: 35.6792,
    lng: 139.6473,
    urgency: 'low',
    currentCapacity: 45,
    maxCapacity: 100,
    recentRequests: 4,
    urgentRequests: 1,
    topRequests: ['パン', 'マスク'],
    requestQuantities: { 'パン': 30, 'マスク': 20 },
    currentSupport: 5000
  }
]
export interface Donation {
  id: string
  donorName: string
  donorContact: string
  items: Array<{
    name: string
    quantity: number
    category: string
  }>
  status: 'pending' | 'received' | 'distributed'
  createdAt: string
}

export interface SupplyRequest {
  id: number
  itemName: string
  quantity: number
  unit: string
  status: 'pending' | 'approved' | 'delivered'
    priority:string
    reason: string
  notes?: string
}

export interface ShelterRequest {
  id: number
  shelterName: string
  adminId: string
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
  const supplies = ref<Supply[]>([])
  const donations = ref<Donation[]>([])
  const shelters = ref<ShelterRequest[]>([])
  const receivedSupplies = ref<ReceivedSupply[]>([])

  const suppliesByCategory = computed(() => {
    const categories: Record<string, Supply[]> = {}
    supplies.value.forEach(supply => {
      if (!categories[supply.category]) {
        categories[supply.category] = []
      }
      categories[supply.category].push(supply)
    })
    return categories
  })

  const lowStockSupplies = computed(() => {
    return supplies.value.filter(supply => supply.quantity < 10)
  })

  const pendingDonations = computed(() => {
    return donations.value.filter(donation => donation.status === 'pending')
  })

  function addSupply(supply: Omit<Supply, 'id' | 'createdAt'>) {
    const newSupply: Supply = {
      ...supply,
      id: Date.now().toString(),
      createdAt: new Date().toISOString()
    }
    supplies.value.push(newSupply)
  }

  function updateSupply(id: string, updates: Partial<Supply>) {
    const index = supplies.value.findIndex(supply => supply.id === id)
    if (index !== -1) {
      supplies.value[index] = { ...supplies.value[index], ...updates }
    }
  }

  function removeSupply(id: string) {
    const index = supplies.value.findIndex(supply => supply.id === id)
    if (index !== -1) {
      supplies.value.splice(index, 1)
    }
  }

  function addDonation(donation: Omit<Donation, 'id' | 'createdAt'>) {
    const newDonation: Donation = {
      ...donation,
      id: Date.now().toString(),
      createdAt: new Date().toISOString()
    }
    donations.value.push(newDonation)
  }

  function updateDonation(id: string, updates: Partial<Donation>) {
    const index = donations.value.findIndex(donation => donation.id === id)
    if (index !== -1) {
      donations.value[index] = { ...donations.value[index], ...updates }
    }
  }

  function approveRequest(shelterId: number, requestId: number) {
    const shelter = shelters.value.find(s => s.id === shelterId)
    if (shelter) {
      const request = shelter.requests.find(r => r.id === requestId)
      if (request) {
        request.status = 'approved'
      }
    }
  }

  function markDelivered(shelterId: number, requestId: number) {
    const shelter = shelters.value.find(s => s.id === shelterId)
    if (shelter) {
      const request = shelter.requests.find(r => r.id === requestId)
      if (request) {
        request.status = 'delivered'
        
        // Add delivered item to received supplies for confirmation
        const receivedSupply: ReceivedSupply = {
          id: Date.now() + Math.random(), // Ensure unique ID
          itemName: request.itemName,
          requestedQuantity: request.quantity,
          actualQuantity: request.quantity, // Default to requested quantity
          unit: request.unit,
          shelterName: shelter.shelterName,
          deliveryDate: new Date().toISOString(),
          status: 'pending_confirmation'
        }
        receivedSupplies.value.push(receivedSupply)
      }
    }
  }

  function updateActualQuantity(itemId: number, quantity: number) {
    const receivedSupply = receivedSupplies.value.find(item => item.id === itemId)
    if (receivedSupply) {
      receivedSupply.actualQuantity = quantity
    }
  }

  function confirmReceipt(itemId: number) {
    const receivedSupply = receivedSupplies.value.find(item => item.id === itemId)
    if (receivedSupply) {
      receivedSupply.status = 'confirmed'
      
      // Transfer confirmed supply to main supplies inventory
      const newSupply: Supply = {
        id: Date.now().toString(),
        name: receivedSupply.itemName,
        category: getCategoryFromItemName(receivedSupply.itemName),
        quantity: receivedSupply.actualQuantity,
        location: receivedSupply.shelterName,
        status: 'available',
        createdAt: new Date().toISOString()
      }
      supplies.value.push(newSupply)
    }
  }

  function getCategoryFromItemName(itemName: string): string {
    const categories: Record<string, string> = {
      '毛布': '寝具',
      'ペットボトル水': '飲料',
      '非常用パン': '食料',
      'マスク': '衛生用品',
      '消毒用アルコール': '衛生用品',
      '紙おむつ': '衛生用品'
    }
    return categories[itemName] || 'その他'
  }

  return {
    supplies,
    donations,
    shelters,
    receivedSupplies,
    suppliesByCategory,
    lowStockSupplies,
    pendingDonations,
    addSupply,
    updateSupply,
    removeSupply,
    addDonation,
    updateDonation,
    approveRequest,
    markDelivered,
    updateActualQuantity,
    confirmReceipt
  }
})