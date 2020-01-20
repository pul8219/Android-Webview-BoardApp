package com.example.yrboardapp;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import static com.example.yrboardapp.ConstantUrl.URL;


public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private WebSettings webSettings;
    private CustomChromeClient chromeClient;

//    private DBHelper dbHelper;
//    private SQLiteDatabase database;

    private final static String MAIN_URL = URL+ "www/signIn.html";


    @Override
    protected void onCreate(Bundle savedInstanceState) { /* main함수 처럼 최우선적으로 실행되는 메서드*/
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       //dbHelper = new DBHelper(this); /*내장 DB를 생성*/
       //database = dbHelper.getWritableDatabase();

        webView = (WebView) findViewById(R.id.webvw); /* xml Java코드 연결 */
        chromeClient = new CustomChromeClient(this);

        webView.setWebViewClient(new WebViewClient()); // URL을 Web View에서 실행할 수 있게 하는 것(새 창 뜨지 않게)
        webView.setWebChromeClient(chromeClient);
        webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true); /* javascript를 실행할 수 있게 */
        webView.addJavascriptInterface(new JSInterface(this, webView), "board");

        webView.loadUrl(MAIN_URL);
    }


    // 수정 필요
    @Override
    public void onBackPressed(){
        if(webView.canGoBack()){
            webView.goBack();
        }
        else{
            super.onBackPressed();
        }
    }


//    public class JavascriptInfoBoard{
//
//        public JavascriptInfoBoard(){
//
//        }
//        @JavascriptInterface
//        public void joinUs(String id, String name, String tel, String birth, String gender, String password){
//            MemberVO memberVo = new MemberVO();
//            memberVo.setId(id);
//            memberVo.setName(name);
//            memberVo.setCpnum(tel);
//            memberVo.setBirth(birth);
//            memberVo.setGender(gender);
//            memberVo.setPw(password);
//// 아래 문장 주석이었음
////            dbHelper.joinClients(memberVo,database);
//        }
//
//    }




}
