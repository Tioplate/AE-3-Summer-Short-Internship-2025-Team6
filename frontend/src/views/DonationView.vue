<template>
  <div class="donation-container">
    <header class="header">
      <h1>{{ shelter?.shelterName }}への支援・寄付</h1>
      <button @click="goBack" class="back-btn">← 詳細画面に戻る</button>
    </header>

    <div class="content" v-if="shelter">
      <div class="shelter-summary">
        <div class="summary-card">
          <h2>支援先情報</h2>
          <div class="shelter-info">
            <div class="info-row">
              <span class="label">避難所名:</span>
              <span class="value">{{ shelter.shelterName }}</span>
            </div>
            <div class="info-row">
              <span class="label">収容状況:</span>
              <span class="value">{{ shelter.shelterCur }} / {{ shelter.shelterCap }}人</span>
            </div>
            <div class="info-row">
              <span class="label">緊急度:</span>
              <span class="value">
                <span class="status-badge" :class="`status-${shelter.status}`">
                  {{ getUrgencyText(shelter.status) }}
                </span>
              </span>
            </div>
            <div class="info-row">
              <span class="label">運営責任者:</span>
              <span class="value">{{ shelter.adminId }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="donation-methods">
        <h2>支援方法を選択</h2>
        
        <div class="method-tabs">
          <button 
            v-for="method in methods" 
            :key="method.id"
            @click="selectedMethod = method.id"
            :class="['method-tab', { active: selectedMethod === method.id }]"
          >
            <div class="tab-icon">{{ method.icon }}</div>
            <div class="tab-text">{{ method.name }}</div>
          </button>
        </div>

        <div class="method-content">
          <!-- 金銭寄付 -->
          <div v-if="selectedMethod === 'money'" class="donation-form">
            <h3>金銭による寄付</h3>
            
            <div class="amount-selection">
              <h4>寄付金額を選択</h4>
              <div class="preset-amounts">
                <button 
                  v-for="amount in presetAmounts" 
                  :key="amount"
                  @click="selectedAmount = amount"
                  :class="['amount-btn', { active: selectedAmount === amount }]"
                >
                  ¥{{ amount.toLocaleString() }}
                </button>
              </div>
              
              <div class="custom-amount">
                <label>その他の金額:</label>
                <div class="input-group">
                  <span class="currency">¥</span>
                  <input 
                    v-model.number="customAmount" 
                    type="number" 
                    placeholder="任意の金額を入力"
                    @input="selectedAmount = 0"
                  >
                </div>
              </div>
            </div>

            <div class="support-organization">
              <h4>支援団体を選択</h4>
              <div class="organization-list">
                <label 
                  v-for="org in supportOrganizations" 
                  :key="org.id"
                  class="org-option"
                >
                  <input 
                    type="radio" 
                    :value="org.id" 
                    v-model="selectedOrganization"
                  >
                  <div class="org-info">
                    <div class="org-name">{{ org.name }}</div>
                    <div class="org-description">{{ org.description }}</div>
                  </div>
                </label>
              </div>
            </div>

            <div class="donation-message">
              <h4>メッセージ (任意)</h4>
              <textarea 
                v-model="donationMessage"
                placeholder="応援メッセージを入力してください..."
                class="message-textarea"
              ></textarea>
            </div>

              <!-- 支払方法選択 -->
              <div class="payment-method">
                <h4>お支払方法の選択</h4>
                <label class="payment-option">
                  <input type="radio" value="card" v-model="paymentMethod"> クレジットカード
                </label>
                <label class="payment-option">
                  <input type="radio" value="bank" v-model="paymentMethod"> 銀行振込
                </label>
                <label class="payment-option">
                  <input type="radio" value="paypay" v-model="paymentMethod"> PayPay
                </label>
              </div>

              <!-- ポイント利用 -->
              <div class="points-section">
                <h4>ポイントを利用する</h4>
                <label>
                  <input type="checkbox" v-model="usePoints"> ポイントを利用する（保有: ¥{{ pointsBalance }})
                </label>
                <div v-if="usePoints" class="use-points-input">
                  <label>利用するポイント:</label>
                  <div>
                    <input type="number" v-model.number="usedPoints" :max="maxUsedPoints" :min="0">
                    <span> ポイント</span>
                  </div>

                  <!-- ポイントと支払いの内訳表示 -->
                  <div class="points-breakdown">
                    <div class="break-row">
                      <span>寄付金額（ベース）:</span>
                      <span>¥{{ baseAmount.toLocaleString() }}</span>
                    </div>
                    <div class="break-row">
                      <span>利用ポイント:</span>
                      <span>¥{{ usedPointsClamped.toLocaleString() }}</span>
                    </div>
                    <div class="break-row">
                      <span>支払方法:</span>
                      <span>{{ paymentMethodLabel }}</span>
                    </div>
                    <div class="break-row total">
                      <strong>最終支払額:</strong>
                      <strong>¥{{ finalPayable.toLocaleString() }}</strong>
                    </div>
                  </div>
                </div>
              </div>

            <button 
              @click="processDonation"
              :disabled="!canProceedDonation"
              class="donate-btn"
            >
              ¥{{ finalPayable.toLocaleString() }}を寄付する（ポイント適用後）
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 寄付完了モーダル -->
    <div v-if="showDonationSuccess" class="success-modal">
      <div class="modal-content">
        <div class="success-icon">✅</div>
        <h3>寄付手続きが完了しました</h3>
        <p>{{ shelter?.name }}への支援ありがとうございます。</p>
  <p>支払方法: {{ paymentMethodLabel }}</p>
  <p>寄付金は{{ selectedOrganizationName }}を通じて適切に配分されます。</p>
        <button @click="closeDonationSuccess" class="modal-btn">OK</button>
      </div>
    </div>

    <!-- 物資支援完了モーダル -->
    <div v-if="showGoodsSuccess" class="success-modal">
      <div class="modal-content">
        <div class="success-icon">📦</div>
        <h3>物資支援申し込みが完了しました</h3>
        <p>配送先情報をご確認の上、物資をお送りください。</p>
        <button @click="showGoodsSuccess = false" class="modal-btn">OK</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, reactive, watch , onBeforeMount} from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { shelters } from '../stores/shelters'
