package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.service.UserService;
import com.gk.study.entity.User;
import com.gk.study.mapper.UserMapper;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.mapper.ThingCollectMapper;
import com.gk.study.mapper.ThingWishMapper;
import com.gk.study.mapper.CommentMapper;
import com.gk.study.entity.Order;
import com.gk.study.entity.ThingCollect;
import com.gk.study.entity.ThingWish;
import com.gk.study.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    UserMapper userMapper;
    
    @Autowired
    OrderMapper orderMapper;
    
    @Autowired
    ThingCollectMapper thingCollectMapper;
    
    @Autowired
    ThingWishMapper thingWishMapper;
    
    @Autowired
    CommentMapper commentMapper;

    @Override
    public List<User> getUserList(String keyword) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        if(StringUtils.isNotBlank(keyword)){
            // like查询
            queryWrapper.like("username", keyword);
        }
        queryWrapper.orderBy(true, false, "create_time");
        return userMapper.selectList(queryWrapper);
    }

    @Override
    public User getAdminUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.gt("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getNormalUser(User user) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        queryWrapper.eq("role", "1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public void createUser(User user) {
        userMapper.insert(user);
    }

    @Override
    public void deleteUser(String id) {
        userMapper.deleteById(id);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }

    @Override
    public User getUserByToken(String token) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("token", token);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserByUserName(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public User getUserDetail(String userId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", userId);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public User getUserByEmail(String email) {
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        queryWrapper.eq("email", email);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public Map<String, Object> getUserStatistics(String userId) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // 统计订单数量
            QueryWrapper<Order> orderQuery = new QueryWrapper<>();
            orderQuery.eq("user_id", userId);
            Long totalOrdersLong = orderMapper.selectCount(orderQuery);
            int totalOrders = totalOrdersLong.intValue();
            stats.put("totalOrders", totalOrders);
            
            // 统计已完成订单
            QueryWrapper<Order> completedOrderQuery = new QueryWrapper<>();
            completedOrderQuery.eq("user_id", userId).eq("status", "7");
            Long completedOrdersLong = orderMapper.selectCount(completedOrderQuery);
            int completedOrders = completedOrdersLong.intValue();
            stats.put("completedOrders", completedOrders);
            
            // 统计收藏数量
            QueryWrapper<ThingCollect> collectQuery = new QueryWrapper<>();
            collectQuery.eq("user_id", userId);
            Long totalCollectsLong = thingCollectMapper.selectCount(collectQuery);
            int totalCollects = totalCollectsLong.intValue();
            stats.put("totalCollects", totalCollects);
            
            // 统计愿望清单数量
            QueryWrapper<ThingWish> wishQuery = new QueryWrapper<>();
            wishQuery.eq("user_id", userId);
            Long totalWishesLong = thingWishMapper.selectCount(wishQuery);
            int totalWishes = totalWishesLong.intValue();
            stats.put("totalWishes", totalWishes);
            
            // 统计评论数量
            QueryWrapper<Comment> commentQuery = new QueryWrapper<>();
            commentQuery.eq("user_id", userId);
            Long totalCommentsLong = commentMapper.selectCount(commentQuery);
            int totalComments = totalCommentsLong.intValue();
            stats.put("totalComments", totalComments);
            
            // 计算用户等级（基于订单数和活跃度）
            String userLevel = "新手";
            if (completedOrders >= 20) {
                userLevel = "专家";
            } else if (completedOrders >= 10) {
                userLevel = "高级";
            } else if (completedOrders >= 5) {
                userLevel = "中级";
            }
            stats.put("userLevel", userLevel);
            
            // 计算活跃度分数
            int activityScore = completedOrders * 10 + totalCollects * 2 + totalWishes * 1 + totalComments * 3;
            stats.put("activityScore", activityScore);
            
        } catch (Exception e) {
            // 异常时返回默认值
            stats.put("totalOrders", 0);
            stats.put("completedOrders", 0);
            stats.put("totalCollects", 0);
            stats.put("totalWishes", 0);
            stats.put("totalComments", 0);
            stats.put("userLevel", "新手");
            stats.put("activityScore", 0);
        }
        
        return stats;
    }
}
