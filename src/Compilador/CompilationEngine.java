package Compilador;

public class CompilationEngine {
    JackTokenizer tokenizer;

    public CompilationEngine(String path){
        tokenizer = new JackTokenizer(path);
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
                    }
                }
            }
        }
        if(!tokenizer.hasMoreTokens())
            xml += "</class>";
        System.out.println(xml);
        tokenizer.rgx.escreverXML(xml);
     }

     private boolean className (String token){
        return tokenizer.tokenType(token).contains("identifier");
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
        }
        return clas;
     }

     private boolean type(String token){
        boolean rst = false;
        if(className(token)){
            rst = true;
        }
        if(tokenizer.keyWord(token).contains("int")|tokenizer.keyWord(token).contains("char")|tokenizer.keyWord(token).contains("boolean")){
            rst = true;
        }
        return rst;
     }

     private boolean varName(String token){
        return tokenizer.tokenType(token).contains("identifier");
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

        return subroutine;
     }
    private boolean subRoutineName(String token){
        return tokenizer.tokenType(token).contains("identifier");
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

        let += "</letStatement>\n     ";
        return let;
    }

    private String auxExp(){
        String e = "";
        if(isTerm()){
            e += compileTerm();
            tokenizer.advance();
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
        if(keywordConstant()){
            rst = true;
        }
        if(varName(tokenizer.token)){
            rst = true;
        }
        return rst;
    }

    public String compileTerm(){
        String term = "<term>\n         ";
        if(tokenizer.tokenType(tokenizer.token).contains("intConst")){
            term += tokenizer.token + "\n         ";
        }
        if(tokenizer.tokenType(tokenizer.token).contains("stringConst")){
            term += tokenizer.token + "\n         ";
        }
        if(keywordConstant()){
            term += tokenizer.token + "\n         ";
        }
        if(varName(tokenizer.token)){
            term += tokenizer.token + "\n         ";
        }
        term += "</term>\n         ";
        return term;
    }

    private boolean op(String tk){
        boolean rst = false;
        if(tokenizer.symbol(tk).contains("+")|tokenizer.symbol(tk).contains("-")|tokenizer.symbol(tk).contains("*")|tokenizer.symbol(tk).contains("/")|tokenizer.symbol(tk).contains("&")|tokenizer.symbol(tk).contains("|")|tokenizer.symbol(tk).contains("<")|tokenizer.symbol(tk).contains(">")|tokenizer.symbol(tk).contains("="))
            rst = true;
        return rst;
    }

    private boolean keywordConstant(){
        boolean rst = false;

        if(tokenizer.keyWord(tokenizer.token).contains("this")|tokenizer.keyWord(tokenizer.token).contains("true")|tokenizer.keyWord(tokenizer.token).contains("false")|tokenizer.keyWord(tokenizer.token).contains("null")){
            rst = true;
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
                System.out.println(tokenizer.token);
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

                                    }
                                }
                            }
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
                        }
                    }
                }
            }
        }
        whl += "</whileStatement>\n       ";
        return whl;
    }
}
