package edu.nuaa.nettop.service.impl;

import com.alibaba.fastjson.JSON;
import edu.nuaa.nettop.common.constant.TaskType;
import edu.nuaa.nettop.common.exception.MonitorException;
import edu.nuaa.nettop.common.model.Link;
import edu.nuaa.nettop.common.model.Node;
import edu.nuaa.nettop.dao.go.DeployDOMapper;
import edu.nuaa.nettop.dao.main.LinkDOMapper;
import edu.nuaa.nettop.dao.main.NodeDOMapper;
import edu.nuaa.nettop.dao.main.PortDOMapper;
import edu.nuaa.nettop.dao.main.ServiceNetDOMapper;
import edu.nuaa.nettop.entity.LinkDO;
import edu.nuaa.nettop.entity.NodeDO;
import edu.nuaa.nettop.quartz.TaskScheduler;
import edu.nuaa.nettop.service.MonitorService;
import edu.nuaa.nettop.task.NetMonitorTask;
import edu.nuaa.nettop.task.NodeMonitorTask;
import edu.nuaa.nettop.vo.NetRequest;
import edu.nuaa.nettop.vo.NodeRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 16:50
 */
@Slf4j
@Service
public class MonitorServiceImpl implements MonitorService {

    private final TaskScheduler taskScheduler;

