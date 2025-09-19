package com.gk.study.service;

import com.gk.study.entity.Complaint;

import java.util.List;

public interface ComplaintService {

    List<Complaint> getComplaintList();

    void createComplaint(Complaint complaint);

    void deleteComplaint(String id);

    void updateComplaint(Complaint complaint);

    Complaint getComplaintById(String id);

    List<Complaint> getUserComplaintList(String userId);

    List<Complaint> getComplaintsByStatus(String status);

    List<Complaint> getComplaintsByType(String type);
}