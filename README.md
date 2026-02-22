# agentAI

⚠️ WARNING — USE AT YOUR OWN RISK

This software allows an LLM (and potentially any agent, person, or process) to generate and execute PowerShell commands.

WE ARE NOT IN ANY WAY LIABLE FOR OR RESPONSIBLE FOR ANY HARM, DAMAGE, DATA LOSS, SECURITY ISSUES, OR UNINTENDED ACTIONS RESULTING FROM THE USE OF THIS SOFTWARE — INCLUDING, BUT NOT LIMITED TO, ACTIONS PERFORMED THROUGH THE POTENTIALLY HARMFUL POWERSHELL ENVIRONMENT.

You are fully responsible for reviewing, validating, and approving all generated scripts before execution.



## Requirements

To run **agentAI**, the following components are required:

### 1. Java Runtime
- **Java 17 or higher**
- Make sure `java` is available in your system PATH.

### 2. Ollama
- Install **Ollama** from: https://ollama.com
- Ensure the Ollama service is running locally.
- The application expects a local model named **phi4** (or adjust the model name in the source code).

### 3. PowerShell (Windows only)
- This application executes PowerShell commands.
- Windows PowerShell 5.1 or PowerShell 7+ is required.
- PowerShell must be available in your PATH as `powershell` or `pwsh`.

### 4. Network Access (Localhost)
- The application communicates with Ollama via `http://localhost:11434`.
- No external network access is required.

### 5. Permissions
- You must have permission to execute PowerShell scripts on your system.
- Some commands may require elevated privileges depending on what the LLM generates.

---

## Optional (but recommended)

### • Git
To clone the repository and contribute:
- Install Git from https://git-scm.com

### • IDE
Any Java IDE works:
- IntelliJ IDEA
- Eclipse
- VS Code with Java extensions

---

## Important Notes

- This software allows an LLM to generate and execute PowerShell commands.
- **Use at your own risk.**
- Always review generated scripts before executing them.
