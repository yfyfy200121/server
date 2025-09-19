<template>
  <div class="complaint-view">
    <div class="tab-menu-view">
      <div class="tab-item active">
        我的投诉
      </div>
    </div>
    
    <div class="content-view">
      <div class="create-complaint-btn">
        <a-button type="primary" @click="handleCreateComplaint">
          <plus-outlined />
          提交新投诉
        </a-button>
      </div>
      
      <div class="complaint-list">
        <div v-if="dataList.length === 0" class="no-data">
          <a-empty description="暂无投诉记录" />
        </div>
        
        <div v-for="item in dataList" :key="item.id" class="complaint-item">
          <div class="complaint-header">
            <h3>{{ item.title }}</h3>
            <a-tag :color="getStatusColor(item.status)">
              {{ getStatusText(item.status) }}
            </a-tag>
          </div>
          
          <div class="complaint-content">
            <p><strong>投诉类型：</strong>{{ getTypeText(item.type) }}</p>
            <p><strong>投诉内容：</strong>{{ item.content }}</p>
            <p><strong>提交时间：</strong>{{ formatTime(item.createTime) }}</p>
            <p v-if="item.adminReply"><strong>管理员回复：</strong>{{ item.adminReply }}</p>
          </div>
          
          <div class="complaint-actions">
            <a-button size="small" @click="viewDetail(item)">查看详情</a-button>
            <a-button v-if="item.status === '0'" size="small" type="primary" @click="editComplaint(item)">
              编辑
            </a-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建/编辑投诉弹窗 -->
    <a-modal
      v-model:visible="modal.visible"
      :title="modal.editFlag ? '编辑投诉' : '提交投诉'"
      width="600px"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <a-form ref="myform" :model="modal.form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="投诉标题" name="title" :rules="[{ required: true, message: '请输入投诉标题' }]">
          <a-input v-model:value="modal.form.title" placeholder="请简洁描述您的问题" />
        </a-form-item>
        
        <a-form-item label="投诉类型" name="type" :rules="[{ required: true, message: '请选择投诉类型' }]">
          <a-select v-model:value="modal.form.type" placeholder="请选择投诉类型">
            <a-select-option value="1">商品问题</a-select-option>
            <a-select-option value="2">服务问题</a-select-option>
            <a-select-option value="3">订单问题</a-select-option>
            <a-select-option value="4">其他</a-select-option>
          </a-select>
        </a-form-item>
        
        <a-form-item label="相关订单" name="orderId">
          <a-input v-model:value="modal.form.orderId" placeholder="如与订单相关，请输入订单ID（可选）" />
        </a-form-item>
        
        <a-form-item label="相关商品" name="thingId">
          <a-input v-model:value="modal.form.thingId" placeholder="如与商品相关，请输入商品ID（可选）" />
        </a-form-item>
        
        <a-form-item label="投诉内容" name="content" :rules="[{ required: true, message: '请详细描述您的问题' }]">
          <a-textarea
            v-model:value="modal.form.content"
            placeholder="请详细描述您遇到的问题，以便我们更好地为您解决"
            :rows="4"
          />
        </a-form-item>
        
        <a-form-item label="优先级" name="priority">
          <a-select v-model:value="modal.form.priority" placeholder="请选择优先级">
            <a-select-option value="1">低</a-select-option>
            <a-select-option value="2">中</a-select-option>
            <a-select-option value="3">高</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- 详情弹窗 -->
    <a-modal
      v-model:visible="detailModal.visible"
      title="投诉详情"
      width="700px"
      :footer="null"
    >
      <div v-if="detailModal.data" class="complaint-detail">
        <a-descriptions :column="2" bordered>
          <a-descriptions-item label="投诉标题">{{ detailModal.data.title }}</a-descriptions-item>
          <a-descriptions-item label="状态">
            <a-tag :color="getStatusColor(detailModal.data.status)">
              {{ getStatusText(detailModal.data.status) }}
            </a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="投诉类型">{{ getTypeText(detailModal.data.type) }}</a-descriptions-item>
          <a-descriptions-item label="优先级">{{ getPriorityText(detailModal.data.priority) }}</a-descriptions-item>
          <a-descriptions-item label="提交时间">{{ formatTime(detailModal.data.createTime) }}</a-descriptions-item>
          <a-descriptions-item label="更新时间">{{ formatTime(detailModal.data.updateTime) }}</a-descriptions-item>
          <a-descriptions-item label="投诉内容" :span="2">{{ detailModal.data.content }}</a-descriptions-item>
          <a-descriptions-item v-if="detailModal.data.adminReply" label="管理员回复" :span="2">
            {{ detailModal.data.adminReply }}
          </a-descriptions-item>
        </a-descriptions>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import { message } from 'ant-design-vue';
