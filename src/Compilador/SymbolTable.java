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
        String[] val = new String[values.size()];
        val = values.toArray(val);

        for(String v:val){
            if(v == kind){
                number++;
            }
        }

        return number;
    }

    public String kindOf(String name){
        String rst = "";
        List<String> names = symboltable.get("name");
        List<String> kinds = symboltable.get("kind");
        String[] n = new String[names.size()];
        String[] k = new String[kinds.size()];
        n = names.toArray(n);
        k = kinds.toArray(k);
        for(int i = 0; i < n.length; i++){
            if(n[i] == name){
                rst = k[i];
                break;
            }
        }
        return rst;
    }

    public String typeOf(String name){
        String rst ="";

        List<String> names = symboltable.get("name");
        List<String> types = symboltable.get("type");

        for(int i =0; i < names.size(); i++){
            if(names.get(i) == name){
                rst = types.get(i);
                break;
            }
        }

        return rst;
    }

    public int indexOf(String name){
        int rst = -1 ;
        List<String> names = symboltable.get("name");
        List<String> indexes = symboltable.get("index");

        for(int i =0; i < names.size(); i++){
            if(names.get(i) == name){
                rst = Integer.parseInt(indexes.get(i));
                break;
            }
        }

        return rst;
    }

}
