package com.lakue.htmleditor.util;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class PeriodTimeGenerator {
    /**
     * 클래스내의 태그 값
     */
    private final String TAG = "PeriodTimeGenerator";

    @Override
    public String toString() {
        int nSecs = this.getSeconds();
        int nMins = this.getMinutes();
        int nHours = this.getHours();
        int nDays = this.getDays();

        String ret = "";

        int temp = nDays;
        if(nDays > 30){
            for(int i=0;i<nDays;i++){
                if(temp > 30){
                    temp -= 30;
                } else{
                    return String.format("%d개월전", i);
                }
            }
        }

        if (nDays > 0) {
            return String.format("%d일전", nDays);
        }
        if (nHours > 0) {
            return String.format("%d시간전", nHours);
        }
        if (nMins > 0) {
            return String.format("%d분전", nMins);
        }
        if (nSecs > 1) {
            return String.format("%d초전", nSecs);
        }
        if (nSecs < 2) {
            return String.format("조금전", nSecs);
        }
        return ret;
    }


    /**
     * 각종 데이터 정의 내부 클래스
     *
     * @author yeongeon
     *
     */
    class Params {
        /**
         * 시간차 계산시 기준이 되는 타임존 설정 기본값
         */
        public final static String TIMEZONE_ID_DEFAULT = "Asia/Seoul";

        /**
         * 입력 Datetime 형식
         */
        public final static String INPUT_DATE_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
    }

    /**
     * long형 시간차
     */
    private long mMilliSecs = 0L;

    /**
     * long형 시간차를 입력 받는 생성자
     *
     * @param milliSecs
     */
    public PeriodTimeGenerator(long milliSecs) {
        this.mMilliSecs = milliSecs;
    }

    /**
     * Date형 시간차를 입력 받는 생성자
     *
     * @param date
     */
    public PeriodTimeGenerator(Date date) {
        long write_datetime = date.getTime();
        Date nowDate = new Date();
        long now_datetime = nowDate.getTime();
        this.mMilliSecs = (now_datetime - write_datetime);
    }

    /**
     * 데이터의 문자열 시간을 입력 받는 생성자
     *
     * @param strDate
     * @throws ParseException
     */
    public PeriodTimeGenerator(String strDate) throws ParseException {


//        DateFormat df = new SimpleDateFormat(Params.INPUT_DATE_FORMAT_DEFAULT);
//        df.setTimeZone(TimeZone.getTimeZone(Params.TIMEZONE_ID_DEFAULT));
//        Date date = df.parse(strDate);
        Date date = dateFormat(strDate);
        long write_datetime = date.getTime();

        Date nowDate = new Date();
        long now_datetime = nowDate.getTime();

        this.mMilliSecs = (now_datetime - write_datetime);
    }

    private Date dateFormat(String date){
        SimpleDateFormat old_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // 받은 데이터 형식
        old_format.setTimeZone(TimeZone.getTimeZone("KST"));
        SimpleDateFormat new_format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 바꿀 데이터 형식

        Date r_date = new Date();
        try {
            Date old_date = old_format.parse(date);
            String new_date = new_format.format(old_date);
            r_date = new_format.parse(new_date);


            Log.i("inma", date);
            Log.i("inma", new_date);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("inma", e.toString());
        }
        return r_date;
    }

    /**
     * 데이터의 시간 포멧과 데이터의 문자열 시간을 입력 받는 생성자
     *
     * @param format
     * @param strDate
     * @throws ParseException
     */
    public PeriodTimeGenerator(String format, String strDate)
            throws ParseException {
        DateFormat df = new SimpleDateFormat(format);
        df.setTimeZone(TimeZone.getTimeZone(Params.TIMEZONE_ID_DEFAULT));
        Date date = df.parse(strDate);
        long write_datetime = date.getTime();

        Date nowDate = new Date();
        long now_datetime = nowDate.getTime();

        this.mMilliSecs = (now_datetime - write_datetime);
    }

    /**
     * 초(sec)를 반환합니다.
     *
     * @return
     */
    private int getSeconds() {
        return (int) (this.mMilliSecs / 1000);
    }

    /**
     * 분(min)을 반환합니다.
     *
     * @return
     */
    private int getMinutes() {
        return (getSeconds() / 60);
    }

    /**
     * 시간(hour)을 반환합니다.
     *
     * @return
     */
    private int getHours() {
        return (getMinutes() / 60);
    }

    /**
     * 일수(day)를 반환합니다.
     *
     * @return
     */
    private int getDays() {
        return (getHours() / 24);
    }
}
