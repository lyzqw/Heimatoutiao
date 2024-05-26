package com.heima.schedule.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.ScheduleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.schedule.dtos.Task;
import com.heima.model.schedule.pojos.Taskinfo;
import com.heima.model.schedule.pojos.TaskinfoLogs;
import com.heima.schedule.mapper.TaskinfoLogsMapper;
import com.heima.schedule.mapper.TaskinfoMapper;
import com.heima.schedule.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

@Service
@Transactional
@Slf4j
public class TaskServiceImpl implements TaskService {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private TaskinfoMapper taskinfoMapper;
    @Autowired
    private TaskinfoLogsMapper taskinfoLogsMapper;

    @Override
    public long addTask(Task task) {
        //1.添加任务到数据库中
        boolean success = addTaskToDb(task);
        if (success) {
            //2.添加任务到redis
            addTaskToCache(task);
        }
        return task.getTaskId();
    }

    private void addTaskToCache(Task task) {
        //存入数据库
        String key = task.getTaskType() + "_" + task.getPriority();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 5);
        long nextScheduleTime = calendar.getTimeInMillis();
        //存入Redis
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            cacheService.lLeftPush(ScheduleConstants.TOPIC + key, JSON.toJSONString(task));
        } else if (task.getExecuteTime() <= nextScheduleTime) {
            //2.2 如果任务的执行时间大于当前时间 && 小于等于预设时间（未来5分钟） 存入zset中
            String jsonString = JSON.toJSONString(task);
            System.out.println("添加zset任务的Task: " + jsonString);
            cacheService.zAdd(ScheduleConstants.FUTURE + key, jsonString, task.getExecuteTime());
        }
    }

    private boolean addTaskToDb(Task task) {
        boolean flag = false;
        try {
            //保存任务表
            Taskinfo taskinfo = new Taskinfo();
            BeanUtils.copyProperties(task, taskinfo);
            taskinfo.setExecuteTime(new Date(task.getExecuteTime()));
            taskinfoMapper.insert(taskinfo);
            //分布式唯一 ID 生成策略，通常也称为雪花算法（Snowflake algorithm）。
            task.setTaskId(taskinfo.getTaskId());

            //保存任务日志数据
            TaskinfoLogs taskinfoLogs = new TaskinfoLogs();
            BeanUtils.copyProperties(taskinfo, taskinfoLogs);
            taskinfoLogs.setVersion(1);
            taskinfoLogs.setStatus(ScheduleConstants.SCHEDULED);
            taskinfoLogsMapper.insert(taskinfoLogs);

            flag = true;
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean cancelTask(long taskId) {
        Task task = updateDb(taskId, ScheduleConstants.CANCELLED);
        if (task != null) {
            removeTaskFromCache(task);
            return true;
        }
        return false;
    }

    private void removeTaskFromCache(Task task) {
        String key = task.getTaskType() + "_" + task.getPriority();
        if (task.getExecuteTime() <= System.currentTimeMillis()) {
            Long l = cacheService.lRemove(ScheduleConstants.TOPIC + key, 0, JSON.toJSONString(task));
            System.out.println("删除List任务: " + l);
        } else {
            String jsonString = JSON.toJSONString(task);
            System.out.println("删除zset任务的Task: " + jsonString);
            Long l = cacheService.zRemove(ScheduleConstants.FUTURE + key, jsonString);
            System.out.println("删除zset任务: " + l);
        }
    }

    private Task updateDb(long taskId, int status) {
        Task task = null;
        try {
            //删除库
            taskinfoMapper.deleteById(taskId);
            //更新日志
            TaskinfoLogs taskinfoLogs = taskinfoLogsMapper.selectById(taskId);
            taskinfoLogs.setStatus(status);
            taskinfoLogsMapper.updateById(taskinfoLogs);

            task = new Task();
            BeanUtils.copyProperties(taskinfoLogs, task);
            task.setExecuteTime(taskinfoLogs.getExecuteTime().getTime());
        } catch (Exception e) {
            log.error("task cancel exception taskid={}", taskId);
        }

        return task;
    }

    @Override
    public Task poll(int type, int priority) {
        Task task = null;
        try {
            String key = type + "_" + priority;
            String task_json = cacheService.lRightPop(ScheduleConstants.TOPIC + key);
            if (StringUtils.isNotEmpty(task_json)) {
                task = JSON.parseObject(task_json, Task.class);
                if (task != null) {
                    updateDb(task.getTaskId(), ScheduleConstants.EXECUTED);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("poll task exception");
        }
        return task;
    }
}
