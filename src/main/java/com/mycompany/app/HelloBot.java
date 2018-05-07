package com.mycompany.app;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.telegram.abilitybots.api.objects.Ability;

import java.util.Date;

import com.mycompany.app.jobs.BroadcastJob;

public class HelloBot extends BaseBot {

  public HelloBot() throws Exception {
    super();
  }

  public Ability sayHelloWorld() {
    return Ability.builder().name("hello").info("says hello world!").locality(ALL).privacy(PUBLIC).input(1)
        .action(ctx -> {
          try {
            JobDetail job = registerJob(BroadcastJob.class);
            //\\ add params to job \\//
            // job.getJobDataMap().put("MESSAGE", "Test");
            // job.getJobDataMap().put("CHAT_IDS", new ArrayList<>());
            Trigger trigger = registerTrigger(new Date());
            queueJob(job, trigger);
          } catch (Exception e) {
            e.printStackTrace();
          }
          silent.send("Hello world!", ctx.chatId());
        }).build();
  }
}
