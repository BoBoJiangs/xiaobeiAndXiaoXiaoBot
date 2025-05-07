package top.sshh.qqbot.data;

import java.io.Serializable;

public class HerbModel implements Serializable {
    private int name;
    private int count;

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
