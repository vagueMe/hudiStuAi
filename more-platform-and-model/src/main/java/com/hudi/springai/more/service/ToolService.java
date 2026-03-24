package com.hudi.springai.more.service;

import cn.hutool.core.util.StrUtil;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hudi
 * @date 23 3月 2026 16:27
 */
@Service
public class ToolService {

    @Tool(description = "退票")
//    @PreAuthorize("hasRole('ADMIN')")
    public String cancel( @ToolParam(description = "预定号，可以是纯数字") String orderId,
                          @ToolParam(description = "真实人名（必填，必须为人的真实姓名，严禁用其他信息代替；如缺失请传null）") String name
                        ) {
        System.out.println("即将对订单：" + orderId + "，姓名：" + name + "进行退票处理");
        // 当前登录用户名
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (StrUtil.isBlank(name)) {
           return "请输入用户名";
        }

        if (StrUtil.isBlank(orderId)) {
            return "预定号";
        }
        return "退票成功！";
    }

    // 获取指定位置的天气
    @Tool(description = """
            获取指定位置天气。
            如果提供的是城市名称,则自动推算经纬度。
            如果提供的是经纬度，则自动推算城市信息。
            """)
    public String queryWeather( @ToolParam(description = "经度") String longitude,
                                @ToolParam(description = "纬度") String latitude,
                                @ToolParam(description = "城市名") String city
                              ) {
        System.out.println("即将查询：" + longitude + "，位置：" + latitude + "，城市：" + city);
        return "天气晴朗！";
    }

    /**
     * 模拟从数据库中动态根据当前用户角色读取tools
     * @param toolService 不能自己new，自己new不能解析依赖注入
     * @return
     */
    public List<ToolCallback> getToolCallList(ToolService toolService) {

        // todo.. 从数据库中读取的代码省略
        List<Map<String, String>> tools = List.of(
                Map.of(
                        "name", "cancel",
                        "description", "退票",
                        "inputSchema", """
                        {
                          "type": "object",
                          "properties": {
                            "orderId": {
                              "type": "string",
                              "description": "预定号，可以是纯数字"
                            },
                            "name": {
                              "type": "string",
                              "description": "真实人名（必填，必须为人的真实姓名，严禁用其他信息代替；如缺失请传null）"
                            }
                          },
                          "required": ["orderId", "name"]
                        }
                        """
                ),
                Map.of(
                        "name", "queryWeather",
                        "description", """
                        获取指定位置天气。
                        如果提供的是城市名称,则自动推算经纬度。
                        如果提供的是经纬度，则自动推算城市信息。
                        """,
                        "inputSchema", """
                        {
                          "type": "object",
                          "properties": {
                            "longitude": {
                              "type": "string",
                              "description": "经度"
                            },
                            "latitude": {
                              "type": "string",
                              "description": "纬度"
                            },
                            "city": {
                              "type": "string",
                              "description": "城市名"
                            }
                          },
                          "required": ["longitude", "latitude", "city"]
                        }
                        """
                )
        );

        List<ToolCallback> toolCallbacks = new ArrayList<>();
        for (Map<String, String> tool : tools) {
            // 1.获取tools处理的方法
            String inputSchema = tool.get("inputSchema");
            int paramCount = countRequiredParams(inputSchema);
            Class<?>[] paramTypes = new Class[paramCount];
            java.util.Arrays.fill(paramTypes, String.class);

            Method method = ReflectionUtils.findMethod(ToolService.class, tool.get("name"), paramTypes);

            if (method == null) {
                throw new IllegalStateException("未找到方法：" + tool.get("name") + ", 参数数量：" + paramCount);
            }

            // 2.构建Tool定义信息  动态配置的方式 @Tool @ToolParam都无效
            ToolDefinition toolDefinition = ToolDefinition.builder()
                    .name(tool.get("name"))
                    .description(tool.get("description"))  // 对应@Tool的description
                    // 对应@ToolParam
                    .inputSchema(tool.get("inputSchema"))
                    .build();
            // 一个ToolCallback对应一个tool
            ToolCallback toolCallback = MethodToolCallback.builder()
                    .toolDefinition(toolDefinition)
                    .toolMethod(method)
                    .toolObject(toolService)        // 不能自己new，自己new不能解析依赖注入
                    .build();
            toolCallbacks.add(toolCallback);
        }
        return toolCallbacks;
    }

    /**
     * 统计 JSON Schema 中 required 字段的数量
     */
    private int countRequiredParams(String inputSchema) {
        try {
            com.fasterxml.jackson.databind.JsonNode jsonNode =
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(inputSchema);
            com.fasterxml.jackson.databind.JsonNode required = jsonNode.get("required");
            if (required != null && required.isArray()) {
                return required.size();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
