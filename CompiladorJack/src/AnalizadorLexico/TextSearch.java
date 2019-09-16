package AnalizadorLexico;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.*;

public class TextSearch {
    public static String lerArquivoJack(String path){ //lê o arquivo jack e retorna uma string de seu conteudo
        Path caminho = Paths.get(path);
        String rst = "-1";
        try{
            byte[] texto = Files.readAllBytes(caminho);
            rst = new String(texto);
        } catch (Exception error){
            System.out.println(error);
        }
        return rst;
    }

    public static void escreverXML(String codigo){ //escreve o arquivo.xml
        Path caminho = Paths.get("jack.xml");
        byte[] conteudo = codigo.getBytes();
        try {
            Files.write(caminho, conteudo);
        } catch (Exception error){
            System.out.println(error);
        }
    }

    public static String regexChecker(String regex, String texto, String token){ //procura no texto e escreve os tokens
        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(texto);
        String rst = "";

        while (regexMatcher.find()){
            if(!regexMatcher.group().startsWith("//")) { //ignorar comentarios
                if(!regexMatcher.group().startsWith("\"") || token.contains("identifier")){
                    rst += "<" + token + ">";
                    if (!regexMatcher.group().contains("<") && !regexMatcher.group().contains(">") && !regexMatcher.group().contains("&") && !regexMatcher.group().contains("\""))
                        rst += regexMatcher.group();
                    else {
                        if (regexMatcher.group().contains("<"))
                            rst += regexMatcher.group().replace("<", "&lt;");
                        if (regexMatcher.group().contains(">"))
                            rst += regexMatcher.group().replace(">", "&gt;");
                        if (regexMatcher.group().contains("&"))
                            rst += regexMatcher.group().replace("&", "&amp;");
                        if (regexMatcher.group().contains("\""))
                            rst += regexMatcher.group().replace("\"", "&quot;");
                    }
                rst += "</" + token + ">\n";
                }}
            }
        return  rst;
        }

    public static String tokenStart(String regex, String texto, String token){//busca a posicao inicial das palavras
        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(texto);

        String start = "";

        while (regexMatcher.find()){
            if(!regexMatcher.group().startsWith("//"))
                if(!regexMatcher.group().startsWith("\"") || token.contains("identifier"))//ignorar comentarios
                    start += regexMatcher.start() +"\n";

        }
        return start;
    }

    public static int[] convertParaInt (String s){//converte uma string para um vetor de inteiro
        int i = 0;
        int [] rst;
        String[] rstStr = s.split("\n");

        rst = new int[rstStr.length];

        while (i < rstStr.length){
            rst[i] = Integer.parseInt(rstStr[i]);
            i++;
        }

        return rst;
    }

    public static String xmlOrdenado(String[] tokens, int[] tokenSt ){//ordena os tokens a partir de sua posicao incial
        String rst = "<tokens>\n";
        int i = 0;
        int j;
        int comparar = 999999999;
        int menor;
        while(i < tokens.length){
            j = 0;
            menor = 0;
            while (j < tokenSt.length){
                if(tokenSt[j] < comparar){
                    comparar = tokenSt[j];
                    menor = j;
                }
                j++;
            }
            rst += tokens[menor] + "\n";
            comparar = 999999999;
            tokenSt[menor] = comparar;
            i++;
        }
        rst += "</tokens>";
        return rst;
    }

}
