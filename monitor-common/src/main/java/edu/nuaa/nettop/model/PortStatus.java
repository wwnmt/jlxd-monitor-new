package edu.nuaa.nettop.model;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
public class PortStatus implements Serializable {

    private String name;

    private int status;

    private long byteRecv;

    private long byteSent;

    private long pktRecv;

    private long pktSent;
}
