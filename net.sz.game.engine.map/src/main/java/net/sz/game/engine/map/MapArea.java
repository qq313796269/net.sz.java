package net.sz.game.engine.map;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import net.sz.game.engine.util.ConcurrentArraylist;
import org.apache.log4j.Logger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapArea implements Serializable {

    private static final Logger log = Logger.getLogger(MapArea.class);
    private static final long serialVersionUID = 7405450854828927173L;

    private int Id;
    private int area_W;
    private int area_H;
    /* 当作为地图线程控制区域信息存在的时候才会有 */
    private long mapThreadId;

    /*  */
    private MapInfo mapInfo;

    /* 因为单线程处理不考虑线程安全性 */
    //玩家列表
    private final ConcurrentArraylist<Long> players = new ConcurrentArraylist<>();
    //npc列表
    private final ConcurrentArraylist<Long> npcs = new ConcurrentArraylist<>();
    //宠物列表
    private final ConcurrentArraylist<Long> pets = new ConcurrentArraylist<>();
    //怪物列表
    private final ConcurrentArraylist<Long> monsters = new ConcurrentArraylist<>();
    //等待复活怪物列表
    private final ConcurrentArraylist<Long> revives = new ConcurrentArraylist<>();
    /* 地面魔法 */
    private final ConcurrentArraylist<Long> magics = new ConcurrentArraylist<>();
    //链接特效列表
    private final ConcurrentArraylist<Long> linkEffects = new ConcurrentArraylist<>();
    /* 场景掉落物 */
    private final ConcurrentArraylist<Long> dropGoodss = new ConcurrentArraylist<>();
    /* 场景特效 */
    private final ConcurrentArraylist<Long> effects = new ConcurrentArraylist<>();

    public MapArea() {
    }

    public MapArea(long mapThreadId, MapInfo mapInfo) {
        this.mapThreadId = mapThreadId;
        this.mapInfo = mapInfo;
    }

    public MapArea(int areaId, int area_W, int area_H, MapInfo mapInfo) {
        this.Id = areaId;
        this.area_W = area_W;
        this.area_H = area_H;
        this.mapInfo = mapInfo;
    }

    public MapArea(int areaId, int area_W, int area_H, long mapThreadId, MapInfo mapInfo) {
        this.Id = areaId;
        this.area_W = area_W;
        this.area_H = area_H;
        this.mapThreadId = mapThreadId;
        this.mapInfo = mapInfo;
    }

    /*当前对象*/
    public int size() {
        return this.dropGoodss.size() + this.effects.size() + this.linkEffects.size()
                + this.magics.size() + this.monsters.size() + this.npcs.size()
                + this.pets.size() + this.players.size() + this.revives.size();
    }

    @Override
    public String toString() {
        return "MapArea{" + "Id=" + Id + ", area_W=" + area_W + ", area_H=" + area_H + ", mapThreadId=" + mapThreadId + ", players=" + players.size() + ", npcs=" + npcs.size() + ", pets=" + pets.size() + ", monsters=" + monsters.size() + ", revives=" + revives.size() + ", magics=" + magics.size() + ", linkEffects=" + linkEffects.size() + ", dropGoodss=" + dropGoodss.size() + ", effects=" + effects.size() + '}';
    }

    public long getMapThreadId() {
        return mapThreadId;
    }

    public void setMapThreadId(long mapThreadId) {
        this.mapThreadId = mapThreadId;
    }

    public MapInfo getMapInfo() {
        return mapInfo;
    }

    public void setMapInfo(MapInfo mapInfo) {
        this.mapInfo = mapInfo;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getArea_W() {
        return area_W;
    }

    public void setArea_W(int area_W) {
        this.area_W = area_W;
    }

    public int getArea_H() {
        return area_H;
    }

    public void setArea_H(int area_H) {
        this.area_H = area_H;
    }

    public ConcurrentArraylist<Long> getDropGoodss() {
        return dropGoodss;
    }

    public ConcurrentArraylist<Long> getPlayers() {
        return players;
    }

    public ConcurrentArraylist<Long> getNpcs() {
        return npcs;
    }

    public ConcurrentArraylist<Long> getPets() {
        return pets;
    }

    public ConcurrentArraylist<Long> getMonsters() {
        return monsters;
    }

    public ConcurrentArraylist<Long> getRevives() {
        return revives;
    }

    public ConcurrentArraylist<Long> getMagics() {
        return magics;
    }

    public ConcurrentArraylist<Long> getLinkEffects() {
        return linkEffects;
    }

    public ConcurrentArraylist<Long> getEffects() {
        return effects;
    }

}
