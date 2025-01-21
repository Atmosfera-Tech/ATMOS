import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class AtmosAppApi {

    private static final String API_KEY = "c9ef07505151747c033064bcfb80c6ac"; // Chave de API do OpenWeatherMap
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String UV_URL = "https://api.openweathermap.org/data/2.5/uvi"; // Novo endpoint para índice UV

    public static JSONObject getWeatherData(String locationName) {
        if (locationName == null || locationName.trim().isEmpty()) {
            System.out.println("Erro: O nome da cidade não pode ser vazio.");
            return null;
        }

        try {
            // Codifica o nome da cidade para evitar problemas com espaços e caracteres especiais
            String encodedLocation = URLEncoder.encode(locationName.trim(), StandardCharsets.UTF_8.toString());
            String urlString = BASE_URL + "?q=" + encodedLocation + "&appid=" + API_KEY + "&units=metric&lang=pt";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Erro: Não foi possível conectar à API. Código de resposta: " +
                        (conn != null ? conn.getResponseCode() : "N/A"));
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            // Exibe o JSON completo retornado para ajudar na depuração
            System.out.println("JSON Retornado pela API: " + resultJson);

            // Valida se o JSON retornado não está vazio
            if (resultJson.length() == 0) {
                System.out.println("Erro: Resposta da API está vazia.");
                return null;
            }

            // Analisa o JSON
            JSONObject weatherData = parseWeatherJson(resultJson.toString());
            if (weatherData == null) {
                System.out.println("Erro: Cidade não encontrada ou erro ao analisar os dados.");
                return null;
            }

            // Adiciona o índice UV
            JSONObject uvData = getUvIndex((Double) weatherData.get("latitude"), (Double) weatherData.get("longitude"));
            if (uvData != null) {
                weatherData.put("uv_index", uvData.get("value"));
            }

            return weatherData;

        } catch (Exception e) {
            System.out.println("Erro ao processar os dados da API: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn;
        } catch (IOException e) {
            System.out.println("Erro ao conectar à API: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static JSONObject parseWeatherJson(String json) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject jsonObj = (JSONObject) parser.parse(json);

            // Validação de mensagem de erro
            if (jsonObj.containsKey("message") && "city not found".equals(jsonObj.get("message"))) {
                return null;
            }

            JSONObject main = (JSONObject) jsonObj.get("main");
            JSONArray weatherArray = (JSONArray) jsonObj.get("weather");
            JSONObject wind = jsonObj.containsKey("wind") ? (JSONObject) jsonObj.get("wind") : null;

            // Coleta e validação de dados
            double temperature = main.containsKey("temp") ? ((Number) main.get("temp")).doubleValue() : 0.0;
            double tempMax = main.containsKey("temp_max") ? ((Number) main.get("temp_max")).doubleValue() : 0.0;
            double tempMin = main.containsKey("temp_min") ? ((Number) main.get("temp_min")).doubleValue() : 0.0;
            double feelsLike = main.containsKey("feels_like") ? ((Number) main.get("feels_like")).doubleValue() : 0.0;  // Sensação térmica
            long humidity = main.containsKey("humidity") ? ((Number) main.get("humidity")).longValue() : 0;
            double pressure = main.containsKey("pressure") ? ((Number) main.get("pressure")).doubleValue() : 0.0;

            String weatherCondition = weatherArray != null && !weatherArray.isEmpty()
                    ? (String) ((JSONObject) weatherArray.get(0)).get("description")
                    : "Não disponível";

            double windSpeed = wind != null && wind.containsKey("speed") ? ((Number) wind.get("speed")).doubleValue() : 0.0;

            JSONObject coordinates = jsonObj.containsKey("coord") ? (JSONObject) jsonObj.get("coord") : null;
            double lat = coordinates != null && coordinates.containsKey("lat") ? ((Number) coordinates.get("lat")).doubleValue() : 0.0;
            double lon = coordinates != null && coordinates.containsKey("lon") ? ((Number) coordinates.get("lon")).doubleValue() : 0.0;

            // Monta os dados para retorno
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("feels_like", feelsLike);  // Adiciona sensação térmica
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windSpeed);
            weatherData.put("temp_max", tempMax);
            weatherData.put("temp_min", tempMin);
            weatherData.put("pressure", pressure);
            weatherData.put("latitude", lat);
            weatherData.put("longitude", lon);

            // Modifica para retornar "clima em cidade, estado"
            String city = (String) jsonObj.get("name");
            JSONObject sys = (JSONObject) jsonObj.get("sys");

            // Aqui está a correção para pegar o estado corretamente
            String state = sys.containsKey("state") ? (String) sys.get("state") : (String) sys.get("country");
            weatherData.put("location", city + ", " + (state != null ? state : "Desconhecido")); // Exibe "Cidade, Estado"

            return weatherData;

        } catch (ParseException e) {
            System.out.println("Erro ao analisar o JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Novo método para buscar o índice UV
    private static JSONObject getUvIndex(double lat, double lon) {
        try {
            String urlString = UV_URL + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;
            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn == null || conn.getResponseCode() != 200) {
                System.out.println("Erro: Não foi possível conectar à API de índice UV. Código de resposta: " +
                        (conn != null ? conn.getResponseCode() : "N/A"));
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            try (Scanner scanner = new Scanner(conn.getInputStream())) {
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            // Exibe o JSON completo retornado para ajudar na depuração
            System.out.println("JSON Retornado pela API de UV: " + resultJson);

            // Valida se o JSON retornado não está vazio
            if (resultJson.length() == 0) {
                System.out.println("Erro: Resposta da API de UV está vazia.");
                return null;
            }

            // Analisa o JSON e retorna os dados
            JSONParser parser = new JSONParser();
            JSONObject uvData = (JSONObject) parser.parse(resultJson.toString());
            return uvData;

        } catch (Exception e) {
            System.out.println("Erro ao processar os dados do índice UV: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
