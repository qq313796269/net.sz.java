package net.sz.game.engine.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class RandomUtils {

    private static final Logger log = Logger.getLogger(RandomUtils.class);

    private static final Random random = new Random();

    public static boolean random() {
        return random.nextBoolean();
    }

    /**
     * 返回0 - (max-1)
     *
     * @param max 如果10 返回 0-9
     * @return
     */
    public static int random(int max) {
        return random.nextInt(max);
    }

    /**
     * 包含最大最小值
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(int min, int max) {
        if (max - min <= 0) {
            return min;
        }
        return min + random.nextInt(max - min + 1);
    }

    /**
     * 包含最大最小值
     *
     * @param min
     * @param max
     * @return
     */
    public static int random(float min, float max) {
        if (max - min <= 0) {
            return (int) min;
        }
        return (int) (min + random.nextInt((int) (max - min + 1)));
    }

    /**
     * 根据几率 计算是否生成
     *
     * @param probability
     * @param gailv
     * @return
     */
    public static boolean isGenerate(int probability, int gailv) {
        if (gailv == 0) {
            gailv = 1000;
        }
        int random_seed = random.nextInt(gailv + 1);
        return probability >= random_seed;
    }

    /**
     *
     * gailv/probability 比率形式
     *
     * @param probability
     * @param gailv
     * @return
     */
    public static boolean isGenerate2(int probability, int gailv) {
        if (probability == gailv) {
            return true;
        }
        if (gailv == 0) {
            return false;
        }
        int random_seed = random.nextInt(probability);
        return random_seed + 1 <= gailv;
    }

    /**
     * 从 min 和 max 中间随机一个值
     *
     * @param max
     * @param min
     * @return 包含min max
     */
    public static int randomValue(int min, int max) {
        int temp = max - min;
        temp = RandomUtils.random.nextInt(temp + 1);
        temp += min;
        return temp;
    }

    /**
     * 返回在0-maxcout之间产生的随机数时候小于num
     *
     * @param num
     * @param maxcout
     * @return
     */
    public static boolean isGenerateToBoolean(float num, int maxcout) {
        return Math.random() * maxcout < num;
    }

    /**
     * 返回在0-maxcout之间产生的随机数时候小于num
     *
     * @param num
     * @param maxcout
     * @return
     */
    public static boolean isGenerateToBoolean(int num, int maxcout) {
        return Math.random() * maxcout < num;
    }

    /**
     * 随机产生min到max之间的整数值 包括min max
     *
     * @param min
     * @param max
     * @return
     */
    public static int randomIntValue(int min, int max) {
        return (int) (Math.random() * (double) (max - min + 1)) + min;
    }

    /**
     * 随机产生min到max之间的整数值 包括min max
     *
     * @param min
     * @param max
     * @return
     */
    public static float randomFloatValue(float min, float max) {
        return (float) (Math.random() * (double) (max - min)) + min;
    }

    /**
     * 随机产生min到max之间的整数值 包括min max
     *
     * @param min
     * @param max
     * @return
     */
    public static double randomDoubleValue(double min, double max) {
        return (Math.random() * (max - min)) + min;
    }

    /**
     * 0.0 ~ 1.0
     *
     * @return
     */
    public static double randomDoubleValue() {
        return Math.random();
    }

    /**
     * 随机产生min到max之间的整数值 包括min max
     *
     * @return
     */
    public static <T> T randomItem(Collection<T> collection) {
        if (collection == null || collection.isEmpty()) {
            return null;
        }
        int t = (int) (collection.size() * Math.random());
        int i = 0;
        for (Iterator<T> item = collection.iterator(); i <= t && item.hasNext();) {
            T next = item.next();
            if (i == t) {
                return next;
            }
            i++;
        }
        return null;
    }

    /**
     *
     * @param probs
     * @return
     */
    public static int randomIndexByProb(ArrayList<Integer> probs) {
        try {
            LinkedList<Integer> newprobs = new LinkedList<>();
            //[0,0,0,0,0,0,0,0,10000]
            for (int i = 0; i < probs.size(); i++) {
                if (i == 0) {
                    newprobs.add(probs.get(i));
                } else {
                    newprobs.add(newprobs.get(i - 1) + probs.get(i));
                }
            }
            if (newprobs.size() <= 0) {
                return -1;
            }
            int last = newprobs.getLast();
            if (last == 0) {
                return -1;
            }
            int random = random(last);
            for (int i = 0; i < newprobs.size(); i++) {
                int value = newprobs.get(i);
                if (value > random) {
                    return i;
                }
            }
        } catch (Exception e) {
            log.error("计算机率错误" + probs.toString(), e);
        }
        return -1;
    }
}
