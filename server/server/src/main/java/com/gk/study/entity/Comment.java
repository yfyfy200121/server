package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@TableName("b_comment")
public class Comment implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    @TableField
    public String content;
    @TableField
    public String commentTime;
    @TableField
    public String likeCount;
    @TableField
    public String userId;
    @TableField(exist = false)
    public String username; // 用户名
    @TableField
    public String thingId;
    @TableField(exist = false)
    public String title; // 商品名称
    @TableField(exist = false)
    public String cover; // 商品封面
    
    @TableField(exist = false)
    public Integer score; // 评论评分
    
    @TableField(exist = false)
    public List<String> keywords; // 评论中提取的关键特征词
}
