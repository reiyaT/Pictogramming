package blockmake;

import construction.BlockConstruction;
import java.util.ArrayList;
import program.Program;
import static pictogramming.Main.errorMessage;

public class BlockMake {
    private Program source;
    private ArrayList<BlockConstruction> block = new ArrayList<BlockConstruction>();
    
    public BlockMake(Program resultProgram,ArrayList<BlockConstruction> resultBlock){
        this.source = resultProgram;
        this.block = resultBlock;
    }
    
    public static BlockMake makeStart(ArrayList<String> source,int depth) {
    	BlockMake blockMake = null;
    	try {
            blockMake = make(source,depth);
    	}catch(Exception e) {
            errorMessage = "プログラムを実行することができませんでした\n"
			 + "・コンパイルエラーになるプログラムは可視化できません\n"
                         + "・一部の処理は可視化することができません\n"
			 + "例）配列、while文、switch文、関数呼び出し　など";
    	}
    	return blockMake;
    }
    
    static BlockMake make(ArrayList<String> source,int depth) {
    	ArrayList<String> result = new ArrayList<String>();
        ArrayList<BlockConstruction> resultBlock = new ArrayList<BlockConstruction>();
        
        while(source.size() > 0){
            String split = source.get(0);
            
            //ifをつくる
            if(split.equals("if")){
                System.out.println("make if");
                result.add(split);
                source.remove(0);//ifを削除
                
                resultBlock.add(IfMake.make(source,depth));
            }else if(split.equals("for")){
                System.out.println("make for");
                result.add(split);
                source.remove(0);//forを削除
                
                resultBlock.add(ForMake.make(source, depth));

            }else if(split.equals("while")){
                System.out.println("make while");
                result.add(split);
                for(String s:result){
                    System.out.println(s);
                }
                System.out.println();
                for(String s:source){
                    System.out.println(s);
                }
                System.out.println();
                source.remove(0);//whileを削除

                resultBlock.add(WhileMake.make(source, depth));
            }else{
                result.add(split);
                source.remove(0);
            }
        }
        
        return new BlockMake(new Program(result),resultBlock);
    }
    
    public Program getSource(){
        return source;
    }
    
    public ArrayList<BlockConstruction> getBlock(){
        return block;
    }
}
