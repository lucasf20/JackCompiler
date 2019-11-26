package Compilador;

import java.util.*;

public class SymbolTable {

    public static List<String> names, types, kinds, indexes, scopes;
    int scope;

    SymbolTable(){
        names = new ArrayList<String>();
        types = new ArrayList<String>();
        kinds = new ArrayList<String>();
        indexes = new ArrayList<String>();
        scopes = new ArrayList<String>();
        scope = -1;
    }

    public void startSubroutine(){
        scope++;
    }


    public void define(String name, String type, String kind){
        names.add(name);
        types.add(type);
        indexes.add(""+varCount(kind));
        kinds.add(kind);
        scopes.add(""+scope);

    }

    public int varCount(String kind){
        int rst = 0;
        if(!kinds.isEmpty()){
            for(int i = 0; i < kinds.size(); i++){
                if(scopes.get(i).equals(""+scope)){
                    if(kinds.get(i).equals(kind)){
                        rst++;
                    }
                }
            }
        }
        if(kind.equals("field") | kind.equals("static")){
            rst = 0;
            if(!kinds.isEmpty()){
                for(int i = 0; i < kinds.size(); i++){
                    if(scopes.get(i).equals("0")){
                        if(kinds.get(i).equals(kind)){
                            rst++;
                        }
                    }
                }
            }
        }
        return rst;
    }

    public String kindOf(String name){
        String rst ="";
        if(!kinds.isEmpty()){
            for(int i = 0; i < kinds.size(); i++){
                if(scopes.get(i).equals(""+scope)){
                    if(names.get(i).equals(name)){
                        rst = kinds.get(i);
                    }
                }
            }
        }
        if(rst.isEmpty()){
            for(int i = 0; i < kinds.size(); i++){
                if(scopes.get(i).equals("0")){
                    if(names.get(i).equals(name)){
                        rst = kinds.get(i);
                    }
                }
            }
        }
        //System.out.println(name + " " + rst);
        return rst;
    }

    public String typeOf(String name){
        String rst ="";

        if(!types.isEmpty()){
            for(int i = 0; i < types.size(); i++){
                if(scopes.get(i).equals(""+scope)){
                    if(names.get(i).equals(name)){
                        rst = types.get(i);
                    }
                }
            }
        }
        if(rst.isEmpty()){
            for(int i = 0; i < types.size(); i++){
                if(scopes.get(i).equals("0")){
                    if(names.get(i).equals(name)){
                        rst = types.get(i);
                    }
                }
            }
        }
        return rst;

    }

    public int indexOf(String name){
        String rst ="";

        if(!indexes.isEmpty()){
            for(int i = 0; i < indexes.size(); i++){
                if(scopes.get(i).equals(""+scope)){
                    if(names.get(i).equals(name)){
                        rst = indexes.get(i);
                    }
                }
            }
        }
        if(rst.isEmpty()){
            for(int i = 0; i < indexes.size(); i++){
                if(scopes.get(i).equals("0")){
                    if(names.get(i).equals(name)){
                        rst = indexes.get(i);
                    }
                }
            }
        }
        int r = (rst.isEmpty())?-1:Integer.parseInt(rst);
        return r;
    }

}
