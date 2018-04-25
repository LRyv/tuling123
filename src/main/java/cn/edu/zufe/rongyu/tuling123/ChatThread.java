package cn.edu.zufe.rongyu.tuling123;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

/**
 * 聊天工作线程
 */
public class ChatThread extends HandlerThread {

    private static final String TAG = "ChatThread";
    /**
     * 下载
     */
    private static final int MESSAGE_DOWNLOAD = 0;

    /**
     * 工作线程处理消息Handler
     */
    private Handler mHandler;

    /**
     * 更新UI线程的Handler
     */
    private Handler responseHandler;

    /**
     * 回调接口
     */
    private MessageListener mListener;

    /**
     * 聊天信息回调接口
     */
    public interface MessageListener {
        void onChatMessage(String revMsg);
    }

    public void setListener(MessageListener listener) {
        mListener = listener;
    }

    public ChatThread(Handler handler) {
        super(TAG);
        this.responseHandler = handler;
    }

    /**
     * 添加发送消息
     */
    public void queueMessage(String url) {
        Log.i(TAG, "发送url：" + url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, url).sendToTarget();
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    String sendMsg = (String) msg.obj;
                    handleRequest(sendMsg);
                }
            }
        };
    }

    /**
     * 处理请求
     */
    private void handleRequest(String url) {
        try {
            final String revString = HttpUtils.getUrlString(url);

            responseHandler.post(new Runnable() {

                @Override
                public void run() {
                    mListener.onChatMessage(revString);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Exception:" + e.getMessage(), e);
        }
    }

    /**
     * 清空消息队列
     */
    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

}
