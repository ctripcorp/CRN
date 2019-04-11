package ctrip.crn.instance;

import com.facebook.react.ReactInstanceManager;

/**
  * @author Leone
  * @date 8/8/16
  */
public interface CRNLoadReportListener {

    /**
     * instance加载完成回调
     * @param mng mng
     * @param time time
     */
    void onLoadComponentTime(ReactInstanceManager mng, long time);

}
