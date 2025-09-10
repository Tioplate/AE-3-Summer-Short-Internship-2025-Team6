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
    currentSupport: 12000
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