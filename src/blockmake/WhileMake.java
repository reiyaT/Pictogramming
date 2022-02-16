package blockmake;

import construction.WhileConstruction;
import java.util.ArrayList;
import program.Program;

public class WhileMake {
    //whileノードの構築
    static WhileConstruction make(ArrayList<String> source,int depth){

        depth++;//新しく作るwhileはどこかに属しているので、深みを１上げる

        source.remove(0);//(を削除
        //条件式を取得
        ArrayList<String> condition = getCondition(source);

        ArrayList<String> resultSource = getProgram(source);

        BlockMake bm = BlockMake.make(resultSource, depth);

        return new WhileConstruction(condition,bm,depth);
    }

    private static ArrayList<String> getCondition(ArrayList<String> source){
        ArrayList<String> result = new ArrayList<String>();
        while(true){
            String str = source.get(0);
            source.remove(0);

            if(str.equals(")"))break;
            result.add(str);
        }
        return result;
    }

    private static ArrayList<String> getFormula(ArrayList<String> source){
        ArrayList<String> result = new ArrayList<String>();

        int brackets = 0;
        while(true){
            String str = source.get(0);
            source.remove(0);
            if(str.equals("("))brackets++;
            else if(str.equals(")"))brackets--;
            if(brackets < 0)break;
            result.add(str);
        }

        //programとして使えるように
        result.add(";");
        return result;
    }

    //{}内のプログラムを取得する
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
