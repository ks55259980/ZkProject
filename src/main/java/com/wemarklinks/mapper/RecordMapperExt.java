package com.wemarklinks.mapper;

import java.util.List;
import java.util.Map;

import com.wemarklinks.pojo.Record;

public interface RecordMapperExt {
    int insert(Record record);
    
    List<Record> selectBySelective(Map<String, Object> map);
    
    int countBySelective(Map<String, Object> map);
    
}