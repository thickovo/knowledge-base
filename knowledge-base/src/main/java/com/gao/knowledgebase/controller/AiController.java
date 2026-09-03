package com.gao.knowledgebase.controller;

import com.gao.knowledgebase.common.Result;
import com.gao.knowledgebase.service.AiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AiController {

    @Autowired
    private AiService aiService;

    @GetMapping("/api/ai")
    public Result ceshi(@RequestParam String message) throws Exception {
        if (message == null || message.trim().isEmpty()) {
            return Result.error("消息不能为空");
        }

        aiService.chat(message);
        return Result.success(aiService.chat(message));
    }
}
