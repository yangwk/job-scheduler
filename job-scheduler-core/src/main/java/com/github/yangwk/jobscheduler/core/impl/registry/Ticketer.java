package com.github.yangwk.jobscheduler.core.impl.registry;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.yangwk.jobscheduler.core.impl.JobSchedulerConfig;
import com.github.yangwk.jobscheduler.core.impl.JobShard;
import com.github.yangwk.jobscheduler.core.jdbc.entity.RegistryEntity;
import com.github.yangwk.jobscheduler.core.jdbc.service.RegistryService;

public class Ticketer {
    private static final Logger LOG = LoggerFactory.getLogger(Ticketer.class);
    
    private final RegistryService registryService;
    private final HostInfo hostInfo;
    private final Notifier notifier;
    
    public Ticketer(RegistryService registryService, Notifier notifier) {
        this.registryService = registryService;
        this.hostInfo = JobSchedulerConfig.getHostInfo();
        this.notifier = notifier;
    }
    
    
    public void start() {
        Thread thread = new Thread(() -> {
            setup();
            
            final long period = JobSchedulerConfig.getRegistryPeriod();
            while(true) {
                try {
                    work();
                    
                    try {
                        Thread.sleep(period);
                    } catch (InterruptedException e) {
                        // ignore
                    }
                    
                    registryService.updateTicketTime(hostInfo.hostIp, hostInfo.port, new Date());
                }catch (Throwable t) {
                    if(LOG.isErrorEnabled()) {
                        LOG.error("work error", t);
                    }
                }
            }
        });
        thread.setDaemon(true);
        thread.setName("Ticketer");
        thread.start();
    }
    
    
    private void setup(){
        try {
            RegistryEntity registryEntity = registryService.selectOne(hostInfo.hostIp, hostInfo.port);
            if(registryEntity == null) {
                registryEntity = new RegistryEntity();
                registryEntity.setHost(hostInfo.hostIp);
                registryEntity.setPort(hostInfo.port);
                registryEntity.setTicketTime(new Date());
                registryEntity.setEnabled(1);
                boolean s = registryService.insert(registryEntity);
                if(! s) {
                    System.exit(1);
                }
            }else {
                boolean s = registryService.updateTicketTime(hostInfo.hostIp, hostInfo.port, new Date());
                if(! s) {
                    System.exit(1);
                }
            }
        }catch (Throwable t) {
            System.exit(1);
        }
    }
    
    private void work() {
        List<RegistryEntity> list = registryService.selectEnabled();
        
        final long overTime = JobSchedulerConfig.getRegistryPeriod() * 3;
        final long refTime = new Date().getTime();
        List<RegistryEntity> toDeleteList = new ArrayList<>();
        Iterator<RegistryEntity> iter = list.iterator();
        while(iter.hasNext()) {
            RegistryEntity re = iter.next();
            if(refTime - re.getTicketTime().getTime() > overTime && !new HostInfo(re.getHost(), re.getPort()).equals(hostInfo) ) {
                toDeleteList.add(re);
                iter.remove();
            }
        }
        
        List<HostInfo> hostInfoList = list.stream().map(re -> new HostInfo(re.getHost(), re.getPort())).sorted().collect(Collectors.toList());
        if(CollectionUtils.isEmpty(hostInfoList)) {
            hostInfoList.add(hostInfo);
        }
        int count = hostInfoList.size();
        int current = 0;
        for(HostInfo hi : hostInfoList) {
            if(hostInfo.equals(hi)) {
                break;
            }
            current ++;
        }
        Pair<Integer,Integer> pair = Pair.of(count,current);
        JobShard.update(pair);
        if(LOG.isDebugEnabled()) {
            LOG.debug("shard {}", pair.toString());
        }
        
        notifier.update(hostInfoList);
        
        toDeleteList.forEach(d -> {
            registryService.updateDisabled(d.getHost(), d.getPort(), d.getTicketTime());
        });
    }
    
}
