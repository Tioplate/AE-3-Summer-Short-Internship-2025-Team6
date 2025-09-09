package com.example.team6_backend.controller;

import com.example.team6_backend.entity.ShelterGoods;
import com.example.team6_backend.service.HttpService;
import com.example.team6_backend.service.ShelterGoodsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/shelter_goods")
public class ShelterGoodsController {
    private final ShelterGoodsService shelterGoodsService;
    @Autowired
    private HttpService httpService;

    @Value("${rakuten.application-id}")
    String applicationId;
    @Value("${rakuten.api-url}")
    String apiUrl;

    public ShelterGoodsController(ShelterGoodsService shelterGoodsService) {
        this.shelterGoodsService = shelterGoodsService;
    }
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public int insert(@RequestParam("url") String itemCode, int numberNow, int numberReq, String shelterId, String comment) throws JsonProcessingException {//@RequestParam注解可用于当参数名称与前端传值名称不同时映射
        StringBuffer url = new StringBuffer();
        url.append(apiUrl)
                .append("?")
                .append("applicationId=")
                .append(applicationId)
                .append("&")
                .append("itemCode=")
                .append(itemCode);
        String retJSON = httpService.sendGetRequest(url.toString());
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> goodsDataList = objectMapper.readValue(
                retJSON, new TypeReference<List<Map<String, Object>>>() {}
        );
        ShelterGoods shelterGoods = new ShelterGoods();
        shelterGoods.setGoodsId(itemCode);
        shelterGoods.setShelterId(shelterId);
        shelterGoods.setGoodsName(goodsDataList.getFirst().get("itemName").toString());
        shelterGoods.setNumberNow(numberNow);
        shelterGoods.setNumberReq(numberReq);
        if(comment != null && !comment.isEmpty())
            shelterGoods.setComment(comment);
        return shelterGoodsService.insert(shelterGoods);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String search(String keyword, int page, int pageSize){
        StringBuffer url = new StringBuffer();
        url.append(apiUrl)
                .append("?")
                .append("applicationId=")
                .append(applicationId)
                .append("&")
                .append("keyword=")
                .append(keyword)
                .append("&")
                .append("hits=")
                .append(pageSize)
                .append("&")
                .append("page=")
                .append(page);
        String retJSON = httpService.sendGetRequest(url.toString());
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        List<Map<String, Object>> GoodsDataList = objectMapper.readValue(
//                retJSON, new TypeReference<List<Map<String, Object>>>() {}
//        );
        return retJSON;
    }

    // 删除
    @DeleteMapping("/delete")
    public int deleteById(@RequestParam("goodsId") String goodsId) {
        return shelterGoodsService.deleteById(goodsId);
    }

    // 修改
    @PostMapping("/update")
    public int update(@RequestBody ShelterGoods shelterGoods) {
        return shelterGoodsService.update(shelterGoods);
    }

    // 按shelter_id查找
    @GetMapping("/listByShelterId")
    public List<ShelterGoods> listByShelterId(@RequestParam("shelterId") String shelterId) {
        return shelterGoodsService.selectByShelterId(shelterId);
    }

    // 按goods_id查找
    @GetMapping("/getByGoodsId")
    public ShelterGoods getByGoodsId(@RequestParam("goodsId") String goodsId) {
        return shelterGoodsService.selectByGoodsId(goodsId);
    }
}
