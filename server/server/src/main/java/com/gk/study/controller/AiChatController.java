package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.entity.Thing;
import com.gk.study.service.ThingService;
import com.gk.study.interceptor.AccessInterceptor;
import com.gk.study.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;

@RestController
@RequestMapping("/ai-chat")
@CrossOrigin(origins = "*")
public class AiChatController {

    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    @Autowired
    private ThingService thingService;

    // 从配置文件中读取API密钥
    @Value("${ai.api.key:}")
    private String apikey;

    // 从配置文件中读取API端点URL
    @Value("${ai.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;

    // 缓存商品信息
    private List<Thing> thingCache = new ArrayList<>();
    private long lastCacheUpdate = 0;
    private final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存

    /**
     * 获取并缓存商品信息
     * @return 商品列表
     */
    private List<Thing> getThingInfo() {
        long currentTime = System.currentTimeMillis();
        if (thingCache.isEmpty() || (currentTime - lastCacheUpdate) > CACHE_DURATION) {
            logger.info("Refreshing thing cache");
            thingCache = thingService.getThingList(null, null, null, null);
            lastCacheUpdate = currentTime;
        }
        return thingCache;
    }

    /**
     * 构建包含商品信息的系统提示
     * @return 系统提示字符串
     */
    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是购物商城的智能客服助手。你的任务是帮助用户了解商城中的商品信息。");
        prompt.append("以下是商城中的商品信息：\n");

        List<Thing> things = getThingInfo();
        for (Thing thing : things) {
            prompt.append("商品名称：").append(thing.getTitle()).append("\n");
            prompt.append("价格：").append(thing.getPrice()).append("\n");
            prompt.append("描述：").append(thing.getDescription()).append("\n");
            prompt.append("库存：").append(thing.getRepertory()).append("\n");
            prompt.append("---\n");
        }

        prompt.append("\n请根据以上商品信息回答用户的问题，如果用户询问某件商品的详细信息，请提供该商品的完整信息。");
        prompt.append("如果用户询问推荐商品，请根据用户需求从以上商品中推荐合适的商品。");
        prompt.append("如果用户询问的问题与商品无关，请礼貌地告知用户你主要的功能是帮助他们了解商城商品。");
        return prompt.toString();
    }

