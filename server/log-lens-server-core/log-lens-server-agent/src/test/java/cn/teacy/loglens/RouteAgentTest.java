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
public class RouteAgentTest {

    @Autowired
    @Qualifier("routeAgent")
    private ReactAgent routeAgent;


    @Test
    void testFatFingerLog() throws GraphRunnerException {
        AssistantMessage message = routeAgent.call("please resolve the following logs:\n" + JOURNAL_FAT_FINGER_SUDO_LOG);

        assertNotNull(message, "Message should not be null");
        assertNotNull(message.getText(), "Message text should not be null");
        System.out.println(message.getText());

        assertTrue(message.getText().contains("triggerAlert") || message.getText().contains("summary") ||
                        message.getText().contains("reasoning"),
                "Output should contain structured fields");
    }

    @Test
    void testBruteForceLog() throws GraphRunnerException {
        AssistantMessage message = routeAgent.call("please resolve the following windows logs:\n" + BRUTE_FORCE_LOGIN_LOG);

        assertNotNull(message, "Message should not be null");
        assertNotNull(message.getText(), "Message text should not be null");
        System.out.println(message.getText());

        assertTrue(message.getText().contains("triggerAlert") || message.getText().contains("summary") ||
                        message.getText().contains("reasoning"),
                "Output should contain structured fields");
    }

    private static final String JOURNAL_FAT_FINGER_SUDO_LOG = """
            Oct 29 15:15:55 some_host sudo[443809]: pam_unix(sudo:auth): auth could not identify password for [some_user]
            Oct 29 15:15:55 some_host sudo[443809]:  some_user : 1 incorrect password attempt ; TTY=pts/0 ; PWD=/home/some_user ; USER=root ; COMMAND=/usr/bin/journalctl
            Oct 29 15:16:00 some_host sudo[443907]: pam_unix(sudo:session): session opened for user root(uid=0) by some_user(uid=1000)
            """;

