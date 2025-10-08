package com.gk.study.service;

/**
 * AI聊天服务接口
 * 提供智能客服对话功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
public interface AiChatService {
    
    /**
     * 处理用户消息并返回AI回复
     * 
     * @param message 用户消息
     * @param sessionId 会话ID
     * @return AI回复内容
     */
    String processMessage(String message, String sessionId);
    
    /**
     * 清除指定会话的历史记录
     * 
     * @param sessionId 会话ID
     */
    void clearHistory(String sessionId);
    
    /**
     * 测试AI服务连接
     * 
     * @return 连接是否成功
     */
    boolean testConnection();
}
