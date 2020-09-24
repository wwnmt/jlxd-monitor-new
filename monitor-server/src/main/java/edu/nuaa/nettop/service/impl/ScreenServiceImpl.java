package edu.nuaa.nettop.service.impl;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.Constants;
import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.obj.NetPortObj;
import edu.nuaa.nettop.common.obj.ServerObj;
import edu.nuaa.nettop.common.utils.CommonUtils;
import edu.nuaa.nettop.common.utils.ProxyUtil;
import edu.nuaa.nettop.dao.go.DeployDOMapper;
import edu.nuaa.nettop.dao.go.PhysicalPortDOMapper;
import edu.nuaa.nettop.dao.go.ServerDOMapper;
import edu.nuaa.nettop.dao.main.DDosTdDOMapper;
import edu.nuaa.nettop.dao.main.LinkDOMapper;
import edu.nuaa.nettop.dao.main.NodeDOMapper;
import edu.nuaa.nettop.dao.main.PhysicalDevDOMapper;
import edu.nuaa.nettop.dao.main.PortDOMapper;
import edu.nuaa.nettop.dao.main.ServiceNetDOMapper;
import edu.nuaa.nettop.dao.main.TaskDOMapper;
import edu.nuaa.nettop.entity.DDosTaskDO;
import edu.nuaa.nettop.entity.LinkDO;
import edu.nuaa.nettop.entity.PhysicalDevDO;
import edu.nuaa.nettop.entity.PhysicalPortDO;
import edu.nuaa.nettop.entity.TaskForDDosDO;
import edu.nuaa.nettop.model.ServPort;
import edu.nuaa.nettop.quartz.TaskScheduler;
import edu.nuaa.nettop.service.ScreenService;
import edu.nuaa.nettop.task.DDosScreenTask;
import edu.nuaa.nettop.task.PerfScreenTask;
import edu.nuaa.nettop.task.VrScreenTask;
import edu.nuaa.nettop.vo.DDosScreenRequest;
import edu.nuaa.nettop.vo.PerfScreenRequest;
import edu.nuaa.nettop.vo.VrScreenRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final LinkDOMapper linkDOMapper;
    private final PortDOMapper portDOMapper;
    private final PhysicalDevDOMapper physicalDevDOMapper;
    private final PhysicalPortDOMapper physicalPortDOMapper;

    @Autowired
    public ScreenServiceImpl(TaskScheduler taskScheduler, TaskDOMapper taskDOMapper, DeployDOMapper deployDOMapper, ServiceNetDOMapper serviceNetDOMapper, NodeDOMapper nodeDOMapper, DDosTdDOMapper dDosTdDOMapper, ServerDOMapper serverDOMapper, LinkDOMapper linkDOMapper, PortDOMapper portDOMapper, PhysicalDevDOMapper physicalDevDOMapper, PhysicalPortDOMapper physicalPortDOMapper) {
        this.taskScheduler = taskScheduler;
        this.taskDOMapper = taskDOMapper;
        this.deployDOMapper = deployDOMapper;
        this.serviceNetDOMapper = serviceNetDOMapper;
        this.nodeDOMapper = nodeDOMapper;
        this.dDosTdDOMapper = dDosTdDOMapper;
        this.serverDOMapper = serverDOMapper;
        this.linkDOMapper = linkDOMapper;
        this.portDOMapper = portDOMapper;
        this.physicalDevDOMapper = physicalDevDOMapper;
        this.physicalPortDOMapper = physicalPortDOMapper;
    }

    @Override
    public DDosScreenRequest createDDosScreen(String wlid) throws MonitorException {
        DDosScreenRequest request = new DDosScreenRequest();
        request.setWlid(wlid);
        //查找网络前缀
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        //根据wlid检索ddos任务
        TaskForDDosDO taskForDDosDO = taskDOMapper.findDDosTaskByWlid(wlid);
        if (CollectionUtils.isEmpty(taskForDDosDO.getDdosTaskDOs())) {
            throw new MonitorException("未找到DDoS攻击任务");
        }
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
    public VrScreenRequest createVrScreen(String wlid) throws MonitorException {
        VrScreenRequest request = new VrScreenRequest();

        request.setWlid(wlid);
        //读取服务器列表
        List<String> serverIps = serverDOMapper.listServerIp();
        request.setServerIps(serverIps);
        //读取虚实互联接口信息
        List<PhysicalPortDO> physicalPortDOList = physicalPortDOMapper.findPportByWlid(wlid);
        for (PhysicalPortDO portDO : physicalPortDOList) {
            VrScreenRequest.VrPort vrPort = new VrScreenRequest.VrPort(
                    portDO.getServerIp(),
                    deployDOMapper.queryManageIpByDeviceName(portDO.getNodeName()),
                    portDO.getIntName(),
                    portDO.getPhyIntName()
            );
            request.getPorts().add(vrPort);
        }
        return request;
    }

    @Override
    public PerfScreenRequest createPerfScreen(String wlid) throws MonitorException {
        PerfScreenRequest request = new PerfScreenRequest();

        request.setWlid(wlid);
        //读取服务器列表
        List<String> serverIps = serverDOMapper.listServerIp();
        request.setServerIps(serverIps);
        //读取链路信息
        List<LinkDO> linkDOList = linkDOMapper.findByWlid(wlid);
        Map<String, String> linkInfoMap = new HashMap<>();
        for (LinkDO linkDO : linkDOList) {
            if (linkDO.getLlbs() != null && linkDO.getLlid() != null) {
                linkInfoMap.put(linkDO.getLlid(), linkDO.getLlbs());
            } else {
                String fromName = nodeDOMapper.findNodeNameByPrimaryKey(linkDO.getYsbid());
                String fromPort = portDOMapper.findNameByPrimaryKey(linkDO.getYdk());
                String toName = nodeDOMapper.findNodeNameByPrimaryKey(linkDO.getMdsbid());
                String toPort = portDOMapper.findNameByPrimaryKey(linkDO.getMddk());
                linkInfoMap.put(linkDO.getLlid(),
                                fromName + "-" + fromPort + ":" + toName + "-" + toPort);
            }
        }
        request.setLinkInfoMap(linkInfoMap);
        //读取链路带宽
        Map<String, Integer> linkBandwidthMap = new HashMap<>();
        for (LinkDO linkDO : linkDOList) {
            String ydkId = linkDO.getYdk();
            String mddkId = linkDO.getMddk();
            Integer ydk = portDOMapper.findDkByPrimaryKey(ydkId);
            Integer mdk = portDOMapper.findDkByPrimaryKey(mddkId);
            linkBandwidthMap.put(linkDO.getLlid(), (ydk + mdk) / 2);
        }
        request.setLinkBandwidthMap(linkBandwidthMap);
        //读取路由器名称
        String sbid;
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        if (Constants.perfDevMap.containsKey(wlid)) {
            sbid = Constants.perfDevMap.get(wlid);
        } else {
            sbid = nodeDOMapper.findNodeByWlid(wlid).get(0).getSbid();
        }
        String devName = nodeDOMapper.findNodeNameByPrimaryKey(sbid);
        String serverIp = deployDOMapper.queryServerIpByDeviceName(pre + devName);
        request.setRouterName(pre + devName);
        request.setRouterDeployServer(serverIp);
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
        //TODO 验证参数
        log.info("Recv Vr screen request-> {}", JSON.toJSONString(request));
        String jobName = request.getWlid();
        String jobGroup = TaskType.VR_SCREEN.getDesc();
        try {
            //判断任务已存在
            if (taskScheduler.checkExists(jobName, jobGroup)) {
                throw new MonitorException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            //配置参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("wlid", request.getWlid());
            jobDataMap.put("servers", request.getServerIps());
            jobDataMap.put("vrPort", request.getPorts());
            //提交任务
            taskScheduler.publishJob(jobName, jobGroup, jobDataMap, 5, VrScreenTask.class);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    @Override
    public void addPerformanceScreen(PerfScreenRequest request) throws MonitorException {
        //TODO 验证参数
        log.info("Recv Perf screen request-> {}", JSON.toJSONString(request));
        String jobName = request.getWlid();
        String jobGroup = TaskType.PRO_SCREEN.getDesc();
        try {
            //判断任务已存在
            if (taskScheduler.checkExists(jobName, jobGroup)) {
                throw new MonitorException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            //配置参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("wlid", request.getWlid());
            jobDataMap.put("servers", request.getServerIps());
            jobDataMap.put("links", request.getLinkInfoMap());
            jobDataMap.put("bandwidth", request.getLinkBandwidthMap());
            jobDataMap.put("router", request.getRouterName());
            jobDataMap.put("ip", request.getRouterDeployServer());
            //提交任务
            taskScheduler.publishJob(jobName, jobGroup, jobDataMap, 5, PerfScreenTask.class);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    @Override
    public void cancelScreen(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.cancelTask(jobName, jobGroup);
    }

    @Override
    public void deleteScreen(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.deleteTask(jobName, jobGroup);
        //清空redis缓存数据
        CommonUtils.delInRedis(jobName + "ddostm");
        CommonUtils.delInRedis(jobName + "vrtm");
        CommonUtils.delInRedis(jobName + "perftm");
    }

    @Override
    public ServerObj getPhysicalInterfaceInfo(String wlid, String sbid) throws MonitorException {
        // PhysicalDevDO physicalDevDO = physicalDevDOMapper.selectByPrimaryKey(sbid);
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        String serverIp = deployDOMapper.queryServerIpByDeviceName(
                pre + nodeDOMapper.findNodeNameByPrimaryKey(sbid));
        // String physicalIntName = physicalDevDO.getLjsb();
        ServPort servPort = ProxyUtil.getServPort(serverIp, serverIp);
        ServerObj serverObj = new ServerObj();
        serverObj.setMc(serverIp);
        for (ServPort.Port port : servPort.getPorts()) {
            if (port.getDkmc().equals("lo")
                    || port.getDkmc().startsWith("veth")
                    || port.getDkmc().startsWith("lxd")
                    || port.getDkmc().startsWith("Mana"))
                continue;
            NetPortObj portObj = new NetPortObj();
            portObj.setIp(port.getIp());
            portObj.setMc(port.getDkmc());
            serverObj.addPort(portObj);
        }
        List<PhysicalPortDO> physicalPortDOList = physicalPortDOMapper.findPportByWlid(wlid);
        for (PhysicalPortDO portDO : physicalPortDOList) {
            String deviceIp = deployDOMapper.queryManageIpByDeviceName(portDO.getNodeName());
            servPort = ProxyUtil.getServPort(portDO.getServerIp(), deviceIp);
            for (ServPort.Port port : servPort.getPorts()) {
                if (port.getDkmc().equals(portDO.getIntName())) {
                    NetPortObj portObj = new NetPortObj();
                    portObj.setIp(port.getIp());
                    portObj.setMc(portDO.getPhyIntName());
                    serverObj.addPort(portObj);
                }
            }
        }
        return serverObj;
    }
}
