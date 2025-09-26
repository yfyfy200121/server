<template>
  <div class="user-profile">
    <!-- 用户信息卡片 -->
    <div class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <a-avatar :size="80" :src="getUserAvatar()">
            <template #icon><UserOutlined /></template>
          </a-avatar>
          <a-upload
            :show-upload-list="false"
            :before-upload="beforeUploadAvatar"
            accept="image/*"
            class="avatar-upload"
          >
            <a-button size="small" type="link">
              <CameraOutlined /> 更换头像
            </a-button>
          </a-upload>
        </div>
        
        <div class="user-info">
          <h2 class="username">{{ userInfo.nickname || userInfo.username }}</h2>
          <div class="user-meta">
            <a-tag :color="getUserLevelColor(userStats.userLevel)">
              {{ userStats.userLevel }}
            </a-tag>
            <span class="join-time">
              加入时间：{{ formatTime(userInfo.createTime) }}
            </span>
          </div>
          <div class="user-contact" v-if="userInfo.email || userInfo.mobile">
            <span v-if="userInfo.email">
              <MailOutlined /> {{ userInfo.email }}
            </span>
            <span v-if="userInfo.mobile">
              <PhoneOutlined /> {{ userInfo.mobile }}
            </span>
          </div>
        </div>
      </div>

      <!-- 用户统计 -->
      <div class="profile-stats">
        <div class="stat-item">
          <div class="stat-number">{{ userStats.totalOrders || 0 }}</div>
          <div class="stat-label">总订单</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ userStats.completedOrders || 0 }}</div>
          <div class="stat-label">已完成</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ userStats.totalCollects || 0 }}</div>
          <div class="stat-label">收藏</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ userStats.totalWishes || 0 }}</div>
          <div class="stat-label">愿望清单</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ userStats.totalComments || 0 }}</div>
          <div class="stat-label">评论</div>
        </div>
        <div class="stat-item">
          <div class="stat-number">{{ userStats.activityScore || 0 }}</div>
          <div class="stat-label">活跃度</div>
        </div>
      </div>
    </div>

    <!-- 功能标签页 -->
    <div class="profile-content">
      <a-tabs v-model:activeKey="activeTab" type="card">
        <a-tab-pane key="info" tab="基本信息">
          <div class="info-section">
            <a-form
              :model="editForm"
              :rules="formRules"
              layout="vertical"
              ref="formRef"
            >
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="昵称" name="nickname">
                    <a-input v-model:value="editForm.nickname" placeholder="请输入昵称" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="邮箱" name="email">
                    <a-input v-model:value="editForm.email" placeholder="请输入邮箱" />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-row :gutter="24">
                <a-col :span="12">
                  <a-form-item label="手机号" name="mobile">
                    <a-input v-model:value="editForm.mobile" placeholder="请输入手机号" />
                  </a-form-item>
                </a-col>
                <a-col :span="12">
                  <a-form-item label="用户名" name="username">
                    <a-input v-model:value="editForm.username" disabled />
                  </a-form-item>
                </a-col>
              </a-row>
              <a-form-item label="个人简介" name="description">
                <a-textarea 
                  v-model:value="editForm.description" 
                  :rows="4" 
                  placeholder="介绍一下自己吧..."
                />
              </a-form-item>
              <a-form-item>
                <a-button type="primary" @click="updateProfile" :loading="updating">
                  保存修改
                </a-button>
                <a-button style="margin-left: 8px" @click="resetForm">
                  重置
                </a-button>
              </a-form-item>
            </a-form>
          </div>
        </a-tab-pane>

        <a-tab-pane key="security" tab="安全设置">
          <div class="security-section">
            <div class="security-item">
              <div class="security-info">
                <h4>修改密码</h4>
                <p>定期修改密码可以保护账户安全</p>
              </div>
              <a-button @click="showPasswordModal = true">修改密码</a-button>
            </div>
            
            <div class="security-item">
              <div class="security-info">
                <h4>邮箱验证</h4>
                <p>{{ userInfo.email ? `已绑定邮箱：${userInfo.email}` : '未绑定邮箱' }}</p>
              </div>
              <a-button @click="showEmailModal = true">
                {{ userInfo.email ? '更换邮箱' : '绑定邮箱' }}
              </a-button>
            </div>
            
            <div class="security-item danger">
              <div class="security-info">
                <h4>注销账户</h4>
                <p>注销后账户将被禁用，所有数据将被保留但无法访问</p>
              </div>
              <a-button danger @click="showDeactivateModal = true">注销账户</a-button>
            </div>
          </div>
        </a-tab-pane>

        <a-tab-pane key="preferences" tab="偏好设置">
          <div class="preferences-section">
            <a-form :model="preferencesForm" layout="vertical">
              <a-form-item label="偏好分类">
                <a-select
                  v-model:value="preferencesForm.favoriteCategories"
                  mode="multiple"
                  placeholder="选择您感兴趣的图书分类"
                  :options="categoryOptions"
                />
              </a-form-item>
              
              <a-form-item label="价格范围">
                <a-select v-model:value="preferencesForm.priceRange" placeholder="选择价格范围">
                  <a-select-option value="0-50">0-50元</a-select-option>
                  <a-select-option value="50-100">50-100元</a-select-option>
                  <a-select-option value="100-200">100-200元</a-select-option>
                  <a-select-option value="200+">200元以上</a-select-option>
                </a-select>
              </a-form-item>
              
              <a-form-item label="界面语言">
                <a-select v-model:value="preferencesForm.language" placeholder="选择界面语言">
                  <a-select-option value="zh-CN">简体中文</a-select-option>
                  <a-select-option value="en-US">English</a-select-option>
                </a-select>
              </a-form-item>
              
              <a-form-item label="通知设置">
                <a-checkbox-group v-model:value="preferencesForm.notifications">
                  <a-checkbox value="email">邮件通知</a-checkbox>
                  <a-checkbox value="order">订单通知</a-checkbox>
                  <a-checkbox value="recommendation">推荐通知</a-checkbox>
                </a-checkbox-group>
              </a-form-item>
              
              <a-form-item>
                <a-button type="primary" @click="updatePreferences" :loading="updatingPrefs">
                  保存偏好
                </a-button>
              </a-form-item>
            </a-form>
          </div>
        </a-tab-pane>

        <a-tab-pane key="activity" tab="活动记录">
          <div class="activity-section">
            <a-timeline>
              <a-timeline-item
                v-for="activity in activityLog"
                :key="activity.id"
                :color="getActivityColor(activity.type)"
              >
                <div class="activity-content">
                  <div class="activity-title">{{ activity.title }}</div>
                  <div class="activity-desc">{{ activity.description }}</div>
                  <div class="activity-time">{{ formatTime(activity.time) }}</div>
                </div>
              </a-timeline-item>
            </a-timeline>
            
            <div class="load-more" v-if="hasMoreActivity">
              <a-button @click="loadMoreActivity" :loading="loadingActivity">
                加载更多
              </a-button>
            </div>
          </div>
        </a-tab-pane>
      </a-tabs>
    </div>

    <!-- 修改密码模态框 -->
    <a-modal
      v-model:visible="showPasswordModal"
      title="修改密码"
      @ok="changePassword"
      :confirm-loading="changingPassword"
    >
      <a-form :model="passwordForm" layout="vertical">
        <a-form-item label="当前密码" required>
          <a-input-password v-model:value="passwordForm.currentPassword" />
        </a-form-item>
        <a-form-item label="新密码" required>
          <a-input-password v-model:value="passwordForm.newPassword" />
        </a-form-item>
        <a-form-item label="确认新密码" required>
          <a-input-password v-model:value="passwordForm.confirmPassword" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 邮箱绑定模态框 -->
    <a-modal
      v-model:visible="showEmailModal"
      title="邮箱设置"
      @ok="updateEmail"
      :confirm-loading="updatingEmail"
    >
      <a-form :model="emailForm" layout="vertical">
        <a-form-item label="新邮箱" required>
          <a-input v-model:value="emailForm.email" type="email" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 账户注销模态框 -->
    <a-modal
      v-model:visible="showDeactivateModal"
      title="注销账户"
      @ok="deactivateAccount"
      :confirm-loading="deactivating"
      ok-text="确认注销"
      ok-type="danger"
    >
      <div class="deactivate-warning">
        <a-alert
          message="警告"
          description="注销账户后，您将无法登录该账户，但您的数据将被保留。此操作不可逆，请谨慎操作。"
          type="warning"
          show-icon
        />
        <a-form :model="deactivateForm" layout="vertical" style="margin-top: 16px">
          <a-form-item label="请输入密码确认注销" required>
            <a-input-password v-model:value="deactivateForm.password" />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, onMounted } from 'vue'
