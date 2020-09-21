package edu.nuaa.nettop.vo;

import edu.nuaa.nettop.common.model.Link;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 18:21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PerfScreenRequest {

    private String wlid;

    private List<String> serverIps = new ArrayList<>();

    private List<Link> links = new ArrayList<>();
}
