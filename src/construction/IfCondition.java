package construction;

import java.util.ArrayList;
import program.Program;

public class IfCondition {
    private ArrayList<String> condition = new ArrayList<String>();
    private ArrayList<BlockConstruction> block = new ArrayList<BlockConstruction>();
    private Program source;
    //conditionの条件が満たされた時、blockの制御構造とsourceのプログラムが実行される
    
    public IfCondition(ArrayList<String> condition,Program source,ArrayList<BlockConstruction> block){
        this(source,block);
        this.condition = condition;
    }
    
    public IfCondition(Program source,ArrayList<BlockConstruction> block){
        this.source = source;
        this.block = block;
    }
    
    public ArrayList<String> getCondition(){
        return condition;
    }
    
    public ArrayList<BlockConstruction> blockCopy(){
        ArrayList<BlockConstruction> result = new ArrayList<>();
        result.addAll(block);
        return result;
    }
    
    public ArrayList<BlockConstruction> getBlock(){
        return this.block;
    }
    
    public Program sourceCopy(){
        return this.source.copy();
    }
}
