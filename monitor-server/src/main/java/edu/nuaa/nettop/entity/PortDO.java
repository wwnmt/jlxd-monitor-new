package edu.nuaa.nettop.entity;

import java.math.BigDecimal;
import java.util.Date;

public class PortDO {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.DKID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private String dkid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.SBID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private String sbid;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.DKMC
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private String dkmc;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.IP
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private String ip;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.YMCD
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Short ymcd;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.DK
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Integer dk;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.WFSY
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Integer wfsy;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.DBL
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private BigDecimal dbl;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.DKZT
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Short dkzt;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.ZDCJF
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Short zdcjf;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.GXSJ
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Date gxsj;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.IPV
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private String ipv;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column t_wlbs_wldk.IPVYM
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    private Short ipvym;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.DKID
     *
     * @return the value of t_wlbs_wldk.DKID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public String getDkid() {
        return dkid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.DKID
     *
     * @param dkid the value for t_wlbs_wldk.DKID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setDkid(String dkid) {
        this.dkid = dkid == null ? null : dkid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.SBID
     *
     * @return the value of t_wlbs_wldk.SBID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public String getSbid() {
        return sbid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.SBID
     *
     * @param sbid the value for t_wlbs_wldk.SBID
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setSbid(String sbid) {
        this.sbid = sbid == null ? null : sbid.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.DKMC
     *
     * @return the value of t_wlbs_wldk.DKMC
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public String getDkmc() {
        return dkmc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.DKMC
     *
     * @param dkmc the value for t_wlbs_wldk.DKMC
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setDkmc(String dkmc) {
        this.dkmc = dkmc == null ? null : dkmc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.IP
     *
     * @return the value of t_wlbs_wldk.IP
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public String getIp() {
        return ip;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.IP
     *
     * @param ip the value for t_wlbs_wldk.IP
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setIp(String ip) {
        this.ip = ip == null ? null : ip.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.YMCD
     *
     * @return the value of t_wlbs_wldk.YMCD
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Short getYmcd() {
        return ymcd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.YMCD
     *
     * @param ymcd the value for t_wlbs_wldk.YMCD
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setYmcd(Short ymcd) {
        this.ymcd = ymcd;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.DK
     *
     * @return the value of t_wlbs_wldk.DK
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Integer getDk() {
        return dk;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.DK
     *
     * @param dk the value for t_wlbs_wldk.DK
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setDk(Integer dk) {
        this.dk = dk;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.WFSY
     *
     * @return the value of t_wlbs_wldk.WFSY
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Integer getWfsy() {
        return wfsy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.WFSY
     *
     * @param wfsy the value for t_wlbs_wldk.WFSY
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setWfsy(Integer wfsy) {
        this.wfsy = wfsy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.DBL
     *
     * @return the value of t_wlbs_wldk.DBL
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public BigDecimal getDbl() {
        return dbl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.DBL
     *
     * @param dbl the value for t_wlbs_wldk.DBL
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setDbl(BigDecimal dbl) {
        this.dbl = dbl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.DKZT
     *
     * @return the value of t_wlbs_wldk.DKZT
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Short getDkzt() {
        return dkzt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.DKZT
     *
     * @param dkzt the value for t_wlbs_wldk.DKZT
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setDkzt(Short dkzt) {
        this.dkzt = dkzt;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.ZDCJF
     *
     * @return the value of t_wlbs_wldk.ZDCJF
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Short getZdcjf() {
        return zdcjf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.ZDCJF
     *
     * @param zdcjf the value for t_wlbs_wldk.ZDCJF
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setZdcjf(Short zdcjf) {
        this.zdcjf = zdcjf;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.GXSJ
     *
     * @return the value of t_wlbs_wldk.GXSJ
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Date getGxsj() {
        return gxsj;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.GXSJ
     *
     * @param gxsj the value for t_wlbs_wldk.GXSJ
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setGxsj(Date gxsj) {
        this.gxsj = gxsj;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.IPV
     *
     * @return the value of t_wlbs_wldk.IPV
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public String getIpv() {
        return ipv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.IPV
     *
     * @param ipv the value for t_wlbs_wldk.IPV
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setIpv(String ipv) {
        this.ipv = ipv == null ? null : ipv.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column t_wlbs_wldk.IPVYM
     *
     * @return the value of t_wlbs_wldk.IPVYM
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public Short getIpvym() {
        return ipvym;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column t_wlbs_wldk.IPVYM
     *
     * @param ipvym the value for t_wlbs_wldk.IPVYM
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    public void setIpvym(Short ipvym) {
        this.ipvym = ipvym;
    }
}