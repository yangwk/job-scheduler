package com.github.yangwk.jobscheduler.core.impl;

import java.util.function.Supplier;

import javax.sql.DataSource;

import com.github.yangwk.jobscheduler.core.impl.registry.Notifier;
import com.github.yangwk.jobscheduler.core.impl.registry.Server;
import com.github.yangwk.jobscheduler.core.impl.registry.Ticketer;
import com.github.yangwk.jobscheduler.core.jdbc.DataSourceCreator;
import com.github.yangwk.jobscheduler.core.jdbc.service.JobTaskService;
import com.github.yangwk.jobscheduler.core.jdbc.service.RegistryService;

public class JobSchedulerSupplier {
    
    static final Supplier<JobScheduler> SUPPLIER = () -> {
        DataSource dataSource = new DataSourceCreator().getDataSource();
                
        JobTaskService jobTaskService = new JobTaskService(dataSource);
        RegistryService registryService = new RegistryService(dataSource);
        
        JobExecutor jobExecutor = new JobExecutor();
        
        JobPoller jobPoller = new JobPoller(jobTaskService, jobExecutor);
        Server server = new Server(jobPoller, jobExecutor);
        Notifier notifier = new Notifier();
        Ticketer ticketer = new Ticketer(registryService, notifier);
        
        JobScheduler jobScheduler = new JobScheduler(jobExecutor, jobTaskService, notifier);
        server.start();
        ticketer.start();
        jobPoller.start();
        
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                try {
                    notifier.close();
                }finally {
                    try {
                        server.close();
                    }finally {
                        try {
                            jobExecutor.close();
                        }finally {
                            if(dataSource instanceof AutoCloseable) {
                                ((AutoCloseable)dataSource).close();
                            }
                        }
                    }
                }
            }catch(Exception e) {
                // ignore
            }
        }));
        
        return jobScheduler;
    };
}
