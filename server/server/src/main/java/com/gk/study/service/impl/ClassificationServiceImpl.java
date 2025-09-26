package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.mapper.ClassificationMapper;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.entity.Classification;
import com.gk.study.entity.Thing;
import com.gk.study.service.ClassificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClassificationServiceImpl extends ServiceImpl<ClassificationMapper, Classification> implements ClassificationService {

    @Autowired
    ClassificationMapper mapper;
    
    @Autowired
    ThingMapper thingMapper;

    @Override
    public List<Classification> getClassificationList() {
        return mapper.selectList(new QueryWrapper<>());
    }

    @Override
    public void createClassification(Classification classification) {
        System.out.println(classification);
        classification.setCreateTime(String.valueOf(System.currentTimeMillis()));
        mapper.insert(classification);
    }

    @Override
    public void deleteClassification(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateClassification(Classification classification) {
        mapper.updateById(classification);
    }
    
    @Override
    public Classification getClassificationById(String id) {
        Classification classification = mapper.selectById(id);
        if (classification != null) {
            // 获取父分类名称
            if (classification.getPid() != null) {
                Classification parent = mapper.selectById(classification.getPid());
                if (parent != null) {
                    classification.setParentTitle(parent.getTitle());
                }
            }
            
            // 获取该分类下的商品数量
            QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
            thingQuery.eq("classification_id", id);
            Long thingCount = thingMapper.selectCount(thingQuery);
            classification.setThingCount(thingCount.intValue());
        }
        return classification;
    }
    
    @Override
    public List<Classification> getClassificationTree() {
        List<Classification> allClassifications = getClassificationList();
        
        // 构建层级关系
        Map<Long, Classification> classificationMap = new HashMap<>();
        List<Classification> rootClassifications = new ArrayList<>();
        
        // 第一遍遍历：建立映射关系
        for (Classification classification : allClassifications) {
            classificationMap.put(classification.getId(), classification);
            classification.setChildren(new ArrayList<>());
        }
        
        // 第二遍遍历：构建树形结构
        for (Classification classification : allClassifications) {
            if (classification.getPid() == null || classification.getPid() == 0) {
                // 根节点
                classification.setLevel(1);
                rootClassifications.add(classification);
            } else {
                // 子节点
                Classification parent = classificationMap.get(classification.getPid());
                if (parent != null) {
                    classification.setLevel(parent.getLevel() + 1);
                    parent.getChildren().add(classification);
                }
            }
        }
        
        // 计算每个分类的商品数量
        calculateThingCount(rootClassifications);
        
        return rootClassifications;
    }
    
    @Override
    public List<Classification> getChildrenClassifications(String parentId) {
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        if (parentId == null || "0".equals(parentId)) {
            queryWrapper.isNull("pid").or().eq("pid", 0);
        } else {
            queryWrapper.eq("pid", parentId);
        }
        queryWrapper.orderByAsc("id");
        
        List<Classification> children = mapper.selectList(queryWrapper);
        
        // 为每个子分类计算商品数量
        for (Classification child : children) {
            QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
            thingQuery.eq("classification_id", child.getId());
            Long thingCount = thingMapper.selectCount(thingQuery);
            child.setThingCount(thingCount.intValue());
            
            // 检查是否有子分类
            QueryWrapper<Classification> childQuery = new QueryWrapper<>();
            childQuery.eq("pid", child.getId());
            Long childCount = mapper.selectCount(childQuery);
            child.setIsLeaf(childCount == 0);
        }
        
        return children;
    }
    
    @Override
    public Map<String, Object> getClassificationStatistics(String id) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            Classification classification = getClassificationById(id);
            if (classification == null) {
                return statistics;
            }
            
            // 基本信息
            statistics.put("id", classification.getId());
            statistics.put("title", classification.getTitle());
            statistics.put("parentTitle", classification.getParentTitle());
            statistics.put("level", classification.getLevel());
            
            // 商品统计
            QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
            thingQuery.eq("classification_id", id);
            Long directThingCount = thingMapper.selectCount(thingQuery);
            statistics.put("directThingCount", directThingCount.intValue());
            
            // 递归统计所有子分类的商品数量
            int totalThingCount = calculateTotalThingCount(id);
            statistics.put("totalThingCount", totalThingCount);
            
            // 子分类统计
            QueryWrapper<Classification> childQuery = new QueryWrapper<>();
            childQuery.eq("pid", id);
            Long childCount = mapper.selectCount(childQuery);
            statistics.put("childCount", childCount.intValue());
            
            // 平均价格统计
            List<Thing> things = thingMapper.selectList(thingQuery);
            if (!things.isEmpty()) {
                double avgPrice = things.stream()
                    .filter(thing -> thing.getPrice() != null)
                    .mapToDouble(thing -> Double.parseDouble(thing.getPrice()))
                    .average()
                    .orElse(0.0);
                statistics.put("averagePrice", Math.round(avgPrice * 100.0) / 100.0);
            } else {
                statistics.put("averagePrice", 0.0);
            }
            
        } catch (Exception e) {
            statistics.put("error", "统计数据获取失败");
        }
        
        return statistics;
    }
    
    @Override
    public boolean moveClassification(String id, String newParentId) {
        try {
            // 检查是否会造成循环引用
            if (wouldCreateCycle(id, newParentId)) {
                return false;
            }
            
            Classification classification = mapper.selectById(id);
            if (classification != null) {
                Long newPid = (newParentId == null || "0".equals(newParentId)) ? null : Long.parseLong(newParentId);
                classification.setPid(newPid);
                mapper.updateById(classification);
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public void updateClassificationSort(List<Map<String, Object>> sortData) {
        for (Map<String, Object> item : sortData) {
            String id = String.valueOf(item.get("id"));
            Integer sortOrder = (Integer) item.get("sortOrder");
            
            Classification classification = mapper.selectById(id);
            if (classification != null) {
                classification.setSortOrder(sortOrder);
                mapper.updateById(classification);
            }
        }
    }
    
    @Override
    public List<Classification> getPopularClassifications(int limit) {
        List<Classification> allClassifications = getClassificationList();
        
        // 计算每个分类的商品数量
        for (Classification classification : allClassifications) {
            QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
            thingQuery.eq("classification_id", classification.getId());
            Long thingCount = thingMapper.selectCount(thingQuery);
            classification.setThingCount(thingCount.intValue());
        }
        
        // 按商品数量排序并限制结果数量
        return allClassifications.stream()
            .sorted((a, b) -> Integer.compare(b.getThingCount(), a.getThingCount()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Classification> searchClassifications(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        QueryWrapper<Classification> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("title", keyword.trim());
        queryWrapper.orderByAsc("id");
        
        List<Classification> results = mapper.selectList(queryWrapper);
        
        // 为每个结果计算商品数量
        for (Classification classification : results) {
            QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
            thingQuery.eq("classification_id", classification.getId());
            Long thingCount = thingMapper.selectCount(thingQuery);
            classification.setThingCount(thingCount.intValue());
        }
        
        return results;
    }
    
    // 辅助方法
    
    /**
     * 递归计算分类树的商品数量
     */
    private void calculateThingCount(List<Classification> classifications) {
        for (Classification classification : classifications) {
            int totalCount = calculateTotalThingCount(String.valueOf(classification.getId()));
            classification.setThingCount(totalCount);
            
            if (classification.getChildren() != null && !classification.getChildren().isEmpty()) {
                calculateThingCount(classification.getChildren());
            }
        }
    }
    
    /**
     * 递归计算分类及其所有子分类的商品总数
     */
    private int calculateTotalThingCount(String classificationId) {
        // 当前分类的商品数量
        QueryWrapper<Thing> thingQuery = new QueryWrapper<>();
        thingQuery.eq("classification_id", classificationId);
        int directCount = thingMapper.selectCount(thingQuery).intValue();
        
        // 子分类的商品数量
        QueryWrapper<Classification> childQuery = new QueryWrapper<>();
        childQuery.eq("pid", classificationId);
        List<Classification> children = mapper.selectList(childQuery);
        
        int childCount = 0;
        for (Classification child : children) {
            childCount += calculateTotalThingCount(String.valueOf(child.getId()));
        }
        
        return directCount + childCount;
    }
    
    /**
     * 检查移动分类是否会造成循环引用
     */
    private boolean wouldCreateCycle(String id, String newParentId) {
        if (newParentId == null || "0".equals(newParentId)) {
            return false; // 移动到根节点不会造成循环
        }
        
        if (id.equals(newParentId)) {
            return true; // 不能将分类移动到自己下面
        }
        
        // 检查新父节点是否是当前节点的后代
        return isDescendant(id, newParentId);
    }
    
    /**
     * 检查targetId是否是sourceId的后代
     */
    private boolean isDescendant(String sourceId, String targetId) {
        Classification target = mapper.selectById(targetId);
        if (target == null || target.getPid() == null) {
            return false;
        }
        
        if (sourceId.equals(String.valueOf(target.getPid()))) {
            return true;
        }
        
        return isDescendant(sourceId, String.valueOf(target.getPid()));
    }
}
