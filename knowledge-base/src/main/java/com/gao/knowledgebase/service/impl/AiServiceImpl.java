package com.gao.knowledgebase.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gao.knowledgebase.service.AiService;
import org.springframework.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
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


    @Override
    public String chat(String userMessage) throws Exception {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model",modelName);
        List<Map<String, Object>> requestBodyList = new ArrayList<>();
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("role","user");
        requestBodyMap.put("content",userMessage);
        requestBodyList.add(requestBodyMap);
        requestBody.put("messages",requestBodyList);
        requestBody.put("stream", false);
//        return "测试回复：" + userMessage;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        HttpEntity<Map<String,Object>> httpEntity =
                new HttpEntity<>(requestBody,headers);
        String response = restTemplate
                .postForObject(baseUrl,httpEntity,String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);
        String aiReply = root.path("choices").get(0)
                .path("message")
                .path("content")
                .asText();

        return aiReply;
    }
}
