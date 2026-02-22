package nl.mihaly.main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandInterpreter {

    private final OllamaClient client;
    private final ObjectMapper mapper = new ObjectMapper();
    private final String model;

    // Regex om de response-string veilig uit kapotte JSON te halen
    private static final Pattern RESPONSE_PATTERN =
            Pattern.compile("\"response\"\\s*:\\s*\"([\\s\\S]*?)\"", Pattern.MULTILINE);

    public CommandInterpreter(OllamaClient client, String model) {
        this.client = client;
        this.model = model;
    }

    public String interpret(String userInput) {
        String raw = "";

        try {
            // 1. Prompt bouwen
            String prompt = Tekst.buildUserPrompt(userInput);

            // 2. Call naar Ollama
            raw = client.call(model, prompt);

            // 3. Probeer eerst regex-extractie (werkt zelfs bij kapotte JSON)
            String extracted = extractResponse(raw);
            if (extracted != null && !extracted.isBlank()) {
                return cleanScript(extracted);
            }

            // 4. Als regex niets vindt, probeer normale JSON parsing
            JsonNode root = mapper.readTree(raw);
            JsonNode responseNode = root.get("response");

            if (responseNode != null && responseNode.isTextual()) {
                return cleanScript(responseNode.asText());
            }

            return "Error: model returned no textual response.\nRaw:\n" + raw;

        } catch (Exception e) {

            // 5. Fallback: regex proberen
            String extracted = extractResponse(raw);

            return """
                    Error parsing model response:
                    %s

                    --- Raw model output ---
                    %s

                    --- Extracted script ---
                    %s
                    """.formatted(
                    e.getMessage(),
                    raw,
                    extracted == null || extracted.isBlank() ? "<empty>" : cleanScript(extracted)
            );
        }
    }

    private String extractResponse(String raw) {
        if (raw == null) return null;

        String key = "\"response\":";
        int start = raw.indexOf(key);
        if (start == -1) return null;

        // Vind eerste quote NA "response":
        int firstQuote = raw.indexOf('"', start + key.length());
        if (firstQuote == -1) return null;

        // Zoek afsluitende quote gevolgd door een komma
        int endQuote = raw.indexOf("\",", firstQuote + 1);
        if (endQuote == -1) return null;

        String extracted = raw.substring(firstQuote + 1, endQuote);

        return cleanScript(extracted);
    }


    // Schoonmaken van script
    private String cleanScript(String s) {
        if (s == null) return "";

        // Strip leading rommel
        s = s.replaceAll("^[\\n\\r\\s\"]+", "");

        // Strip trailing quotes
        s = s.replaceAll("[\"]+$", "");

        // Strip markdown
        s = s.replace("```powershell", "")
                .replace("```PowerShell", "")
                .replace("```ps", "")
                .replace("```", "");

        return s.trim();
    }
}
