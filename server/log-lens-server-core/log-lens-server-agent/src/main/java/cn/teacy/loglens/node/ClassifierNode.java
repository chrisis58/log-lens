package cn.teacy.loglens.node;

import cn.teacy.loglens.graph.LogLensGraphKeys;
import cn.teacy.loglens.interfaces.IdProvider;
import cn.teacy.loglens.properties.AgentProperties;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.node.QuestionClassifierNode;
import lombok.Getter;
import org.springframework.ai.chat.client.ChatClient;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ClassifierNode implements IdProvider<String>, NodeWrapper {

    public static final String NODE_ID = "classifier-agent";

    @Getter
    public enum LogCategory {
        LOW_RISK("low risk"),
        TRIGGER_ALERT("trigger alert");

        LogCategory(String value) {
            this.value = value;
        }

        private final String value;

        public static List<String> toList() {
            return Arrays.stream(LogCategory.values())
                    .map(LogCategory::getValue)
                    .toList();
        }
    }

    private final NodeAction node;

    @Override
    public String getId() {
        return NODE_ID;
    }

    @Override
    public NodeAction getNode() {
        return node;
    }

    public ClassifierNode(
            Map<String, AgentProperties> agentProperties,
            ChatClient chatClient
    ) {
        AgentProperties properties = agentProperties.get(this.getId());

        String instruction = properties.getInstruction();

        // TODO: customize ToolCallbacks to fetch more context if needed

        this.node = QuestionClassifierNode.builder()
                .chatClient(chatClient)
                .categories(LogCategory.toList())
                .inputTextKey(LogLensGraphKeys.INPUT.getKey())
                .outputKey(LogLensGraphKeys.CATEGORY_RESULT.getKey())
                .classificationInstructions(List.of(
                        instruction
                )).build();
    }

}
