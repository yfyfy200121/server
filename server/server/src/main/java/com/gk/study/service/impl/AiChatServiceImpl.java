package com.gk.study.service.impl;

import com.gk.study.entity.Comment;
import com.gk.study.entity.Thing;
import com.gk.study.service.AiChatService;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI聊天服务实现类
 * 实现智能客服对话功能，包括对话历史管理和系统提示构建
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class AiChatServiceImpl implements AiChatService {
    
    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);
    
    @Autowired
    private ThingService thingService;
    
    @Value("${ai.api.url:https://ark.cn-beijing.volces.com/api/v3/chat/completions}")
    private String apiUrl;
    
    @Value("${ai.api.key:}")
    private String apiKey;
    
    // 对话上下文缓存
    private final Map<String, List<Map<String, String>>> conversationHistory = new ConcurrentHashMap<>();
    
    // 缓存商品信息
    private List<Thing> thingCache = new ArrayList<>();
    private long lastCacheUpdate = 0;
    private final long CACHE_DURATION = 5 * 60 * 1000; // 5分钟缓存
    
    @Override
    public String processMessage(String message, String sessionId) {
        log.info("Processing message for session {}: {}", sessionId, message);
        
        try {
            // 构建系统提示
            String systemPrompt = buildSystemPrompt();
            
            // 获取对话历史
            List<Map<String, String>> history = conversationHistory.getOrDefault(sessionId, new ArrayList<>());
            
            // 构建请求体
            Map<String, Object> requestBody = buildRequestBody(systemPrompt, message, history);
            
            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Accept", "application/json");
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            
            // 发送请求
            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, requestEntity, Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // 检查是否有错误
                if (responseBody.containsKey("error")) {
                    Map error = (Map) responseBody.get("error");
                    String errorMessage = (String) error.get("message");
                    log.error("AI service returned error: {}", errorMessage);
                    return "抱歉，AI服务返回错误：" + errorMessage;
                }
                
                // 解析回复内容
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    Map<String, Object> messageObj = (Map<String, Object>) choice.get("message");
                    String aiResponse = (String) messageObj.get("content");
                    
                    // 更新对话历史
                    updateConversationHistory(sessionId, message, aiResponse);
                    
                    log.info("AI response generated successfully for session {}", sessionId);
                    return aiResponse;
                }
            }
            
            return "抱歉，我暂时无法回答您的问题，请稍后再试。";
            
        } catch (Exception e) {
            log.error("AI服务调用失败", e);
            return "抱歉，服务暂时不可用，请稍后再试。";
        }
    }
    
    @Override
    public void clearHistory(String sessionId) {
        conversationHistory.remove(sessionId);
        log.info("Cleared conversation history for session {}", sessionId);
    }
    
    @Override
    public boolean testConnection() {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("API key not configured");
            return false;
        }
        
        try {
            // 发送简单的测试消息
            String testResponse = processMessage("你好", "test_connection_" + System.currentTimeMillis());
            return testResponse != null && !testResponse.contains("失败") && !testResponse.contains("不可用");
        } catch (Exception e) {
            log.error("Connection test failed", e);
            return false;
        }
    }
    
    /**
     * 获取并缓存商品信息
     */
    private List<Thing> getThingInfo() {
        long currentTime = System.currentTimeMillis();
        if (thingCache.isEmpty() || (currentTime - lastCacheUpdate) > CACHE_DURATION) {
            log.info("Refreshing thing cache");
            try {
                thingCache = thingService.getThingListWithComments(null, null, null, null);
                lastCacheUpdate = currentTime;
            } catch (Exception e) {
                log.error("Failed to refresh thing cache", e);
                // 如果缓存更新失败，继续使用旧缓存
            }
        }
        return thingCache;
    }
    
    /**
     * 构建系统提示
     */
    private String buildSystemPrompt() {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是购物商城的智能客服助手，专门为用户提供图书购买咨询服务。");
        prompt.append("你需要根据用户的问题，结合商品信息给出专业、友好的回答。");
        prompt.append("当用户询问商品推荐时，请基于以下商品信息进行推荐：\n\n");
        
        List<Thing> things = getThingInfo();
        
        // 限制商品信息数量，避免提示过长
        int maxThings = Math.min(things.size(), 10);
        for (int i = 0; i < maxThings; i++) {
            Thing thing = things.get(i);
            prompt.append("商品名称：").append(thing.getTitle()).append("\n");
            prompt.append("价格：").append(thing.getPrice()).append("元\n");
            
            if (thing.getScore() != null && !thing.getScore().isEmpty()) {
                prompt.append("评分：").append(thing.getScore()).append("分\n");
            }
            
            if (thing.getDescription() != null && !thing.getDescription().isEmpty()) {
                String desc = thing.getDescription();
                if (desc.length() > 100) {
                    desc = desc.substring(0, 100) + "...";
                }
                prompt.append("简介：").append(desc).append("\n");
            }
            
            // 添加用户评价信息
            if (thing.getComments() != null && !thing.getComments().isEmpty()) {
                prompt.append("用户评价：\n");
                int commentCount = Math.min(3, thing.getComments().size());
                for (int j = 0; j < commentCount; j++) {
                    Comment comment = thing.getComments().get(j);
                    if (comment.getContent() != null) {
                        String commentContent = comment.getContent();
                        if (commentContent.length() > 50) {
                            commentContent = commentContent.substring(0, 50) + "...";
                        }
                        prompt.append("- ").append(commentContent).append("\n");
                    }
                }
            }
            
            prompt.append("\n");
        }
        
        prompt.append("请根据用户的具体需求，推荐最合适的商品，并说明推荐理由。");
        prompt.append("如果用户询问价格、库存等具体信息，请基于上述商品数据回答。");
        prompt.append("保持回答简洁明了，语气友好专业。");
        
        return prompt.toString();
    }
    
    /**
     * 构建请求体
     */
    private Map<String, Object> buildRequestBody(String systemPrompt, String userMessage, 
                                               List<Map<String, String>> history) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "deepseek-v3-250324");
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统提示
        Map<String, String> systemMessage = new HashMap<>();
        systemMessage.put("role", "system");
        systemMessage.put("content", systemPrompt);
        messages.add(systemMessage);
        
        // 添加历史对话
        messages.addAll(history);
        
        // 添加用户当前消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        messages.add(userMsg);
        
        requestBody.put("messages", messages);
        
        return requestBody;
    }
    
    /**
     * 更新对话历史
     */
    private void updateConversationHistory(String sessionId, String userMessage, String aiResponse) {
        List<Map<String, String>> history = conversationHistory.getOrDefault(sessionId, new ArrayList<>());
        
        // 添加用户消息
        Map<String, String> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userMessage);
        history.add(userMsg);
        
        // 添加AI回复
        Map<String, String> aiMsg = new HashMap<>();
        aiMsg.put("role", "assistant");
        aiMsg.put("content", aiResponse);
        history.add(aiMsg);
        
        // 限制历史记录长度（保留最近10轮对话）
        if (history.size() > 20) {
            history = history.subList(history.size() - 20, history.size());
        }
        
        conversationHistory.put(sessionId, history);
        log.debug("Updated conversation history for session {}, size: {}", sessionId, history.size());
    }
}
