package helper.jalalicalendar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HelperPastDate {
  private  int currentYear,currentMonth,currentDay,currentHour,currentMinute,currentSecond;
  private  int pastYear,pastMonth,pastDay,pastHour,pastMinute,pastSecond;


  public HelperPastDate(String currentDate ,String pastDate){
    Calendar cal = Calendar.getInstance();
    DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

    try {
      Date currentdate = format.parse(currentDate);
      cal.setTime(currentdate);
      currentYear= cal.get(Calendar.YEAR);
      currentMonth= cal.get(Calendar.MONTH);
      currentDay= cal.get(Calendar.DAY_OF_MONTH);
      currentHour= cal.get(Calendar.HOUR_OF_DAY);
      currentMinute= cal.get(Calendar.MINUTE);
      currentSecond= cal.get(Calendar.SECOND);

      Date pastdate = format.parse(pastDate);
      cal.setTime(pastdate);
      pastYear= cal.get(Calendar.YEAR);
      pastMonth= cal.get(Calendar.MONTH);
      pastDay= cal.get(Calendar.DAY_OF_MONTH);
      pastHour= cal.get(Calendar.HOUR_OF_DAY);
      pastMinute= cal.get(Calendar.MINUTE);
      pastSecond= cal.get(Calendar.SECOND);

    } catch (ParseException e) {
      e.printStackTrace();
    }
  }

  public  String calculate(){
    String result="";
   if(currentYear > pastYear){
     return "یکسال پیش";
   }else{
     if(currentMonth > pastMonth){
       switch (currentMonth-pastMonth){
         case 1: result= "1 ماه پیش" ;break;
         case 2: result= "2 ماه پیش" ;break;
         case 3: result= "3 ماه پیش" ;break;
         case 4: result= "4 ماه پیش" ;break;
         case 5: result= "5 ماه پیش" ;break;
         case 6: result= "6 ماه پیش" ;break;
         case 7: result= "7 ماه پیش" ;break;
         case 8: result= "8 ماه پیش" ;break;
         case 9: result= "9 ماه پیش" ;break;
         case 10: result= "10 ماه پیش" ;break;
         case 11: result= "11 ماه پیش" ;break;
         case 12: result= "12 ماه پیش" ;break;
       }
     }else{
       if(currentDay>pastDay){
         switch (currentDay-pastDay){
           case 1: result= "1 روز پیش" ;break;
           case 2: result= "2 روز پیش" ;break;
           case 3: result= "3 روز پیش" ;break;
           case 4: result= "4 روز پیش" ;break;
           case 5: result= "5 روز پیش" ;break;
           case 6: result= "6 روز پیش" ;break;
           case 7: result= "7 روز پیش" ;break;
           case 8: case 9:case 10:case 11:case 12:case 13: result= "1 هفته پیش" ;break;
           case 14: case 15: case 16:case 17:case 18:case 19:case 20: result= "2 هفته پیش" ;break;
           case 21: case 22: case 23:case 24:case 25:case 26:case 27: result= "3 هفته پیش" ;break;
           case 28: case 29: case 30: case 31:result= "4 هفته پیش" ;break;
         }
       }else{
         if(currentHour-pastHour>1){
           switch (currentMonth-pastMonth){
             case 1: result= "1 ماه پیش" ;break;
             case 2: result= "2 ماه پیش" ;break;
             case 3: result= "3 ماه پیش" ;break;
             case 4: result= "4 ماه پیش" ;break;
             case 5: result= "5 ماه پیش" ;break;
             case 6: result= "6 ماه پیش" ;break;
             case 7: result= "7 ماه پیش" ;break;
             case 8: result= "8 ماه پیش" ;break;
             case 9: result= "9 ماه پیش" ;break;
             case 10: result= "10 ماه پیش" ;break;
             case 11: result= "11 ساعت پیش" ;break;
             case 12: result= "12 ساعت پیش" ;break;
             case 13: result= "13 ساعت پیش" ;break;
             case 14: result= "14 ساعت پیش" ;break;
             case 15: result= "15 ساعت پیش" ;break;
             case 16: result= "16 ساعت پیش" ;break;
             case 17: result= "17 ساعت پیش" ;break;
             case 18: result= "18 ساعت پیش" ;break;
             case 19: result= "19 ساعت پیش" ;break;
             case 20: result= "20 ساعت پیش" ;break;
             case 21: result= "21 ساعت پیش" ;break;
             case 22: result= "22 ساعت پیش" ;break;
             case 23: result= "23 ساعت پیش" ;break;
             case 24: result= "24 ساعت پیش" ;break;
           }
         }else{
           if(currentMinute-pastMinute>30){
             result= "نیم ساعت پیش";
           }else if(currentMinute-pastMinute<30 && currentMinute-pastMinute>15){
             result= "یک ربع پیش";
           }else if(currentMinute-pastMinute<15){
             result= "دقایقی پیش";
           }
         }
       }
     }
   }
    return result;
  }
}
