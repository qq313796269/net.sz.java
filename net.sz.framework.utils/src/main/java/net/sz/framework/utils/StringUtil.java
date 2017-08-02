package net.sz.framework.utils;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;
import net.sz.framework.szlog.SzLogger;

/**
 * utf-8字符集操作
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/shizuchengxuyuan/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class StringUtil {

    private static final SzLogger log = SzLogger.getLogger();

    public static final String EMPTY_STRING = "";
    public static final int ZERO = 0;
    public static final String Empty = "";
    public static final String EmptyZero = "{0:0}";
    public static final String FENHAO = ";";//分号
    public static final String FENHAO_REG = ";|；";//分号
    public static final String MAOHAO_REG = ":|：";//冒号
    public static final String MAOHAO_1_REG = ":|：|=|_";//冒号
    public static final String DOUHAO = ",";
    public static final String DOUHAO_REG = ",|，";
    public static final String XIEGANG_REG = "/";
    public static final String SHUXIAN_REG = "\\|";
    public static final String XIAHUAXIAN_REG = "_";
    public static final String JINGHAO_REG = "\\#";
    public static final String DENGHAO = "=";
    public static final String AT_REG = "@";

    /**
     * 验证必须是 数字 or 字母 or 下划线
     */
    public static final Pattern PATTERN_ABC_0 = Pattern.compile("^[_\\s@a-zA-Z0-9]{0,}$");
    /**
     * 过滤 非 数字 or 字母 or 下划线
     */
    public static final Pattern PATTERN_REPLACE_ABC_0 = Pattern.compile("[^_\\s@a-zA-Z0-9\\u4e00-\\u9fa5]");
    /**
     * 验证必须是 数字 or 字母 or 下划线
     */
    public static final Pattern PATTERN_ABC_PWD = Pattern.compile("^[~\"\"?<>\\[\\]={}_\\-*&()!:@$%\\^.\\s@a-zA-Z0-9]{0,}$");
    /**
     * 验证只能是,汉字，数字，字母，下划线
     */
    public static final Pattern PATTERN_ABC_1 = Pattern.compile("^[_\\s@a-zA-Z0-9\\u4e00-\\u9fa5]{0,}$");
    /**
     * 验证只能是,汉字，数字，字母
     */
    public static final Pattern PATTERN_ABC_2 = Pattern.compile("^[\\s@a-zA-Z0-9\\u4e00-\\u9fa5]{0,}$");
    /**
     * 过滤 非 汉字，数字，字母，下划线
     */
    public static final Pattern PATTERN_REPLACE_ABC_1 = Pattern.compile("[^_\\s@a-zA-Z0-9\\u4e00-\\u9fa5]");
    /**
     * 验证必须是字母开头
     */
    public static final Pattern PATTERN_ABC = Pattern.compile("^[a-zA-Z]\\S+$");

    /**
     * 纯汉字
     */
    public static final Pattern PATTERN_UUU = Pattern.compile("^[\\u4e00-\\u9fa5]{0,}$");
    /**
     * 过滤 非 纯汉字
     */
    public static final Pattern PATTERN_REPLACE_UUU = Pattern.compile("[^\\u4e00-\\u9fa5]");

    /**
     * 汉字，字母，数字，以及一些常规字符
     */
    public static final Pattern PATTERN_ABC_00_UUU = Pattern.compile("^[~`“”\"\"?<>\\[\\]【】{}_\\-——=《》*&（）()!！:：#@$￥%……\\^.。,，\\s@a-zA-Z0-9\\u4e00-\\u9fa5]{0,}$");
    /**
     * 过滤 非 汉字，字母，数字，以及一些常规字符
     */
    public static final Pattern PATTERN_REPLACE_ABC_00_UUU = Pattern.compile("[^~`“”\"\"?<>\\[\\]【】{}_=\\-——《》*&（）()!！:：#@$￥%……\\^.。,，\\s@a-zA-Z0-9\\u4e00-\\u9fa5]");

    public static void main(String[] args) {

        log.error(StringUtil.convert_InBase64_ASE("token=1a45ab3830b340de80da75e124d246f2&cmd=reloadscript大发了书法家拉设计费拉束带结发离开", 3, 2));

//        log.error("数字 or 字母 or 下划线 " + StringUtil.replaceFilter("d阿德发发发委托__-——ASDGFsdfsdg方群刚#%#￥%&￥*￥@3453456548#￥%&%……（*）@￥【】[][", StringUtil.PATTERN_ABC_1));
//        log.error("数字 or 字母 or 下划线 " + StringUtil.replaceFilter("d阿德发发发委托__-——ASDGFsdfsdg方群刚#%#￥%&￥*￥@3453456548#￥%&%……（*）@￥【】[][", StringUtil.PATTERN_REPLACE_ABC_00_UUU));
//        log.error("数字 or 字母 or 下划线 " + StringUtil.checkFilter("撸猪哥）（", StringUtil.PATTERN_ABC_00_UUU));
//        String str = "0|1700,43013=1=0=1:1700,43023=1=0=1:1700,43033=1=0=1:1700,43043=1=0=1:1700;0|1600,43053=1=0=1:1600,43063=1=0=1:1600";
//        String str1 = "1|1000,11068=1=0=1:900,29392=1=0=1:1000;2|1000,12068=1=0=1:1000,29401=1=0=1:1000";
//        TreeMap<Integer, ArrayList<String>> stringToTreeMap = getStringToTreeMap(0, str);
//        TreeMap<Integer, ArrayList<String>> stringToTreeMap1 = getStringToTreeMap(2, str1);
//        for (Map.Entry<Integer, ArrayList<String>> entry : stringToTreeMap.entrySet()) {
//            Integer key = entry.getKey();
//            ArrayList<String> value = entry.getValue();
//        }
        //PersonAttribute personAttribute = new PersonAttribute();
        //getAttribute(personAttribute, str);
    }

    /**
     * 检查是否能通过
     *
     * @param str
     * @param regx
     * @return
     */
    public static boolean checkFilter(String str, Pattern regx) {
        try {
            return regx.matcher(str).matches();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 过滤特殊字符
     *
     * @param str
     * @param regx
     * @return
     */
    public static String replaceFilter(String str, Pattern regx) {
        try {
            return regx.matcher(str).replaceAll("").trim();
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 字符串的组合
     *
     * @param chs
     * @return
     */
    public static List<List<String>> combiantion(String... chs) {
        if (chs == null || chs.length == 0) {
            return null;
        }
        List<String> listIn = new ArrayList();
        List<List<String>> listOut = new ArrayList();
        for (int i = 1; i <= chs.length; i++) {
            combine(chs, 0, i, listIn, listOut);
        }
        return listOut;
    }

    /**
     * 从字符数组中第begin个字符开始挑选number个字符加入list中
     *
     * @param cs
     * @param begin
     * @param number
     * @param listIn
     * @param listOut
     */
    private static void combine(String[] cs, int begin, int number, List<String> listIn, List<List<String>> listOut) {
        if (number == 0) {
            listOut.add(new ArrayList<>(listIn));
            return;
        }
        if (begin == cs.length) {
            return;
        }
        listIn.add(cs[begin]);
        combine(cs, begin + 1, number - 1, listIn, listOut);
        listIn.remove(cs[begin]);
        combine(cs, begin + 1, number, listIn, listOut);
    }

    private static final Base64.Encoder BASE64_ENCODER = java.util.Base64.getEncoder();

    /**
     * 编码64位
     *
     * @param str
     * @return
     */
    public static String convertToBase64String(String str) {
        try {
            return BASE64_ENCODER.encodeToString(str.getBytes("utf-8"));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 编码64位
     *
     * @param str
     * @return
     */
    public static byte[] convertToBase64Byte(String str) {
        try {
            return BASE64_ENCODER.encode(str.getBytes("utf-8"));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 编码64位
     *
     * @param str
     * @return
     */
    public static String convertToBase64String(byte[] str) {
        return BASE64_ENCODER.encodeToString(str);
    }

    /**
     * 编码64位
     *
     * @param str
     * @return
     */
    public static byte[] convertToBase64Byte(byte[] str) {
        return BASE64_ENCODER.encode(str);
    }

    private static final Base64.Decoder Base64_DECODER = java.util.Base64.getDecoder();

    /**
     * 解码64位
     *
     * @param str
     * @return
     */
    public static byte[] convertFromBase64Byte(String str) {
        return Base64_DECODER.decode(str);
    }

    /**
     * 解码64位
     *
     * @param str
     * @return
     */
    public static String convertFromBase64(String str) {
        try {
            return new String(StringUtil.convertFromBase64Byte(str), "utf-8");
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 解码64位
     *
     * @param str
     * @return
     */
    public static byte[] convertFromBase64Byte(byte[] str) {
        return Base64_DECODER.decode(str);
    }

    /**
     * 解码64位
     *
     * @param str
     * @return
     */
    public static String convertFromBase64(byte[] str) {
        try {
            return new String(convertFromBase64Byte(str), "utf-8");
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * ASE 对称加密
     *
     * @param str 需要加密的字符串
     * @param kk 对称加密顺序
     * @return
     */
    public static String convert_InBase64_ASE(String str, int... kk) {
        char[] strChars = convertToBase64String(str).toCharArray();
        return convert_ASE_ToCharString(strChars, kk);
    }

    /**
     * ASE 对称加密
     *
     * @param str 需要加密的字符串
     * @param kk 对称加密顺序
     * @return
     */
    public static String convert_UnBase64_ASE(String str, int... kk) {
        String convert_ASE = convert_ASE(str, kk);
        return convertFromBase64(convert_ASE);
    }

    /**
     * ASE 对称加密
     *
     * @param str 需要加密的字符串
     * @param kk 对称加密顺序
     * @return
     */
    public static String convert_ASE(String str, int... kk) {
        char[] strChars = str.toCharArray();
        return convert_ASE_ToCharString(strChars, kk);
    }

    /**
     * ASE 对称加密
     *
     * @param strChars 需要加密的字符串
     * @param kk 对称加密顺序
     * @return
     */
    public static String convert_ASE_ToCharString(char[] strChars, int... kk) {
        try {
            return new String(convert_ASE_ToChar(strChars, kk));
        } catch (Exception e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * ASE 对称加密
     *
     * @param strChars 需要加密的字符串
     * @param kk 对称加密顺序
     * @return
     */
    public static char[] convert_ASE_ToChar(char[] strChars, int... kk) {
        /*二分对称性*/
        int fcount = strChars.length / 2;
        /*对称性 k 值*/
        for (int k : kk) {
            for (int i = 0; i < fcount; i += k) {
                /*对称处理*/
                char tmp = strChars[i];
                strChars[i] = strChars[fcount + i];
                strChars[fcount + i] = tmp;
            }
        }
        return strChars;
    }

    // <editor-fold desc="字符串左补齐，左对齐 public static String padLeft(Object source, int length, String paddingChar)">
    /**
     * 字符串左补齐，左对齐
     *
     * @param source
     * @param length 检查如果位数不足
     * @param paddingChar 如果位数不足填充字符
     * @return
     */
    public static String padLeft(Object source, int length, String paddingChar) {
        String strB = source.toString();
        int forCount = length - strB.length();
        for (int i = 0; i < forCount; i++) {
            strB = paddingChar + strB;
        }
        return strB;
    }
    // </editor-fold>

    // <editor-fold desc="字符串右对齐，右补齐 public static String padRigth(Object source, int length, String paddingChar)">
    /**
     * 字符串右对齐，右补齐
     *
     * @param source
     * @param length 检查如果位数不足
     * @param paddingChar 如果位数不足填充字符
     * @return
     */
    public static String padRigth(Object source, int length, String paddingChar) {
        String strB = source.toString();
        int forCount = length - strB.length();
        for (int i = 0; i < forCount; i++) {
            strB = paddingChar + strB;
        }
        return strB;
    }
    // </editor-fold>

    // <editor-fold desc="如果字符是null或者空白字符返回true public static boolean isNullOrEmpty(String str)">
    /**
     * 如果字符是null或者空白字符返回true
     *
     * @param str
     * @return
     */
    public static boolean isNullOrEmpty(String str) {
        return null == str || str.trim().isEmpty();
    }
    // </editor-fold>

    // <editor-fold desc="返回指定关键字的奖励物品 默认包含0 public static HashMap<Integer, Integer> get_Integer_Integer(int jobKey, String configString)">
    /**
     * 返回指定关键字的奖励物品 默认包含0
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @return 职业和概率，物品配置
     */
    public static HashMap<Integer, Integer> get_Integer_Integer(int jobKey, String configString) {
        ArrayList<String> stringToList = getStringToList(jobKey, configString);
        HashMap<Integer, Integer> retMap = new HashMap<>();
        for (String string : stringToList) {
            String[] split = string.split("=");
            if (split.length > 1) {
                int parseInt = Integer.parseInt(split[0]);
                int parseInt1 = Integer.parseInt(split[1]);
                if (retMap.containsKey(parseInt)) {
                    retMap.put(parseInt, retMap.get(parseInt) + parseInt1);
                } else {
                    retMap.put(parseInt, parseInt1);
                }
            }
        }
        return retMap;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定关键字的奖励物品 默认包含0 public static HashMap<Integer, Integer> get_Integer_Integer(int jobKey, String configString, boolean iszreo)">
    /**
     * 返回指定关键字的奖励物品 默认包含0
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @param iszreo
     * @return 职业和概率，物品配置
     */
    public static HashMap<Integer, Integer> get_Integer_Integer(int jobKey, String configString, boolean iszreo) {
        ArrayList<String> stringToList = getStringToList(jobKey, configString, iszreo);
        HashMap<Integer, Integer> retMap = new HashMap<>();
        for (String string : stringToList) {
            String[] split = string.split("=");
            if (split.length > 1) {
                int parseInt = Integer.parseInt(split[0]);
                int parseInt1 = Integer.parseInt(split[1]);
                if (retMap.containsKey(parseInt)) {
                    retMap.put(parseInt, retMap.get(parseInt) + parseInt1);
                } else {
                    retMap.put(parseInt, parseInt1);
                }
            }
        }
        return retMap;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定关键字的奖励物品 默认包含0 public static TreeMap<Integer, ArrayList<String>> getStringToTreeMap(int jobKey, String configString)">
    /**
     * 返回指定关键字的奖励物品 默认包含0
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @return 职业和概率，物品配置的treemap TreeMap 默认的键是从小到大的排列
     */
    public static TreeMap<Integer, ArrayList<String>> getStringToTreeMap(int jobKey, String configString) {
        return getStringToTreeMap(jobKey, configString, true);
    }
    // </editor-fold>

    // <editor-fold desc="返回指定关键字的奖励物品 public static TreeMap<Integer, ArrayList<String>> getStringToTreeMap(int jobKey, String configString, boolean isZreo)">
    /**
     * 返回指定关键字的奖励物品
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @param isZreo 是否包含 0 键
     * @return 职业和概率，物品配置的treemap TreeMap 默认的键是从小到大的排列
     */
    public static TreeMap<Integer, ArrayList<String>> getStringToTreeMap(int jobKey, String configString, boolean isZreo) {
        TreeMap<Integer, TreeMap<Integer, ArrayList<String>>> stringToTreeMap = getStringToTreeMap(configString);
        TreeMap<Integer, ArrayList<String>> get = stringToTreeMap.get(jobKey);
        if (get == null) {
            get = new TreeMap<>();
        }
        if (isZreo && jobKey != 0) {
            TreeMap<Integer, ArrayList<String>> get1 = stringToTreeMap.get(0);
            if (get1 != null) {
                for (Map.Entry<Integer, ArrayList<String>> entry : get1.entrySet()) {
                    Integer key = entry.getKey();
                    ArrayList<String> value = entry.getValue();
                    if (get.containsKey(key)) {
                        get.get(key).addAll(value);
                    } else {
                        get.put(key, value);
                    }
                }
            }
        }
        return get;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定键的所有物品，默认包含0键的 public static ArrayList<String> getStringToList(int jobKey, String configString)">
    /**
     * 返回指定键的所有物品，默认包含0键的
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @return 物品配置的
     */
    public static ArrayList<String> getStringToList(int jobKey, String configString) {
        return getStringToList(jobKey, configString, true);
    }
    // </editor-fold>

    // <editor-fold desc="返回指定键的所有物品 public static ArrayList<String> getStringToList(int jobKey, String configString, boolean isZreo)">
    /**
     * 返回指定键的所有物品
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @param isZreo 是否包含 0 键
     * @return 物品配置的
     */
    public static ArrayList<String> getStringToList(int jobKey, String configString, boolean isZreo) {
        TreeMap<Integer, ArrayList<String>> stringToTreeMap = getStringToTreeMap(jobKey, configString, isZreo);
        ArrayList<String> retList = new ArrayList<>();
        for (Map.Entry<Integer, ArrayList<String>> entry : stringToTreeMap.entrySet()) {
            Integer key = entry.getKey();
            ArrayList<String> value = entry.getValue();
            retList.addAll(value);
        }
        return retList;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定键随机物品  public static ArrayList<String> getStringToList(int jobKey, String configString, boolean isZreo, int min, int max) ">
    /**
     * 返回指定键随机物品
     *
     * @param jobKey 职业
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @param isZreo 是否包含 0 键
     * @param min 随机最小值
     * @param max 随机最大值
     * @return 物品配置的
     */
    public static ArrayList<String> getStringToList(int jobKey, String configString, boolean isZreo, int min, int max) {
        TreeMap<Integer, ArrayList<String>> stringToTreeMap = getStringToTreeMap(jobKey, configString, isZreo);
        ArrayList<String> retList = new ArrayList<>();
        int runcount = RandomUtils.random(min, max);
        for (int i = 0; i < runcount; i++) {
            for (Map.Entry<Integer, ArrayList<String>> entry : stringToTreeMap.entrySet()) {
                Integer key = entry.getKey();
                int random = RandomUtils.random(0, 10000);
                if (random <= key) {
                    ArrayList<String> value = entry.getValue();
                    if (value.size() > 0) {
                        int index = RandomUtils.random(0, value.size() - 1);
                        retList.add(value.remove(index));
                        break;
                    }
                }
            }
        }
        return retList;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定关键字的奖励物品 public static TreeMap<Integer, TreeMap<Integer, ArrayList<String>>> getStringToTreeMap(String configString)">
    /**
     * 返回指定关键字的奖励物品
     *
     * @param configString 职业|掉率,物品,物品:掉率;职业|掉率,物品,物品
     * @return 职业和概率，物品配置的treemap TreeMap 默认的键是从小到大的排列
     */
    public static TreeMap<Integer, TreeMap<Integer, ArrayList<String>>> getStringToTreeMap(String configString) {
        TreeMap<Integer, TreeMap<Integer, ArrayList<String>>> retStrings = new TreeMap<>();
        if (configString != null && !configString.trim().isEmpty() && !"0".equals(configString.trim())) {
            String[] split = configString.split(";|；");
            for (String string : split) {
                String[] split1 = string.split("\\|");
                if (split1.length > 1) {
                    int jobkey = Integer.parseInt(split1[0]);
                    if (!retStrings.containsKey(jobkey)) {
                        retStrings.put(jobkey, new TreeMap<>());
                    }
                    for (int i = 1; i < split1.length; i++) {
                        String string1 = split1[i];
                        String[] split2 = string1.split(":|：");
                        TreeMap<Integer, ArrayList<String>> jobsMap = retStrings.get(jobkey);
                        for (String string2 : split2) {
                            String[] split3 = string2.split(",|，");
                            if (split3.length > 1) {
                                int parseInt = Integer.parseInt(split3[0]);//掉率
                                if (!jobsMap.containsKey(parseInt)) {
                                    jobsMap.put(parseInt, new ArrayList<>());
                                }
                                for (int j = 1; j < split3.length; j++) {
                                    jobsMap.get(parseInt).add(split3[j]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return retStrings;
    }
    // </editor-fold>

    // <editor-fold desc="返回指定物品id和附属属性的Arraylist public static TreeMap< Integer, ArrayList<String>> getGoodsTreeMap(String goodsString) ">
    /**
     * 返回指定物品id和附属属性 格式 物品=属性=属性..；物品=属性=属性..;
     *
     * @param goodsString
     * @return
     */
    public static TreeMap< Integer, ArrayList<String>> getGoodsTreeMap(String goodsString) {
        TreeMap< Integer, ArrayList<String>> goods = new TreeMap<>();
        if (goodsString != null && !goodsString.trim().isEmpty() && !"0".equals(goodsString.trim())) {
            int i = 0;
            String[] split = goodsString.split(";|；");
            for (String string : split) {
                String[] split1 = string.split("=");
                ArrayList<String> list = new ArrayList<>();
                for (String string1 : split1) {
                    list.add(string1);
                }
                if (!goods.containsKey(Integer.parseInt(list.get(0)))) {
                    goods.put(Integer.parseInt(list.get(0)), list);
                }
            }
        }
        return goods;
    }
    // </editor-fold>

}
