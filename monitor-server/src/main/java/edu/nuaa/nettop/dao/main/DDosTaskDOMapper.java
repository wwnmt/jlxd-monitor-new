package edu.nuaa.nettop.dao.main;

import edu.nuaa.nettop.entity.DDosTaskDO;

public interface DDosTaskDOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_rwcs_ddos
     *
     * @mbg.generated Fri Sep 18 10:17:02 CST 2020
     */
    int insert(DDosTaskDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_rwcs_ddos
     *
     * @mbg.generated Fri Sep 18 10:17:02 CST 2020
     */
    int insertSelective(DDosTaskDO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table t_wlbs_rwcs_ddos
     *
     * @mbg.generated Fri Sep 18 10:17:02 CST 2020
     */
    DDosTaskDO selectByPrimaryKey(String rwid);
}