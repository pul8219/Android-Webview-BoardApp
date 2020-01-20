package com.example.yrboardapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

// 태일님 기존 코드에서 복사한 import들은 안먹혀서 아래처럼 고침
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.File;

import static android.app.Activity.RESULT_OK;

//import android.support.annotation.Nullable;

public class CustomChromeClient extends WebChromeClient {
    private Context mContext;
    public ValueCallback<Uri> mFilePathCallbackNormal;
    public ValueCallback<Uri[]> mFilePathCallbackLollipop;
    public final static int FILECHOOSER_NORMAL_REQ_CODE = 2001;
    public final static int FILECHOOSER_LOLLIPOP_REQ_CODE = 2002;
    private Uri mCameraImageUri = null;

    public CustomChromeClient(Context context) {
        this.mContext = context;
    }

    // For Android < 3.0
    public void openFileChooser( ValueCallback<Uri> uploadMsg) {
        openFileChooser(uploadMsg, "");
    }

    // For Android 3.0+
    public void openFileChooser( ValueCallback<Uri> uploadMsg, String acceptType) {
        mFilePathCallbackNormal = uploadMsg;

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        ((Activity)mContext).startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_NORMAL_REQ_CODE);
    }

    // For Android 4.1+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        openFileChooser(uploadMsg, acceptType);
    }

    // For Android 5.0+
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {

        // Callback 초기화 (중요!)
        if (mFilePathCallbackLollipop != null) {
            mFilePathCallbackLollipop.onReceiveValue(null);
            mFilePathCallbackLollipop = null;
        }

        mFilePathCallbackLollipop = filePathCallback;

        boolean isCapture = fileChooserParams.isCaptureEnabled();
        runCamera(isCapture);

        return true;
    }

    private void runCamera(boolean _isCapture)
    {
        // 갤러리 띄운다.
        if (!_isCapture)
        {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택하세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

            ((Activity)mContext).startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
            return;
        }

        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File path = mContext.getFilesDir();
        File file = new File(path, "fokCamera.png");
        // File 객체의 URI 를 얻는다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            String strpa = mContext.getPackageName();
            mCameraImageUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
        }
        else
        {
            mCameraImageUri = Uri.fromFile(file);
        }

        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, mCameraImageUri);

        if (!_isCapture)
        { // 선택팝업 카메라, 갤러리 둘다 띄우고 싶을 때..
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            pickIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            String pickTitle = "사진 가져올 방법을 선택하세요.";
            Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

            // 카메라 intent 포함시키기..
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{intentCamera});
            ((Activity)mContext).startActivityForResult(chooserIntent, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
        else
        {// 바로 카메라 실행..
            ((Activity)mContext).startActivityForResult(intentCamera, FILECHOOSER_LOLLIPOP_REQ_CODE);
        }
    }

    public void onResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case FILECHOOSER_NORMAL_REQ_CODE:
                if (resultCode == RESULT_OK)
                {
                    if (mFilePathCallbackNormal == null) return;
                    Uri result = (data == null || resultCode != RESULT_OK) ? null : data.getData();
                    mFilePathCallbackNormal.onReceiveValue(result);
                    mFilePathCallbackNormal = null;
                }
                break;
            case FILECHOOSER_LOLLIPOP_REQ_CODE:
                if (resultCode == RESULT_OK)
                {
                    if (mFilePathCallbackLollipop == null) return;
                    if (data == null)
                        data = new Intent();
                    if (data.getData() == null)
                        data.setData(mCameraImageUri);

                    Uri[] result = new Uri[0];

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        if (resultCode == RESULT_OK) {
                            result = (data == null) ? new Uri[]{mCameraImageUri} : WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                        }
                    }

                    mFilePathCallbackLollipop.onReceiveValue(result);
                    mFilePathCallbackLollipop = null;
                }
                else
                {
                    if (mFilePathCallbackLollipop != null)
                    {
                        mFilePathCallbackLollipop.onReceiveValue(null);
                        mFilePathCallbackLollipop = null;
                    }

                    if (mFilePathCallbackNormal != null)
                    {
                        mFilePathCallbackNormal.onReceiveValue(null);
                        mFilePathCallbackNormal = null;
                    }
                }
                break;
            default:

                break;
        }
    }
}
