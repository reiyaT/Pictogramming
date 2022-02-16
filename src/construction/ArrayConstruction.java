package construction;

public class ArrayConstruction {
    private String type;//型
    private String name;//名前
    private String value;//値
    private int depth;
    public ArrayConstruction(String type, String name, String value, int depth) {
        this.type = type;
        this.name = name;
        this.value = value;
        this.depth = depth;
    }

    public ArrayConstruction(String type, String name, int depth) {

        this(type, name, null,depth);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName(){

        return this.name;
    }

    public String getType(){
        return this.type;
    }

    public String getValue(){
        if(this.value == null)return null;//System.out.println(this.name + "に値はありません！");
        return this.value;
    }

    public int getDepth(){
        return this.depth;
    }

    public void setDepth(int depth){
        this.depth = depth;
    }

    public VariableConstruction copy(){
        if(this.value == null)return new VariableConstruction(this.type,this.name,this.depth);
        return new VariableConstruction(this.type,this.name,this.value,this.depth);
    }

    @Override
    public String toString() {
        if (value == null) return "type:" + type + " name:" + name + " value:null";
        return "type:" + type + " name:" + name + " value:" + value;
    }
}
