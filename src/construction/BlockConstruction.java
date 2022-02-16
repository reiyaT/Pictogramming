package construction;

import java.util.ArrayList;
import program.Program;

public abstract class BlockConstruction {
    protected boolean end = false;
    protected boolean start = false;
    protected int depth;
    protected String name;
    protected String type;
    protected ArrayList<VariableConstruction> variable = new ArrayList<VariableConstruction>();
    protected ArrayList<BlockConstruction> block = new ArrayList<BlockConstruction>();
    protected Program source;
    
    BlockConstruction(int depth,String name,String type){
        this.depth = depth;
        this.name = name;
        this.type = type;
    }
    
    BlockConstruction(int depth,String name,String type,Program source,ArrayList<BlockConstruction> block){
        this.depth = depth;
        this.name = name;
        this.type = type;
        this.source = source;
        this.block = block;
    }
    
    public boolean getEnd(){
        return this.end;
    }
    
    public boolean getStart(){
        return this.start;
    }
    
    public int getDepth(){
        return this.depth;
    }
    
    public String getName(){
        return this.name;
    }
    
    public String getType(){
        return this.type;
    }
    
    public Program getSource(){
        return this.source;
    }
    
    public void end(){
        this.end = true;
    }
    
    public void start(){
        this.start = true;
    }
    
    public VariableConstruction getVariable(String searchName, int depth) {
        //探索すべき範囲を越えたらnullで返す
        if(this.depth > depth)return null;
        if(end)return null;
        for (VariableConstruction var : variable) {
            String name = var.getName();
            if (name.equals(searchName))return var;
        }
        for(BlockConstruction b : block){
            if(b.getEnd())continue;
            return b.getVariable(searchName,depth);
        }
        return null;
    }
    
    public void removeVariable(String searchName){
        //if()飛ばすやつ作る
        for(int i = 0;i < this.variable.size();i++){
            String name = this.variable.get(i).getName();
            if(name.equals(searchName)){
                this.variable.remove(i);
                return;
            }
        }
        for(BlockConstruction b : this.block)b.removeVariable(searchName);
    } 
    
    public BlockConstruction getBlock(){
        for(BlockConstruction b : block){
            if(b.getEnd())continue;
            return b;
        }
        return null;
    }
    
    public void addVariable(VariableConstruction var){
        variable.add(var);
    }
    
    public ArrayList<VariableConstruction> getVariable(){
        return this.variable;
    }
    
    public void reset(){
        this.variable.clear();
        for(BlockConstruction b:this.block)b.reset();
        if(this.source != null)this.source.reset();
        if(this.end)this.start = false;
        this.end = false;
    }
    
    public void printVariable() {
        if(this.end || this.start == false)return;
        for(int i = 0;i < depth;i++)System.out.print("　");
        System.out.println(name+"----------------");
        for(int i = 0;i < variable.size();i++){
            for(int j = 0;j < depth;j++)System.out.print("　");
            System.out.println(variable.get(i));
        }
        for(BlockConstruction bc : this.block)bc.printVariable();
    }
    
    public void printConstruction() {
        for(int i = 0;i < this.depth;i++)System.out.print(" ");
        System.out.println(this.name);
        for(BlockConstruction bl : this.block){
            bl.printConstruction();
        }
    }
    
    public ArrayList<BlockConstruction> blockCopy(){
        ArrayList<BlockConstruction> block = new ArrayList<BlockConstruction>();
        block.addAll(this.block);
        return block;
    }
}
