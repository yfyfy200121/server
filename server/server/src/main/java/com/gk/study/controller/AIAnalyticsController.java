package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Comment;
import com.gk.study.entity.Thing;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.AIAnalyticsService;
import com.gk.study.service.CommentService;
import com.gk.study.service.ThingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI分析控制器
 * 提供基于AI的数据分析和智能推荐接口
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@RestController
@RequestMapping("/ai-analytics")
@CrossOrigin(origins = "*")
public class AIAnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalyticsController.class);

    @Autowired
    private AIAnalyticsService aiAnalyticsService;

    @Autowired
    private ThingService thingService;

    @Autowired
    private CommentService commentService;

    /**
     * 商品评论情感分析
     * 
     * @param thingId 商品ID
     * @return 情感分析结果
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/sentiment-analysis")
    public APIResponse analyzeSentiment(@RequestParam String thingId) {
        logger.info("Starting sentiment analysis for thing {}", thingId);
        
        try {
            List<Comment> comments = commentService.getThingCommentList(thingId, null);
            Map<String, Object> result = aiAnalyticsService.analyzeSentiment(comments);
            
            logger.info("Sentiment analysis completed for thing {}", thingId);
            return new APIResponse(ResponeCode.SUCCESS, "情感分析完成", result);
            
        } catch (Exception e) {
            logger.error("Failed to analyze sentiment for thing {}", thingId, e);
            return new APIResponse(ResponeCode.FAIL, "情感分析失败: " + e.getMessage());
        }
    }

    /**
     * 智能商品推荐
     * 
     * @param userId 用户ID
     * @param limit 推荐数量，默认10个
     * @return 推荐商品列表
     */
    @GetMapping("/recommendations")
    public APIResponse getRecommendations(@RequestParam String userId, 
                                        @RequestParam(defaultValue = "10") int limit) {
        logger.info("Generating recommendations for user {} with limit {}", userId, limit);
        
        try {
            List<Thing> recommendations = aiAnalyticsService.recommendProducts(userId, limit);
            
            logger.info("Generated {} recommendations for user {}", recommendations.size(), userId);
            return new APIResponse(ResponeCode.SUCCESS, "推荐成功", recommendations);
            
        } catch (Exception e) {
            logger.error("Failed to generate recommendations for user {}", userId, e);
            return new APIResponse(ResponeCode.FAIL, "推荐失败: " + e.getMessage());
        }
    }

    /**
     * 销售趋势分析
     * 
     * @param thingId 商品ID
     * @param days 分析天数，默认30天
     * @return 销售趋势分析结果
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/sales-trend")
    public APIResponse analyzeSalesTrend(@RequestParam String thingId,
                                       @RequestParam(defaultValue = "30") int days) {
        logger.info("Analyzing sales trend for thing {} over {} days", thingId, days);
        
        try {
            Map<String, Object> result = aiAnalyticsService.analyzeSalesTrend(thingId, days);
            
            logger.info("Sales trend analysis completed for thing {}", thingId);
            return new APIResponse(ResponeCode.SUCCESS, "销售趋势分析完成", result);
            
        } catch (Exception e) {
            logger.error("Failed to analyze sales trend for thing {}", thingId, e);
            return new APIResponse(ResponeCode.FAIL, "销售趋势分析失败: " + e.getMessage());
        }
    }

    /**
     * 生成智能营销文案
     * 
     * @param thingId 商品ID
     * @return 营销文案
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/marketing-content")
    public APIResponse generateMarketingContent(@RequestParam String thingId) {
        logger.info("Generating marketing content for thing {}", thingId);
        
        try {
            Thing thing = thingService.getThingById(thingId);
            if (thing == null) {
                return new APIResponse(ResponeCode.FAIL, "商品不存在");
            }
            
            String content = aiAnalyticsService.generateMarketingContent(thing);
            
            logger.info("Marketing content generated for thing {}", thingId);
            return new APIResponse(ResponeCode.SUCCESS, "营销文案生成成功", content);
            
        } catch (Exception e) {
            logger.error("Failed to generate marketing content for thing {}", thingId, e);
            return new APIResponse(ResponeCode.FAIL, "营销文案生成失败: " + e.getMessage());
        }
    }

    /**
     * 用户画像分析
     * 
     * @param userId 用户ID
     * @return 用户画像信息
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/user-profile")
    public APIResponse analyzeUserProfile(@RequestParam String userId) {
        logger.info("Analyzing user profile for user {}", userId);
        
        try {
            Map<String, Object> profile = aiAnalyticsService.analyzeUserProfile(userId);
            
            logger.info("User profile analysis completed for user {}", userId);
            return new APIResponse(ResponeCode.SUCCESS, "用户画像分析完成", profile);
            
        } catch (Exception e) {
            logger.error("Failed to analyze user profile for user {}", userId, e);
            return new APIResponse(ResponeCode.FAIL, "用户画像分析失败: " + e.getMessage());
        }
    }

    /**
     * 智能定价建议
     * 
     * @param thingId 商品ID
     * @return 定价建议
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/pricing-suggestion")
    public APIResponse suggestPricing(@RequestParam String thingId) {
        logger.info("Suggesting pricing for thing {}", thingId);
        
        try {
            Thing thing = thingService.getThingById(thingId);
            if (thing == null) {
                return new APIResponse(ResponeCode.FAIL, "商品不存在");
            }
            
            Map<String, Object> suggestion = aiAnalyticsService.suggestPricing(thing);
            
            logger.info("Pricing suggestion completed for thing {}", thingId);
            return new APIResponse(ResponeCode.SUCCESS, "定价建议生成成功", suggestion);
            
        } catch (Exception e) {
            logger.error("Failed to suggest pricing for thing {}", thingId, e);
            return new APIResponse(ResponeCode.FAIL, "定价建议生成失败: " + e.getMessage());
        }
    }

    /**
     * 库存预警分析
     * 
     * @return 库存预警信息
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/inventory-alerts")
    public APIResponse analyzeInventoryAlerts() {
        logger.info("Analyzing inventory alerts");
        
        try {
            Map<String, Object> alerts = aiAnalyticsService.analyzeInventoryAlerts();
            
            logger.info("Inventory alerts analysis completed");
            return new APIResponse(ResponeCode.SUCCESS, "库存预警分析完成", alerts);
            
        } catch (Exception e) {
            logger.error("Failed to analyze inventory alerts", e);
            return new APIResponse(ResponeCode.FAIL, "库存预警分析失败: " + e.getMessage());
        }
    }

    /**
     * 竞争对手分析
     * 
     * @param categoryId 商品分类ID
     * @return 竞争分析结果
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/competition-analysis")
    public APIResponse analyzeCompetition(@RequestParam Long categoryId) {
        logger.info("Analyzing competition for category {}", categoryId);
        
        try {
            Map<String, Object> analysis = aiAnalyticsService.analyzeCompetition(categoryId);
            
            logger.info("Competition analysis completed for category {}", categoryId);
            return new APIResponse(ResponeCode.SUCCESS, "竞争分析完成", analysis);
            
        } catch (Exception e) {
            logger.error("Failed to analyze competition for category {}", categoryId, e);
            return new APIResponse(ResponeCode.FAIL, "竞争分析失败: " + e.getMessage());
        }
    }

    /**
     * 综合智能分析报告
     * 整合多个AI分析功能，生成综合报告
     * 
     * @return 综合分析报告
     */
    @Access(level = AccessLevel.ADMIN)
    @GetMapping("/comprehensive-report")
    public APIResponse generateComprehensiveReport() {
        logger.info("Generating comprehensive AI analysis report");
        
        try {
            Map<String, Object> report = new java.util.HashMap<>();
            
            // 库存预警
            Map<String, Object> inventoryAlerts = aiAnalyticsService.analyzeInventoryAlerts();
            report.put("inventoryAlerts", inventoryAlerts);
            
            // 系统概况
            List<Thing> allThings = thingService.getThingList(null, null, null, null);
            report.put("totalProducts", allThings.size());
            
            long activeProducts = allThings.stream()
                .filter(thing -> "1".equals(thing.getStatus()))
                .count();
            report.put("activeProducts", activeProducts);
            
            // 平均评分
            double avgScore = allThings.stream()
                .filter(thing -> thing.getScore() != null && !thing.getScore().isEmpty())
                .mapToDouble(thing -> Double.parseDouble(thing.getScore()))
                .average()
                .orElse(0.0);
            report.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
            
            // 价格分布
            double avgPrice = allThings.stream()
                .filter(thing -> thing.getPrice() != null && !thing.getPrice().isEmpty())
                .mapToDouble(thing -> Double.parseDouble(thing.getPrice()))
                .average()
                .orElse(0.0);
            report.put("averagePrice", Math.round(avgPrice * 100.0) / 100.0);
            
            report.put("reportGeneratedAt", System.currentTimeMillis());
            report.put("reportType", "comprehensive");
            
            logger.info("Comprehensive AI analysis report generated successfully");
            return new APIResponse(ResponeCode.SUCCESS, "综合分析报告生成成功", report);
            
        } catch (Exception e) {
            logger.error("Failed to generate comprehensive report", e);
            return new APIResponse(ResponeCode.FAIL, "综合分析报告生成失败: " + e.getMessage());
        }
    }
}