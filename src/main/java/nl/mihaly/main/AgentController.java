package nl.mihaly.main;

public class AgentController {

    private final CommandInterpreter interpreter;
    private final PowerShellExecutor executor;
    private final SecurityLayer security;

    public AgentController(CommandInterpreter interpreter, PowerShellExecutor executor) {
        this.interpreter = interpreter;
        this.executor = executor;
        this.security = new SecurityLayer();
    }

    public String handleUserCommand(String userInput) {

        // 1. Vraag DeepSeek om pure PowerShell
        String psScript = interpreter.interpret(userInput);

        // 2. Trim whitespace
        psScript = psScript == null ? "" : psScript.trim();

        // 3. Security check
        if (!security.isSafe(psScript)) {
            return """
                    ⚠️ Script rejected by security layer

                    Reason:
                    %s

                    --- Generated script ---
                    %s
                    ------------------------
                    """.formatted(
                    security.explainRisk(psScript),
                    psScript.isBlank() ? "<empty script>" : psScript
            );
        }

        // 4. Alles ok → geef script terug aan GUI
        return psScript;
    }

    public String executeApprovedScript(String script) {
        return executor.execute(script);
    }
}
