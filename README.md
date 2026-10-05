# Disaster Support Platform

Rakuten Summer Short Internship 2025 で Team 6 として開発した、災害時の避難者・支援者・避難所をつなぐための Web アプリケーションです。

フロントエンドとバックエンドを分離した構成で、避難所情報の閲覧、地図表示、支援物資・寄付に関する機能、ユーザー認証などを実装しています。

## Features

- ユーザー登録・ログイン
- 避難者向け画面 / 支援者向け画面
- 避難所の一覧・詳細表示
- 地図上での避難所情報表示
- 支援物資・寄付関連機能
- リクエスト管理
- 管理者向け画面
- 外部 API との連携

## My Contributions

チーム開発の中で、主にバックエンド・データベースとフロントエンド連携を担当しました。

- MySQL の初期データベーススキーマと SQL の作成
- Spring Boot + MyBatis による避難所物資データの CRUD / API 実装
- Vue 3 と Spring Boot 間の Sign Up / Login 連携（Axios）
- `AuthController` を含む認証処理の実装・修正
- Rakuten Web Service を利用した商品・ジャンル検索機能の実装

## Tech Stack

### Frontend
- Vue 3
- TypeScript
- Vite
- Vue Router
- Pinia
- Element Plus
- Axios
- Leaflet

### Backend
- Java 21
- Spring Boot 3
- Spring Web / WebFlux
- MyBatis
- MySQL
- Maven

### External Services
- Rakuten Web Service
- Map / geocoding API

## Project Structure

```text
.
├── frontend/   # Vue 3 + TypeScript
├── backend/    # Spring Boot + MyBatis + MySQL
└── Report/     # Internship materials / reports
```

## Setup

### Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```

Required environment variables are listed in `frontend/.env.example`.

### Backend

Set the following environment variables before starting the application:

```text
DB_URL
DB_USER
DB_PASSWORD
RAKUTEN_APPLICATION_ID
RAKUTEN_APPLICATION_SECRET
RAKUTEN_AFFILIATE_ID
```

Then run:

```bash
cd backend
./mvnw spring-boot:run
```

On Windows:

```powershell
cd backend
./mvnw.cmd spring-boot:run
```

## Notes

This repository was developed as a team project during the Rakuten Summer Short Internship 2025.
Credentials and local environment files are intentionally excluded from version control.
