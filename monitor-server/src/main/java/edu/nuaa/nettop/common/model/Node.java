package edu.nuaa.nettop.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:38
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Node implements Serializable {

    private String name;

    private String nodeId;

    private String serverIp;
}
