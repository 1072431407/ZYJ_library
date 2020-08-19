package com.zyj.library.lang.util;

import android.content.ClipData;
import android.content.Context;

/**
 * Created by adu on 2019-08-30.
 */
public class ClipUtil {


    /**
     * 复制文本到剪切板
     *
     * @param text
     * @return
     */
    public static boolean copy(Context mContext, String text) {
        try {
            if (text == null)
                return false;
            if (android.os.Build.VERSION.SDK_INT > 11) {
                android.content.ClipboardManager c = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text, text);
                c.setPrimaryClip(clip);
            } else {
                android.text.ClipboardManager c = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                c.setText(text);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 获取剪切板数据
     *
     * @return
     */
    public static String getString(Context mContext) {
        String clipResult = null;
        try {
            if (android.os.Build.VERSION.SDK_INT > 11) {
                android.content.ClipboardManager c = (android.content.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData primaryClip = c.getPrimaryClip();
                ClipData.Item itemAt = null;
                if (primaryClip != null) {
                    itemAt = primaryClip.getItemAt(0);
                }
                if (itemAt != null && itemAt.getText() != null) {
                    String trim = itemAt.getText().toString().trim();
                    clipResult = trim;
                }
            } else {
                android.text.ClipboardManager c = (android.text.ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                CharSequence text = c.getText();
                if (text != null) {
                    clipResult = text.toString().trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clipResult;
    }

    public static boolean clear(Context mContext) {
        return copy(mContext, "");
    }

}
