package com.mycompany.app;

import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import java.sql.Statement;
import java.text.DateFormat;
import javafx.scene.control.Tab;

import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.telegram.abilitybots.api.objects.Ability;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.text.ParseException;
import java.util.function.Predicate;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import com.mycompany.app.Tables.*;
import com.mycompany.app.jobs.BroadcastJob;

import java.text.ParseException;
import java.util.*;
import java.time.*;
import java.time.format.*;
import java.sql.*;

public class HelloBot extends BaseBot {
  private Statement stmt;

  private final String REMOTE = "remote";
  private final String CUTI = "cuti";
  private final String SAKIT = "sakit";

  public HelloBot() throws Exception {
    super();
    // stmt = super.ConnecttoDB();
    // super.openDBConnection();
  }

  public Ability sayHelloWorld() {

    return Ability.builder().name("hello").info("says hello world!").input(0).locality(USER).privacy(ADMIN)
        .action(ctx -> silent.send("Hello world!", ctx.chatId())).post(ctx -> silent.send("Bye world!", ctx.chatId()))
        .build();
  }

  // start user from
  public Ability StartingBeforeConfiguration() {

    return Ability.builder().name("start").info("says hello world!").input(0).privacy(PUBLIC).locality(USER)
        .action(ctx -> {
          // String rst = "";
          if (ctx.arguments().length == 0) {

            super.openDBConnection();

            User user = Tables.USER.findFirst("username = ?", ctx.user().username());
            if (user == null) {
              user = new User();

              user.set("username", ctx.user().username());
              user.set("chat_id", ctx.chatId());
              // user.set("dob", "1935-12-06");
              user.saveIt();

              silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());

              silent.send(
                  "Untuk menambahkan supervisor kamu, silahkan tulis /addsupervisor1 [@username] atau /addsupervisor2 [@username]",
                  ctx.chatId());
              // close db
            } else {
              silent.send("Hallo untuk bantuan silahkan ketik /help", ctx.chatId());

            }
            super.closeDBConnection();

            // silent.send("start", ctx.chatId());
          }

        }).build();
  }

  public Ability StartingBeforeConfigurationGroup() {

    return Ability.builder().name("startgroup").info("says hello world!").input(0).privacy(PUBLIC).locality(ALL)
        .action(ctx -> {
          // String rst = "";
          if (ctx.arguments().length == 0) {

            super.openDBConnection();

            Group group = Tables.GROUP.findFirst("chat_id = ?",ctx.chatId());
            if (true) {
              group = new Group();

              group.set("nama", ctx.user().username());
              group.set("chat_id", ctx.chatId());
              //user.set("dob", "1935-12-06");
              group.saveIt();

              // silent.send("" + ctx.chatId(), ctx.chatId());

              // silent.send("Untuk menambahkan supervisor kamu, silahkan tulis
              // /addsupervisor1 [@username] atau /addsupervisor2 [@username]", ctx.chatId());
              // close db
            } else {
              silent.send("Notifikasi cuti/remote/sakit telah di set di group ini sebelumnya", ctx.chatId());

            }
            super.closeDBConnection();

            // silent.send("start", ctx.chatId());
          }

        }).build();
  }

  public Ability setRemote() {
    // arg 1 = tanggal
    // arg 2 = alasan
    return Ability.builder().name("remote").info("Set status menjadi remote").input(0).locality(USER).privacy(PUBLIC)
        .action(ctx -> {
          String tanggal = ctx.firstArg();
          String alasan = ctx.secondArg();
          for (int i = 2; i < ctx.arguments().length; i++) {
            alasan += " " + ctx.arguments()[i];
          }
          if (dateIsValid(tanggal)) {
            java.sql.Date newDate = changeDateFormat(tanggal);

            openDBConnection();

            String username = ctx.user().username();
            User user = Tables.USER.findFirst("username = ?", username);

            if (user != null) {
              History history = Tables.HISTORY.findFirst("user_id = ? AND tanggal = ? AND status = ?", user.get("id"),
                  newDate, REMOTE);

              if (history == null) {
                History record = new History();
                record.set("user_id", user.get("id")).set("status", REMOTE).set("tanggal", newDate).set("reason",
                    alasan);
                record.saveIt();
              } else {
                silent.send("Kamu sudah mengajukan " + REMOTE + " di tanggal " + tanggal, ctx.chatId());
              }
            }

            List<Group> chatIds = Tables.GROUP.findAll();
            String[] ids = new String[chatIds.size()];
            for (int a = 0; a < chatIds.size(); a++) {
              Group temp = chatIds.get(a);
              ids[a] = temp.get("chat_id").toString();
            }

            String identifier = ctx.firstArg() + "/remote";
            JobDetail job = registerJob(BroadcastJob.class, identifier);
            String message = String.join(" ", Arrays.copyOfRange(ctx.arguments(), 1, ctx.arguments().length));
            job.getJobDataMap().put("REASON", message);
            job.getJobDataMap().put("CHAT_IDS", ids);
            job.getJobDataMap().put("USER_ID", ctx.user().id());
            job.getJobDataMap().put("ACTION", "remote");

            Date targetTime = formatDate(ctx.firstArg());
            Trigger trigger = registerTrigger(targetTime, identifier);

            try {
				      queueJob(job, trigger);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }

            closeDBConnection();
          } else {
            // salah tanggal
            silent.send("Format tanggal salah", ctx.chatId());
          }
        }).build();
  }

  // blm
  public Ability setCuti() {
    // arg 1 = tanggal
    return Ability.builder().name("cuti").info("Set status menjadi cuti").input(2).locality(USER).privacy(PUBLIC)
        .action(ctx -> {
          String tanggalMulai = ctx.firstArg();
          String tanggalSelesai = ctx.secondArg();
          if (dateIsValid(tanggalMulai) && dateIsValid(tanggalSelesai)) {
            java.sql.Date newDateMulai = changeDateFormat(tanggalMulai);
            java.sql.Date newDateSelesai = changeDateFormat(tanggalSelesai);

            openDBConnection();

            String username = ctx.user().username();
            User user = Tables.USER.findFirst("username = ?", username);

            if (user != null) {
              History history = Tables.HISTORY.findFirst("user_id = ? AND tanggal = ? AND status = ?", user.get("id"),
                  newDateMulai, CUTI);

              if (history == null) {
                History record = new History();
                record.set("user_id", user.get("id")).set("status", CUTI).set("tanggal", newDateMulai);
                record.saveIt();
              } else {
                silent.send("Kamu sudah mengajukan " + CUTI + " di tanggal " + newDateMulai, ctx.chatId());
              }
            }

            List<Group> chatIds = Tables.GROUP.findAll();
            String[] ids = new String[chatIds.size()];
            for (int a = 0; a < chatIds.size(); a++) {
              Group temp = chatIds.get(a);
              ids[a] = temp.get("chat_id").toString();
            }

            String identifier = ctx.firstArg() + "/" + ctx.secondArg() + "/cuti";
            JobDetail job = registerJob(BroadcastJob.class, identifier);
            job.getJobDataMap().put("CHAT_IDS", ids);
            job.getJobDataMap().put("ACTION", "cuti");
            job.getJobDataMap().put("USER_ID", ctx.user().id());
            job.getJobDataMap().put("END_DATE", ctx.secondArg());

            Date targetTime = formatDate(ctx.firstArg());
            Trigger trigger = registerTrigger(targetTime, identifier);

            try {
				      queueJob(job, trigger);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            closeDBConnection();
          } else {
            // salah tanggal
            silent.send("Format tanggal salah", ctx.chatId());
          }
        }).build();
  }

  // start user from
  public Ability ConfigurationSupervisor1() {

    return Ability.builder().name("addsupervisor1").info("says hello world!").input(0).privacy(PUBLIC).locality(ALL)
        .action(ctx -> {
          // String rst = "";
          if (ctx.arguments().length == 1) {

            super.openDBConnection();

            User supervisor = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));
            if (supervisor == null) {
              silent.send("Username " + ctx.firstArg()
                  + " belum menambahkan bot ini, mohon informasikan dia untuk menambahkan bot ini sebelum ditambahkan untuk notifikasi kamu",
                  ctx.chatId());
            } else {
              User user = Tables.USER.findFirst("username = ?", ctx.user().username());

              user.set("supervisor1_id", supervisor.get("id"));
              // user.set("chat_id", ctx.chatId());
              // user.set("dob", "1935-12-06");
              user.saveIt();

              // silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());
              silent.send(
                  "Supervisor 1 kamu yakni @" + ctx.firstArg()
                      + " berhasil ditambahkan, silahkan tulis /addsupervisor2 untuk menambahkan supervisor 2",
                  ctx.chatId());
              // close db
            }

            super.closeDBConnection();
            // silent.send("start", ctx.chatId());
          } else {
            silent.send("Salah komentar, seharusnya  /addsupervisor1 [@username]", ctx.chatId());

          }

        }).build();
  }

  // blm
  public Ability setSakit() {
    // arg 1 = tanggal
    return Ability.builder().name("sakit").info("Set status menjadi sakit").input(1).locality(USER).privacy(PUBLIC)
        .action(ctx -> {
          String tanggal = ctx.firstArg();
          if (dateIsValid(tanggal)) {
            java.sql.Date newDate = changeDateFormat(tanggal);

            openDBConnection();

            String username = ctx.user().username();
            User user = Tables.USER.findFirst("username = ?", username);

            if (user != null) {
              History history = Tables.HISTORY.findFirst("user_id = ? AND tanggal = ? AND status = ?", user.get("id"),
                  newDate, SAKIT);

              if (history == null) {
                History record = new History();
                record.set("user_id", user.get("id")).set("status", SAKIT).set("tanggal", newDate);
                record.saveIt();
              } else {
                silent.send("Kamu sudah mengajukan " + SAKIT + " di tanggal " + tanggal, ctx.chatId());
              }
            }

            List<Group> chatIds = Tables.GROUP.findAll();
            String[] ids = new String[chatIds.size()];
            for (int a = 0; a < chatIds.size(); a++) {
              Group temp = chatIds.get(a);
              ids[a] = temp.get("chat_id").toString();
            }

            String identifier = ctx.firstArg() + "/sakit";
            JobDetail job = registerJob(BroadcastJob.class, identifier);
            job.getJobDataMap().put("CHAT_IDS", ids);
            job.getJobDataMap().put("ACTION", "sakit");
            job.getJobDataMap().put("USER_ID", ctx.user().id());
            job.getJobDataMap().put("END_DATE", ctx.secondArg());

            Date targetTime = formatDate(ctx.firstArg());
            Trigger trigger = registerTrigger(targetTime, identifier);

            try {
				      queueJob(job, trigger);
            } catch (Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            closeDBConnection();
          } else {
            // salah tanggal
            silent.send("Format tanggal salah", ctx.chatId());
          }
        }).build();
  }

  private Date formatDate(String date) {
    SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    try {
      Date oldDate = oldDateFormat.parse(date);
      return oldDate;
    } catch (ParseException e) {
      return null;
    }
  }

  private boolean dateIsValid(String tanggal) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    try {
      Date inputDate = dateFormat.parse(tanggal);
      Date currentDate = new Date();

      return inputDate.compareTo(currentDate) >= 0;
    } catch (ParseException e) {
      // salah format
      return false;
    }
  }

  private java.sql.Date changeDateFormat(String oldTanggal) {
    SimpleDateFormat oldDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    try {
      Date oldDate = oldDateFormat.parse(oldTanggal);

      String date = newDateFormat.format(oldDate);
      Date newDate = newDateFormat.parse(date);

      java.sql.Date sqlDate = new java.sql.Date(newDate.getTime());
      return sqlDate;
    } catch (ParseException e) {
      return null;
    }
  }

  public Ability ConfigurationSupervisor2() {

    return Ability.builder().name("addsupervisor2").info("says hello world!").input(0).privacy(PUBLIC).locality(ALL)
        .action(ctx -> {
          // String rst = "";
          if (ctx.arguments().length == 1) {
            super.openDBConnection();

            User supervisor = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));
            if (supervisor == null) {
              silent.send("Username " + ctx.firstArg()
                  + " belum menambahkan bot ini, mohon informasikan dia untuk menambahkan bot ini sebelum ditambahkan untuk notifikasi kamu",
                  ctx.chatId());
            } else {
              User user = Tables.USER.findFirst("username = ?", ctx.user().username());

              user.set("supervisor2_id", supervisor.get("id"));
              // user.set("chat_id", ctx.chatId());
              // user.set("dob", "1935-12-06");
              user.saveIt();

              // silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());
              silent.send(
                  "Supervisor 2 kamu yakni @" + ctx.firstArg()
                      + " berhasil ditambahkan, silahkan tulis /addsupervisor1 untuk menambahkan supervisor 2",
                  ctx.chatId());
              // close db
            }

            super.closeDBConnection();

            // silent.send("start", ctx.chatId());
          } else {
            silent.send("Salah komentar, seharusnya  /addsupervisor1 [@username]", ctx.chatId());

          }

        }).build();
  }

  public Ability historyByUsername() {
    return Ability.builder().name("historyByUsername").info("Lihat semua riwayat status dari seorang user").input(1)
        .locality(ALL).privacy(PUBLIC).action(ctx -> {
          String arg = ctx.firstArg();

          super.openDBConnection();
          User user = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));

          if (user != null) {

            int userID = Integer.parseInt(user.get("id").toString());
            List<History> history = Tables.HISTORY.where("user_id = ?", userID);

            if (history.size() > 0) {
              String finalString = "Hai kaka, berikut riwayat status si " + arg + " : \n";
              for (int i = 0; i < history.size(); i++) {
                String tanggal = history.get(i).get("tanggal").toString();
                String status = history.get(i).get("status").toString();
                Object alasanObj = history.get(i).get("reason");
                String alasan = "";
                if (alasanObj != null) {
                  alasan = history.get(i).get("reason").toString();
                }
                finalString += "tanggal : " + tanggal + ", status : " + status + ", alasan : " + alasan + "\n";
              }
              silent.send(finalString, ctx.chatId());
            } else {
              silent.send("History apapun belum ada", ctx.chatId());

            }

            // close db
            super.closeDBConnection();
          } else {
            silent.send("Username " + arg + " tidak terdaftar", ctx.chatId());
          }
        }).build();
  }

  public Ability historyByTanggal() {
    return Ability.builder().name("historyByTanggal").info("Lihat semua riwayat status pada tanggal tertentu").input(1)
        .locality(ALL).privacy(PUBLIC).action(ctx -> {
          try {
            String arg = ctx.secondArg();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
            LocalDate date = LocalDate.parse(arg, formatter);
            super.openDBConnection();
            List<History> history = Tables.HISTORY.where("tanggal = ?", date);
            String finalString = "";
            if (history.size() > 0) {
              finalString = "Hai kaka, berikut riwayat status pada tanggal " + arg + " : \n";

              for (int i = 0; i < history.size(); i++) {
                int id = Integer.parseInt(history.get(i).get("user_id").toString());
                String username = Tables.USER.findFirst("id = ?", id).get("username").toString();
                String status = history.get(i).get("status").toString();
                String alasan = "";
                try {
                  alasan = history.get(i).get("reason").toString();
                } catch (NullPointerException e) {

                }
                finalString += "username : @" + username + ", status : " + status + ", alasan : " + alasan + "\n";
              }
            } else {
              finalString = "Hai kaka, pada tanggal " + arg + " semuanya masuk nih";
            }
            silent.send(finalString, ctx.chatId());
          } catch (DateTimeParseException e) {
            silent.send("Hai kaka, format tanggal salah", ctx.chatId());
          }
          super.closeDBConnection();
        }).build();
  }
}
