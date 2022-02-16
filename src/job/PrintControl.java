package job;

import construction.MethodConstruction;
import java.util.ArrayList;
import program.Program;

public class PrintControl {
    public static void print(Program source,MethodConstruction main,int depth)throws Exception{
        source.countNext(2);
        int brackets = 0;
        boolean quotation = false;
        ArrayList<String> formula = new ArrayList<>();
        while(source.getContinue()){
            String str = source.get();
            if(str.equals("\""))quotation = !quotation;
            if(str.equals("(") && quotation == false)brackets++;
            else if(str.equals(")")&& quotation == false){
                brackets--;
                if(brackets == -1)break;
            }
            formula.add(str);
        }
        source.countNext();
        System.out.println(formula);
        
        String result = StringCalculation.pirntCalcRun(formula, main, depth);
        System.out.println(result);
    }
}