import axios from "axios";
import {ElMessage} from "element-plus";

const backUrl = import.meta.env.VITE_BACK_URL;
const shelterId = ref<string>('');
const router = useRouter()
const route = useRoute()

const shelter = ref<any>(null)
const selectedMethod = ref('money')
const selectedAmount = ref(5000)
const customAmount = ref<number>(0)
const selectedOrganization = ref('redcross')
const donationMessage = ref('')
const selectedGoodsCategory = ref('food')
const selectedGoods = ref<string[]>([])
const goodsQuantities = reactive<Record<string, number>>({})
const showDonationSuccess = ref(false)
const showGoodsSuccess = ref(false)

// 支払方法
const paymentMethod = ref('card')
const paymentMethodLabel = computed(() => {
  const map: Record<string, string> = {
    card: 'クレジットカード',
    bank: '銀行振込',
    paypay: 'PayPay'
  }
  return map[paymentMethod.value] || '未選択'
})

// ポイント機能（デモ用）
const pointsBalance = ref(2000) // 保有ポイント（ダミー）
const usePoints = ref(false)
const usedPoints = ref<number>(0)

// ベース金額（テンプレート表示用）
const baseAmount = computed(() => {
  return getFinalAmount()
})

// 入力できる最大の利用ポイントは「保有ポイント」と「ベース金額」の小さい方にする
const maxUsedPoints = computed(() => {
  return Math.max(0, Math.min(pointsBalance.value, baseAmount.value))
})

// 常に 0 以上かつ maxUsedPoints 以下の値を返す（表示用）
const usedPointsClamped = computed(() => {
  const v = Number(usedPoints.value) || 0
  return Math.max(0, Math.min(v, maxUsedPoints.value))
})

// 最終支払額: ポイント適用後（利用ポイントは maxUsedPoints で上限）
const finalPayable = computed(() => {
  const base = baseAmount.value
  const use = usePoints.value ? usedPointsClamped.value : 0
  return Math.max(base - use, 0)
})

// 入力された usedPoints を常に 0〜maxUsedPoints の範囲内に保つ
watch(usedPoints, (val) => {
  let n = Number(val) || 0
  if (n < 0) n = 0
  if (n > maxUsedPoints.value) n = maxUsedPoints.value
  if (n !== usedPoints.value) usedPoints.value = n
})

const methods = [
  { id: 'money', name: '金銭寄付', icon: '💰' }
]

const presetAmounts = [1000, 3000, 5000, 10000, 30000, 50000]

