package originalcompiler;

import java.util.ArrayList;
import construction.*;
import program.Program;
import job.*;
import static pictogramming.Main.animaOrder;
import static pictogramming.Main.errorMessage;

public class OriginalCompiler {
    private static MethodConstruction main;
	
    public static void compilerStart(MethodConstruction main){
        try {
            OriginalCompiler.main = main;

            System.out.println("---------------------------run---------------------------");
            main.start();
            runJob(main);
            main.end();
            animaOrder.makeStart(1, 0);
            //animaOrder.programEnd();
            animaOrder.EndMove(); //追加
        } catch (Exception e) {
            errorMessage = "プログラムを実行することができませんでした\n"
                         + "・コンパイルエラーになるプログラムは可視化できません\n"
                         + "・一部の処理は可視化することができません\n"
                         + "例）配列、while文、switch文、関数呼び出し　など";
        }
    }
    
    //分割されたものを見て、次の処理へ指示を出す
    //返り値は1でbreak、2でcontinue
    public static int runJob(BlockConstruction block)throws Exception{
        Program source = block.getSource();
        int depth = block.getDepth();
        int loopFlag = 0;
        System.out.println(source.getContinue()+":source.getContinue()");
        while(source.getContinue()){
            if(source.look().equals(";"))return loopFlag;
            String str = source.look();
            //変数の宣言
            System.out.println(str.equals("while"));
            if(str.equals("break"))return 1;
            else if(str.equals("continue"))return 2;
            else if(str.equals("if")){
                source.countNext();//ifを飛ばす
                BlockConstruction nextBlock = block.getBlock();
                String type = nextBlock.getType();
                if(type.equals("if"))loopFlag = runIf((IfConstruction) nextBlock);
                if(loopFlag > 0)return loopFlag;
                
            }else if(str.equals("for")){
                source.countNext();//forを飛ばす
                BlockConstruction nextBlock = block.getBlock();
                String type = nextBlock.getType();
                if(type.equals("for"))runFor((ForConstruction) nextBlock,block);

            }else if(str.equals("while")){
                System.out.println("else if(str.equals(\"while\"))");
                source.countNext();//whileを飛ばす
                BlockConstruction nextBlock = block.getBlock();
                String type = nextBlock.getType();
                System.out.println(type.equals("while"));
                if(type.equals("while"))runWhile((WhileConstruction) nextBlock,block);


            }else if(str.equals("System.out.println")){
                animaOrder.makeStart(0,block.getDepth());
                job.PrintControl.print(source, main, depth);
                animaOrder.makeEnd();
            }else{
                runVariable(block);
            }
        }
        main.printVariable();
        System.out.println();
        return loopFlag;
    }
    
    private static int runIf(IfConstruction block)throws Exception{
        block.start();
        animaOrder.makeStart(3,block.getDepth()-1); //depthにより背景の色が変わる
        animaOrder.ifStart(block.getCondition(),main,block.getDepth());
        int trueCount = -1;//trueが何番目にあったか
        
        boolean j = false; //ifの条件式(trueかfalseか)
        boolean elseJudge = false;
        for(int i = 0;i < block.getCondition().size();i++){
            IfCondition ifcon = block.getCondition().get(i);
            if(ifcon.getCondition().size() == 0){ //数式が入っていない場合
                j = true;
                elseJudge = true;
            }else j = ConditionJudge.judge(ifcon.getCondition(), main, block.getDepth(),true);
            
            if(j){
                animaOrder.goIf(elseJudge); //移動して扉の中に消えていく
                animaOrder.simpleEnd();
                trueCount = i; //何番目のifか記録
                //IfConditionに格納されているものを受け取る
                block.setBlock(ifcon.blockCopy());
                block.setSource(ifcon.sourceCopy());
                break;
            }else animaOrder.nextCondition();
        }
        
        animaOrder.makeEnd();
        
        if(j == false){
            block.end();
            return 0;
        }
        
        System.out.println("if run");
        int loopFlag = runJob(block);
        
        animaOrder.makeStart(4,block.getDepth());
        animaOrder.ifScope(block.getVariable());
        animaOrder.simpleEnd();
        
        animaOrder.makeStart(3,block.getDepth()-1);
        animaOrder.ifEnd(block.getCondition(),main,block.getDepth(),trueCount);
        while(trueCount < block.getCondition().size()){
            animaOrder.nextCondition();
            trueCount++;
        }
        animaOrder.makeEnd();
        block.end();
        return loopFlag;
    }
    