import { message } from 'ant-design-vue'
import {
  UserOutlined,
  CameraOutlined,
  MailOutlined,
  PhoneOutlined
} from '@ant-design/icons-vue'
import { useUserStore } from '/@/store/modules/user'
import { BASE_URL } from '/@/store/constants'

// Store
const userStore = useUserStore()

// 响应式数据
const activeTab = ref('info')
const updating = ref(false)
const updatingPrefs = ref(false)
const changingPassword = ref(false)
const updatingEmail = ref(false)
const deactivating = ref(false)
const loadingActivity = ref(false)
const hasMoreActivity = ref(true)

// 模态框显示状态
const showPasswordModal = ref(false)
const showEmailModal = ref(false)
const showDeactivateModal = ref(false)

// 用户信息
const userInfo = reactive({
  id: '',
  username: '',
  nickname: '',
  email: '',
  mobile: '',
  description: '',
  avatar: '',
  createTime: ''
})

// 用户统计
const userStats = reactive({
  totalOrders: 0,
  completedOrders: 0,
  totalCollects: 0,
  totalWishes: 0,
  totalComments: 0,
  activityScore: 0,
  userLevel: '新手'
})

// 编辑表单
const editForm = reactive({
  nickname: '',
  email: '',
  mobile: '',
  username: '',
  description: ''
})

