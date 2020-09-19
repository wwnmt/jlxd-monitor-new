package edu.nuaa.nettop.vo;

import edu.nuaa.nettop.common.model.Link;
import edu.nuaa.nettop.common.model.Node;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NetRequest {

    private String wlid;

    private List<Node> nodes;

    private List<Link> links;

    private int timeout;

    public boolean validate() {
        return wlid != null && CollectionUtils.isNotEmpty(nodes)
                && CollectionUtils.isNotEmpty(links) && timeout != 0;
    }
}