    private static void runFor(ForConstruction forBlock,BlockConstruction block)throws Exception{
        forBlock.start();
        animaOrder.makeStart(5,block.getDepth());
        animaOrder.forStart(forBlock);
        animaOrder.simpleEnd();
        //変数の準備
        ArrayList<String> loopVar = forBlock.getLoopVariable();
        String loopVarName = "";
        if(loopVar.size() > 1){
            if(loopVar.get(1).equals("=") == false)loopVarName = loopVar.get(1);//新規の変数宣言か(for分の中で使われる変数か)
            runVariable(block,new Program(loopVar),true,false);
            VariableConstruction var = block.getVariable(loopVarName,block.getDepth());
            if(loopVarName.equals("") == false)var.setDepth(var.getDepth()+1);
        }else{
            animaOrder.makeStart(8,forBlock.getDepth());
            animaOrder.forVariableNextJudgeSetting();//追加修正
        }
        
  
        ArrayList<String> condition = forBlock.getCondition();
        ArrayList<String> formula = forBlock.getFormula();
        
        animaOrder.forVariableNextJudge(new IfCondition(condition,null,null), main, forBlock.getDepth(),forBlock);
        
        while(true){
            int loopFlag;//breakやcontinueをやったか
            boolean loop = false;
            if(condition.size() == 0)loop = true;
            else loop = ConditionJudge.judge(condition, main, forBlock.getDepth(),true); //条件式を考えて箱をびんた
            
            if(loop){
                animaOrder.makeEnd();
                
                System.out.println("for run");
                loopFlag = runJob(forBlock);
                
                animaOrder.makeStart(9,forBlock.getDepth());
                animaOrder.forScope(forBlock.getVariable(),forBlock);
                
                forBlock.reset(); //forの中の変数等を元に戻す
                if(formula.size() > 1)runVariable(block,new Program(formula),false,true); //i++
                
                animaOrder.forReturn();
                animaOrder.simpleEnd();
                
                animaOrder.makeStart(8,forBlock.getDepth());
                animaOrder.forReJudege(new IfCondition(condition,null,null), main,forBlock.getDepth(),forBlock);
               
                if(loopFlag == 1)break;
            }else break;
        }
        
        animaOrder.forEndBack();

        if(loopVarName.equals("") == false){ //loop変数の削除
            System.out.println(loopVarName);
            animaOrder.forLoopVarScope(main.getVariable(loopVarName,forBlock.getDepth()),forBlock);
            block.removeVariable(loopVarName);
        }
        
        animaOrder.forOut();
        animaOrder.simpleEnd();
        animaOrder.makeStart(5,block.getDepth());
        animaOrder.forEnd(forBlock);
        animaOrder.makeEnd();
        forBlock.end();
    }

    private static void runWhile(WhileConstruction whileBlock,BlockConstruction block)throws Exception{
        whileBlock.start();
        animaOrder.makeStart(5,block.getDepth());
        animaOrder.whileStart(whileBlock);
        animaOrder.simpleEnd();
        ArrayList<String> loopVar = whileBlock.getLoopVariable();
        String loopVarName = "";

        if(loopVar.size() > 1){
            if(loopVar.get(1).equals("=") == false)loopVarName = loopVar.get(1);//新規の変数宣言か
            runVariable(block,new Program(loopVar),true,false);
            VariableConstruction var = block.getVariable(loopVarName,block.getDepth());
            if(loopVarName.equals("") == false)var.setDepth(var.getDepth()+1);
        }else animaOrder.makeStart(8,whileBlock.getDepth());

        ArrayList<String> condition = whileBlock.getCondition();

        //ArrayList<String> formula = whileBlock.getFormula();

        animaOrder.whileVariableNextJudge(new IfCondition(condition,null,null), main, whileBlock.getDepth(),whileBlock);

        while(true){
            int loopFlag;//breakやcontinueをやったか
            boolean loop = false;
            if(condition.size() == 0)loop = true;
            else loop = ConditionJudge.judge(condition, main, whileBlock.getDepth(),true);
            System.out.println("runWhile[],"+loop);
            if(loop){
                animaOrder.makeEnd();

                System.out.println("while run");
                loopFlag = runJob(whileBlock);

                animaOrder.makeStart(9,whileBlock.getDepth());
                animaOrder.whileScope(whileBlock.getVariable(),whileBlock);

                whileBlock.reset();
                //if(formula.size() > 1)runVariable(block,new Program(formula),false,true);

                animaOrder.whileReturn();
                animaOrder.simpleEnd();

                animaOrder.makeStart(8,whileBlock.getDepth());
                animaOrder.whileReJudege(new IfCondition(condition,null,null), main,whileBlock.getDepth(),whileBlock);

                if(loopFlag == 1)break;
            }else break;
        }

        animaOrder.whileEndBack();

        if(!loopVarName.equals("")){
            System.out.println(loopVarName);
            animaOrder.whileLoopVarScope(main.getVariable(loopVarName,whileBlock.getDepth()),whileBlock);
            block.removeVariable(loopVarName);
        }

        animaOrder.whileOut();
        animaOrder.simpleEnd();
        animaOrder.makeStart(5,block.getDepth());
        animaOrder.whileEnd(whileBlock);
        animaOrder.makeEnd();
        whileBlock.end();
    }
    