import { PlusOutlined } from '@ant-design/icons-vue';
import { useRouter } from 'vue-router';
import { listUserComplaintsApi, createApi, updateApi } from '/@/api/complaint';
import { useUserStore } from '/@/store';

const userStore = useUserStore();
const router = useRouter();

const dataList = ref([]);
const myform = ref();

const modal = reactive({
  visible: false,
  editFlag: false,
  form: {
    id: '',
    title: '',
    content: '',
    type: '',
    orderId: '',
    thingId: '',
    priority: '2', // 默认中等优先级
    userId: userStore.user_id
  }
});

const detailModal = reactive({
  visible: false,
  data: null
});

const getDataList = () => {
  listUserComplaintsApi({ userId: userStore.user_id })
    .then((res) => {
      dataList.value = res.data;
    })
    .catch((err) => {
      console.log(err);
    });
};

const handleCreateComplaint = () => {
  resetModal();
  modal.visible = true;
  modal.editFlag = false;
};

const editComplaint = (item: any) => {
  resetModal();
  modal.visible = true;
  modal.editFlag = true;
  Object.assign(modal.form, item);
};

const viewDetail = (item: any) => {
  detailModal.data = item;
  detailModal.visible = true;
};

const handleOk = () => {
  myform.value
    ?.validate()
    .then(() => {
      const formData = new FormData();
      Object.keys(modal.form).forEach(key => {
        if (modal.form[key] !== null && modal.form[key] !== undefined && modal.form[key] !== '') {
          formData.append(key, modal.form[key]);
        }
      });
      
      if (modal.editFlag) {
        updateApi(formData)
          .then((res) => {
            hideModal();
            getDataList();
            message.success('投诉更新成功');
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      } else {
        createApi(formData)
          .then((res) => {
            hideModal();
            getDataList();
            message.success('投诉提交成功，我们会尽快处理');
          })
          .catch((err) => {
            console.log(err);
            message.error(err.msg || '操作失败');
          });
      }
    })
    .catch((err) => {
      console.log('表单验证失败', err);
    });
};

const handleCancel = () => {
  hideModal();
};

const resetModal = () => {
  myform.value?.resetFields();
  modal.form = {
    id: '',
    title: '',
    content: '',
    type: '',
    orderId: '',
    thingId: '',
    priority: '2',
    userId: userStore.user_id
  };
};

const hideModal = () => {
  modal.visible = false;
};

const getStatusText = (status: string) => {
  const statusMap = {
    '0': '待处理',
    '1': '处理中',
    '2': '已解决',
    '3': '已关闭'
  };
  return statusMap[status] || '未知';
};

const getStatusColor = (status: string) => {
  const colorMap = {
    '0': 'orange',
    '1': 'blue',
    '2': 'green',
    '3': 'gray'
  };
  return colorMap[status] || 'default';
};

const getTypeText = (type: string) => {
  const typeMap = {
    '1': '商品问题',
    '2': '服务问题',
    '3': '订单问题',
    '4': '其他'
  };
  return typeMap[type] || '未知';
};

const getPriorityText = (priority: string) => {
  const priorityMap = {
    '1': '低',
    '2': '中',
    '3': '高'
  };
  return priorityMap[priority] || '中';
};

const formatTime = (timeStr: string) => {
  if (!timeStr) return '';
  const date = new Date(parseInt(timeStr));
  return date.toLocaleString();
};

onMounted(() => {
  getDataList();
});
</script>

<style scoped lang="less">
.complaint-view {
  .tab-menu-view {
    border-bottom: 1px solid #e8e8e8;
    margin-bottom: 16px;
    
    .tab-item {
      display: inline-block;
      padding: 12px 24px;
      font-size: 16px;
      font-weight: 500;
      border-bottom: 2px solid transparent;
      
      &.active {
        color: #1890ff;
        border-bottom-color: #1890ff;
      }
    }
  }

  .content-view {
    .create-complaint-btn {
      margin-bottom: 16px;
    }

    .complaint-list {
      .no-data {
        text-align: center;
        padding: 40px 0;
      }

      .complaint-item {
        border: 1px solid #e8e8e8;
        border-radius: 8px;
        padding: 16px;
        margin-bottom: 16px;
        background: #fff;

        .complaint-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 12px;

          h3 {
            margin: 0;
            font-size: 16px;
            font-weight: 500;
          }
        }

        .complaint-content {
          margin-bottom: 12px;

          p {
            margin: 8px 0;
            color: #666;
            line-height: 1.5;
          }
        }

        .complaint-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }

  .complaint-detail {
    .ant-descriptions {
      margin-top: 16px;
    }
  }
}
</style>