package Compilador;
//falta fazer:
//compileTerm para Identifier
//compileSubRoutineCall
//compileLet

public class CompilationEngine {
    JackTokenizer tokenizer;
    private String local;
    VMWriter vm;
    SymbolTable st;
    String classname;
    String functionName;
    String subroutineType;
    String nameAux;
    int numArgs;
    String classname2;
    int ifLabelNum = 0;
    int whileLabelNum = 0;

    public CompilationEngine(String path){
        tokenizer = new JackTokenizer(path);
        local = path;
        vm = new VMWriter(path);
        st = new SymbolTable();
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
                classname = tokenizer.identifier(tokenizer.token);
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains("{")){
                    xml += tokenizer.token + "\n    ";
                    tokenizer.advance();
                    xml += compileClassVarDec();
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
            vm.close();
            System.out.println(xml);
            TextTools.escreverXML(xml, local);
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

     private String classVarDecAux(){
        String rst ="";
        String type, kind, name;
        if(tokenizer.keyWord(tokenizer.token).contains("field") | tokenizer.keyWord(tokenizer.token).contains("static")){
            rst += tokenizer.token + "\n      ";
            kind = tokenizer.keyWord(tokenizer.token);
            tokenizer.advance();
            if(type(tokenizer.token)){
                rst += tokenizer.token+ "\n      ";
                type = (tokenizer.tokenType(tokenizer.token).contains("keyword"))?tokenizer.keyWord(tokenizer.token):tokenizer.identifier(tokenizer.token);
                tokenizer.advance();
                if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
                    rst += tokenizer.token+ "\n      ";
                    name = tokenizer.identifier(tokenizer.token);
                    st.define(name,type,kind);
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(",")){
                        while (tokenizer.symbol(tokenizer.token).contains(",")){
                            rst += tokenizer.token+ "\n      ";
                            tokenizer.advance();
                            if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
                                rst += tokenizer.token+ "\n      ";
                                name = tokenizer.identifier(tokenizer.token);
                                st.define(name,type,kind);
                                tokenizer.advance();
                            }
                        }
                    }
                    if(tokenizer.symbol(tokenizer.token).contains(";")){
                        rst += tokenizer.token+ "\n      ";
                        tokenizer.advance();
                    }else {
                        System.out.println("Esperado ;");
                        imprime_erro();
                    }
                }
            }
        }
        return rst;
     }

     public String compileClassVarDec(){
        String clas = "<classVarDec>\n      ";
        st.startSubroutine();
        while (tokenizer.keyWord(tokenizer.token).contains("field") | tokenizer.keyWord(tokenizer.token).contains("static")){
            clas += classVarDecAux();
        }
        clas += "</classVarDec>\n      ";
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
            imprime_erro();
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
            st.startSubroutine();
            if (tokenizer.keyWord(tokenizer.token).contains("constructor") | tokenizer.keyWord(tokenizer.token).contains("function") | tokenizer.keyWord(tokenizer.token).contains("method")) {
                 subroutine += tokenizer.token + "\n      ";
                 subroutineType = tokenizer.keyWord(tokenizer.token);
                 tokenizer.advance();
                 if(type(tokenizer.token)|tokenizer.keyWord(tokenizer.token).contains("void")){
                     subroutine += tokenizer.token + "\n      ";
                     tokenizer.advance();
                     if(subRoutineName(tokenizer.token)){
                         subroutine += tokenizer.token + "\n      ";
                         functionName =classname + "." + tokenizer.identifier(tokenizer.token);
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
        String name, type, kind;
        kind = "argument";
        st.define("this", classname,kind);
        while (tokenizer.hasMoreTokens() && !tokenizer.symbol(tokenizer.token).contains(")")){
            if(type(tokenizer.token)){
                parameter += tokenizer.token + "\n       ";
                type = (tokenizer.tokenType(tokenizer.token).contains("keyword"))?tokenizer.keyWord(tokenizer.token):tokenizer.identifier(tokenizer.token);
                tokenizer.advance();
                if(varName(tokenizer.token)){
                    parameter += tokenizer.token + "\n       ";
                    name = tokenizer.identifier(tokenizer.token);
                    st.define(name,type,kind);
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(",")){
                        parameter += tokenizer.token + "\n       ";
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
            vm.writeFunction(functionName,st.varCount("local"));
            switch (subroutineType){
                case "constructor":
                    vm.writePush("constant",st.varCount("field"));
                    vm.writeCall("Memory.alloc",1);
                    vm.writePop("pointer",0);
                    break;
                case "method":
                    vm.writePush("argument",0);
                    vm.writePop("pointer",0);
                    break;
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
        st.startSubroutine();
        String type, name, kind;
        kind = "local";
        if(tokenizer.keyWord(tokenizer.token).contains("var")){
            varDec += tokenizer.token +"\n         ";
            tokenizer.advance();
            if(type(tokenizer.token)){
               varDec += tokenizer.token +"\n         ";
               type = (tokenizer.tokenType(tokenizer.token).contains("keyword"))?tokenizer.keyWord(tokenizer.token):tokenizer.identifier(tokenizer.token);
               tokenizer.advance();
                while (tokenizer.hasMoreTokens() && !tokenizer.symbol(tokenizer.token).contains(";")){
                    if(varName(tokenizer.token)){
                        varDec += tokenizer.token +"\n         ";
                        name = tokenizer.identifier(tokenizer.token);
                        st.define(name,type,kind);
                        tokenizer.advance();
                        if(tokenizer.symbol(tokenizer.token).contains(",")){
                            varDec += tokenizer.token +"\n         ";
                            tokenizer.advance();
                            if(varName(tokenizer.token)){
                                varDec += tokenizer.token +"\n         ";
                                name = tokenizer.identifier(tokenizer.token);
                                st.define(name,type,kind);
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
        String name, type, kind;
        kind = "local";
        type = "int";
        boolean isArray =false;
        if(tokenizer.keyWord(tokenizer.token).contains("let")){
            let += tokenizer.token + "\n              ";
            tokenizer.advance();
            if(varName(tokenizer.token)){
                let += tokenizer.token + "\n              ";
                name = tokenizer.identifier(tokenizer.token);
                st.define(name,type,kind);
                tokenizer.advance();
                if(tokenizer.symbol(tokenizer.token).contains("[")){
                    let += tokenizer.token + "\n              ";
                    tokenizer.advance();
                    let += compileExpression();
                    vm.writePush(st.kindOf(name),st.indexOf(name));
                    vm.writeArithmetic("add");
                    if(tokenizer.symbol(tokenizer.token).contains("]")){
                        let += tokenizer.token + "\n              ";
                        isArray = true;
                        tokenizer.advance();
                    }
                }

                if(tokenizer.symbol(tokenizer.token).contains("=")){
                    let += tokenizer.token + "\n              ";
                    tokenizer.advance();
                    let += compileExpression();

                }
                if(isArray){
                    vm.writePop("temp",0);
                    vm.writePop("pointer", 1);
                    vm.writePush("temp",0);
                    vm.writePop("that", 0);
                }else{
                    vm.writePop(st.kindOf(name),st.indexOf(name));
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
                switch (tokenizer.symbol(tokenizer.token)){
                    case "+":
                        vm.writeArithmetic("add");
                        break;
                    case "-":
                        vm.writeArithmetic("sub");
                        break;
                    case "*":
                        vm.writeCall("Math.multiply",2);
                        break;
                    case "/":
                        vm.writeCall("Math.divide",2);
                        break;
                    case "&":
                        vm.writeArithmetic("and");
                        break;
                    case "|":
                        vm.writeArithmetic("or");
                        break;
                    case "<":
                        vm.writeArithmetic("lt");
                        break;
                    case ">":
                        vm.writeArithmetic("gt");
                        break;
                    case "=":
                        vm.writeArithmetic("eq");
                        break;
                    case "~":
                        vm.writeArithmetic("not");
                        break;
                }
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
            vm.writePush("constant",tokenizer.intVal(tokenizer.token));
            tokenizer.advance();
        }
        if(tokenizer.tokenType(tokenizer.token).contains("stringConst")){
            term += tokenizer.token + "\n         ";
            vm.writePush("constant",tokenizer.stringVal(tokenizer.token).length());
            vm.writeCall("String.new",1);
            for(char c:tokenizer.stringVal(tokenizer.token).toCharArray()){
                vm.writePush("constant",c);
                vm.writeCall("String.appendChar", 2);
            }
            tokenizer.advance();
        }
        if(tokenizer.tokenType(tokenizer.token).contains("keyword")){
            if(keywordConstant()){
                term += tokenizer.token + "\n         ";
                switch (tokenizer.keyWord(tokenizer.token)){
                    case "true":
                        vm.writePush("constant",0);
                        vm.writeArithmetic("not");
                        break;
                    case "false":
                    case "null":
                        vm.writePush("constant",0);
                        break;
                    case "this":
                        vm.writePush("pointer",0);
                        break;
                }
                tokenizer.advance();
            }
        }
        if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
            //falta essa parte aqui
            term += tokenizer.token + "\n         ";
            nameAux = tokenizer.identifier(tokenizer.token);
            String name = nameAux;
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")|tokenizer.symbol(tokenizer.token).contains(".")){
                classname2 = classname;
                term += subroutineCall();
            }else{
                if(tokenizer.symbol(tokenizer.token).contains("[")){
                    term += tokenizer.token + "\n         ";
                    tokenizer.advance();
                    term += compileExpression();
                    vm.writePush(st.kindOf(name),st.indexOf(name));
                    vm.writeArithmetic("add");
                    if(tokenizer.symbol(tokenizer.token).contains("]")){
                        vm.writePop("pointer", 1);
                        vm.writePush("that",0);
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
            if (tokenizer.symbol(tokenizer.token).contains("-")){
                vm.writeArithmetic("neg");
            }else{
                vm.writeArithmetic("not");
            }
            tokenizer.advance();
            term += compileTerm();
        }
        term += "</term>\n         ";
        return term;
    }

    private String subroutineCall(){
        String call = "";
        String ident = nameAux;
        if(tokenizer.symbol(tokenizer.token).contains("(")){
            call += tokenizer.token + "\n       ";
            tokenizer.advance();
            numArgs = 0;
            if(!tokenizer.symbol(tokenizer.token).contains(")")){
                call += compileExpressionList();
            }else{
                call += "<expressionList>\n        " + "</expressionList>\n        ";
            }
            ident = classname2+"."+nameAux;
            vm.writeCall(ident,numArgs);
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
                    classname2 = nameAux;
                    nameAux = tokenizer.identifier(tokenizer.token);
                    vm.writePush(nameAux,st.indexOf(nameAux));
                    ident = st.typeOf(nameAux) + "." + nameAux;
                    tokenizer.advance();
                    call += subroutineCall();
                    numArgs++;
                }else{
                    System.out.println("Esperado identifier");
                    imprime_erro();
                }
            }else{
                System.out.println("Esperado . ou (");
                imprime_erro();
            }
            vm.writeCall(ident,numArgs);
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

    private String doaux(){
        String rst ="";
        if(tokenizer.tokenType(tokenizer.token).contains("identifier")){
            rst += tokenizer.token+  "\n      ";
            nameAux = tokenizer.identifier(tokenizer.token);
            tokenizer.advance();
            classname2 = classname;
            rst += subroutineCall();
            if(tokenizer.symbol(tokenizer.token).contains(";")){
                rst += tokenizer.token+  "\n      ";
                vm.writePop("temp",0);
            }else{
                System.out.println("Esperado ;");
                imprime_erro();
            }
        }
        return rst;
    }

    public String compileDo(){
        String doo = "<doStatement>\n           ";
        if(tokenizer.keyWord(tokenizer.token).contains("do")){
            doo += tokenizer.token +  "\n      ";
            tokenizer.advance();
            doo += doaux();
        }
        doo += "</doStatement>\n       ";
        return doo;
    }

    public String compileExpressionList(){
        numArgs = 0;
        String expList = "<expressionList>\n        ";
        while (tokenizer.hasMoreTokens()){
            if(isTerm()){
                numArgs++;
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
                vm.writePush("constant", 0);
                vm.writeReturn();
            }
            else{
                aux = compileExpression();
                if(aux.contains("intConst")|aux.contains("identifier")){
                    ret += aux;
                    //tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains(";")){
                        ret += tokenizer.token + "\n       ";
                        vm.writeReturn();
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
        String labelTrue = "IF_TRUE" + ifLabelNum;
        String labelFalse = "IF_FALSE" + ifLabelNum;
        String labelEnd = "IF_END" + ifLabelNum;
        ifLabelNum++;
        if(tokenizer.keyWord(tokenizer.token).contains("if")){
            ife += tokenizer.token + "\n           ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")){
                ife += tokenizer.token + "\n           ";
                tokenizer.advance();
                ife += compileExpression();
                if(tokenizer.symbol(tokenizer.token).contains(")")){
                    ife += tokenizer.token + "\n           ";
                    vm.writeIf(labelTrue);
                    vm.writeGoTo(labelFalse);
                    vm.writeLabel(labelTrue);
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
                                vm.writeGoTo(labelEnd);
                                vm.writeLabel(labelFalse);
                                tokenizer.advance();
                                if(tokenizer.symbol(tokenizer.token).contains("{")){
                                    ife += tokenizer.token + "\n           ";
                                    tokenizer.advance();
                                    ife += compileStatements();
                                    if(tokenizer.symbol(tokenizer.token).contains("}")){
                                        ife += tokenizer.token + "\n      ";
                                        vm.writeLabel(labelEnd);
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
        String labelWhileExp = "WHILE_EXP" + whileLabelNum;
        String labelWhileEnd = "WHILE_END" + whileLabelNum;
        whileLabelNum++;
        vm.writeLabel(labelWhileExp);
        if(tokenizer.keyWord(tokenizer.token).contains("while")){
            whl += tokenizer.token + "\n            ";
            tokenizer.advance();
            if(tokenizer.symbol(tokenizer.token).contains("(")){
                whl += tokenizer.token + "\n           ";
                tokenizer.advance();
                whl += compileExpression();
                vm.writeArithmetic("not");
                vm.writeIf(labelWhileEnd);
                if(tokenizer.symbol(tokenizer.token).contains(")")){
                    whl += tokenizer.token + "\n           ";
                    tokenizer.advance();
                    if(tokenizer.symbol(tokenizer.token).contains("{")){
                        whl += tokenizer.token + "\n           ";
                        tokenizer.advance();
                        whl += compileStatements();
                        vm.writeGoTo(labelWhileExp);
                        vm.writeLabel(labelWhileEnd);
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
