package com.hudi.springai.more;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @author hudi
 * @date 20 3月 2026 10:35
 */
@SpringBootTest
public class TestStructuredOut {


    @Autowired
    DashScopeChatModel dashScopeChatModel;
    @Autowired
    DeepSeekChatModel deepSeekChatModel;

    @Autowired
    private ChatMemory chatMemory;

    public ChatClient init() {
//        ChatClient.Builder builder = ChatClient.builder(dashScopeChatModel);
        ChatClient.Builder builder = ChatClient.builder(deepSeekChatModel);
        ChatOptions options = ChatOptions.builder()
                .build();
        builder.defaultOptions(options);
        builder.defaultAdvisors(List.of(
                new SimpleLoggerAdvisor(),
                new SafeGuardAdvisor(List.of("政治")),
//                    new ReReadingAdvisor(),
                PromptChatMemoryAdvisor.builder(chatMemory).build()
        ));
        return builder.build();
    }

    static  class Res {
        private Boolean value;

        public Boolean getValue() {
            return value;
        }

        public void setValue(Boolean value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Res{" +
                    "value=" + value +
                    '}';
        }
    }

     record Address(
            String name,        // 收件人姓名
            String phone,       // 联系电话
            String province,    // 省
            String city,        // 市
            String district,    // 区/县
            String detail       // 详细地址
    ){}

    @Test
    public void test1() {
        ChatClient client = init();
        Res entity = client.prompt()
                .system("请判断用户信息是否表达了投诉意图?只能用 true 或 false 回答，不要输出多余内容")
                .user("你们服务为什么这么差！！")
                .call()
                .entity(Res.class);

        if (entity != null && Boolean.TRUE.equals(entity.getValue())) {
            System.out.println("用户是投诉，转接人工客服！");
        } else {
            System.out.println("用户不是投诉，自动流转客服机器人。");
            //  继续调用 客服ChatClient进行对话
        }
    }

    @Test
    public void test2(){
        Address  address = init().prompt()
                .system("""
                        请从下面这条文本中提取收货信息,
                        """)
                .user("收货人：张三，电话13588888888，地址：浙江省杭州市西湖区文一西路100号8幢202室")
                .call()
                .entity(Address.class);
        if (address != null) {
            System.out.println(address.name());
            System.out.println(address.name);
            System.out.println(address);
        }

    }

    @Test
    public void test3(){
        BeanOutputConverter<Address> beanOutputConverter =
                new BeanOutputConverter<>(Address.class);

        String format = beanOutputConverter.getFormat();

        String actor = "收货人：张三，电话13588888888，地址：浙江省杭州市西湖区文一西路100号8幢202室";

        String template = """
        请从{actor}文本中提取收货信息,.
        {format}
        """;

        PromptTemplate promptTemplate = PromptTemplate.builder().template(template).variables(Map.of("actor", actor, "format", format)).build();
        ChatResponse response = deepSeekChatModel.call(
                promptTemplate.create()
        );

        Address actorsFilms = beanOutputConverter.convert(response.getResult().getOutput().getText());
        System.out.println(actorsFilms);
    }



}
