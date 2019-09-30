package AnalizadorLexico;

public class JackTokenizer {

    public static TextSearch rgx = new TextSearch();
    public static String xmlFinal;
    public static String token;

    private static String[] tokens;
    private static int tokenPos;

   public JackTokenizer(String path){

        //regex
       String regStr = "(\"(.*?)\")";
       String regKeywords = regStr + "|(\\b(constructor|class|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return))|//.*";
       String regSimbolos = regStr + "|//.*|\\}|\\(|\\)|\\[|\\]|\\.|\\,|\\;|\\+|\\-|\\*|\\/|\\&|\\||\\<|\\>|\\=|\\~|\\{";
       String regNumeros = regStr + "|//.*|(\\b([0-9]|[1-8][0-9]|9[0-9]|[1-8][0-9]{2}|9[0-8][0-9]|99[0-9]|[1-8][0-9]{3}|9[0-8][0-9]{2}|99[0-8][0-9]|999[0-9]|[12][0-9]{4}|3[01][0-9]{3}|32[0-6][0-9]{2}|327[0-5][0-9]|3276[0-7])\\b)";
       String regIndentificadores = "\\b(?!class|constructor|function|method|field|static|var|int|char|boolean|void|true|false|null|this|let|do|if|else|while|return)+([a-z]|[A-Z]|_)([0-9]|[a-z]|[A-Z]|_)*|//.*|" + regStr;

       //arquivo .jack
       String texto = rgx.lerArquivoJack(path);

        //busca de palavras e classificacao
        String keywords = rgx.regexChecker(regKeywords,texto,"keyword");
        String simbolos = rgx.regexChecker(regSimbolos,texto,"symbol");
        String numeros = rgx.regexChecker(regNumeros,texto,"intConst");
        String identificadores = rgx.regexChecker(regIndentificadores,texto,"identifier");
        //as stringConst sao classificadas primeiramente como identifier, depois elas irao ser reclassificadas em sua categoria correta

        //string nao ordenada
        String xmlForaDeOrdem = keywords + simbolos + numeros + identificadores;
        String[] xmlForaDeOrdemSeparado = xmlForaDeOrdem.split("\n");

        //string contendo a posicao inicial das palavras
        String start = rgx.tokenStart(regKeywords,texto, "keyword") + rgx.tokenStart(regSimbolos,texto, "symbol") + rgx.tokenStart(regNumeros,texto,"intConst")+ rgx.tokenStart(regIndentificadores,texto,"identifier") ;

        //vetor de inteiros contendo a posicao inicial das palavras
        int[] tokenSt= rgx.convertParaInt(start);

        //xml ordenado
        String xmlOrdenado = rgx.xmlOrdenado(xmlForaDeOrdemSeparado,tokenSt);

        //xml final - insercao da tag stringConst
        xmlFinal = xmlOrdenado.replace("&quot;</identifier>","</stringConst>");
        xmlFinal = xmlFinal.replace("<identifier>&quot;","<stringConst>");
        xmlFinal = rgx.comentariosDeBloco(xmlFinal); // trata os comentarios de bloco

        tokens = xmlFinal.split("\n");
        tokenPos = 0;

       }

       public static void zerar(){// leva o cursor dos tokens para o comeco
            tokenPos = 0;
       }

       public static boolean hasMoreTokens(){
            boolean rst;
            if(tokenPos < tokens.length)
                rst = true;
            else
                rst = false;
            return rst;
            }

       public static void advance(){
            if(hasMoreTokens()){
                token = tokens[tokenPos];
                tokenPos++;
            }
       }

       public static String tokenType(String tks){
            String rst = "None";
            String[] tkspl = tks.split(">");
            String tk = tkspl[0];
            if(tk.contains("keyword"))
                rst = "keyword";
            if(tk.contains("intConst"))
                rst = "intConst";
            if(tk.contains("stringConst"))
                rst = "stringConst";
            if(tk.contains("identifier"))
                rst = "identifier";
            if(tk.contains("symbol"))
                rst = "symbol";
            return rst;
       }

       public static String keyWord(String tk){
            String rst = "";
            String[] tks = tk.split(">");
            if(!tokenType(tk).contains("keyword"))
                rst = "NOT_KEYWORD";
            else{
                tks = tks[1].split("<");
                rst = tks[0];
            }
            return rst;
        }

       public static String symbol(String tk){
           String rst = "";
           String[] tks = tk.split(">");
           if(!tokenType(tk).contains("symbol"))
               rst = "NOT_SYMBOL";
           else{
               tks = tks[1].split("<");
               rst = tks[0];
           }
           rst = rst.replace("&lt;","<");
           rst = rst.replace("&gt;",">");
           rst = rst.replace("&quot;","\"");
           rst = rst.replace("&amp;","&");
           return rst;
        }

        public static String identifier(String tk){
            String rst = "";
            String[] tks = tk.split(">");
            if(!tokenType(tk).contains("identifier"))
                rst = "NOT_IDENTIFIER";
            else{
                tks = tks[1].split("<");
                rst = tks[0];
            }
            return rst;
        }

        public static int intVal (String tk){
            int opt;
            String rst;
            String[] tks = tk.split(">");
            if(!tokenType(tk).contains("intConst"))
                rst = "-1";
            else{
                tks = tks[1].split("<");
                rst = tks[0];
            }
            opt = Integer.parseInt(rst);
            return opt;
        }

        public static String stringVal (String tk){
            String rst = "";
            String[] tks = tk.split(">");
            if(!tokenType(tk).contains("stringConst"))
                rst = "NOT_STRINGCONST";
            else{
                tks = tks[1].split("<");
                rst = tks[0];
            }
            return rst;
        }

    }
