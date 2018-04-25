package cn.edu.zufe.rongyu.tuling123;

/**
 * Created by die Ehre on 2017/12/30.
 */

public class ChatListData {
    private String content;//数据内容

    //为了使得到数据与发送数据进行区分
    static final int SEND = 0;
    static final int RECEIVER = 1;
    private int flag;//标记：接收 or 发送
    private String time; //	显示时间

    public ChatListData(String content, int flag, String time) {
        setContent(content);
        setFlag(flag);
        setTime(time);
    }
    String getContent() {
        return content;
    }
    private void setContent(String content) {
        this.content = content;
    }

    int getFlag() {
        return flag;
    }
    private void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }

}
