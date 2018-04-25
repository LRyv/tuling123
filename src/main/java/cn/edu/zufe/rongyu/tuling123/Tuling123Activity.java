package cn.edu.zufe.rongyu.tuling123;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tuling123Activity extends AppCompatActivity implements View.OnClickListener {
    private final static String APPKEY = "fd8f109363db449abbab438dbf6c71e5";
    private static final String ENDPOINT = "http://www.tuling123.com/openapi/api";
    private static final String KEY = "key";
    private static final String INFO = "info";
    private static final String USERID = "userid";

    private EditText mEditText;

    List<ChatListData> mChatList;
    private ChatAdapter mChatAdapter;
    private double oldTime = 0;
    private ChatThread mChatThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tuling123);
        initView();
        setupActionBar();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        // Show the Up button in the action bar.
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        ListView mListView = (ListView) findViewById(R.id.lv_tuling123);
        mEditText = (EditText) findViewById(R.id.edt_tuling123_message);
        ImageButton btnSend = (ImageButton) findViewById(R.id.btn_tuling123_send);

        // 为发送按钮设置监听器
        btnSend.setOnClickListener(this);

        mChatList = new ArrayList<ChatListData>();

        //设置聊天工作线程
        mChatThread = new ChatThread(new Handler());
        mChatThread.setListener(new ChatThread.MessageListener() {

            @Override
            public void onChatMessage(String revMsg) {
                parseJson(revMsg);
            }
        });
        mChatThread.start();
        mChatThread.getLooper();

        // ListView绑定数据适配器
        mChatAdapter = new ChatAdapter();
        mListView.setAdapter(mChatAdapter);

        // 为lists添加欢迎语
        mChatList.add(new ChatListData(getRandomWelcomeTips(),
                ChatListData.RECEIVER,
                getTime()));
    }

    /**
     * 欢迎语随机获取
     */
    private String getRandomWelcomeTips() {
        String[] welcome_array = this.getResources()
                .getStringArray(R.array.welcome_tips);
        int index = (int) (Math.random() * welcome_array.length);
        return welcome_array[index];
    }

    /**
     * 获取显示时间
     */
    private String getTime() {
        double currentTime = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date();
        String curStr = format.format(curDate);
        if (currentTime - oldTime > 60 * 1000) {
            oldTime = currentTime;
            return curStr;
        } else {
            oldTime = currentTime;
            return null;
        }
    }

    /**
     * 去掉发送空格和回车符
     */
    private String formatContent(String content) {
        return content.replace(" ", "").replace("\n", "");
    }

    /**
     * 解析json数据
     */
    private void parseJson(String text) {
        try {
            JSONObject jsonObject = new JSONObject(text);
            ChatListData message = new ChatListData(
                    jsonObject.getString("text"),
                    ChatListData.RECEIVER,
                    getTime());

            mChatList.add(message);
            mChatAdapter.notifyDataSetChanged();// 数据刷新

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 构造URL
     */
    private String construcUrl(String content) {
        return Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter(KEY, APPKEY)
                .appendQueryParameter(USERID, "1")
                .appendQueryParameter(INFO, content).build().toString();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        //发送按钮
        if (id == R.id.btn_tuling123_send) {
            //获取发送时间
            getTime();
            // 获取发送的内容
            String content_text = formatContent(mEditText.getText().toString());
            mEditText.setText("");
            // 将用户发送的数据放入列表中
            ChatListData chatMessage = new ChatListData(
                    content_text,
                    ChatListData.SEND,
                    getTime());
            mChatList.add(chatMessage);

            /*
            *  当lists数据超过30条移除十条记录
		    *  这里需要注意下List集合是顺序列表循环移除只需把第0项移除即可
			*/
            if (mChatList.size() > 30) {
                for (int i = 0; i < 10; i++) {
                    mChatList.remove(0);
                }
            }
            // 数据刷新 更新ListView
            mChatAdapter.notifyDataSetChanged();
            // 将发送的消息传入消息工作线程，加入消息队列并等待返回
            mChatThread.queueMessage(construcUrl(content_text));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mChatThread.clearQueue();
        mChatThread.quit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) onBackPressed();
        return super.onOptionsItemSelected(item);
    }


    /**
     * ListView数据适配器
     */
    private class ChatAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mChatList.size();
        }

        @Override
        public Object getItem(int position) {
            return mChatList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            ChatListData ChatMessage = mChatList.get(position);
            if (ChatMessage.getFlag() == ChatListData.SEND) {
                return 0;
            }
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            // TODO Auto-generated method stub
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            ViewHolder viewHolder = null;
            // 判断是接收方还是发送方
            if (null == convertView) {
                if (getItemViewType(position) == ChatListData.RECEIVER) {
                    convertView = inflater.inflate(R.layout.activity_tuling123_left, null);
                    viewHolder = new ViewHolder();
                    viewHolder.textView = convertView.findViewById(R.id.tv_tuling123_chat);
                    viewHolder.timeView = convertView.findViewById(R.id.tv_tuling123_time);
                } else if (getItemViewType(position) == ChatListData.SEND) {
                    convertView = inflater.inflate(R.layout.activity_tuling123_right, null);
                    viewHolder = new ViewHolder();
                    viewHolder.textView = convertView.findViewById(R.id.tv_tuling123_chat);
                    viewHolder.timeView = convertView.findViewById(R.id.tv_tuling123_time);
                }
                assert convertView != null;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            assert viewHolder != null;
            viewHolder.textView.setText(mChatList.get(position).getContent());
            viewHolder.timeView.setText(mChatList.get(position).getTime());

            return convertView;
        }

        private class ViewHolder {
            TextView textView;
            TextView timeView;
        }
    }
}