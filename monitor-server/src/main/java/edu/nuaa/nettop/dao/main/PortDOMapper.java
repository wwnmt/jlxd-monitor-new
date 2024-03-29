package edu.nuaa.nettop.dao.main;

import edu.nuaa.nettop.entity.PortDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_wldk
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    int insert(PortDO record);

    List<String> findIpBySbid(@Param("sbid") String sbid);

    Integer findDkByPrimaryKey(@Param("dkid") String dkid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_wldk
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    int insertSelective(PortDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_wldk
     *
     * @mbg.generated Wed Sep 16 11:12:52 CST 2020
     */
    PortDO selectByPrimaryKey(String dkid);

    String findNameByPrimaryKey(@Param("dkid") String dkid);
}