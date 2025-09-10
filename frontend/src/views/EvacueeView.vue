<template>
  <div class="evacuee-container">
    <header class="header">
      <h1>物資要請投稿</h1>
      <button @click="goBack" class="back-btn">← ログイン画面に戻る</button>
      <button @click="goToMapView" class="mapview-btn map-icon-btn">支援状況を見る</button>
    </header>

    <div class="content">
      <div class="shelter-selection">
        <h2>避難所を選択</h2>
        <select v-model="selectedShelter" class="shelter-select">
          <option value="">避難所を選択してください</option>
          <option v-for="shelter in shelters" :key="shelter.id" :value="shelter.id">
            {{ shelter.name }}
          </option>
        </select>

      </div>

      <div class="request-form" v-if="selectedShelter">
        <h2>必要な物資を選択</h2>
        
        <div class="category-tabs">
          <button 
            v-for="category in categories" 
            :key="category.id"
            @click="selectedCategory = category.id"
            :class="['tab-btn', { active: selectedCategory === category.id }]"
          >
            {{ category.name }}
          </button>
        </div>

        <div class="items-grid">
          <div 
            v-for="item in getCurrentCategoryItems()" 
            :key="item.id"
            class="item-card"
          >
            <div class="item-info">
              <span class="item-name">{{ item.name }}</span>
              <span class="item-priority" :class="`priority-${item.priority}`">
                {{ getPriorityText(item.priority) }}
              </span>
            </div>
            <div class="quantity-control">
              <button @click="decreaseQuantity(item.id)" class="qty-btn">-</button>
              <input 
                v-model.number="itemRequests[item.id]" 
                type="number" 
                min="0" 
                class="qty-input"
              >
              <button @click="increaseQuantity(item.id)" class="qty-btn">+</button>
            </div>
          </div>
        </div>

        <div class="free-request">
          <h3>その他の要請</h3>
          <div class="keyword-search-row">
            <input
              v-model="itemKeyword"
              type="text"
              placeholder="楽天商品キーワードを入力"
              class="item-keyword-input"
            >
            <button @click="searchCategories" class="search-btn">検索</button>
          </div>
              <div v-if="selectedCategoryName" class="selected-category">
            選択カテゴリ: {{ selectedCategoryName }}
          </div>
          <textarea 
            v-model="freeRequest" 
            placeholder="その他に必要な物資や詳細な要望があれば記入してください..."
            class="free-request-textarea"
          ></textarea>

        </div>

        <button @click="submitRequest" class="submit-btn" :disabled="!hasAnyRequest()">
          要請を送信
        </button>
        <!-- カテゴリ選択モーダル -->
        <div v-if="showCategoryModal" class="category-modal">
          <div class="modal-content">
            <h3>検索結果を選択してください</h3>
            <ul>
              <li v-for="cat in modalCategories" :key="cat" @click="selectCategory(cat)" class="modal-category">
                {{ cat }}
              </li>
            </ul>
            <button @click="closeCategoryModal" class="modal-btn">閉じる</button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="showSuccess" class="success-modal">
      <div class="modal-content">
        <h3>要請を送信しました</h3>
        <p>支援者の方々に物資要請が届けられました。</p>
        <button @click="goToMyRequests" class="modal-btn">OK</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import EvacueeMapView from './EvacueeMapView.vue'

const router = useRouter()

const selectedShelter = ref('')
const selectedCategory = ref('food')
const freeRequest = ref('')
const itemKeyword = ref('')
const selectedCategoryName = ref('')
const showCategoryModal = ref(false)
const modalCategories = ref<string[]>([])
const showSuccess = ref(false)
const itemRequests = reactive<Record<string, number>>({})

// 疑似キーワード→カテゴリデータ
const keywordCategoryMap: Record<string, string[]> = {
  'ティッシュ': ['ボックスティッシュ', 'ポケットティッシュ', 'ウェットティッシュ'],
  'ご飯': ['白米', 'おにぎり', 'レトルトご飯', 'お粥'],
  'マスク': ['不織布マスク', '布マスク', '子供用マスク'],
}

const shelters = [
  { id: 'shelter1', name: '中央小学校' },
  { id: 'shelter2', name: '市民体育館' },
  { id: 'shelter3', name: '総合公園体育館' },
]

const categories = [
  { id: 'food', name: '食料・水' },
  { id: 'clothing', name: '衣類・日用品' },
  { id: 'medical', name: '医薬品・衛生用品' },
  { id: 'other', name: 'その他' },
]

const items = [
  // 食料・水
  { id: 'water', name: 'ミネラルウォーター', category: 'food', priority: 'high' },
  { id: 'rice', name: 'おにぎり・弁当', category: 'food', priority: 'high' },
  { id: 'bread', name: 'パン', category: 'food', priority: 'medium' },
  { id: 'instant', name: 'インスタント食品', category: 'food', priority: 'medium' },
  { id: 'baby_food', name: '離乳食・ベビーフード', category: 'food', priority: 'high' },
  
  // 衣類・日用品
  { id: 'blanket', name: '毛布', category: 'clothing', priority: 'high' },
  { id: 'towel', name: 'タオル', category: 'clothing', priority: 'medium' },
  { id: 'underwear', name: '下着・靴下', category: 'clothing', priority: 'medium' },
  { id: 'diaper', name: 'おむつ', category: 'clothing', priority: 'high' },
  
  // 医薬品・衛生用品
  { id: 'mask', name: 'マスク', category: 'medical', priority: 'medium' },
  { id: 'sanitizer', name: '消毒用アルコール', category: 'medical', priority: 'high' },
  { id: 'medicine', name: '常備薬・処方薬', category: 'medical', priority: 'high' },
  { id: 'tissue', name: 'ティッシュ・トイレットペーパー', category: 'medical', priority: 'medium' },
  
  // その他
  { id: 'battery', name: '乾電池', category: 'other', priority: 'medium' },
  { id: 'phone_charger', name: 'スマホ充電器', category: 'other', priority: 'medium' },
  { id: 'flashlight', name: '懐中電灯', category: 'other', priority: 'low' },
]

