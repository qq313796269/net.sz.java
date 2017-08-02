package net.sz.framework.way.astar;

import org.simpleframework.xml.Root;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@Root(name = "SceneInfo_Server", strict = false)
public class MapBlockConfig {

    @Attribute(name = "MapID", required = true)
    private int mapID;

    @Element(name = "WalkSetting", required = true, type = WalkSetting.class)
    private WalkSetting walkSetting;

    @Root(name = "WalkSetting", strict = false)
    public static class WalkSetting {

        @Attribute(name = "RZLEN", required = false)
        private int rzlen;

        @Attribute(name = "RXLEN", required = false)
        private int rxlen;

        @Attribute(name = "BLOCKINFO", required = false)
        private String blockinfo;

        public int getRzlen() {
            return rzlen;
        }

        public void setRzlen(int rzlen) {
            this.rzlen = rzlen;
        }

        public int getRxlen() {
            return rxlen;
        }

        public void setRxlen(int rxlen) {
            this.rxlen = rxlen;
        }

        public String getBlockinfo() {
            return blockinfo;
        }

        public void setBlockinfo(String blockinfo) {
            this.blockinfo = blockinfo;
        }
    }

    public int getMapID() {
        return mapID;
    }

    public void setMapID(int mapID) {
        this.mapID = mapID;
    }

    public WalkSetting getWalkSetting() {
        return walkSetting;
    }

    public void setWalkSetting(WalkSetting walkSetting) {
        this.walkSetting = walkSetting;
    }

    public MapBlock buildMapBlock() throws Exception {

        int rzlen = this.walkSetting.rzlen;
        int rxlen = this.walkSetting.rxlen;

        String blockinfo = walkSetting.getBlockinfo();
        if (blockinfo.length() != rxlen * rzlen) {
            throw new Exception("地图:" + mapID + "rxlen:" + rxlen + "rzlen:" + rzlen + "需要" + rxlen * rzlen + "个阻挡点,但实际配置的阻挡点共:" + blockinfo.length());
        }

        MapBlock mapBlock = new MapBlock(mapID, rxlen, rzlen);

        int[][] mapblock = mapBlock.getMapBlock();

        for (int i = 0; i < rzlen; i++) {
            for (int j = 0; j < rxlen; j++) {
                String valueOf = String.valueOf(blockinfo.charAt(i * rxlen + j));
                byte parseByte = Byte.parseByte(valueOf);
                mapblock[i][j] = parseByte;
            }
        }
        return mapBlock;

    }

}
