package com.gk.study.service;


import com.gk.study.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService {
    List<Order> getOrderList();
    void createOrder(Order order);
    void payOrder(Order order);
    void deleteOrder(String id);

    void updateOrder(Order order);

    List<Order> getUserOrderList(String userId, String status);
    
    /**
     * 根据ID获取订单
     */
    Order getOrderById(String orderId);
    
    /**
     * 获取订单详情（包含商品信息）
     */
    Order getOrderDetailWithThing(String orderId);
    
    /**
     * 记录订单完成（触发推荐系统更新）
     */
    void recordOrderCompletion(Long orderId, String userId);
    
    /**
     * 获取订单统计信息
     */
    Map<String, Object> getOrderStatistics(String startDate, String endDate);
}
