package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.User;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
/**
 * @author Administrator
 * @email 123456@qq.com
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger logger = LoggerFactory.getLogger(UserController.class);

    String salt = "abcd1234";

    @Autowired
    UserService userService;

    @Value("${File.uploadPath}")
    private String uploadPath;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(String keyword){
        List<User> list =  userService.getUserList(keyword);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String userId){
        User user =  userService.getUserDetail(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", user);
    }

    // 后台用户登录
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public APIResponse login(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getAdminUser(user);
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户登录
    @RequestMapping(value = "/userLogin", method = RequestMethod.POST)
    public APIResponse userLogin(User user){
        user.setPassword(DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes()));
        User responseUser =  userService.getNormalUser(user);
        if(responseUser != null) {
            return new APIResponse(ResponeCode.SUCCESS, "查询成功", responseUser);
        }else {
            return new APIResponse(ResponeCode.FAIL, "用户名或密码错误");
        }
    }

    // 普通用户注册
    @RequestMapping(value = "/userRegister", method = RequestMethod.POST)
    @Transactional
    public APIResponse userRegister(User user) throws IOException {

        if (!StringUtils.isEmpty(user.getUsername())
                && !StringUtils.isEmpty(user.getPassword())
                && !StringUtils.isEmpty(user.getRePassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            // 验证密码
            if(!user.getPassword().equals(user.getRePassword())) {
                return new APIResponse(ResponeCode.FAIL, "密码不一致");
            }
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);

            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            // 设置角色
            user.setRole(String.valueOf(User.NormalUser));
            // 设置状态
            user.setStatus("0");
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(User user) throws IOException {

        if (!StringUtils.isEmpty(user.getUsername()) || !StringUtils.isEmpty(user.getPassword())) {
            // 查重
            if(userService.getUserByUserName(user.getUsername()) != null) {
                return new APIResponse(ResponeCode.FAIL, "用户名重复");
            }
            String md5Str = DigestUtils.md5DigestAsHex((user.getPassword() + salt).getBytes());
            // 设置密码
            user.setPassword(md5Str);
            md5Str = DigestUtils.md5DigestAsHex((user.getUsername() + salt).getBytes());
            // 设置token
            user.setToken(md5Str);
            user.setCreateTime(String.valueOf(System.currentTimeMillis()));

            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.createUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "创建成功");
        }
        return new APIResponse(ResponeCode.FAIL, "创建失败");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            userService.deleteUser(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(User user) throws IOException {
        // update不能修改密码，故置空
        user.setPassword(null);
        String avatar = saveAvatar(user);
        if(!StringUtils.isEmpty(avatar)) {
            user.avatar = avatar;
        }
        userService.updateUser(user);
        System.out.println(user);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }


    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updateUserInfo", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserInfo(User user) throws IOException {
        User tmpUser =  userService.getUserDetail(user.getId());
        if(tmpUser.getRole().equals(String.valueOf(User.NormalUser))){
            // username和password不能改，故置空
            user.setUsername(null);
            user.setPassword(null);
            user.setRole(String.valueOf(User.NormalUser));
            String avatar = saveAvatar(user);
            if(!StringUtils.isEmpty(avatar)) {
                user.avatar = avatar;
            }
            userService.updateUser(user);
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }
    }

    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePwd", method = RequestMethod.POST)
    @Transactional
    public APIResponse updatePwd(String userId, String password, String newPassword) throws IOException {
        User user =  userService.getUserDetail(userId);
        if(user.getRole().equals(String.valueOf(User.NormalUser))) {
            String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if(user.getPassword().equals(md5Pwd)){
                user.setPassword(DigestUtils.md5DigestAsHex((newPassword + salt).getBytes()));
                userService.updateUser(user);
            }else {
                return new APIResponse(ResponeCode.FAIL, "原密码错误");
            }
            return new APIResponse(ResponeCode.SUCCESS, "更新成功");
        }else {
            return new APIResponse(ResponeCode.FAIL, "非法操作");
        }

    }

    /**
     * 获取用户统计信息（订单数、收藏数等）
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public APIResponse getUserStats(String userId) {
        try {
            Map<String, Object> stats = userService.getUserStatistics(userId);
            return new APIResponse(ResponeCode.SUCCESS, "获取用户统计成功", stats);
        } catch (Exception e) {
            logger.error("获取用户统计失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取用户统计失败");
        }
    }

    /**
     * 更新用户偏好设置
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/updatePreferences", method = RequestMethod.POST)
    @Transactional
    public APIResponse updateUserPreferences(String userId, 
                                           @RequestParam(required = false) String favoriteCategories,
                                           @RequestParam(required = false) String priceRange,
                                           @RequestParam(required = false) String language,
                                           @RequestParam(required = false) String notificationSettings) {
        try {
            User user = userService.getUserDetail(userId);
            if (user == null) {
                return new APIResponse(ResponeCode.FAIL, "用户不存在");
            }

            // 构建偏好设置JSON
            Map<String, Object> preferences = new HashMap<>();
            if (favoriteCategories != null) {
                preferences.put("favoriteCategories", favoriteCategories.split(","));
            }
            if (priceRange != null) {
                preferences.put("priceRange", priceRange);
            }
            if (language != null) {
                preferences.put("language", language);
            }
            if (notificationSettings != null) {
                preferences.put("notificationSettings", notificationSettings);
            }

            // 更新用户描述字段存储偏好（或者可以扩展User实体添加preferences字段）
            user.setDescription("preferences:" + preferences.toString());
            userService.updateUser(user);

            return new APIResponse(ResponeCode.SUCCESS, "更新用户偏好成功");
        } catch (Exception e) {
            logger.error("更新用户偏好失败", e);
            return new APIResponse(ResponeCode.FAIL, "更新用户偏好失败");
        }
    }

    /**
     * 获取用户偏好设置
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/preferences", method = RequestMethod.GET)
    public APIResponse getUserPreferences(String userId) {
        try {
            User user = userService.getUserDetail(userId);
            if (user == null) {
                return new APIResponse(ResponeCode.FAIL, "用户不存在");
            }

            Map<String, Object> preferences = new HashMap<>();
            String description = user.getDescription();
            if (description != null && description.startsWith("preferences:")) {
                // 解析偏好设置（简化实现，生产环境建议使用JSON解析）
                preferences.put("raw", description.substring(12));
            }

            return new APIResponse(ResponeCode.SUCCESS, "获取用户偏好成功", preferences);
        } catch (Exception e) {
            logger.error("获取用户偏好失败", e);
            return new APIResponse(ResponeCode.FAIL, "获取用户偏好失败");
        }
    }

    /**
     * 注销用户账户
     */
    @Access(level = AccessLevel.LOGIN)
    @RequestMapping(value = "/deactivate", method = RequestMethod.POST)
    @Transactional
    public APIResponse deactivateAccount(String userId, String password) {
        try {
            User user = userService.getUserDetail(userId);
            if (user == null) {
                return new APIResponse(ResponeCode.FAIL, "用户不存在");
            }

            // 验证密码
            String md5Pwd = DigestUtils.md5DigestAsHex((password + salt).getBytes());
            if (!user.getPassword().equals(md5Pwd)) {
                return new APIResponse(ResponeCode.FAIL, "密码错误");
            }

            // 设置用户状态为禁用
            user.setStatus("1");
            userService.updateUser(user);

            return new APIResponse(ResponeCode.SUCCESS, "账户已注销");
        } catch (Exception e) {
            logger.error("注销账户失败", e);
            return new APIResponse(ResponeCode.FAIL, "注销账户失败");
        }
    }

    /**
     * 重置密码（发送邮件验证码）
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public APIResponse resetPassword(String email) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return new APIResponse(ResponeCode.FAIL, "邮箱不存在");
            }

            // 生成验证码（这里简化实现，实际应该发送邮件）
            String resetCode = UUID.randomUUID().toString().substring(0, 6);
            
            // 将验证码存储到缓存或数据库中（这里简化存储到用户的pushEmail字段）
            user.setPushEmail(resetCode);
            userService.updateUser(user);

            logger.info("重置密码验证码: {} (用户: {})", resetCode, email);
            
            return new APIResponse(ResponeCode.SUCCESS, "验证码已发送到邮箱，请查收");
        } catch (Exception e) {
            logger.error("重置密码失败", e);
            return new APIResponse(ResponeCode.FAIL, "重置密码失败");
        }
    }

    /**
     * 确认重置密码
     */
    @RequestMapping(value = "/confirmResetPassword", method = RequestMethod.POST)
    @Transactional
    public APIResponse confirmResetPassword(String email, String resetCode, String newPassword) {
        try {
            User user = userService.getUserByEmail(email);
            if (user == null) {
                return new APIResponse(ResponeCode.FAIL, "邮箱不存在");
            }

            // 验证重置码
            if (!resetCode.equals(user.getPushEmail())) {
                return new APIResponse(ResponeCode.FAIL, "验证码错误");
            }

            // 更新密码
            String md5Pwd = DigestUtils.md5DigestAsHex((newPassword + salt).getBytes());
            user.setPassword(md5Pwd);
            user.setPushEmail(null); // 清除验证码
            userService.updateUser(user);

            return new APIResponse(ResponeCode.SUCCESS, "密码重置成功");
        } catch (Exception e) {
            logger.error("确认重置密码失败", e);
            return new APIResponse(ResponeCode.FAIL, "确认重置密码失败");
        }
    }

    public String saveAvatar(User user) throws IOException {
        MultipartFile file = user.getAvatarFile();
        String newFileName = null;
        if(file !=null && !file.isEmpty()) {

            // 存文件
            String oldFileName = file.getOriginalFilename();
            String randomStr = UUID.randomUUID().toString();
            newFileName = randomStr + oldFileName.substring(oldFileName.lastIndexOf("."));
            String filePath = uploadPath + File.separator + "avatar" + File.separator + newFileName;
            File destFile = new File(filePath);
            if(!destFile.getParentFile().exists()){
                destFile.getParentFile().mkdirs();
            }
            file.transferTo(destFile);
        }
        if(!StringUtils.isEmpty(newFileName)) {
            user.avatar = newFileName;
        }
        return newFileName;
    }
}
