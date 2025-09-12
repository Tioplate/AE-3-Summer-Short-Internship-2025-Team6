// ShelterGoods实体类对应的TypeScript接口
import {ref} from "vue";

export interface ShelterGoods {
    goodsId: string
    goodsName: string
    shelterId: string
    numberNow: number
    numberReq: number
    comment: string
    status: string
}

// 空数组并导出
export const shelterGoodsList = ref<ShelterGoods[]>([])

