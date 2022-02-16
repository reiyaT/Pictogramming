package programSetup;

import java.util.ArrayList;

public class SourceSplit {
    //srcファイルのプログラムを特定の記号で分割

    private static ArrayList<String> splitSource;

    public static ArrayList<String> sourceSplit(String src) {
        splitSource = new ArrayList<>();
        splitSource.add(src);

        splitSource = keywordSplit("\"", false);

        splitSource = keywordSplit(" ", true);
        splitSource = keywordSplit("\n", true);
        splitSource = keywordSplit("\t", true);

        splitSource = keywordSplit(";", false);
        splitSource = keywordSplit("(", false);
        splitSource = keywordSplit(")", false);
        splitSource = keywordSplit("{", false);
        splitSource = keywordSplit("}", false);

        splitSource = keywordSplit("|", false);
        splitSource = keywordSplit("&", false);
        splitSource = keywordSplit("!", false);
        splitSource = keywordSplit("<", false);
        splitSource = keywordSplit(">", false);

        splitSource = keywordSplit("=", false);
        splitSource = keywordSplit("+", false);
        splitSource = keywordSplit("-", false);
        splitSource = keywordSplit("*", false);
        splitSource = keywordSplit("/", false);
        splitSource = keywordSplit("%", false);



        splitDebug();
        //１行のコメントアウトの削除
        splitSource = keywordSplit("\r", false);
        splitSource = commentOutLineDelete();

        splitSource = keywordSplit("\r", true);

        //範囲のコメントアウトの削除
        splitSource = commentOutDelete();

        return splitSource;
    }

    private static ArrayList<String> keywordSplit(String keyword, boolean delete) {//keywordで分割、deleteは分割後keywordをプログラムに含むか否か
        ArrayList<String> resultSource = new ArrayList<>();

        boolean skipFlag = false;//"文字列"内を分割しないためのフラグ

        for (int i = 0; i < splitSource.size(); i++) {
            String srcWord = splitSource.get(i);//分割したプログラムを１つずつ取得
            if (srcWord.equals("\""))skipFlag = !skipFlag;
            
            if (skipFlag) {
                resultSource.add(srcWord);
                continue;
            }

            int charCount = 0;
            String splitSrcWord = "";//分割したプログラムを１文字ずつ解析
            while (charCount < srcWord.length()) {
                String srcChar = srcWord.substring(charCount, charCount + 1);

                if (srcChar.equals(keyword)) {
                    if (splitSrcWord.equals("") == false) {
                        resultSource.add(splitSrcWord);
                        splitSrcWord = "";
                    }
                    if (delete == false)resultSource.add(keyword);
                } else {
                    splitSrcWord += srcChar;
                }
                charCount++;
            }
            if (splitSrcWord.equals("") == false)resultSource.add(splitSrcWord);
        }

        return resultSource;
    }

    private static ArrayList<String> commentOutLineDelete() {
        ArrayList<String> resultSource = new ArrayList<>();

        boolean firstSlash = false;//コメントアウトの/があるかのフラグ
        boolean secondSlash = false;//コメントアウトの/があるかのフラグ
        boolean delete = false;

        for (int i = 0; i < splitSource.size(); i++) {
            String srcWord = splitSource.get(i);

            if (delete) {
                if (srcWord.contentEquals("\r"))delete = false;

                //コメントアウトは含まないためcontinue
                continue;
            }

            if (srcWord.equals("/")) {
                if (firstSlash)secondSlash = true;
                else firstSlash = true;
            } else {
                firstSlash = false;
            }

            resultSource.add(srcWord);

            if (secondSlash) {
                firstSlash = false;
                secondSlash = false;

                delete = true;

                //コメントアウトの//を削除
                int lastIndex = resultSource.size() - 1;
                resultSource.remove(lastIndex);

                lastIndex = resultSource.size() - 1;
                resultSource.remove(lastIndex);
            }

        }

        return resultSource;
    }

    private static ArrayList<String> commentOutDelete() {
        ArrayList<String> resultSource = new ArrayList<>();

        boolean asterisk = false;
        boolean slash = false;

        boolean delete = false;

        for (int i = 0; i < splitSource.size(); i++) {
            String srcWord = splitSource.get(i);

            //コメントアウトの範囲か判定
            if (delete) {
                if (asterisk) {
                    if (srcWord.equals("/"))slash = true;
                }
                if (srcWord.equals("*"))asterisk = true;

            } else {
                if (slash) {
                    if (srcWord.equals("*"))asterisk = true;
                    else slash = false;
                    
                }
                if (srcWord.equals("/"))slash = true;
                
            }

            //範囲外ならプログラムをそのまま入れる
            if (delete == false) {
                resultSource.add(srcWord);
            }

            //コメントアウトの範囲外に入る・出る時の処理
            if (asterisk && slash) {
                asterisk = false;
                slash = false;

                if (delete) {
                    delete = false;
                } else {
                    delete = true;

                    //始めの/*を削除する
                    int lastIndex = resultSource.size() - 1;
                    resultSource.remove(lastIndex);

                    lastIndex = resultSource.size() - 1;
                    resultSource.remove(lastIndex);
                }
            }
        }

        return resultSource;
    }

    public static void splitDebug() {
        System.out.println(splitSource);
    }
}