// 偏好设置表单
const preferencesForm = reactive({
  favoriteCategories: [],
  priceRange: '',
  language: 'zh-CN',
  notifications: ['email', 'order']
})

// 密码修改表单
const passwordForm = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

// 邮箱表单
const emailForm = reactive({
  email: ''
})

// 注销表单
const deactivateForm = reactive({
  password: ''
})

// 活动记录
const activityLog = ref([
  {
    id: 1,
    type: 'order',
    title: '完成订单',
    description: '成功购买《Java编程思想》',
    time: '2023-12-01 10:30:00'
  },
  {
    id: 2,
    type: 'collect',
    title: '收藏商品',
    description: '收藏了《算法导论》',
    time: '2023-11-28 15:20:00'
  },
  {
    id: 3,
    type: 'comment',
    title: '发表评论',
    description: '对《数据结构与算法分析》进行了评价',
    time: '2023-11-25 09:15:00'
  }
])

// 分类选项
const categoryOptions = ref([
  { label: '计算机科学', value: '1' },
  { label: '编程语言', value: '2' },
  { label: '算法与数据结构', value: '3' },
  { label: '人工智能', value: '4' },
  { label: '数据库', value: '5' }
])

// 表单验证规则
const formRules = {
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
  ],
  mobile: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
}

// 方法
const getUserAvatar = () => {
  if (!userInfo.avatar) return ''
  if (userInfo.avatar.startsWith('http')) return userInfo.avatar
  return `${BASE_URL}/api/staticfiles/image/${userInfo.avatar}`
}

const getUserLevelColor = (level: string) => {
  const colorMap: { [key: string]: string } = {
    '新手': 'default',
    '中级': 'blue',
    '高级': 'green',
    '专家': 'gold'
  }
  return colorMap[level] || 'default'
}

const formatTime = (time: string) => {
  if (!time) return ''
  const date = new Date(parseInt(time))
  return date.toLocaleString('zh-CN')
}

const getActivityColor = (type: string) => {
  const colorMap: { [key: string]: string } = {
    'order': 'green',
    'collect': 'blue',
    'comment': 'orange',
    'login': 'gray'
  }
  return colorMap[type] || 'gray'
}

const beforeUploadAvatar = (file: File) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    message.error('只能上传图片文件!')
    return false
  }
  
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    message.error('图片大小不能超过 2MB!')
    return false
  }
  
  // 这里实现头像上传逻辑
  uploadAvatar(file)
  return false
}

const uploadAvatar = async (file: File) => {
  try {
    const formData = new FormData()
    formData.append('avatarFile', file)
    formData.append('userId', userInfo.id)
    
    // 调用上传接口
    // const response = await uploadAvatarApi(formData)
    message.success('头像上传成功')
    // 更新头像显示
    // userInfo.avatar = response.data.avatar
  } catch (error) {
    message.error('头像上传失败')
  }
}

const updateProfile = async () => {
  updating.value = true
  try {
    // 调用更新用户信息接口
    // await updateUserInfoApi(editForm)
    
    // 更新本地用户信息
    Object.assign(userInfo, editForm)
    message.success('个人信息更新成功')
  } catch (error) {
    message.error('个人信息更新失败')
  } finally {
    updating.value = false
  }
}

const resetForm = () => {
  Object.assign(editForm, {
    nickname: userInfo.nickname,
    email: userInfo.email,
    mobile: userInfo.mobile,
    username: userInfo.username,
    description: userInfo.description
  })
}

const updatePreferences = async () => {
  updatingPrefs.value = true
  try {
    // 调用更新偏好设置接口
    // await updateUserPreferencesApi(preferencesForm)
    message.success('偏好设置保存成功')
  } catch (error) {
    message.error('偏好设置保存失败')
  } finally {
    updatingPrefs.value = false
  }
}

const changePassword = async () => {
  if (passwordForm.newPassword !== passwordForm.confirmPassword) {
    message.error('两次输入的密码不一致')
    return
  }
  
  changingPassword.value = true
  try {
    // 调用修改密码接口
    // await changePasswordApi(passwordForm)
    message.success('密码修改成功')
    showPasswordModal.value = false
    
    // 清空表单
    Object.assign(passwordForm, {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    })
  } catch (error) {
    message.error('密码修改失败')
  } finally {
    changingPassword.value = false
  }
}

