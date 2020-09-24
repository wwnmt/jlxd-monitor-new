package edu.nuaa.nettop.service.impl;

import edu.nuaa.nettop.exception.ProxyException;
import edu.nuaa.nettop.lxd.LxdMonitorHandle;
import edu.nuaa.nettop.model.LxdStatus;
import edu.nuaa.nettop.service.LxdService;
import edu.nuaa.nettop.vo.lxd.LxdRequest;
import edu.nuaa.nettop.vo.lxd.LxdResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ForkJoinPool;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: WeinanWu
 * Date: 2020-09-15
 * Time: 15:30
 */
@Slf4j
@Service
public class LxdServiceImpl implements LxdService {

    private static final ForkJoinPool forkJoinPool = new ForkJoinPool();

    @Override
    public LxdResponse getLxdStatus(LxdRequest request) throws ProxyException {

        LxdMonitorHandle task = new LxdMonitorHandle(request.getDeviceList());
        forkJoinPool.invoke(task);
        Set<LxdStatus> result = task.getRawResult();
        if (result == null || result.size() == 0) {
            throw new ProxyException("无法获取状态数据");
        }
        LxdResponse response = new LxdResponse();
        for (LxdStatus lxdStatus : result) {
            if (lxdStatus.getStatus() == 1) {
                response.getLxdStatuses().add(lxdStatus);
            } else {
                response.getErrDevs().add(lxdStatus.getName());
            }
        }
        return response;
    }
}
