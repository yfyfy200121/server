package com.gk.study.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Order;
import com.gk.study.entity.Thing;
import com.gk.study.service.OrderService;
import com.gk.study.service.RecommendationService;
import com.gk.study.mapper.OrderMapper;
import com.gk.study.mapper.ThingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    @Autowired
    OrderMapper mapper;
    
    @Autowired
    ThingMapper thingMapper;
    
    @Autowired
    RecommendationService recommendationService;

    @Override
    public List<Order> getOrderList() {
        return mapper.getList();
    }

    @Override
    public void createOrder(Order order) {
        long ct = System.currentTimeMillis();
        order.setOrderTime(String.valueOf(ct));
        //order.setOrderNumber(String.valueOf(ct));
        order.setStatus("1");
        mapper.insert(order);
    }

    @Override
    public void payOrder(Order order) {
        UpdateWrapper<Order> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_number",order.getOrderNumber());
        order.setStatus("2");
        mapper.update(order,updateWrapper);
    }
    
    @Override
    public void deleteOrder(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateOrder(Order order) {
        mapper.updateById(order);
    }

    @Override
    public List<Order> getUserOrderList(String userId, String status) {
        return mapper.getUserOrderList(userId, status);
//        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("user_id", userId);
//        if (StringUtils.isNotBlank(status)) {
//            queryWrapper.eq("status", status);
//        }
//        queryWrapper.orderBy(true, false, "order_time");
//        return mapper.selectList(queryWrapper);
    }
    
    @Override
    public Order getOrderById(String orderId) {
        return mapper.selectById(orderId);
    }
    
    @Override
    public Order getOrderDetailWithThing(String orderId) {
        Order order = getOrderById(orderId);
        if (order != null && order.getThingId() != null) {
            // 获取商品详情
            Thing thing = thingMapper.selectById(order.getThingId());
            order.setThing(thing);
        }
        return order;
    }
    
    @Override
    public void recordOrderCompletion(Long orderId, String userId) {
        try {
            Order order = getOrderById(String.valueOf(orderId));
            if (order != null && order.getThingId() != null) {
                // 更新推荐系统的用户行为数据
                recommendationService.updateUserBehavior(userId, order.getThingId(), "purchase", 3.0);
            }
        } catch (Exception e) {
            // 异常不影响订单主流程
        }
    }
    
    @Override
    public Map<String, Object> getOrderStatistics(String startDate, String endDate) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            
            // 添加时间范围条件
            if (!StringUtils.isEmpty(startDate)) {
                queryWrapper.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                queryWrapper.le("order_time", endDate);
            }
            
            // 总订单数
            Long totalOrdersLong = mapper.selectCount(queryWrapper);
            int totalOrders = totalOrdersLong.intValue();
            statistics.put("totalOrders", totalOrders);
            
            // 已完成订单数
            QueryWrapper<Order> completedQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                completedQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                completedQuery.le("order_time", endDate);
            }
            completedQuery.eq("status", "4");
            Long completedOrdersLong = mapper.selectCount(completedQuery);
            int completedOrders = completedOrdersLong.intValue();
            statistics.put("completedOrders", completedOrders);
            
            // 待付款订单数
            QueryWrapper<Order> pendingPaymentQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                pendingPaymentQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                pendingPaymentQuery.le("order_time", endDate);
            }
            pendingPaymentQuery.eq("status", "1");
            Long pendingPaymentOrdersLong = mapper.selectCount(pendingPaymentQuery);
            int pendingPaymentOrders = pendingPaymentOrdersLong.intValue();
            statistics.put("pendingPaymentOrders", pendingPaymentOrders);
            
            // 已付款订单数
            QueryWrapper<Order> paidQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                paidQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                paidQuery.le("order_time", endDate);
            }
            paidQuery.eq("status", "2");
            Long paidOrdersLong = mapper.selectCount(paidQuery);
            int paidOrders = paidOrdersLong.intValue();
            statistics.put("paidOrders", paidOrders);
            
            // 已发货订单数
            QueryWrapper<Order> shippedQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                shippedQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                shippedQuery.le("order_time", endDate);
            }
            shippedQuery.eq("status", "3");
            Long shippedOrdersLong = mapper.selectCount(shippedQuery);
            int shippedOrders = shippedOrdersLong.intValue();
            statistics.put("shippedOrders", shippedOrders);
            
            // 已取消订单数
            QueryWrapper<Order> cancelledQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                cancelledQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                cancelledQuery.le("order_time", endDate);
            }
            cancelledQuery.eq("status", "7");
            Long cancelledOrdersLong = mapper.selectCount(cancelledQuery);
            int cancelledOrders = cancelledOrdersLong.intValue();
            statistics.put("cancelledOrders", cancelledOrders);
            
            // 退款中订单数
            QueryWrapper<Order> refundingQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                refundingQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                refundingQuery.le("order_time", endDate);
            }
            refundingQuery.eq("status", "5");
            Long refundingOrdersLong = mapper.selectCount(refundingQuery);
            int refundingOrders = refundingOrdersLong.intValue();
            statistics.put("refundingOrders", refundingOrders);
            
            // 已退款订单数
            QueryWrapper<Order> refundedQuery = new QueryWrapper<>();
            if (!StringUtils.isEmpty(startDate)) {
                refundedQuery.ge("order_time", startDate);
            }
            if (!StringUtils.isEmpty(endDate)) {
                refundedQuery.le("order_time", endDate);
            }
            refundedQuery.eq("status", "6");
            Long refundedOrdersLong = mapper.selectCount(refundedQuery);
            int refundedOrders = refundedOrdersLong.intValue();
            statistics.put("refundedOrders", refundedOrders);
            
            // 计算完成率
            double completionRate = totalOrders > 0 ? (double) completedOrders / totalOrders * 100 : 0;
            statistics.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
            
            // 计算取消率
            double cancellationRate = totalOrders > 0 ? (double) cancelledOrders / totalOrders * 100 : 0;
            statistics.put("cancellationRate", Math.round(cancellationRate * 100.0) / 100.0);
            
            // 获取今日订单数
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String today = sdf.format(new Date());
            QueryWrapper<Order> todayQuery = new QueryWrapper<>();
            todayQuery.like("order_time", today);
            Long todayOrdersLong = mapper.selectCount(todayQuery);
            int todayOrders = todayOrdersLong.intValue();
            statistics.put("todayOrders", todayOrders);
            
        } catch (Exception e) {
            // 异常时返回默认统计数据
            statistics.put("totalOrders", 0);
            statistics.put("completedOrders", 0);
            statistics.put("pendingPaymentOrders", 0); 
            statistics.put("paidOrders", 0);
            statistics.put("shippedOrders", 0);
            statistics.put("cancelledOrders", 0);
            statistics.put("refundingOrders", 0);
            statistics.put("refundedOrders", 0);
            statistics.put("completionRate", 0.0);
            statistics.put("cancellationRate", 0.0);
            statistics.put("todayOrders", 0);
        }
        
        return statistics;
    }
}
