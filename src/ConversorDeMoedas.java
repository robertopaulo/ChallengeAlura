import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.Scanner;

public class ConversorDeMoedas {
    public static void main(String[] args) {
        Scanner leitura = new Scanner(System.in);
        String[] MOEDAS = {"ARS", "BOB", "BRL", "CLP", "COP", "USD"};
        /**Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .setPrettyPrinting()
                .create();
        */
        JsonObject taxas = null;
        try {
            taxas = obterTaxasDeCambio();
        } catch (Exception e) {
            System.out.println("Erro ao obter taxas de câmbio: " + e.getMessage());
        }

        if (taxas == null) {
            System.out.println("Não foi possível obter as taxas de câmbio.");
            return;
        }


        System.out.println("Conversor de Moedas");
        System.out.println("Escolha a moeda base:");
        for (int i = 0; i < MOEDAS.length; i++) {
            System.out.println((i + 1) + ". " + MOEDAS[i]);
        }
        int escolhaBase = leitura.nextInt() - 1;
        String moedaBase = MOEDAS[escolhaBase];

        System.out.println("Escolha a moeda para conversão:");
        for (int i = 0; i < MOEDAS.length; i++) {
            System.out.println((i + 1) + ". " + MOEDAS[i]);
        }
        int escolhaAlvo = leitura.nextInt() - 1;
        String moedaAlvo = MOEDAS[escolhaAlvo];

        System.out.print("Digite o valor em " + moedaBase + ": ");
        double valor = leitura.nextDouble();

        double taxaBase = obterTaxaParaMoeda(taxas, moedaBase);
        double taxaAlvo = obterTaxaParaMoeda(taxas, moedaAlvo);

        System.out.println("Taxa base: " + taxaBase); // Mensagem de depuração
        System.out.println("Taxa alvo: " + taxaAlvo); // Mensagem de depuração

        if (taxaBase != -1 && taxaAlvo != -1) {
            double valorConvertido = (valor / taxaBase) * taxaAlvo;
            System.out.printf("%.2f %s é igual a %.2f %s%n", valor, moedaBase, valorConvertido, moedaAlvo);
        } else {
            System.out.println("Não foi possível obter a taxa de câmbio.");
        }

        leitura.close();
    }

    public static JsonObject obterTaxasDeCambio() throws Exception {
        HttpClient cliente = HttpClient.newHttpClient();
        String URL_API = "https://api.exchangerate-api.com/v4/latest/USD";
        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(new URI(URL_API))
                .build();

        HttpResponse<String> resposta = cliente.send(requisicao, HttpResponse.BodyHandlers.ofString());
        String respostaBody = resposta.body();
        //System.out.println("Resposta da API: " + respostaBody); // Log da resposta JSON

        JsonObject jsonResposta = null;
        try {
            jsonResposta = JsonParser.parseString(respostaBody).getAsJsonObject();
        } catch (Exception e) {
            System.out.println("Erro ao analisar o JSON: " + e.getMessage());
        }
        return jsonResposta;
    }

    public static double obterTaxaParaMoeda(JsonObject taxas, String codigoMoeda) {
        //System.out.println("Estrutura JSON recebida: " + gson.toJson(taxas)); // Log da estrutura do JSON

        if (taxas == null || !taxas.has("rates")) {
            System.out.println("Chave 'rates' não encontrada no JSON."); // Mensagem de depuração
            return -1;
        }
        JsonObject taxasObj = taxas.getAsJsonObject("rates");
        if (!taxasObj.has(codigoMoeda)) {
            System.out.println("Taxa para a moeda " + codigoMoeda + " não encontrada."); // Mensagem de depuração
            return -1;
        }
        return taxasObj.get(codigoMoeda).getAsDouble();
    }
}
