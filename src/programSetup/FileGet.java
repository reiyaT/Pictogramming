package programSetup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JFileChooser;

import static pictogramming.Main.errorMessage;
import static pictogramming.Main.visualizationFileName;

public class FileGet {
    //.javaファイルを取得する

    public static String fileGet() {
        String src = "";

        JFileChooser chooser = new JFileChooser();
        //ダイアログの表示
        chooser.showOpenDialog(null);
        //ダイアログの選択結果を取得
        File file = chooser.getSelectedFile();
        FileReader fileReader = null;
        try {
            //ファイルの探索

            File mainClassFile = file;
            visualizationFileName = file.toString();

            fileReader = new FileReader(mainClassFile);

            //1文字ずつ取得する
            int data;
            while ((data = fileReader.read()) != -1) {
                //System.out.print((char) data);
                src += String.valueOf((char) data);
            }

        } catch (IOException e) {
            e.printStackTrace();
            errorMessage = e.getMessage();

        } finally {
            try {
                if (fileReader != null) {
                    //ファイルを閉じる
                    fileReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return src;
    }
}
