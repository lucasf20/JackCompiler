package AnalizadorLexico;

public class JackCompiler {
    public static void main(String[] args){

        JackTokenizer tknize = new JackTokenizer("file.jack");

        //escreve o arquivo xml
        tknize.rgx.escreverXML("<tokens>\n" + tknize.xmlFinal + "</tokens>");

        System.out.println("Os tokens:\n");

        //imprime os tokens um por um
        System.out.println("<tokens>");
        while (tknize.hasMoreTokens()){
            tknize.advance();
            System.out.println(tknize.token);
        }
        System.out.println("</tokens>");

        //leva o cursor dos tokens para o comeco
        tknize.zerar();
        System.out.println("\nO valor dos tokens e seu tipo:\n");

        //imprime os valores dos tokens de acordo com seu tipo
        while (tknize.hasMoreTokens()){
            tknize.advance();
            if(tknize.tokenType(tknize.token).contains("keyword"))
                System.out.println(tknize.keyWord(tknize.token) + " - " + tknize.tokenType(tknize.token));
            if(tknize.tokenType(tknize.token).contains("intConst"))
                System.out.println(tknize.intVal(tknize.token) + " - " + tknize.tokenType(tknize.token));
            if(tknize.tokenType(tknize.token).contains("stringConst"))
                System.out.println(tknize.stringVal(tknize.token) + " - " + tknize.tokenType(tknize.token));
            if(tknize.tokenType(tknize.token).contains("identifier"))
                System.out.println(tknize.identifier(tknize.token) + " - " + tknize.tokenType(tknize.token));
            if(tknize.tokenType(tknize.token).contains("symbol"))
                System.out.println(tknize.symbol(tknize.token) + " - " + tknize.tokenType(tknize.token));
        }

    }
}
