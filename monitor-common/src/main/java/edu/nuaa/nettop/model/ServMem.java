package edu.nuaa.nettop.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-22
 * Time: 11:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServMem implements Serializable {

    private long total;
    private long avail;
    private long shared;
    private long buffer;
    private long cache;
}
