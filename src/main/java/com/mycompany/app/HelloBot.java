package com.mycompany.app;

import org.telegram.abilitybots.api.objects.Ability;

import org.telegram.abilitybots.api.sender.SilentSender;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.text.ParseException;
import java.util.function.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

import com.mycompany.app.Tables.*;

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
        //stmt = super.ConnecttoDB();
        //super.openDBConnection();
    }

    public Ability playWithMe() {
        String playMessage = "Play with me!";

        return Ability
                .builder()
                .name("play")
                .info("Do you want to play with me?")
                .privacy(PUBLIC)
                .locality(ALL)
                .input(0)
                .action(ctx -> silent.forceReply(playMessage, ctx.chatId()))
                // The signature of a reply is -> (Consumer<Update> action, Predicate<Update>... conditions)
                // So, we  first declare the action that takes an update (NOT A MESSAGECONTEXT) like the action above
                // The reason of that is that a reply can be so versatile depending on the message, context becomes an inefficient wrapping
                .reply(upd -> {
                            // Prints to console
                            System.out.println("I'm in a reply!");
                            // Sends message
                            silent.send("It's been nice playing with you!", upd.getMessage().getChatId());
                        },
                    // Now we start declaring conditions, MESSAGE is a member of the enum Flag class
                    // That class contains out-of-the-box predicates for your replies!
                    // MESSAGE means that the update must have a message
                    // This is imported statically, Flag.MESSAGE
                    MESSAGE,
                    // REPLY means that the update must be a reply, Flag.REPLY
                    REPLY,
                    // A new predicate user-defined
                    // The reply must be to the bot
                    isReplyToBot(),
                    // If we process similar logic in other abilities, then we have to make this reply specific to this message
                    // The reply is to the playMessage
                    isReplyToMessage(playMessage)
                )
                // You can add more replies by calling .reply(...)
                .build();
    }

    private Predicate<Update> isReplyToMessage(String message) {
        return upd -> {
            Message reply = upd.getMessage().getReplyToMessage();
            return reply.hasText() && reply.getText().equalsIgnoreCase(message);
        };
    }

    private Predicate<Update> isReplyToBot() {
        return upd -> upd.getMessage().getReplyToMessage().getFrom().getUserName().equalsIgnoreCase(getBotUsername());
      }


      public Ability sayHelloWorld() {


          return Ability
                    .builder()
                    .name("hello")
                    .info("says hello world!")
                    .input(0)
                    .locality(USER)
                    .privacy(ADMIN)
                    .action(ctx -> silent.send("Hello world!", ctx.chatId()))
                    .post(ctx -> silent.send("Bye world!", ctx.chatId()))
                    .build();
      }
      // //untuk percobaan query db jadi tulis /start <argument apapun>
      // public Ability TestingDb()  {
      //
      //
      //     return Ability
      //               .builder()
      //               .name("start")
      //               .info("says hello world!")
      //               .input(0)
      //               .locality(USER)
      //               .privacy(ADMIN)
      //               .action(ctx ->       {
      //               //    String rst = "";
      //                   if(ctx.arguments().length == 0 ){
      //
      //                         silent.send("start", ctx.chatId());
      //                   }
      //                   else{
      //                           //open db connection (it is a must before a query)
      //                           super.openDBConnection();
      //                           User user = Tables.USER.findFirst("id = ?", 1);
      //                           silent.send(" "+ user.get("username"), ctx.chatId());
      //                           //close db
      //                           super.closeDBConnection();
      //                   }
      //
      //                })
      //               .build();
      // }


      //start user from
      public Ability StartingBeforeConfiguration()  {


          return Ability
                    .builder()
                    .name("start")
                    .info("says hello world!")
                    .input(0)
                    .privacy(PUBLIC)
                    .locality(USER)
                    .action(ctx ->       {
                    //    String rst = "";
                        if(ctx.arguments().length == 0 ){

                              super.openDBConnection();


                              User user = Tables.USER.findFirst("username = ?", ctx.user().username());
                                if(user == null){
                                  user = new User();

                                  user.set("username", ctx.user().username());
                                  user.set("chat_id",  ctx.chatId());
                            //    user.set("dob", "1935-12-06");
                                  user.saveIt();


                                  silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());

                                  silent.send("Untuk menambahkan supervisor kamu, silahkan tulis /addsupervisor1 [@username] atau /addsupervisor2 [@username]", ctx.chatId());
                                //close db
                              }
                              else{
                                silent.send("Hallo untuk bantuan silahkan ketik /help", ctx.chatId());


                              }
                              super.closeDBConnection();


                            //  silent.send("start", ctx.chatId());
                        }

                     })
                    .build();
      }

  public Ability StartingBeforeConfigurationGroup()  {


          return Ability
                    .builder()
                    .name("startgroup")
                    .info("says hello world!")
                    .input(0)
                    .privacy(PUBLIC)
                    .locality(ALL)
                    .action(ctx ->       {
                    //    String rst = "";
                        if(ctx.arguments().length == 0 ){

                              super.openDBConnection();


                            //  Group group = Tables.GROUP.findFirst("chat_id = ?",ctx.chatId());
                                if(true){
                                //  group = new Group();

                                  //user.set("username", ctx.user().username());
                              //    group.set("chat_id",  ctx.chatId());
                            //    user.set("dob", "1935-12-06");
                                //  group.saveIt();


                                  silent.send(""+ctx.chatId(), ctx.chatId());

                                //  silent.send("Untuk menambahkan supervisor kamu, silahkan tulis /addsupervisor1 [@username] atau /addsupervisor2 [@username]", ctx.chatId());
                                //close db
                              }
                              else{
                                silent.send("Notifikasi cuti/remote/sakit telah di set di group ini sebelumnya", ctx.chatId());


                              }
                              super.closeDBConnection();


                            //  silent.send("start", ctx.chatId());
                        }

                     })
                    .build();
      }
  
      public Ability setRemote() {
      // arg 1 = tanggal
      // arg 2 = alasan
        return Ability
                .builder()
                .name("remote")
                .info("Set status menjadi remote")
                .input(0)
                .locality(USER)
                .privacy(PUBLIC)
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
                            History history = Tables.HISTORY.findFirst(
                                    "user_id = ? AND tanggal = ? AND status = ?",
                                    user.get("id"),
                                    newDate,
                                    REMOTE);

                            if (history == null) {
                                History record = new History();
                                record.set("user_id", user.get("id"))
                                        .set("status", REMOTE)
                                        .set("tanggal", newDate)
                                        .set("reason", alasan);
                                record.saveIt();
                            } else {
                                silent.send("Kamu sudah mengajukan " + REMOTE + " di tanggal " + tanggal, ctx.chatId());
                            }
                        }

                        int[] chatIds = getSupervisorChatId(username);
                        for(int id : chatIds) {
                          if(id != -1) {
                            silent.send("Halo, kaka!" + "\n"
                                        + "@" + username + " " + REMOTE + " pada tanggal "
                                        + tanggal + " karena "
                                        + alasan, id);
                          }
                        }



                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
    }

    //blm
    public Ability setCuti() {
      // arg 1 = tanggal
        return Ability
                .builder()
                .name("cuti")
                .info("Set status menjadi cuti")
                .input(2)
                .locality(USER)
                .privacy(PUBLIC)
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
                            History history = Tables.HISTORY.findFirst(
                                    "user_id = ? AND tanggal = ? AND status = ?",
                                    user.get("id"),
                                    newDateMulai,
                                    CUTI);

                            if (history == null) {
                                History record = new History();
                                record.set("user_id", user.get("id"))
                                        .set("status", CUTI)
                                        .set("tanggal", newDateMulai);
                                record.saveIt();
                            } else {
                                silent.send("Kamu sudah mengajukan " + CUTI + " di tanggal " + newDateMulai, ctx.chatId());
                            }
                        }

                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
    }

  //start user from
      public Ability ConfigurationSupervisor1()  {


          return Ability
                    .builder()
                    .name("addsupervisor1")
                    .info("says hello world!")
                    .input(0)
                    .privacy(PUBLIC)
                    .locality(ALL)
                    .action(ctx ->       {
                    //    String rst = "";
                        if(ctx.arguments().length == 1 ){

                              super.openDBConnection();


                              User supervisor = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));
                              if(supervisor == null){
                                silent.send("Username "+ ctx.firstArg()+" belum menambahkan bot ini, mohon informasikan dia untuk menambahkan bot ini sebelum ditambahkan untuk notifikasi kamu", ctx.chatId());
                              }
                              else{
                                  User user = Tables.USER.findFirst("username = ?", ctx.user().username());


                                  user.set("supervisor1_id", supervisor.get("id"));
                        //          user.set("chat_id",  ctx.chatId());
                            //    user.set("dob", "1935-12-06");
                                  user.saveIt();


                              //    silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());
                                  silent.send("Supervisor 1 kamu yakni @"+ ctx.firstArg()+" berhasil ditambahkan, silahkan tulis /addsupervisor2 untuk menambahkan supervisor 2", ctx.chatId());
                                //close db
                              }

                              super.closeDBConnection();


                            //  silent.send("start", ctx.chatId());
                        }
                        else{
                          silent.send("Salah komentar, seharusnya  /addsupervisor1 [@username]", ctx.chatId());

                        }

                     })
                    .build();
      }
  
    //blm
    public Ability setSakit() {
      // arg 1 = tanggal
        return Ability
                .builder()
                .name("sakit")
                .info("Set status menjadi sakit")
                .input(1)
                .locality(USER)
                .privacy(PUBLIC)
                .action(ctx -> {
                    String tanggal = ctx.firstArg();
                    if (dateIsValid(tanggal)) {
                        java.sql.Date newDate = changeDateFormat(tanggal);

                        openDBConnection();

                        String username = ctx.user().username();
                        User user = Tables.USER.findFirst("username = ?", username);

                        if (user != null) {
                            History history = Tables.HISTORY.findFirst(
                                    "user_id = ? AND tanggal = ? AND status = ?",
                                    user.get("id"),
                                    newDate,
                                    SAKIT);

                            if (history == null) {
                                History record = new History();
                                record.set("user_id", user.get("id"))
                                        .set("status", SAKIT)
                                        .set("tanggal", newDate);
                                record.saveIt();
                            } else {
                                silent.send("Kamu sudah mengajukan " + SAKIT + " di tanggal " + tanggal, ctx.chatId());
                            }
                        }

                        closeDBConnection();
                    } else {
                        // salah tanggal
                        silent.send("Format tanggal salah", ctx.chatId());
                    }
                })
                .build();
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

                            
      public Ability ConfigurationSupervisor2()  {


          return Ability
                    .builder()
                    .name("addsupervisor2")
                    .info("says hello world!")
                    .input(0)
                    .privacy(PUBLIC)
                    .locality(ALL)
                    .action(ctx ->       {
                    //    String rst = "";
                        if(ctx.arguments().length == 1 ){
                              super.openDBConnection();


                              User supervisor = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));
                              if(supervisor == null){
                                silent.send("Username "+ ctx.firstArg()+" belum menambahkan bot ini, mohon informasikan dia untuk menambahkan bot ini sebelum ditambahkan untuk notifikasi kamu", ctx.chatId());
                              }
                              else{
                                  User user = Tables.USER.findFirst("username = ?", ctx.user().username());


                                  user.set("supervisor2_id", supervisor.get("id"));
                        //          user.set("chat_id",  ctx.chatId());
                            //    user.set("dob", "1935-12-06");
                                  user.saveIt();


                              //    silent.send("Hallo selamat menambahkan Bot!", ctx.chatId());
                                  silent.send("Supervisor 2 kamu yakni @"+ ctx.firstArg()+" berhasil ditambahkan, silahkan tulis /addsupervisor1 untuk menambahkan supervisor 2", ctx.chatId());
                                //close db
                              }

                              super.closeDBConnection();


                            //  silent.send("start", ctx.chatId());
                        }
                        else{
                          silent.send("Salah komentar, seharusnya  /addsupervisor1 [@username]", ctx.chatId());

                        }

                     })
                    .build();
      }

      public Ability historyByUsername()  {
        return Ability
          .builder()
          .name("historyByUsername")
          .info("Lihat semua riwayat status dari seorang user")
          .input(1)
          .locality(ALL)
          .privacy(PUBLIC)
          .action(ctx ->       {
            String arg = ctx.firstArg();

              super.openDBConnection();
              User user = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));

              if(user != null){

                  int userID = Integer.parseInt(user.get("id").toString());
                  List<History> history = Tables.HISTORY.where("user_id = ?", userID);

                  if(history.size() > 0){
                      String finalString = "Hai kaka, berikut riwayat status si " + arg + " : \n";
                    for(int i = 0; i < history.size(); i++) {
                      String tanggal = history.get(i).get("tanggal").toString();
                      String status = history.get(i).get("status").toString();
                      Object alasanObj = history.get(i).get("reason");
                      String alasan = "";
                      if(alasanObj != null){
                        alasan = history.get(i).get("reason").toString();
                      }
                      finalString += "tanggal : " + tanggal + ", status : "+ status + ", alasan : " + alasan +"\n";
                    }
                    silent.send(finalString, ctx.chatId());
                  }
                  else{
                    silent.send("History apapun belum ada", ctx.chatId());

                  }
            } else{
              silent.send("Username " + arg + " tidak terdaftar", ctx.chatId());
            }
            super.closeDBConnection();
          }).build();
      }

      public Ability notifySupervisor()  {
        return Ability
          .builder()
          .name("testnotifysupervisor")
          .info("test feature")
          .input(0)
          .locality(ALL)
          .privacy(PUBLIC)
          .action(ctx ->       {
            super.openDBConnection();
            User user = Tables.USER.findFirst("username = ?", ctx.user().username());
            // System.out.println("USERNAME= ");

            if(user != null) {
              String strId1 = "";
              String strId2 = "";
              try {
                strId1 = user.get("supervisor1_id").toString();
              } catch (NullPointerException e) {
                //TODO: handle exception
              }

              try {
                strId2 = user.get("supervisor2_id").toString();
              } catch (NullPointerException e) {
                //TODO: handle exception
              }
              int supervisor1Id = -1;
              int supervisor2Id = -1;

              if(strId1 != "") {
                supervisor1Id = Integer.parseInt(strId1);
              }
              if(strId2 != "") {
                supervisor2Id = Integer.parseInt(strId2);
              }
              
              int[] supervisorIds = {supervisor1Id, supervisor2Id};
              int sentStatus = 0;
              for(int supervisorId : supervisorIds) {
                if(supervisorId != -1) {
                  User supervisor = Tables.USER.findFirst("id = ?", supervisorId);

                  if(supervisor != null) {
                    String strChatId = "";
                    try {
                      strChatId = supervisor.get("chat_id").toString();;  
                    } catch (Exception e) {
                      //TODO: handle exception
                    }
                    
                    int chatId = -1;
                    if(strChatId != "") {
                      chatId = Integer.parseInt(strChatId);
                    }
                    if(chatId != -1) {
                      silent.send("you've been notified because your position as supervisor for @" + ctx.user().username(), chatId);
                      sentStatus += 1;
                    } else {
                      silent.send("chatId not found", ctx.chatId());
                    }
                  } 
                }
                if(sentStatus == 0) {
                  silent.send("you haven't registered any supervisor yet", ctx.chatId());
                }
              }  
            } else {
              silent.send("your username haven't recorded yet", ctx.chatId());
            }
          }).build();
          }

          
      public Ability historyByTanggal()  {
        return Ability
          .builder()
          .name("historyByTanggal")
          .info("Lihat semua riwayat status pada tanggal tertentu")
          .input(1)
          .locality(ALL)
          .privacy(PUBLIC)
          .action(ctx ->       {
            try {
              String arg = ctx.firstArg();
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
              LocalDate date = LocalDate.parse(arg, formatter);
              super.openDBConnection();
              List<History> history = Tables.HISTORY.where("tanggal = ?", date);
              String finalString = "";
              if (history.size() > 0) {
                finalString = "Hai kaka, berikut riwayat status pada tanggal " + arg + " : \n";

                for(int i = 0; i < history.size(); i++) {
                  int id = Integer.parseInt(history.get(i).get("user_id").toString());
                  String username = Tables.USER.findFirst("id = ?", id).get("username").toString();
                  String status = history.get(i).get("status").toString();
                  String alasan = "";
                  try {
                    alasan = history.get(i).get("reason").toString();
                  } catch (NullPointerException e) {

                  }
                  finalString += "username : @" + username + ", status : " + status + ", alasan : " + alasan +"\n";
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

      public Ability historyByUsernameAndTanggal()  {
        return Ability
          .builder()
          .name("history")
          .info("Lihat semua riwayat status pada tanggal dan username tertentu")
          .input(2)
          .locality(ALL)
          .privacy(PUBLIC)
          .action(ctx ->       {
            try {
              super.openDBConnection();
              String arg1 = ctx.firstArg();
              String arg2 = ctx.secondArg();

              System.out.println(arg1 + " " + arg2);
              System.out.println(ctx.firstArg().substring(1));

              User user = Tables.USER.findFirst("username = ?", ctx.firstArg().substring(1));
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);
              LocalDate date = LocalDate.parse(arg2, formatter);
              
              if (user != null) {
                int userID = Integer.parseInt(user.get("id").toString());
                List<History> history = Tables.HISTORY.where("tanggal = ? AND user_id = ?", date, userID);
                String finalString = "";
                if (history.size() > 0) {
                  finalString = "Hai kaka, berikut riwayat status si " + arg1 + " pada tanggal " + arg2 + " : \n";

                  for(int i = 0; i < history.size(); i++) {
                    int id = Integer.parseInt(history.get(i).get("user_id").toString());
                    String status = history.get(i).get("status").toString();
                    String alasan = "";
                    try {
                      alasan = history.get(i).get("reason").toString();
                    } catch (NullPointerException e) {

                    }
                    finalString += "status : " + status + ", alasan : " + alasan +"\n";
                  }
                } else {
                  finalString = "Hai kaka, pada tanggal " + arg2 + " username " + arg1 + " masuk nih";
                }
                silent.send(finalString, ctx.chatId());
              } else {
                silent.send("Username " + arg1 + " tidak terdaftar", ctx.chatId());
              }
              
            } catch (DateTimeParseException e) {
              silent.send("Hai kaka, format tanggal salah", ctx.chatId());
            }
            super.closeDBConnection();
          }).build();
      }
    


    public int[] getSupervisorChatId(String username)  {
      int[] supervisorChatIds = {-1,-1};
      User user = Tables.USER.findFirst("username = ?", username);

      if(user != null) {
        String strId1 = "";
        String strId2 = "";
        try {
          strId1 = user.get("supervisor1_id").toString();
        } catch (NullPointerException e) {
          //TODO: handle exception
        }

        try {
          strId2 = user.get("supervisor2_id").toString();
        } catch (NullPointerException e) {
          //TODO: handle exception
        }
        int supervisor1Id = -1;
        int supervisor2Id = -1;

        if(strId1 != "") {
          supervisor1Id = Integer.parseInt(strId1);
        }
        if(strId2 != "") {
          supervisor2Id = Integer.parseInt(strId2);
        }
        
        int[] supervisorIds = {supervisor1Id, supervisor2Id};
        int chatIdsIdx = 0;
        for(int supervisorId : supervisorIds) {
          if(supervisorId != -1) {
            User supervisor = Tables.USER.findFirst("id = ?", supervisorId);

            if(supervisor != null) {
              String strChatId = "";
              try {
                strChatId = supervisor.get("chat_id").toString();;  
              } catch (Exception e) {
                //TODO: handle exception
              }
              
              int chatId = -1;
              if(strChatId != "") {
                chatId = Integer.parseInt(strChatId);
              }
              supervisorChatIds[chatIdsIdx] = chatId;
              chatIdsIdx ++;
            } 
          }
        }  
      } 
      return supervisorChatIds;
    }
  }
    