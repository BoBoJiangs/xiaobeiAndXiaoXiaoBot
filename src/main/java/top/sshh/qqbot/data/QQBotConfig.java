package top.sshh.qqbot.data;

import com.zhuangxv.bot.config.BotConfig;

import java.io.Serializable;

public class QQBotConfig implements Serializable {

    private int FamilyTaskStatus = 0;
    private String controlQQ;
    private boolean isStartScheduled = true;
    private long lastSendTime = 0L;

    public long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public int getFamilyTaskStatus() {
        return FamilyTaskStatus;
    }

    public void setFamilyTaskStatus(int familyTaskStatus) {
        FamilyTaskStatus = familyTaskStatus;
    }

    public String getControlQQ() {
        return controlQQ;
    }

    public void setControlQQ(String controlQQ) {
        this.controlQQ = controlQQ;
    }

    public boolean isStartScheduled() {
        return isStartScheduled;
    }

    public void setStartScheduled(boolean startScheduled) {
        isStartScheduled = startScheduled;
    }
}
