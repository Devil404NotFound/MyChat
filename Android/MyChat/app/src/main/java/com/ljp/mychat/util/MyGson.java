package com.ljp.mychat.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.ljp.mychat.adapter.FriendListAdapter;
import com.ljp.mychat.entity.Friendship;
import com.ljp.mychat.entity.RequMsg;
import com.ljp.mychat.entity.User;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class MyGson {
    private static Gson gson;
    private static MyGson myGson = new MyGson();
    private MyGson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return  new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        gson = gsonBuilder.create();
    }
    public static Gson getGson(){
        return gson;
    }

    /**
     * 获取前端传过来的RequMsg
     * @param json
     * @return
     */
    public static RequMsg getRequMsg(String json){
        RequMsg requMsg = gson.fromJson(json, RequMsg.class);
        return requMsg;
    }
    /**
     * JSON转换为List<Friendship>
     * @param date
     * @return
     */
    public static List<Friendship> getFriendshipList(Object date){
        return gson.fromJson(gson.toJson(date), new TypeToken<List<Friendship>>(){}.getType());
    }

    public static User getUser(Object date){
        return gson.fromJson(gson.toJson(date), User.class);
    }

}
