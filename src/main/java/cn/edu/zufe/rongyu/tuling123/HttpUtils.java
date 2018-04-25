package cn.edu.zufe.rongyu.tuling123;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网络操作工具类
 */
class HttpUtils {

    private static final String TAG = "HttpUtils";

    /**
     * 通过url获取byte[]数据
     */
    private static byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try {
            int i = connection.getResponseCode();
            String s = connection.getResponseMessage();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        }  finally {
            connection.disconnect();
        }
    }

    /**
     * 通过url获取String数据
     */
    static String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
}
