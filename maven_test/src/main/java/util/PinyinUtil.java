package util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * PinYinUtil中存放对拼音的相关操作
 * @author zhaomin
 * @date 2020/1/9 19:49
 */
public class PinyinUtil {
    /**
     * 中文字符格式
     */
    private static final String CHINESE_PATTERN = "[\\u4E00-\\u9FA5]";

    /**
     * 汉语拼音格式化类
     */
    private static final HanyuPinyinOutputFormat FORMAT
            =new HanyuPinyinOutputFormat();
    static{
        //设置拼音小写
        FORMAT.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        //设置不带音调
        FORMAT.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        //设置带v字符
        FORMAT.setVCharType(HanyuPinyinVCharType.WITH_V);
    }

    /**
     * 字符是否包含中文
     * @param name
     * @return
     */
    public static  boolean containsChinese(String name){

        return name.matches(".*"+CHINESE_PATTERN+".*");
    }
    /**
     * 通过文件名获取全拼+拼音首字母
     * 中华人民共和国---》zhongghuarenmingongheguo/zhrmghg(不考虑多音字)
     * @param name 文件名
     * @return 拼音全拼字符串+拼音首字母字符串 数组
     */
    public static String[] get(String name){
        String[] result=new String[2];
        StringBuilder pinyin=new StringBuilder();//全拼
        StringBuilder pinyinFirst=new StringBuilder();//拼音首字母
        for(char c:name.toCharArray()){
            try {
                String[] pinyins
                        =PinyinHelper.toHanyuPinyinStringArray(c,FORMAT);
                if(pinyins==null||pinyins.length==0){
                    pinyin.append(c);
                    pinyinFirst.append(c);
                }else{
                    pinyin.append(pinyins[0]);
                    pinyinFirst.append(pinyins[0].charAt(0));
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
               pinyin.append(c);
               pinyinFirst.append(c);
            }
        }
        result[0]=pinyin.toString();
        result[1]=pinyinFirst.toString();
        return result;
    }

    /**
     *
     * @param name 文件名
     * @param fullSpell true表示全拼，false 取拼音首字母
     * @return 包含多音字符的字符串二位数组
     */
    public static String[][] get(String name,boolean fullSpell){
        char[] chars=name.toCharArray();
        String[][] result = new String[chars.length][];
        for(int i=0;i<chars.length;i++){
            try {
                //去除音调后,会有重复,"只":[zhi,zhi...]---------quchong
                String[] pinyins
                        = PinyinHelper.toHanyuPinyinStringArray(chars[i], FORMAT);
                if(pinyins==null||pinyins.length==0){
                    result[i]=new String[]{String.valueOf(chars[i])};
                }else {
                    result[i]=unique(pinyins,fullSpell);
                }
            } catch (BadHanyuPinyinOutputFormatCombination badHanyuPinyinOutputFormatCombination) {
                result[i]=new String[]{
                        String.valueOf(chars[i])
                };
            }
        }
        return result;
}

    /**
     * 拼音去重
     * @param array
     * @param fullSpell
     * @return
     */
    public static String[] unique(String[] array,boolean fullSpell){
    Set<String> set=new HashSet<>();
    for(String s:array){
        if(fullSpell){
            set.add(s);
        }else{
            set.add(String.valueOf(s.charAt(0)));
        }
    }
    return set.toArray(new String[set.size()]);
}
    /**
     *  每个中文字符返回的是字符串数组,每两个字符串数组合并为一个字符串数组,之后以此类推
     * [he,hu,huo],[zhang,chang]
     * hezhang,hechang,huzhang,huchang,huozhang,huochang
     * @param pinyinArray 文件名多音字数组
     * @return 返回文件名的排列组合
     */
    public static String[] compose(String[][] pinyinArray){
        if(pinyinArray==null||pinyinArray.length==0){
            return  null;
        }else if(pinyinArray.length==1){
            return pinyinArray[0];
        }else{
            for(int i=1;i<pinyinArray.length;i++){
                pinyinArray[0]=compose(pinyinArray[0],pinyinArray[i]);
            }
            return pinyinArray[0];
        }
}

    /**
     * 两个拼音数组进行组合
     * @param pinyins1 第一个拼音参数
     * @param pinyins2 第二个拼音参数
     * @return
     */
    public static String[] compose(String[] pinyins1, String[] pinyins2) {
        String[] result=new String[pinyins1.length*pinyins2.length];
        for (int i=0;i<pinyins1.length;i++) {
            for (int j=0;j<pinyins2.length;j++) {
                    result[i*pinyins2.length+j]=pinyins1[i]+pinyins2[j];
            }
        }
        return result;
    }
    public static void main(String[] args) {

        System.out.println(Arrays.toString(get("中华人民共和国")));
        System.out.println(Arrays.toString(get("中华ABC人民共和国")));
        System.out.println(Arrays.toString(compose(get("和长和",false))));
    }
}
