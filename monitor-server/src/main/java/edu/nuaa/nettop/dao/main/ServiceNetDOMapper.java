package edu.nuaa.nettop.dao.main;

import edu.nuaa.nettop.entity.ServiceNetDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceNetDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_fwwl
     *
     * @mbg.generated Tue Sep 15 19:15:23 CST 2020
     */
    int insert(ServiceNetDO record);


    String getYxidByPrimaryKey(@Param("wlid") String wlid);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_fwwl
     *
     * @mbg.generated Tue Sep 15 19:15:23 CST 2020
     */
    int insertSelective(ServiceNetDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_fwwl
     *
     * @mbg.generated Tue Sep 15 19:15:23 CST 2020
     */
    ServiceNetDO selectByPrimaryKey(String wlid);
}