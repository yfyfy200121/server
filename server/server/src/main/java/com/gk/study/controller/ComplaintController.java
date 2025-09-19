package com.gk.study.controller;

import com.gk.study.common.APIResponse;
import com.gk.study.common.ResponeCode;
import com.gk.study.entity.Complaint;
import com.gk.study.permission.Access;
import com.gk.study.permission.AccessLevel;
import com.gk.study.service.ComplaintService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/complaint")
public class ComplaintController {

    private final static Logger logger = LoggerFactory.getLogger(ComplaintController.class);

    @Autowired
    ComplaintService service;

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public APIResponse list(){
        List<Complaint> list = service.getComplaintList();
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 用户的所有投诉
    @RequestMapping(value = "/listUserComplaints", method = RequestMethod.GET)
    public APIResponse listUserComplaints(String userId){
        List<Complaint> list = service.getUserComplaintList(userId);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 按状态查询投诉
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/listByStatus", method = RequestMethod.GET)
    public APIResponse listByStatus(String status){
        List<Complaint> list = service.getComplaintsByStatus(status);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    // 按类型查询投诉
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/listByType", method = RequestMethod.GET)
    public APIResponse listByType(String type){
        List<Complaint> list = service.getComplaintsByType(type);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", list);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @Transactional
    public APIResponse create(Complaint complaint) throws IOException {
        service.createComplaint(complaint);
        return new APIResponse(ResponeCode.SUCCESS, "投诉提交成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public APIResponse delete(String ids){
        System.out.println("ids===" + ids);
        // 批量删除
        String[] arr = ids.split(",");
        for (String id : arr) {
            service.deleteComplaint(id);
        }
        return new APIResponse(ResponeCode.SUCCESS, "删除成功");
    }

    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    @Transactional
    public APIResponse update(Complaint complaint) throws IOException {
        service.updateComplaint(complaint);
        return new APIResponse(ResponeCode.SUCCESS, "更新成功");
    }

    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    public APIResponse detail(String id){
        Complaint complaint = service.getComplaintById(id);
        return new APIResponse(ResponeCode.SUCCESS, "查询成功", complaint);
    }

    // 管理员回复投诉
    @Access(level = AccessLevel.ADMIN)
    @RequestMapping(value = "/reply", method = RequestMethod.POST)
    @Transactional
    public APIResponse reply(String id, String adminReply, String adminId) throws IOException {
        Complaint complaint = service.getComplaintById(id);
        if (complaint != null) {
            complaint.setAdminReply(adminReply);
            complaint.setAdminId(adminId);
            complaint.setStatus("2"); // 设置为已解决
            service.updateComplaint(complaint);
            return new APIResponse(ResponeCode.SUCCESS, "回复成功");
        }
        return new APIResponse(ResponeCode.FAIL, "投诉不存在");
    }
}