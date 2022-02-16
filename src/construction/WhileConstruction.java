package construction;

import blockmake.BlockMake;
import java.util.ArrayList;
import program.Program;

public class WhileConstruction extends BlockConstruction{
    private static int instanceCount = 1;
    private ArrayList<String> loopVariable = new ArrayList<>();
    private ArrayList<String> condition = new ArrayList<>();
    private ArrayList<String> formula = new ArrayList<>();

    public WhileConstruction(ArrayList<String> condition,BlockMake bm,int depth) {
        super(depth, "while"+instanceCount, "while",bm.getSource(),bm.getBlock());
        instanceCount++;

        this.condition = condition;
        this.formula = formula;
    }
    public ArrayList<String> getCondition(){

        return this.condition;
    }
    public ArrayList<String> getFormula(){
        return this.formula;
    }
    public ArrayList<String> getLoopVariable(){
        return this.loopVariable;
    }

}
