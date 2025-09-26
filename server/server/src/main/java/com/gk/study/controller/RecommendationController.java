package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Thing;
import com.gk.study.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能推荐控制器
 * 实现论文中智能推荐系统的API接口
 */
@RestController
@RequestMapping("/recommendation")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final static Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;

    /**
     * 获取个性化推荐
     * @param userId 用户ID
     * @param limit 推荐数量限制，默认10
     * @return 个性化推荐商品列表
     */
    @GetMapping("/personalized")
    public APIResponse getPersonalizedRecommendations(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting personalized recommendations for user: {}, limit: {}", userId, limit);
            
            List<Thing> recommendations = recommendationService.getPersonalizedRecommendations(userId, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", recommendations);
            result.put("total", recommendations.size());
            result.put("type", "personalized");
            
            return new APIResponse(ResponeCode.SUCCESS, "获取个性化推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting personalized recommendations for user: " + userId, e);
            return new APIResponse(ResponeCode.FAIL, "获取个性化推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取相似商品推荐
     * @param thingId 商品ID
     * @param limit 推荐数量限制，默认6
     * @return 相似商品列表
     */
    @GetMapping("/similar")
    public APIResponse getSimilarThings(
            @RequestParam String thingId,
            @RequestParam(defaultValue = "6") int limit) {
        try {
            logger.info("Getting similar things for thing: {}, limit: {}", thingId, limit);
            
            List<Thing> similarThings = recommendationService.getSimilarThings(thingId, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", similarThings);
            result.put("total", similarThings.size());
            result.put("type", "similar");
            result.put("baseThingId", thingId);
            
            return new APIResponse(ResponeCode.SUCCESS, "获取相似商品推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting similar things for thing: " + thingId, e);
            return new APIResponse(ResponeCode.FAIL, "获取相似商品推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取热门推荐
     * @param limit 推荐数量限制，默认10
     * @return 热门商品列表
     */
    @GetMapping("/popular")
    public APIResponse getPopularRecommendations(@RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting popular recommendations, limit: {}", limit);
            
            List<Thing> popularThings = recommendationService.getPopularRecommendations(limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", popularThings);
            result.put("total", popularThings.size());
            result.put("type", "popular");
            
            return new APIResponse(ResponeCode.SUCCESS, "获取热门推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting popular recommendations", e);
            return new APIResponse(ResponeCode.FAIL, "获取热门推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取分类推荐
     * @param classificationId 分类ID
     * @param userId 用户ID（可选）
     * @param limit 推荐数量限制，默认10
     * @return 分类推荐商品列表
     */
    @GetMapping("/category")
    public APIResponse getCategoryRecommendations(
            @RequestParam String classificationId,
            @RequestParam(required = false) String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting category recommendations for category: {}, user: {}, limit: {}", 
                       classificationId, userId, limit);
            
            List<Thing> categoryThings = recommendationService.getCategoryRecommendations(
                classificationId, userId, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", categoryThings);
            result.put("total", categoryThings.size());
            result.put("type", "category");
            result.put("classificationId", classificationId);
            
            return new APIResponse(ResponeCode.SUCCESS, "获取分类推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting category recommendations for category: " + classificationId, e);
            return new APIResponse(ResponeCode.FAIL, "获取分类推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取协同过滤推荐
     * @param userId 用户ID
     * @param limit 推荐数量限制，默认10
     * @return 协同过滤推荐商品列表
     */
    @GetMapping("/collaborative")
    public APIResponse getCollaborativeFilteringRecommendations(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting collaborative filtering recommendations for user: {}, limit: {}", userId, limit);
            
            List<Thing> recommendations = recommendationService.getCollaborativeFilteringRecommendations(userId, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", recommendations);
            result.put("total", recommendations.size());
            result.put("type", "collaborative");
            
            return new APIResponse(ResponeCode.SUCCESS, "获取协同过滤推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting collaborative filtering recommendations for user: " + userId, e);
            return new APIResponse(ResponeCode.FAIL, "获取协同过滤推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取混合推荐
     * @param userId 用户ID
     * @param limit 推荐数量限制，默认10
     * @return 混合推荐商品列表
     */
    @GetMapping("/hybrid")
    public APIResponse getHybridRecommendations(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            logger.info("Getting hybrid recommendations for user: {}, limit: {}", userId, limit);
            
            List<Thing> recommendations = recommendationService.getHybridRecommendations(userId, limit);
            
            Map<String, Object> result = new HashMap<>();
            result.put("recommendations", recommendations);
            result.put("total", recommendations.size());
            result.put("type", "hybrid");
            
            return new APIResponse(ResponeCode.SUCCESS, "获取混合推荐成功", result);
        } catch (Exception e) {
            logger.error("Error getting hybrid recommendations for user: " + userId, e);
            return new APIResponse(ResponeCode.FAIL, "获取混合推荐失败：" + e.getMessage());
        }
    }

    /**
     * 获取推荐解释
     * @param userId 用户ID
     * @param thingId 商品ID
     * @return 推荐原因说明
     */
    @GetMapping("/explanation")
    public APIResponse getRecommendationExplanation(
            @RequestParam String userId,
            @RequestParam String thingId) {
        try {
            logger.info("Getting recommendation explanation for user: {}, thing: {}", userId, thingId);
            
            String explanation = recommendationService.getRecommendationExplanation(userId, thingId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("explanation", explanation);
            result.put("userId", userId);
            result.put("thingId", thingId);
            
            return new APIResponse(ResponeCode.SUCCESS, "获取推荐解释成功", result);
        } catch (Exception e) {
            logger.error("Error getting recommendation explanation for user: " + userId + ", thing: " + thingId, e);
            return new APIResponse(ResponeCode.FAIL, "获取推荐解释失败：" + e.getMessage());
        }
    }

    /**
     * 更新用户行为
     * @param userId 用户ID
     * @param thingId 商品ID
     * @param action 行为类型（view, purchase, collect, wish等）
     * @param weight 行为权重，默认1.0
     * @return 更新结果
     */
    @PostMapping("/behavior")
    public APIResponse updateUserBehavior(
            @RequestParam String userId,
            @RequestParam String thingId,
            @RequestParam String action,
            @RequestParam(defaultValue = "1.0") double weight) {
        try {
            logger.info("Updating user behavior - user: {}, thing: {}, action: {}, weight: {}", 
                       userId, thingId, action, weight);
            
            recommendationService.updateUserBehavior(userId, thingId, action, weight);
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", userId);
            result.put("thingId", thingId);
            result.put("action", action);
            result.put("weight", weight);
            result.put("timestamp", System.currentTimeMillis());
            
            return new APIResponse(ResponeCode.SUCCESS, "用户行为更新成功", result);
        } catch (Exception e) {
            logger.error("Error updating user behavior", e);
            return new APIResponse(ResponeCode.FAIL, "用户行为更新失败：" + e.getMessage());
        }
    }

    /**
     * 获取用户偏好分析
     * @param userId 用户ID
     * @return 用户偏好分析数据
     */
    @GetMapping("/user-preference")
    public APIResponse getUserPreferenceAnalysis(@RequestParam String userId) {
        try {
            logger.info("Getting user preference analysis for user: {}", userId);
            
            Map<String, Object> preferences = recommendationService.getUserPreferenceAnalysis(userId);
            
            return new APIResponse(ResponeCode.SUCCESS, "获取用户偏好分析成功", preferences);
        } catch (Exception e) {
            logger.error("Error getting user preference analysis for user: " + userId, e);
            return new APIResponse(ResponeCode.FAIL, "获取用户偏好分析失败：" + e.getMessage());
        }
    }

    /**
     * 计算商品相似度
     * @param thing1Id 商品1 ID
     * @param thing2Id 商品2 ID
     * @return 相似度分数
     */
    @GetMapping("/similarity/thing")
    public APIResponse calculateThingSimilarity(
            @RequestParam String thing1Id,
            @RequestParam String thing2Id) {
        try {
            logger.info("Calculating thing similarity between: {} and {}", thing1Id, thing2Id);
            
            double similarity = recommendationService.calculateThingSimilarity(thing1Id, thing2Id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("thing1Id", thing1Id);
            result.put("thing2Id", thing2Id);
            result.put("similarity", similarity);
            
            return new APIResponse(ResponeCode.SUCCESS, "计算商品相似度成功", result);
        } catch (Exception e) {
            logger.error("Error calculating thing similarity", e);
            return new APIResponse(ResponeCode.FAIL, "计算商品相似度失败：" + e.getMessage());
        }
    }

    /**
     * 计算用户相似度
     * @param user1Id 用户1 ID
     * @param user2Id 用户2 ID
     * @return 相似度分数
     */
    @GetMapping("/similarity/user")
    public APIResponse calculateUserSimilarity(
            @RequestParam String user1Id,
            @RequestParam String user2Id) {
        try {
            logger.info("Calculating user similarity between: {} and {}", user1Id, user2Id);
            
            double similarity = recommendationService.calculateUserSimilarity(user1Id, user2Id);
            
            Map<String, Object> result = new HashMap<>();
            result.put("user1Id", user1Id);
            result.put("user2Id", user2Id);
            result.put("similarity", similarity);
            
            return new APIResponse(ResponeCode.SUCCESS, "计算用户相似度成功", result);
        } catch (Exception e) {
            logger.error("Error calculating user similarity", e);
            return new APIResponse(ResponeCode.FAIL, "计算用户相似度失败：" + e.getMessage());
        }
    }
}