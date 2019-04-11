package ctrip.crn.instance;

/**
 * Created by neo on 12/12/2017.
 */

public enum CRNInstanceState {

    /**
     * init
     */
    None("None"),

    /**
     * 加载中
     */
    Loading("Loading"),

    /**
     * common ready
     */
    Ready("Ready"),

    /**
     * biz dirty
     */
    Dirty("Dirty"),

    /**
     * instance error
     */
    Error("Error");


    public String name;


    CRNInstanceState(String named) {
        name = named;
    }
}