    private static final String BRUTE_FORCE_LOGIN_LOG = """
            <Events>
                <Event xmlns='http://schemas.microsoft.com/win/2004/08/events/event'>
                    <System>
                        <Provider Name='Microsoft-Windows-Security-Auditing' Guid='{54849625-5478-4994-a5ba-3e3b0328c30d}'/>
                        <EventID>4625</EventID>
                        <Version>0</Version>
                        <Level>0</Level>
                        <Task>12544</Task>
                        <Opcode>0</Opcode>
                        <Keywords>0x8010000000000000</Keywords>
                        <TimeCreated SystemTime='2025-10-29T13:49:55.0180867Z'/>
                        <EventRecordID>30396740</EventRecordID>
                        <Correlation ActivityID='{d4934a3e-4874-0002-124b-93d47448dc01}'/>
                        <Execution ProcessID='1440' ThreadID='1480'/>
                        <Channel>Security</Channel>
                        <Computer>some_pc</Computer>
                        <Security/>
                    </System>
                    <EventData>
                        <Data Name='SubjectUserSid'>S-1-0-0</Data>
                        <Data Name='SubjectUserName'>-</Data>
                        <Data Name='SubjectDomainName'>-</Data>
                        <Data Name='SubjectLogonId'>0x0</Data>
                        <Data Name='TargetUserSid'>S-1-0-0</Data>
                        <Data Name='TargetUserName'>ADMINISTRATOR</Data>
                        <Data Name='TargetDomainName'></Data>
                        <Data Name='Status'>0xc000006d</Data>
                        <Data Name='FailureReason'>%%2313</Data>
                        <Data Name='SubStatus'>0xc000006a</Data>
                        <Data Name='LogonType'>3</Data>
                        <Data Name='LogonProcessName'>NtLmSsp </Data>
                        <Data Name='AuthenticationPackageName'>NTLM</Data>
                        <Data Name='WorkstationName'>-</Data>
                        <Data Name='TransmittedServices'>-</Data>
                        <Data Name='LmPackageName'>-</Data>
                        <Data Name='KeyLength'>0</Data>
                        <Data Name='ProcessId'>0x0</Data>
                        <Data Name='ProcessName'>-</Data>
                        <Data Name='IpAddress'>127.0.0.1</Data>
                        <Data Name='IpPort'>0</Data>
                    </EventData>
                </Event>
                <Event xmlns='http://schemas.microsoft.com/win/2004/08/events/event'>
                    <System>
                        <Provider Name='Microsoft-Windows-Security-Auditing' Guid='{54849625-5478-4994-a5ba-3e3b0328c30d}'/>
                        <EventID>4625</EventID>
                        <Version>0</Version>
                        <Level>0</Level>
                        <Task>12544</Task>
                        <Opcode>0</Opcode>
                        <Keywords>0x8010000000000000</Keywords>
                        <TimeCreated SystemTime='2025-10-29T13:49:50.2327932Z'/>
                        <EventRecordID>30396739</EventRecordID>
                        <Correlation ActivityID='{d4934a3e-4874-0002-124b-93d47448dc01}'/>
                        <Execution ProcessID='1440' ThreadID='1480'/>
                        <Channel>Security</Channel>
                        <Computer>some_pc</Computer>
                        <Security/>
                    </System>
                    <EventData>
                        <Data Name='SubjectUserSid'>S-1-0-0</Data>
                        <Data Name='SubjectUserName'>-</Data>
                        <Data Name='SubjectDomainName'>-</Data>
                        <Data Name='SubjectLogonId'>0x0</Data>
                        <Data Name='TargetUserSid'>S-1-0-0</Data>
                        <Data Name='TargetUserName'>ADMINISTRATOR</Data>
                        <Data Name='TargetDomainName'></Data>
                        <Data Name='Status'>0xc000006d</Data>
                        <Data Name='FailureReason'>%%2313</Data>
                        <Data Name='SubStatus'>0xc000006a</Data>
                        <Data Name='LogonType'>3</Data>
                        <Data Name='LogonProcessName'>NtLmSsp </Data>
                        <Data Name='AuthenticationPackageName'>NTLM</Data>
                        <Data Name='WorkstationName'>-</Data>
                        <Data Name='TransmittedServices'>-</Data>
                        <Data Name='LmPackageName'>-</Data>
                        <Data Name='KeyLength'>0</Data>
                        <Data Name='ProcessId'>0x0</Data>
                        <Data Name='ProcessName'>-</Data>
                        <Data Name='IpAddress'>127.0.0.1</Data>
                        <Data Name='IpPort'>0</Data>
                    </EventData>
                </Event>
                <Event xmlns='http://schemas.microsoft.com/win/2004/08/events/event'>
                    <System>
                        <Provider Name='Microsoft-Windows-Security-Auditing' Guid='{54849625-5478-4994-a5ba-3e3b0328c30d}'/>
                        <EventID>4625</EventID>
                        <Version>0</Version>
                        <Level>0</Level>
                        <Task>12544</Task>
                        <Opcode>0</Opcode>
                        <Keywords>0x8010000000000000</Keywords>
                        <TimeCreated SystemTime='2025-10-29T13:49:49.4538950Z'/>
                        <EventRecordID>30396738</EventRecordID>
                        <Correlation ActivityID='{d4934a3e-4874-0002-124b-93d47448dc01}'/>
                        <Execution ProcessID='1440' ThreadID='1480'/>
                        <Channel>Security</Channel>
                        <Computer>some_pc</Computer>
                        <Security/>
                    </System>
                    <EventData>
                        <Data Name='SubjectUserSid'>S-1-0-0</Data>
                        <Data Name='SubjectUserName'>-</Data>
                        <Data Name='SubjectDomainName'>-</Data>
                        <Data Name='SubjectLogonId'>0x0</Data>
                        <Data Name='TargetUserSid'>S-1-0-0</Data>
                        <Data Name='TargetUserName'>ADMINISTRATOR</Data>
                        <Data Name='TargetDomainName'></Data>
                        <Data Name='Status'>0xc000006d</Data>
                        <Data Name='FailureReason'>%%2313</Data>
                        <Data Name='SubStatus'>0xc000006a</Data>
                        <Data Name='LogonType'>3</Data>
                        <Data Name='LogonProcessName'>NtLmSsp </Data>
                        <Data Name='AuthenticationPackageName'>NTLM</Data>
                        <Data Name='WorkstationName'>-</Data>
                        <Data Name='TransmittedServices'>-</Data>
                        <Data Name='LmPackageName'>-</Data>
                        <Data Name='KeyLength'>0</Data>
                        <Data Name='ProcessId'>0x0</Data>
                        <Data Name='ProcessName'>-</Data>
                        <Data Name='IpAddress'>127.0.0.1</Data>
                        <Data Name='IpPort'>0</Data>
                    </EventData>
                </Event>
            </Events>
            """;

}