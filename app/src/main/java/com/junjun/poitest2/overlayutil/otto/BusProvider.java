package com.junjun.poitest2.overlayutil.otto;

import com.squareup.otto.Bus;

/**
 * Created by Administrator on 2018/7/13 0013.
 */

public class BusProvider {

    private static final Bus BUS=new Bus();

    private BusProvider(){

    }

    public static Bus getInstance(){

        return BUS;
    }


}
