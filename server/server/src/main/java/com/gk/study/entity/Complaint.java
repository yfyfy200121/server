package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("b_complaint")
public class Complaint implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String title; // 投诉标题
    
    @TableField
    public String content; // 投诉内容
    
    @TableField
    public String type; // 投诉类型：1-商品问题，2-服务问题，3-订单问题，4-其他
    
    @TableField
    public String status; // 处理状态：0-待处理，1-处理中，2-已解决，3-已关闭
    
    @TableField
    public String createTime; // 提交时间
    
    @TableField
    public String updateTime; // 更新时间
    
    @TableField
    public String userId; // 投诉用户ID
    
    @TableField
    public String orderId; // 相关订单ID（可选）
    
    @TableField
    public String thingId; // 相关商品ID（可选）
    
    @TableField
    public String adminReply; // 管理员回复
    
    @TableField
    public String adminId; // 处理管理员ID
    
    @TableField
    public String priority; // 优先级：1-低，2-中，3-高
    
    // 非数据库字段，用于关联查询
    @TableField(exist = false)
    public String username; // 用户名
    
    @TableField(exist = false)
    public String thingTitle; // 商品名称
    
    @TableField(exist = false)
    public String orderNumber; // 订单编号
    
    @TableField(exist = false)
    public String adminName; // 管理员姓名
}