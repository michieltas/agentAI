package nl.mihaly.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PowerShellExecutor {

    public String execute(String script) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "powershell.exe",
                    "-NoProfile",
                    "-ExecutionPolicy", "Bypass",
                    "-Command", script
            );
            pb.redirectErrorStream(true);

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            p.waitFor();
            return output.toString();

        } catch (Exception e) {
            return "Error executing PowerShell: " + e.getMessage();
        }
    }
}
