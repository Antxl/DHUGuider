package dhu.cst.zhangxuhong171310126.dhuguider.models;

import android.graphics.PointF;

import java.io.Serializable;

import lombok.Data;

@Data
public class Location implements Serializable {
    private String name, description;
    private PointF lowerBound,upperBound;
    private int drawableResource;

    public PointF getCenterPF()
    {
        return new PointF((lowerBound.x+upperBound.x)/2,
                (lowerBound.y+upperBound.y)/2);
    }

    public boolean contains(PointF p)
    {
        return p.x>=lowerBound.x&&
                p.y>=lowerBound.y&&
                p.x<=upperBound.x&&
                p.y<=upperBound.y;
    }
}
