package cn.teacy.loglens;

import cn.teacy.loglens.config.AgentConfig;
import cn.teacy.loglens.config.OpenAiChatModelConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {AgentConfig.class, OpenAiChatModelConfig.class})
public class AgentConfigTest {

    @Autowired
    @Qualifier("routeAgent")
    private ReactAgent routeAgent;

    private static final String LOG_CONTENT = """
            Oct 29 15:15:55 some_host sudo[443809]: pam_unix(sudo:auth): auth could not identify password for [some_user]
            Oct 29 15:15:55 some_host sudo[443809]:  some_user : 1 incorrect password attempt ; TTY=pts/0 ; PWD=/home/some_user ; USER=root ; COMMAND=/usr/bin/journalctl
            Oct 29 15:16:00 some_host sudo[443907]: pam_unix(sudo:session): session opened for user root(uid=0) by some_user(uid=1000)
            """;

    @Test
    void testRoute() throws GraphRunnerException {
        AssistantMessage message = routeAgent.call("please resolve the following logs:\n" + LOG_CONTENT);

        assertNotNull(message, "Message should not be null");
        assertNotNull(message.getText(), "Message text should not be null");
        System.out.println(message.getText());

        assertTrue(message.getText().contains("triggerAlert") || message.getText().contains("summary") ||
                        message.getText().contains("reasoning"),
                "Output should contain structured fields");
    }

}