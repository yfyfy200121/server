package com.gk.study.service;


import com.gk.study.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<User> getUserList(String keyword);
    User getAdminUser(User user);
    User getNormalUser(User user);
    void createUser(User user);
    void deleteUser(String id);

    void updateUser(User user);

    User getUserByToken(String token);
    User getUserByUserName(String username);

    User getUserDetail(String userId);
    
    /**
     * 根据邮箱获取用户
     */
    User getUserByEmail(String email);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics(String userId);
}
