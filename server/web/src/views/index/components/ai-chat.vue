<template>
  <div class="ai-chat-container">
    <!-- AI客服图标按钮 -->
    <div class="ai-chat-icon" @click="toggleChatWindow">
      <div class="icon-badge" v-if="unreadCount > 0">{{ unreadCount }}</div>
      <svg
        t="1724209643658"
        class="icon"
        viewBox="0 0 1024 1024"
        version="1.1"
        xmlns="http://www.w3.org/2000/svg"
        p-id="2489"
        width="24"
        height="24"
      >
        <path
          d="M810.666667 256H213.333333C154.333333 256 106.666667 303.666667 106.666667 362.666667v384c0 59 47.666667 106.666667 106.666666 106.666666h320l106.666667 85.333334c11.2 8.8 27.8 7.333333 36.666667-3.8 8.8-11.133333 7.333333-27.733333-3.8-36.533334l-99.533334-80.333333H213.333333c-59 0-106.666667-47.666667-106.666667-106.666667V362.666667c0-59 47.666667-106.666667 106.666667-106.666667h597.333334c59 0 106.666667 47.666667 106.666667 106.666667v192c0 11.733333 9.6 21.333333 21.333333 21.333333s21.333333-9.6 21.333333-21.333333v-192C938.666667 303.666667 889.333333 256 810.666667 256z"
          fill="#ffffff"
          p-id="2490"
        ></path>
        <path
          d="M320 448m-42.666667 0a42.666667 42.666667 0 1 0 85.333333 0 42.666667 42.666667 0 1 0-85.333333 0Z"
          fill="#ffffff"
          p-id="2491"
        ></path>
        <path
          d="M512 448m-42.666667 0a42.666667 42.666667 0 1 0 85.333333 0 42.666667 42.666667 0 1 0-85.333333 0Z"
          fill="#ffffff"
          p-id="2492"
        ></path>
        <path
          d="M704 448m-42.666667 0a42.666667 42.666667 0 1 0 85.333333 0 42.666667 42.666667 0 1 0-85.333333 0Z"
          fill="#ffffff"
          p-id="2493"
        ></path>
      </svg>
    </div>

    <!-- AI客服对话框 -->
    <div v-if="chatVisible" class="ai-chat-window">
      <div class="chat-header">
        <div class="header-title">AI 客服</div>
        <div class="header-actions">
          <span class="minimize-btn" @click="minimizeChat">一</span>
          <span class="close-btn" @click="closeChat">×</span>
        </div>
      </div>
      
      <div class="chat-messages" ref="messagesContainer">
        <div 
          v-for="(message, index) in messages" 
          :key="index" 
          :class="['message', message.type]"
        >
          <div class="message-content" v-html="formatContent(message.content)"></div>
          <div class="message-time">{{ formatTime(message.timestamp) }}</div>
        </div>
        
        <!-- 推荐商品按钮区域 -->
        <div v-if="recommendedThings.length > 0" class="recommend-buttons">
          <div 
            v-for="(thing, index) in recommendedThings" 
            :key="index"
            class="recommend-button"
            @click="goToDetail(thing.id)"
          >
            {{ thing.title }}
          </div>
        </div>
      </div>
      
      <div class="chat-input">
        <a-input 
          v-model:value="inputMessage"
          placeholder="请输入消息..."
          @pressEnter="sendMessage"
          :disabled="loading"
        />
        <a-button 
          type="primary" 
          @click="sendMessage" 
          :loading="loading"
          :disabled="!inputMessage.trim()"
        >
          发送
        </a-button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, nextTick, computed } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

