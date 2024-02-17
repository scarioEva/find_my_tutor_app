package com.example.assignment;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommonClass {
    public String generateRandomString(int n) {
        StringBuilder stringBuilder = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            // generate a random number between
            // 0 to AlphaNumericString variable length
            String alphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "0123456789" + "abcdefghijklmnopqrstuvxyz";
            int index = (int) (alphaNumericString.length() * Math.random());

            // add Character one by one in end of sb
            stringBuilder.append(alphaNumericString.charAt(index));
        }
        return stringBuilder.toString();
    }

    public String getDateTime(String c_date, String time) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("MM/d/yyyy", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        Date date = inputFormat.parse(c_date);
        String formattedDate = outputFormat.format(date);

        String final_d= formattedDate+" ("+ time+")";
        return  final_d;
    }

}
