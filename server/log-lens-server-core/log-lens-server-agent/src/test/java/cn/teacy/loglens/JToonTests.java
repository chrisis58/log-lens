package cn.teacy.loglens;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipestanzani.jtoon.JToon;
import org.junit.jupiter.api.Test;

public class JToonTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testJToon() throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readValue(WINDOWS_EVENT_LOG_IN_JSON, JsonNode.class);

        String encoded = JToon.encode(jsonNode);

        System.out.println(encoded);
    }

    private static final String WINDOWS_EVENT_LOG_IN_JSON = """
            {
              "Events": [
                {
                  "System": {
                    "Provider": {
                      "Name": "Microsoft-Windows-Security-Auditing",
                      "Guid": "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                    },
                    "EventID": 4625,
                    "Version": 0,
                    "Level": 0,
                    "Task": 12544,
                    "Opcode": 0,
                    "Keywords": "0x8010000000000000",
                    "TimeCreated": {
                      "SystemTime": "2025-10-29T13:49:55.0180867Z"
                    },
                    "EventRecordID": 30396740,
                    "Correlation": {
                      "ActivityID": "{d4934a3e-4874-0002-124b-93d47448dc01}"
                    },
                    "Execution": {
                      "ProcessID": 1440,
                      "ThreadID": 1480
                    },
                    "Channel": "Security",
                    "Computer": "some_pc",
                    "Security": null
                  },
                  "EventData": {
                    "SubjectUserSid": "S-1-0-0",
                    "SubjectUserName": "-",
                    "SubjectDomainName": "-",
                    "SubjectLogonId": "0x0",
                    "TargetUserSid": "S-1-0-0",
                    "TargetUserName": "ADMINISTRATOR",
                    "TargetDomainName": "",
                    "Status": "0xc000006d",
                    "FailureReason": "%%2313",
                    "SubStatus": "0xc000006a",
                    "LogonType": 3,
                    "LogonProcessName": "NtLmSsp ",
                    "AuthenticationPackageName": "NTLM",
                    "WorkstationName": "-",
                    "TransmittedServices": "-",
                    "LmPackageName": "-",
                    "KeyLength": 0,
                    "ProcessId": "0x0",
                    "ProcessName": "-",
                    "IpAddress": "127.0.0.1",
                    "IpPort": 0
                  }
                },
                {
                  "System": {
                    "Provider": {
                      "Name": "Microsoft-Windows-Security-Auditing",
                      "Guid": "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                    },
                    "EventID": 4625,
                    "Version": 0,
                    "Level": 0,
                    "Task": 12544,
                    "Opcode": 0,
                    "Keywords": "0x8010000000000000",
                    "TimeCreated": {
                      "SystemTime": "2025-10-29T13:49:50.2327932Z"
                    },
                    "EventRecordID": 30396739,
                    "Correlation": {
                      "ActivityID": "{d4934a3e-4874-0002-124b-93d47448dc01}"
                    },
                    "Execution": {
                      "ProcessID": 1440,
                      "ThreadID": 1480
                    },
                    "Channel": "Security",
                    "Computer": "some_pc",
                    "Security": null
                  },
                  "EventData": {
                    "SubjectUserSid": "S-1-0-0",
                    "SubjectUserName": "-",
                    "SubjectDomainName": "-",
                    "SubjectLogonId": "0x0",
                    "TargetUserSid": "S-1-0-0",
                    "TargetUserName": "ADMINISTRATOR",
                    "TargetDomainName": "",
                    "Status": "0xc000006d",
                    "FailureReason": "%%2313",
                    "SubStatus": "0xc000006a",
                    "LogonType": 3,
                    "LogonProcessName": "NtLmSsp ",
                    "AuthenticationPackageName": "NTLM",
                    "WorkstationName": "-",
                    "TransmittedServices": "-",
                    "LmPackageName": "-",
                    "KeyLength": 0,
                    "ProcessId": "0x0",
                    "ProcessName": "-",
                    "IpAddress": "127.0.0.1",
                    "IpPort": 0
                  }
                },
                {
                  "System": {
                    "Provider": {
                      "Name": "Microsoft-Windows-Security-Auditing",
                      "Guid": "{54849625-5478-4994-a5ba-3e3b0328c30d}"
                    },
                    "EventID": 4625,
                    "Version": 0,
                    "Level": 0,
                    "Task": 12544,
                    "Opcode": 0,
                    "Keywords": "0x8010000000000000",
                    "TimeCreated": {
                      "SystemTime": "2025-10-29T13:49:49.4538950Z"
                    },
                    "EventRecordID": 30396738,
                    "Correlation": {
                      "ActivityID": "{d4934a3e-4874-0002-124b-93d47448dc01}"
                    },
                    "Execution": {
                      "ProcessID": 1440,
                      "ThreadID": 1480
                    },
                    "Channel": "Security",
                    "Computer": "some_pc",
                    "Security": null
                  },
                  "EventData": {
                    "SubjectUserSid": "S-1-0-0",
                    "SubjectUserName": "-",
                    "SubjectDomainName": "-",
                    "SubjectLogonId": "0x0",
                    "TargetUserSid": "S-1-0-0",
                    "TargetUserName": "ADMINISTRATOR",
                    "TargetDomainName": "",
                    "Status": "0xc000006d",
                    "FailureReason": "%%2313",
                    "SubStatus": "0xc000006a",
                    "LogonType": 3,
                    "LogonProcessName": "NtLmSsp ",
                    "AuthenticationPackageName": "NTLM",
                    "WorkstationName": "-",
                    "TransmittedServices": "-",
                    "LmPackageName": "-",
                    "KeyLength": 0,
                    "ProcessId": "0x0",
                    "ProcessName": "-",
                    "IpAddress": "127.0.0.1",
                    "IpPort": 0
                  }
                }
              ]
            }
            """;

}
