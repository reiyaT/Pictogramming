package pictogramming;

import java.util.ArrayList;

import processing.core.PApplet;
import programSetup.*;
import blockmake.BlockMake;
import construction.MethodConstruction;
import ordermake.OrderMake;
import originalcompiler.OriginalCompiler;


public class Main {

    public static String errorMessage;
    public static String visualizationFileName;
    private static boolean errorEnd = false;

    public static OrderMake animaOrder; //ordermakeで本を生成

    public static void main(String[] args) {

        //.javaファイルの取得
        String src = FileGet.fileGet();
        System.out.println(src);

        //プログラムを適切な形に分割
        ArrayList<String> srcList = new ArrayList<>();
        if (errorCheck() == false)srcList = SourceSplit.sourceSplit(src);
        System.out.println(srcList);

        //mainメソッドの中身だけを取り出す
        if (errorCheck() == false)GetMainProgram.getMainprogram(srcList);
        System.out.println(srcList);

        //ASTノードの生成
        MethodConstruction programBlock = null;
        if (errorCheck() == false) {
            BlockMake block = BlockMake.makeStart(srcList, 0);
            if (errorCheck() == false) {
                programBlock = new MethodConstruction(block);
            }
        }

        //プログラム内容を実行
        if (errorCheck() == false) {
            animaOrder = new OrderMake(); //台本部分
            OriginalCompiler.compilerStart(programBlock);
        }

        //可視化結果の出力
        if (errorCheck() == false)PApplet.main("view.MainView");

        System.out.println("end");

    }

    public static boolean errorCheck() {
        //errorの処理
        if (errorEnd) return true;

        if (errorMessage != null) {
            PApplet.main("errorview.ErrorView");
            errorEnd = true;
        }

        return errorEnd;
    }
}
