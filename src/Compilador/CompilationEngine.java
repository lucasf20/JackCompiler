package Compilador;

public class CompilationEngine {
    JackTokenizer tokenizer;
    private String local;

    public CompilationEngine(String path){
        tokenizer = new JackTokenizer(path);
        local = path;
        compileClass();
    }
     public void compileClass(){
        String xml = "";
        tokenizer.advance();
        if(tokenizer.keyWord(tokenizer.token).contains("class")) {
            xml = "<class>\n    " + tokenizer.token + "\n    ";
            tokenizer.advance();
            if(className(tokenizer.token)){
                xml += tokenizer.token + "\n    ";
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains("{")){
                    xml += tokenizer.token + "\n    ";
                    tokenizer.advance();
                    while(tokenizer.hasMoreTokens() && !(tokenizer.keyWord(tokenizer.token).contains("constructor") | tokenizer.keyWord(tokenizer.token).contains("function") | tokenizer.keyWord(tokenizer.token).contains("method"))){
                        xml += compileClassVarDec();
                        tokenizer.advance();
                    }
                    xml += compileSubRoutine();
                    if(tokenizer.symbol(tokenizer.token).contains("}")){
                        xml += tokenizer.token + "\n    ";
                        tokenizer.advance();
                    }else{
                        imprime_erro();
                    }
                }
            }
        }
        if(!tokenizer.hasMoreTokens()){
            xml += "</class>";
            System.out.println(xml);
            TextTools.escreverXML(xml);
        }
        else{
            imprime_erro();
        }
     }

     private boolean className (String token){
        if(tokenizer.tokenType(token).contains("identifier"))
            return true;
        else{
            System.out.println("Esperado um identifier");
            imprime_erro();
            return false;
        }
     }

     public String compileClassVarDec(){
        String clas = "<classVarDec>\n      ";
        while (tokenizer.hasMoreTokens() && !tokenizer.symbol(tokenizer.token).contains(";")){
            if(tokenizer.keyWord(tokenizer.token).contains("static")|tokenizer.keyWord(tokenizer.token).contains("field")){
                clas += tokenizer.token + "\n      ";
                tokenizer.advance();
                if(type(tokenizer.token)){
                    clas += tokenizer.token + "\n      ";
                    tokenizer.advance();
                    if(varName(tokenizer.token)){
                        clas += tokenizer.token + "\n      ";
                        tokenizer.advance();
                        if(tokenizer.symbol(tokenizer.token).contains(",")){
                            clas += tokenizer.token + "\n      ";
                            tokenizer.advance();
                            if(varName(tokenizer.token)){
                                clas += tokenizer.token + "\n      ";
                                tokenizer.advance();
                            }
                        }
                    }
                }
            }
        }
        if(tokenizer.symbol(tokenizer.token).contains(";")){
            clas += tokenizer.token + "\n    </classVarDec>\n   ";
        }else {
            System.out.println("Esperado ;");
            imprime_erro();
        }
        return clas;
     }

     private boolean type(String token){
        boolean rst = false;
        if(tokenizer.tokenType(token).contains("identifier")){
            if(className(token)){
                rst = true;
            }
        }
        if(tokenizer.tokenType(token).contains("keyword")){
            if(tokenizer.keyWord(token).contains("int")|tokenizer.keyWord(token).contains("char")|tokenizer.keyWord(token).contains("boolean")){
                rst = true;
            }
        }
        if(!rst && !tokenizer.keyWord(token).contains("void")){
            System.out.println("Esperado um identifier ou as palavras: int, char, boolean");
        }
        return rst;
     }

     private boolean varName(String token){
        return className(token);
     }

     public String compileSubRoutine(){
        String subroutine =  "";
        while (tokenizer.hasMoreTokens() && (tokenizer.keyWord(tokenizer.token).contains("constructor") | tokenizer.keyWord(tokenizer.token).contains("function") | tokenizer.keyWord(tokenizer.token).contains("method"))){
            subroutine +=  "<subroutineDec>\n      ";
            if (tokenizer.keyWord(tokenizer.token).contains("constructor") | tokenizer.keyWord(tokenizer.token).contains("function") | tokenizer.keyWord(tokenizer.token).contains("method")) {
                 subroutine += tokenizer.token + "\n      ";
                 tokenizer.advance();
                 if(type(tokenizer.token)|tokenizer.keyWord(tokenizer.token).contains("void")){
                     subroutine += tokenizer.token + "\n      ";
                     tokenizer.advance();
                     if(subRoutineName(tokenizer.token)){
                         subroutine += tokenizer.token + "\n      ";
                         tokenizer.advance();
                         if(tokenizer.symbol(tokenizer.token).contains("(")){
                             subroutine += tokenizer.token + "\n      ";
                             tokenizer.advance();
                             subroutine += compileParamenterList();
                             if(tokenizer.symbol(tokenizer.token).contains(")")){
                                 subroutine += tokenizer.token + "\n      ";
                                 tokenizer.advance();
                                 subroutine += subroutineBody();
                             }
                         }
                     }
                 }
             }
            subroutine +="</subroutineDec>\n    ";
        }
        if(!tokenizer.token.contains("}")){
            System.out.println("Esperado }");
            imprime_erro();
        }

        return subroutine;
     }
    private boolean subRoutineName(String token){
        return className(token);
    }

    public String compileParamenterList(){
        String parameter = "<parameterList>\n       ";
        while (tokenizer.hasMoreTokens() && !tokenizer.symbol(tokenizer.token).contains(")")){
            if(type(tokenizer.token)){
                parameter += tokenizer.token +"\n       ";
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains(",")){
                    parameter += tokenizer.token +"\n       ";
                    tokenizer.advance();
                    if(type(tokenizer.token)){
                        parameter += tokenizer.token +"\n       ";
                        tokenizer.advance();
                    }
                }
            }
        }
        parameter += "</parameterList>\n      ";
        if(!tokenizer.token.contains(")")){
            System.out.println("Esperado )");
            imprime_erro();
        }
        return parameter;
    }

    private String subroutineBody(){
        String body = "<subroutineBody>\n       ";

        if(tokenizer.symbol(tokenizer.token).contains("{")){
            body += tokenizer.token + "\n       ";
            tokenizer.advance();
            while(tokenizer.hasMoreTokens() && tokenizer.keyWord(tokenizer.token).contains("var")){
                body += compileVarDec();
            }
            body += compileStatements();
            if(tokenizer.symbol(tokenizer.token).contains("")){
                body += tokenizer.token + "\n       ";
                tokenizer.advance();
            }
        }


        body += "</subroutineBody>\n     ";
        return body;
    }

    public String compileVarDec(){
        String varDec = "<varDec>\n         ";

        if(tokenizer.keyWord(tokenizer.token).contains("var")){
            varDec += tokenizer.token +"\n         ";
            tokenizer.advance();
            if(type(tokenizer.token)){
               varDec += tokenizer.token +"\n         ";
               tokenizer.advance();
                while (tokenizer.hasMoreTokens() && !tokenizer.symbol(tokenizer.token).contains(";")){
                    if(varName(tokenizer.token)){
                        varDec += tokenizer.token +"\n         ";
                        tokenizer.advance();
                        if(tokenizer.symbol(tokenizer.token).contains(",")){
                            varDec += tokenizer.token +"\n         ";
                            tokenizer.advance();
                            if(varName(tokenizer.token)){
                                varDec += tokenizer.token +"\n         ";
                                tokenizer.advance();
                            }
                        }
                    }
                }
                if(tokenizer.symbol(tokenizer.token).contains(";")){
                    varDec += tokenizer.token +"\n         ";
                    tokenizer.advance();
                }
                else{
                    System.out.println("Esperado ;");
                    imprime_erro();
                }
            }
        }

        varDec += "</varDec>\n         ";
        return varDec;
    }

    public String compileStatements(){
        String statements = "<statements>\n         ";
        int i = 0;
        while(tokenizer.hasMoreTokens() && (tokenizer.keyWord(tokenizer.token).contains("let")|tokenizer.keyWord(tokenizer.token).contains("do")|tokenizer.keyWord(tokenizer.token).contains("if")|tokenizer.keyWord(tokenizer.token).contains("while")|tokenizer.keyWord(tokenizer.token).contains("return"))){
            if(tokenizer.keyWord(tokenizer.token).contains("let")){
                statements += compileLet();
                tokenizer.advance();
            }
            if(tokenizer.keyWord(tokenizer.token).contains("do")){
                statements += compileDo();
                tokenizer.advance();
            }
            if(tokenizer.keyWord(tokenizer.token).contains("return")){
                statements += compileReturn();
                tokenizer.advance();
            }
            if(tokenizer.keyWord(tokenizer.token).contains("if")){
                statements += compileIf();
            }
            if(tokenizer.keyWord(tokenizer.token).contains("while")){
                statements += compileWhile();
            }

        }

        statements += "</statements>\n      ";
        return statements;
    }

    public String compileLet(){
        String let = "<letStatement>\n              ";
        if(tokenizer.keyWord(tokenizer.token).contains("let")){
            let += tokenizer.token + "\n              ";
            tokenizer.advance();
            if(varName(tokenizer.token)){
                let += tokenizer.token + "\n              ";
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains("[")){
                    let += tokenizer.token + "\n              ";
                    tokenizer.advance();
                    let += compileExpression();
                    if(tokenizer.symbol(tokenizer.token).contains("]")){
                        let += tokenizer.token + "\n              ";
                        tokenizer.advance();
                    }
                }

                if(tokenizer.symbol(tokenizer.token).contains("=")){
                    let += tokenizer.token + "\n              ";
                    tokenizer.advance();
                    let += compileExpression();

                }
                if(tokenizer.symbol(tokenizer.token).contains(";")){
                        let += tokenizer.token + "\n         ";

                }
                else{
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(";")){
                        let += tokenizer.token + "\n         ";
                    }
                }
            }
        }

        if(!tokenizer.symbol(tokenizer.token).contains(";")){
            System.out.println("Esperado ;");
            imprime_erro();
        }
        let += "</letStatement>\n     ";
        return let;
    }

    private String auxExp(){
        String e = "";
        if(isTerm()){
            e += compileTerm();
            if(tokenizer.hasMoreTokens() && op(tokenizer.token)){
                e += tokenizer.token + "\n            ";
                tokenizer.advance();
                e += auxExp();
            }
        }
        return e;
    }

    public String compileExpression(){
        String exp = "<expression>\n            ";
        exp += auxExp();
        exp += "</expression>\n            ";
        return exp;
    }

    private boolean isTerm(){
        boolean rst = false;
        if(tokenizer.tokenType(tokenizer.token).contains("intConst")){
            rst = true;
        }
        if(tokenizer.tokenType(tokenizer.token).contains("stringConst")){
            rst = true;
        }
        if(tokenizer.tokenType(tokenizer.token).contains("keyword")){
            if(keywordConstant()){
                rst = true;
            }
        }
        if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
            rst = true;
        }
        if(!rst){
            System.out.println("Term incorreto");
            imprime_erro();
        }
        return rst;
    }

    public String compileTerm(){
        String term = "<term>\n         ";
        if(tokenizer.tokenType(tokenizer.token).contains("intConst")){
            term += tokenizer.token + "\n         ";
            tokenizer.advance();
        }
        if(tokenizer.tokenType(tokenizer.token).contains("stringConst")){
            term += tokenizer.token + "\n         ";
            tokenizer.advance();
        }
        if(tokenizer.tokenType(tokenizer.token).contains("keyword")){
            if(keywordConstant()){
                term += tokenizer.token + "\n         ";
                tokenizer.advance();
            }
        }
        if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
            term += tokenizer.token + "\n         ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")|tokenizer.symbol(tokenizer.token).contains(".")){
                term += subroutineCall();
            }else{
                if(tokenizer.symbol(tokenizer.token).contains("[")){
                    term += tokenizer.token + "\n         ";
                    tokenizer.advance();
                    term += compileExpression();
                    if(tokenizer.symbol(tokenizer.token).contains("]")){
                        term += tokenizer.token + "\n         ";
                        tokenizer.advance();
                    }else{
                        System.out.println("Esperado ]");
                        imprime_erro();
                    }
                }
            }
        }
        if(tokenizer.symbol(tokenizer.token).contains("(")){
            term += tokenizer.token + "\n         ";
            tokenizer.advance();
            term += compileExpression();
            if(tokenizer.symbol(tokenizer.token).contains(")")){
                term += tokenizer.token + "\n         ";
                tokenizer.advance();
            }else{
                System.out.println("Esperado )");
                imprime_erro();
            }
        }
        if(unaryOp(tokenizer.token)){
            term += tokenizer.token + "\n         ";
            tokenizer.advance();
            term += compileTerm();
        }
        term += "</term>\n         ";
        return term;
    }

    private String subroutineCall(){
        String call = "";
        if(tokenizer.symbol(tokenizer.token).contains("(")){
            call += tokenizer.token + "\n       ";
            tokenizer.advance();
            call += compileExpressionList();
            if(tokenizer.symbol(tokenizer.token).contains(")")){
                call += tokenizer.token + "\n       ";
                tokenizer.advance();
            }else{
                System.out.println("Esperado )");
                imprime_erro();
            }
        }else{
            if (tokenizer.symbol(tokenizer.token).contains(".")){
                call += tokenizer.token + "\n       ";
                tokenizer.advance();
                if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
                    call += tokenizer.token + "\n       ";
                    tokenizer.advance();
                    call += subroutineCall();
                }else{
                    System.out.println("Esperado identifier");
                    imprime_erro();
                }
            }else{
                System.out.println("Esperado . ou (");
                imprime_erro();
            }
        }
        return call;
    }

    private boolean op(String tk){
        boolean rst = false;
        if(tokenizer.symbol(tk).contains("+")|tokenizer.symbol(tk).contains("-")|tokenizer.symbol(tk).contains("*")|tokenizer.symbol(tk).contains("/")|tokenizer.symbol(tk).contains("&")|tokenizer.symbol(tk).contains("|")|tokenizer.symbol(tk).contains("<")|tokenizer.symbol(tk).contains(">")|tokenizer.symbol(tk).contains("="))
            rst = true;
        return rst;
    }

    private boolean unaryOp(String tk){
        boolean rst = false;
        if(tokenizer.symbol(tk).contains("-") | tokenizer.symbol(tk).contains("~")) {
            rst =true;
        }
        return rst;
    }

    private boolean keywordConstant(){
        boolean rst = false;

        if(tokenizer.keyWord(tokenizer.token).contains("this")|tokenizer.keyWord(tokenizer.token).contains("true")|tokenizer.keyWord(tokenizer.token).contains("false")|tokenizer.keyWord(tokenizer.token).contains("null")){
            rst = true;
        }
        if(!rst){
            System.out.println("Esperado this, true, false ou null");
            imprime_erro();
        }
        return rst;
    }

    private String subroute(){
        String sub = "";
        if (tokenizer.symbol(tokenizer.token).contains("(")) {
            sub += tokenizer.token + "\n         ";
            tokenizer.advance();
            if (tokenizer.symbol(tokenizer.token).contains(")")) {
                sub += "<expressionList>\n          </expressionList>\n           ";
                sub += tokenizer.token + "\n         ";
            } else {
                sub += compileExpressionList();
                if (tokenizer.symbol(tokenizer.token).contains(")")) {
                    sub += tokenizer.token + "\n         ";
                }

            }
        }
        if(!tokenizer.symbol(tokenizer.token).contains(")")){
            System.out.println("Esperado )");
            imprime_erro();
        }
        return sub;
    }

    public String compileDo(){
        String doo = "<doStatement>\n           ";
        if(tokenizer.keyWord(tokenizer.token).contains("do")){
            doo += tokenizer.token + "\n           ";
            tokenizer.advance();
            if(subRoutineName(tokenizer.token)){
                doo += tokenizer.token + "\n           ";
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains("(")){
                    doo += subroute();
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(";")){
                        doo += tokenizer.token + "\n     ";
                    }
                }
                else{
                    if(tokenizer.symbol(tokenizer.token).contains(".")){
                        doo += tokenizer.token + "\n           ";
                        tokenizer.advance();
                        if(varName(tokenizer.token)){
                            doo += tokenizer.token + "\n           ";
                            tokenizer.advance();
                            doo += subroute();
                            tokenizer.advance();
                            if(tokenizer.symbol(tokenizer.token).contains(";")){
                                doo += tokenizer.token + "\n     ";
                            }
                        }
                    }
                }
            }
        }
        if(!tokenizer.symbol(tokenizer.token).contains(";")){
            System.out.println("Esperado ;");
            imprime_erro();
        }
        doo += "</doStatement>\n       ";
        return doo;
    }

    public String compileExpressionList(){
        String expList = "<expressionList>\n        ";
        while (tokenizer.hasMoreTokens()){
            if(isTerm()){
                expList += compileExpression();
                if(tokenizer.symbol(tokenizer.token).contains(",")){
                    expList += tokenizer.token + "\n        ";
                    tokenizer.advance();
                }
                else{
                    break;
                }
            }
        }
        expList += "</expressionList>\n        ";
        return expList;
    }

    public String compileReturn(){
        String ret = "<returnStatement>\n           ";
        String aux;
        if(tokenizer.keyWord(tokenizer.token).contains("return")){
            ret += tokenizer.token + "\n           ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains(";")){
                ret += tokenizer.token + "\n       ";
            }
            else{
                aux = compileExpression();
                if(aux.contains("intConst")|aux.contains("identifier")){
                    ret += aux;
                    //tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(";")){
                        ret += tokenizer.token + "\n       ";
                    }
                }
            }

        }
        if(!tokenizer.symbol(tokenizer.token).contains(";")){
            System.out.println("Esperado ;");
            imprime_erro();
        }
        ret += "</returnStatement>\n        ";
        return ret;
    }

    public String compileIf(){
        String ife = "<ifStatement>\n           ";
        if(tokenizer.keyWord(tokenizer.token).contains("if")){
            ife += tokenizer.token + "\n           ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")){
                ife += tokenizer.token + "\n           ";
                tokenizer.advance();
                ife += compileExpression();
                if(tokenizer.symbol(tokenizer.token).contains(")")){
                    ife += tokenizer.token + "\n           ";
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains("{")){
                        ife += tokenizer.token + "\n           ";
                        tokenizer.advance();
                        ife += compileStatements();
                        if(tokenizer.symbol(tokenizer.token).contains("}")){
                            ife += tokenizer.token + "\n      ";
                            tokenizer.advance();
                            if(tokenizer.keyWord(tokenizer.token).contains("else")){
                                ife += tokenizer.token + "\n           ";
                                tokenizer.advance();
                                if(tokenizer.symbol(tokenizer.token).contains("{")){
                                    ife += tokenizer.token + "\n           ";
                                    tokenizer.advance();
                                    ife += compileStatements();
                                    if(tokenizer.symbol(tokenizer.token).contains("}")){
                                        ife += tokenizer.token + "\n      ";
                                        tokenizer.advance();

                                    }else{
                                        System.out.println("Esperado }");
                                        imprime_erro();
                                    }
                                }
                            }
                        }else{
                            System.out.println("Esperado }");
                            imprime_erro();
                        }
                    }
                }
            }
        }
        ife += "</ifStatement>\n        ";
        return ife;
    }

    public String compileWhile(){
        String whl = "<whileStatement>\n            ";
        if(tokenizer.keyWord(tokenizer.token).contains("while")){
            whl += tokenizer.token + "\n            ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")){
                whl += tokenizer.token + "\n           ";
                tokenizer.advance();
                whl += compileExpression();
                if(tokenizer.symbol(tokenizer.token).contains(")")){
                    whl += tokenizer.token + "\n           ";
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains("{")){
                        whl += tokenizer.token + "\n           ";
                        tokenizer.advance();
                        whl += compileStatements();
                        if(tokenizer.symbol(tokenizer.token).contains("}")){
                            whl += tokenizer.token + "\n      ";
                            tokenizer.advance();
                        }else{
                            System.out.println("Esperado }");
                            imprime_erro();
                        }
                    }
                }
            }
        }
        whl += "</whileStatement>\n       ";
        return whl;
    }
    private void imprime_erro(){
        int linha = TextTools.controleDeLinha(tokenizer.tkst,local);
        if(tokenizer.tokenType(tokenizer.token).contains("keyword")){
            System.out.println("Erro proximo a: " + tokenizer.keyWord(tokenizer.token));
        }
        if(tokenizer.tokenType(tokenizer.token).contains("symbol")){
            System.out.println("Erro proximo a: "+ tokenizer.symbol(tokenizer.token));
        }
        if(tokenizer.tokenType(tokenizer.token).contains("intConst")){
            System.out.println("Erro proximo a:  " + tokenizer.intVal(tokenizer.token));
        }
        if(tokenizer.tokenType(tokenizer.token).contains("stringConst")){
            System.out.println("Erro proximo a: " + tokenizer.stringVal(tokenizer.token));
        }
        if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
            System.out.println("Erro proximo a: " +  tokenizer.identifier(tokenizer.token));
        }
        System.out.println("Linha: " + linha);
        System.exit(-1);
    }
}