const updateEmail = async () => {
  updatingEmail.value = true
  try {
    // 调用更新邮箱接口
    // await updateEmailApi(emailForm)
    userInfo.email = emailForm.email
    message.success('邮箱更新成功')
    showEmailModal.value = false
  } catch (error) {
    message.error('邮箱更新失败')
  } finally {
    updatingEmail.value = false
  }
}

const deactivateAccount = async () => {
  if (!deactivateForm.password) {
    message.error('请输入密码确认')
    return
  }
  
  deactivating.value = true
  try {
    // 调用注销账户接口
    // await deactivateAccountApi(deactivateForm)
    message.success('账户已注销')
    showDeactivateModal.value = false
    
    // 退出登录
    userStore.logout()
  } catch (error) {
    message.error('账户注销失败')
  } finally {
    deactivating.value = false
  }
}

const loadUserInfo = async () => {
  try {
    // 获取用户信息
    // const userResponse = await getUserDetailApi(userStore.user_id)
    // Object.assign(userInfo, userResponse.data)
    
    // 获取用户统计
    // const statsResponse = await getUserStatsApi(userStore.user_id)
    // Object.assign(userStats, statsResponse.data)
    
    // 初始化编辑表单
    resetForm()
  } catch (error) {
    message.error('加载用户信息失败')
  }
}

const loadMoreActivity = async () => {
  loadingActivity.value = true
  try {
    // 加载更多活动记录
    // const response = await getActivityLogApi()
    // activityLog.value.push(...response.data)
    
    await new Promise(resolve => setTimeout(resolve, 1000)) // 模拟加载
    hasMoreActivity.value = false
  } catch (error) {
    message.error('加载活动记录失败')
  } finally {
    loadingActivity.value = false
  }
}

// 生命周期
onMounted(() => {
  loadUserInfo()
})
</script>

<style lang="less" scoped>
.user-profile {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;

  .profile-card {
    background: #fff;
    border-radius: 12px;
    padding: 32px;
    margin-bottom: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .profile-header {
      display: flex;
      align-items: flex-start;
      margin-bottom: 32px;

      .avatar-section {
        text-align: center;
        margin-right: 32px;

        .avatar-upload {
          margin-top: 8px;
        }
      }

      .user-info {
        flex: 1;

        .username {
          margin: 0 0 8px 0;
          font-size: 28px;
          font-weight: 600;
          color: #262626;
        }

        .user-meta {
          display: flex;
          align-items: center;
          gap: 16px;
          margin-bottom: 12px;

          .join-time {
            color: #8c8c8c;
            font-size: 14px;
          }
        }

        .user-contact {
          display: flex;
          gap: 24px;
          color: #595959;
          font-size: 14px;

          span {
            display: flex;
            align-items: center;
            gap: 4px;
          }
        }
      }
    }

    .profile-stats {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
      gap: 24px;
      padding: 24px 0;
      border-top: 1px solid #f0f0f0;

      .stat-item {
        text-align: center;

        .stat-number {
          font-size: 24px;
          font-weight: 600;
          color: #1890ff;
          margin-bottom: 4px;
        }

        .stat-label {
          font-size: 14px;
          color: #8c8c8c;
        }
      }
    }
  }

  .profile-content {
    background: #fff;
    border-radius: 12px;
    padding: 24px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    .info-section {
      .ant-form {
        max-width: 600px;
      }
    }

    .security-section {
      .security-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 24px 0;
        border-bottom: 1px solid #f0f0f0;

        &:last-child {
          border-bottom: none;
        }

        &.danger {
          .security-info h4 {
            color: #ff4d4f;
          }
        }

        .security-info {
          h4 {
            margin: 0 0 4px 0;
            font-size: 16px;
            font-weight: 500;
          }

          p {
            margin: 0;
            color: #8c8c8c;
            font-size: 14px;
          }
        }
      }
    }

    .preferences-section {
      max-width: 600px;
    }

    .activity-section {
      .activity-content {
        .activity-title {
          font-weight: 500;
          margin-bottom: 4px;
        }

        .activity-desc {
          color: #8c8c8c;
          font-size: 14px;
          margin-bottom: 4px;
        }

        .activity-time {
          color: #bfbfbf;
          font-size: 12px;
        }
      }

      .load-more {
        text-align: center;
        margin-top: 24px;
      }
    }
  }

  .deactivate-warning {
    .ant-alert {
      margin-bottom: 16px;
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .user-profile {
    padding: 16px;

    .profile-card {
      padding: 20px;

      .profile-header {
        flex-direction: column;
        text-align: center;

        .avatar-section {
          margin-right: 0;
          margin-bottom: 20px;
        }
      }

      .profile-stats {
        grid-template-columns: repeat(3, 1fr);
        gap: 16px;
      }
    }

    .profile-content {
      padding: 16px;
    }
  }
}
</style>