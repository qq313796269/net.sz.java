package net.sz.game.engine.nio.nettys.tcp;

public class SyncRequestResponse<Message> {
    private long syncId;
    private Message response;

    /**
     * @return the syncId
     */
    public long getSyncId() {
        return syncId;
    }

    /**
     * @param syncId the syncId to set
     */
    public void setSyncId(long syncId) {
        this.syncId = syncId;
    }

    /**
     * @return the response
     */
    public Message getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(Message response) {
        this.response = response;
    }

    
}
