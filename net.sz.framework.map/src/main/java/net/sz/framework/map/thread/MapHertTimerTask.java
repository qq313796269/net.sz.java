//package net.sz.framework.map.thread;
//
//import java.util.ArrayList;
//import net.sz.framework.map.MapInfo;
//import net.sz.framework.map.manager.AbsMapManager;
//import net.sz.framework.szlog.SzLogger;
//import net.sz.framework.szthread.TimerTaskModel;
//
///**
// *
// * <br>
// * author 失足程序员<br>
// * blog http://www.cnblogs.com/ty408/<br>
// * mail 492794628@qq.com<br>
// * phone 13882122019<br>
// */
//class MapHertTimerTask extends TimerTaskModel {
//
//    private static final SzLogger log = SzLogger.getLogger();
//    private static final long serialVersionUID = -4590064142759244242L;
//
//    public MapHertTimerTask() {
//        super(8);
//    }
//
//    @Override
//    public void run() {
//        ArrayList<MapInfo> mapInfo_Map = new ArrayList<>(AbsMapManager.getALL_MAPINFO_Map().values());
//        for (int i = 0; i < mapInfo_Map.size(); i++) {
//            MapInfo mapInfo = mapInfo_Map.get(i);
//            MapThreadExcutor.MapAllExcutor.timerRun(mapInfo.getTimerTaskMap());
//        }
//    }
//
//}
