/*
  axiosの利用方法をこのファイルで参考してください。
  CORS問題への対処は、バックエンド側すでに設定済みですので、特に追加の設定は不要です。
*/

<template>
  <div class="signup-container">
    <div class="signup-card">
      <h1>新規登録</h1>
      <p class="description">災害物資支援プラットフォームへようこそ！</p>

      <div class="signup-form">
        <input v-model="userId" type="text" placeholder="ユーザーID" class="signup-input">
        <input v-model="password" type="password" placeholder="パスワード" class="signup-input">
        <input v-model="confirmPassword" type="password" placeholder="パスワード (確認)" class="signup-input">
      </div>

      <button @click="signUp" class="signup-btn" :disabled="!isFormValid">登録する</button>

      <div class="login-link">
        <p>すでにアカウントをお持ちですか？ <a @click="goBack">ログイン</a></p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { ElMessage } from 'element-plus'

const backUrl = import.meta.env.VITE_BACK_URL;
const router = useRouter();

const userId = ref('');
const password = ref('');
const confirmPassword = ref('');

const isFormValid = computed(() => {
  return userId.value && password.value && password.value === confirmPassword.value;
});

const signUp = async () => {
  if (!isFormValid.value) {
    alert('すべての必須項目を入力し、パスワードが一致していることを確認してください。');
    return;
  }

  if (password.value !== confirmPassword.value) {
    alert('パスワードが一致しません。');
    return;
  }

  const user = {
    userId: userId.value,
    password: password.value,
    permission: 0
  };
  //axiosでpostリクエストを送る。axios
  try {
    const { data } = await axios.post(backUrl + '/user/add', user, {
      headers: { 'Content-Type': 'application/json' }
    });

    if (data.code === 0) {
      ElMessage.success('登録が成功しました！ログイン画面に移動します。')
      router.push('/login');
    } else {
      ElMessage.error(`登録に失敗しました: ${data.message}`)
    }
  } catch (error) {
    console.error('登録エラー:', error);
    ElMessage.error('登録中にエラーが発生しました。')
  }
};

const goBack = () => {
  router.push('/login')
}
</script>

<style scoped>
.signup-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

.signup-card {
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
  font-size: 28px;
  font-weight: 600;
}

.description {
  color: #666;
  margin-bottom: 30px;
  font-size: 16px;
}

.signup-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 30px;
}

.signup-input {
  padding: 15px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  transition: border-color 0.3s;
}

.signup-input:focus {
  outline: none;
  border-color: #667eea;
}

.signup-btn {
  width: 100%;
  padding: 15px;
  background: #4CAF50;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 18px;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s;
}

.signup-btn:hover:not(:disabled) {
  background: #45a049;
}

.signup-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.login-link {
  margin-top: 20px;
  font-size: 14px;
}

.login-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  cursor: pointer;
}

.login-link a:hover {
  text-decoration: underline;
}
</style>

