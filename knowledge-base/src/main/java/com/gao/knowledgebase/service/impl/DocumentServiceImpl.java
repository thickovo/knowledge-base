package com.gao.knowledgebase.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gao.knowledgebase.entity.Document;
import com.gao.knowledgebase.mapper.DocumentMapper;
import com.gao.knowledgebase.service.DocumentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, Document>implements DocumentService {


}
