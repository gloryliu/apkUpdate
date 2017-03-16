package com.glory.upgrade.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.glory.upgrade.R;
import com.glory.upgrade.bean.UpdateInfo;
import com.glory.upgrade.library.ApkDownloadService;
import com.glory.upgrade.library.ApkUpgradeTool;

/**
 * Created by liu.zhenrong on 2017/3/10.
 */

public class DialogUtils {

    /**
     * 显示app升级的dialog
     * @param context
     * @param builder
     */
    public static void showUpdateDialog(final Context context, final ApkUpgradeTool.Builder builder) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.dialog_version_update, null);
        TextView info = (TextView) view.findViewById(R.id.tv_info);
        info.setText(Html.fromHtml(builder.getVersionInfo()));
        TextView mNegativeBtn = (TextView) view.findViewById(R.id.positive_btn);
        ImageView mPositiveBtn = (ImageView) view.findViewById(R.id.negative_btn);
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(view);
        if (builder.isForceUpdate()) {
            dialog.setCanceledOnTouchOutside(false);
            mPositiveBtn.setVisibility(View.GONE);
        } else {
            mPositiveBtn.setVisibility(View.VISIBLE);
        }
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                                 KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (builder.isForceUpdate()) {
                        return true;
                    }
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                }
                return false;
            }
        });

        mNegativeBtn.setOnClickListener(new View.OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {//确认更新
                Intent intent = new Intent(context, ApkDownloadService.class);
                intent.putExtra("url", builder.getApkUrl());
                context.startService(intent);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        mPositiveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Window window = dialog.getWindow();
        dialog.show();
    }

}
