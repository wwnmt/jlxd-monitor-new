package edu.nuaa.nettop.vo.lxd;

import edu.nuaa.nettop.model.LxdStatus;
import lombok.Data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-16
 * Time: 16:56
 */
@Data
public class LxdResponse implements Serializable {

    private Set<LxdStatus> lxdStatuses = new HashSet<>();

    private Set<String> errDevs = new HashSet<>();
}
