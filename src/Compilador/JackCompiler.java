package Compilador;

import java.io.File;

public class JackCompiler {
    public static void main(String[] args){



        for (int i = 0 ; i < args.length; i++) {
            File file = new File (args[0]);
            if (file.isDirectory()) {
                for (File f : file.listFiles()) {
                    if(f.getPath().endsWith(".jack")){
                        new CompilationEngine(f.getPath());
                    }
                }
            } else {
                new CompilationEngine(args[i]);
            }


        }

    }
}
