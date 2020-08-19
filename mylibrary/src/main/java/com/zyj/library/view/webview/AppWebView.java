package com.zyj.library.view.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.RequiresApi;


import com.zyj.library.lang.StringUtil;
import com.zyj.library.log.Log;

import static android.app.Activity.RESULT_OK;

/**
 * Created by dufangzhu
 * 自定义webview   如果有视频文件播放，使用的activity设置成 android:hardwareAccelerated="true"
 * 1、设置加载模式  不缓存
 * 2、自适配高度和宽度
 * 3、http和https资源可以混合使用
 * 4、支持规则  tel： qq：微信支付等scheme
 * 5、支持文件上传
 * 6、支持微信支付等scheme  alipays:// 方式的支付
 * 7、setWebViewClient  setWebChromeClient 不要使用这个方法，额外的扩展使用addWebViewClient addWebChromeClient
 */

public class AppWebView extends WebView {
    public static final String TAG = "AppWebView";
    Context context;
    /*文件上传操作*/
    public ValueCallback<Uri> mUploadMessage;
    /*5.0以上的版本文件上传操作*/
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;


    public AppWebView(Context context) {
        super(context);
        initWebView(context);
    }

    public AppWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initWebView(context);
    }

    public AppWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWebView(context);
    }


    /**
     * 处理是www.开头的链接
     * 如果不是http开头的链接，暂时认为是http，不管https
     *
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        if (url == null)
            return;
        if (url.startsWith("javascript")) {
            super.loadUrl(url);
            return;
        }
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        super.loadUrl(url);
    }

    /**
     * 初始化webview
     */
    public void initWebView(Context c) {
        this.context = c;
        WebSettings settings = getSettings();
        //设置标记给服务端使用
//        String ua = settings.getUserAgentString();
//        if (ua == null)
//            ua = "";
//        settings.setUserAgentString(ua + "; " + AppSetting.VALUE_USER_AGENT);

        //自适配高度和宽度
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);

        //设置webview https访问跳过证书
