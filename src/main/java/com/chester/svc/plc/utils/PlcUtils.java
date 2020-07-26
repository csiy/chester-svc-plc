package com.chester.svc.plc.utils;

import com.chester.svc.plc.core.model.DishEnum;
import com.chester.svc.plc.core.model.GearsEnum;

public class PlcUtils {
    public static String getDishKey(DishEnum dish,GearsEnum gears){
        return dish.toString()+"_"+gears.toString();
    }
}
