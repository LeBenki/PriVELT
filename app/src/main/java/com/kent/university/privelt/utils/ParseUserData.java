package com.kent.university.privelt.utils;

import android.util.Log;

import com.kent.university.privelt.model.UserData;

import java.util.LinkedHashMap;
import java.util.List;

public class ParseUserData {
    public static LinkedHashMap<String, Integer> parseUserData(List<UserData> userDatas) {
        LinkedHashMap<String, Integer> datas = new LinkedHashMap<>();
        Log.d("LULU", String.valueOf(userDatas.size()));
        for (UserData userData : userDatas) {
            if (!datas.containsKey(userData.getType())) {
                datas.put(userData.getType(), 1);
            }
            else {
                datas.put(userData.getType(), datas.get(userData.getType()) + 1);
            }
        }
        Log.d("LULU", String.valueOf(datas.size()));
        return datas;
    }
}
