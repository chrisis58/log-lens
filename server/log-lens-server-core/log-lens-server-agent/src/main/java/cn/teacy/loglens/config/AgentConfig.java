package cn.teacy.loglens.config;

import cn.teacy.loglens.domain.RouteOutput;
import cn.teacy.loglens.properties.AgentProperties;
import cn.teacy.loglens.properties.LogLensProperties;
import com.alibaba.cloud.ai.graph.agent.Builder;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({LogLensProperties.class})
public class AgentConfig {

    private final LogLensProperties logLensProperties;

    @Bean("routeAgent")
    public ReactAgent routeAgent(ChatModel chatModel) throws GraphStateException {

        AgentProperties agentProperties = logLensProperties.getAgents().get("route-agent");

        if (Objects.isNull(agentProperties)) {
            log.warn("No agents configured for route-agent, using default configuration.");
            agentProperties = AgentProperties.builder()
                    .instruction("""
                            You are the primary log analysis routing agent.
                            Your immediate task is to analyze the incoming log block.
                            Apply your SRE/Security rules (defined in your system prompt) to determine the correct output (alert status and summary).
                            """)
                    .build();
        }

        Builder agentBuilder = ReactAgent.builder()
                .model(chatModel)
                .outputType(RouteOutput.class)
                .description("An agent that routes log analysis tasks.");

        if (Objects.nonNull(agentProperties.getInstruction()) && !agentProperties.getInstruction().isEmpty()) {
            agentBuilder.instruction(agentProperties.getInstruction());
        }

        if (Objects.nonNull(agentProperties.getSystemPrompt()) && !agentProperties.getSystemPrompt().isEmpty()) {
            agentBuilder.systemPrompt(agentProperties.getSystemPrompt());
        }

        return agentBuilder.build();
    }

}
