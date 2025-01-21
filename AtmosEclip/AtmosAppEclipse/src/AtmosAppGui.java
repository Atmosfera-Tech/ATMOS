import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import org.json.simple.JSONObject;

public class AtmosAppGui {

    private JFrame frame;

    public AtmosAppGui() {
        // Configurações da janela principal
        frame = new JFrame("ATMOS - Previsão do Tempo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 650);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        // Painel superior com barra de busca e título "ATMOS"
        JPanel topPanel = new JPanel(new BorderLayout(20, 0));  // Aumentado o espaçamento horizontal
        topPanel.setBackground(new Color(60, 90, 120)); // Azul suave
        topPanel.setPreferredSize(new Dimension(1000, 120));  // Ajustado a altura para caber o título

        JLabel logo = new JLabel("ATMOS", SwingConstants.CENTER);  // Alinhado ao centro
        logo.setFont(new Font("SansSerif", Font.BOLD, 36));
        logo.setForeground(Color.WHITE); // Texto claro para contraste

        JTextField searchField = new JTextField("Digite o nome de uma cidade...");
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.setForeground(Color.BLACK);
        searchField.setBackground(Color.WHITE);

        JButton searchButton = new JButton("Buscar");
        searchButton.setPreferredSize(new Dimension(120, 40));
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 18));
        searchButton.setBackground(new Color(100, 160, 200)); // Azul claro
        searchButton.setForeground(Color.WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));  // Centralizado horizontalmente
        searchPanel.setBackground(new Color(60, 90, 120)); // Azul suave
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        topPanel.add(logo, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        // Painel principal com três divisões
        JPanel mainPanel = new JPanel(new GridLayout(1, 3, 50, 0)); // Aumentado o espaçamento horizontal
        mainPanel.setBackground(new Color(230, 240, 250)); // Fundo cinza claro
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));  // Aumentado o espaçamento

        // Painel esquerdo - Previsão para hoje
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));  // Alinhando verticalmente
        leftPanel.setBackground(new Color(60, 90, 120)); // Azul suave (mesmo tom do fundo "ATMOS")
        leftPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                "Previsão para Hoje",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 16),
                Color.WHITE)); // Texto branco

        // Painel central - Condições climáticas atuais
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 160, 200), 3));

        JLabel tempLabel = new JLabel("25°C", SwingConstants.CENTER);
        tempLabel.setFont(new Font("SansSerif", Font.BOLD, 60));
        tempLabel.setForeground(new Color(60, 90, 120)); // Azul escuro

        JLabel conditionLabel = new JLabel("Céu limpo", SwingConstants.CENTER);
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        conditionLabel.setForeground(new Color(60, 90, 120));

        centerPanel.add(tempLabel, BorderLayout.CENTER);
        centerPanel.add(conditionLabel, BorderLayout.SOUTH);

        // Painel direito - Detalhes climáticos adicionais
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));  // Alinhando verticalmente
        rightPanel.setBackground(new Color(60, 90, 120)); // Azul suave (mesmo tom do fundo "ATMOS")
        rightPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                "Detalhes do Clima",
                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                new Font("SansSerif", Font.BOLD, 16),
                Color.WHITE)); // Texto branco

        mainPanel.add(leftPanel);
        mainPanel.add(centerPanel);
        mainPanel.add(rightPanel);

        // Ação do botão de busca
        searchButton.addActionListener(e -> {
            String city = searchField.getText().trim();
            if (!city.isEmpty()) {
                JSONObject weatherData = AtmosAppApi.getWeatherData(city);
                if (weatherData != null) {
                    try {
                        // Atualiza o painel esquerdo (previsão para hoje)
                        leftPanel.removeAll();
                        double temp = ((Number) weatherData.get("temperature")).doubleValue();
                        double tempMax = ((Number) weatherData.get("temp_max")).doubleValue();
                        double tempMin = ((Number) weatherData.get("temp_min")).doubleValue();

                        leftPanel.add(createCenteredLabel("Manhã: " + String.format("%.1f°C", temp - 3)));
                        leftPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        leftPanel.add(createCenteredLabel("Tarde: " + String.format("%.1f°C", tempMax)));
                        leftPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        leftPanel.add(createCenteredLabel("Noite: " + String.format("%.1f°C", temp - 2)));
                        leftPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        leftPanel.add(createCenteredLabel("Madrugada: " + String.format("%.1f°C", tempMin)));
                        leftPanel.add(Box.createVerticalStrut(40));  // Aumentado o espaçamento entre as linhas
                        leftPanel.revalidate();
                        leftPanel.repaint();

                        // Atualiza o painel central (condição atual)
                        String weatherCondition = (String) weatherData.get("weather_condition");

                        tempLabel.setText(String.format("%.1f°C", temp));
                        conditionLabel.setText(weatherCondition);

                        // Atualiza o painel direito (detalhes adicionais)
                        long humidity = ((Number) weatherData.get("humidity")).longValue();  // Corrigido: casting para long
                        double windSpeed = ((Number) weatherData.get("windspeed")).doubleValue();
                        double pressure = ((Number) weatherData.get("pressure")).doubleValue();
                        double uvIndex = ((Number) weatherData.get("uv_index")).doubleValue(); // Índice UV

                        rightPanel.removeAll();
                        rightPanel.add(createCenteredLabel("Máx: " + String.format("%.1f°C", tempMax) + " / Min: " + String.format("%.1f°C", tempMin)));
                        rightPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        rightPanel.add(createCenteredLabel("Umidade: " + humidity + "%"));
                        rightPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        rightPanel.add(createCenteredLabel("Vento: " + String.format("%.1f km/h", windSpeed)));
                        rightPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        rightPanel.add(createCenteredLabel("Pressão: " + pressure + " hPa"));
                        rightPanel.add(Box.createVerticalStrut(15));  // Aumentado o espaçamento
                        rightPanel.add(createCenteredLabel("Índice UV: " + String.format("%.1f", uvIndex))); // Exibindo UV
                        rightPanel.add(Box.createVerticalStrut(40));  // Aumentado o espaçamento entre as linhas
                        rightPanel.revalidate();
                        rightPanel.repaint();

                        // Atualiza a exibição do clima no painel superior
                        String cityLocation = (String) weatherData.get("location");
                        String countryLocation = (String) weatherData.get("country"); // Obtendo o país

                        // Verifica se o país é nulo e define um valor padrão, caso necessário
                        if (countryLocation == null || countryLocation.isEmpty()) {
                            countryLocation = "";  // Corrigido para não exibir "Desconhecido"
                        }

                        // Mantém o título da janela como "ATMOS" (não é alterado)
                        logo.setText("ATMOS");

                        // Atualiza o título no painel direito
                        rightPanel.setBorder(BorderFactory.createTitledBorder(
                                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                                "Detalhes do Clima",
                                TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION,
                                new Font("SansSerif", Font.BOLD, 16),
                                Color.WHITE)); // Texto branco

                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(frame, "Erro ao processar os dados!", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Cidade não encontrada!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Por favor, insira o nome de uma cidade.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Adiciona os painéis ao frame
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
    }

    // Método para criar JLabel centralizado com fonte preta
    private JLabel createCenteredLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 18));  // Fonte antiga (SansSerif)
        label.setForeground(Color.WHITE); // Fonte preta
        return label;
    }

    // Método para exibir a GUI
    public void display() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AtmosAppGui appGui = new AtmosAppGui();
            appGui.display();
        });
    }
}
