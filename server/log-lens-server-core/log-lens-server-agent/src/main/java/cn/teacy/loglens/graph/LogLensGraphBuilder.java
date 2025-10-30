package cn.teacy.loglens.graph;

import cn.teacy.loglens.node.ClassifierNode;
import cn.teacy.loglens.node.SummarizationNode;
import cn.teacy.loglens.properties.LogLensProperties;
import com.alibaba.cloud.ai.graph.*;
import com.alibaba.cloud.ai.graph.action.AsyncNodeAction;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.alibaba.cloud.ai.graph.StateGraph.END;
import static com.alibaba.cloud.ai.graph.StateGraph.START;
import static com.alibaba.cloud.ai.graph.action.AsyncEdgeAction.edge_async;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({LogLensProperties.class})
public class LogLensGraphBuilder {

    private final LogLensProperties logLensProperties;

    @Bean
    public CompiledGraph buildGraph(ChatModel chatModel) throws GraphStateException {
        ChatClient chatClient = ChatClient.builder(chatModel)
                .build();

        KeyStrategyFactory keyStrategyFactory = LogLensGraphKeys.buildKeyStrategyFactory();

        StateGraph stateGraph = new StateGraph(keyStrategyFactory);

        // init and add nodes
        ClassifierNode classifierNode = new ClassifierNode(logLensProperties.getAgents(), chatClient);
        stateGraph.addNode(
                classifierNode.getId(),
                AsyncNodeAction.node_async(classifierNode.getNode())
        );

        SummarizationNode summarizationNode = new SummarizationNode(logLensProperties.getAgents(), chatClient);
        stateGraph.addNode(
                summarizationNode.getId(),
                AsyncNodeAction.node_async(summarizationNode.getNode())
        );

        // add edges
        stateGraph.addEdge(START, classifierNode.getId());
        stateGraph.addConditionalEdges(classifierNode.getId(),
                edge_async(state -> {
                    String value = state.value(LogLensGraphKeys.CATEGORY_RESULT.getKey(), String.class).orElse("");
                    for (ClassifierNode.LogCategory category : ClassifierNode.LogCategory.values()) {
                        if (category.getValue().equalsIgnoreCase(value)) {
                            return category.getValue();
                        }
                    }
                    return "fallback";
                }),
                Map.of(
                        ClassifierNode.LogCategory.TRIGGER_ALERT.getValue(), summarizationNode.getId(),
                        ClassifierNode.LogCategory.LOW_RISK.getValue(), END,
                        "fallback", END
                )
        );
        stateGraph.addEdge(summarizationNode.getId(), END);

        return stateGraph.compile();
    }


}
