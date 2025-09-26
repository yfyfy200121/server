<template>
  <div class="intelligent-recommendations">
    <!-- 推荐标题 -->
    <div class="recommendations-header">
      <h2 class="section-title">
        <i class="recommend-icon"></i>
        智能推荐
      </h2>
      <div class="recommendation-tabs">
        <a-tabs v-model:activeKey="activeTab" @change="handleTabChange" type="card" size="small">
          <a-tab-pane key="personalized" tab="个性化推荐" />
          <a-tab-pane key="popular" tab="热门推荐" />
          <a-tab-pane key="similar" tab="相似推荐" v-if="similarBaseId" />
          <a-tab-pane key="category" tab="分类推荐" v-if="categoryId" />
        </a-tabs>
      </div>
    </div>

    <!-- 推荐商品列表 -->
    <div class="recommendations-content" v-loading="loading">
      <div class="recommendation-explanation" v-if="explanation">
        <a-alert :message="explanation" type="info" show-icon style="margin-bottom: 16px" />
      </div>

      <div class="products-grid">
        <div 
          class="product-card" 
          v-for="product in recommendations" 
          :key="product.id"
          @click="viewProduct(product.id)"
          @mouseenter="onProductHover(product.id)"
        >
          <div class="product-image">
            <img :src="getImageUrl(product.cover)" :alt="product.title" />
            <div class="product-overlay">
              <div class="product-actions">
                <a-button type="primary" size="small" @click.stop="addToCart(product.id)">
                  <i class="cart-icon"></i>
                  加入购物车
                </a-button>
                <a-button size="small" @click.stop="addToWishlist(product.id)">
                  <i class="heart-icon"></i>
                  收藏
                </a-button>
              </div>
            </div>
          </div>
          
          <div class="product-info">
            <h3 class="product-title" :title="product.title">{{ product.title }}</h3>
            <div class="product-meta">
              <span class="product-price">¥{{ product.price }}</span>
              <span class="product-rating" v-if="product.score">
                <a-rate :value="parseFloat(product.score)" disabled allow-half size="small" />
                <span class="rating-text">({{ product.score }}分)</span>
              </span>
            </div>
            <div class="product-stats">
              <span class="stat-item">
                <i class="view-icon"></i>
                {{ product.pv || 0 }}浏览
              </span>
              <span class="stat-item">
                <i class="wish-icon"></i>
                {{ product.wishCount || 0 }}收藏
              </span>
            </div>
            
            <!-- 推荐标签 -->
            <div class="recommendation-tags" v-if="getRecommendationTags(product).length > 0">
              <a-tag 
                v-for="tag in getRecommendationTags(product)" 
                :key="tag.type" 
                :color="tag.color"
                size="small"
              >
                {{ tag.label }}
              </a-tag>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="!loading && recommendations.length === 0">
        <div class="empty-content">
          <i class="empty-icon"></i>
          <h3>暂无推荐</h3>
          <p>我们正在为您寻找更多精彩内容</p>
          <a-button type="primary" @click="refreshRecommendations">刷新推荐</a-button>
        </div>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore && !loading">
        <a-button @click="loadMore" :loading="loadingMore">
          加载更多推荐
        </a-button>
      </div>
    </div>

    <!-- 用户偏好分析模态框 -->
    <a-modal
      v-model:visible="showPreferenceModal"
      title="您的阅读偏好分析"
      width="600px"
      :footer="null"
    >
      <div class="preference-analysis" v-if="userPreference">
        <div class="preference-section">
          <h4>偏好分类</h4>
          <div class="category-preferences">
            <div 
              v-for="(score, categoryId) in userPreference.categoryPreferences" 
              :key="categoryId"
              class="category-item"
            >
              <span class="category-name">{{ getCategoryName(categoryId) }}</span>
              <a-progress 
                :percent="Math.round((score / getMaxCategoryScore()) * 100)" 
                size="small"
                :show-info="false"
              />
              <span class="score-text">{{ score.toFixed(1) }}</span>
            </div>
          </div>
        </div>

        <div class="preference-section">
          <h4>价格偏好</h4>
          <p>您的平均购买价格：<strong>¥{{ userPreference.averagePrice?.toFixed(2) || '0.00' }}</strong></p>
        </div>

        <div class="preference-section">
          <h4>活跃度</h4>
          <a-tag 
            :color="getActivityColor(userPreference.activityLevel)"
            size="large"
          >
            {{ getActivityLabel(userPreference.activityLevel) }}
          </a-tag>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, onMounted, computed, watch } from 'vue'
