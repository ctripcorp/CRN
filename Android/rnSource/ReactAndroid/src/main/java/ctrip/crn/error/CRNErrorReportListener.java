package ctrip.crn.error;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReadableArray;

public interface CRNErrorReportListener {

    /**
     * throw js-fatal exception
     * @param title title
     * @param details details
     * @param exceptionId exceptionId
     */
    void reportFatalException(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId);

    /**
     * throw js-soft exception
     * @param title title
     * @param details details
     * @param exceptionId exceptionId
     */
    void reportSoftException(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId);

    /**
     * throw js-update exception
     * @param title title
     * @param details details
     * @param exceptionId exceptionId
     */
    void updateExceptionMessage(ReactInstanceManager instanceManager, String title, ReadableArray details, int exceptionId);

}
