package com.example.yrboardapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;

public class JSInterface {
    public static final String PREF_NAME = "shared";

    private Context mContext;
    private WebView mWebView;
    private Handler mHandler;
    private DBHelper mDatabase;
    private SharedPreferences mSharedPreferences; // 세션

    public JSInterface(Context context, WebView webView){
        mContext = context;
        mWebView = webView;
        mHandler = new Handler();
        mDatabase = new DBHelper(context);
        // 아래 코드 어디에 쓰는거고 무슨 의미? //궁금증해결
        mSharedPreferences = context.getSharedPreferences(PREF_NAME, context.MODE_PRIVATE);
    }



    public void threadSetting(final String url){ /*loadurl을 통해 페이지 resetting시 필요*/
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                mWebView.loadUrl(url);
            }
        });

    }


    // 앱 실행시 테스트 데이터 넣는 메서드
    @JavascriptInterface
    public void insertDefaultUserJS(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mDatabase.insertDefaultUser();
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
            }
        });
    } // end of insertDefaultUserJS() method


    // 로그인
    @JavascriptInterface
    public void login(final String params) {
        mHandler.post(new Runnable() { //Thread 처리
            @Override
            public void run() {
                try {
                    JSONObject obj = new JSONObject(params); // 매개변수 params를 (String) -> json 객체로 변환
//                    String data1 = obj.toString(); //테스트 출력
//                    System.out.println(data1); //테스트 출력
                    String id = obj.getString("id"); // json object에서 key가 id인 아이디값을 저장
                    String pw = obj.getString("pw");

                    JSONArray user = mDatabase.getUser(id, pw);

                    boolean data;
                    String user_id;
                    String user_name;

//                    String str = user.toString(); //테스트
//                    // System.out.println(str); //테스트 출력

                    if(user.length() > 0){ // 일치하는 데이터가 있을 경우
                        // 일치하는 데이터가 있을 경우 아이디, 패스워드가 일치하는 1명만의 정보가 나오므로
                        // json array에 담긴 json object는 1개다. 그래서 index 0으로 정보를 뽑는 것!
                        user_id = user.getJSONObject(0).getString("USER_ID");
                        user_name = user.getJSONObject(0).getString("NAME");
                        data = true;
                    }
                    else{ // 일치하는 데이터가 없을 경우
                        user_id = "";
                        user_name = "";
                        data = false;
                    }

                    // 세션 처리 필요 (로그인한 사용자의 아이디와 이름(닉네임)을 저장)
                    /* 이 세문장이 아래 한 문장과 동일한 역할
//                    SharedPreferences.Editor editor = mSharedPreferences.edit();
//                    editor.putString("id", user_id);
//                    editor.commit();
                    */
                    mSharedPreferences.edit().putString("id", user_id).commit(); //ex) key는 id, value는 현재 사용자의 id가 저장됨
                    mSharedPreferences.edit().putString("name", user_name).commit();

                    mWebView.loadUrl("javascript:login_callback(" + data + ")");

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    } // end of login() method


//    @JavascriptInterface
//    public void signUp(final String params){ // 회원 가입
//
//
//    }



//    @JavascriptInterface
//    public void getUserById(final String id) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//                try {
//                    JSONArray list = mDatabase.getUserById(id);
//
//                    if (list.length() < 1) return;
//
//                    JSONObject user = list.getJSONObject(0);
//
//                    String email = user.getString("EMAIL");
//
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//
//                }
//
//            }
//        });
//
//    }



    // 모든 게시글 정보를 가져오는 기능
    @JavascriptInterface
    public void getBoards(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                JSONArray boards = mDatabase.getBoards();

                mWebView.loadUrl("javascript:getBoards_callback(" + boards + ")");
            }
        });
    }

    // 글번호(post_no)를 이용하여 해당 게시글 정보를 가져오는 기능
    @JavascriptInterface
    public void getBoardByNo(final String param){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject obj = new JSONObject(param); // parameter로 넘어온 postno을 json object로(상위객체로) 변환
                    String postno = obj.getString("postno"); // postno이라는 키값의 value를 String에 저장

                    // 조회수 증가 코드 구현하기
                    //

                    JSONArray list = mDatabase.getBoardByNo(postno);

                    if (list.length() < 1) return; // list가 비었는지 검사

                    JSONObject board = list.getJSONObject(0);
                    // 담겨온 게시글 정보는 1개일거니까 인덱스가 0. postno을 이용해 게시글의 정보를 가져온 것을 json object 형식으로 저장

                    mWebView.loadUrl("javascript:getBoardByNo_callback(" + board + ")");//json object인 board를 넘겨줌 -> info.html에 있는 스크립트 단의 callback 함수

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }


    // 사용자가 작성한 게시글을 DB에 전달하는 기능
    @JavascriptInterface
    public void insertBoard(final String params){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject obj = new JSONObject(params);
                    String writer = obj.getString("writer");
                    String writer_name = obj.getString("writer_name");
                    String title = obj.getString("title");
                    String content = obj.getString("content");

                    mDatabase.insertBoard(writer, writer_name, title, content);

                    mWebView.loadUrl("javascript:insertBoard_callback()");
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });

    }


    // 글 번호를 매개변수로 받아 수정하고자 하는 게시글의 정보를 가져오는 기능
    @JavascriptInterface
    public void updateBoardInfo(final String postno){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
//                    JSONObject obj = new JSONObject(param);
//                    String postno = obj.getString("postno");

                    JSONArray list = mDatabase.getBoardByNo(postno);

                    if(list.length() < 1) return;

                    JSONObject board = list.getJSONObject(0);

                    mWebView.loadUrl("javascript:updateBoardInfo_callback(" + board + ")");
                }
                catch(JSONException e){
                    e.printStackTrace();;
                }

            }
        });
    }


    // 수정한 내용을 매개변수로 받아 DB에 update하는 기능
    @JavascriptInterface
    public void updateBoard(final String params){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject obj = new JSONObject(params);
//                    String writer = obj.getString("writer");
//                    String writer_name = obj.getString("writer_name");
                    String postno = obj.getString("post_no");
                    String title = obj.getString("title");
                    String content = obj.getString("content");

                    mDatabase.updateBoard(postno, title, content);

                    mWebView.loadUrl("javascript:updateBoard_callback()");

                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });
    }


    // 글 번호를 매개변수로 받아 해당 글을 DB에서 삭제하는 기능
    @JavascriptInterface
    public void deleteBoard(final String postno){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mDatabase.deleteBoard(postno);
            }
        });

    }


    // 해당 게시글의 모든 댓글을 불러오는 기능
    @JavascriptInterface
    public void getReplys(final String param){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject obj = new JSONObject(param);
                    String post_no = obj.getString("postno");

                    JSONArray replys = mDatabase.getReplys(post_no);

                    mWebView.loadUrl("javascript:getReplys_callback(" + replys + ")");
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }




    // 사용자가 작성한 댓글을 DB에 전달하는 기능
    @JavascriptInterface
    public void insertReply(final String params){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject obj = new JSONObject(params);
                    String reply_id = obj.getString("reply_id");
                    String post_no = obj.getString("post_no");
                    String reply_content = obj.getString("reply_content");

                    mDatabase.insertReply(reply_id, post_no, reply_content);

                    JSONObject board = mDatabase.getBoardByNo(post_no).getJSONObject(0);
                    mWebView.loadUrl("javascript:insertReply_callback(" + board +")");

                }

                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });

    }


    // 사용자가 수정한 댓글을 DB에 전달하는 기능
    @JavascriptInterface
    public void updateReply(final String reply_no, final String rep_content, final String post_no){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    mDatabase.updateReply(reply_no, rep_content);

                    JSONObject board = mDatabase.getBoardByNo(post_no).getJSONObject(0);
                    mWebView.loadUrl("javascript:updateReply_callback(" + board +")");
                }
                catch (JSONException e){
                    e.printStackTrace();
                }

            }
        });


    }


    // 댓글 번호를 받아 해당 댓글을 DB에서 삭제하는 기능
    @JavascriptInterface
    public void deleteReply(final String reply_no, final String post_no){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try{
                    mDatabase.deleteReply(reply_no);

                    JSONObject board = mDatabase.getBoardByNo(post_no).getJSONObject(0);
                    mWebView.loadUrl("javascript:deleteReply_callback(" + board +")");
                }
                catch(JSONException e){
                    e.printStackTrace();
                }

            }
        });
    }










    // 세션(SharedPreferences에서 아이디 혹은 이름(닉네임) 받아오기)
    @JavascriptInterface
    public String getSessionData(final String key){
        // key에 저장된 값이 있는지 확인. 들어있지 않으면 ""를 반환
        return mSharedPreferences.getString(key, "");
    }



} //end of JSInterface Class
