package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Order;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
public class OrderController {

    private final static Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    OrderService service;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Order> list =  service.getOrderList();

        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 用户订单
    @RequestMapping(value = "/userOrderList", method = RequestMethod.GET)
    public APIResponse userOrderList(String userId, String status){
        List<Order> list =  service.getUserOrderList(userId, status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Order order) throws IOException {
        service.createOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "创建成功");
    }
    @RequestMapping(value = "/payOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse payOrder(Order order) throws IOException {
        service.payOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "支付成功");
    }


    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteOrder(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Order order) throws IOException {
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/cancelOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7"); // 7=取消
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/cancelUserOrder", method = RequestMethod.POST)
    @Transactional
    public APIResponse cancelUserOrder(Long id) throws IOException {
        Order order = new Order();
        order.setId(id);
        order.setStatus("7"); // 7=取消
        service.updateOrder(order);
        return new APIResponse(ResponeCode.SUCCESS, "取消成功");
    }

    /**
     * 确认收货
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/confirmReceived", method = RequestMethod.POST)
    @Transactional  
    public APIResponse confirmReceived(Long orderId, String userId) {
        try {
            Order order = service.getOrderById(String.valueOf(orderId));
            if (order == null) {
                return new APIResponse(ResponeCode.FAIL, "订单不存在");
            }
            
            // 验证订单归属
            if (!order.getUserId().equals(userId)) {
                return new APIResponse(ResponeCode.FAIL, "无权操作此订单");
            }
            
            // 只有已发货状态才能确认收货
            if (!"3".equals(order.getStatus())) {
                return new APIResponse(ResponeCode.FAIL, "订单状态不正确");
            }
            
            order.setStatus("4"); // 4=已完成
            service.updateOrder(order);
            
            // 触发推荐系统更新用户行为（购买完成）
            service.recordOrderCompletion(orderId, userId);
            
            return new APIResponse(ResponeCode.SUCCESS, "确认收货成功");
        } catch (Exception e) {
            logger.error("确认收货失败", e);
            return new APIResponse(ResponeCode.FAIL, "确认收货失败");
        }
    }
    
    /**
     * 申请退款
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/requestRefund", method = RequestMethod.POST)
    @Transactional
    public APIResponse requestRefund(Long orderId, String userId, String reason) {
        try {
            Order order = service.getOrderById(String.valueOf(orderId));
            if (order == null) {
                return new APIResponse(ResponeCode.FAIL, "订单不存在");
            }
            
            // 验证订单归属
            if (!order.getUserId().equals(userId)) {
                return new APIResponse(ResponeCode.FAIL, "无权操作此订单");
            }
            
            // 只有已付款或已发货状态才能申请退款
            if (!"2".equals(order.getStatus()) && !"3".equals(order.getStatus())) {
                return new APIResponse(ResponeCode.FAIL, "当前订单状态不支持退款");
            }
            
            order.setStatus("5"); // 5=退款中
            order.setRemark("退款原因：" + reason);
            service.updateOrder(order);
            
            return new APIResponse(ResponeCode.SUCCESS, "退款申请已提交");
        } catch (Exception e) {
            logger.error("申请退款失败", e);
            return new APIResponse(ResponeCode.FAIL, "申请退款失败");
        }
    }
    
    /**
     * 处理退款（管理员）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/processRefund", method = RequestMethod.POST)
    @Transactional
    public APIResponse processRefund(Long orderId, String action, String adminNote) {
        try {
            Order order = service.getOrderById(String.valueOf(orderId));
            if (order == null) {
                return new APIResponse(ResponeCode.FAIL, "订单不存在");
            }
            
            if (!"5".equals(order.getStatus())) {
                return new APIResponse(ResponeCode.FAIL, "订单不在退款状态");
            }
            
            if ("approve".equals(action)) {
                order.setStatus("6"); // 6=已退款
                order.setRemark(order.getRemark() + " | 管理员处理：" + adminNote);
            } else {
                order.setStatus("2"); // 恢复为已付款状态
                order.setRemark(order.getRemark() + " | 退款被拒绝：" + adminNote);
            }
            
            service.updateOrder(order);
            
            return new APIResponse(ResponeCode.SUCCESS, 
                "approve".equals(action) ? "退款已通过" : "退款已拒绝");
        } catch (Exception e) {
            logger.error("处理退款失败", e);
            return new APIResponse(ResponeCode.FAIL, "处理退款失败");
        }
    }
    
    /**
     * 获取订单详情
     */
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse getOrderDetail(String orderId) {
        try {
            Order order = service.getOrderDetailWithThing(orderId);
            if (order == null) {
                return new APIResponse(ResponeCode.FAIL, "订单不存在");
            }
            return new APIResponse(ResponeCode.SUCCESS, "获取订单详情成功", order);
        } catch (Exception e) {
            logger.error("获取订单详情失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取订单详情失败");
        }
    }
    
    /**
     * 获取订单统计信息（管理员）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public APIResponse getOrderStatistics(String startDate, String endDate) {
        try {
            Map<String, Object> statistics = service.getOrderStatistics(startDate, endDate);
            return new APIResponse(ResponeCode.SUCCESS, "获取订单统计成功", statistics);
        } catch (Exception e) {
            logger.error("获取订单统计失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取订单统计失败");
        }
    }
    
    /**
     * 批量发货（管理员）
     */
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/batchShip", method = RequestMethod.POST)
    @Transactional
    public APIResponse batchShipOrders(String orderIds) {
        try {
            String[] ids = orderIds.split(",");
            int successCount = 0;
            
            for (String orderId : ids) {
                Order order = service.getOrderById(orderId.trim());
                if (order != null && "2".equals(order.getStatus())) { // 只处理已付款的订单
                    order.setStatus("3"); // 3=已发货
                    service.updateOrder(order);
                    successCount++;
                }
            }
            
            return new APIResponse(ResponeCode.SUCCESS, 
                String.format("批量发货完成，成功发货 %d 个订单", successCount));
        } catch (Exception e) {
            logger.error("批量发货失败", e);
            return new APIResponse(ResponeCode.FAIL, "批量发货失败");
        }
    }

}
