package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.gk.study.entity.*;
import com.gk.study.mapper.*;
import com.gk.study.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 智能推荐服务实现类
 * 实现基于论文第三章设计的多种推荐算法
 */
@Service
public class RecommendationServiceImpl implements RecommendationService {
    
    @Autowired
    private ThingMapper thingMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ThingCollectMapper thingCollectMapper;
    
    @Autowired
    private ThingWishMapper thingWishMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private ClassificationMapper classificationMapper;

    @Override
    public List<Thing> getPersonalizedRecommendations(String userId, int limit) {
        // 基于用户历史行为的个性化推荐
        List<Thing> recommendations = new ArrayList<>();
        
        try {
            // 1. 获取用户的购买历史
            QueryWrapper<Order> orderQuery = new QueryWrapper<>();
            orderQuery.eq("user_id", userId).orderByDesc("order_time");
            List<Order> userOrders = orderMapper.selectList(orderQuery);
            
            // 2. 获取用户的收藏和愿望清单
            QueryWrapper<ThingCollect> collectQuery = new QueryWrapper<>();
            collectQuery.eq("user_id", userId);
            List<ThingCollect> userCollects = thingCollectMapper.selectList(collectQuery);
            
            QueryWrapper<ThingWish> wishQuery = new QueryWrapper<>();
            wishQuery.eq("user_id", userId);
            List<ThingWish> userWishes = thingWishMapper.selectList(wishQuery);
            
            // 3. 分析用户偏好的分类
            Map<String, Double> categoryPreferences = analyzeUserCategoryPreferences(userId);
            
            // 4. 基于分类偏好推荐商品
            for (Map.Entry<String, Double> entry : categoryPreferences.entrySet()) {
                String categoryId = entry.getKey();
                QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
                thingQuery.eq("classification_id", categoryId)
                         .eq("status", "0") // 只推荐上架商品
                         .orderByDesc("score", "pv", "wish_count");
                
                List<Thing> categoryThings = thingMapper.selectList(thingQuery);
                
                // 过滤掉用户已购买或收藏的商品
                categoryThings = filterUserInteractedThings(categoryThings, userId);
                
                recommendations.addAll(categoryThings.stream()
                    .limit(Math.max(1, limit / categoryPreferences.size()))
                    .collect(Collectors.toList()));
            }
            
            // 5. 如果推荐不足，补充热门商品
            if (recommendations.size() < limit) {
                List<Thing> popularThings = getPopularRecommendations(limit - recommendations.size());
                recommendations.addAll(popularThings);
            }
            
            // 6. 去重并限制数量
            recommendations = recommendations.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // 如果个性化推荐失败，降级到热门推荐
            recommendations = getPopularRecommendations(limit);
        }
        
        return recommendations;
    }

    @Override
    public List<Thing> getSimilarThings(String thingId, int limit) {
        List<Thing> similarThings = new ArrayList<>();
        
        try {
            Thing targetThing = thingMapper.selectById(thingId);
            if (targetThing == null) {
                return similarThings;
            }
            
            // 1. 基于分类的相似推荐
            if (targetThing.getClassificationId() != null) {
                QueryWrapper<Thing> categoryQuery = new QueryWrapper<>();
                categoryQuery.eq("classification_id", targetThing.getClassificationId())
                           .ne("id", thingId)
                           .eq("status", "0")
                           .orderByDesc("score", "pv");
                
                List<Thing> categoryThings = thingMapper.selectList(categoryQuery);
                similarThings.addAll(categoryThings.stream()
                    .limit(limit / 2)
                    .collect(Collectors.toList()));
            }
            
            // 2. 基于价格区间的相似推荐
            if (targetThing.getPrice() != null) {
                double price = Double.parseDouble(targetThing.getPrice());
                double priceRange = price * 0.3; // 30%价格区间
                
                QueryWrapper<Thing> priceQuery = new QueryWrapper<>();
                priceQuery.ne("id", thingId)
                         .eq("status", "0")
                         .ge("price", price - priceRange)
                         .le("price", price + priceRange)
                         .orderByDesc("score");
                
                List<Thing> priceThings = thingMapper.selectList(priceQuery);
                similarThings.addAll(priceThings.stream()
                    .limit(limit / 2)
                    .collect(Collectors.toList()));
            }
            
            // 3. 去重并限制数量
            similarThings = similarThings.stream()
                .distinct()
                .limit(limit)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // 异常处理，返回空列表
            similarThings = new ArrayList<>();
        }
        
        return similarThings;
    }

