package AnalizadorLexico;

public class JackCompiler {
    public static void main(String[] args){
        JackTokenizer tknize = new JackTokenizer("file.jack");
        while(tknize.hasMoreTokens()){
            tknize.advance();
            //System.out.println(tknize.stringVal(tknize.token));
        }
        tknize.rgx.escreverXML(tknize.xmlFinal);
        System.out.println(tknize.xmlFinal);

    }
}