const supportOrganizations = [
  {
    id: 'redcross',
    name: '日本赤十字社',
    description: '国際的な人道支援組織として、災害時の緊急支援を行います'
  },
  {
    id: 'local_gov',
    name: '地方自治体災害対策本部',
    description: '地域に密着した支援を迅速に実施します'
  },
  {
    id: 'npo',
    name: 'NPO災害支援ネットワーク',
    description: '市民参加型の支援活動を推進します'
  }
]

const goodsCategories = [
  { id: 'food', name: '食料・水' },
  { id: 'clothing', name: '衣類・日用品' },
  { id: 'medical', name: '医療・衛生用品' },
  { id: 'other', name: 'その他' }
]

const goodsItems = [
  // 食料・水
  { id: 'water', name: 'ミネラルウォーター', category: 'food', unit: 'L', note: '500ml・2Lボトル' },
  { id: 'rice', name: 'お米・おにぎり', category: 'food', unit: 'kg', note: '無洗米推奨' },
  { id: 'canned', name: '缶詰・レトルト食品', category: 'food', unit: '個', note: '長期保存可能なもの' },
  { id: 'baby_food', name: '離乳食', category: 'food', unit: '個', note: '月齢別' },
  
  // 衣類・日用品
  { id: 'blanket', name: '毛布', category: 'clothing', unit: '枚', note: '清潔なもの' },
  { id: 'towel', name: 'タオル', category: 'clothing', unit: '枚', note: 'バスタオル・フェイスタオル' },
  { id: 'underwear', name: '下着・靴下', category: 'clothing', unit: 'セット', note: '新品のみ' },
  { id: 'diaper', name: 'おむつ', category: 'clothing', unit: 'パック', note: 'サイズ別' },
  
  // 医療・衛生用品
  { id: 'mask', name: 'マスク', category: 'medical', unit: 'パック', note: '不織布マスク' },
  { id: 'sanitizer', name: '消毒用アルコール', category: 'medical', unit: 'L', note: '70%以上のもの' },
  { id: 'tissue', name: 'ティッシュ・トイレットペーパー', category: 'medical', unit: 'パック', note: '' },
  
  // その他
  { id: 'battery', name: '乾電池', category: 'other', unit: 'パック', note: '単1〜単4' },
  { id: 'flashlight', name: '懐中電灯', category: 'other', unit: '個', note: '電池付き' },
  { id: 'radio', name: '携帯ラジオ', category: 'other', unit: '個', note: '電池式・手回し式' }
]

const mockShelters = {
  shelter1: {
    id: 'shelter1',
    name: '中央小学校',
    urgency: 'urgent',
    currentCapacity: 180,
    maxCapacity: 200,
    manager: '校長 田中一郎'
  },
  shelter2: {
    id: 'shelter2',
    name: '市民体育館',
    urgency: 'important',
    currentCapacity: 90,
    maxCapacity: 150,
    manager: '館長 佐藤花子'
  },
  shelter3: {
    id: 'shelter3',
    name: '総合公園体育館',
    urgency: 'normal',
    currentCapacity: 45,
    maxCapacity: 100,
    manager: '館長 山田太郎'
  }
}

const canProceedDonation = computed(() => {
  return (selectedAmount.value > 0 || customAmount.value > 0) && selectedOrganization.value
})

const getFinalAmount = () => {
  return customAmount.value > 0 ? customAmount.value : selectedAmount.value
}

const selectedOrganizationName = computed(() => {
  const org = supportOrganizations.find(o => o.id === selectedOrganization.value)
  return org?.name || ''
})

const getUrgencyText = (urgency: string) => {
  const map: Record<string, string> = {
    urgent: '緊急支援必要',
    important: '支援必要',
    normal: '状況良好'
  }
  return map[urgency] || '状況良好'
}

const getCurrentGoodsItems = () => {
  return goodsItems.filter(item => item.category === selectedGoodsCategory.value)
}

