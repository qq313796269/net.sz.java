package net.sz;

import net.sz.game.engine.struct.Vector;
import net.sz.game.engine.utils.MoveUtil;

import net.sz.game.engine.szlog.SzLogger;

/**
 *
 * @author troy-pc
 */
public class ATest {

    private static SzLogger log = SzLogger.getLogger();

    public static void main(String[] args) {

        /*攻击方坐标点是 2,2     被攻击 6,7*/
        Vector vector = MoveUtil.getV12Vector(2, 2, 6, 7);
        log.error(vector);
        /*扇形半径为5码*/
        double vr = 5;
        /*我们当前扇形是70°攻击范围*/
        double skillAngle = 35;
        /*有角度 为扇形*/
        double atan360 = vector.getAtan360();
        /*往左偏移 A1*/
        double aTan360_A1 = MoveUtil.getATan360(atan360, -1 * skillAngle);
        /*往右偏移 A2*/
        double aTan360_A2 = MoveUtil.getATan360(atan360, skillAngle);
        /*求证 5,5 点位是否在矩形内*/
        if (MoveUtil.distance(2, 2, 5, 5) <= vr) {
            double tmpTan360 = MoveUtil.getATan360(2, 2, 5, 5);
            log.error("当前点位（5, 5）在扇形内 360°=" + tmpTan360);
            if ((aTan360_A1 > aTan360_A2 && ((aTan360_A1 <= tmpTan360 && tmpTan360 <= 360) || (0 <= tmpTan360 && tmpTan360 <= aTan360_A2)))
                    || (aTan360_A1 < aTan360_A2 && aTan360_A1 <= tmpTan360 && tmpTan360 <= aTan360_A2)) {
                /*"修正后的夹角：" + aTan360_A1 + " ~ 360 和 0 ~" + aTan360_A2*/
                log.error("当前点位（5, 5）在扇形 内");
            } else {
                log.error("当前点位（5, 5）在扇形 外");
            }
        }

        /*求证 1,1 点位是否在矩形内*/
        if (MoveUtil.distance(2, 2, 1, 1) <= vr) {
            double tmpTan360 = MoveUtil.getATan360(2, 2, 1, 1);
            log.error("当前点位（1, 1）在扇形内 360°=" + tmpTan360);
            if ((aTan360_A1 > aTan360_A2 && ((aTan360_A1 <= tmpTan360 && tmpTan360 <= 360) || (0 <= tmpTan360 && tmpTan360 <= aTan360_A2)))
                    || (aTan360_A1 < aTan360_A2 && aTan360_A1 <= tmpTan360 && tmpTan360 <= aTan360_A2)) {
                /*"修正后的夹角：" + aTan360_A1 + " ~ 360 和 0 ~" + aTan360_A2*/
                log.error("当前点位(1, 1)在扇形 内");
            } else {
                log.error("当前点位（1, 1）在扇形 外");
            }
        }
    }
}
