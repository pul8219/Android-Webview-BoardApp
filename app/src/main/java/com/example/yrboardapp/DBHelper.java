package com.example.yrboardapp;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DBHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = "DBHelper";
    public static final String DB_NAME = "yrtest.db";
    public static final int DB_VER = 6;


    //생성자 - DB 생성
    public DBHelper(@Nullable Context context){
        super(context, DB_NAME, null, DB_VER);
    }

    // DB 처음 생성시 호출(기본)
    @Override
    public void onCreate(SQLiteDatabase db){
//        db = getWritableDatabase();

        db.execSQL(Contract.User.SQL_CREATE_TABLE); //User 테이블 생성
        db.execSQL(Contract.Board.SQL_CREATE_TABLE); //Board 테이블 생성
        db.execSQL(Contract.Reply.SQL_CREATE_TABLE); //Reply 테이블 생성

        //Contract 적용 이전 코드
        //db.execSQL("CREATE TABLE user(user_id varchar(20) primary key not null, user_pw varchar(20) not null, name varchar(20) not null, email varchar(50), gender varchar(10), birth date, cp_num varchar(20), regdate date )");
        //db.execSQL("CREATE TABLE board(post_idx integer primary key autoincrement not null, user_id varchar(20) not null, post_pw varchar(20) not null, title text, content text, filename clob, hit integer, regdate date)");

        Log.d(LOG_TAG,"TABLE CREATED");
    }


    // DB 업그레이드시 호출(기본)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){ // app제거 또는 version upgrade시 테이블 재생성
//        db = getWritableDatabase();

        db.execSQL(Contract.User.SQL_DROP_TABLE); //User 테이블 삭제
        db.execSQL(Contract.Board.SQL_DROP_TABLE); //Board 테이블 삭제
        db.execSQL(Contract.Reply.SQL_DROP_TABLE); //Reply 테이블 삭제

        db.execSQL(Contract.User.SQL_CREATE_TABLE); //User 테이블 다시 생성
        db.execSQL(Contract.Board.SQL_CREATE_TABLE); //Board 테이블 다시 생성
        db.execSQL(Contract.Reply.SQL_CREATE_TABLE); //Reply 테이블 다시 생성

        Log.d(LOG_TAG, "TABLE UPDATED");
    }


    // 로그인용 데이터를 insert 하는 기능
    // 앱 실행 처음부터 들어가게 하기
    public void insertDefaultUser(){
        SQLiteDatabase db = getWritableDatabase();

        // 테스트용 회원1
        String sql = "INSERT INTO USER " +
                "(" +
                "USER_ID," + "USER_PW," + "NAME," + "BIRTH," + "GENDER," + "CPNUM," + "EMAIL," + "REGDATE" +
                ")" +
                "VALUES('pul8219','1234','박유림', (SELECT date('now')), '1', '010-1111-1111','pul8219@naver.com', (SELECT date('now')))";

        // 테스트용 회원2
        String sql2 = "INSERT INTO USER " +
                "(" +
                "USER_ID," + "USER_PW," + "NAME," + "BIRTH," + "GENDER," + "CPNUM," + "EMAIL," + "REGDATE" +
                ")" +
                "VALUES('a','a','더조인', (SELECT date('now')), '2', '010-2222-2222','a@naver.com', (SELECT date('now')))";

        db.execSQL(sql);
        db.execSQL(sql2);
    }


    // 회원가입
    public void insertUser(String id, String pw, String name, String email, String cpnum, String gender, String birth){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "INSERT INTO USER" +
                "(" +
                "USER_ID," + "USER_PW," + "NAME," + "BIRTH," + "GENDER," + "CPNUM," + "EMAIL," + "REGDATE" +
                ") " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, (SELECT date('now')))";
        //현재시간 (SELECT date('now'))
        //regdate 컬럼에는 회원가입한 현재 날짜를 넣음
        //sql에 정해지지 않은 값을 ?(물음표)로 표시하는 바인딩 변수 사용 가능

        ArrayList<Object> list = new ArrayList<>();

        list.add(id);
        list.add(pw);
        list.add(name);
        list.add(birth);
        list.add(gender);
        list.add(cpnum);
        list.add(email);

        db.execSQL(sql, list.toArray());
    }




    //    // 회원가입
