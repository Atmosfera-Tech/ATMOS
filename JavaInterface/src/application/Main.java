package application;

import javafx.application.Application;
import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Cria o layout principal da tela de login
            StackPane stackPane = new StackPane();
            VBox vbox = new VBox(15); // Define o espaçamento entre os elementos
            vbox.setAlignment(Pos.CENTER); // Centraliza os elementos na tela
            vbox.getStyleClass().add("vbox"); // Adiciona classe CSS para estilização

            // Carrega a imagem do logotipo
            Image logo = new Image("file:../App/src/images/logo.png");
            ImageView logoView = new ImageView(logo);
            logoView.getStyleClass().add("logo");
            logoView.setFitWidth(300);  // Define a largura da imagem do logo
            logoView.setFitHeight(300); // Define a altura da imagem do logo

            // Carrega a imagem decorativa da tela de login
            Image loginImage = new Image("file:../App/src/images/login.png");
            ImageView loginImageView = new ImageView(loginImage);
            loginImageView.getStyleClass().add("login-image");
            loginImageView.setPreserveRatio(true);  // Mantém a proporção original da imagem
            loginImageView.setFitWidth(400);        // Define a largura da imagem
            loginImageView.setFitHeight(300);       // Define a altura da imagem

            // Cria o campo de entrada para o e-mail
            TextField emailField = new TextField();
            emailField.setPromptText("E-mail"); // Define o texto de dica dentro do campo
            emailField.getStyleClass().add("text-field");

            // Cria o campo de entrada para a senha
            PasswordField senhaField = new PasswordField();
            senhaField.setPromptText("Senha"); // Define o texto de dica dentro do campo
            senhaField.getStyleClass().add("password-field");

            // Cria a label para exibição de mensagens de erro
            Label mensagemLabel = new Label();
            mensagemLabel.getStyleClass().add("mensagem-label");
            mensagemLabel.setVisible(false); // Inicialmente oculta a mensagem

            // Cria o botão de confirmação
            Button confirmarButton = new Button("Confirmar");
            confirmarButton.getStyleClass().add("confirmar-button");

            // Define a ação do botão de login
            confirmarButton.setOnAction(e -> {
                String email = emailField.getText(); // Obtém o texto digitado no campo de e-mail
                String senha = senhaField.getText(); // Obtém o texto digitado no campo de senha

                // Verifica se as credenciais são válidas
                if (email.equals("oi") && senha.equals("oi")) {
                    new TelaAplicativo().start(new Stage()); // Abre a tela principal do aplicativo
                    primaryStage.close(); // Fecha a tela de login
                } else {
                    // Exibe mensagem de erro quando as credenciais estão incorretas
                    mensagemLabel.setText("E-mail ou senha incorretos");
                    mensagemLabel.getStyleClass().add("mensagem-erro"); // Aplica o estilo de erro

                    emailField.clear(); // Limpa o campo de e-mail
                    senhaField.clear(); // Limpa o campo de senha
                }

                mensagemLabel.setVisible(true); // Torna a mensagem visível

                // Define um tempo para ocultar a mensagem automaticamente após 5 segundos
                PauseTransition pause = new PauseTransition(Duration.seconds(5));
                pause.setOnFinished(event -> mensagemLabel.setVisible(false));
                pause.play();
            });

            // Cria um espaçador entre os elementos
            Region spacer = new Region();
            spacer.setPrefHeight(10); // Define a altura do espaçador

            // Cria o rótulo de direitos autorais
            Label copyrightLabel = new Label("© 2025 ATMOS. Todos os direitos reservados.");
            copyrightLabel.getStyleClass().add("copyright-label");

            // Adiciona os elementos ao layout
            vbox.getChildren().addAll(logoView, loginImageView, mensagemLabel, emailField, senhaField, spacer, confirmarButton, copyrightLabel);
            stackPane.getChildren().add(vbox);

            // Cria a cena principal e aplica a folha de estilos CSS
            Scene scene = new Scene(stackPane);
            scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

            // Configura a janela principal
            primaryStage.setTitle("Tela de Login"); // Define o título da janela
            primaryStage.getIcons().add(new Image("file:../App/src/images/logojava.png")); // Define o ícone da janela
            primaryStage.setScene(scene); // Define a cena na janela
            primaryStage.setMaximized(true); // Inicia a tela maximizada
            primaryStage.show(); // Exibe a janela
        } catch (Exception e) {
            e.printStackTrace(); // Exibe erros no console
        }
    }

    public static void main(String[] args) {
        launch(args); // Inicia a aplicação JavaFX
    }
}
