<template>
  <div class="evacuee-container">
    <header class="header">
      <h1>送信済みリクエスト一覧</h1>
      <button @click="goBack" class="back-btn">← 物資要請画面に戻る</button>
    </header>

    <div class="content">
      <div class="request-list">
        <div v-for="request in requests" :key="request.id" class="request-card">
          <h3>リクエストID: {{ request.id }}</h3>
          <p>避難所: {{ request.shelterName }}</p>
          <p>要請日: {{ request.requestDate }}</p>
          <p v-if="request.category">選択カテゴリ: {{ request.category }}</p>
          <ul>
            <li v-for="item in request.items" :key="item.id">
              {{ item.name }} - {{ item.quantity }}個
            </li>
          </ul>
          <p v-if="request.freeRequest">その他: {{ request.freeRequest }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

const requests = ref([]);
const userId = 'user123'; // 仮のuserId

onMounted(async () => {
  try {
    const response = await fetch(`/requests?userId=${userId}`); // APIエンドポイント
    if (!response.ok) {
      throw new Error('リクエストの取得に失敗しました');
    }
    const data = await response.json();
    requests.value = data.data; // ApiResponseのdataフィールドに格納されているため
  } catch (error) {
    console.error('リクエストの取得エラー:', error);
    alert('リクエストの取得に失敗しました');
  }
});

const goBack = () => {
  router.push('/evacuee');
};
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

.content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.request-list {
  margin-top: 20px;
}

.request-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.request-card h3 {
  margin-top: 0;
  color: #333;
}

.request-card p {
  margin-bottom: 10px;
}

.request-card ul {
  list-style: none;
  padding: 0;
}

.request-card li {
  margin-bottom: 5px;
}
</style>