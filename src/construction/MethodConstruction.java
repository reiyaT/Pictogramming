package construction;

import blockmake.BlockMake;
import java.util.ArrayList;
import program.Program;

//メインメソッド
public class MethodConstruction extends BlockConstruction{
    public MethodConstruction(BlockMake bm){
        super(0,"main","method",bm.getSource(),bm.getBlock());
    }
    
    public MethodConstruction(String name,BlockMake bm){
        super(0,name,"method");
        this.block = bm.getBlock();
        this.source = bm.getSource();
    }
}
