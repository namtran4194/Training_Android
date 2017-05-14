package com.namtran.lazada.model.objectclass;

/**
 * Created by namtr on 10/05/2017.
 */

public enum Action {
    THUONG_HIEU_LON("laydanhsachthuonghieulon", "DANHSACHTHUONGHIEU"),
    TOP_DT_VA_MTB("laydanhsachtopDTvaMTB", "TOPDTVAMTB"),
    PHU_KIEN("laydanhsachphukien", "DANHSACHPHUKIEN"),
    TIEN_ICH("laydanhsachtienich", "DANHSACHTIENICH"),
    TOP_PHU_KIEN("laydanhsachtopphukien", "TOPPHUKIEN"),
    TOP_TIEN_ICH("laydanhsachtoptienich", "TOPTIENICH"),
    LOGO_THUONG_HIEU("laylogothuonghieulon", "LOGOTHUONGHIEU"),
    HANG_MOI_VE("laydanhsachhangmoive", "HANGMOIVE");

    private String action;
    private String parentNodeName;

    Action(String action, String parentNodeName) {
        this.action = action;
        this.parentNodeName = parentNodeName;
    }

    public String getAction() {
        return action;
    }

    public String getParentNodeName() {
        return parentNodeName;
    }

    @Override
    public String toString() {
        return action;
    }
}