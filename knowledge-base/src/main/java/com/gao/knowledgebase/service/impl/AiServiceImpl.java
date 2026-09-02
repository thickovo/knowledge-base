package com.gao.knowledgebase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gao.knowledgebase.entity.Document;
import com.gao.knowledgebase.service.AiService;
import com.gao.knowledgebase.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ai.model.api-key}")
    String apiKey;
    @Value("${ai.model.model-name}")
    String modelName;
    @Value("${ai.model.base-url}")
    String baseUrl;

    @Autowired
    private DocumentService documentService;


    @Override
    public String chat(String userMessage) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", modelName);
        List<Map<String, Object>> requestBodyList = new ArrayList<>();
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("role", "user");
        requestBodyMap.put("content", userMessage);
        requestBodyList.add(requestBodyMap);
        requestBody.put("messages", requestBodyList);
        requestBody.put("stream", false);
//        return "测试回复：" + userMessage;


        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> userIdProp = new HashMap<>();
        userIdProp.put("type", "string");
        userIdProp.put("description", "当前登录用户的用户名");
        properties.put("user_id", userIdProp);

        List<String> required = new ArrayList<>();
        required.add("user_id");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("type", "object");

        //叠！
        parameters.put("properties", properties);
        parameters.put("required", required);

        Map<String, Object> function = new HashMap<>();
        function.put("name", "query_document");
        function.put("description", "查询当前用户的文档列表");

        //叠！！
        function.put("parameters", parameters);

        List<Map<String, Object>> toolList = new ArrayList<>();
        Map<String, Object> tool = new HashMap<>();
        tool.put("type", "function");

        //叠！！！
        tool.put("function", function);
        toolList.add(tool);

        requestBody.put("tools", toolList);
        requestBody.put("tool_choice", "required");


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Map<String, Object>> httpEntity =
                new HttpEntity<>(requestBody, headers);
        String response = restTemplate
                .postForObject(baseUrl, httpEntity, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JsonNode root = objectMapper.readTree(response);
        JsonNode message = root.path("choices")
                .get(0)
                .path("message");
        if (message.has("tool_calls")) {
            //如果有工具 就调用
            JsonNode toolCalls = message.path("tool_calls");
            JsonNode firstTool = toolCalls.get(0);
            String toolName = firstTool
                    .path("function")
                    .path("name")
                    .asText();
            String arguments = firstTool
                    .path("function")
                    .path("arguments")
                    .asText();

            //根据toolName执行对应的方法
            //取出user_id
            String userId = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getName();

            //调用DocumentService 查询文档
            LambdaQueryWrapper<Document> wrapper
                    = new LambdaQueryWrapper<>();
            wrapper.eq(Document::getUserId, userId);
            List<Document> documents = documentService.list(wrapper);
            String result = objectMapper.writeValueAsString(documents);

            return result;
        } else {
            //没用工具调用，直接取内容

            String aiReply = message
                    .path("content")
                    .asText();

            return aiReply;
        }
    }
}
