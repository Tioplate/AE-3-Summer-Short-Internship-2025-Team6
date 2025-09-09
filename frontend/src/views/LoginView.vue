<template>
  <div class="login-container">
    <div class="login-card">
      <h1>災害物資支援プラットフォーム</h1>
      <h2>ログイン</h2>
      
      <div class="user-type-selection">
        <h3>利用者タイプを選択してください</h3>
        
        <div class="button-group">
          <button 
            @click="loginAsEvacuee" 
            class="user-type-btn evacuee-btn"
          >
            <div class="btn-icon">🏠</div>
            <div class="btn-text">
              <div class="btn-title">避難者として利用</div>
              <div class="btn-subtitle">物資の要請・投稿</div>
            </div>
          </button>
          
          <button 
            @click="loginAsSupporter" 
            class="user-type-btn supporter-btn"
          >
            <div class="btn-icon">💝</div>
            <div class="btn-text">
              <div class="btn-title">支援者として利用</div>
              <div class="btn-subtitle">避難所への支援・寄付</div>
            </div>
          </button>
          
          <button 
            @click="loginAsAdmin" 
            class="user-type-btn admin-btn"
          >
            <div class="btn-icon">🏢</div>
            <div class="btn-text">
              <div class="btn-title">運営者として利用</div>
              <div class="btn-subtitle">避難所管理・物資管理</div>
            </div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const loginAsEvacuee = () => {
  router.push('/evacuee')
}

const loginAsSupporter = () => {
  router.push('/supporter')
}

const loginAsAdmin = () => {
  router.push('/admin')
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.login-card {
  background: white;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  text-align: center;
}

h1 {
  color: #333;
  margin-bottom: 10px;
  font-size: 24px;
  font-weight: 600;
}

h2 {
  color: #666;
  margin-bottom: 30px;
  font-size: 18px;
  font-weight: 400;
}

h3 {
  color: #333;
  margin-bottom: 20px;
  font-size: 16px;
  font-weight: 500;
}

.button-group {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.user-type-btn {
  display: flex;
  align-items: center;
  padding: 20px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
}

.user-type-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.evacuee-btn:hover {
  border-color: #4CAF50;
  background: #f8fff8;
}

.supporter-btn:hover {
  border-color: #2196F3;
  background: #f8fbff;
}

.admin-btn:hover {
  border-color: #FF9800;
  background: #fffaf0;
}

.btn-icon {
  font-size: 32px;
  margin-right: 15px;
}

.btn-text {
  flex: 1;
}

.btn-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin-bottom: 5px;
}

.btn-subtitle {
  font-size: 14px;
  color: #666;
}

@media (max-width: 600px) {
  .login-card {
    padding: 30px 20px;
  }
  
  .user-type-btn {
    padding: 15px;
  }
  
  .btn-icon {
    font-size: 28px;
  }
  
  .btn-title {
    font-size: 16px;
  }
}
</style>