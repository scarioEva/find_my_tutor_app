package com.example.assignment;

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
}