const getCurrentCategoryItems = () => {
  return items.filter(item => item.category === selectedCategory.value)
}

const getPriorityText = (priority: string) => {
  const map: Record<string, string> = {
    high: '緊急',
    medium: '重要',
    low: '通常'
  }
  return map[priority] || '通常'
}

const increaseQuantity = (itemId: string) => {
  itemRequests[itemId] = (itemRequests[itemId] || 0) + 1
}

const decreaseQuantity = (itemId: string) => {
  if (itemRequests[itemId] > 0) {
    itemRequests[itemId]--
  }
}
// 検索ボタン押下時
const searchCategories = () => {
  const keyword = itemKeyword.value.trim()
  if (!keyword) return
  // 疑似API
  modalCategories.value = keywordCategoryMap[keyword] || ['該当カテゴリなし']
  showCategoryModal.value = true
}
const selectCategory = (cat: string) => {
  selectedCategoryName.value = cat
  showCategoryModal.value = false
}

const closeCategoryModal = () => {
  showCategoryModal.value = false
}


const hasAnyRequest = () => {
  return Object.values(itemRequests).some(qty => qty > 0)
    || freeRequest.value.trim() !== ''
    || (itemKeyword.value.trim() !== '' && selectedCategoryName.value !== '')
}

const submitRequest = () => {
  console.log('Submitting request:', {
    shelter: selectedShelter.value,
    items: itemRequests,
    freeRequest: freeRequest.value,
    keyword: itemKeyword.value,
    category: selectedCategoryName.value,
  })
  showSuccess.value = true

  // Reset form
  Object.keys(itemRequests).forEach(key => {
    itemRequests[key] = 0
  })
  freeRequest.value = ''
  itemKeyword.value = ''
  selectedCategoryName.value = ''
}


const goBack = () => {
  router.push('/')
}
const goToMapView = () => {
  router.push('/evacueemapview')
}

const goToMyRequests = () => {
  showSuccess.value = false;
  router.push('/myrequests')
}
</script>

<style scoped>
.evacuee-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #4CAF50;
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

.mapview-btn {
  background: rgba(33, 150, 243, 0.5);
  color: white;
  border: none;
  padding: 10px 15px;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.3s;
  display: inline-flex;
  align-items: center;
}
.mapview-btn:not(.map-icon-btn):hover {
  background: rgba(33, 150, 243, 0.8);
}
.map-icon-btn {
  padding-left: 36px; 
  background-image: url('../assets/map-icon.svg');
  background-repeat: no-repeat;
  background-position: 10px center;
}
.content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.shelter-selection {
  background: white;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.shelter-selection h2 {
  margin-top: 0;
  color: #333;
}

.shelter-select {
  width: 100%;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 5px;
  font-size: 16px;
}

.request-form {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.category-tabs {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 20px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.tab-btn.active {
  background: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.items-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 15px;
  margin-bottom: 30px;
}

.item-card {
  border: 2px solid #eee;
  border-radius: 5px;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-info {
  flex: 1;
}

.item-name {
  display: block;
  font-weight: 600;
  margin-bottom: 5px;
}

.item-priority {
  font-size: 12px;
  padding: 3px 8px;
  border-radius: 3px;
  font-weight: 600;
}

.priority-high {
  background: #ffebee;
  color: #c62828;
}

.priority-medium {
  background: #fff3e0;
  color: #f57c00;
}

.priority-low {
  background: #e8f5e8;
  color: #2e7d32;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 10px;
}

.qty-btn {
  width: 35px;
  height: 35px;
  border: 2px solid #4CAF50;
  background: white;
  color: #4CAF50;
  border-radius: 3px;
  cursor: pointer;
  font-weight: bold;
}

.qty-btn:hover {
  background: #4CAF50;
  color: white;
}

.qty-input {
  width: 60px;
  padding: 8px;
  text-align: center;
  border: 2px solid #ddd;
  border-radius: 3px;
}
.keyword-search-row {
  display: flex;
  gap: 8px;
  margin-top: 10px;
}
.item-keyword-input {
  flex: 1;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 5px;
  font-family: inherit;
}
.search-btn {
  background: #2196f3;
  color: white;
  border: none;
  padding: 0 18px;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  font-weight: bold;
  transition: background 0.3s;
}
.search-btn:hover {
  background: #1565c0;
}
.selected-category {
  margin-top: 8px;
  color: #2196f3;
  font-weight: bold;
}
.category-modal {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
}
.category-modal .modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  min-width: 250px;
}
.modal-category {
  padding: 10px;
  margin: 8px 0;
  background: #f5f5f5;
  border-radius: 5px;
  cursor: pointer;
  transition: background 0.2s;
}
.modal-category:hover {
  background: #e3f2fd;
}

.free-request {
  margin-bottom: 30px;
}

.free-request h3 {
  margin-top: 0;
  color: #333;
}

.free-request-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 5px;
  resize: vertical;
  font-family: inherit;
}


.submit-btn {
  width: 100%;
  padding: 15px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.submit-btn:hover:not(:disabled) {
  background: #45a049;
}

.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  max-width: 400px;
}

.modal-content h3 {
  margin-top: 0;
  color: #4CAF50;
}

.modal-btn {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 10px;
    text-align: center;
  }
  
  .content {
    padding: 10px;
  }
  
  .category-tabs {
    justify-content: center;
  }
  
  .items-grid {
    grid-template-columns: 1fr;
  }
}
</style>