const processDonation = async () => {
  console.log('Processing donation:', {
    amount: getFinalAmount(),
    organization: selectedOrganization.value,
    message: donationMessage.value,
  shelter: shelter.value?.shelterId,
  paymentMethod: paymentMethod.value,
  usedPoints: usePoints.value ? (usedPoints.value || 0) : 0,
  finalPayable: finalPayable.value
  })

  // 寄付金額をshelters配列のcurrentSupportに加算
  const targetShelter = shelters.value.find(s => s.shelterId === shelter.value?.shelterId)
  //console.log('Target shelter:', targetShelter.shelterId)
  if (targetShelter) {
    //console.log('Before donation, currentSupport:', targetShelter.currentSupport)
    targetShelter.moneyCur = (targetShelter.moneyCur || 0) + finalPayable.value
    //console.log('After donation, currentSupport:', targetShelter.currentSupport)
    await axios.post(backUrl + '/shelter/updateCurrentMoney', null, {
      params: {
        shelterId: targetShelter.shelterId,
        moneyCur: targetShelter.moneyCur
      }
    })
    showDonationSuccess.value = true
  }
  else {
    ElMessage.error('寄付先の避難所が見つかりません')
  }


}

const submitGoodsSupport = () => {
  const supportItems = selectedGoods.value.map(id => ({
    id,
    quantity: goodsQuantities[id] || 1
  }))
  
  console.log('Submitting goods support:', {
    items: supportItems,
    shelter: shelter.value?.shelterId
  })
  showGoodsSuccess.value = true
}

const openVolunteerContact = () => {
  alert('実際のアプリケーションでは、ここで電話アプリやメールアプリが開かれます。\n\n連絡先: 03-1234-5679\nvolunteer@disaster-support.go.jp')
}

const closeDonationSuccess = () => {
  showDonationSuccess.value = false
  router.push('/supporter')
}

const goBack = () => {
  router.push(`/shelter/${route.params.shelterId}`)
}
onBeforeMount( () => {
  // モックデータをstoresにセット
  shelterId.value = route.params.shelterId as string
  shelter.value = shelters.value.find(s => s.shelterId === shelterId.value) || shelters.value[0]
})

onMounted(async () => {
  try {
    const res = await axios.get(backUrl + '/shelter/getById', {
      params: { shelterId: shelterId.value }
    })
    //shelters.value = res.data // 假设后端返回的是避难所数组
    //shelters
    //alert(res.data)
    shelter.value = shelters.value.find(s => s.shelterId === shelterId.value) || null
    if (!shelter.value) {
      ElMessage.error('指定された避難所が見つかりません')
    }
  } catch (e) {
    ElMessage.error('避難所データの取得に失敗しました')
    return
  }
})
</script>

<style scoped>
.donation-container {
  min-height: 100vh;
  background: #f5f5f5;
}

