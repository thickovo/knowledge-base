package com.gao.knowledgebase.controller;


import com.gao.knowledgebase.common.Result;
import com.gao.knowledgebase.dto.TagCreateRequest;
import com.gao.knowledgebase.dto.TagUpdateRequest;
import com.gao.knowledgebase.entity.DocumentTag;
import com.gao.knowledgebase.entity.Tag;
import com.gao.knowledgebase.service.DocumentService;
import com.gao.knowledgebase.service.DocumentTagService;
import com.gao.knowledgebase.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    @Autowired
    private DocumentTagService documentTagService;

    @PostMapping("/create")
    public Result<String> creatTag(@RequestBody TagCreateRequest request){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        //检查同名标签是否存在
        Tag exisTag = tagService.lambdaQuery()
                .eq(Tag::getUserId,username)
                .eq(Tag::getName,request.getName())
                .one();
        if(exisTag != null){
            return Result.error("标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(request.getName());
        tag.setUserId(username);
        tagService.save(tag);

        return Result.success("创建成功");
    }

    @GetMapping("/list")
    public Result<List<Tag>> listTag(){
        String username = (String)SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        List<Tag> list = tagService.lambdaQuery()
                .eq(Tag::getUserId,username)
                .list();
        return Result.success(list);
    }

    @PutMapping("/update")
    public  Result<String> updateTag(@RequestBody TagUpdateRequest request){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();
        Tag existag = tagService.getById(request.getId());
        if (existag == null){
            return Result.error("标签不存在");
        }
        if (!existag.getUserId().equals(username)){
            return Result.error("无更新权限");
        }
        existag.setName(request.getName());
        tagService.updateById(existag);
        return Result.success("更新成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> deleteTag(@PathVariable Long id){
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Tag existag = tagService.getById(id);
        if (existag == null){
            return Result.error("标签不存在");
        }
        if (!existag.getUserId().equals(username)){
            return Result.error("无删除权限");
        }
        tagService.removeById(id);
        documentTagService.lambdaUpdate()
                .eq(DocumentTag::getTagId,id)
                .remove();
        tagService.removeById(id);
        return Result.success("删除成功");
    }

}
