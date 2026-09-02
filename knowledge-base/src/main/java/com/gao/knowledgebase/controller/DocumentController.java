package com.gao.knowledgebase.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gao.knowledgebase.common.Result;
import com.gao.knowledgebase.dto.DocumentCreateRequest;
import com.gao.knowledgebase.dto.DocumentUpdateRequest;
import com.gao.knowledgebase.entity.Document;
import com.gao.knowledgebase.entity.DocumentTag;
import com.gao.knowledgebase.service.DocumentService;
import com.gao.knowledgebase.service.DocumentTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;
    @Autowired
    private DocumentTagService documentTagService;

    @PostMapping("/create")
    public Result<String> createDocument(@Valid @RequestBody DocumentCreateRequest
            request){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        System.out.println("当前用户: " + username);

        Document document = new Document();
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        document.setUserId(username);
        document.setParentId(request.getParentId());

        documentService.save(document);
        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            for (Long tagId : request.getTagIds()) {
                DocumentTag dt = new DocumentTag();
                dt.setDocumentId(document.getId());
                dt.setTagId(tagId);
                documentTagService.save(dt);
            }
        }
        return Result.success("创建成功");
    }

    @GetMapping("/list")
    public Result<List<Document>> listDocument(@RequestParam(required = false) Long parentId,
    @RequestParam(required = false) Long tagId,
    @RequestParam(required = false) String keyword){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        LambdaQueryWrapper<Document> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Document::getUserId, username);
        wrapper.eq(Document::getParentId,parentId != null ? parentId : 0L);
        if (tagId != null) {
            //1.查出该标签下所有文档ID
            List<DocumentTag> docTags = documentTagService.lambdaQuery()
                    .eq(DocumentTag::getTagId, tagId)
                    .list();
            //2.提取文档ID列表
            List<Long> docIds = docTags.stream()
                    .map(DocumentTag::getDocumentId)
                    .collect(Collectors.toList());
            //3.如果文档ID列表为空，直接返回空列表
            if (docIds.isEmpty()) {
                return Result.success(Collections.emptyList());
            }
            //4.在查询中加上文档ID筛选
            wrapper.in(Document::getId, docIds);
        }
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Document::getTitle, keyword)
                    .or()
                    .like(Document::getContent, keyword));
        }
        List<Document> list = documentService.list(wrapper);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<Document> getDocument(@PathVariable Long id){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Document document = documentService.getById(id);
        if (document == null) {
            return Result.error("文档不存在");
        }
        if (!document.getUserId().equals(username)){
            return Result.error("无权限访问");
        }
        return Result.success(document);
    }

    @PutMapping("/update")
    public Result<String> updateDocument(@RequestBody DocumentUpdateRequest request){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Document document = documentService.getById(request.getId());
        if (document == null) {
            return Result.error("文档不存在");
        }
        if (!document.getUserId().equals(username)){
            return Result.error("无权限访问");
        }
        document.setTitle(request.getTitle());
        document.setContent(request.getContent());
        documentService.updateById(document);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteDocument(@PathVariable Long id){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Document document = documentService.getById(id);
        if (document == null) {
            return Result.error("文档不存在");
        }
        if (!document.getUserId().equals(username)){
            return Result.error("无权限访问");
        }
        documentService.removeById(id);
        return Result.success("删除成功");
    }
}
