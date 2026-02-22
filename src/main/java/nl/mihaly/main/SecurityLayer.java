package nl.mihaly.main;

import java.util.List;

public class SecurityLayer {

    // Commando’s die NOOIT automatisch uitgevoerd mogen worden
    private static final List<String> DENYLIST = List.of(
            "Remove-Item",
            "rm ",
            "Format-Volume",
            "Stop-Process",
            "Set-ExecutionPolicy",
            "New-LocalUser",
            "Disable-LocalUser",
            "Clear-Content",
            "Remove-Item -Recurse C:\\",
            "Remove-Item -Recurse /"
    );

    // Commando’s die we wél toestaan
    private static final List<String> ALLOWLIST = List.of(
            "copy-item",
            "new-item",
            "get-process",
            "start-process",
            "get-childitem",
            "move-item",
            "get-service",
            "restart-service",
            "get-content",
            "set-location"
    );

    public SecurityLayer() {}

    public boolean isSafe(String script) {
        if (script == null || script.isBlank()) {
            return false;
        }

        String lower = script.toLowerCase();

        // Check denylist
        for (String deny : DENYLIST) {
            if (lower.contains(deny.toLowerCase())) {
                return false;
            }
        }

        // Check allowlist: minstens één veilig commando moet aanwezig zijn
        boolean containsAllowed = ALLOWLIST.stream()
                .anyMatch(a -> lower.contains(a.toLowerCase()));

        return containsAllowed;
    }

    public String explainRisk(String script) {
        String lower = script.toLowerCase();

        for (String deny : DENYLIST) {
            if (lower.contains(deny.toLowerCase())) {
                return "Blocked: script contains forbidden command: " + deny;
            }
        }

        return "Script rejected: no allowed commands detected.";
    }
}
