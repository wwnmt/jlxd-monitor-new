package edu.nuaa.nettop.vo.lxd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LxdRequest implements Serializable {

    private List<String> deviceList = new ArrayList<>();
}