    private static void runVariable(BlockConstruction block)throws Exception{
        Program source = block.getSource();
        runVariable(block,source,false,false);
    }
    
    private static void runVariable(BlockConstruction block,Program source,boolean forStart,boolean forEnd)throws Exception{
        int depth = block.getDepth();
        
        String str = source.look();
        if(str.equals("int")){
            if(forStart){
                animaOrder.makeStart(7,block.getDepth()+1);
                animaOrder.forVariableNextJudgeSetting();
                animaOrder.whileVariableNextJudgeSetting();
            }else animaOrder.makeStart(2,block.getDepth());
            
            block.addVariable(VariableControl.declaration(source, main, block,depth,forStart));
        }else if(str.equals("double")){
            if(forStart){
                animaOrder.makeStart(7,block.getDepth()+1);
                animaOrder.forVariableNextJudgeSetting();
                animaOrder.whileVariableNextJudgeSetting();
            }else animaOrder.makeStart(2,block.getDepth());
            
            block.addVariable(VariableControl.declaration(source, main, block,depth,forStart));
        }else if(str.equals("boolean")){
            if(forStart){
                animaOrder.makeStart(7,block.getDepth()+1);
                animaOrder.forVariableNextJudgeSetting();
                animaOrder.whileVariableNextJudgeSetting();
            }else animaOrder.makeStart(2,block.getDepth());
            
            block.addVariable(VariableControl.declaration(source, main, block,depth,forStart));
        }else if(str.equals("String")){
            if(forStart){
                animaOrder.makeStart(7,block.getDepth()+1);
                animaOrder.forVariableNextJudgeSetting();
                animaOrder.whileVariableNextJudgeSetting();
            }else animaOrder.makeStart(2,block.getDepth());
            
            block.addVariable(VariableControl.declaration(source, main, block,depth,forStart));
        }else{//予約語に当てはまらないから変数の可能性大
            VariableConstruction var = main.getVariable(str,depth);
            if(var == null)throw new Exception();
            String str2 = source.look(1);//=なら初期化
            String str3 = source.look(2);//=なら追加代入、+-ならインクリメント等
            if(str2.equals("=")){//初期化
                if(forStart){
                    animaOrder.makeStart(6,block.getDepth()+1);
                    animaOrder.forVariableNextJudgeSetting();
                    animaOrder.whileVariableNextJudgeSetting();
                }else if(forEnd);
                else animaOrder.makeStart(0,block.getDepth());
                VariableControl.initialization(source, main,depth,forEnd);
            }
            else if(str3.equals("=")){
                if(forStart){
                    animaOrder.makeStart(6,block.getDepth()+1);
                    animaOrder.forVariableNextJudgeSetting();
                    animaOrder.whileVariableNextJudgeSetting();
                }else if(forEnd);
                else animaOrder.makeStart(0,block.getDepth());
                VariableControl.operatorInitialization(source, main,depth,forEnd);
            }
            else if(str3.equals("+")){
                if(forEnd == false){
                    animaOrder.makeStart(0,block.getDepth());
                    VariableControl.increment(source,var,forEnd);
                    animaOrder.makeEnd();
                }else{
                    VariableControl.increment(source,var,forEnd);
                }
            }
            else if(str3.equals("-")){
                if(forEnd == false){
                    animaOrder.makeStart(0,block.getDepth());
                    VariableControl.increment(source,var,forEnd);
                    animaOrder.makeEnd();
                }
                VariableControl.decrement(source,var,forEnd);
            }
        }
        if(forStart == false && forEnd == false)animaOrder.makeEnd();

        //animaOrder.makeStart(0,3);
        //animaOrder.sample();
        //animaOrder.sample2();
        //animaOrder.makeEnd();
    }
}
