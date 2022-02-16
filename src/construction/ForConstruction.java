package construction;

import blockmake.BlockMake;
import java.util.ArrayList;
import program.Program;

public class ForConstruction extends BlockConstruction{
    //for文では０番目の要素にループ変数が格納されます
    private static int instanceCount = 1;
    private ArrayList<String> loopVariable = new ArrayList<>();
    private ArrayList<String> condition = new ArrayList<>();
    private ArrayList<String> formula = new ArrayList<>();
    
    public ForConstruction(ArrayList<String> loopVariable, ArrayList<String> condition,ArrayList<String> formula,BlockMake bm,int depth){
        super(depth,"for"+instanceCount,"for",bm.getSource(),bm.getBlock());
        instanceCount++;
        
        this.loopVariable = loopVariable;
        this.condition = condition;
        this.formula = formula;
    }
    
    public ArrayList<String> getLoopVariable(){
        return this.loopVariable;
    }
    
    public ArrayList<String> getCondition(){
        return this.condition;
    }
    
    public ArrayList<String> getFormula(){
        return this.formula;
    }
}