.header {
  background: #FF6B35;
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

.content {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.shelter-summary {
  margin-bottom: 30px;
}

.summary-card {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.summary-card h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.shelter-info {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-row {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-row .label {
  font-weight: 600;
  color: #666;
  font-size: 14px;
}

.info-row .value {
  font-size: 16px;
  color: #333;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-urgent {
  background: #ffebee;
  color: #c62828;
}

.status-important {
  background: #fff3e0;
  color: #f57c00;
}

.status-normal {
  background: #e8f5e8;
  color: #2e7d32;
}

.donation-methods {
  background: white;
  border-radius: 8px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.donation-methods h2 {
  margin: 0 0 20px 0;
  color: #333;
}

.method-tabs {
  display: flex;
  gap: 15px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.method-tab {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  min-width: 120px;
}

.method-tab.active {
  background: #FF6B35;
  color: white;
  border-color: #FF6B35;
}

.tab-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.tab-text {
  font-weight: 600;
}

.donation-form h3,
.goods-form h3,
.volunteer-form h3 {
  margin: 0 0 25px 0;
  color: #333;
  font-size: 20px;
}

.amount-selection {
  margin-bottom: 30px;
}

.amount-selection h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.preset-amounts {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 10px;
  margin-bottom: 20px;
}

.amount-btn {
  padding: 15px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
}

.amount-btn.active {
  background: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.custom-amount {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.input-group {
  display: flex;
  align-items: center;
  border: 2px solid #ddd;
  border-radius: 5px;
  overflow: hidden;
}

.currency {
  background: #f5f5f5;
  padding: 12px 15px;
  font-weight: bold;
}

.input-group input {
  flex: 1;
  padding: 12px 15px;
  border: none;
  outline: none;
  font-size: 16px;
}

.use-points-input input[type="number"] {
  font-size: 16px;
  font-weight: 100;
}

.support-organization {
  margin-bottom: 30px;
}

.support-organization h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.organization-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.org-option {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.org-option:has(input:checked) {
  background: #e8f5e8;
  border-color: #4CAF50;
}

.org-option input {
  margin-right: 15px;
}

.org-info {
  flex: 1;
}

.org-name {
  font-weight: 600;
  margin-bottom: 5px;
}

.org-description {
  font-size: 14px;
  color: #666;
}

.donation-message {
  margin-bottom: 30px;
}

.donation-message h4 {
  margin: 0 0 15px 0;
  color: #333;
}

.message-textarea {
  width: 100%;
  min-height: 100px;
  padding: 12px;
  border: 2px solid #ddd;
  border-radius: 5px;
  resize: vertical;
  font-family: inherit;
}

/* ポイント利用欄の余白と内訳フォント調整 */
.use-points-input {
  margin-top: 12px;
  display: flex;
  flex-direction: column;
  gap: 12px; /* 入力欄と内訳の間隔 */
}

.points-breakdown {
  margin-top: 6px;
  padding: 12px;
  background: #fbfbfc;
  border: 1px solid #eee;
  border-radius: 8px;
  font-size: 16px; /* 全体の基本フォントサイズ */
  color: #333;
}

.points-breakdown .break-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
}

.points-breakdown .total {
  margin-top: 8px;
  border-top: 1px dashed #e6e6e6;
  padding-top: 8px;
  font-size: 18px;
}

/* モバイル時にフォントを少し小さく、間隔も調整 */
@media (max-width: 480px) {
  .points-breakdown {
    font-size: 15px;
    padding: 10px;
  }
}

.donate-btn,
.support-btn,
.volunteer-btn {
  width: 100%;
  padding: 15px;
  background: #FF6B35;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.donate-btn:hover:not(:disabled),
.support-btn:hover:not(:disabled),
.volunteer-btn:hover:not(:disabled) {
  background: #FF5722;
}

.donate-btn:disabled,
.support-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.goods-selection {
  margin-bottom: 30px;
}

.goods-categories {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.category-btn {
  padding: 8px 16px;
  border: 2px solid #ddd;
  background: white;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.category-btn.active {
  background: #4CAF50;
  color: white;
  border-color: #4CAF50;
}

.goods-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.goods-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 2px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
  transition: all 0.3s;
}

.goods-item:has(input:checked) {
  background: #e8f5e8;
  border-color: #4CAF50;
}

.goods-item input[type="checkbox"] {
  margin-right: 15px;
}

.item-info {
  flex: 1;
}

.item-name {
  font-weight: 600;
  margin-bottom: 3px;
}

.item-note {
  font-size: 12px;
  color: #666;
}

.quantity-input {
  width: 100px;
}

.quantity-input input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ccc;
  border-radius: 3px;
  text-align: center;
}

.delivery-info {
  margin-bottom: 30px;
}

.delivery-note {
  background: #fff3cd;
  border: 1px solid #ffeaa7;
  border-radius: 5px;
  padding: 20px;
}

.delivery-address {
  background: white;
  padding: 15px;
  margin: 15px 0;
  border-radius: 5px;
  border-left: 4px solid #FF6B35;
}

.volunteer-info {
  margin-bottom: 30px;
}

.info-card {
  background: #f8f9fa;
  padding: 25px;
  border-radius: 8px;
}

.volunteer-needs {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.need-item {
  padding: 15px;
  background: white;
  border-radius: 5px;
  border-left: 4px solid #4CAF50;
}

.need-title {
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.need-details {
  color: #666;
  margin-bottom: 5px;
}

.need-time {
  font-size: 14px;
  color: #2196F3;
  font-weight: 500;
}

.volunteer-contact {
  margin-bottom: 30px;
}

.contact-info {
  background: #e3f2fd;
  padding: 20px;
  border-radius: 5px;
}

.contact-details {
  margin-top: 15px;
}

.contact-item {
  margin-bottom: 10px;
  padding: 5px 0;
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
  padding: 40px;
  border-radius: 8px;
  text-align: center;
  max-width: 400px;
  margin: 20px;
}

.success-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.modal-content h3 {
  margin: 0 0 15px 0;
  color: #4CAF50;
}

.modal-content p {
  margin: 10px 0;
  color: #666;
}

.modal-btn {
  background: #4CAF50;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 5px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 15px;
    text-align: center;
  }
  
  .content {
    padding: 10px;
  }
  
  .method-tabs {
    justify-content: center;
  }
  
  .preset-amounts {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .goods-categories {
    justify-content: center;
  }
  
  .shelter-info {
    grid-template-columns: 1fr;
  }
}
</style>