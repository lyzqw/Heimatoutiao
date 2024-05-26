import com.heima.model.schedule.dtos.Task;
import com.heima.schedule.ScheduleApplication;
import com.heima.schedule.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@SpringBootTest(classes = ScheduleApplication.class)
@RunWith(SpringRunner.class)
public class TaskServiceImplTest {

    @Autowired
    private TaskService taskService;

    @Test
    public void addTask() {
//        添加zset任务的Task: {"executeTime":1716738874262,"parameters":"dGFzazIyMjY2ODg=","priority":20,"taskId":1794758032551436290,"taskType":10}
//        for (int i = 0; i < 5; i++) {
        Task task = new Task();
        task.setTaskType(10);
        task.setPriority(20);
        task.setParameters("task2226688".getBytes());
//        task.setExecuteTime(new Date().getTime() + TimeUnit.MINUTES.toMillis(4));
        task.setExecuteTime(new Date().getTime());

        long taskId = taskService.addTask(task);
        System.out.println(taskId);
//        }
    }

    @Test
    public void cancelTesk() {
        taskService.cancelTask(1794761432651374594L);
    }

    @Test
    public void testPoll() {
        Task task = taskService.poll(10, 20);
        System.out.println(task);
    }


}