package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Classification;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
/**
 * @author Administrator
 * @email 123456@qq.com
 */
@RestController
@RequestMapping("/classification")
public class ClassificationController {

    private final static Logger logger = LoggerFactory.getLogger(ClassificationController.class);

    @Autowired
    ClassificationService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Classification> list =  service.getClassificationList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Classification classification) throws IOException {
        service.createClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteClassification(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Classification classification) throws IOException {
        service.updateClassification(classification);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    /**
     * 获取分类树形结构
     */
    @RequestMapping(value = "/tree", method = RequestMethod.GET)
    public APIResponse getClassificationTree() {
        try {
            List<Classification> tree = service.getClassificationTree();
            return new APIResponse(ResponeCode.SUCCESS, "获取分类树成功", tree);
        } catch (Exception e) {
            logger.error("获取分类树失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取分类树失败");
        }
    }

    /**
     * 获取分类详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse getClassificationDetail(String id) {
        try {
            Classification classification = service.getClassificationById(id);
            if (classification == null) {
                return new APIResponse(ResponeCode.FAIL, "分类不存在");
            }
            return new APIResponse(ResponeCode.SUCCESS, "获取分类详情成功", classification);
        } catch (Exception e) {
            logger.error("获取分类详情失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取分类详情失败");
        }
    }

    /**
     * 获取子分类列表
     */
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    public APIResponse getChildrenClassifications(String parentId) {
        try {
            List<Classification> children = service.getChildrenClassifications(parentId);
            return new APIResponse(ResponeCode.SUCCESS, "获取子分类成功", children);
        } catch (Exception e) {
            logger.error("获取子分类失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取子分类失败");
        }
    }

    /**
     * 获取分类统计信息
     */
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public APIResponse getClassificationStatistics(String id) {
        try {
            Map<String, Object> statistics = service.getClassificationStatistics(id);
            return new APIResponse(ResponeCode.SUCCESS, "获取分类统计成功", statistics);
        } catch (Exception e) {
            logger.error("获取分类统计失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取分类统计失败");
        }
    }

    /**
     * 移动分类（更改父分类）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/move", method = RequestMethod.POST)
    @Transactional
    public APIResponse moveClassification(String id, String newParentId) {
        try {
            boolean success = service.moveClassification(id, newParentId);
            if (success) {
                return new APIResponse(ResponeCode.SUCCESS, "分类移动成功");
            } else {
                return new APIResponse(ResponeCode.FAIL, "分类移动失败，可能存在循环引用");
            }
        } catch (Exception e) {
            logger.error("移动分类失败", e);
            return new APIResponse(ResponeCode.FAIL, "移动分类失败");
        }
    }

    /**
     * 批量更新分类排序
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/updateSort", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateClassificationSort(@RequestBody List<Map<String, Object>> sortData) {
        try {
            service.updateClassificationSort(sortData);
            return new APIResponse(ResponeCode.SUCCESS, "排序更新成功");
        } catch (Exception e) {
            logger.error("更新分类排序失败", e);
            return new APIResponse(ResponeCode.FAIL, "更新分类排序失败");
        }
    }

    /**
     * 获取热门分类（根据商品数量排序）
     */
    @RequestMapping(value = "/popular", method = RequestMethod.GET)
    public APIResponse getPopularClassifications(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<Classification> popularClassifications = service.getPopularClassifications(limit);
            return new APIResponse(ResponeCode.SUCCESS, "获取热门分类成功", popularClassifications);
        } catch (Exception e) {
            logger.error("获取热门分类失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取热门分类失败");
        }
    }

    /**
     * 搜索分类
     */
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public APIResponse searchClassifications(String keyword) {
        try {
            List<Classification> results = service.searchClassifications(keyword);
            return new APIResponse(ResponeCode.SUCCESS, "搜索分类成功", results);
        } catch (Exception e) {
            logger.error("搜索分类失败", e);
            return new APIResponse(ResponeCode.FAIL, "搜索分类失败");
        }
    }

}
