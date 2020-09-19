package edu.nuaa.nettop.service.impl;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.dao.go.DeployDOMapper;
import edu.nuaa.nettop.dao.go.ServerDOMapper;
import edu.nuaa.nettop.dao.main.DDosTdDOMapper;
import edu.nuaa.nettop.dao.main.NodeDOMapper;
import edu.nuaa.nettop.dao.main.ServiceNetDOMapper;
import edu.nuaa.nettop.dao.main.TaskDOMapper;
import edu.nuaa.nettop.entity.DDosTaskDO;
import edu.nuaa.nettop.entity.TaskForDDosDO;
import edu.nuaa.nettop.quartz.TaskScheduler;
import edu.nuaa.nettop.service.ScreenService;
import edu.nuaa.nettop.task.DDosScreenTask;
import edu.nuaa.nettop.task.NodeMonitorTask;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.PerformanceScreenRequest;
import edu.nuaa.nettop.vo.VrScreenRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-17
 * Time: 16:30
 */
@Slf4j
@Service
public class ScreenServiceImpl implements ScreenService {

    private final TaskScheduler taskScheduler;
    private final TaskDOMapper taskDOMapper;
    private final DeployDOMapper deployDOMapper;
    private final ServiceNetDOMapper serviceNetDOMapper;
    private final NodeDOMapper nodeDOMapper;
    private final DDosTdDOMapper dDosTdDOMapper;
    private final ServerDOMapper serverDOMapper;

    @Autowired
    public ScreenServiceImpl(TaskScheduler taskScheduler, TaskDOMapper taskDOMapper, DeployDOMapper deployDOMapper, ServiceNetDOMapper serviceNetDOMapper, NodeDOMapper nodeDOMapper, DDosTdDOMapper dDosTdDOMapper, ServerDOMapper serverDOMapper) {
        this.taskScheduler = taskScheduler;
        this.taskDOMapper = taskDOMapper;
        this.deployDOMapper = deployDOMapper;
        this.serviceNetDOMapper = serviceNetDOMapper;
        this.nodeDOMapper = nodeDOMapper;
        this.dDosTdDOMapper = dDosTdDOMapper;
        this.serverDOMapper = serverDOMapper;
    }

    @Override
    public DDosScreenRequest createDDosScreen(String wlid) throws MonitorException{
        DDosScreenRequest request = new DDosScreenRequest();
        request.setWlid(wlid);
        //查找网络前缀
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        //根据wlid检索ddos任务
        TaskForDDosDO taskForDDosDO = taskDOMapper.findDDosTaskByWlid(wlid);
        if (CollectionUtils.isEmpty(taskForDDosDO.getDdosTaskDOs()))
            throw new MonitorException("未找到DDoS攻击任务");
        //TODO只针对一个DDos攻击任务
        DDosTaskDO dDosTaskDO = taskForDDosDO.getDdosTaskDOs().get(0);
        //设置攻击者参数
        DDosScreenRequest.Attacker attacker = new DDosScreenRequest.Attacker();
        String name = nodeDOMapper.findNodeNameByPrimaryKey(dDosTaskDO.getGjdxid());
        String serverIp = deployDOMapper.queryServerIpByDeviceName(pre + name);
        attacker.setName(name);
        attacker.setFullName(pre + name);
        attacker.setServerIp(serverIp);
        request.setAttacker(attacker);
        //设置被攻击者参数
        DDosScreenRequest.Victim victim = new DDosScreenRequest.Victim();
        name = nodeDOMapper.findNodeNameByPrimaryKey(dDosTaskDO.getBgjdxid());
        serverIp = deployDOMapper.queryServerIpByDeviceName(pre + name);
        String manageIp = deployDOMapper.queryManageIpByDeviceName(pre + name);
        victim.setName(name);
        victim.setFullName(pre + name);
        victim.setServerIp(serverIp);
        victim.setManageIp(manageIp);
        request.setVictim(victim);
        //设置傀儡机参数
        List<String> tdIds = dDosTdDOMapper.findTdsByRwid(dDosTaskDO.getRwid());
        tdIds.forEach(tdid -> {
            DDosScreenRequest.Td td = new DDosScreenRequest.Td();
            String tdName = nodeDOMapper.findNodeNameByPrimaryKey(tdid);
            String tdServerIp = deployDOMapper.queryServerIpByDeviceName(pre + tdName);
            String tdManageIp = deployDOMapper.queryManageIpByDeviceName(pre + tdName);
            td.setName(tdName);
            td.setFullName(pre + tdName);
            td.setServerIp(tdServerIp);
            td.setManageIp(tdManageIp);
            request.getTds().add(td);
        });
        //设置服务器列表
        List<String> serverIps = serverDOMapper.listServerIp();
        request.setServerIps(serverIps);
        return request;
    }

    @Override
    public void addDDosScreen(DDosScreenRequest request) throws MonitorException {
        //TODO 验证参数
        log.info("Recv ddos screen request-> {}", JSON.toJSONString(request));
        String jobName = request.getWlid();
        String jobGroup = TaskType.DDOS_SCREEN.getDesc();
        try {
            //判断任务已存在
            if (taskScheduler.checkExists(jobName, jobGroup)) {
                throw new MonitorException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            //配置参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("wlid", request.getWlid());
            jobDataMap.put("attacker", request.getAttacker());
            jobDataMap.put("victim", request.getVictim());
            jobDataMap.put("tds", request.getTds());
            jobDataMap.put("servers", request.getServerIps());
            //提交任务
            taskScheduler.publishJob(jobName, jobGroup, jobDataMap, 5, DDosScreenTask.class);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    @Override
    public void addVrScreen(VrScreenRequest request) throws MonitorException {

    }

    @Override
    public void addNetPerformanceScreen(PerformanceScreenRequest request) throws MonitorException {

    }

    @Override
    public void cancelScreen(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.cancelTask(jobName, jobGroup);
    }

    @Override
    public void deleteScreen(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.deleteTask(jobName, jobGroup);
    }
}
