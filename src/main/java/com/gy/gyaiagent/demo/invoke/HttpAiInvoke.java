package com.gy.gyaiagent.demo.invoke;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

public class HttpAiInvoke {
    public static void main(String[] args) {
        String apiKey = TestApiKey.ApiKey;
        String url = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";

        // 构建消息体
        JSONArray messages = new JSONArray();
        messages.add(JSONUtil.createObj()
                .set("role", "system")
                .set("content", "You are a helpful assistant."));
        messages.add(JSONUtil.createObj()
                .set("role", "user")
                .set("content", "你是谁？"));

        // 构建请求体 JSON
        JSONObject requestBody = JSONUtil.createObj()
                .set("model", "qwen-plus")
                .set("input", JSONUtil.createObj().set("messages", messages))
                .set("parameters", JSONUtil.createObj().set("result_format", "message"));

        // 发送请求
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute();

        // 输出结果
        System.out.println("Status: " + response.getStatus());
        System.out.println("Response: " + response.body());
    }
}