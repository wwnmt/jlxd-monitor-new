package edu.nuaa.nettop.vo;

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
 * Time: 18:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VrScreenRequest {

    private String wlid;

    List<String> serverIps = new ArrayList<>();


}