//    // 수정 필요
//    // birth를 어떻게 받아야 하나? 일단 String으로 해놨음
//    public void insertUser(String userid, String userpw, String name, String birth, String gender, String phone, String email){
//        SQLiteDatabase db = getWritableDatabase(); // 읽고 쓰기 위해 DB 연다. 권한이 없거나 디스크가 가득 차면 실패
//
//        String sql = "INSERT INTO USER" +
//                "(" +
//                "USER_ID," + "USER_PW," + "NAME," + "BIRTH," + "GENDER," + "CPNUM," + "EMAIL," + "REGDATE" +
//                ")" +
//                "VALUES(?, ?, ?, ?, ?, ?, ?, datetime())";
//        //현재시간 (SELECT date('now'))
//        //regdate 컬럼에는 회원가입한 현재 시간을 넣음
//        //sql에 정해지지 않은 값을 ?(물음표)로 표시하는 바인딩 변수 사용 가능
//
//        ArrayList<Object> list = new ArrayList<>();
//
//        list.add(userid);
//        list.add(userpw);
//        list.add(name);
//        list.add(birth);
//        list.add(gender);
//        list.add(phone);
//        list.add(email);
//
//        db.execSQL(sql, list.toArray());
//
//        //        db.execSQL("INSERT INTO MEMBER(ID, PASSWORD, NAME, GENDER, BIRTH, TEL, REGDATE)" +
////                "VALUES('"+vo.getName()+"','"+vo.getId()+"','"+vo.getPw()+"','"+vo.getGender()+"','"+vo.getBirth()+"','"+vo.getCpnum()+"',"+"strftime('%Y-%m-%d %H:%M:%S', 'now', 'localtime'))");
//    }





    // 아이디와 패스워드가 일치하는 회원의 정보를 가져오는 쿼리
    public JSONArray getUser(String id, String password){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT * FROM USER " +
                "WHERE " +
                    "USER_ID = ? AND " +
                    "USER_PW = ? ";

        String[] params = {id, password};

        Cursor cursor = db.rawQuery(sql, params); //위의 select문 실행
        return getJsonFromCursor(cursor);
    }


    // 아이디가 일치하는 회원의 정보를 가져오는 쿼리
    public JSONArray getUserById(String id){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT * FROM USER " +
                "WHERE " +
                "USER_ID = ? ";

        String[] param = {id};
        Cursor cursor = db.rawQuery(sql, param); //위의 select문 실행
        return getJsonFromCursor(cursor);
    }


    // 모든 게시글의 정보를 가져오는 쿼리
    public JSONArray getBoards(){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT " +
                "POST_NO," +
                "USER_ID," +
                "POST_PW," +
                "TITLE," +
                "CONTENT," +
                "HIT," +
                "REGDATE," +
                "NAME," +
                "FILENAME" +
                " FROM BOARD"
                ;

        // 쿼리 안에 ? 형식이 없기 때문에 rawQuery의 두번째 인자는 null로 지정하였음
        Cursor cursor = db.rawQuery(sql, null);

        return getJsonFromCursor(cursor);

    }


    // 매개변수로 받은 글번호(post_no) 값을 이용하여 해당 게시글의 정보를 가져오는 쿼리
    public JSONArray getBoardByNo(String post_no){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "SELECT " +
                "POST_NO," +
                "USER_ID," +
                "POST_PW," +
                "TITLE," +
                "CONTENT," +
                "HIT," +
                "REGDATE," +
                "NAME," +
                "FILENAME" +
                " FROM BOARD" +
                " WHERE POST_NO = " + post_no
                ;

        Cursor cursor = db.rawQuery(sql,null);

        return getJsonFromCursor(cursor);

    }


    // 사용자가 작성한 게시글의 정보를 DB에 넣는 INSERT 쿼리
    public void insertBoard(String writer, String writer_name, String title, String content){
        SQLiteDatabase db = getWritableDatabase();
        //System.out.println("insertBoard() start"); //테스트 출력
        //System.out.println(writer+ title + content); //테스트 출력
        String sql = "INSERT INTO BOARD " +
                "(" +
                "USER_ID," +
                "POST_PW," +
                "TITLE," +
                "CONTENT," +
                "HIT," +
                "REGDATE," +
                "NAME," +
                "FILENAME" +
                ") " +
                "VALUES(?, '', ?, ?, 0, (SELECT date('now')), ?, '')";

        ArrayList<Object> list = new ArrayList<>();

        list.add(writer); //ID에 저장
        list.add(title);
        list.add(content);
        list.add(writer_name);

        db.execSQL(sql, list.toArray());
    }


    // 사용자가 수정한 게시글의 정보를 DB에 UPDATE 하는 쿼리(글 번호 기반 업데이트)
    public void updateBoard(String post_no, String title, String content){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE BOARD SET "+
                "TITLE = ?, " +
                "CONTENT = ? " +
                "WHERE POST_NO = ?"
                ;

        ArrayList<Object> list = new ArrayList<>();

        list.add(title);
        list.add(content);
        list.add(post_no);

        db.execSQL(sql, list.toArray());
    }


    // 사용자가 삭제하고자 하는 게시글을 DB에서 delete 하는 쿼리
    public void deleteBoard(String post_no){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "DELETE FROM BOARD " +
                "WHERE POST_NO = " + post_no;

        // 앱 구현 초기, 잘못 들어간 데이터 지우는데 사용한 임시 코드
//        String sql2 = "DELETE FROM BOARD " +
//                "WHERE USER_ID = " + "'박유림'";
//         db.execSQL(sql2); // 이 문장을 db.execSQL(sql); 보다 나중에 실행시 에러.

         db.execSQL(sql);
    }


    // 해당 게시글의 댓글을 모두 가져오는 SELECT 쿼리
    public JSONArray getReplys(String post_no){
        SQLiteDatabase db = getWritableDatabase();
//        System.out.println("getReplys db helper call");
        String sql = "SELECT " +
                "REPLY.REPLY_NO," +
                "REPLY.POST_NO," +
                "REPLY.REPLY_ID,"+
                "REPLY.REPLYCONTENT," +
                "REPLY.REGDATE," +
                "USER.NAME " +
                "FROM REPLY " +
                "JOIN USER ON REPLY.REPLY_ID = USER.USER_ID " +
                "WHERE REPLY.POST_NO=" + post_no
                ;

        Cursor cursor = db.rawQuery(sql,null);

        return getJsonFromCursor(cursor);
    }


    // 사용자가 작성한 댓글 정보를 DB에 넣는 INSERT 쿼리
    public void insertReply(String rep_id, String post_no, String rep_content){
        SQLiteDatabase db = getWritableDatabase();

        Log.d(LOG_TAG,"insert success");

        String sql = "INSERT INTO REPLY " +
                "(" +
                "POST_NO," +
                "REPLY_ID," +
                "REPLYCONTENT," +
                "REGDATE" +
                ") " +
                "VALUES(?, ?, ?, (SELECT date('now')))";

        ArrayList<Object> list = new ArrayList<>();

        list.add(post_no);
        list.add(rep_id);
        list.add(rep_content);

        db.execSQL(sql, list.toArray());
    }


    // 사용자가 수정한 댓글 정보를 UPDATE 하는 쿼리
    public void updateReply(String rep_no, String rep_content){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "UPDATE REPLY SET " +
                "REPLYCONTENT = ? " +
                "WHERE REPLY_NO = ?"
                ;

        ArrayList<Object> list = new ArrayList<>();
        list.add(rep_content);
        list.add(rep_no);

        db.execSQL(sql,list.toArray());

    }


    // 댓글 정보를 삭제하는 DELETE 쿼리
    public void deleteReply(String rep_no){
        SQLiteDatabase db = getWritableDatabase();

        String sql = "DELETE FROM REPLY " +
                "WHERE REPLY_NO = " + rep_no ;

        db.execSQL(sql);

    }











    // 가져다 쓴 것! 내가 작성한 코드 아님
    private JSONArray getJsonFromCursor(Cursor cursor) {
        JSONArray result = new JSONArray();

        // DB 컬럼명 문자열 배열에 저장
        String[] columns = cursor.getColumnNames();

        // 한 행씩 반복
        while (cursor.moveToNext()) {
            JSONObject obj = new JSONObject();

            // 컬럼 반복
            for (String column : columns) {

                int index = cursor.getColumnIndex(column);
                Object value = new Object();

                switch (cursor.getType(index)) {
                    case Cursor.FIELD_TYPE_INTEGER:
                        value = cursor.getInt(index);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        value = cursor.getFloat(index);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        value = cursor.getString(index);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        value = cursor.getBlob(index);
                        break;
                }

                try {
                    // JSON Object에 저장
                    obj.put(column, value);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // 위 반복으로 얻은 오브젝트(한 행에 대한 정보임)를 JSON Array에 저장
            result.put(obj);
        }

        return result;
    }


}