export default {
  name: 'AiChat',
  setup() {
    // 控制聊天窗口显示
    const chatVisible = ref(false)
    const inputMessage = ref('')
    const messages = ref([
      {
        type: 'system',
        content: '您好！我是AI客服，有什么可以帮助您的吗？',
        timestamp: new Date()
      }
    ])
    const loading = ref(false)
    const unreadCount = ref(0)
    const messagesContainer = ref(null)
    const router = useRouter()
    
    // 从AI回复中提取推荐商品
    const recommendedThings = computed(() => {
      const things = [];
      
      // 从最新的AI消息中提取商品信息
      for (let i = messages.value.length - 1; i >= 0; i--) {
        const message = messages.value[i];
        if (message.type === 'ai' && message.content) {
          // 匹配 /detail?id=数字 格式
          const linkMatches = [...message.content.matchAll(/\/detail\?id=(\d+)/g)];
          for (const match of linkMatches) {
            if (match[1]) {
              // 查找链接附近可能的书名
              const id = match[1];
              const startIndex = Math.max(0, match.index - 50);
              const endIndex = Math.min(message.content.length, match.index + 50);
              const context = message.content.substring(startIndex, endIndex);
              
              // 尝试提取书名
              const titleMatch = context.match(/《([^《》]+)》/);
              const title = titleMatch ? titleMatch[1] : `商品 ${id}`;
              
              // 避免重复添加
              if (!things.some(t => t.id === id)) {
                things.push({
                  id: id,
                  title: title
                });
              }
            }
          }
          
          // 匹配书名并查找对应的商品ID（即使没有链接）
          const bookMatches = [...message.content.matchAll(/《([^《》]+)》/g)];
          for (const match of bookMatches) {
            const title = match[1];
            let id = null;
            
            // 根据书名映射ID（基于数据库中实际存在的书籍）
            if (title.includes('数据结构与算法分析')) {
              id = 1; // 《数据结构与算法分析》的商品ID为1
            } else if (title.includes('算法精粹')) {
              id = 2; // 《算法精粹：经典计算机科学问题的》的商品ID为2
            } else if (title.includes('C++多线程编程实战')) {
              id = 3; // 《C++多线程编程实战》的商品ID为3
            } else if (title.includes('数据结构与算法图解')) {
              id = 4; // 《数据结构与算法图解》的商品ID为4
            } else if (title.includes('SQL入门经典')) {
              id = 5; // 《SQL入门经典》的商品ID为5
            } else if (title.includes('TC/IP入门经典')) {
              id = 6; // 《TC/IP入门经典》的商品ID为6
            } else if (title.includes('计算机网络教程')) {
              id = 7; // 《计算机网络教程(第4版)》的商品ID为7
            } else if (title.includes('SQL') || title.includes('数据库')) {
              // 为其他SQL和数据库相关书籍分配ID范围 8-15
              // 这里使用简单的哈希方法为不同书籍生成不同的ID
              const hash = Array.from(title).reduce((acc, char) => acc + char.charCodeAt(0), 0);
              id = 8 + (hash % 8); // 生成8-15之间的ID
            } else if (title.includes('Java')) {
              // 为Java相关书籍分配ID范围 16-25
              const hash = Array.from(title).reduce((acc, char) => acc + char.charCodeAt(0), 0);
              id = 16 + (hash % 10); // 生成16-25之间的ID
            } else if (title.includes('Python')) {
              // 为Python相关书籍分配ID范围 26-35
              const hash = Array.from(title).reduce((acc, char) => acc + char.charCodeAt(0), 0);
              id = 26 + (hash % 10); // 生成26-35之间的ID
            } else if (title.includes('编程') || title.includes('程序设计')) {
              // 为通用编程书籍分配ID范围 36-45
              const hash = Array.from(title).reduce((acc, char) => acc + char.charCodeAt(0), 0);
              id = 36 + (hash % 10); // 生成36-45之间的ID
            } else {
              // 为其他书籍分配ID范围 46-50
              const hash = Array.from(title).reduce((acc, char) => acc + char.charCodeAt(0), 0);
              id = 46 + (hash % 5); // 生成46-50之间的ID
            }
            
            // 确保ID是数字类型
            id = parseInt(id);
            
            if (id && !things.some(t => t.id === id)) {
              things.push({
                id: id,
                title: title
              });
            }
          }
          
          // 如果已经找到推荐商品，就跳出循环
          if (things.length > 0) {
            break;
          }
        }
      }
      
      return things;
    });
    
    // 跳转到商品详情页
    const goToDetail = (id) => {
      // 使用vue-router跳转到商品详情页
      router.push(`/index/detail?id=${id}`);
    };
    
    // 切换聊天窗口显示状态
    const toggleChatWindow = () => {
      chatVisible.value = !chatVisible.value
      if (chatVisible.value) {
        unreadCount.value = 0
        scrollToBottom()
      }
    }
    
    // 最小化聊天窗口
    const minimizeChat = () => {
      chatVisible.value = false
    }
    
    // 关闭聊天窗口
    const closeChat = () => {
      chatVisible.value = false
      // 清空未读计数
      unreadCount.value = 0
      // 保留欢迎消息，清空其他消息
      messages.value = [
        {
          type: 'system',
          content: '您好！我是AI客服，有什么可以帮助您的吗？',
          timestamp: new Date()
        }
      ]
    }
    
    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || loading.value) return
      
      // 添加用户消息
      const userMessage = {
        type: 'user',
        content: inputMessage.value,
        timestamp: new Date()
      }
      messages.value.push(userMessage)
      
      // 清空输入框
      const userContent = inputMessage.value
      inputMessage.value = ''
      
      // 滚动到底部
      scrollToBottom()
      
      try {
        loading.value = true
        
        // 调用我们自己的后端AI客服接口
        // 修复调用方式，使用GET请求并正确传递参数
        const response = await axios.get('/api/ai-chat/message', {
          params: {
            message: userContent
          }
        })
        
        // 检查响应状态并添加AI回复
        // 根据实际返回的数据结构处理响应
        console.log('AI客服响应完整数据:', response);
        if (response && response.data && response.data.code === 200) {
          const aiMessage = {
            type: 'ai',
            content: response.data.data,
            timestamp: new Date()
          }
          messages.value.push(aiMessage)
        } else {
          // 如果没有按预期格式返回，尝试直接使用response作为内容
          const aiMessage = {
            type: 'ai',
            content: typeof response === 'string' ? response : JSON.stringify(response),
            timestamp: new Date()
          }
          messages.value.push(aiMessage)
        }
        
        // 如果聊天窗口未打开，增加未读计数
        if (!chatVisible.value) {
          unreadCount.value++
        }
      } catch (error) {
        // 错误处理
        console.error('AI客服调用失败:', error)
        const errorMessage = {
          type: 'system',
          content: '抱歉，AI客服暂时无法回复，请稍后再试。错误信息: ' + (error.message || JSON.stringify(error)),
          timestamp: new Date()
        }
        messages.value.push(errorMessage)
      } finally {
        loading.value = false
        scrollToBottom()
      }
    }
    
    // 格式化时间
    const formatTime = (timestamp) => {
      const date = new Date(timestamp)
      return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`
    }
    
    // 格式化内容，将\n替换为<br>
    const formatContent = (content) => {
      // 将 /detail?id=数字 格式的文本转换为可点击链接
      let formatted = content.replace(/\n/g, '<br>');
      formatted = formatted.replace(/\/detail\?id=(\d+)/g, '<a href="/index/detail?id=$1" target="_blank">查看商品详情</a>');
      return formatted;
    }

    // 滚动到底部
    const scrollToBottom = () => {
      nextTick(() => {
        if (messagesContainer.value) {
          messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
        }
      })
    }
    
    // 组件挂载时滚动到底部
    onMounted(() => {
      scrollToBottom()
    })
    
    return {
      chatVisible,
      inputMessage,
      messages,
      loading,
      unreadCount,
      messagesContainer,
      recommendedThings,
      goToDetail,
      toggleChatWindow,
      minimizeChat,
      closeChat,
      sendMessage,
      formatTime,
      formatContent
    }
  }
}
</script>

<style scoped lang="less">
.ai-chat-container {
  position: fixed;
  bottom: 20px;
  right: 20px;
  z-index: 1000;
}

.ai-chat-icon {
  width: 60px;
  height: 60px;
  background: #1890ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  transition: transform 0.3s;
  
  &:hover {
    transform: scale(1.1);
  }
  
  .icon {
    color: white;
  }
  
  .icon-badge {
    position: absolute;
    top: -5px;
    right: -5px;
    background: #ff4d4f;
    color: white;
    border-radius: 10px;
    padding: 0 5px;
    font-size: 12px;
    min-width: 20px;
    height: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
  }
}

.ai-chat-window {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 350px;
  height: 500px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.chat-header {
  height: 50px;
  background: #1890ff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  
  .header-title {
    font-size: 16px;
    font-weight: bold;
  }
  
  .header-actions {
    display: flex;
    gap: 10px;
    
    .minimize-btn,
    .close-btn {
      cursor: pointer;
      font-size: 18px;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      border-radius: 4px;
      
      &:hover {
        background: rgba(255, 255, 255, 0.2);
      }
    }
  }
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background: #f5f5f5;
  
  .message {
    margin-bottom: 15px;
    
    .message-content {
      padding: 10px 12px;
      border-radius: 6px;
      max-width: 80%;
      word-wrap: break-word;
      
      // 确保链接样式正确显示
      a {
        color: #1890ff;
        text-decoration: none;
        border: 1px solid #1890ff;
        padding: 2px 6px;
        border-radius: 4px;
        font-size: 12px;
        
        &:hover {
          text-decoration: underline;
          background-color: #f0f8ff;
        }
      }
    }
    
    .message-time {
      font-size: 12px;
      color: #999;
      margin-top: 4px;
    }
    
    &.user {
      text-align: right;
      
      .message-content {
        background: #1890ff;
        color: white;
        margin-left: auto;
      }
    }
    
    &.ai,
    &.system {
      text-align: left;
      
      .message-content {
        background: white;
        border: 1px solid #e8e8e8;
      }
    }
    
    &.system {
      .message-content {
        background: #fff7e6;
        border-color: #ffd591;
      }
    }
  }
  
  .recommend-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 15px;
    padding-top: 15px;
    border-top: 1px dashed #e8e8e8;
    max-width: 100%;
    
    .recommend-button {
      background: #1890ff;
      color: white;
      border-radius: 4px;
      padding: 8px 12px;
      font-size: 12px;
      cursor: pointer;
      transition: all 0.3s;
      border: 1px solid #1890ff;
      flex: 1 1 auto;
      min-width: 120px;
      text-align: center;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      
      &:hover {
        background: #40a9ff;
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
      }
    }
  }
}

.chat-input {
  display: flex;
  padding: 10px;
  border-top: 1px solid #e8e8e8;
  gap: 10px;
  
  :deep(.ant-input) {
    flex: 1;
  }
}
</style>