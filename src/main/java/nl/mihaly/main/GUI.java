package nl.mihaly.main;

import javax.swing.*;
import java.awt.*;

public class GUI {

    public static final String MODEL = "phi4";

    // ---------------------------
    // DISCLAIMER (HTML formatted)
    // ---------------------------
    public static final JLabel DISCLAIMER = new JLabel(
            "<html>" +
                    "<div style='color:red; font-weight:bold; font-size:13px;'>⚠️ WARNING — USE AT YOUR OWN RISK</div>" +
                    "<div style='font-size:11px; margin-top:4px;'>" +
                    "This software allows an LLM (and potentially any agent, person, or process) to generate and execute PowerShell commands.<br>" +
                    "<b>We are not liable for any harm, damage, data loss, security issues, or unintended actions</b> resulting from its use — including actions performed through the potentially harmful PowerShell environment.<br>" +
                    "You are fully responsible for reviewing and validating all generated scripts before execution." +
                    "</div>" +
                    "</html>"
    );

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::start);
    }

    private static void start() {

        // ---------------------------
        // FRAME SETUP
        // ---------------------------
        JFrame frame = new JFrame("Windows AI Agent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());

        DISCLAIMER.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ---------------------------
        // INPUT AREA
        // ---------------------------
        JTextArea input = new JTextArea(5, 50);
        input.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Your Request"));

        // ---------------------------
        // OUTPUT AREA
        // ---------------------------
        JTextArea output = new JTextArea(15, 50);
        output.setFont(new Font("Consolas", Font.PLAIN, 14));
        output.setEditable(false);
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));

        // ---------------------------
        // BUTTON
        // ---------------------------
        JButton run = new JButton("Generate Script");
        run.setFont(new Font("SansSerif", Font.BOLD, 14));

        // ---------------------------
        // BACKEND SETUP
        // ---------------------------
        OllamaClient client = new OllamaClient(System.out::println);
        CommandInterpreter interpreter = new CommandInterpreter(client, MODEL);
        PowerShellExecutor executor = new PowerShellExecutor();
        AgentController controller = new AgentController(interpreter, executor);

        // ---------------------------
        // BUTTON ACTION
        // ---------------------------
        run.addActionListener(e -> {
            String userCmd = input.getText().trim();
            if (userCmd.isEmpty()) {
                output.setText("Please enter a request.");
                return;
            }

            String scriptOrWarning = controller.handleUserCommand(userCmd);

            // If unsafe → show warning
            if (scriptOrWarning.startsWith("⚠️")) {
                output.setText(scriptOrWarning);
                return;
            }

            // Ask confirmation
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Generated PowerShell script:\n\n" + scriptOrWarning + "\n\nExecute this?",
                    "Confirm Execution",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                String result = controller.executeApprovedScript(scriptOrWarning);
                output.setText(result);
            } else {
                output.setText("Execution cancelled.");
            }
        });

        // ---------------------------
        // MAIN PANEL LAYOUT
        // ---------------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(inputScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        centerPanel.add(run);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
        centerPanel.add(outputScroll);

        // ---------------------------
        // ADD COMPONENTS TO FRAME
        // ---------------------------
        frame.add(DISCLAIMER, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
