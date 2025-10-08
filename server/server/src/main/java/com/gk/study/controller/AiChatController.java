package com.gk.study.controller;

import com.gk.study.service.AiChatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/ai-chat")
@CrossOrigin(origins = "*")
public class AiChatController {

    private static final Logger logger = LoggerFactory.getLogger(AiChatController.class);

    @Autowired
    private AiChatService aiChatService;

    // 从配置文件中读取API密钥
    @Value("${ai.api.key:}")
    private String apikey;

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
            // 使用服务层处理消息
            String aiResponse = aiChatService.processMessage(message, sessionId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", aiResponse);
            logger.info("Successfully processed AI response");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Unexpected error in AI chat", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "error");
            errorResponse.put("data", "处理AI请求时发生错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 清除会话历史
     *
     * @param sessionId 会话ID
     * @return 清除结果
     */
    @GetMapping("/clear-history")
    public ResponseEntity<Object> clearHistory(@RequestParam String sessionId) {
        logger.info("Clear history request for session {}", sessionId);
        
        try {
            aiChatService.clearHistory(sessionId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("code", 200);
            result.put("message", "success");
            result.put("data", "会话历史已清除");
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            logger.error("Error clearing history for session {}", sessionId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("code", 500);
            errorResponse.put("message", "error");
            errorResponse.put("data", "清除历史失败: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
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

        if (apikey != null && !apikey.isEmpty()) {
            // 隐藏API密钥中间部分以保护隐私
            String maskedKey = apikey.substring(0, Math.min(4, apikey.length())) +
                    "***" +
                    apikey.substring(Math.max(apikey.length() - 4, 0));
            result.put("maskedApiKey", maskedKey);
            
            // 测试实际连接
            boolean connected = aiChatService.testConnection();
            result.put("connectionStatus", connected ? "成功" : "失败");
        } else {
            result.put("connectionStatus", "未配置");
        }

        return ResponseEntity.ok(result);
    }
}


