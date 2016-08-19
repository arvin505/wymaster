package com.screenrecorder;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaoyi on 2016/7/19.
 */
public class Constants {
    public static List<String> PUSH_URL_KEYS = Arrays.asList(
            "wyds_test_android_01",
            "wyds_test4",
            "wyds_test1",
            "boss线路"
    );

    public static Map<String, String> PUSH_URLS = new HashMap<>();


    public final static String PUSH_URL_ANDROID_TEST_01 = "{\n" +
            "    \"id\": \"z1.wyds.wyds_test_android_01\",\n" +
            "    \"createdAt\": \"2016-07-16T13:32:55.318+08:00\",\n" +
            "    \"updatedAt\": \"2016-07-16T13:32:55.318+08:00\",\n" +
            "    \"title\": \"wyds_test_android_01\",\n" +
            "    \"hub\": \"wyds\",\n" +
            "    \"disabled\": false,\n" +
            "    \"publishKey\": \"e27f6170-a1c7-4f6f-ae0f-1de109b6b1b7\",\n" +
            "    \"publishSecurity\": \"static\",\n" +
            "    \"hosts\": {\n" +
            "        \"publish\": {\n" +
            "            \"rtmp\": \"pili-publish.wangyuhudong.com\"\n" +
            "        },\n" +
            "        \"live\": {\n" +
            "            \"hdl\": \"pili-live-hdl.wangyuhudong.com\",\n" +
            "            \"hls\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"rtmp\": \"pili-live-rtmp.wangyuhudong.com\",\n" +
            "            \"snapshot\": \"10000uf.live1-snapshot.z1.pili.qiniucdn.com\"\n" +
            "        },\n" +
            "        \"playback\": {\n" +
            "            \"hls\": \"pili-playback.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-playback.wangyuhudong.com\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public final static String PUSH_URL_TEST4 = "{\n" +
            "    \"id\": \"z1.wyds.wyds_test4\",\n" +
            "    \"createdAt\": \"2016-07-16T12:45:43.318+08:00\",\n" +
            "    \"updatedAt\": \"2016-07-16T12:45:43.318+08:00\",\n" +
            "    \"title\": \"wyds_test4\",\n" +
            "    \"hub\": \"wyds\",\n" +
            "    \"disabled\": false,\n" +
            "    \"publishKey\": \"e27f6170-a1c7-4f6f-ae0f-1de109b6b1b7\",\n" +
            "    \"publishSecurity\": \"static\",\n" +
            "    \"hosts\": {\n" +
            "        \"publish\": {\n" +
            "            \"rtmp\": \"pili-publish.wangyuhudong.com\"\n" +
            "        },\n" +
            "        \"live\": {\n" +
            "            \"hdl\": \"pili-live-hdl.wangyuhudong.com\",\n" +
            "            \"hls\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"rtmp\": \"pili-live-rtmp.wangyuhudong.com\",\n" +
            "            \"snapshot\": \"10000uf.live1-snapshot.z1.pili.qiniucdn.com\"\n" +
            "        },\n" +
            "        \"playback\": {\n" +
            "            \"hls\": \"pili-playback.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-playback.wangyuhudong.com\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public final static String PUSH_URL_TEST1 = "{\n" +
            "    \"id\": \"z1.wyds.wyds_test1\",\n" +
            "    \"createdAt\": \"2016-06-03T18:18:26.318+08:00\",\n" +
            "    \"updatedAt\": \"2016-07-04T19:34:09.332+08:00\",\n" +
            "    \"title\": \"wyds_test4\",\n" +
            "    \"hub\": \"wyds\",\n" +
            "    \"disabled\": false,\n" +
            "    \"publishKey\": \"e27f6170-a1c7-4f6f-ae0f-1de109b6b1b7\",\n" +
            "    \"publishSecurity\": \"static\",\n" +
            "    \"hosts\": {\n" +
            "        \"publish\": {\n" +
            "            \"rtmp\": \"pili-publish.wangyuhudong.com\"\n" +
            "        },\n" +
            "        \"live\": {\n" +
            "            \"hdl\": \"pili-live-hdl.wangyuhudong.com\",\n" +
            "            \"hls\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"rtmp\": \"pili-live-rtmp.wangyuhudong.com\",\n" +
            "            \"snapshot\": \"10000uf.live1-snapshot.z1.pili.qiniucdn.com\"\n" +
            "        },\n" +
            "        \"playback\": {\n" +
            "            \"hls\": \"pili-playback.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-playback.wangyuhudong.com\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public final static String PUSH_URL_BOSS = "{\n" +
            "    \"id\": \"z1.wyds.wyds_player\",\n" +
            "    \"createdAt\": \"2016-07-25T10:15:31.318+08:00\",\n" +
            "    \"updatedAt\": \"2016-07-25T10:15:31.318+08:00\",\n" +
            "    \"title\": \"wyds_player\",\n" +
            "    \"hub\": \"wyds\",\n" +
            "    \"disabled\": false,\n" +
            "    \"publishKey\": \"e27f6170-a1c7-4f6f-ae0f-1de109b6b1b7\",\n" +
            "    \"publishSecurity\": \"static\",\n" +
            "    \"hosts\": {\n" +
            "        \"publish\": {\n" +
            "            \"rtmp\": \"pili-publish.wangyuhudong.com\"\n" +
            "        },\n" +
            "        \"live\": {\n" +
            "            \"hdl\": \"pili-live-hdl.wangyuhudong.com\",\n" +
            "            \"hls\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-live-hls.wangyuhudong.com\",\n" +
            "            \"rtmp\": \"pili-live-rtmp.wangyuhudong.com\",\n" +
            "            \"snapshot\": \"10000uf.live1-snapshot.z1.pili.qiniucdn.com\"\n" +
            "        },\n" +
            "        \"playback\": {\n" +
            "            \"hls\": \"pili-playback.wangyuhudong.com\",\n" +
            "            \"http\": \"pili-playback.wangyuhudong.com\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    static {

        PUSH_URLS.put(PUSH_URL_KEYS.get(0), PUSH_URL_ANDROID_TEST_01);
        PUSH_URLS.put(PUSH_URL_KEYS.get(1), PUSH_URL_TEST4);
        PUSH_URLS.put(PUSH_URL_KEYS.get(2), PUSH_URL_TEST1);
        PUSH_URLS.put(PUSH_URL_KEYS.get(3), PUSH_URL_BOSS);
    }

    public static List<String> quality = Arrays.asList("高清1280*720： FPS 30 , 1200Kbps"
            , "标清960*544 ：FPS 30, 600Kbps"
            , "流畅848*480 ：FPS 30, 300Kpbs ");



}
