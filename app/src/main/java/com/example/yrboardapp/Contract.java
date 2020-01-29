package com.example.yrboardapp;
// 쿼리문 띄어쓰기 주의
public final class Contract {
    private Contract(){} // 생성자
    // Contract 클래스는 인스턴스를 만들 필요가 없기 때문에 Contract(계약) 클래스의 생성자는 private으로 설정

    public static final class User{ // 테이블 - 회원정보(내부클래스1)
        public static final String TABLE_NAME = "USER";
        public static final String COLUMN_NAME_USERID = "USER_ID";
        public static final String COLUMN_NAME_USERPW = "USER_PW";
        public static final String COLUMN_NAME_NAME = "NAME";
        public static final String COLUMN_NAME_BIRTH = "BIRTH";
        public static final String COLUMN_NAME_GENDER = "GENDER";
        public static final String COLUMN_NAME_CPNUM = "CPNUM";
        public static final String COLUMN_NAME_EMAIL = "EMAIL";
        public static final String COLUMN_NAME_REGDATE = "REGDATE";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_USERID + " TEXT PRIMARY KEY," +
                COLUMN_NAME_USERPW + " TEXT NOT NULL," +
                COLUMN_NAME_NAME + " TEXT NOT NULL," +
                COLUMN_NAME_BIRTH + " DATE," +
                COLUMN_NAME_GENDER + " INTEGER," +
                COLUMN_NAME_CPNUM + " TEXT," +
                COLUMN_NAME_EMAIL + " TEXT," +
                COLUMN_NAME_REGDATE + " DATE" +
                ")";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


    public static class Board{ // 테이블 - 게시글 정보(내부클래스2)
        public static final String TABLE_NAME = "BOARD";
        public static final String COLUMN_NAME_POSTNO = "POST_NO";
        public static final String COLUMN_NAME_USERID = "USER_ID";
        public static final String COLUMN_NAME_POSTPW = "POST_PW";
        public static final String COLUMN_NAME_TITLE = "TITLE";
        public static final String COLUMN_NAME_CONTENT = "CONTENT";
        public static final String COLUMN_NAME_HIT = "HIT";
        public static final String COLUMN_NAME_REGDATE = "REGDATE";
        public static final String COLUMN_NAME_NAME = "NAME";
        public static final String COLUMN_NAME_FILENAME = "FILENAME";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_POSTNO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_USERID + " TEXT NOT NULL," +
                COLUMN_NAME_POSTPW + " TEXT," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_CONTENT + " CLOB," +
                COLUMN_NAME_HIT + " INTEGER," +
                COLUMN_NAME_REGDATE + " DATE," +
                COLUMN_NAME_NAME + " TEXT," +
                COLUMN_NAME_FILENAME + " CLOB" +
                ")";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }


    public static final class Reply{ // 테이블 - 댓글정보(내부클래스3)
        public static final String TABLE_NAME = "REPLY";
        public static final String COLUMN_NAME_REPLY_NO = "REPLY_NO";
        public static final String COLUMN_NAME_POST_NO = "POST_NO";
        public static final String COLUMN_NAME_REPLY_ID = "REPLY_ID";
        public static final String COLUMN_NAME_REPLYCONTENT = "REPLYCONTENT";
        public static final String COLUMN_NAME_REGDATE = "REGDATE";

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "(" +
                COLUMN_NAME_REPLY_NO + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_POST_NO + " INTEGER NOT NULL," +
                COLUMN_NAME_REPLY_ID + " TEXT NOT NULL," +
                COLUMN_NAME_REPLYCONTENT + " TEXT," +
                COLUMN_NAME_REGDATE + " DATE" +
                ")";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    }




}
