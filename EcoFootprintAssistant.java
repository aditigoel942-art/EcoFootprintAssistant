import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class EcoFootprintAssistant extends JFrame {
    private JTextField carKmField, electricityField, flightsField;
    private JComboBox<String> dietBox;
    private JTextArea resultArea, tipArea;
    private JTextArea chatArea;
    private JTextField chatInput;

    private Map<String, String> responses = new HashMap<>();

    // Emission factors (approx)
    private static final double CAR_KM_EMISSION = 0.12;       // kg CO₂ per km
    private static final double ELECTRICITY_KWH_EMISSION = 0.82; // kg CO₂ per kWh
    private static final double FLIGHT_EMISSION = 250;        // kg per flight
    private static final double DIET_VEG = 1500;              // kg/year
    private static final double DIET_NON_VEG = 2500;          // kg/year

    public EcoFootprintAssistant() {
        super("Eco Footprint Assistant");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(850, 600);
        setLocationRelativeTo(null);

        loadResponses();

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Calculator", createCalculatorPanel());
        tabs.addTab("Eco Chat", createChatPanel());
        tabs.addTab("About", createAboutPanel());

        add(tabs);
    }

    // CALCULATOR PANEL
    private JPanel createCalculatorPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Carbon Footprint Inputs"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        carKmField = new JTextField("");
        electricityField = new JTextField("");
        flightsField = new JTextField("");
        dietBox = new JComboBox<>(new String[]{"Vegetarian", "Non-Vegetarian"});

        gbc.gridx = 0; gbc.gridy = 0; inputPanel.add(new JLabel("Daily Car Travel (km):"), gbc);
        gbc.gridx = 1; inputPanel.add(carKmField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; inputPanel.add(new JLabel("Monthly Electricity (kWh):"), gbc);
        gbc.gridx = 1; inputPanel.add(electricityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; inputPanel.add(new JLabel("Flights per Year:"), gbc);
        gbc.gridx = 1; inputPanel.add(flightsField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; inputPanel.add(new JLabel("Diet Type:"), gbc);
        gbc.gridx = 1; inputPanel.add(dietBox, gbc);

        JButton calcBtn = new JButton("Calculate Footprint");
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        inputPanel.add(calcBtn, gbc);

        resultArea = new JTextArea(8, 40);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane resultScroll = new JScrollPane(resultArea);

        tipArea = new JTextArea(3, 40);
        tipArea.setEditable(false);
        tipArea.setLineWrap(true);
        tipArea.setWrapStyleWord(true);
        tipArea.setBorder(BorderFactory.createTitledBorder("Personalized Tip"));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(resultScroll, BorderLayout.CENTER);
        panel.add(tipArea, BorderLayout.SOUTH);

        calcBtn.addActionListener(e -> calculateFootprint());
        return panel;
    }

    // CHAT PANEL
    private JPanel createChatPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Chat with EcoBot"));

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatArea.append("EcoBot: Hi! Ask me about travel, electricity, or eco-friendly tips.\n\n");

        chatInput = new JTextField();
        JButton sendBtn = new JButton("Send");
        sendBtn.addActionListener(e -> handleChatInput());
        chatInput.addActionListener(e -> handleChatInput());

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.add(chatInput, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        panel.add(chatScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    // ABOUT PANEL
    private JPanel createAboutPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea about = new JTextArea();
        about.setEditable(false);
        about.setFont(new Font("Serif", Font.PLAIN, 16));
        about.setText(
                "Eco Footprint Assistant\n\n" +
                        "This project helps users estimate their annual carbon footprint based on:\n" +
                        "- Daily car travel distance\n" +
                        "- Monthly electricity usage\n" +
                        "- Yearly flight count\n" +
                        "- Type of diet followed\n\n" +
                        "The calculator estimates your CO₂ emissions and provides personalized tips.\n" +
                        "An integrated chat assistant (EcoBot) can give simple eco-friendly advice.\n\n" +
                        "Features:\n" +
                        "• Real-life based problem: Carbon footprint awareness\n" +
                        "• GUI built using Java Swing\n" +
                        "• Chatbot with predefined responses\n\n" +
                        "Developed by: Group No.- 8\n" +
                        "Language: Java (Swing)\n"
        );
        about.setWrapStyleWord(true);
        about.setLineWrap(true);
        panel.add(new JScrollPane(about), BorderLayout.CENTER);
        return panel;
    }

    // CALCULATION LOGIC
    private void calculateFootprint() {
        try {
            double carKm = Double.parseDouble(carKmField.getText().trim());
            double elec = Double.parseDouble(electricityField.getText().trim());
            double flights = Double.parseDouble(flightsField.getText().trim());
            String diet = (String) dietBox.getSelectedItem();

            double carEmission = carKm * CAR_KM_EMISSION * 365;
            double elecEmission = elec * ELECTRICITY_KWH_EMISSION * 12;
            double flightEmission = flights * FLIGHT_EMISSION;
            double foodEmission = diet.equals("Vegetarian") ? DIET_VEG : DIET_NON_VEG;

            double totalKg = carEmission + elecEmission + flightEmission + foodEmission;
            double totalTons = totalKg / 1000.0;

            StringBuilder sb = new StringBuilder();
            sb.append(String.format("Estimated Annual Carbon Footprint: %.2f tons CO₂\n\n", totalTons));
            sb.append("Breakdown (kg/year):\n");
            sb.append(String.format(" • Transport (car + flights): %.2f kg\n", carEmission + flightEmission));
            sb.append(String.format(" • Electricity: %.2f kg\n", elecEmission));
            sb.append(String.format(" • Food: %.2f kg\n\n", foodEmission));

            if (totalTons < 4)
                sb.append("Below global average. Excellent work!\n");
            else if (totalTons < 6)
                sb.append("Slightly above average. You can still reduce emissions.\n");
            else
                sb.append("High footprint. Consider lifestyle changes.\n");

            resultArea.setText(sb.toString());
            tipArea.setText(generateTip(carEmission + flightEmission, elecEmission, foodEmission));

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String generateTip(double transport, double electricity, double food) {
        if (transport > electricity && transport > food)
            return "Transport is your main contributor. Try carpooling, public transport, or cycling.";
        else if (electricity > transport && electricity > food)
            return "Electricity use is high. Use LED bulbs, unplug chargers, and go solar.";
        else
            return "Food contributes most. Try more plant-based meals and reduce food waste.";
    }

    // CHATBOT
    private void loadResponses() {
        responses.put("hello", "Hello! I can help you reduce your carbon footprint. Ask about travel, electricity, or food.");
        responses.put("hi", "Hi there! Try asking: 'how to reduce electricity use' or 'tips for travel'.");
        responses.put("travel", "Consider walking, cycling, or using public transport to reduce emissions.");
        responses.put("car", "Maintain tyre pressure and drive efficiently to reduce fuel usage.");
        responses.put("electricity", "Switch to LED bulbs and turn off appliances when not in use.");
        responses.put("food", "Eat more local and seasonal foods. Reducing meat helps too!");
        responses.put("flight", "Try combining trips or opting for trains over short flights.");
        responses.put("recycle", "Separate waste, compost, and recycle materials whenever possible.");
        responses.put("bye", "Goodbye! Remember, small steps make a big difference");
    }

    private void handleChatInput() {
        String userInput = chatInput.getText().trim();
        if (userInput.isEmpty()) return;

        chatArea.append("You: " + userInput + "\n");
        chatInput.setText("");

        String lower = userInput.toLowerCase();
        String response = null;

        for (String key : responses.keySet()) {
            if (lower.contains(key)) {
                response = responses.get(key);
                break;
            }
        }

        if (response == null)
            response = "I'm not sure about that. Try asking about travel, electricity, food, or recycling.";

        chatArea.append("EcoBot: " + response + "\n\n");
    }

    // SPLASH SCREEN
    private static void showSplash() {
        JWindow splash = new JWindow();
        JLabel label = new JLabel("<html><center><h1>Eco Footprint Assistant</h1><br>Loading...</center></html>", SwingConstants.CENTER);
        label.setFont(new Font("Serif", Font.PLAIN, 18));
        splash.getContentPane().add(label, BorderLayout.CENTER);
        splash.setSize(400, 200);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
        splash.dispose();
    }

    // MAIN
    public static void main(String[] args) {
        showSplash();
        SwingUtilities.invokeLater(() -> new EcoFootprintAssistant().setVisible(true));
    }
}