    private final ServiceNetDOMapper serviceNetDOMapper;
    private final NodeDOMapper nodeDOMapper;
    private final DeployDOMapper deployDOMapper;
    private final LinkDOMapper linkDOMapper;
    private final PortDOMapper portDOMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Autowired
    public MonitorServiceImpl(TaskScheduler taskScheduler,
                              ServiceNetDOMapper serviceNetDOMapper,
                              NodeDOMapper nodeDOMapper,
                              DeployDOMapper deployDOMapper,
                              LinkDOMapper linkDOMapper,
                              PortDOMapper portDOMapper, StringRedisTemplate stringRedisTemplate) {
        this.taskScheduler = taskScheduler;
        this.serviceNetDOMapper = serviceNetDOMapper;
        this.nodeDOMapper = nodeDOMapper;
        this.deployDOMapper = deployDOMapper;
        this.linkDOMapper = linkDOMapper;
        this.portDOMapper = portDOMapper;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public NetRequest createNetMonitorTask(String wlid) throws MonitorException {
        NetRequest request = new NetRequest();
        //读取该网络的网络前缀
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        //读取该网络下所有节点信息
        List<NodeDO> nodeDOList = nodeDOMapper.findNodeByWlid(wlid);
        if (CollectionUtils.isEmpty(nodeDOList))
            throw new MonitorException("节点信息不存在");
        List<Node> nodes = nodeDOList.parallelStream().map(nodeDO -> {
            Node node = new Node();
            node.setNodeId(nodeDO.getSbid());
            node.setName(pre + nodeDO.getSbmc());
            node.setServerIp(deployDOMapper.queryServerIpByDeviceName(node.getName()));
            return node;
        }).collect(Collectors.toList());
        //读取链路信息、
        boolean flag = true;
        List<Link> links;
        List<String> linkInfoList = linkDOMapper.findLlbsByWlid(wlid);
        if (CollectionUtils.isEmpty(linkInfoList))
            throw new MonitorException("链路信息不存在");
        for (String linkInfo : linkInfoList) {
            if (linkInfo == null) {
                flag = false;
                break;
            }
        }
        //通过读取LLBS字段创建links
        if (flag) {
            links = linkInfoList.parallelStream().map(linkInfo -> {
                Link link = new Link();
                String[] strings1 = linkInfo.split(":");
                link.setFrom(strings1[0].split("-")[0]);
                link.setFromPort(strings1[0].split("-")[1]);
                link.setTo(strings1[1].split("-")[0]);
                link.setToPort(strings1[1].split("-")[1]);
                return link;
            }).collect(Collectors.toList());
        } else {
            //通过普通方法创建links
            List<LinkDO> linkDOList = linkDOMapper.findByWlid(wlid);
            if (CollectionUtils.isEmpty(linkDOList))
                throw new MonitorException("链路信息不存在");
            links = linkDOList.parallelStream().map(linkDO -> {
                Link link = new Link();
                String fromName = nodeDOMapper.findNodeNameByPrimaryKey(linkDO.getYsbid());
                String fromPort = portDOMapper.findNameByPrimaryKey(linkDO.getYdk());
                String toName = nodeDOMapper.findNodeNameByPrimaryKey(linkDO.getMdsbid());
                String toPort = portDOMapper.findNameByPrimaryKey(linkDO.getMddk());
                link.setLinkId(linkDO.getLlid());
                link.setFrom(pre + fromName);
                link.setFromPort(fromPort);
                link.setTo(pre + toName);
                link.setToPort(toPort);
                return link;
            }).collect(Collectors.toList());
        }
        //
        request.setWlid(wlid);
        request.setNodes(nodes);
        request.setLinks(links);
        request.setTimeout(7);
        return request;
    }

    @Override
    public void addNetMonitorTask(NetRequest request) throws MonitorException {
        if (!request.validate())
            throw new MonitorException("参数错误");
        String jobName = request.getWlid();
        String jobGroup = TaskType.NET_TASK.getDesc();
        try {
            //判断任务已存在
            if (taskScheduler.checkExists(jobName, jobGroup)) {
                throw new MonitorException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            //配置参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("wlid", request.getWlid());
            jobDataMap.put("nodes", JSON.toJSONString(request.getNodes()));
            jobDataMap.put("links", JSON.toJSONString(request.getLinks()));
            //提交任务
            taskScheduler.publishJob(jobName, jobGroup, jobDataMap, request.getTimeout(), NetMonitorTask.class);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    @Override
    public NodeRequest createNodeMonitorTask(String wlid, String sbid) throws MonitorException {
        NodeRequest request = new NodeRequest();
        //读取该网络的网络前缀
        String pre = serviceNetDOMapper.getYxidByPrimaryKey(wlid);
        NodeDO nodeDO = nodeDOMapper.selectByPrimaryKey(sbid);
        String nodeName = pre + nodeDO.getSbmc();
        String serverIp = deployDOMapper.queryServerIpByDeviceName(nodeName);
        //
        request.setWlid(wlid);
        request.setNodeId(sbid);
        request.setName(nodeName);
        request.setServerIp(serverIp);
        request.setTimeout(7);
        return request;
    }

    @Override
    public void addNodeMonitorTask(NodeRequest request) throws MonitorException {
        if (!request.validate())
            throw new MonitorException("参数错误");
        String jobName = request.getNodeId();
        String jobGroup = TaskType.NODE_TASK.getDesc();
        try {
            //判断任务已存在
            if (taskScheduler.checkExists(jobName, jobGroup)) {
                throw new MonitorException(String.format("Job已经存在, jobName:{%s},jobGroup:{%s}", jobName, jobGroup));
            }
            //配置参数
            JobDataMap jobDataMap = new JobDataMap();
            jobDataMap.put("wlid", request.getWlid());
            jobDataMap.put("sbid", request.getNodeId());
            jobDataMap.put("nodeName", request.getName());
            jobDataMap.put("serverIp", request.getServerIp());
            //提交任务
            taskScheduler.publishJob(jobName, jobGroup, jobDataMap, request.getTimeout(), NodeMonitorTask.class);
        } catch (SchedulerException e) {
            throw new MonitorException(e.getMessage());
        }
    }

    @Override
    public void cancelTask(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.cancelTask(jobName, jobGroup);
    }

    @Override
    public void deleteTask(String jobName, String jobGroup) throws MonitorException {
        taskScheduler.deleteTask(jobName, jobGroup);
        //删除redis数据
        //TODO
    }
}
