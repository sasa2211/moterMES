package com.step.sacannership.tools;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by step on 2018-3-16.
 */

public class PermisionRequest {
    public final int requestCode = 0x00000001;
    PermisionView permisionView;
    Activity activity;

    public PermisionRequest(PermisionView permisionView) {
        this.permisionView = permisionView;
        activity = permisionView.getActivity();
    }

    public void requestPermision(String[] permisionArr){
        List<String> permisionLists = new ArrayList<>();
        for (String permision : permisionArr){
            if (ActivityCompat.checkSelfPermission(activity, permision) != PackageManager.PERMISSION_GRANTED){
                permisionLists.add(permision);
            }
        }

        if (permisionLists.isEmpty()){
            permisionView.onGranted();
        }else {
            ActivityCompat.requestPermissions(activity, permisionLists.toArray(new String[permisionLists.size()]), requestCode);
        }
    }
    public interface PermisionView{
        public void onGranted();
        public void onDenied(List<String> deniedLists);
        public Activity getActivity();
    }
}
