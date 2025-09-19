package com.gk.study.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gk.study.entity.Complaint;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    List<Complaint> getList();

    List<Complaint> selectUserComplaintList(String userId);

    List<Complaint> selectByStatus(String status);
    
    List<Complaint> selectByType(String type);
}