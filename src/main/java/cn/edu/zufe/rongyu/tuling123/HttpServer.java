package cn.edu.zufe.rongyu.tuling123;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 异步通信
 *
 * @author rongyu
 */
public class HttpServer extends AsyncTask<String, Void, String> {

    private InputStream mInputStream;
    private HttpGetDataListener mHttpDataListener;

    // 定义数据回调接口
    public interface HttpGetDataListener {
        void getDataUrl(String data);
    }

    private String url;

    public HttpServer(String url, HttpGetDataListener httpGetDataListener) {
        this.url = url;
        this.mHttpDataListener = httpGetDataListener;
    }

    @Override
    protected String doInBackground(String... arg0) {
        BufferedReader br = null;
        try {
            HttpClient mHttpClient = new DefaultHttpClient();
            HttpGet mHttpGet = new HttpGet(url);
            HttpResponse mHttpResponse = mHttpClient.execute(mHttpGet);
            HttpEntity mHttpEntity = mHttpResponse.getEntity();
            mInputStream = mHttpEntity.getContent();

            br = new BufferedReader(new InputStreamReader(mInputStream));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (null != mInputStream) {
                    mInputStream.close();
                }
                if (null != br) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        mHttpDataListener.getDataUrl(result);
        super.onPostExecute(result);
    }
}
