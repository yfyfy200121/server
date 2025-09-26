package com.gk.study.service;


import com.gk.study.entity.Classification;

import java.util.List;
import java.util.Map;

public interface ClassificationService {
    List<Classification> getClassificationList();
    void createClassification(Classification Classification);
    void deleteClassification(String id);
    void updateClassification(Classification Classification);
    
    /**
     * 根据ID获取分类
     */
    Classification getClassificationById(String id);
    
    /**
     * 获取分类树形结构
     */
    List<Classification> getClassificationTree();
    
    /**
     * 获取子分类列表
     */
    List<Classification> getChildrenClassifications(String parentId);
    
    /**
     * 获取分类统计信息
     */
    Map<String, Object> getClassificationStatistics(String id);
    
    /**
     * 移动分类（更改父分类）
     */
    boolean moveClassification(String id, String newParentId);
    
    /**
     * 批量更新分类排序
     */
    void updateClassificationSort(List<Map<String, Object>> sortData);
    
    /**
     * 获取热门分类
     */
    List<Classification> getPopularClassifications(int limit);
    
    /**
     * 搜索分类
     */
    List<Classification> searchClassifications(String keyword);
}