import { message } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '/@/store/modules/user'
import { 
  getPersonalizedRecommendations,
  getPopularRecommendations,
  getSimilarThings,
  getCategoryRecommendations,
  updateUserBehavior,
  getUserPreferenceAnalysis,
  getRecommendationExplanation
} from '/@/api/recommendation'
import { BASE_URL } from '/@/store/constants'

interface Product {
  id: number
  title: string
  cover: string
  price: string
  score?: string
  pv?: string
  wishCount?: string
  collectCount?: string
  classificationId?: number
}

interface RecommendationTag {
  type: string
  label: string
  color: string
}

// Props
interface Props {
  userId?: string
  similarBaseId?: string
  categoryId?: string
  limit?: number
}

const props = withDefaults(defineProps<Props>(), {
  limit: 12
})

// State
const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const loadingMore = ref(false)
const activeTab = ref('personalized')
const recommendations = ref<Product[]>([])
const explanation = ref('')
const hasMore = ref(true)
const currentPage = ref(1)
const showPreferenceModal = ref(false)
const userPreference = ref<any>(null)

// Computed
const currentUserId = computed(() => props.userId || userStore.user_id)

// Methods
const handleTabChange = (key: string) => {
  activeTab.value = key
  loadRecommendations()
}

const loadRecommendations = async () => {
  if (!currentUserId.value && activeTab.value === 'personalized') {
    loadPopularRecommendations()
    return
  }

  loading.value = true
  try {
    let result
    
    switch (activeTab.value) {
      case 'personalized':
        result = await getPersonalizedRecommendations(currentUserId.value, props.limit)
        break
      case 'popular':
        result = await getPopularRecommendations(props.limit)
        break
      case 'similar':
        if (props.similarBaseId) {
          result = await getSimilarThings(props.similarBaseId, props.limit)
        }
        break
      case 'category':
        if (props.categoryId) {
          result = await getCategoryRecommendations(props.categoryId, currentUserId.value, props.limit)
        }
        break
      default:
        result = await getPopularRecommendations(props.limit)
    }

    if (result && result.code === 200) {
      recommendations.value = result.data.recommendations || []
      hasMore.value = recommendations.value.length >= props.limit
      
      // 获取推荐解释（仅对个性化推荐）
      if (activeTab.value === 'personalized' && recommendations.value.length > 0 && currentUserId.value) {
        loadRecommendationExplanation(recommendations.value[0].id)
      }
    }
  } catch (error) {
    console.error('加载推荐失败:', error)
    message.error('加载推荐失败')
  } finally {
    loading.value = false
  }
}

const loadPopularRecommendations = async () => {
  activeTab.value = 'popular'
  await loadRecommendations()
}

const loadRecommendationExplanation = async (thingId: number) => {
  if (!currentUserId.value) return
  
  try {
    const result = await getRecommendationExplanation(currentUserId.value, String(thingId))
    if (result && result.code === 200) {
      explanation.value = result.data.explanation
    }
  } catch (error) {
    console.error('获取推荐解释失败:', error)
  }
}

