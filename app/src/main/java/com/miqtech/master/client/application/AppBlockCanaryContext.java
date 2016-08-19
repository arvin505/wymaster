/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.miqtech.master.client.application;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import com.github.moduth.blockcanary.BlockCanaryContext;

/**
 * 监控环境的上下文实现
 * <p/>
 * Created by markzhai on 15/9/25.
 */
public class AppBlockCanaryContext extends BlockCanaryContext {
    private static final String TAG = "AppBlockCanaryContext";

    /**
     * 标示符，可以唯一标示该安装版本号，如版本+渠道名+编译平台
     *
     * @return apk唯一标示符
     */
    @Override
    public String getQualifier() {
        return getUserAgent();
    }

    private String getUserAgent() {
        PackageManager packageManager = WangYuApplication.getGlobalContext().getPackageManager();
        PackageInfo packInfo;
        String version = null;
        try {
            packInfo = packageManager.getPackageInfo(WangYuApplication.getGlobalContext().getPackageName(), 0);
            version = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String userAgent = "WangYu" + "/" + version + " (Android; Android " + android.os.Build.VERSION.RELEASE + "; UUID :"
                + ((TelephonyManager) WangYuApplication.getGlobalContext().getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() + "; MODEL "
                + Build.MODEL + "; SERIAL :" + Build.SERIAL + "; BOARD :" + Build.BOARD + "; DEVICE :" + Build.DEVICE
                + ";)";
        return userAgent;
    }

    @Override
    public String getUid() {
        return "87224330";
    }

    @Override
    public String getNetworkType() {
        return "4G";
    }

    @Override
    public int getConfigDuration() {
        return 9999;
    }

    @Override
    public int getConfigBlockThreshold() {
        return 500;
    }

    @Override
    public boolean isNeedDisplay() {
        return true;
    }

}