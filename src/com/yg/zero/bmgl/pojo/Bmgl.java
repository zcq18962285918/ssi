package com.yg.zero.bmgl.pojo;

import net.sf.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Bmgl {
    private String bmbm_pk;//部门编码
    private String bmbh;//部门编号
    private String sjbmbm_pk;//上级部门编码
    private String bmmc;//部门名称
    private String bmjb;//部门级别
    private String sfzy;//是否在用
    private String cjr;//
    private String cjsj;//
    private String xgr;//
    private String xgsj;//
    private String yxbz;//
    private String cjbh;//层级编号，排序用
    private String sfsyb;       //是否事业部  0：否  1：是 
    private String bmspjb;      //部门审批级别12345
    private String fgsbm_pk;//分公司编码
    private String sflxbm;//是否立项部门 0否1是
    private String sfcjdw;//是否承建单位 0否1是
    private String sfjsdw;//是否结算单位 0否 1是
    private String newbmbh;//部门编号 财务金蝶用（20190404新增）


    private String tbbmbh;      //??
    private String sjbmbh;//
    private String sjbmmc;//
    private String fgsmc;//
    private String state;//
    private String text;//
    private String id;//
    private String par_id;//
    private String iconCls;//
    private String fgsdh;//分公司代号
    private String kjkm;//会计科目
    private String kjkmcode;//会计科目code
    private JSONObject attributes = new JSONObject();//
    private List<Bmgl> children = new ArrayList<Bmgl>();//
    private String newbmmc;

    public String getBmbm_pk() {
        return bmbm_pk;
    }

    public void setBmbm_pk(String bmbm_pk) {
        this.bmbm_pk = bmbm_pk;
    }

    public String getBmbh() {
        return bmbh;
    }

    public void setBmbh(String bmbh) {
        this.bmbh = bmbh;
    }

    public String getSjbmbm_pk() {
        return sjbmbm_pk;
    }

    public void setSjbmbm_pk(String sjbmbm_pk) {
        this.sjbmbm_pk = sjbmbm_pk;
    }

    public String getBmmc() {
        return bmmc;
    }

    public void setBmmc(String bmmc) {
        this.bmmc = bmmc;
    }

    public String getBmjb() {
        return bmjb;
    }

    public void setBmjb(String bmjb) {
        this.bmjb = bmjb;
    }

    public String getSfzy() {
        return sfzy;
    }

    public void setSfzy(String sfzy) {
        this.sfzy = sfzy;
    }

    public String getCjr() {
        return cjr;
    }

    public void setCjr(String cjr) {
        this.cjr = cjr;
    }

    public String getCjsj() {
        return cjsj;
    }

    public void setCjsj(String cjsj) {
        this.cjsj = cjsj;
    }

    public String getXgr() {
        return xgr;
    }

    public void setXgr(String xgr) {
        this.xgr = xgr;
    }

    public String getXgsj() {
        return xgsj;
    }

    public void setXgsj(String xgsj) {
        this.xgsj = xgsj;
    }

    public String getYxbz() {
        return yxbz;
    }

    public void setYxbz(String yxbz) {
        this.yxbz = yxbz;
    }

    public String getCjbh() {
        return cjbh;
    }

    public void setCjbh(String cjbh) {
        this.cjbh = cjbh;
    }

    public String getSfsyb() {
        return sfsyb;
    }

    public void setSfsyb(String sfsyb) {
        this.sfsyb = sfsyb;
    }

    public String getBmspjb() {
        return bmspjb;
    }

    public void setBmspjb(String bmspjb) {
        this.bmspjb = bmspjb;
    }

    public String getFgsbm_pk() {
        return fgsbm_pk;
    }

    public void setFgsbm_pk(String fgsbm_pk) {
        this.fgsbm_pk = fgsbm_pk;
    }

    public String getSflxbm() {
        return sflxbm;
    }

    public void setSflxbm(String sflxbm) {
        this.sflxbm = sflxbm;
    }

    public String getSfcjdw() {
        return sfcjdw;
    }

    public void setSfcjdw(String sfcjdw) {
        this.sfcjdw = sfcjdw;
    }

    public String getSfjsdw() {
        return sfjsdw;
    }

    public void setSfjsdw(String sfjsdw) {
        this.sfjsdw = sfjsdw;
    }

    public String getNewbmbh() {
        return newbmbh;
    }

    public void setNewbmbh(String newbmbh) {
        this.newbmbh = newbmbh;
    }

    public String getTbbmbh() {
        return tbbmbh;
    }

    public void setTbbmbh(String tbbmbh) {
        this.tbbmbh = tbbmbh;
    }

    public String getSjbmbh() {
        return sjbmbh;
    }

    public void setSjbmbh(String sjbmbh) {
        this.sjbmbh = sjbmbh;
    }

    public String getSjbmmc() {
        return sjbmmc;
    }

    public void setSjbmmc(String sjbmmc) {
        this.sjbmmc = sjbmmc;
    }

    public String getFgsmc() {
        return fgsmc;
    }

    public void setFgsmc(String fgsmc) {
        this.fgsmc = fgsmc;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPar_id() {
        return par_id;
    }

    public void setPar_id(String par_id) {
        this.par_id = par_id;
    }

    public String getIconCls() {
        return iconCls;
    }

    public void setIconCls(String iconCls) {
        this.iconCls = iconCls;
    }

    public String getFgsdh() {
        return fgsdh;
    }

    public void setFgsdh(String fgsdh) {
        this.fgsdh = fgsdh;
    }

    public String getKjkm() {
        return kjkm;
    }

    public void setKjkm(String kjkm) {
        this.kjkm = kjkm;
    }

    public String getKjkmcode() {
        return kjkmcode;
    }

    public void setKjkmcode(String kjkmcode) {
        this.kjkmcode = kjkmcode;
    }

    public JSONObject getAttributes() {
        return attributes;
    }

    public void setAttributes(JSONObject attributes) {
        this.attributes = attributes;
    }

    public List<Bmgl> getChildren() {
        return children;
    }

    public void setChildren(List<Bmgl> children) {
        this.children = children;
    }

    public String getNewbmmc() {
        return newbmmc;
    }

    public void setNewbmmc(String newbmmc) {
        this.newbmmc = newbmmc;
    }
}
