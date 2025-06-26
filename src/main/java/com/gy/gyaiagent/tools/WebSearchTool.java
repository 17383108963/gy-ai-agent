package com.gy.gyaiagent.tools;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 百度搜索工具包
 */
public class WebSearchTool {

    // SearchAPI 的搜索接口地址
    private static final String SEARCH_API_URL = "https://www.searchapi.io/api/v1/search";

    private final String apiKey;

    public WebSearchTool(String apiKey) {
        this.apiKey = apiKey;
    }

    @Tool(description = "Search for information from Baidu Search Engine")
    public String searchWeb(
            @ToolParam(description = "Search query keyword") String query) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("api_key", apiKey);
        paramMap.put("engine", "baidu");
        try {
            String response = HttpUtil.get(SEARCH_API_URL, paramMap);
            // 取出返回结果的前 5 条
            JSONObject jsonObject = JSONUtil.parseObj(response);
            // 1. 获取热门搜索
            JSONArray topSearches = jsonObject.getJSONArray("top_searches");
            String topResults = topSearches.stream()
                    .map(obj -> ((JSONObject)obj).getStr("query"))
                    .collect(Collectors.joining(","));
            // 2. 获取相关搜索
            JSONArray relatedSearches = jsonObject.getJSONArray("related_searches");
            String relatedResults = relatedSearches.stream()
                    .map(obj -> ((JSONObject)obj).getStr("query"))
                    .collect(Collectors.joining(","));
            return "相关搜索:\n" + relatedResults;
        } catch (Exception e) {
            return "Error searching Baidu: " + e.getMessage();
        }
    }
}
