package application;

import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class TelaAplicativo extends Application {

    private Label cidadeLabel = new Label("Cidade: ");
    private Label temperaturaLabel = new Label("Temperatura: ");
    private Label condicaoLabel = new Label("Condição: ");
    private Label umidadeLabel = new Label("Umidade: ");
    private Label pressaoLabel = new Label("Pressão: ");
    private Label ventoLabel = new Label("Vento: ");
    private Label visibilidadeLabel = new Label("Visibilidade: ");
    private Label nuvensLabel = new Label("Nuvens: ");
    private Label latitudeLabel = new Label("Latitude: ");
    private Label longitudeLabel = new Label("Longitude: ");
    private Label descricaoLabel = new Label("Descrição: ");
    private Label feelslikeLabel = new Label("Sensação Térmica: ");
    private Label tempmanhLabel = new Label("Temperatura Manhã: ");
    private Label temptardLabel = new Label("Temperatura Tarde: ");
    private Label tempnoitLabel = new Label("Temperatura Noite: ");

    @Override
    public void start(Stage stage) {
    	cidadeLabel.getStyleClass().addAll("label-clima", "cidade");
    	temperaturaLabel.getStyleClass().addAll("label-clima", "temperatura");
    	condicaoLabel.getStyleClass().addAll("label-clima", "condicao");
    	umidadeLabel.getStyleClass().addAll("label-clima", "umidade");
    	pressaoLabel.getStyleClass().addAll("label-clima", "pressao");
    	ventoLabel.getStyleClass().addAll("label-clima", "vento");
    	visibilidadeLabel.getStyleClass().addAll("label-clima", "visibilidade");
    	nuvensLabel.getStyleClass().addAll("label-clima", "nuvens");
    	latitudeLabel.getStyleClass().addAll("label-clima", "latitude");
    	longitudeLabel.getStyleClass().addAll("label-clima", "longitude");
    	descricaoLabel.getStyleClass().addAll("label-clima", "descricao");
    	feelslikeLabel.getStyleClass().addAll("label-clima", "feelslike");
    	tempmanhLabel.getStyleClass().addAll("label-clima", "tempmanh");
    	temptardLabel.getStyleClass().addAll("label-clima", "temptard");
    	tempnoitLabel.getStyleClass().addAll("label-clima", "tempnoit");

        // Criando os quadrados e associando as classes CSS
        Rectangle previsaoHoje = new Rectangle(400, 500);
        previsaoHoje.getStyleClass().add("previsaohoje");

        Rectangle climaSimples = new Rectangle(400, 200);
        climaSimples.getStyleClass().add("climasimples");

        Rectangle climaDetalhado = new Rectangle(400, 500);
        climaDetalhado.getStyleClass().add("climadetalhado");

        // Criando a logo
        Image logo = new Image("file:../App/src/images/logo.png");
        ImageView logoView = new ImageView(logo);
        logoView.getStyleClass().add("foto");
        logoView.setPreserveRatio(true);
        logoView.setFitWidth(300);
        logoView.setFitHeight(200);
        
        HBox logoBox = new HBox(10);
        logoBox.getChildren().add(logoView);
        logoBox.setAlignment(Pos.TOP_CENTER); // Centraliza a logo dentro do HBox

        // Criando a caixa de pesquisa (TextField)
        TextField pesquisaField = new TextField();
        pesquisaField.setPromptText("Buscar Cidade");
        pesquisaField.getStyleClass().add("busca");
        pesquisaField.setOnAction(e -> buscarDadosClima(pesquisaField.getText().trim().replace(" ", "+")));

        // Criando a HBox para a caixa de pesquisa no topo
        HBox pesquisaBox = new HBox(10);
        pesquisaBox.getChildren().addAll(pesquisaField);
        pesquisaBox.setAlignment(Pos.CENTER);
        pesquisaBox.getStyleClass().add("pesquisa-box");

        // Criando VBox para exibir as informações do clima
        VBox infoClimaDetalhado = new VBox(10, feelslikeLabel, condicaoLabel, umidadeLabel, ventoLabel, pressaoLabel,
                visibilidadeLabel, nuvensLabel, latitudeLabel, longitudeLabel);
        infoClimaDetalhado.setAlignment(Pos.CENTER);
        VBox infoClimaSimples = new VBox(10, cidadeLabel, temperaturaLabel);
        infoClimaSimples.setAlignment(Pos.CENTER);
        VBox infoClimaHoje = new VBox(10, tempmanhLabel, temptardLabel, tempnoitLabel);
        infoClimaHoje.setAlignment(Pos.CENTER);

        // Criando um StackPane para sobrepor infoClima dentro do climaSimples
        StackPane climaDetalhadoPane = new StackPane(climaDetalhado, infoClimaDetalhado);
        climaDetalhadoPane.setAlignment(Pos.TOP_CENTER);

        StackPane previsaoHojePane = new StackPane(previsaoHoje, infoClimaHoje);
        previsaoHojePane.setAlignment(Pos.TOP_CENTER);

        StackPane climaSimplesPane = new StackPane(climaSimples, infoClimaSimples);
        climaSimplesPane.setAlignment(Pos.TOP_CENTER);

        // Criando a VBox para centralizar climaSimples e possibilidadeChuva verticalmente
        VBox vbox = new VBox(10);
        vbox.getChildren().addAll(climaSimplesPane);
        vbox.getStyleClass().add("vbox");
        vbox.setAlignment(Pos.CENTER);

        // Criando a HBox para alinhar previsaoHoje, vbox e climaDetalhado
        HBox hbox = new HBox(10);
        hbox.getChildren().addAll(previsaoHojePane, vbox, climaDetalhadoPane);
        hbox.setAlignment(Pos.CENTER);
        hbox.getStyleClass().add("hbox");

        // Criando a VBox para organizar a logo, a pesquisa e os painéis de clima
        VBox root = new VBox(20);
        root.getChildren().addAll(logoBox, pesquisaBox, hbox);
        root.setAlignment(Pos.TOP_CENTER); // Centraliza logo e os outros elementos no topo
        root.setSpacing(20); // Espaço entre a logo, pesquisa e os painéis

        // Configurando a cena com a VBox que contém todos os elementos
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("TelaApp.css").toExternalForm());

        stage.setResizable(false); // Impede o redimensionamento da janela
        stage.setMaximized(true); // A janela será maximizada
        stage.setTitle("Tela Principal");
        stage.setScene(scene);
        stage.show();
    }

    private void buscarDadosClima(String cidade) {
        // Imprimir no console quando a busca for realizada
        System.out.println("Buscando dados de clima para a cidade: " + cidade);

        ClimaAPI.ClimaCallback callback = new ClimaAPI.ClimaCallback() {
            @Override
            public void onDadosClimaRecebidos(String cidade, String pais, double temperatura, double feelsLike, 
                    double tempMin, double tempMax, int pressure, int umidade,
                    String descricao, double vento, double rajadaVento,
                    int coberturaNuvens, int visibilidade, double lat,
                    double lon, double chancePrecipitacao,
                    JSONObject dadosChuva, JSONObject dadosSistema) {
                // Imprimir os dados recebidos
                System.out.println("Dados de clima recebidos:");
                System.out.println("Cidade: " + cidade);
                System.out.println("Temperatura: " + temperatura + "°C");
                System.out.println("Umidade: " + umidade + "%");
                System.out.println("Vento: " + vento + " m/s");
                System.out.println("Pressão: " + pressure + " hPa");
                System.out.println("Descrição: " + descricao);
                System.out.println("Visibilidade: " + visibilidade + " m");
                System.out.println("Nuvens: " + coberturaNuvens + "%");
                System.out.println("Latitude: " + lat);
                System.out.println("Longitude: " + lon);

                // Atualizando as labels na interface com os dados recebidos
                atualizarLabels(cidade, temperatura, descricao, umidade, pressure, vento, visibilidade, feelsLike, coberturaNuvens, lat, lon);
            }

            @Override
            public void onErro(String mensagemErro) {
                // Imprimir erro, caso ocorra
                System.out.println("Erro ao buscar dados de clima: " + mensagemErro);
            }

            @Override
            public void onMédiaTemperaturas(String mediaNoite, String mediaManha, String mediaTarde) {
                tempmanhLabel.setText("Temperatura Manhã: " + mediaManha);
                temptardLabel.setText("Temperatura Tarde: " + mediaTarde);
                tempnoitLabel.setText("Temperatura Noite: " + mediaNoite);
                
                System.out.println("Media Noite: " + mediaNoite);
                System.out.println("Média Tarde: " + mediaTarde);
                System.out.println("Media Manhã: " + mediaManha);
            }
        };

        // Chama o método da API para buscar os dados de clima
        ClimaAPI.buscarClima(cidade, callback);
    }

    private void atualizarLabels(String cidade, double temperatura, String descricao, double umidade, double pressao,
                                 double vento, int visibilidade, double feelsLike, int nuvens, double lat, double lon) {
        javafx.application.Platform.runLater(() -> {
            cidadeLabel.setText("Cidade: " + cidade);
            temperaturaLabel.setText("Temperatura: " + temperatura + "°C");
            condicaoLabel.setText("Condição: " + descricao);
            umidadeLabel.setText("Umidade: " + umidade + "%");
            pressaoLabel.setText("Pressão: " + pressao + " hPa");
            ventoLabel.setText("Vento: " + vento + " m/s");
            visibilidadeLabel.setText("Visibilidade: " + visibilidade + " m");
            nuvensLabel.setText("Nuvens: " + nuvens + "%");
            latitudeLabel.setText("Latitude: " + lat);
            longitudeLabel.setText("Longitude: " + lon);
            feelslikeLabel.setText("Sensação Térmica: " + feelsLike + "°C");
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
