package cn.teacy.loglens.node;

import cn.teacy.loglens.graph.LogLensGraphKeys;
import cn.teacy.loglens.properties.AgentProperties;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import com.alibaba.cloud.ai.graph.node.AgentNode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;

import java.util.Map;

public class SummarizationNode implements NodeWrapper {

    public static final String NODE_ID = "summarization-agent";

    @Override
    public String getId() {
        return NODE_ID;
    }

    @Override
    public NodeAction getNode() {
        return node;
    }

    private final NodeAction node;

    public SummarizationNode(
            Map<String, AgentProperties> agentProperties,
            ChatClient chatClient
    ) {
        AgentProperties properties = agentProperties.get(NODE_ID);

        String systemPrompt = properties.getSystemPrompt();
        String instruction = properties.getInstruction();

        this.node = AgentNode.builder()
                .systemPrompt(systemPrompt + "\n" + instruction)
                .userPrompt(LogLensGraphKeys.INPUT.getWrappedKey())
                .chatClient(chatClient)
                .outputKey(LogLensGraphKeys.SUMMARY_RESULT.getKey())
                .toolCallbacks(new ToolCallback[]{})
                .build();
    }


}
