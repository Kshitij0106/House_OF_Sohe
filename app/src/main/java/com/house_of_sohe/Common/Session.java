package com.house_of_sohe.Common;

import android.content.Context;
import android.content.SharedPreferences;

public class Session {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context ctx;

    public Session(Context ctx){
        this.ctx = ctx;
        this.pref = ctx.getSharedPreferences("Settings",Context.MODE_PRIVATE);
        this.editor = pref.edit();
    }

    public void setLoggedIn(boolean loggedIn){
        editor.putBoolean("loggedInMode",loggedIn);
        editor.commit();
    }

    public Boolean isLoggedIn(){
        return pref.getBoolean("loggedInMode",false);
    }
}