const loadMore = async () => {
  loadingMore.value = true
  currentPage.value++
  
  try {
    // 实现加载更多逻辑
    const moreRecommendations = await loadRecommendations()
    // 这里需要实现分页逻辑
  } catch (error) {
    console.error('加载更多失败:', error)
    message.error('加载更多失败')
  } finally {
    loadingMore.value = false
  }
}

const refreshRecommendations = () => {
  currentPage.value = 1
  loadRecommendations()
}

const viewProduct = (productId: number) => {
  // 记录用户行为
  if (currentUserId.value) {
    updateUserBehavior(currentUserId.value, String(productId), 'view', 1.0)
  }
  
  router.push(`/detail?id=${productId}`)
}

const onProductHover = (productId: number) => {
  // 记录悬停行为（权重较低）
  if (currentUserId.value) {
    updateUserBehavior(currentUserId.value, String(productId), 'hover', 0.1)
  }
}

const addToCart = (productId: number) => {
  // 实现加入购物车逻辑
  if (currentUserId.value) {
    updateUserBehavior(currentUserId.value, String(productId), 'cart', 2.0)
  }
  message.success('已加入购物车')
}

const addToWishlist = (productId: number) => {
  // 实现加入愿望清单逻辑
  if (currentUserId.value) {
    updateUserBehavior(currentUserId.value, String(productId), 'wish', 1.5)
  }
  message.success('已加入收藏夹')
}

const getImageUrl = (cover: string) => {
  if (!cover) return '/src/assets/images/default-book.png'
  if (cover.startsWith('http')) return cover
  return `${BASE_URL}/api/staticfiles/image/${cover}`
}

const getRecommendationTags = (product: Product): RecommendationTag[] => {
  const tags: RecommendationTag[] = []
  
  // 根据不同维度添加标签
  if (product.score && parseFloat(product.score) >= 4.5) {
    tags.push({ type: 'high-rating', label: '高评分', color: 'gold' })
  }
  
  if (product.pv && parseInt(product.pv) > 1000) {
    tags.push({ type: 'popular', label: '热门', color: 'red' })
  }
  
  if (product.wishCount && parseInt(product.wishCount) > 100) {
    tags.push({ type: 'loved', label: '受欢迎', color: 'pink' })
  }
  
  return tags
}

const showUserPreference = async () => {
  if (!currentUserId.value) {
    message.warning('请先登录')
    return
  }
  
  try {
    const result = await getUserPreferenceAnalysis(currentUserId.value)
    if (result && result.code === 200) {
      userPreference.value = result.data
      showPreferenceModal.value = true
    }
  } catch (error) {
    console.error('获取用户偏好失败:', error)
    message.error('获取用户偏好失败')
  }
}

const getCategoryName = (categoryId: string) => {
  // 这里应该从分类映射中获取名称
  const categoryMap: { [key: string]: string } = {
    '1': '计算机科学',
    '2': '编程语言',
    '3': '算法与数据结构',
    '4': '人工智能',
    '5': '数据库'
  }
  return categoryMap[categoryId] || `分类${categoryId}`
}

const getMaxCategoryScore = () => {
  if (!userPreference.value?.categoryPreferences) return 1
  return Math.max(...Object.values(userPreference.value.categoryPreferences) as number[])
}

const getActivityColor = (level: string) => {
  const colorMap: { [key: string]: string } = {
    'high': 'green',
    'medium': 'orange',
    'low': 'red'
  }
  return colorMap[level] || 'default'
}

const getActivityLabel = (level: string) => {
  const labelMap: { [key: string]: string } = {
    'high': '高活跃用户',
    'medium': '中等活跃用户',
    'low': '低活跃用户'
  }
  return labelMap[level] || '未知'
}

// Lifecycle
onMounted(() => {
  loadRecommendations()
})

// Watch
watch(() => props.userId, () => {
  loadRecommendations()
})

watch(() => props.similarBaseId, () => {
  if (props.similarBaseId) {
    activeTab.value = 'similar'
    loadRecommendations()
  }
})

