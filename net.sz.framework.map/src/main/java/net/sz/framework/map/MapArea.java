package net.sz.framework.map;

import java.io.Serializable;
import net.sz.framework.util.concurrent.ConcurrentHashSet;

import net.sz.framework.szlog.SzLogger;

/**
 *
 * <br>
 * author 失足程序员<br>
 * blog http://www.cnblogs.com/ty408/<br>
 * mail 492794628@qq.com<br>
 * phone 13882122019<br>
 */
public class MapArea implements Serializable {

    private static final SzLogger log = SzLogger.getLogger();
    private static final long serialVersionUID = 7405450854828927173L;

    private int Id;
    private int area_W;
    private int area_H;
    /*  */
    private MapInfo mapInfo;

    /* 因为单线程处理不考虑线程安全性 */
    //玩家列表
    private final ConcurrentHashSet<Long> players = new ConcurrentHashSet<>();
    //npc列表
    private final ConcurrentHashSet<Long> npcs = new ConcurrentHashSet<>();
    //宠物列表
    private final ConcurrentHashSet<Long> pets = new ConcurrentHashSet<>();
    //怪物列表
    private final ConcurrentHashSet<Long> monsters = new ConcurrentHashSet<>();
    //等待复活怪物列表
    private final ConcurrentHashSet<Long> revives = new ConcurrentHashSet<>();
    /* 地面魔法 */
    private final ConcurrentHashSet<Long> magics = new ConcurrentHashSet<>();
    //链接特效列表
    private final ConcurrentHashSet<Long> linkEffects = new ConcurrentHashSet<>();
    /* 场景掉落物 */
    private final ConcurrentHashSet<Long> dropGoodss = new ConcurrentHashSet<>();
    /* 场景特效 */
    private final ConcurrentHashSet<Long> effects = new ConcurrentHashSet<>();

    public MapArea() {
    }

    public MapArea(int areaId, int area_W, int area_H, MapInfo mapInfo) {
        this.Id = areaId;
        this.area_W = area_W;
        this.area_H = area_H;
        this.mapInfo = mapInfo;
    }

    /*当前对象*/
    public int size() {
        return this.dropGoodss.size() + this.effects.size() + this.linkEffects.size()
                + this.magics.size() + this.monsters.size() + this.npcs.size()
                + this.pets.size() + this.players.size() + this.revives.size();
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

    public ConcurrentHashSet<Long> getDropGoodss() {
        return dropGoodss;
    }

    public ConcurrentHashSet<Long> getPlayers() {
        return players;
    }

    public ConcurrentHashSet<Long> getNpcs() {
        return npcs;
    }

    public ConcurrentHashSet<Long> getPets() {
        return pets;
    }

    public ConcurrentHashSet<Long> getMonsters() {
        return monsters;
    }

    public ConcurrentHashSet<Long> getRevives() {
        return revives;
    }

    public ConcurrentHashSet<Long> getMagics() {
        return magics;
    }

    public ConcurrentHashSet<Long> getLinkEffects() {
        return linkEffects;
    }

    public ConcurrentHashSet<Long> getEffects() {
        return effects;
    }

    @Override
    public String toString() {
        return "{" + "Id=" + Id + ", area_W=" + area_W + ", area_H=" + area_H + ", players=" + players.size() + ", npcs=" + npcs.size() + ", pets=" + pets.size() + ", monsters=" + monsters.size() + ", revives=" + revives.size() + ", magics=" + magics.size() + ", linkEffects=" + linkEffects.size() + ", dropGoodss=" + dropGoodss.size() + ", effects=" + effects.size() + '}';
    }
}
