package application;

import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

public class ApiInicial {

    private static final String CHAVE_API = "1b8011be68237c8bbbc70fe9ecca7722"; // sua chave de API
    private static final String URL_BASE = "http://api.openweathermap.org/data/2.5/forecast?";

    public static void main(String[] args) {
        try {
            String nomeCidade = "Fortaleza";
            String urlString = URL_BASE + "q=" + nomeCidade + "&appid=" + CHAVE_API + "&units=metric"; // Define a unidade como Celsius
            URL url = new URL(urlString);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("GET");

            // Obtém a resposta
            BufferedReader leitor = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
            StringBuilder resposta = new StringBuilder();
            String linha;
            while ((linha = leitor.readLine()) != null) {
                resposta.append(linha);
            }
            leitor.close();

            // Parse JSON da resposta
            JSONObject respostaJson = new JSONObject(resposta.toString());
            JSONObject cidade = respostaJson.getJSONObject("city");
            JSONArray lista = respostaJson.getJSONArray("list");

            // Exibe informações básicas da cidade
            System.out.println("Cidade: " + cidade.getString("name"));
            System.out.println("País: " + cidade.getString("country"));

            // Obtém a data atual e a data de amanhã
            SimpleDateFormat formatoData = new SimpleDateFormat("yyyy-MM-dd");
            Date dataAtual = new Date();
            String dataAtualString = formatoData.format(dataAtual);
            dataAtual.setDate(dataAtual.getDate() + 1); // Move para o próximo dia
            String dataAmanhaString = formatoData.format(dataAtual);

            // Listas para armazenar as temperaturas por parte do dia
            List<Double> temperaturasNoite = new ArrayList<>();
            List<Double> temperaturasManha = new ArrayList<>();
            List<Double> temperaturasTarde = new ArrayList<>();
            List<Double> temperaturasNoite2 = new ArrayList<>();

            // Itera sobre as previsões de tempo
            for (int i = 0; i < lista.length(); i++) {
                JSONObject dadosTempo = lista.getJSONObject(i);
                long timestamp = dadosTempo.getLong("dt");
                Date dataPrevisao = new Date(timestamp * 1000); // Converter de segundos para milissegundos
                String dataPrevisaoString = formatoData.format(dataPrevisao); // Formato yyyy-MM-dd

                // Verifica se a previsão é para hoje ou amanhã
                if (dataPrevisaoString.equals(dataAtualString) || dataPrevisaoString.equals(dataAmanhaString)) {
                    SimpleDateFormat formatoHora = new SimpleDateFormat("HH");
                    String horaString = formatoHora.format(dataPrevisao); // Formato de hora "HH"
                    int hora = Integer.parseInt(horaString); // Converte para inteiro

                    // Obtém a temperatura da previsão
                    double temperatura = dadosTempo.getJSONObject("main").getDouble("temp");

                    // Classifica a previsão de acordo com a parte do dia
                    if (hora >= 0 && hora < 6) {
                        temperaturasNoite.add(temperatura);
                    } else if (hora >= 6 && hora < 12) {
                        temperaturasManha.add(temperatura);
                    } else if (hora >= 12 && hora < 18) {
                        temperaturasTarde.add(temperatura);
                    } else {
                        temperaturasNoite2.add(temperatura);
                    }

                    // Exibe as informações detalhadas da previsão
                    JSONObject dadosPrincipais = dadosTempo.getJSONObject("main");
                    JSONArray clima = dadosTempo.getJSONArray("weather");
                    JSONObject condicaoClima = clima.getJSONObject(0);
                    JSONObject dadosVento = dadosTempo.getJSONObject("wind");
                    JSONObject dadosNuvens = dadosTempo.getJSONObject("clouds");
                    JSONObject dadosSistema = dadosTempo.optJSONObject("sys");

                    // Exibe as informações detalhadas
                    System.out.println("\nData e hora: " + dadosTempo.getString("dt_txt"));
                    System.out.println("Temperatura: " + dadosPrincipais.getDouble("temp") + "°C");
                    System.out.println("Sensação Térmica: " + dadosPrincipais.getDouble("feels_like") + "°C");
                    System.out.println("Temperatura Mínima: " + dadosPrincipais.getDouble("temp_min") + "°C");
                    System.out.println("Temperatura Máxima: " + dadosPrincipais.getDouble("temp_max") + "°C");
                    System.out.println("Pressão: " + dadosPrincipais.getInt("pressure") + " hPa");
                    System.out.println("Nível do Mar: " + dadosPrincipais.optInt("sea_level", -1) + " hPa");
                    System.out.println("Nível do Solo: " + dadosPrincipais.optInt("grnd_level", -1) + " hPa");
                    System.out.println("Umidade: " + dadosPrincipais.getInt("humidity") + "%");
                    System.out.println("Fator de Correção da Temperatura: " + dadosPrincipais.optDouble("temp_kf", 0));
                    System.out.println("Condição Climática: " + condicaoClima.getString("description"));
                    System.out.println("Velocidade do Vento: " + dadosVento.getDouble("speed") + " m/s");
                    System.out.println("Rajada de Vento: " + dadosVento.optDouble("gust", 0) + " m/s");
                    System.out.println("Cobertura de Nuvens: " + dadosNuvens.getInt("all") + "%");
                    System.out.println("Visibilidade: " + dadosTempo.getInt("visibility") + " metros");
                    System.out.println("Probabilidade de Precipitação: " + dadosTempo.getDouble("pop") * 100 + "%");

                    // Verifica se há chuva registrada
                    if (dadosTempo.has("rain")) {
                        JSONObject dadosChuva = dadosTempo.getJSONObject("rain");
                        System.out.println("Chuva (última hora): " + dadosChuva.optDouble("1h", 0) + " mm");
                    } else {
                        System.out.println("Chuva: 0 mm");
                    }

                    // Informações do sol
                    if (dadosSistema != null) {
                        long nascerSol = dadosSistema.optLong("sunrise", 0);
                        long porSol = dadosSistema.optLong("sunset", 0);

                        if (nascerSol != 0) {
                            System.out.println("Nascer do Sol: " + formatarTempoUnix(nascerSol));
                        }

                        if (porSol != 0) {
                            System.out.println("Pôr do Sol: " + formatarTempoUnix(porSol));
                        }
                    }
                }
            }

            // Exibe a média arredondada das temperaturas por parte do dia
            System.out.println("\nMédia das temperaturas por parte do dia:");

            System.out.println("Noite (00h - 06h): " + calcularMediaTemperatura(temperaturasNoite));
            System.out.println("Manhã (06h - 12h): " + calcularMediaTemperatura(temperaturasManha));
            System.out.println("Tarde (12h - 18h): " + calcularMediaTemperatura(temperaturasTarde));
            System.out.println("Noite (18h - 00h): " + calcularMediaTemperatura(temperaturasNoite2));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para calcular a média e arredondar
    private static String calcularMediaTemperatura(List<Double> temperaturas) {
        if (temperaturas.isEmpty()) {
            return "N/A";
        }
        double soma = 0;
        for (double temp : temperaturas) {
            soma += temp;
        }
        double media = soma / temperaturas.size();
        return String.format("%.0f", media); // Arredonda para o inteiro mais próximo
    }

    // Método para formatar o horário Unix para um formato legível
    private static String formatarTempoUnix(long tempoUnix) {
        if (tempoUnix == 0) return "N/A";
        Date data = new Date(tempoUnix * 1000);
        SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        return formatoHora.format(data);
    }
}