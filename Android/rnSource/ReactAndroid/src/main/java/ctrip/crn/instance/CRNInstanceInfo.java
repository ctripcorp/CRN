package ctrip.crn.instance;

import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.WritableMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.Vector;

import ctrip.crn.error.CRNErrorReportListener;

/**
 * Created by neo on 12/12/2017.
 */

public class CRNInstanceInfo {

    /**
     * buid CRNInstanceInfo
     */
    public static CRNInstanceInfo getCRNInstanceInfo() {
        CRNInstanceInfo crnInstanceInfo = new CRNInstanceInfo();
        crnInstanceInfo.usedTimestamp = System.currentTimeMillis();
        crnInstanceInfo.inUseCount = 0;
        crnInstanceInfo.businessURL = "";
        return crnInstanceInfo;
    }

    public  CRNInstanceInfo() {
        this.instanceID = makeInstanceID();
    }

    private static  int guid = 0;

    private static String makeInstanceID() {
        Calendar calendar = Calendar.getInstance();
        String ret = "";
        if (calendar != null) {
            TimeZone timeZone  = calendar.getTimeZone();
            if (timeZone == null) {
                timeZone = TimeZone.getTimeZone("Asia/Shanghai");
            }
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss.SSS");//yyyy-MM-dd HH:mm:ss.SSS
            dateFormat.setTimeZone(timeZone);
            ret = (dateFormat).format(calendar.getTime());
        }
        if (ret == null || ret.length() == 0) {
            ret = System.currentTimeMillis()+"";
        }
        return ret + "_" + (++guid);
    }

    /**
     * 业务加载url
     */
    @DoNotStrip
    public String businessURL = "";

    /**
     * instance正在使用的个数
     */
    @DoNotStrip
    public int inUseCount = 0;

    /**
     * crn instance 被使用的次数
     */
    @DoNotStrip
    public int usedCount;
    /**
     * crn instance 被使用的次数
     */
    @DoNotStrip
    public long usedTimestamp;

    /**
     * 是否全量缓存包
     */
    @DoNotStrip
    public CRNInstanceState originalInstateState;

    /**
     * 原始Instance状态
     */
    @DoNotStrip
    public CRNInstanceState originalInstanceStatus;

    /**
     * 是否拆分包
     */
    @DoNotStrip
    public boolean isUnbundle = false;

    /**
     * 拆分包配置Map
     */
    @DoNotStrip
    public WritableMap moduleIdConfig = null;

    /**
     * instance状态
     */
    @DoNotStrip
    public CRNInstanceState instanceState = CRNInstanceState.None;

    /**
     * 是否已经绘制过
     */
    @DoNotStrip
    public boolean isRendered = false;

    /**
     * 当前业务包名称
     */
    @DoNotStrip
    public String inUseProductName;

    /**
     * 报错回调
     */
    @DoNotStrip
    public CRNErrorReportListener errorReportListener;

    /**
     * 加载完成回调
     */
    @DoNotStrip
    public CRNLoadReportListener loadReportListener;

    /**
     * js报错标记
     */
    @DoNotStrip
    public int countJSFatalError = 0;

    /**
     * log报错标记
     */
    @DoNotStrip
    public int countLogFatalError = 0;

    /**
     * native报错标记
     */
    @DoNotStrip
    public int countNativeFatalError = 0;

    /**
     * 加载超时标记
     */
    @DoNotStrip
    public int countTimeoutError = 0;

    /**
     * 标记Intance唯一性
     */
    @DoNotStrip
    public String instanceID;

    @DoNotStrip
    public Map<String, String> extroInfo;

}
