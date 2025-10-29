package cn.teacy.loglens.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@TestConfiguration
public class OpenAiChatModelConfig {

    @Bean
    public ChatModel chatModel() {

        return OpenAiChatModel.builder()
                .openAiApi(
                        OpenAiApi.builder()
                                .apiKey(System.getenv("OPENAI_API_KEY"))
                                .baseUrl(System.getenv("OPENAI_BASE_URL"))
                                .build()
                ).defaultOptions(
                        OpenAiChatOptions.builder()
                                .model(System.getenv("OPENAI_MODEL"))
                                .build()
                ).build();
    }

}
