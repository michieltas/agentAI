package nl.mihaly.main;

import javax.swing.*;
import java.awt.*;

public class GUI {

    public static final String MODEL = "phi4";

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

        // FRAME
        JFrame frame = new JFrame("Windows AI Agent");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 650);
        frame.setLayout(new BorderLayout());

        DISCLAIMER.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // INPUT
        JTextArea input = new JTextArea(5, 50);
        input.setFont(new Font("Consolas", Font.PLAIN, 14));
        JScrollPane inputScroll = new JScrollPane(input);
        inputScroll.setBorder(BorderFactory.createTitledBorder("Your Request"));
        inputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // OUTPUT
        JTextArea output = new JTextArea(15, 50);
        output.setFont(new Font("Consolas", Font.PLAIN, 14));
        output.setEditable(false);
        output.setLineWrap(false);          // stap 1: geen wrapping
        output.setWrapStyleWord(false);     // stap 1: geen word-wrap
        JScrollPane outputScroll = new JScrollPane(output);
        outputScroll.setBorder(BorderFactory.createTitledBorder("Output"));
        outputScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 350));

        // BUTTON
        JButton run = new JButton("Generate Script");
        run.setFont(new Font("SansSerif", Font.BOLD, 14));
        run.setAlignmentX(Component.CENTER_ALIGNMENT);

        // BACKEND
        OllamaClient client = new OllamaClient(System.out::println);
        CommandInterpreter interpreter = new CommandInterpreter(client, MODEL);
        PowerShellExecutor executor = new PowerShellExecutor();
        AgentController controller = new AgentController(interpreter, executor);

        // ACTION
        run.addActionListener(e -> {

            String userCmd = input.getText().trim();
            if (userCmd.isEmpty()) {
                output.setText("Please enter a request.");
                return;
            }

            JDialog waitDialog = new JDialog(frame, "Please wait…", true);
            waitDialog.setLayout(new BorderLayout());
            waitDialog.add(new JLabel("Processing request…", SwingConstants.CENTER), BorderLayout.CENTER);
            waitDialog.setSize(200, 100);
            waitDialog.setLocationRelativeTo(frame);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                String scriptOrWarning;

                @Override
                protected Void doInBackground() {
                    scriptOrWarning = controller.handleUserCommand(userCmd);
                    return null;
                }

                @Override
                protected void done() {
                    waitDialog.dispose();

                    if (scriptOrWarning.startsWith("⚠️")) {
                        // tabs → spaties voor nette uitlijning
                        output.setText(scriptOrWarning.replace("\t", "    "));
                        return;
                    }

                    int confirm = JOptionPane.showConfirmDialog(
                            frame,
                            "Generated PowerShell script:\n\n" + scriptOrWarning + "\n\nExecute this?",
                            "Confirm Execution",
                            JOptionPane.YES_NO_OPTION
                    );

                    if (confirm == JOptionPane.YES_OPTION) {
                        String result = controller.executeApprovedScript(scriptOrWarning);
                        // stap 2: tabs → spaties
                        output.setText(result.replace("\t", "    "));
                    } else {
                        output.setText("Execution cancelled.");
                    }
                }
            };

            worker.execute();
            waitDialog.setVisible(true);
        });

        // LAYOUT
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        centerPanel.add(inputScroll);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(run);
        centerPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        centerPanel.add(outputScroll);

        frame.add(DISCLAIMER, BorderLayout.NORTH);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}
