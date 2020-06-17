package dhu.cst.zhangxuhong171310126.dhuguider.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.List;

import dhu.cst.zhangxuhong171310126.dhuguider.models.Location;
import lombok.Getter;

public class LocationResourcesReader {
    private static SharedPreferences pref=null;
    @Getter
    private static List<Location> resorts=null;

    public LocationResourcesReader(Context context)
    {
        if (pref==null)
            pref=context.getSharedPreferences("locationMapping",Context.MODE_PRIVATE);
        /*SharedPreferences.Editor editor=pref.edit();
        String[] names={"一号学院楼","东华大道","图文信息中心","北门"};
        PointF[] lows={new PointF(281,1001),
                new PointF(740,669),
                new PointF(708,791),new PointF(562,136)},
                ups={new PointF(542,1140),
                        new PointF(2342,760),
                        new PointF(1086,987),
                        new PointF(647,201)};
        for (int i=0;i<4;i++){
            editor.putString("name"+i,names[i]);
            editor.putFloat("lbx"+i,lows[i].x);
            editor.putFloat("lby"+i,lows[i].y);
            editor.putFloat("ubx"+i,ups[i].x);
            editor.putFloat("uby"+i,ups[i].y);
        }
        editor.putInt("datas",4);
        editor.apply();*/
        if (resorts==null) {
            int dataNum = pref.getInt("datas", 0);
            for (int i = 0; i < dataNum; i++) {
                Location l = new Location();
                //l.setName(pref.getString("name" + i, null));
                //l.setLowerBound(new PointF(pref.getFloat("lbx" + i, -1),
                        //pref.getFloat("lby" + i,-1)));
                //l.setUpperBound(new PointF(pref.getFloat("ubx"+i,-1),
                        //pref.getFloat("uby"+i,-1)));
            }
        }
    }
}
