package com.gk.study.service;

import com.gk.study.entity.Thing;
import com.gk.study.entity.User;

import java.util.List;
import java.util.Map;

/**
 * 智能推荐服务接口
 * 根据论文第三章的推荐系统设计要求实现
 */
public interface RecommendationService {
    
    /**
     * 基于用户行为的个性化推荐
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 推荐商品列表
     */
    List<Thing> getPersonalizedRecommendations(String userId, int limit);
    
    /**
     * 基于商品内容的相似推荐
     * @param thingId 商品ID
     * @param limit 推荐数量限制
     * @return 相似商品列表
     */
    List<Thing> getSimilarThings(String thingId, int limit);
    
    /**
     * 热门商品推荐
     * @param limit 推荐数量限制
     * @return 热门商品列表
     */
    List<Thing> getPopularRecommendations(int limit);
    
    /**
     * 基于分类的推荐
     * @param classificationId 分类ID
     * @param userId 用户ID（可选，用于个性化排序）
     * @param limit 推荐数量限制
     * @return 分类推荐商品列表
     */
    List<Thing> getCategoryRecommendations(String classificationId, String userId, int limit);
    
    /**
     * 协同过滤推荐（基于用户相似度）
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 协同过滤推荐商品列表
     */
    List<Thing> getCollaborativeFilteringRecommendations(String userId, int limit);
    
    /**
     * 混合推荐算法
     * @param userId 用户ID
     * @param limit 推荐数量限制
     * @return 混合推荐商品列表
     */
    List<Thing> getHybridRecommendations(String userId, int limit);
    
    /**
     * 获取推荐解释
     * @param userId 用户ID
     * @param thingId 商品ID
     * @return 推荐原因说明
     */
    String getRecommendationExplanation(String userId, String thingId);
    
    /**
     * 更新用户行为数据
     * @param userId 用户ID
     * @param thingId 商品ID
     * @param action 行为类型（view, purchase, collect, wish等）
     * @param weight 行为权重
     */
    void updateUserBehavior(String userId, String thingId, String action, double weight);
    
    /**
     * 计算商品相似度
     * @param thing1Id 商品1 ID
     * @param thing2Id 商品2 ID
     * @return 相似度分数
     */
    double calculateThingSimilarity(String thing1Id, String thing2Id);
    
    /**
     * 计算用户相似度
     * @param user1Id 用户1 ID
     * @param user2Id 用户2 ID
     * @return 相似度分数
     */
    double calculateUserSimilarity(String user1Id, String user2Id);
    
    /**
     * 获取用户偏好分析
     * @param userId 用户ID
     * @return 用户偏好数据
     */
    Map<String, Object> getUserPreferenceAnalysis(String userId);
}