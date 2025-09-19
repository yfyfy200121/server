<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-select
            v-model:value="data.statusFilter"
            placeholder="按状态筛选"
            style="width: 120px"
            @change="handleStatusFilter"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="0">待处理</a-select-option>
            <a-select-option value="1">处理中</a-select-option>
            <a-select-option value="2">已解决</a-select-option>
            <a-select-option value="3">已关闭</a-select-option>
          </a-select>
          <a-select
            v-model:value="data.typeFilter"
            placeholder="按类型筛选"
            style="width: 120px"
            @change="handleTypeFilter"
          >
            <a-select-option value="">全部</a-select-option>
            <a-select-option value="1">商品问题</a-select-option>
            <a-select-option value="2">服务问题</a-select-option>
            <a-select-option value="3">订单问题</a-select-option>
            <a-select-option value="4">其他</a-select-option>
          </a-select>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.list"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }"
      >
        <template #bodyCell="{ text, record, index, column }">
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(record.status)">
              {{ getStatusText(record.status) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'priority'">
            <a-tag :color="getPriorityColor(record.priority)">
              {{ getPriorityText(record.priority) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleReply(record)">回复</a>
              <a-divider type="vertical"/>
              <a @click="handleChangeStatus(record)">更改状态</a>
              <a-divider type="vertical"/>
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 回复弹窗 -->
    <a-modal
      v-model:visible="replyModal.visible"
      title="回复投诉"
      width="600px"
      @ok="handleReplyOk"
      @cancel="handleReplyCancel"
    >
      <div class="reply-content">
        <div class="complaint-info">
          <h3>{{ replyModal.complaint?.title }}</h3>
          <p><strong>投诉内容：</strong>{{ replyModal.complaint?.content }}</p>
          <p><strong>投诉用户：</strong>{{ replyModal.complaint?.username }}</p>
        </div>
        
        <a-form ref="replyForm" :model="replyModal.form" :label-col="{ span: 4 }" :wrapper-col="{ span: 20 }">
          <a-form-item label="回复内容" name="adminReply" :rules="[{ required: true, message: '请输入回复内容' }]">
            <a-textarea
              v-model:value="replyModal.form.adminReply"
              placeholder="请输入回复内容"
              :rows="4"
            />
          </a-form-item>
        </a-form>
      </div>
    </a-modal>

    <!-- 更改状态弹窗 -->
    <a-modal
      v-model:visible="statusModal.visible"
      title="更改状态"
      width="400px"
      @ok="handleStatusOk"
      @cancel="handleStatusCancel"
    >
      <a-form ref="statusForm" :model="statusModal.form" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="状态" name="status" :rules="[{ required: true, message: '请选择状态' }]">
          <a-select v-model:value="statusModal.form.status" placeholder="请选择状态">
            <a-select-option value="0">待处理</a-select-option>
            <a-select-option value="1">处理中</a-select-option>
            <a-select-option value="2">已解决</a-select-option>
            <a-select-option value="3">已关闭</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue';
import {FormInstance, message} from 'ant-design-vue';
import {listApi, deleteApi, replyApi, updateApi} from '/@/api/complaint';
import {getFormatTime} from "/@/utils";
import {useUserStore} from '/@/store';

const userStore = useUserStore();
const replyForm = ref();
const statusForm = ref();

const columns = reactive([
  {
    title: '序号',
    dataIndex: 'index',
    key: 'index',
    align: 'center',
    width: 60
  },
  {
    title: '投诉标题',
    dataIndex: 'title',
    key: 'title',
    align: 'center',
    width: 150
  },
  {
    title: '用户',
    dataIndex: 'username',
    key: 'username',
    align: 'center',
    width: 100
  },
  {
    title: '类型',
    dataIndex: 'type',
    key: 'type',
    align: 'center',
    width: 100,
    customRender: ({text}) => getTypeText(text)
  },
  {
    title: '状态',
    dataIndex: 'status',
    key: 'status',
    align: 'center',
    width: 100
  },
  {
    title: '优先级',
    dataIndex: 'priority',
    key: 'priority',
    align: 'center',
    width: 80
  },
  {
    title: '投诉内容',
    dataIndex: 'content',
    key: 'content',
    align: 'center',
    width: 200,
    customRender: ({text}) => text && text.length > 30 ? text.substring(0, 30) + '...' : text
  },
  {
    title: '提交时间',
    dataIndex: 'createTime',
    key: 'createTime',
    align: 'center',
    width: 150,
    customRender: ({text}) => getFormatTime(text, true)
  },
  {
    title: '管理员回复',
    dataIndex: 'adminReply',
    key: 'adminReply',
    align: 'center',
    width: 150,
    customRender: ({text}) => {
      if (!text) return '未回复';
      return text.length > 20 ? text.substring(0, 20) + '...' : text;
    }
  },
  {
    title: '操作',
    dataIndex: 'action',
    key: 'operation',
    align: 'center',
    fixed: 'right',
    width: 180,
  },
]);

// 页面数据
const data = reactive({
  list: [],
  loading: false,
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
  statusFilter: '',
  typeFilter: ''
});

// 回复弹窗数据
const replyModal = reactive({
  visible: false,
  complaint: null,
  form: {
    adminReply: ''
  }
});

// 状态弹窗数据
const statusModal = reactive({
  visible: false,
  complaint: null,
  form: {
    status: ''
  }
});

onMounted(() => {
  getList();
});

const getList = () => {
  data.loading = true;
  listApi({})
      .then((res) => {
        data.loading = false;
        console.log(res);
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.list = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
};

const handleStatusFilter = () => {
  // 这里可以实现按状态筛选的逻辑
  getList();
};

const handleTypeFilter = () => {
  // 这里可以实现按类型筛选的逻辑
  getList();
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[], selectedRows: any[]) => {
    console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
    data.selectedRowKeys = selectedRowKeys;
  },
});

const confirmDelete = (record: any) => {
  console.log('delete', record);
  deleteApi({ids: record.id})
      .then((res) => {
        message.success('删除成功');
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
};

const handleBatchDelete = () => {
  console.log(data.selectedRowKeys);
  if (data.selectedRowKeys.length <= 0) {
    message.warn('请勾选删除项');
    return;
  }
  deleteApi({ids: data.selectedRowKeys.join(',')})
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
};

const handleReply = (record: any) => {
  replyModal.complaint = record;
  replyModal.visible = true;
  replyModal.form.adminReply = record.adminReply || '';
};

const handleReplyOk = () => {
  replyForm.value
    ?.validate()
    .then(() => {
      const formData = new FormData();
      formData.append('id', replyModal.complaint.id);
      formData.append('adminReply', replyModal.form.adminReply);
      formData.append('adminId', userStore.user_id);
      
      replyApi(formData)
        .then((res) => {
          message.success('回复成功');
          replyModal.visible = false;
          getList();
        })
        .catch((err) => {
          message.error(err.msg || '操作失败');
        });
    })
    .catch((err) => {
      console.log('表单验证失败', err);
    });
};

const handleReplyCancel = () => {
  replyModal.visible = false;
  replyModal.form.adminReply = '';
};

const handleChangeStatus = (record: any) => {
  statusModal.complaint = record;
  statusModal.visible = true;
  statusModal.form.status = record.status;
};

const handleStatusOk = () => {
  statusForm.value
    ?.validate()
    .then(() => {
      const formData = new FormData();
      formData.append('id', statusModal.complaint.id);
      formData.append('status', statusModal.form.status);
      
      updateApi(formData)
        .then((res) => {
          message.success('状态更新成功');
          statusModal.visible = false;
          getList();
        })
        .catch((err) => {
          message.error(err.msg || '操作失败');
        });
    })
    .catch((err) => {
      console.log('表单验证失败', err);
    });
};

const handleStatusCancel = () => {
  statusModal.visible = false;
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

const getPriorityColor = (priority: string) => {
  const colorMap = {
    '1': 'green',
    '2': 'orange',
    '3': 'red'
  };
  return colorMap[priority] || 'orange';
};
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}

.table-operations {
  margin-bottom: 16px;
  text-align: right;
}

.table-operations > button {
  margin-right: 8px;
}

.reply-content {
  .complaint-info {
    background: #f5f5f5;
    padding: 16px;
    border-radius: 4px;
    margin-bottom: 16px;
    
    h3 {
      margin: 0 0 8px 0;
      color: #333;
    }
    
    p {
      margin: 4px 0;
      color: #666;
    }
  }
}
</style>