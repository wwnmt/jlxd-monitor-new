package edu.nuaa.nettop.entity;

import lombok.Data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-18
 * Time: 15:53
 */
@Data
public class TaskForDDosDO {

    private String wlid;

    private List<DDosTaskDO> ddosTaskDOs;
}
