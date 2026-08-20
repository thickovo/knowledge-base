package com.gao.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.knowledgebase.entity.Tag;
import com.gao.knowledgebase.mapper.TagMapper;
import com.gao.knowledgebase.service.TagService;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {
}