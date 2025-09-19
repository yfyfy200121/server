package com.gk.study.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gk.study.entity.Complaint;
import com.gk.study.mapper.ComplaintMapper;
import com.gk.study.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
    @Autowired
    ComplaintMapper mapper;

    @Override
    public List<Complaint> getComplaintList() {
        return mapper.getList();
    }

    @Override
    public void createComplaint(Complaint complaint) {
        System.out.println(complaint);
        String currentTime = String.valueOf(System.currentTimeMillis());
        complaint.setCreateTime(currentTime);
        complaint.setUpdateTime(currentTime);
        if (complaint.getStatus() == null || complaint.getStatus().isEmpty()) {
            complaint.setStatus("0"); // 默认状态：待处理
        }
        if (complaint.getPriority() == null || complaint.getPriority().isEmpty()) {
            complaint.setPriority("2"); // 默认优先级：中
        }
        mapper.insert(complaint);
    }

    @Override
    public void deleteComplaint(String id) {
        mapper.deleteById(id);
    }

    @Override
    public void updateComplaint(Complaint complaint) {
        complaint.setUpdateTime(String.valueOf(System.currentTimeMillis()));
        mapper.updateById(complaint);
    }

    @Override
    public Complaint getComplaintById(String id) {
        return mapper.selectById(id);
    }

    @Override
    public List<Complaint> getUserComplaintList(String userId) {
        return mapper.selectUserComplaintList(userId);
    }

    @Override
    public List<Complaint> getComplaintsByStatus(String status) {
        return mapper.selectByStatus(status);
    }

    @Override
    public List<Complaint> getComplaintsByType(String type) {
        return mapper.selectByType(type);
    }
}