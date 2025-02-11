package application;

import org.json.JSONArray;
import org.json.JSONObject;
import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClimaAPI {

    private static final String CHAVE_API = "1b8011be68237c8bbbc70fe9ecca7722"; // Chave de API para autenticação
    private static final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast?"; // URL base da API

    /**
     * Método para buscar os dados climáticos de uma cidade específica.
     * Os dados são processados em uma thread separada para evitar travamento da interface gráfica.
     * 
     * @param cidade   Nome da cidade para a busca
     * @param callback Interface de callback para retornar os dados
     */
    public static void buscarClima(String cidade, ClimaCallback callback) {
        new Thread(() -> {
            try {
                // Monta a URL da requisição com a cidade e a chave da API
                String urlString = URL_BASE + "q=" + cidade + "&appid=" + CHAVE_API + "&units=metric";
                URL url = new URL(urlString);
                HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                conexao.setRequestMethod("GET");

                // Lê a resposta da API
                BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                StringBuilder resposta = new StringBuilder();
                String linha;
                while ((linha = leitor.readLine()) != null) {
                    resposta.append(linha);
                }
                leitor.close();

                // Converte a resposta JSON
                JSONObject respostaJson = new JSONObject(resposta.toString());
                JSONObject cidadeObj = respostaJson.getJSONObject("city");
                JSONArray lista = respostaJson.getJSONArray("list");

                // Obtém informações básicas da cidade
                String nomeCidade = cidadeObj.getString("name");
                String paisCidade = cidadeObj.getString("country");
                double latitude = cidadeObj.getJSONObject("coord").getDouble("lat");
                double longitude = cidadeObj.getJSONObject("coord").getDouble("lon");

                // Listas para armazenar temperaturas médias de diferentes partes do dia
                List<Double> temperaturasNoite = new ArrayList<>();
                List<Double> temperaturasManha = new ArrayList<>();
                List<Double> temperaturasTarde = new ArrayList<>();

                // Itera sobre as previsões de tempo retornadas pela API
                for (int i = 0; i < lista.length(); i++) {
                    JSONObject dadosTempo = lista.getJSONObject(i);
                    long timestamp = dadosTempo.getLong("dt");
                    Date dataPrevisao = new Date(timestamp * 1000); // Converte o timestamp em data
                    SimpleDateFormat formatoHora = new SimpleDateFormat("HH");
                    int hora = Integer.parseInt(formatoHora.format(dataPrevisao));

                    // Obtém os dados climáticos principais
                    JSONObject dadosPrincipais = dadosTempo.getJSONObject("main");
                    JSONArray clima = dadosTempo.getJSONArray("weather");
                    JSONObject condicaoClima = clima.getJSONObject(0);
                    JSONObject dadosVento = dadosTempo.getJSONObject("wind");
                    JSONObject dadosNuvens = dadosTempo.getJSONObject("clouds");
                    JSONObject dadosSistema = dadosTempo.optJSONObject("sys");
                    double temperatura = dadosPrincipais.getDouble("temp");

                    // Traduz a descrição do clima para português
                    String descricaoClima = traduzirCondicaoClima(condicaoClima.getString("description"));

                    // Classifica a temperatura com base no horário do dia
                    if (hora >= 0 && hora < 6) {
                        temperaturasNoite.add(temperatura);
                    } else if (hora >= 6 && hora < 12) {
                        temperaturasManha.add(temperatura);
                    } else if (hora >= 12 && hora < 18) {
                        temperaturasTarde.add(temperatura);
                    }

                    // Passa os dados climáticos para a interface gráfica via callback
                    Platform.runLater(() -> callback.onDadosClimaRecebidos(
                            nomeCidade, paisCidade,
                            dadosPrincipais.getDouble("temp"),
                            dadosPrincipais.getDouble("feels_like"),
                            dadosPrincipais.getDouble("temp_min"),
                            dadosPrincipais.getDouble("temp_max"),
                            dadosPrincipais.getInt("pressure"),
                            dadosPrincipais.getInt("humidity"),
                            descricaoClima,
                            dadosVento.getDouble("speed"),
                            dadosVento.optDouble("gust", 0),
                            dadosNuvens.getInt("all"),
                            dadosTempo.getInt("visibility"),
                            latitude,
                            longitude,
                            dadosTempo.optDouble("pop", 0) * 100,
                            dadosTempo.optJSONObject("rain"),
                            dadosSistema
                    ));
                }

                // Calcula médias das temperaturas por período do dia e envia via callback
                Platform.runLater(() -> callback.onMédiaTemperaturas(
                        calcularMediaTemperatura(temperaturasNoite),
                        calcularMediaTemperatura(temperaturasManha),
                        calcularMediaTemperatura(temperaturasTarde)
                ));

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> callback.onErro("Erro ao buscar clima"));
            }
        }).start();
    }
    
    // Traduz a descrição das condições climáticas
    private static String traduzirCondicaoClima(String descricao) {
        switch (descricao.toLowerCase()) {
            case "light rain": return "Chuva Leve";
            case "moderate rain": return "Chuva Moderada";
            case "heavy rain": return "Chuva Forte";
            case "clear sky": return "Céu Claro";
            case "few clouds": return "Poucas Nuvens";
            case "scattered clouds": return "Nuvens Espalhadas";
            case "broken clouds": return "Nuvens Parcialmente Cobertas";
            case "overcast clouds": return "Nuvens Espessas";
            case "thunderstorm": return "Tempestade";
            case "snow": return "Neve";
            case "mist": return "Névoa";
            default: return descricao;
        }
    }

    // Calcula a média das temperaturas de uma lista e arredonda para o inteiro mais próximo
    private static String calcularMediaTemperatura(List<Double> temperaturas) {
        if (temperaturas.isEmpty()) return "N/A";
        double soma = 0;
        for (double temp : temperaturas) soma += temp;
        return String.format("%.0f", soma / temperaturas.size());
    }

    // Interface de callback para retorno dos dados climáticos
    public interface ClimaCallback {
        void onDadosClimaRecebidos(String cidade, String pais, double temperatura, double feelsLike, double tempMin, double tempMax, int pressure, int umidade,
                                   String descricao, double vento, double rajadaVento, int coberturaNuvens, int visibilidade, double lat, double lon,
                                   double chancePrecipitacao, JSONObject dadosChuva, JSONObject dadosSistema);
        void onErro(String mensagemErro);
        void onMédiaTemperaturas(String mediaNoite, String mediaManha, String mediaTarde);
    }
}