    /**
     * AI客服聊天接口
     *
     * @param message 用户发送的消息
     * @param sessionId 会话ID
     * @return AI回复内容
     */
    @GetMapping("/message")
    public ResponseEntity<Object> chat(@RequestParam String message,
                                       @RequestParam(required = false, defaultValue = "") String sessionId) {
        logger.info("AI chat request received - message: {}, sessionId: {}", message, sessionId);

        // 检查API密钥是否存在
        if (apikey == null || apikey.isEmpty()) {
            logger.warn("AI API key not configured, returning mock response");
            // 如果没有配置API密钥，返回模拟回复
            Map<String, Object> mockResponse = new HashMap<>();
            mockResponse.put("code", 200);
            mockResponse.put("message", "success");
            mockResponse.put("data", "您好，我是AI客服助手。目前系统未配置AI服务，请联系管理员进行配置。");
            return ResponseEntity.ok(mockResponse);
        }

        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-v3-250324");

            // 添加系统角色消息
            List<Map<String, String>> messages = new ArrayList<>();
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", buildSystemPrompt());
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", message);
            messages.add(userMessage);
            requestBody.put("messages", messages);

            requestBody.put("stream", false);

            // 创建RestTemplate并发送请求
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            // 火山引擎DeepSeek API认证方式
            headers.set("Authorization", "Bearer " + apikey);
            // 添加其他必要的头部信息
            headers.set("Accept", "application/json");
            // 一些API可能需要User-Agent头部
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

            // 构建带API Key参数的URL
            String urlWithApiKey = UriComponentsBuilder.fromHttpUrl(apiUrl)
                    .toUriString();

            logger.info("Sending request to AI service with message: {}", message);
            logger.debug("Request URL: {}", urlWithApiKey);
            logger.debug("Request body: {}", JsonUtils.toJsonString(requestBody));
            logger.debug("API URL: {}", apiUrl);
            logger.debug("API Key length: {}", apikey.length());
            logger.debug("Request headers: {}", headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(urlWithApiKey, requestEntity, Map.class);

            logger.info("Received response from AI service with status: {}", response.getStatusCode());
            logger.debug("Response body: {}", response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();

                // 检查是否有错误
                if (responseBody.containsKey("error")) {
                    logger.error("AI service returned error: {}", responseBody.get("error"));
                    Map error = (Map) responseBody.get("error");
                    String errorMessage = (String) error.get("message");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI服务返回错误: " + errorMessage);
                }

                // 解析回复内容
                if (responseBody.containsKey("choices")) {
                    List<Map> choices = (List<Map>) responseBody.get("choices");
                    if (!choices.isEmpty()) {
                        Map choice = choices.get(0);
                        if (choice.containsKey("message")) {
                            Map messageObj = (Map) choice.get("message");
                            if (messageObj.containsKey("content")) {
                                String aiResponse = (String) messageObj.get("content");

                                Map<String, Object> result = new HashMap<>();
                                result.put("code", 200);
                                result.put("message", "success");
                                result.put("data", aiResponse);
                                logger.info("Successfully processed AI response");
                                return ResponseEntity.ok(result);
                            }
                        }
                    }
                }

                logger.warn("Unexpected response format from AI service: {}", responseBody);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI服务返回格式异常");
            } else {
                logger.error("AI service returned non-200 status: {}", response.getStatusCode());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI服务不可用，状态码: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.error("AI service returned 401 Unauthorized. Check API key and authentication headers.", e);
            logger.error("Response body: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI服务认证失败，请检查API密钥配置。错误详情: " + e.getMessage() + "。请确认您已在火山引擎控制台正确配置了API密钥，并且该密钥具有访问AI服务的权限。");
        } catch (HttpClientErrorException e) {
            logger.error("AI service returned HTTP error: {}", e.getStatusCode(), e);
            logger.error("Response body: {}", e.getResponseBodyAsString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("AI服务返回HTTP错误: " + e.getStatusCode() + ", " + e.getResponseBodyAsString());
        } catch (RestClientException e) {
            logger.error("Error calling AI service", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("调用AI服务时发生网络错误: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in AI chat", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("处理AI请求时发生未知错误: " + e.getMessage());
        }
    }

    /**
     * 手动刷新商品信息缓存
     *
     * @return 刷新结果
     */
    @GetMapping("/refresh-cache")
    public ResponseEntity<Object> refreshCache() {
        logger.info("Manual cache refresh requested");
        thingCache.clear();
        getThingInfo(); // 重新加载缓存
        Map<String, Object> result = new HashMap<>();
        result.put("code", 200);
        result.put("message", "商品信息缓存已刷新");
        result.put("data", "成功加载 " + thingCache.size() + " 件商品信息");
        return ResponseEntity.ok(result);
    }

    /**
     * 测试AI服务连接的端点
     *
     * @return 连接测试结果
     */
    @GetMapping("/test-connection")
    public ResponseEntity<Object> testConnection() {
        logger.info("AI service connection test requested");

        Map<String, Object> result = new HashMap<>();
        result.put("apiKeyConfigured", apikey != null && !apikey.isEmpty());
        result.put("apiKeyLength", apikey != null ? apikey.length() : 0);
        result.put("apiUrl", apiUrl);
        result.put("thingCount", getThingInfo().size());

        if (apikey != null && !apikey.isEmpty()) {
            // 隐藏API密钥中间部分以保护隐私
            String maskedKey = apikey.substring(0, Math.min(4, apikey.length())) +
                    "***" +
                    apikey.substring(Math.max(apikey.length() - 4, 0));
            result.put("maskedApiKey", maskedKey);
        }

        return ResponseEntity.ok(result);
    }
}