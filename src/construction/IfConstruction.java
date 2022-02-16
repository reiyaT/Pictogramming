package construction;

import java.util.ArrayList;
import program.Program;


public class IfConstruction extends BlockConstruction{
    private static int instanceCount = 1;
    //条件に合わせてプログラムを格納
    private ArrayList<IfCondition> condition = new ArrayList<IfCondition>();
    //継承されたblockは実行する制御構造を入れる
    
    public IfConstruction(ArrayList<IfCondition> condition,int depth){
        super(depth,"If"+instanceCount,"if");
        instanceCount++;
        
        this.condition = condition;
    }
    
    public ArrayList<IfCondition> getCondition(){
        return this.condition;
    }
    
    public void setSource(Program program){
        this.source = program;
    }
    
    public void setBlock(ArrayList<BlockConstruction> block){
        this.block = block;
    }
    
    public void printConstruction() {
        for(IfCondition ifcon : this.condition){
            for(int i = 0;i < this.depth;i++)System.out.print("　");
            if(ifcon.getCondition().size() == 0)System.out.println(this.name+"else↓");
            else System.out.println(this.name+"if↓");
            for(BlockConstruction bl : ifcon.getBlock()){
                bl.printConstruction();
            }
        }
    }
}
