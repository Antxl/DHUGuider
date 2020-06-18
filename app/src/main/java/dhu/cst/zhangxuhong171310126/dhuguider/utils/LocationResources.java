package dhu.cst.zhangxuhong171310126.dhuguider.utils;

import android.content.Context;
import android.graphics.PointF;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import dhu.cst.zhangxuhong171310126.dhuguider.R;
import dhu.cst.zhangxuhong171310126.dhuguider.models.Location;

public class LocationResources {
    private static Map<String,Location> resorts=null;

    public static void init(Context context)
    {
        if (resorts!=null)
            return;
        Location l;
        String[] names = {"一号学院楼", "东华大道", "图文信息中心", "北门"};
        PointF[] lows = {new PointF(281, 1001),
                new PointF(740, 669),
                new PointF(708, 791), new PointF(562, 136)},
                ups = {new PointF(542, 1140),
                        new PointF(2342, 760),
                        new PointF(1086, 987),
                        new PointF(647, 201)};
        int[] picR={R.drawable.xueyuanlou1,R.drawable.dhu1,R.drawable.tuwenzhongxin,R.drawable.beimeng};
        int[] descriptions={R.string.yihaoxueyuanlou,R.string.donghuadadao,R.string.tuwenxinxizhongxin,R.string.beimen};
        resorts=new ConcurrentHashMap<>();
        for (int i=0;i<names.length;i++) {
            l=new Location();
            l.setLowerBound(lows[i]);
            l.setUpperBound(ups[i]);
            l.setName(names[i]);
            l.setDescriptionResource(descriptions[i]);
            l.setDrawableResource(picR[i]);
            resorts.put(names[i],l);
        }
    }

    public static Location get(String where)
    {
        return resorts.get(where);
    }

    public static Location find(PointF p)
    {
        for (Location l:resorts.values()){
            if (l.contains(p))
                return l;
        }
        return null;
    }

    public static String[] resorts()
    {
        String[] keys=new String[resorts.size()];
        resorts.keySet().toArray(keys);
        return keys;
    }
}
