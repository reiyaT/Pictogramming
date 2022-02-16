package program;
import java.util.ArrayList;

public class Program {
    private ArrayList<String> source = new ArrayList<String>();
    private int count = 0;
    
    public Program(ArrayList source){
        this.source = source;
    }
    
    public void reset(){
        count = 0;
    }
    
    public String look(){
        return source.get(count);
    }
    
    public String look(int num){
        return source.get(count+num);
    }
    
    public String get(){
        String str = source.get(count);
        count++;
        return str;
    }
    
    public void countNext(){
        this.count++;
    }
    
    public void countNext(int num){
        this.count += num;
    }
    
    //最後まで見たか確認する
    public boolean getContinue(){
        if(count == source.size())return false;
        return true;
    }
    
    public void printProgram(){
        for(int i = 0; i < source.size();i++){
            System.out.print(source.get(i)+" ");
            if(source.get(i).equals(";"))System.out.println();
        }
    }
    
    public Program copy(){
        return new Program(this.source);
    }
}