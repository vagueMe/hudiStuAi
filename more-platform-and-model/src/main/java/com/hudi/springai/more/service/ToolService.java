package com.hudi.springai.more.service;

import cn.hutool.core.util.StrUtil;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author hudi
 * @date 23 3月 2026 16:27
 */
@Service
public class ToolService {

    @Tool(description = "退票")
    @PreAuthorize("hasRole('ADMIN')")
    public String cancel( @ToolParam(description = "预定号，可以是纯数字") String orderId,
                          @ToolParam(description = "真实人名（必填，必须为人的真实姓名，严禁用其他信息代替；如缺失请传null）") String name
                        ) {
        System.out.println("即将对订单：" + orderId + "，姓名：" + name + "进行退票处理");
        // 当前登录用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
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


        // 拿1个tool为例

        // 1.获取tools处理的方法
        Method method = ReflectionUtils.findMethod(ToolService.class, "cancel",String.class,String.class);
        // 2.构建Tool定义信息  动态配置的方式 @Tool @ToolParam都无效
        ToolDefinition toolDefinition = ToolDefinition.builder()
                .name("cancel")
                .description("退票")  // 对应@Tool的description
                // 对应@ToolParam
                .inputSchema("""
                        {
                          "type": "object",
                          "properties": {
                            "ticketNumber": {
                              "type": "string",
                              "description": "预定号，可以是纯数字"
                            },
                            "name": {
                              "type": "string",
                              "description": "真实人名"
                            }
                          },
                          "required": ["ticketNumber", "name"]
                        }
                        """)
                .build();
        // 一个ToolCallback对应一个tool
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(toolDefinition)
                .toolMethod(method)
                .toolObject(toolService)        // 不能自己new，自己new不能解析依赖注入
                .build();

        return List.of(toolCallback);
    }
}
