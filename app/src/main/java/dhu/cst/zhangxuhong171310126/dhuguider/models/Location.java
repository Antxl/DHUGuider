package dhu.cst.zhangxuhong171310126.dhuguider.models;

import android.graphics.PointF;

import lombok.Data;

@Data
public class Location {
    private String name;
    private PointF lowerBound,upperBound;
}
