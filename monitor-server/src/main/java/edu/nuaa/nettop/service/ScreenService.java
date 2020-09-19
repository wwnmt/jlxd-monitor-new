package edu.nuaa.nettop.service;

import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.PerformanceScreenRequest;
import edu.nuaa.nettop.vo.VrScreenRequest;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:19
 */
public interface ScreenService {

    DDosScreenRequest createDDosScreen(String wlid) throws MonitorException;

    void addDDosScreen(DDosScreenRequest request) throws MonitorException;

    void addVrScreen(VrScreenRequest request) throws MonitorException;

    void addNetPerformanceScreen(PerformanceScreenRequest request) throws MonitorException;

    void cancelScreen(String jobName, String jobGroup) throws MonitorException;

    void deleteScreen(String jobName, String jobGroup) throws MonitorException;
}
