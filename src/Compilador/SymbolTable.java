package Compilador;

import java.util.*;

public class SymbolTable {
    public static Map<String, List<String>> symboltable;
    int scope;

    SymbolTable(){
        symboltable = new HashMap<String, List<String>>();
        scope = -1;
    }

    public void startSubroutine(){
        scope++;
    }

    public void define(String name, String type, String kind){

        List<String> n = new ArrayList<String>();
        List<String> t = new ArrayList<String>();
        List<String> k = new ArrayList<String>();
        List<String> i = new ArrayList<String>();
        List<String> s = new ArrayList<String>();

        if(symboltable.get("name") != null)
            n.addAll(symboltable.get("name"));
        if(symboltable.get("type") != null)
            t.addAll(symboltable.get("type"));
        if(symboltable.get("kind") != null)
            k.addAll(symboltable.get("kind"));
        if(symboltable.get("index") != null)
            i.addAll(symboltable.get("index"));
        if(symboltable.get("scope") != null)
            s.addAll(symboltable.get("scope"));

        n.add(name);
        t.add(type);
        k.add(kind);
        s.add(""+scope);
        String ind = countKind(kind);
        i.add(ind);

        symboltable.put("name",n);
        symboltable.put("type",t);
        symboltable.put("kind",k);
        symboltable.put("index",i);
        symboltable.put("scope",s);
    }

    private String countKind (String kind){
        int i = -1;
        List<String> k,s;
        k = symboltable.get("kind");
        s = symboltable.get("scope");
        if(k == null){
            k = new ArrayList<String>();
        }
        if(s == null){
            s = new ArrayList<String>();
        }
        if(k.size() == 1){
            if(s.get(0).contains(""+scope)){
                if(k.get(0).contains(kind)){
                    i++;
                }
            }
        }else{
            for(int j = 0; j<k.size(); j++){
                if(s.get(j).contains(""+scope)){
                    if(k.get(j).contains(kind)){
                        i++;
                    }
                }
            }
        }
        return "" + (i + 1);
    }

    public int varCount(String kind){
        int number = 0;
        List<String> values = symboltable.get("kind");
        List<String> escopo = symboltable.get("scope");
        if (values != null && escopo != null){
            for(int j = 0; j<values.size(); j++){
                if(values.get(j).contains(kind) && escopo.get(j).contains(""+scope)){
                    number++;
                }
            }
            return number;
        }else{
            return 0;
        }
    }

    public String kindOf(String name){
        String rst = "";
        List<String> names = symboltable.get("name");
        List<String> kinds = symboltable.get("kind");
        if(names != null && kinds != null){
            String[] n = new String[names.size()];
            String[] k = new String[kinds.size()];
            n = names.toArray(n);
            k = kinds.toArray(k);
            for(int i = 0; i < n.length; i++){
                if(n[i].contains(name)){
                    rst = k[i];
                    break;
                }
            }
            return rst;
        }else{
            return "";
        }
    }

    public String typeOf(String name){
        String rst ="";

        List<String> names = symboltable.get("name");
        List<String> types = symboltable.get("type");
        if(names != null && types != null) {
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).contains(name)) {
                    rst = types.get(i);
                    break;
                }
            }
            return rst;
        }else{
            return "";
        }
    }

    public int indexOf(String name){
        int rst = -1 ;
        List<String> names = symboltable.get("name");
        List<String> indexes = symboltable.get("index");

        if(names != null && indexes != null){
            for(int i =0; i < names.size(); i++){
                if(names.get(i).contains(name)){
                    rst = Integer.parseInt(indexes.get(i));
                    break;
                }
            }

            return rst;
        }else{
            return 0;
        }
    }

//    public String resolve (String name){
//        List<String> names = symboltable.get("name");
//        List<String> sco = symboltable.get("scope");
//        String rst = "";
//        for(int i = 0; i<names.size();i++){
//            if (names.get(i).contains(name)){
//                rst = sco.get(i);
//                break;
//            }
//        }
//        return rst;
//    }

}