watch(() => props.categoryId, () => {
  if (props.categoryId) {
    activeTab.value = 'category'
    loadRecommendations()
  }
})
</script>

<style lang="less" scoped>
.intelligent-recommendations {
  background: #fff;
  border-radius: 8px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

  .recommendations-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    border-bottom: 1px solid #f0f0f0;
    padding-bottom: 16px;

    .section-title {
      margin: 0;
      font-size: 20px;
      font-weight: 600;
      color: #262626;
      display: flex;
      align-items: center;

      .recommend-icon {
        width: 24px;
        height: 24px;
        background: url('/src/assets/images/recommend.svg') no-repeat center;
        background-size: contain;
        margin-right: 8px;
      }
    }

    .recommendation-tabs {
      flex: 1;
      margin-left: 24px;
    }
  }

  .recommendations-content {
    .recommendation-explanation {
      margin-bottom: 16px;
    }

    .products-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 20px;
      margin-bottom: 24px;
    }

    .product-card {
      border: 1px solid #f0f0f0;
      border-radius: 8px;
      overflow: hidden;
      transition: all 0.3s ease;
      cursor: pointer;
      background: #fff;

      &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
        border-color: #1890ff;

        .product-overlay {
          opacity: 1;
        }
      }

      .product-image {
        position: relative;
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.3s ease;
        }

        .product-overlay {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          opacity: 0;
          transition: opacity 0.3s ease;

          .product-actions {
            display: flex;
            gap: 8px;

            .ant-btn {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }

      .product-info {
        padding: 16px;

        .product-title {
          font-size: 16px;
          font-weight: 500;
          margin: 0 0 8px 0;
          line-height: 1.4;
          height: 44px;
          overflow: hidden;
          display: -webkit-box;
          -webkit-line-clamp: 2;
          -webkit-box-orient: vertical;
          color: #262626;
        }

        .product-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .product-price {
            font-size: 18px;
            font-weight: 600;
            color: #ff4d4f;
          }

          .product-rating {
            display: flex;
            align-items: center;
            gap: 4px;

            .rating-text {
              font-size: 12px;
              color: #8c8c8c;
            }
          }
        }

        .product-stats {
          display: flex;
          gap: 16px;
          margin-bottom: 8px;

          .stat-item {
            font-size: 12px;
            color: #8c8c8c;
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }

        .recommendation-tags {
          display: flex;
          gap: 4px;
          flex-wrap: wrap;
        }
      }
    }

    .empty-state {
      text-align: center;
      padding: 60px 20px;

      .empty-content {
        .empty-icon {
          width: 64px;
          height: 64px;
          background: url('/src/assets/images/empty.svg') no-repeat center;
          background-size: contain;
          margin: 0 auto 16px;
          opacity: 0.4;
        }

        h3 {
          font-size: 16px;
          color: #262626;
          margin-bottom: 8px;
        }

        p {
          color: #8c8c8c;
          margin-bottom: 24px;
        }
      }
    }

    .load-more {
      text-align: center;
      padding: 24px 0;
    }
  }

  .preference-analysis {
    .preference-section {
      margin-bottom: 24px;

      h4 {
        font-size: 16px;
        font-weight: 500;
        margin-bottom: 12px;
        color: #262626;
      }

      .category-preferences {
        .category-item {
          display: flex;
          align-items: center;
          margin-bottom: 8px;

          .category-name {
            width: 120px;
            font-size: 14px;
          }

          .ant-progress {
            flex: 1;
            margin: 0 12px;
          }

          .score-text {
            font-size: 12px;
            color: #8c8c8c;
            width: 40px;
            text-align: right;
          }
        }
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .intelligent-recommendations {
    padding: 16px;

    .recommendations-header {
      flex-direction: column;
      align-items: stretch;

      .recommendation-tabs {
        margin-left: 0;
        margin-top: 16px;
      }
    }

    .products-grid {
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
    }
  }
}
</style>