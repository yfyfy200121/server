package com.gk.study.service.impl;

import com.gk.study.entity.Comment;
import com.gk.study.entity.Thing;
import com.gk.study.service.AIAnalyticsService;
import com.gk.study.service.CommentService;
import com.gk.study.service.ThingService;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI分析服务实现类
 * 实现基于AI的数据分析和智能推荐功能
 * 
 * @author System
 * @version 1.0
 * @since 2024-01-01
 */
@Service
public class AIAnalyticsServiceImpl implements AIAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(AIAnalyticsServiceImpl.class);

    @Autowired
    private ThingService thingService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    // 积极情感关键词
    private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
        "好", "棒", "优秀", "满意", "推荐", "喜欢", "完美", "惊喜", "值得", "精彩",
        "清晰", "详细", "实用", "有趣", "深刻", "启发", "收获", "受益", "经典", "权威"
    );

    // 消极情感关键词
    private static final List<String> NEGATIVE_KEYWORDS = Arrays.asList(
        "差", "糟糕", "失望", "不满", "后悔", "浪费", "无聊", "难懂", "过时", "错误",
        "混乱", "不清", "片面", "肤浅", "重复", "枯燥", "艰涩", "偏见", "误导", "不值"
    );

    @Override
    public Map<String, Object> analyzeSentiment(List<Comment> comments) {
        logger.info("Starting sentiment analysis for {} comments", comments.size());
        
        Map<String, Object> result = new HashMap<>();
        
        if (comments == null || comments.isEmpty()) {
            result.put("totalComments", 0);
            result.put("positiveCount", 0);
            result.put("negativeCount", 0);
            result.put("neutralCount", 0);
            result.put("positiveRatio", 0.0);
            result.put("negativeRatio", 0.0);
            result.put("overallSentiment", "neutral");
            return result;
        }

        int positiveCount = 0;
        int negativeCount = 0;
        int neutralCount = 0;
        
        List<Map<String, Object>> sentimentDetails = new ArrayList<>();

        for (Comment comment : comments) {
            String content = comment.getContent();
            if (content == null) continue;

            // 计算情感得分
            int positiveScore = 0;
            int negativeScore = 0;

            for (String keyword : POSITIVE_KEYWORDS) {
                if (content.contains(keyword)) {
                    positiveScore++;
                }
            }

            for (String keyword : NEGATIVE_KEYWORDS) {
                if (content.contains(keyword)) {
                    negativeScore++;
                }
            }

            String sentiment;
            if (positiveScore > negativeScore) {
                sentiment = "positive";
                positiveCount++;
            } else if (negativeScore > positiveScore) {
                sentiment = "negative";
                negativeCount++;
            } else {
                sentiment = "neutral";
                neutralCount++;
            }

            Map<String, Object> commentSentiment = new HashMap<>();
            commentSentiment.put("commentId", comment.getId());
            commentSentiment.put("content", content.length() > 100 ? content.substring(0, 100) + "..." : content);
            commentSentiment.put("sentiment", sentiment);
            commentSentiment.put("positiveScore", positiveScore);
            commentSentiment.put("negativeScore", negativeScore);
            sentimentDetails.add(commentSentiment);
        }

        int totalComments = comments.size();
        double positiveRatio = (double) positiveCount / totalComments;
        double negativeRatio = (double) negativeCount / totalComments;

        // 确定整体情感倾向
        String overallSentiment;
        if (positiveRatio > 0.6) {
            overallSentiment = "positive";
        } else if (negativeRatio > 0.4) {
            overallSentiment = "negative";
        } else {
            overallSentiment = "neutral";
        }

        result.put("totalComments", totalComments);
        result.put("positiveCount", positiveCount);
        result.put("negativeCount", negativeCount);
        result.put("neutralCount", neutralCount);
        result.put("positiveRatio", Math.round(positiveRatio * 100.0) / 100.0);
        result.put("negativeRatio", Math.round(negativeRatio * 100.0) / 100.0);
        result.put("overallSentiment", overallSentiment);
        result.put("sentimentDetails", sentimentDetails);

        logger.info("Sentiment analysis completed: {} positive, {} negative, {} neutral", 
                   positiveCount, negativeCount, neutralCount);
        
        return result;
    }

    @Override
    public List<Thing> recommendProducts(String userId, int limit) {
        logger.info("Generating product recommendations for user {} with limit {}", userId, limit);
        
        try {
            // 获取所有商品
            List<Thing> allThings = thingService.getThingList(null, null, null, null);
            
            if (allThings == null || allThings.isEmpty()) {
                return new ArrayList<>();
            }

            // 简单的推荐算法：基于评分和评论数量
            List<Thing> recommendations = allThings.stream()
                .filter(thing -> "1".equals(thing.getStatus())) // 只推荐上架商品
                .filter(thing -> Integer.parseInt(thing.getRepertory()) > 0) // 只推荐有库存的商品
                .sorted((t1, t2) -> {
                    // 按评分和推荐数量排序
                    double score1 = calculateRecommendationScore(t1);
                    double score2 = calculateRecommendationScore(t2);
                    return Double.compare(score2, score1); // 降序排序
                })
                .limit(limit)
                .collect(Collectors.toList());

            logger.info("Generated {} recommendations for user {}", recommendations.size(), userId);
            return recommendations;
            
        } catch (Exception e) {
            logger.error("Failed to generate recommendations for user {}", userId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 计算商品推荐得分
     */
    private double calculateRecommendationScore(Thing thing) {
        try {
            double score = 0.0;
            
            // 基础评分（假设满分5分）
            if (thing.getScore() != null && !thing.getScore().isEmpty()) {
                double rating = Double.parseDouble(thing.getScore());
                score += rating * 2; // 权重为2
            }
            
            // 推荐数量
            if (thing.getRecommendCount() != null && !thing.getRecommendCount().isEmpty()) {
                int recommendCount = Integer.parseInt(thing.getRecommendCount());
                score += Math.log(recommendCount + 1); // 对数缩放
            }
            
            // 收藏数量
            if (thing.getCollectCount() != null && !thing.getCollectCount().isEmpty()) {
                int collectCount = Integer.parseInt(thing.getCollectCount());
                score += Math.log(collectCount + 1) * 0.5; // 权重为0.5
            }
            
            // 浏览量
            if (thing.getPv() != null && !thing.getPv().isEmpty()) {
                int pv = Integer.parseInt(thing.getPv());
                score += Math.log(pv + 1) * 0.3; // 权重为0.3
            }
            
            return score;
        } catch (Exception e) {
            logger.warn("Error calculating recommendation score for thing {}", thing.getId(), e);
            return 0.0;
        }
    }

    @Override
    public Map<String, Object> analyzeSalesTrend(String thingId, int days) {
        logger.info("Analyzing sales trend for thing {} over {} days", thingId, days);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 这里应该从订单表中查询实际的销售数据
            // 由于示例系统中没有详细的订单历史，这里使用模拟数据
            
            List<Map<String, Object>> salesData = generateMockSalesData(thingId, days);
            
            // 计算趋势指标
            double totalSales = salesData.stream()
                .mapToDouble(data -> (Double) data.get("sales"))
                .sum();
            
            double averageDailySales = totalSales / days;
            
            // 计算增长趋势
            double firstHalfAvg = salesData.stream()
                .limit(days / 2)
                .mapToDouble(data -> (Double) data.get("sales"))
                .average()
                .orElse(0.0);
            
            double secondHalfAvg = salesData.stream()
                .skip(days / 2)
                .mapToDouble(data -> (Double) data.get("sales"))
                .average()
                .orElse(0.0);
            
            double growthRate = firstHalfAvg > 0 ? (secondHalfAvg - firstHalfAvg) / firstHalfAvg * 100 : 0;
            
            result.put("thingId", thingId);
            result.put("analysisDay", days);
            result.put("totalSales", totalSales);
            result.put("averageDailySales", Math.round(averageDailySales * 100.0) / 100.0);
            result.put("growthRate", Math.round(growthRate * 100.0) / 100.0);
            result.put("trend", growthRate > 5 ? "上升" : (growthRate < -5 ? "下降" : "平稳"));
            result.put("salesData", salesData);
            
            logger.info("Sales trend analysis completed for thing {}", thingId);
            return result;
            
        } catch (Exception e) {
            logger.error("Failed to analyze sales trend for thing {}", thingId, e);
            result.put("error", "销售趋势分析失败");
            return result;
        }
    }

    /**
     * 生成模拟销售数据
     */
    private List<Map<String, Object>> generateMockSalesData(String thingId, int days) {
        List<Map<String, Object>> salesData = new ArrayList<>();
        Random random = new Random();
        
        for (int i = 0; i < days; i++) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", "2024-01-" + String.format("%02d", i + 1));
            dayData.put("sales", Math.round((random.nextDouble() * 50 + 10) * 100.0) / 100.0); // 10-60之间的随机数
            dayData.put("orders", random.nextInt(20) + 1); // 1-20之间的订单数
            salesData.add(dayData);
        }
        
        return salesData;
    }

    @Override
    public String generateMarketingContent(Thing thing) {
        logger.info("Generating marketing content for thing {}", thing.getId());
        
        try {
            StringBuilder content = new StringBuilder();
            
            // 根据商品信息生成营销文案
            content.append("🔥 特别推荐：").append(thing.getTitle()).append(" 🔥\n\n");
            
            // 价格亮点
            if (thing.getPrice() != null) {
                content.append("💰 超值价格：仅需 ¥").append(thing.getPrice()).append("\n");
            }
            
            // 评分亮点
            if (thing.getScore() != null && !thing.getScore().isEmpty()) {
                double score = Double.parseDouble(thing.getScore());
                if (score >= 4.5) {
                    content.append("⭐ 高分好评：").append(score).append("分 用户一致好评！\n");
                }
            }
            
            // 库存紧张提醒
            if (thing.getRepertory() != null) {
                int stock = Integer.parseInt(thing.getRepertory());
                if (stock < 50) {
                    content.append("⚠️ 库存紧张：仅剩 ").append(stock).append(" 件，先到先得！\n");
                }
            }
            
            // 商品描述精华
            if (thing.getDescription() != null && !thing.getDescription().isEmpty()) {
                String desc = thing.getDescription();
                if (desc.length() > 100) {
                    desc = desc.substring(0, 100) + "...";
                }
                content.append("\n📖 内容简介：").append(desc).append("\n");
            }
            
            // 购买建议
            content.append("\n🛒 立即购买，享受优质阅读体验！");
            content.append("\n📚 知识改变命运，阅读成就未来！");
            
            logger.info("Marketing content generated successfully for thing {}", thing.getId());
            return content.toString();
            
        } catch (Exception e) {
            logger.error("Failed to generate marketing content for thing {}", thing.getId(), e);
            return "精品图书推荐：" + thing.getTitle() + "，值得您的收藏！";
        }
    }

    @Override
    public Map<String, Object> analyzeUserProfile(String userId) {
        logger.info("Analyzing user profile for user {}", userId);
        
        Map<String, Object> profile = new HashMap<>();
        
        try {
            // 这里应该分析用户的购买历史、浏览记录、评论等
            // 由于示例系统数据有限，这里生成模拟的用户画像
            
            profile.put("userId", userId);
            profile.put("purchaseFrequency", "中等"); // 购买频率
            profile.put("averageOrderValue", "68.5"); // 平均订单金额
            profile.put("preferredCategories", Arrays.asList("计算机", "教材教辅", "人文社科")); // 偏好分类
            profile.put("priceRange", "50-100"); // 价格区间偏好
            profile.put("activityLevel", "活跃"); // 活跃程度
            profile.put("loyaltyLevel", "中等"); // 忠诚度
            
            // 购买行为分析
            Map<String, Object> behavior = new HashMap<>();
            behavior.put("browseDuration", "5.2分钟"); // 平均浏览时长
            behavior.put("conversionRate", "15.8%"); // 转化率
            behavior.put("returnCustomer", true); // 是否回头客
            behavior.put("seasonalPattern", "春季购买较多"); // 季节性模式
            
            profile.put("behaviorAnalysis", behavior);
            
            // 推荐策略
            List<String> strategies = Arrays.asList(
                "推荐技术类新书",
                "提供教育类图书优惠",
                "发送个性化推荐邮件",
                "关注价格敏感度"
            );
            profile.put("recommendedStrategies", strategies);
            
            logger.info("User profile analysis completed for user {}", userId);
            return profile;
            
        } catch (Exception e) {
            logger.error("Failed to analyze user profile for user {}", userId, e);
            profile.put("error", "用户画像分析失败");
            return profile;
        }
    }

    @Override
    public Map<String, Object> suggestPricing(Thing thing) {
        logger.info("Suggesting pricing for thing {}", thing.getId());
        
        Map<String, Object> suggestion = new HashMap<>();
        
        try {
            double currentPrice = Double.parseDouble(thing.getPrice());
            
            // 基于市场分析的定价建议
            double suggestedMinPrice = currentPrice * 0.85; // 最低价格
            double suggestedMaxPrice = currentPrice * 1.15; // 最高价格
            double optimalPrice = currentPrice; // 最优价格
            
            // 根据商品表现调整定价建议
            if (thing.getScore() != null && !thing.getScore().isEmpty()) {
                double score = Double.parseDouble(thing.getScore());
                if (score >= 4.5) {
                    optimalPrice = currentPrice * 1.05; // 高评分商品可以提价
                } else if (score < 3.5) {
                    optimalPrice = currentPrice * 0.95; // 低评分商品建议降价
                }
            }
            
            // 库存情况影响定价
            if (thing.getRepertory() != null) {
                int stock = Integer.parseInt(thing.getRepertory());
                if (stock < 10) {
                    optimalPrice = optimalPrice * 1.02; // 库存紧张可以小幅提价
                } else if (stock > 200) {
                    optimalPrice = optimalPrice * 0.98; // 库存过多建议降价促销
                }
            }
            
            suggestion.put("thingId", thing.getId());
            suggestion.put("currentPrice", currentPrice);
            suggestion.put("suggestedMinPrice", Math.round(suggestedMinPrice * 100.0) / 100.0);
            suggestion.put("suggestedMaxPrice", Math.round(suggestedMaxPrice * 100.0) / 100.0);
            suggestion.put("optimalPrice", Math.round(optimalPrice * 100.0) / 100.0);
            
            // 定价策略建议
            List<String> strategies = new ArrayList<>();
            if (optimalPrice > currentPrice) {
                strategies.add("商品评价良好，建议适当提价");
            } else if (optimalPrice < currentPrice) {
                strategies.add("建议降价促销，提高销量");
            } else {
                strategies.add("当前定价合理，保持现价");
            }
            
            suggestion.put("pricingStrategies", strategies);
            
            logger.info("Pricing suggestion completed for thing {}", thing.getId());
            return suggestion;
            
        } catch (Exception e) {
            logger.error("Failed to suggest pricing for thing {}", thing.getId(), e);
            suggestion.put("error", "定价分析失败");
            return suggestion;
        }
    }

    @Override
    public Map<String, Object> analyzeInventoryAlerts() {
        logger.info("Analyzing inventory alerts");
        
        Map<String, Object> alerts = new HashMap<>();
        
        try {
            List<Thing> allThings = thingService.getThingList(null, null, null, null);
            
            List<Map<String, Object>> lowStockItems = new ArrayList<>();
            List<Map<String, Object>> outOfStockItems = new ArrayList<>();
            List<Map<String, Object>> overStockItems = new ArrayList<>();
            
            for (Thing thing : allThings) {
                if (thing.getRepertory() != null) {
                    int stock = Integer.parseInt(thing.getRepertory());
                    
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", thing.getId());
                    item.put("title", thing.getTitle());
                    item.put("currentStock", stock);
                    item.put("price", thing.getPrice());
                    
                    if (stock == 0) {
                        item.put("alertLevel", "严重");
                        item.put("suggestion", "立即补货");
                        outOfStockItems.add(item);
                    } else if (stock < 10) {
                        item.put("alertLevel", "警告");
                        item.put("suggestion", "建议补货");
                        lowStockItems.add(item);
                    } else if (stock > 200) {
                        item.put("alertLevel", "注意");
                        item.put("suggestion", "考虑促销");
                        overStockItems.add(item);
                    }
                }
            }
            
            alerts.put("lowStockCount", lowStockItems.size());
            alerts.put("outOfStockCount", outOfStockItems.size());
            alerts.put("overStockCount", overStockItems.size());
            alerts.put("lowStockItems", lowStockItems);
            alerts.put("outOfStockItems", outOfStockItems);
            alerts.put("overStockItems", overStockItems);
            
            // 总体建议
            List<String> recommendations = new ArrayList<>();
            if (!outOfStockItems.isEmpty()) {
                recommendations.add("有 " + outOfStockItems.size() + " 个商品缺货，需要紧急处理");
            }
            if (!lowStockItems.isEmpty()) {
                recommendations.add("有 " + lowStockItems.size() + " 个商品库存不足，建议补货");
            }
            if (!overStockItems.isEmpty()) {
                recommendations.add("有 " + overStockItems.size() + " 个商品库存过多，建议促销");
            }
            if (recommendations.isEmpty()) {
                recommendations.add("库存状况良好，无需特别处理");
            }
            
            alerts.put("recommendations", recommendations);
            
            logger.info("Inventory alerts analysis completed: {} low stock, {} out of stock, {} over stock", 
                       lowStockItems.size(), outOfStockItems.size(), overStockItems.size());
            
            return alerts;
            
        } catch (Exception e) {
            logger.error("Failed to analyze inventory alerts", e);
            alerts.put("error", "库存预警分析失败");
            return alerts;
        }
    }

    @Override
    public Map<String, Object> analyzeCompetition(Long categoryId) {
        logger.info("Analyzing competition for category {}", categoryId);
        
        Map<String, Object> analysis = new HashMap<>();
        
        try {
            // 获取该分类下的所有商品
            List<Thing> categoryThings = thingService.getThingList(null, null, categoryId.toString(), null);
            
            if (categoryThings.isEmpty()) {
                analysis.put("message", "该分类下暂无商品");
                return analysis;
            }
            
            // 价格分析
            List<Double> prices = categoryThings.stream()
                .filter(thing -> thing.getPrice() != null && !thing.getPrice().isEmpty())
                .map(thing -> Double.parseDouble(thing.getPrice()))
                .sorted()
                .collect(Collectors.toList());
            
            double avgPrice = prices.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            double minPrice = prices.isEmpty() ? 0.0 : prices.get(0);
            double maxPrice = prices.isEmpty() ? 0.0 : prices.get(prices.size() - 1);
            
            // 评分分析
            List<Double> scores = categoryThings.stream()
                .filter(thing -> thing.getScore() != null && !thing.getScore().isEmpty())
                .map(thing -> Double.parseDouble(thing.getScore()))
                .collect(Collectors.toList());
            
            double avgScore = scores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
            
            analysis.put("categoryId", categoryId);
            analysis.put("totalProducts", categoryThings.size());
            analysis.put("priceRange", Map.of(
                "min", minPrice,
                "max", maxPrice,
                "average", Math.round(avgPrice * 100.0) / 100.0
            ));
            analysis.put("averageScore", Math.round(avgScore * 100.0) / 100.0);
            
            // 竞争激烈程度
            String competitionLevel;
            if (categoryThings.size() > 50) {
                competitionLevel = "激烈";
            } else if (categoryThings.size() > 20) {
                competitionLevel = "中等";
            } else {
                competitionLevel = "较低";
            }
            analysis.put("competitionLevel", competitionLevel);
            
            // 市场机会分析
            List<String> opportunities = new ArrayList<>();
            if (maxPrice - minPrice > avgPrice) {
                opportunities.add("价格分布较广，有定价空间");
            }
            if (avgScore < 4.0) {
                opportunities.add("平均评分不高，有质量提升机会");
            }
            if (categoryThings.size() < 30) {
                opportunities.add("竞争商品较少，有市场空间");
            }
            
            analysis.put("marketOpportunities", opportunities);
            
            logger.info("Competition analysis completed for category {}", categoryId);
            return analysis;
            
        } catch (Exception e) {
            logger.error("Failed to analyze competition for category {}", categoryId, e);
            analysis.put("error", "竞争分析失败");
            return analysis;
        }
    }
}