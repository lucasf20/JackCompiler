package Compilador;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VMWriter {
    private static Path file;

    public static String codigo;

    public VMWriter(String filepath){
        file = Paths.get(filepath.replaceAll(".jack",".vm"));
        writeFile("");
    }

    private void writeFile(String conteudo){
        try{
            Files.write(file,conteudo.getBytes());
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void writePush(String segment, int index){
        codigo += "push " + segment + " " + index + "\n";
    }

    public void writePop(String segment, int index){
        codigo += "pop " + segment + " " + index + "\n";
    }

    public void writeArithmetic(String comand){
        codigo += comand + "\n";
    }

    public void writeLabel(String label){
        codigo  +=  "label " + label + "\n";
    }

    public void writeGoTo(String label){
        codigo  += "goto " + label + "\n";
    }

    public void writeIf(String label){
        codigo += "if-goto " + label + "\n";
    }

    public void writeCall(String name, int nArgs){
        codigo += "call " + name + " " + nArgs + "\n";
    }

    public void writeFunction(String name, int nLocals){
        codigo += "function " + name + " " + nLocals + "\n";
    }

    public void writeReturn(){
        codigo += "return \n";
    }

    public void close(){
        writeFile(codigo);
    }
}
