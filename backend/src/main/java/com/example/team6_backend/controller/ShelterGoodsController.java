package com.example.team6_backend.controller;

import com.example.team6_backend.common.GenreFinder;
import com.example.team6_backend.entity.ItemInfo;
import com.example.team6_backend.entity.ShelterGoods;
import com.example.team6_backend.service.HttpService;
import com.example.team6_backend.service.ShelterGoodsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    @Value("${rakuten.item-api-url}")
    String itemApiUrl;
    @Value("${rakuten.genre-api-url}")
    String genreApiUrl;

    public ShelterGoodsController(ShelterGoodsService shelterGoodsService) {
        this.shelterGoodsService = shelterGoodsService;
    }
    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public int insert(@RequestParam("itemCode") String itemCode,
                      @RequestParam("numberNow") Integer numberNow,
                      @RequestParam("numberReq") Integer numberReq,
                      @RequestParam("shelterId") String shelterId,
                      @RequestParam(value = "comment", required = false) String comment) throws JsonProcessingException { //@RequestParam注解可用于当参数名称与前端传值名称不同时映射
        // 检查数量是否为0或负数，非法则静默返回0
        if (numberNow == null || numberReq == null || numberNow <= 0 || numberReq <= 0) {
            return -1;
        }
        
        StringBuffer url = new StringBuffer();
        url.append(itemApiUrl)
                .append("?")
                .append("applicationId=")
                .append(applicationId)
                .append("&")
                .append("itemCode=")
                .append(itemCode);

        String retJSON = httpService.sendGetRequest(url.toString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(retJSON);
        JsonNode itemsNode = root.get("Items");
        List<ItemInfo> itemList = new ArrayList<>();
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                JsonNode itemNode = node.get("Item");
                ItemInfo item = new ItemInfo();
                item.setItemCode(itemNode.path("itemCode").asText());
                item.setItemName(itemNode.path("itemName").asText());
                item.setGenreId(itemNode.path("genreId").asText());
                item.setItemPrice(itemNode.path("itemPrice").asInt());
                item.setItemUrl(itemNode.path("itemUrl").asText());
                List<String> smallImageUrls = new ArrayList<>();
                for (JsonNode img : itemNode.path("smallImageUrls")) {
                    smallImageUrls.add(img.path("imageUrl").asText());
                }
                item.setSmallImageUrls(smallImageUrls);
                itemList.add(item);
            }
        }
        if (itemList.isEmpty() || itemList.get(0).getItemName() == null || itemList.get(0).getItemName().trim().isEmpty()) {
            return -1;
        }
        ShelterGoods shelterGoods = new ShelterGoods();
        shelterGoods.setGoodsId(itemCode);
        shelterGoods.setShelterId(shelterId);
        shelterGoods.setGoodsName(itemList.get(0).getItemName());
        shelterGoods.setNumberNow(numberNow);
        shelterGoods.setNumberReq(numberReq);
        if(comment != null && !comment.isEmpty())
            shelterGoods.setComment(comment);
        return shelterGoodsService.insert(shelterGoods);
    }

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public List<ItemInfo> search(String keyword, int page, int pageSize) throws JsonProcessingException {
        StringBuffer url = new StringBuffer();
        url.append(itemApiUrl)
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
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(retJSON);
        JsonNode itemsNode = root.get("Items");
        List<ItemInfo> itemList = new ArrayList<>();
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode node : itemsNode) {
                JsonNode itemNode = node.get("Item"); // 直接拿出 Item 字段
                ItemInfo item = new ItemInfo();
                item.setItemCode(itemNode.path("itemCode").asText());
                item.setItemName(itemNode.path("itemName").asText());
                item.setGenreId(itemNode.path("genreId").asText());
                item.setItemPrice(itemNode.path("itemPrice").asInt());
                item.setItemUrl(itemNode.path("itemUrl").asText());
                List<String> smallImageUrls = new ArrayList<>();
                for (JsonNode img : itemNode.path("smallImageUrls")) {
                    smallImageUrls.add(img.path("imageUrl").asText());
                }
                item.setSmallImageUrls(smallImageUrls);
                itemList.add(item);
            }
        }
        return itemList;
    }
    @GetMapping("/searchGenreId")
    public String searchGenreId(String genreId) throws JsonProcessingException {
        StringBuffer url = new StringBuffer();
        url.append(genreApiUrl)
                .append("?")
                .append("applicationId=")
                .append(applicationId)
                .append("&")
                .append("genreId=")
                .append(genreId);
        String retJSON = httpService.sendGetRequest(url.toString());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(retJSON);
        String genreName = GenreFinder.findGenreName(root, Integer.parseInt(genreId));
        if (genreName != null) {
            return genreName;
        }
        return "Not Found";
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

    @PostMapping("/updateNumberNow")
    public int updateNumberNow(@RequestParam String goodsId, @RequestParam Integer numberNow) {
        return shelterGoodsService.updateNumberNow(goodsId, numberNow);
    }

    @PostMapping("/updateNumberReq")
    public int updateNumberReq(@RequestParam String goodsId, @RequestParam Integer numberReq) {
        return shelterGoodsService.updateNumberReq(goodsId, numberReq);
    }

    @PostMapping("/batchUpsert")
    public int batchUpsert(@RequestBody List<ShelterGoods> list) {
        // 过滤掉无效项（如goodsId为空或numberReq为null/0）
        List<ShelterGoods> validList = list == null ? List.of() : list.stream()
            .filter(g -> g != null && g.getGoodsId() != null && !g.getGoodsId().isEmpty() && g.getNumberReq() != null && g.getNumberReq() > 0)
            .toList();
        if (validList.isEmpty()) return 0;
        return shelterGoodsService.batchUpsert(validList);
    }

    @GetMapping("/groupByShelter")
    public Map<String, List<ShelterGoods>> groupByShelter() {
        return shelterGoodsService.groupByShelterId();
    }
}
