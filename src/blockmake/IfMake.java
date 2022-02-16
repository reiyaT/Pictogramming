package blockmake;

import construction.BlockConstruction;
import construction.IfConstruction;
import construction.IfCondition;
import java.util.ArrayList;
import program.Program;

public class IfMake {
    //ifノードの構築
    static IfConstruction make(ArrayList<String> source,int depth){
        depth++;//新しく作るifはどこかに属しているので、深みを１上げる
        ArrayList<IfCondition> ifcon = new ArrayList<IfCondition>(); 
        
        ifcon.add(newIfCondition(source,depth));
        //else ifでつながるか判定
        while(source.size() != 0){
            if(source.get(0).equals("else")){
                source.remove(0);//elseの削除
                if(source.get(0).equals("if")){
                    source.remove(0);//ifの削除
                    ifcon.add(newIfCondition(source,depth));
                }else{
                    ifcon.add(newElseCondition(source,depth));
                }
            }else break;
        }
        return new IfConstruction(ifcon,depth);
    }
    
    private static IfCondition newIfCondition(ArrayList<String> source,int depth){
        //条件を格納
        source.remove(0);//"("の削除
        
        ArrayList<String> condition = new ArrayList<String>();
        int brackets = 0;
        while(true){
            String str = source.get(0);
            source.remove(0);
            if(str.equals("("))brackets++;
            if(str.equals(")"))brackets--;
            if(str.equals(")") && brackets == -1)break;
            condition.add(str);
        }
        //{}内のプログラムを格納
        ArrayList<String> ifSource = getProgram(source);
        
        BlockMake result = BlockMake.make(ifSource, depth);
        
        return new IfCondition(condition,result.getSource(),result.getBlock());
    }
    
    private static IfCondition newElseCondition(ArrayList<String> source,int depth){
        //{}内のプログラムを格納
        ArrayList<String> ifSource = getProgram(source);
        BlockMake result = BlockMake.make(ifSource, depth);
        
        return new IfCondition(result.getSource(),result.getBlock());
    }
    
    //{}内のプログラムを獲得する
    private static ArrayList<String> getProgram(ArrayList<String> source){
        ArrayList<String> ifSource = new ArrayList<String>();
        //{}か１行かを判定
        if(source.get(0).equals("{")){
            source.remove(0);//"{"の削除
            int brackets = 0;
            while(true){
                String str = source.get(0);
                source.remove(0);
                if(str.equals("{"))brackets++;
                if(str.equals("}"))brackets--;
                if(str.equals("}") && brackets == -1)break;
                ifSource.add(str);
            }
        }else{
            while(true){
                String str = source.get(0);
                source.remove(0);
                ifSource.add(str);
                if(str.equals(";"))break;
            }
        }
        return ifSource;
    }
}
