package com.suning.cus.update;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.KeyEvent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.suning.cus.CusServiceApplication;
import com.suning.cus.activity.InitialActivity;
import com.suning.cus.bean.UpdateAppInfo;
import com.suning.cus.config.ServerConfig;
import com.suning.cus.utils.AppUtils;
import com.suning.cus.utils.L;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 版本检测与下载更新（单应用：物流移动工作站）
 *
 * @author 13075578
 */
public class CompareUpdateApk {
    /**
     * 进度条对话框
     */
    private static ProgressDialog pBar;

    private static Context context;
    /**
     * 应用名称
     */
    private static String appName = "";
    /**
     * version name
     */
    private static String newVersion = "";

    private static Handler handler = new Handler();

    private static CompareUpdateApk compareUpdateApk;

    private static HttpUtils mHttp;
    /**
     * update message
     */
    private static UpdateAppInfo updateAppInfo;

    /**
     * 已下载文件长度
     */
    private static int downloadFileLength;
    /**
     * 是否正在更新
     */
    private static boolean isUpdate = false;

    private static Handler mHandler;

    public static void CompareUpdate(Context mContext, Handler inHandler) throws
            NameNotFoundException {
        context = mContext;
        mHandler = inHandler;
        if (compareUpdateApk == null) {
            compareUpdateApk = new CompareUpdateApk();
        } else {
            checkToUpdate();
        }
    }

    private CompareUpdateApk() throws NameNotFoundException {
        checkToUpdate();
    }

    /**
     * 从服务端获取版本信息
     *
     * @Description:
     * @Author 13050629
     * @Date 2014-4-21
     */
    private static void checkToUpdate() {
        mHttp = new HttpUtils();
        mHttp.send(HttpRequest.HttpMethod.GET, ServerConfig.URL_UPDATE,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> response) {
                        if (response != null) {

                            Gson gson = new Gson();
                            try {
                                updateAppInfo = gson.fromJson(response.result,
                                        new TypeToken<UpdateAppInfo>() {}.getType());
                                if (updateAppInfo != null) {
                                    newVersion = updateAppInfo.getVersion();
                                    if (compareVersion()) {
                                        if (!isUpdate) {
                                            showUpdateDialog();
                                        }
                                    } else {
                                        // 不需要更新
                                        mHandler.sendEmptyMessage(InitialActivity.NOT_NEED_UPDATE);
                                    }

                                }
                            } catch (Exception e) {
                                try {
                                    mHandler.sendEmptyMessage(InitialActivity.UPDATE_NET_ERROR);
                                    newVersion = CurrentVersion.getVerName(context);
                                } catch (NameNotFoundException e1) {
                                    e1.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException e, String s) {
                        mHandler.sendEmptyMessage(InitialActivity.UPDATE_NET_ERROR);
                    }

                });
    }

    private static void showUpdateDialog() throws NameNotFoundException {
        StringBuffer sb = new StringBuffer();
        sb.append("发现新版本：");
        sb.append(newVersion);
        sb.append("\n");
        sb.append("\n 是否更新？");
        Dialog dialog = new AlertDialog.Builder(context).setTitle("软件更新").setMessage(sb.toString
                ()).setPositiveButton("更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isUpdate = true;
                showProgressBar();// 更新当前版本
            }
        })/*.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mHandler.sendEmptyMessage(InitialActivity.NOT_NEED_UPDATE);
            }
        })*/.create();
        dialog.setCancelable(false);
        dialog.show();

        dialog.setOnKeyListener(new DialogOnKeyListener());
    }

    private static class DialogOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                return true;
            }
            return false;
        }

    }

    protected static void showProgressBar() {
        pBar = new ProgressDialog(context);
        pBar.setCanceledOnTouchOutside(false);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍后...");
        // 设置ProgressDialog 进度条类型
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 设置ProgressDialog 的进度条是否不明确
        pBar.setIndeterminate(false);
        downAppFile(updateAppInfo.getDownloadAddr());
        // downAppFiles();
    }

    /**
     * 安装文件下载
     */
    protected static void downAppFile(final String url) {
        pBar.show();
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    // file total length
                    long fileLength = entity.getContentLength();
                    // pBar.setMax(filelength);
                    L.i("DownTag", "" + (int) fileLength);
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream;
                    if (is == null) {
                        throw new RuntimeException("isStream is null");
                    }
                    File file = getFile();
                    fileOutputStream = new FileOutputStream(file);
                    byte[] buf = new byte[1024];
                    int ch = -1;
                    do {
                        ch = is.read(buf);
                        if (ch < 0) {
                            break;
                        }
                        fileOutputStream.write(buf, 0, ch);
                        downloadFileLength += ch;
                        int result = downloadFileLength * 100 / (int) fileLength;
                        pBar.setProgress(result);
                    } while (true);
                    is.close();
                    fileOutputStream.close();
                    haveDownLoad();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 下载完成
     */
    protected static void haveDownLoad() {
        handler.post(new Runnable() {
            public void run() {
                isUpdate = false;
                pBar.cancel();
                // 安装新的版本
                installNewApk();
            }
        });
    }

    /**
     * 安装新的应用
     */
    protected static void installNewApk() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(getFile()), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 获取文件路径
     */
    private static File getFile() {
        File file;
        boolean isHaveSDCard = Environment.getExternalStorageState().equals(Environment
                .MEDIA_MOUNTED);
        if (isHaveSDCard) {
            file = new File(Environment.getExternalStorageDirectory() + "/" + appName);
        } else {
            file = new File(CusServiceApplication.getInstance().getFilesDir() + "/" + appName);
        }
        return file;
    }

    /**
     * 判断应用是否需要下载新版本
     *
     * @return （true：有需要更新的应用 false：没有需要更新的应用）
     */
    private static boolean compareVersion() {
        appName = AppUtils.getAppName(context);

        if (newVersion.length() <= 0) {
            return false;
        }

        String[] oldVersions = AppUtils.getVersionName(context).split("\\.");
        String[] newVersions = newVersion.split("\\.");

        for (int i = 0; i < oldVersions.length; i++) {
            if (Integer.valueOf(newVersions[i]) > Integer.valueOf(oldVersions[i])) {
                return true;
            } else if (Integer.valueOf(newVersions[i]) < Integer.valueOf(oldVersions[i])) {
                return false;
            }
        }
        return false;
    }
}