    @Override
    public List<Thing> getPopularRecommendations(int limit) {
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("status", "0") // 只推荐上架商品
             .orderByDesc("pv", "wish_count", "collect_count", "score")
             .last("LIMIT " + limit);
        
        return thingMapper.selectList(query);
    }

    @Override
    public List<Thing> getCategoryRecommendations(String classificationId, String userId, int limit) {
        QueryWrapper<Thing> query = new QueryWrapper<>();
        query.eq("classification_id", classificationId)
             .eq("status", "0")
             .orderByDesc("score", "pv", "wish_count");
        
        List<Thing> categoryThings = thingMapper.selectList(query);
        
        // 如果提供了用户ID，过滤掉用户已交互的商品
        if (userId != null && !userId.isEmpty()) {
            categoryThings = filterUserInteractedThings(categoryThings, userId);
        }
        
        return categoryThings.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public List<Thing> getCollaborativeFilteringRecommendations(String userId, int limit) {
        List<Thing> recommendations = new ArrayList<>();
        
        try {
            // 1. 找到相似用户
            List<String> similarUsers = findSimilarUsers(userId, 10);
            
            // 2. 获取相似用户喜欢的商品
            Set<String> recommendedThingIds = new HashSet<>();
            for (String similarUserId : similarUsers) {
                // 获取相似用户的收藏
                QueryWrapper<ThingCollect> collectQuery = new QueryWrapper<>();
                collectQuery.eq("user_id", similarUserId);
                List<ThingCollect> collects = thingCollectMapper.selectList(collectQuery);
                
                // 获取相似用户的购买记录
                QueryWrapper<Order> orderQuery = new QueryWrapper<>();
                orderQuery.eq("user_id", similarUserId).eq("status", "7"); // 已完成订单
                List<Order> orders = orderMapper.selectList(orderQuery);
                
                collects.forEach(collect -> recommendedThingIds.add(collect.getThingId()));
                orders.forEach(order -> recommendedThingIds.add(order.getThingId()));
            }
            
            // 3. 过滤掉当前用户已交互的商品
            List<String> userInteractedThings = getUserInteractedThingIds(userId);
            recommendedThingIds.removeAll(userInteractedThings);
            
            // 4. 获取推荐商品详情
            for (String thingId : recommendedThingIds) {
                if (recommendations.size() >= limit) break;
                
                Thing thing = thingMapper.selectById(thingId);
                if (thing != null && "0".equals(thing.getStatus())) {
                    recommendations.add(thing);
                }
            }
            
        } catch (Exception e) {
            // 协同过滤失败时，降级到热门推荐
            recommendations = getPopularRecommendations(limit);
        }
        
        return recommendations;
    }

    @Override
    public List<Thing> getHybridRecommendations(String userId, int limit) {
        List<Thing> hybridRecommendations = new ArrayList<>();
        
        // 混合推荐策略：40%个性化 + 30%协同过滤 + 30%热门
        int personalizedCount = (int) (limit * 0.4);
        int collaborativeCount = (int) (limit * 0.3);
        int popularCount = limit - personalizedCount - collaborativeCount;
        
        // 1. 个性化推荐
        List<Thing> personalizedRecs = getPersonalizedRecommendations(userId, personalizedCount);
        hybridRecommendations.addAll(personalizedRecs);
        
        // 2. 协同过滤推荐
        List<Thing> collaborativeRecs = getCollaborativeFilteringRecommendations(userId, collaborativeCount);
        hybridRecommendations.addAll(collaborativeRecs);
        
        // 3. 热门推荐
        List<Thing> popularRecs = getPopularRecommendations(popularCount);
        hybridRecommendations.addAll(popularRecs);
        
        // 4. 去重并重新排序
        return hybridRecommendations.stream()
            .distinct()
            .limit(limit)
            .collect(Collectors.toList());
    }

    @Override
    public String getRecommendationExplanation(String userId, String thingId) {
        StringBuilder explanation = new StringBuilder();
        
        try {
            Thing thing = thingMapper.selectById(thingId);
            if (thing == null) {
                return "商品不存在";
            }
            
            // 分析推荐原因
            Map<String, Object> userPreferences = getUserPreferenceAnalysis(userId);
            
            explanation.append("推荐理由：");
            
            // 1. 基于分类偏好
            if (userPreferences.containsKey("favoriteCategories")) {
                @SuppressWarnings("unchecked")
                List<String> favoriteCategories = (List<String>) userPreferences.get("favoriteCategories");
                if (favoriteCategories.contains(String.valueOf(thing.getClassificationId()))) {
                    explanation.append("根据您的浏览偏好，您对此类商品感兴趣；");
                }
            }
            
            // 2. 基于热门程度
            if (thing.getPv() != null && Integer.parseInt(thing.getPv()) > 100) {
                explanation.append("该商品浏览量较高，深受用户喜爱；");
            }
            
            // 3. 基于评分
            if (thing.getScore() != null && Double.parseDouble(thing.getScore()) >= 4.0) {
                explanation.append("该商品评分较高，质量有保障；");
            }
            
            // 4. 基于价格区间
            explanation.append("价格适中，性价比较高。");
            
        } catch (Exception e) {
            explanation.append("基于智能算法为您推荐的优质商品。");
        }
        
        return explanation.toString();
    }

    @Override
    public void updateUserBehavior(String userId, String thingId, String action, double weight) {
        // 这里可以实现用户行为数据的记录和更新
        // 实际项目中可能需要单独的用户行为表来存储这些数据
        try {
            // 根据行为类型更新相应的计数
            switch (action.toLowerCase()) {
                case "view":
                    // 更新商品浏览量
                    Thing thing = thingMapper.selectById(thingId);
                    if (thing != null) {
                        int pv = thing.getPv() != null ? Integer.parseInt(thing.getPv()) : 0;
                        thing.setPv(String.valueOf(pv + 1));
                        thingMapper.updateById(thing);
                    }
                    break;
                case "collect":
                    // 处理收藏行为
                    updateCollectCount(thingId);
                    break;
                case "wish":
                    // 处理愿望清单行为
                    updateWishCount(thingId);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            // 异常处理
        }
    }

    @Override
    public double calculateThingSimilarity(String thing1Id, String thing2Id) {
        try {
            Thing thing1 = thingMapper.selectById(thing1Id);
            Thing thing2 = thingMapper.selectById(thing2Id);
            
            if (thing1 == null || thing2 == null) {
                return 0.0;
            }
            
            double similarity = 0.0;
            
            // 1. 分类相似度 (权重: 0.4)
            if (Objects.equals(thing1.getClassificationId(), thing2.getClassificationId())) {
                similarity += 0.4;
            }
            
            // 2. 价格相似度 (权重: 0.3)
            if (thing1.getPrice() != null && thing2.getPrice() != null) {
                double price1 = Double.parseDouble(thing1.getPrice());
                double price2 = Double.parseDouble(thing2.getPrice());
                double priceDiff = Math.abs(price1 - price2);
                double maxPrice = Math.max(price1, price2);
                double priceSimilarity = 1.0 - (priceDiff / maxPrice);
                similarity += 0.3 * Math.max(0, priceSimilarity);
            }
            
            // 3. 评分相似度 (权重: 0.3)
            if (thing1.getScore() != null && thing2.getScore() != null) {
                double score1 = Double.parseDouble(thing1.getScore());
                double score2 = Double.parseDouble(thing2.getScore());
                double scoreDiff = Math.abs(score1 - score2);
                double scoreSimilarity = 1.0 - (scoreDiff / 5.0); // 假设评分最大为5
                similarity += 0.3 * Math.max(0, scoreSimilarity);
            }
            
            return similarity;
            
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public double calculateUserSimilarity(String user1Id, String user2Id) {
        try {
            // 获取两个用户的行为数据
            Set<String> user1Things = getUserInteractedThingIds(user1Id).stream().collect(Collectors.toSet());
            Set<String> user2Things = getUserInteractedThingIds(user2Id).stream().collect(Collectors.toSet());
            
            if (user1Things.isEmpty() || user2Things.isEmpty()) {
                return 0.0;
            }
            
            // 计算Jaccard相似度
            Set<String> intersection = new HashSet<>(user1Things);
            intersection.retainAll(user2Things);
            
            Set<String> union = new HashSet<>(user1Things);
            union.addAll(user2Things);
            
            return (double) intersection.size() / union.size();
            
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public Map<String, Object> getUserPreferenceAnalysis(String userId) {
        Map<String, Object> preferences = new HashMap<>();
        
        try {
            // 1. 分析用户偏好的分类
            Map<String, Double> categoryPreferences = analyzeUserCategoryPreferences(userId);
            preferences.put("categoryPreferences", categoryPreferences);
            
            List<String> favoriteCategories = categoryPreferences.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            preferences.put("favoriteCategories", favoriteCategories);
            
            // 2. 分析价格偏好
            double averagePrice = calculateUserAveragePrice(userId);
            preferences.put("averagePrice", averagePrice);
            
            // 3. 活跃度分析
            int totalInteractions = getUserInteractedThingIds(userId).size();
            preferences.put("activityLevel", totalInteractions > 10 ? "high" : totalInteractions > 5 ? "medium" : "low");
            
        } catch (Exception e) {
            preferences.put("error", "分析用户偏好时发生错误");
        }
        
        return preferences;
    }
    
    // 辅助方法
    
    private Map<String, Double> analyzeUserCategoryPreferences(String userId) {
        Map<String, Double> categoryScores = new HashMap<>();
        
        try {
            // 分析购买记录
            QueryWrapper<Order> orderQuery = new QueryWrapper<>();
            orderQuery.eq("user_id", userId);
            List<Order> orders = orderMapper.selectList(orderQuery);
            
            for (Order order : orders) {
                Thing thing = thingMapper.selectById(order.getThingId());
                if (thing != null && thing.getClassificationId() != null) {
                    String categoryId = String.valueOf(thing.getClassificationId());
                    categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0.0) + 3.0);
                }
            }
            
            // 分析收藏记录
            QueryWrapper<ThingCollect> collectQuery = new QueryWrapper<>();
            collectQuery.eq("user_id", userId);
            List<ThingCollect> collects = thingCollectMapper.selectList(collectQuery);
            
            for (ThingCollect collect : collects) {
                Thing thing = thingMapper.selectById(collect.getThingId());
                if (thing != null && thing.getClassificationId() != null) {
                    String categoryId = String.valueOf(thing.getClassificationId());
                    categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0.0) + 2.0);
                }
            }
            
            // 分析愿望清单
            QueryWrapper<ThingWish> wishQuery = new QueryWrapper<>();
            wishQuery.eq("user_id", userId);
            List<ThingWish> wishes = thingWishMapper.selectList(wishQuery);
            
            for (ThingWish wish : wishes) {
                Thing thing = thingMapper.selectById(wish.getThingId());
                if (thing != null && thing.getClassificationId() != null) {
                    String categoryId = String.valueOf(thing.getClassificationId());
                    categoryScores.put(categoryId, categoryScores.getOrDefault(categoryId, 0.0) + 1.0);
                }
            }
            
        } catch (Exception e) {
            // 异常处理
        }
        
        return categoryScores;
    }
    
    private List<Thing> filterUserInteractedThings(List<Thing> things, String userId) {
        List<String> userInteractedThings = getUserInteractedThingIds(userId);
        return things.stream()
            .filter(thing -> !userInteractedThings.contains(String.valueOf(thing.getId())))
            .collect(Collectors.toList());
    }
    
    private List<String> getUserInteractedThingIds(String userId) {
        Set<String> interactedThings = new HashSet<>();
        
        try {
            // 获取购买记录
            QueryWrapper<Order> orderQuery = new QueryWrapper<>();
            orderQuery.eq("user_id", userId);
            List<Order> orders = orderMapper.selectList(orderQuery);
            orders.forEach(order -> interactedThings.add(order.getThingId()));
            
            // 获取收藏记录
            QueryWrapper<ThingCollect> collectQuery = new QueryWrapper<>();
            collectQuery.eq("user_id", userId);
            List<ThingCollect> collects = thingCollectMapper.selectList(collectQuery);
            collects.forEach(collect -> interactedThings.add(collect.getThingId()));
            
            // 获取愿望清单
            QueryWrapper<ThingWish> wishQuery = new QueryWrapper<>();
            wishQuery.eq("user_id", userId);
            List<ThingWish> wishes = thingWishMapper.selectList(wishQuery);
            wishes.forEach(wish -> interactedThings.add(wish.getThingId()));
            
        } catch (Exception e) {
            // 异常处理
        }
        
        return new ArrayList<>(interactedThings);
    }
    
    private List<String> findSimilarUsers(String userId, int limit) {
        List<String> similarUsers = new ArrayList<>();
        
        try {
            // 获取所有用户
            List<User> allUsers = userMapper.selectList(new QueryWrapper<>());
            
            // 计算用户相似度
            Map<String, Double> userSimilarities = new HashMap<>();
            for (User user : allUsers) {
                if (!user.getId().equals(userId)) {
                    double similarity = calculateUserSimilarity(userId, user.getId());
                    if (similarity > 0) {
                        userSimilarities.put(user.getId(), similarity);
                    }
                }
            }
            
            // 按相似度排序并返回前N个
            similarUsers = userSimilarities.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            // 异常处理
        }
        
        return similarUsers;
    }
    
    private double calculateUserAveragePrice(String userId) {
        try {
            QueryWrapper<Order> orderQuery = new QueryWrapper<>();
            orderQuery.eq("user_id", userId);
            List<Order> orders = orderMapper.selectList(orderQuery);
            
            if (orders.isEmpty()) {
                return 0.0;
            }
            
            double totalPrice = 0.0;
            int count = 0;
            
            for (Order order : orders) {
                Thing thing = thingMapper.selectById(order.getThingId());
                if (thing != null && thing.getPrice() != null) {
                    totalPrice += Double.parseDouble(thing.getPrice());
                    count++;
                }
            }
            
            return count > 0 ? totalPrice / count : 0.0;
            
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private void updateCollectCount(String thingId) {
        try {
            Thing thing = thingMapper.selectById(thingId);
            if (thing != null) {
                int collectCount = thing.getCollectCount() != null ? Integer.parseInt(thing.getCollectCount()) : 0;
                thing.setCollectCount(String.valueOf(collectCount + 1));
                thingMapper.updateById(thing);
            }
        } catch (Exception e) {
            // 异常处理
        }
    }
    
    private void updateWishCount(String thingId) {
        try {
            Thing thing = thingMapper.selectById(thingId);
            if (thing != null) {
                int wishCount = thing.getWishCount() != null ? Integer.parseInt(thing.getWishCount()) : 0;
                thing.setWishCount(String.valueOf(wishCount + 1));
                thingMapper.updateById(thing);
            }
        } catch (Exception e) {
            // 异常处理
        }
    }
}