//        HttpsTrustManager.allowAllSSL();

        //设置http和https资源可以混合使用，解决问题，https的地址中包含了http的视频流，导致5.0以上的手机不能播放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //不使用缓存
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setAppCacheEnabled(false);//不缓存
        settings.setDefaultTextEncodingName("UTF-8");

        // 设置可以访问文件
        settings.setAllowFileAccess(true);
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message,
                                     JsResult result) {
                // TODO Auto-generated method stub
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (webChromeClient != null)
                    webChromeClient.onProgressChanged(view, newProgress);

                Log.v(TAG, "newProgress == " + newProgress);
//                if (newProgress < 100) {
//                    EventBus.getDefault().post(new EventWebObj(EventWebObj.PAGE_START));
//                }
//                if (newProgress == 100) {
//                    EventBus.getDefault().post(new EventWebObj(EventWebObj.PAGE_END));
//                }
//                if (newProgress == 100) {
//                    if (context instanceof BaseAct) {
//                        ((BaseAct)context).hideNetLoadingProgressDialog();
//                    }
//                }
//                if (newProgress < 100 && !isProgressDialogShowing() && isToShowDlg) {
//                    showNetLoadingProgressDialog("加载中...");//or do it onCreate
//                    if (loadingDlg != null) {
//                        loadingDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                            @Override
//                            public void onDismiss(DialogInterface dialogInterface) {
//                                isToShowDlg = false;
//                            }
//                        });
//                    }
//                }
//                if (newProgress == 100) {
////                    Log.v(TAG, "newProgress == 100");
////                    hideNetLoadingProgressDialog();
//                    //这里不准效果不好
////                    mHandle.sendEmptyMessageDelayed(MSG_ENTER_MAIN, SHOW_TIME);
////                    mHandle.sendEmptyMessage(MSG_ENTER_MAIN);
//                }

            }

            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, FileChooserParams fileChooserParams) {

                if (webChromeClient != null) {
                    webChromeClient.onShowFileChooser(webView, uploadMsg, fileChooserParams);
                }

                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
        });

        //WebViewClient
        setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (webViewClient != null) {
                    webViewClient.onReceivedSslError(view, handler, error);
                }

                super.onReceivedSslError(view, handler, error);
                try {
                    //handler.cancel(); // Android默认的处理方式
                    //添加下面代码来忽略SSL验证
                    handler.proceed();  // 接受所有网站的证书
                    //handleMessage(Message msg); // 进行其他处理
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                Log.v(TAG, "shouldOverrideUrlLoading=" + url);

                if (StringUtil.isEmpty(url))
                    return true;

                if (webViewClient != null) {
                    //优先处理额外扩展的webViewClient，如果阻断则不下发
                    boolean b = webViewClient.shouldOverrideUrlLoading(view, url);
                    if (b)
                        return true;
                }


                if (url.trim().startsWith("tel:")) {
                    String phoneStr = url.replace("tel:", "");
                    try {
                        //直接打电话
//                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneStr)));

                        //拨号盘界面
                        String tel = StringUtil.replace(phoneStr, "-", "");
                        context.startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("mqqwpa://")
                        || url.startsWith("mqqopensdkapi://")) {
                    //打开qq
                    try {
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (url.startsWith("weixin://wap/pay?")) {
                    //微信支付
                    try {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        context.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, "您还没有安装微信");
//                        showCusToast("您还没有安装微信");
                    }

                    return true;
                }

                //支付宝扫码支付webview拉起支付宝
//                if (url.contains("scheme=alipays")) {
//                    // http://www.jianshu.com/p/e335333574a8
//                    //7.0的手机,链接编码过，最后不是intent:// 必须使用scheme=alipays
//                    //或者直接都使用scheme=alipays判断也可以，这样会先经过系统浏览器
//                    if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
//                        Log.v(TAG, "scheme=alipays");
//                        startAlipayActivity(url);
//                        return true;
//                    }
//                }
//                if (url.startsWith("intent://")) {
//                    Log.v(TAG, "intent://");
//                    //目前有响应支付宝支付
//                    startAlipayActivity(url);
//                    return true;
//                }

                //alipays://platformapi/startApp?appId=20000125&orderSuffix=h5_route_token%3D%22dede00c854782289c57064a048645615%22%26is_h5_route%3D%22true%22#Intent;scheme=alipays;package=com.eg.android.AlipayGphone;end
                //拉起支付宝的支付
                if (url.startsWith("alipays://")) {
                    Log.v(TAG, "alipays://");
                    //目前有响应支付宝支付
                    startAlipayActivity(url);
                    return true;
                }


                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // TODO Auto-generated method stub
                super.onPageStarted(view, url, favicon);
                if (webViewClient != null) {
                    webViewClient.onPageStarted(view, url, favicon);
                }


                //onPageStarted will call many times progressDialog can not dismiss
                //do it onProgressChanged, byfangzhu
//				showNetLoadingProgressDialog("加载中...");
                Log.e(TAG, "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (webViewClient != null) {
                    webViewClient.onPageFinished(view, url);
                }

//				hideNetLoadingProgressDialog();
                Log.e(TAG, "onPageFinished");

                //设置title

            }


            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                // TODO Auto-generated method stub
                super.onReceivedError(view, errorCode, description, failingUrl);
                //do it onProgressChanged, byfangzhu
//				hideNetLoadingProgressDialog();
                Log.e(TAG, "onReceivedError");
            }
        });

        setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }

        });

    }


    /**
     * 打开文件操作
     *
     * @param uploadMsg
     */
    public void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        try {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            ((Activity) context).startActivityForResult(Intent.createChooser(i, "图片选择"), FILECHOOSER_RESULTCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 5.0以上的版打开文件操作
     *
     * @param uploadMsg
     */
    public void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        try {
            mUploadMessageForAndroid5 = uploadMsg;
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");

            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "图片选择");
            ((Activity) context).startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 如果使用文件上传的话，需要再act中调用webview 的onActivityResult
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5) {
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null : intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }


    /**
     * 刷新cookie
     */
    public void refreshCookie() {
        try {
            if (Build.VERSION.SDK_INT < 21) {
                CookieSyncManager.createInstance(context);
                CookieSyncManager.getInstance().sync();
            } else {
                CookieManager.getInstance().flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清除缓存数据
     */
    public void clearCache() {
        try {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            }
            removeAllViews();
            clearCache(true);
            clearHistory();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 调起支付宝并跳转到指定页面
     *
     * @param url intent://xxx
     */
    private void startAlipayActivity(String url) {
        try {
            Intent intent = Intent.parseUri(url,
                    Intent.URI_INTENT_SCHEME);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setComponent(null);
            context.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "请下载最新版支付宝支付", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param client
     * @deprecated repleace by setWebChromeClient
     */
    @Override
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    /**
     * @param client
     * @deprecated repleace by addWebViewClient
     */
    @Override
    public void setWebViewClient(WebViewClient client) {
        super.setWebViewClient(client);
    }

    /**
     * WebChromeClient 用来做扩展使用
     */
    WebChromeClient webChromeClient;

    /**
     * extra listener
     *
     * @param client
     */
    public void addWebChromeClient(WebChromeClient client) {
        this.webChromeClient = client;
    }

    /**
     * WebViewClient 用来做扩展使用
     */
    WebViewClient webViewClient;

    /**
     * extra listener
     *
     * @param client
     */
    public void addWebViewClient(WebViewClient client) {
        this.webViewClient = client;
    }

}
