package cn.teacy.loglens.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@Slf4j
@TestConfiguration
public class OllamaChatModelConfig {

    @Bean
    public ChatModel chatModel() {

        log.info("Configuring Ollama with model: {}", System.getenv("OLLAMA_MODEL"));

        return OllamaChatModel.builder()
                .ollamaApi(
                        OllamaApi.builder()
                                .baseUrl(System.getenv("OLLAMA_BASE_URL"))
                                .build()
                )
                .defaultOptions(
                        OllamaOptions.builder()
                                .model(System.getenv("OLLAMA_MODEL"))
                                .build()
                ).build();
    }

}
