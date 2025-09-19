# Log-Lens: A Self-Hosted, AI-Powered Log Analysis Assistant

**English** | [简体中文](./README.md)

> Status: Active Early Development
>
> Core Tech Stack: Agent (Go) | Server (Python/FastAPI) | LLM (Ollama) | WebUI (Vue.js / React) 

## Vision
Have you ever found yourself working through massive volumes of service logs, trying to find the root cause of an issue amidst the noise? Traditional log management tools help aggregate and search logs, but this remains a reactive and time-consuming process.

Log-Lens aims to provide a new approach to traditional log management. We use the capabilities of modern Large Language Models (LLMs) to distill complex raw logs into insightful, human-readable digests. Log-Lens is a log analysis assistant designed to **proactively** analyze problems, protect your **data privacy**, and be **easy to deploy**.

## Core Pain Points
The traditional log processing workflow presents the following challenges:

1. **Information Overload**: Modern applications generate vast amounts of logs, making manual filtering and correlation analysis nearly impossible. Critical signals are often buried in overwhelming noise.
2. **Reactive Troubleshooting**: Developers typically only look at logs after a system failure has occurred. This approach lacks the ability to preemptively warn or discover potential issues.
3. **High Cost & Complexity**: Commercial log analysis platforms are powerful but come with high price tags and complex deployment and maintenance, making them difficult for individual developers and small to medium-sized teams to adopt.
4. **Data Privacy Concerns**: Uploading sensitive production logs to third-party cloud services for AI analysis poses significant data privacy and security risks.

## Our Solution
Log-Lens addresses these challenges in the following ways:

1. **Intelligent Summarization & Insights**: By integrating AI models, Log-Lens automatically reads logs, summarizes key events, identifies anomaly patterns, and generates natural language reports.
2. **Proactive Anomaly Detection**: The built-in alerting engine supports traditional rule-based matching (e.g., "more than 10 errors in 5 minutes") and also utilizes AI to discover unknown anomaly patterns, helping you get alerted before issues escalate.
3. **Open Source & Simplicity**: As an open-source project, Log-Lens is free to use and deploy. We are committed to providing a lightweight, simple, and easy-to-use architecture to lower the barrier to log analysis.
4. **Local-First AI**: Privacy is a core design principle of Log-Lens. It prioritizes running local LLMs on your own servers via tools like Ollama, ensuring your log data remains within your own infrastructure.

## Core Features
- **Pluggable Log Collection**:
  - A high-performance, low-resource client developed in Go.
  - Natively cross-platform, capable of monitoring files, Windows Event Logs, Journald, and more.
  - Easily extendable to support new log types through a unified factory pattern.

- **Real-time AI Analysis**:
  - The agent securely streams logs to the server in near real-time.
  - The server invokes AI models to analyze, classify, and tag the log stream.
  - Natively supports local models via Ollama, while also remaining compatible with cloud APIs like OpenAI.
- **Intelligent Alerting Engine**:
  - Rule-Driven: Flexible custom configurations to handle known alert scenarios.
  - AI-Driven: Intelligently discovers unexpected behaviors that deviate from normal patterns.
  - Supports sending AI-summarized alert notifications through various channels like Webhooks, Slack, and Email.
- **User-Friendly Dashboard**:
  - Visually presents system health status, anomaly trends, and AI-generated digests through a clean web interface.
  - Provides powerful search and filtering capabilities for structured logs.

## Get Involved
Log-Lens is currently in the active, early stages of development.

We welcome all forms of feedback and contributions. If you have any ideas or suggestions for the project, or if you've found a bug, please feel free to open an issue on  [GitHub Issues](https://www.google.com/search?q=https://github.com/chrisis58/log-lens/issues).

As the core architecture stabilizes, we will publish a detailed contribution guide.

Let's build a practical and intelligent log analysis tool together!