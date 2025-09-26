package com.gk.study.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

@Data
@TableName("b_classification")
public class Classification implements Serializable {
    @TableId(value = "id",type = IdType.AUTO)
    public Long id;
    
    @TableField
    public String title; // 分类名称
    
    @TableField
    public String createTime; // 创建时间
    
    @TableField
    public Long pid; // 父分类ID
    
    @TableField(exist = false)
    public String parentTitle; // 父分类名称
    
    @TableField(exist = false)
    public List<Classification> children; // 子分类列表
    
    @TableField(exist = false)
    public Integer level; // 分类层级
    
    @TableField(exist = false)
    public Integer thingCount; // 该分类下的商品数量
    
    @TableField(exist = false)
    public Integer sortOrder; // 排序顺序
    
    @TableField(exist = false)
    public String description; // 分类描述
    
    @TableField(exist = false)
    public String icon; // 分类图标
    
    @TableField(exist = false)
    public Boolean isLeaf; // 是否叶子节点

}
