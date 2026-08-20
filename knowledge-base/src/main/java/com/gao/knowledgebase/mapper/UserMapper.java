package com.gao.knowledgebase.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gao.knowledgebase.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}