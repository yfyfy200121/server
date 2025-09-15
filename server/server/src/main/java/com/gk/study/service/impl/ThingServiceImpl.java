package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Comment;
import com.gk.study.entity.Thing;
import com.gk.study.entity.ThingTag;
import com.gk.study.mapper.ThingMapper;
import com.gk.study.mapper.ThingTagMapper;
import com.gk.study.service.CommentService;
import com.gk.study.service.ThingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ThingServiceImpl extends ServiceImpl<ThingMapper, Thing> implements ThingService {
    @Autowired
    ThingMapper mapper;

    @Autowired
    ThingTagMapper thingTagMapper;

    @Autowired
    private CommentService commentService;

    // 积极评价关键词库
    private static final List<String> POSITIVE_KEYWORDS = Arrays.asList(
        "质量好", "物流快", "性价比高", "满意", "推荐", "不错",
        "很好", "喜欢", "正品", "包装好", "服务好",
        "速度快", "物美价廉", "超出预期", "完美","内容详尽", "讲解清晰", "条理清楚", "图文并茂", "案例实用",
            "习题丰富", "答案详解", "适合自学", "适合考试", "学术权威",
            "翻译流畅", "印刷精美", "装订结实", "排版清晰", "纸张良好",
            "易懂", "深入浅出", "实用性强", "物有所值", "推荐购买",
            "附带电子资源", "配套代码", "更新及时", "适合入门", "适合进阶"
    );

    // 消极评价关键词库
    private static final List<String> NEGATIVE_KEYWORDS = Arrays.asList(
        "质量差", "物流慢", "性价比低", "失望", "不推荐",
        "很差", "不喜欢", "假货", "包装差", "服务差",
        "速度慢", "物不美价不廉", "低于预期", "糟糕", "内容空洞", "讲解晦涩", "结构混乱", "例子少", "习题少",
            "答案错误", "缺少答案", "不适合自学", "陈旧过时", "事实错误",
            "术语混乱", "翻译生硬", "印刷差", "装订松散", "排版混乱",
            "错别字多", "缺页", "索引缺失", "电子资源失效", "缺少代码实现",
            "价格偏高", "不推荐购买", "实用性差", "误导性强", "不值这个价"
    );

    @Override
    public List<Thing> getThingList(String keyword, String sort, String c, String tag) {
        QueryWrapper<Thing> queryWrapper = new QueryWrapper<>();

        // 搜索
        queryWrapper.like(StringUtils.isNotBlank(keyword), "title", keyword);

        // 排序
        if (StringUtils.isNotBlank(sort)) {
            if (sort.equals("recent")) {
                queryWrapper.orderBy(true, false, "create_time");
            } else if (sort.equals("hot") || sort.equals("recommend")) {
                queryWrapper.orderBy(true, false, "pv");
            }
        }else {
            queryWrapper.orderBy(true, false, "create_time");
        }

        // 根据分类筛选
        if (StringUtils.isNotBlank(c) && !c.equals("-1")) {
            queryWrapper.eq(true, "classification_id", c);
        }

        List<Thing> things = mapper.selectList(queryWrapper);

        // tag筛选
        if (StringUtils.isNotBlank(tag)) {
            List<Thing> tThings = new ArrayList<>();
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.eq("tag_id", tag);
            List<ThingTag> thingTagList = thingTagMapper.selectList(thingTagQueryWrapper);
            for (Thing thing : things) {
                for (ThingTag thingTag : thingTagList) {
                    if (thing.getId().equals(thingTag.getThingId())) {
                        tThings.add(thing);
                    }
                }
            }
            things.clear();
            things.addAll(tThings);
        }

        // 附加tag
        for (Thing thing : things) {
            QueryWrapper<ThingTag> thingTagQueryWrapper = new QueryWrapper<>();
            thingTagQueryWrapper.lambda().eq(ThingTag::getThingId, thing.getId());
            List<ThingTag> thingTags = thingTagMapper.selectList(thingTagQueryWrapper);
            List<Long> tags = thingTags.stream().map(ThingTag::getTagId).collect(Collectors.toList());
            thing.setTags(tags);
        }
        return things;
    }

    @Override
    public List<Thing> getThingListWithComments(String keyword, String sort, String c, String tag) {
        List<Thing> things = getThingList(keyword, sort, c, tag);
        
        for (Thing thing : things) {
            // 获取该商品的最新5条评论
            List<Comment> comments = commentService.getThingCommentList(String.valueOf(thing.getId()), "recent");
            if (comments != null && !comments.isEmpty()) {
                // 只保留必要的评论信息
                List<Comment> simplifiedComments = new ArrayList<>();
                for (Comment comment : comments) {
                    Comment simpleComment = new Comment();
                    simpleComment.setContent(comment.getContent());
                    // 从content中提取评分
                    try {
                        int scoreStart = comment.getContent().indexOf("评分：");
                        if (scoreStart >= 0) {
                            int scoreEnd = comment.getContent().indexOf("分", scoreStart);
                            if (scoreEnd > scoreStart) {
                                String scoreStr = comment.getContent().substring(scoreStart + 3, scoreEnd).trim();
                                simpleComment.setScore(Integer.parseInt(scoreStr));
                            } else {
                                simpleComment.setScore(5); // 默认高分
                            }
                        } else {
                            simpleComment.setScore(5); // 默认高分
                        }
                    } catch (Exception e) {
                        simpleComment.setScore(5); // 解析失败则默认高分
                    }
                    
                    // 提取关键词
                    extractKeywords(simpleComment);
                    
                    simplifiedComments.add(simpleComment);
                }
                thing.setComments(simplifiedComments);
            }
        }
        return things;
    }
    
    /**
     * 从评论内容中提取关键词
     */
    private void extractKeywords(Comment comment) {
        String content = comment.getContent();
        List<String> keywords = new ArrayList<>();
        
        // 检查积极关键词
        for (String keyword : POSITIVE_KEYWORDS) {
            if (content.contains(keyword)) {
                keywords.add(keyword);
            }
        }
        
        // 检查消极关键词
        for (String keyword : NEGATIVE_KEYWORDS) {
            if (content.contains(keyword)) {
                keywords.add("【注意】" + keyword);
            }
        }
        
        if (!keywords.isEmpty()) {
            comment.setKeywords(keywords);
        }
    }

    @Override
    public void createThing(Thing thing) {
        System.out.println(thing);
        thing.setCreateTime(String.valueOf(System.currentTimeMillis()));

        if (thing.getPv() == null) {
            thing.setPv("0");
        }
        if (thing.getScore() == null) {
            thing.setScore("0");
        }
        if (thing.getWishCount() == null) {
            thing.setWishCount("0");
        }
        mapper.insert(thing);
        // 更新tag
        setThingTags(thing);
    }

    @Override
    public void deleteThing(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateThing(Thing thing) {

        // 更新tag
        setThingTags(thing);

        mapper.updateById(thing);
    }

    @Override
    public Thing getThingById(String id) {
        return mapper.selectById(id);
    }

    // 心愿数加1
    @Override
    public void addWishCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        String wishCount = thing.getWishCount();
        if (wishCount == null) {
            wishCount = "0";
        }
        thing.setWishCount(String.valueOf(Integer.parseInt(wishCount) + 1));
        mapper.updateById(thing);
    }

    // 收藏数加1
    @Override
    public void addCollectCount(String thingId) {
        Thing thing = mapper.selectById(thingId);
        String collectCount = thing.getCollectCount();
        if (collectCount == null) {
            collectCount = "0";
        }
        thing.setCollectCount(String.valueOf(Integer.parseInt(collectCount) + 1));
        mapper.updateById(thing);
    }

    public void setThingTags(Thing thing) {
        // 删除tag
        Map<String, Object> map = new HashMap<>();
        map.put("thing_id", thing.getId());
        thingTagMapper.deleteByMap(map);
        // 新增tag
        if (thing.getTags() != null) {
            for (Long tag : thing.getTags()) {
                ThingTag thingTag = new ThingTag();
                thingTag.setThingId(thing.getId());
                thingTag.setTagId(tag);
                thingTagMapper.insert(thingTag);
            }
        }
    }

}
