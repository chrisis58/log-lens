package cn.teacy.loglens;


import cn.teacy.loglens.config.OpenAiChatModelConfig;
import cn.teacy.loglens.graph.LogLensGraphBuilder;
import cn.teacy.loglens.graph.LogLensGraphKeys;
import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = {LogLensGraphBuilder.class, OpenAiChatModelConfig.class})
public class LogLensGraphTests {

    @Autowired
    private CompiledGraph graph;

    @Test
    void testResolveFatFingerLog() {
        Optional<OverAllState> invoke = graph.call(Map.of(
                LogLensGraphKeys.INPUT.getKey(),
                JOURNAL_FAT_FINGER_SUDO_LOG
        ));

        assertNotNull(invoke, "Invoke result should not be null");
        assertTrue(invoke.isPresent(), "Invoke result should not be null");

        OverAllState overAllState = invoke.get();

        System.out.println(overAllState.value(LogLensGraphKeys.CATEGORY_RESULT.getKey()));
        System.out.println(overAllState.value(LogLensGraphKeys.SUMMARY_RESULT.getKey()));
    }

    @Test
    void testBruteForceLoginLog() {
        Optional<OverAllState> invoke = graph.call(Map.of(
                LogLensGraphKeys.INPUT.getKey(),
                BRUTE_FORCE_LOGIN_LOG
        ));

        assertNotNull(invoke, "Invoke result should not be null");
        assertTrue(invoke.isPresent(), "Invoke result should not be null");

        OverAllState overAllState = invoke.get();

        System.out.println(overAllState.value(LogLensGraphKeys.CATEGORY_RESULT.getKey()));
        System.out.println(overAllState.value(LogLensGraphKeys.SUMMARY_RESULT.getKey()));
    }

    private static final String JOURNAL_FAT_FINGER_SUDO_LOG = """
            Oct 29 15:15:55 some_host sudo[443809]: pam_unix(sudo:auth): auth could not identify password for [some_user]
            Oct 29 15:15:55 some_host sudo[443809]:  some_user : 1 incorrect password attempt ; TTY=pts/0 ; PWD=/home/some_user ; USER=root ; COMMAND=/usr/bin/journalctl
            Oct 29 15:16:00 some_host sudo[443907]: pam_unix(sudo:session): session opened for user root(uid=0) by some_user(uid=1000)
            """;

    /**
     * windows event log in toon format
     *
     * @see <a href="https://github.com/johannschopplich/toon">toon repo</a>
     * @see <a href="https://github.com/felipestanzani/JToon">JToon repo</a>
     */
    private static final String BRUTE_FORCE_LOGIN_LOG = """
            Events[3]:
              - System:
                  Provider:
                    Name: Microsoft-Windows-Security-Auditing
                    Guid: "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                  EventID: 4625
                  Version: 0
                  Level: 0
                  Task: 12544
                  Opcode: 0
                  Keywords: 0x8010000000000000
                  TimeCreated:
                    SystemTime: "2025-10-30T14:11:24.7536745Z"
                  EventRecordID: 30488827
                  Correlation:
                    ActivityID: "{7bdbb4bf-493d-0002-9ab5-db7b3d49dc01}"
                  Execution:
                    ProcessID: 1404
                    ThreadID: 50148
                  Channel: Security
                  Computer: some_pc
                  Security: null
                EventData:
                  SubjectUserSid: S-1-0-0
                  SubjectUserName: "-"
                  SubjectDomainName: "-"
                  SubjectLogonId: 0x0
                  TargetUserSid: S-1-0-0
                  TargetUserName: TEST1
                  TargetDomainName: ""
                  Status: 0xc000006d
                  FailureReason: %%2313
                  SubStatus: 0xc0000064
                  LogonType: 3
                  LogonProcessName: "NtLmSsp "
                  AuthenticationPackageName: NTLM
                  WorkstationName: "-"
                  TransmittedServices: "-"
                  LmPackageName: "-"
                  KeyLength: 0
                  ProcessId: 0x0
                  ProcessName: "-"
                  IpAddress: 127.0.0.1
                  IpPort: 0
              - System:
                  Provider:
                    Name: Microsoft-Windows-Security-Auditing
                    Guid: "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                  EventID: 4625
                  Version: 0
                  Level: 0
                  Task: 12544
                  Opcode: 0
                  Keywords: 0x8010000000000000
                  TimeCreated:
                    SystemTime: "2025-10-30T14:11:24.4465272Z"
                  EventRecordID: 30488826
                  Correlation:
                    ActivityID: "{7bdbb4bf-493d-0002-9ab5-db7b3d49dc01}"
                  Execution:
                    ProcessID: 1404
                    ThreadID: 50148
                  Channel: Security
                  Computer: some_pc
                  Security: null
                EventData:
                  SubjectUserSid: S-1-0-0
                  SubjectUserName: "-"
                  SubjectDomainName: "-"
                  SubjectLogonId: 0x0
                  TargetUserSid: S-1-0-0
                  TargetUserName: TEST2
                  TargetDomainName: ""
                  Status: 0xc000006d
                  FailureReason: %%2313
                  SubStatus: 0xc000006a
                  LogonType: 3
                  LogonProcessName: "NtLmSsp "
                  AuthenticationPackageName: NTLM
                  WorkstationName: "-"
                  TransmittedServices: "-"
                  LmPackageName: "-"
                  KeyLength: 0
                  ProcessId: 0x0
                  ProcessName: "-"
                  IpAddress: 127.0.0.1
                  IpPort: 0
              - System:
                  Provider:
                    Name: Microsoft-Windows-Security-Auditing
                    Guid: "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                  EventID: 4625
                  Version: 0
                  Level: 0
                  Task: 12544
                  Opcode: 0
                  Keywords: 0x8010000000000000
                  TimeCreated:
                    SystemTime: "2025-10-30T14:11:23.9319877Z"
                  EventRecordID: 30488825
                  Correlation:
                    ActivityID: "{7bdbb4bf-493d-0002-9ab5-db7b3d49dc01}"
                  Execution:
                    ProcessID: 1404
                    ThreadID: 50148
                  Channel: Security
                  Computer: some_pc
                  Security: null
                EventData:
                  SubjectUserSid: S-1-0-0
                  SubjectUserName: "-"
                  SubjectDomainName: "-"
                  SubjectLogonId: 0x0
                  TargetUserSid: S-1-0-0
                  TargetUserName: TEST3
                  TargetDomainName: ""
                  Status: 0xc000006d
                  FailureReason: %%2313
                  SubStatus: 0xc0000064
                  LogonType: 3
                  LogonProcessName: "NtLmSsp "
                  AuthenticationPackageName: NTLM
                  WorkstationName: "-"
                  TransmittedServices: "-"
                  LmPackageName: "-"
                  KeyLength: 0
                  ProcessId: 0x0
                  ProcessName: "-"
                  IpAddress: 127.0.0.1
                  IpPort: 0
            """;

}
