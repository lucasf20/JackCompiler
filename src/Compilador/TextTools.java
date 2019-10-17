package Compilador;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.*;
import java.util.stream.Stream;

public class TextTools {
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
                }
            }
            else{
                if(regexMatcher.group().contains("*/") && token.contains("symbol")){
                    rst += "<symbol>*</symbol>\n<symbol>/</symbol>\n";
                }
            }
        }
        return  rst;
    }

    public static String tokenStart(String regex, String texto, String token){//busca a posicao inicial das palavras
        Pattern checkRegex = Pattern.compile(regex);
        Matcher regexMatcher = checkRegex.matcher(texto);

        String start = "";

        while (regexMatcher.find()){
            if(!regexMatcher.group().startsWith("//")) {
                if (!regexMatcher.group().startsWith("\"") || token.contains("identifier"))//ignorar comentarios
                    start += regexMatcher.start() + "\n";
            }
            else{
                if(regexMatcher.group().contains("*/") && token.contains("symbol")){
                    start += regexMatcher.group().indexOf("*/") +"\n";
                    start += (regexMatcher.group().indexOf("*/") + 1) +"\n";
                }
            }
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
        String rst = "";
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
        return rst;
    }

    public static String comentariosDeBloco (String xml){
        String blockSt = "<symbol>/</symbol>\n" + "<symbol>*</symbol>";
        String blockEnd = "<symbol>*</symbol>\n" + "<symbol>/</symbol>";
        String rst = xml.replace(blockSt,"<coment>");
        if(rst.contains("<coment>"))
            rst = rst.replace(blockEnd, "</coment>");
        else
            rst = rst.replace(blockEnd + "\n","");
        return eliminaBloco(rst);
    }

    private static String eliminaBloco (String blocoMarcado){
        String[] separaBlocos = blocoMarcado.split("<coment>");
        String rst = "";
        if(validaComent(blocoMarcado)[0] >= validaComent(blocoMarcado)[1]){
            for(int i = 0; i < separaBlocos.length; i++){
                if(separaBlocos[i].split("</coment>").length > 1)
                    separaBlocos[i] = separaBlocos[i].split("</coment>\n")[1];
                rst += separaBlocos[i];
            }
        }
        else{
            for(int i = 0; i < separaBlocos.length - 1; i++){
                if(separaBlocos[i].split("</coment>").length > 1) {
                    separaBlocos[i] = separaBlocos[i].split("</coment>\n")[1];
                }
                rst += separaBlocos[i];
            }
            for(int i = 1; i < separaBlocos[separaBlocos.length - 1].split("</coment>\n").length; i++){
                rst += "<symbol>*</symbol>\n" + "<symbol>/</symbol>\n" + separaBlocos[separaBlocos.length - 1].split("</coment>\n")[i];
            }
        }
        return rst;
    }

    private static int[] validaComent(String texto){
        int[] tags = new int[2];
        String[] linhaAlinha = texto.split("\n");
        for(int i = 0; i < linhaAlinha.length; i++){
            if(linhaAlinha[i].contains("<coment>")){
                tags[0]++;
            }
            if(linhaAlinha[i].contains("</coment>")){
                tags[1]++;
            }
        }
        return tags;
    }

    public static int[] removeSujeira(String path, int[] tkst){
        int[] rst = new int[tkst.length];
        int[] rst2;
        int aux =0;
        int cnt = 0;
        String file = lerArquivoJack(path);
        for (int i = 1; i < tkst.length; i++){
            if(file.charAt(tkst[i-1]) == '/' && file.charAt(tkst[i]) == '*'){
                rst[i-1] = -1;
                cnt++;
                while(i < tkst.length){
                    rst[i] = -1;
                    i++;
                    cnt++;
                    if(file.charAt(tkst[i]) == '/' && file.charAt(tkst[i-1]) == '*'){
                        rst[i] = -1;
                        cnt++;
                        break;
                    }
                }
            }else{
                rst[i] = tkst[i];
            }
        }
        rst2 = new int[rst.length - cnt];
        for(int i = 0; i < rst.length; i++){
            if(rst[i] != -1){
                rst2[aux] = rst[i];
                aux++;
            }
        }
        return rst2;
    }

    public static int controleDeLinha(int tokenpos, String path){
        String file = lerArquivoJack(path);
        String divide = file.substring(0,tokenpos);
        Stream<String> lines = divide.lines();
        return (int)lines.count();
    }
}