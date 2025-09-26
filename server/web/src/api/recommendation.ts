import { defHttp } from '/@/utils/http/axios';

const Api = {
  personalized: '/recommendation/personalized',
  similar: '/recommendation/similar',
  popular: '/recommendation/popular',
  category: '/recommendation/category',
  collaborative: '/recommendation/collaborative',
  hybrid: '/recommendation/hybrid',
  explanation: '/recommendation/explanation',
  behavior: '/recommendation/behavior',
  userPreference: '/recommendation/user-preference',
  thingSimilarity: '/recommendation/similarity/thing',
  userSimilarity: '/recommendation/similarity/user',
};

/**
 * 获取个性化推荐
 * @param userId 用户ID
 * @param limit 推荐数量限制
 */
export function getPersonalizedRecommendations(userId: string, limit = 10) {
  return defHttp.get({
    url: Api.personalized,
    params: { userId, limit },
  });
}

/**
 * 获取相似商品推荐
 * @param thingId 商品ID
 * @param limit 推荐数量限制
 */
export function getSimilarThings(thingId: string, limit = 6) {
  return defHttp.get({
    url: Api.similar,
    params: { thingId, limit },
  });
}

/**
 * 获取热门推荐
 * @param limit 推荐数量限制
 */
export function getPopularRecommendations(limit = 10) {
  return defHttp.get({
    url: Api.popular,
    params: { limit },
  });
}

/**
 * 获取分类推荐
 * @param classificationId 分类ID
 * @param userId 用户ID（可选）
 * @param limit 推荐数量限制
 */
export function getCategoryRecommendations(classificationId: string, userId?: string, limit = 10) {
  return defHttp.get({
    url: Api.category,
    params: { classificationId, userId, limit },
  });
}

/**
 * 获取协同过滤推荐
 * @param userId 用户ID
 * @param limit 推荐数量限制
 */
export function getCollaborativeFilteringRecommendations(userId: string, limit = 10) {
  return defHttp.get({
    url: Api.collaborative,
    params: { userId, limit },
  });
}

/**
 * 获取混合推荐
 * @param userId 用户ID
 * @param limit 推荐数量限制
 */
export function getHybridRecommendations(userId: string, limit = 10) {
  return defHttp.get({
    url: Api.hybrid,
    params: { userId, limit },
  });
}

/**
 * 获取推荐解释
 * @param userId 用户ID
 * @param thingId 商品ID
 */
export function getRecommendationExplanation(userId: string, thingId: string) {
  return defHttp.get({
    url: Api.explanation,
    params: { userId, thingId },
  });
}

/**
 * 更新用户行为
 * @param userId 用户ID
 * @param thingId 商品ID
 * @param action 行为类型
 * @param weight 行为权重
 */
export function updateUserBehavior(userId: string, thingId: string, action: string, weight = 1.0) {
  return defHttp.post({
    url: Api.behavior,
    params: { userId, thingId, action, weight },
  });
}

/**
 * 获取用户偏好分析
 * @param userId 用户ID
 */
export function getUserPreferenceAnalysis(userId: string) {
  return defHttp.get({
    url: Api.userPreference,
    params: { userId },
  });
}

/**
 * 计算商品相似度
 * @param thing1Id 商品1 ID
 * @param thing2Id 商品2 ID
 */
export function calculateThingSimilarity(thing1Id: string, thing2Id: string) {
  return defHttp.get({
    url: Api.thingSimilarity,
    params: { thing1Id, thing2Id },
  });
}

/**
 * 计算用户相似度
 * @param user1Id 用户1 ID
 * @param user2Id 用户2 ID
 */
export function calculateUserSimilarity(user1Id: string, user2Id: string) {
  return defHttp.get({
    url: Api.userSimilarity,
    params: { user1Id, user2Id },
  });
}