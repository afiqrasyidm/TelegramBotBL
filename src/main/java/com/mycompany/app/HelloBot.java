package com.mycompany.app;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import com.mycompany.app.Tables.History;
import com.mycompany.app.jobs.BroadcastJob;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.telegram.abilitybots.api.objects.Ability;

public class HelloBot extends BaseBot {
  private Statement stmt;

  public HelloBot() throws Exception {
    super();
    stmt = super.ConnecttoDB();
    super.openDBConnection();
  }

  public Ability cuti() {
    return Ability.builder().name("cuti").info("izin cuti").locality(ALL).privacy(PUBLIC).input(0).action(ctx -> {
      try {
        String[] chatIds = { "505798110" };
        String identifier = ctx.firstArg() + "/" + ctx.secondArg() + "/cuti";
        // DateFormat format = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
        // Date date = format.parse(ctx.firstArg());
        Date targetTime = new Date(); // now
        targetTime = DateUtils.addMinutes(targetTime, 1);

        JobDetail job = registerJob(BroadcastJob.class, identifier);
        job.getJobDataMap().put("CHAT_IDS", chatIds);
        job.getJobDataMap().put("ACTION", "cuti");
        job.getJobDataMap().put("USER_ID", ctx.user().id());
        job.getJobDataMap().put("END_DATE", ctx.secondArg());

        Trigger trigger = registerTrigger(targetTime, identifier);

        queueJob(job, trigger);
      } catch (Exception e) {
        e.printStackTrace();
      }
      silent.send("Permintaan cuti berhasil disimpan.", ctx.chatId());
    }).build();
  }

  public Ability remote() {
    return Ability.builder().name("remote").info("izin remote").locality(ALL).privacy(PUBLIC).input(0).action(ctx -> {
      try {
        String[] chatIds = { "505798110" };
        String identifier = ctx.firstArg() + "/remote";
        // DateFormat format = new SimpleDateFormat("dd-mm-yyyy", Locale.ENGLISH);
        // Date date = format.parse(ctx.firstArg());
        Date targetTime = new Date(); // now
        targetTime = DateUtils.addMinutes(targetTime, 1);

        JobDetail job = registerJob(BroadcastJob.class, identifier);
        String message = String.join(" ", Arrays.copyOfRange(ctx.arguments(), 1, ctx.arguments().length));
        job.getJobDataMap().put("REASON", message);
        job.getJobDataMap().put("CHAT_IDS", chatIds);
        job.getJobDataMap().put("USER_ID", ctx.user().id());
        job.getJobDataMap().put("ACTION", "remote");

        Trigger trigger = registerTrigger(targetTime, identifier);

        queueJob(job, trigger);
      } catch (Exception e) {
        e.printStackTrace();
      }
      silent.send("Permintaan remote berhasil disimpan.", ctx.chatId());
    }).build();
  }

  public Ability db() {
    return Ability.builder().name("db").info("db").locality(ALL).privacy(PUBLIC).input(0).action(ctx -> {
      try {
        openDBConnection();
        java.sql.Date newDate = new java.sql.Date(new Date().getTime());
        String username = ctx.user().username();
        // User user = Tables.USER.findFirst("username = ?", username);

        History record = new History();
        record.set("id", ctx.firstArg());
        record.set("status", "cuti");
        record.set("tanggal", newDate);
        record.saveIt();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }).build();
  }
}
