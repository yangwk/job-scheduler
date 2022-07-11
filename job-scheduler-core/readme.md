
# readme

This is a distributed job scheduler, support One-Shot and Repeatable job.

# architecture

```

                        Database
                           ^
                           |
     ---------------------------------------------
     ^                     ^                     ^
     |                     |                     |
  mod shard             mod shard             mod shard
     |                     |                     |
     |                     |                     |
    poll                  poll                  poll
     |                     |                     |
     |                     |                     |
 JobScheduler-1      JobScheduler-2      JobScheduler-3
     |                     |                     |
     |                     |                     |
     |<-------broadcast------------broadcast---->|
     |                     |                     |
     |                     |                     |
update shard          update shard          update shard
     |                     |                     |
     |                     |                     |
     V                     V                     V
     ---------------------------------------------
                           |
                           V
                        Registry
```

# how to use

##### prepare MySQL

The job scheduler requires MySQL.

##### execute required SQL

sql/create.sql

##### Scheduler class

com.github.yangwk.jobscheduler.core.impl.JobScheduler


