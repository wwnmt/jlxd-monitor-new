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
 * Time: 16:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Link implements Serializable {

    private String linkId;

    private String name;

    private String from;

    private String fromPort;

    private String to;

    private String toPort;
}
