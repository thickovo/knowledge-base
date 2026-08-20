package com.gao.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.knowledgebase.entity.DocumentTag;
import com.gao.knowledgebase.mapper.DocumentTagMapper;
import com.gao.knowledgebase.service.DocumentTagService;
import org.springframework.stereotype.Service;

@Service
public class DocumentTagServiceImpl extends
        ServiceImpl<DocumentTagMapper, DocumentTag> implements DocumentTagService {

}
