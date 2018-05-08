package com.mycompany.app;

import com.google.common.annotations.VisibleForTesting;
import org.glassfish.hk2.api.Visibility;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;

import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;

import java.util.Map;
import java.util.function.Predicate;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import static org.telegram.abilitybots.api.objects.Flag.*;
import static org.telegram.abilitybots.api.objects.Locality.ALL;
import static org.telegram.abilitybots.api.objects.Locality.USER;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;
import org.javalite.activejdbc.Base;

import com.mycompany.app.Tables.*;


import java.sql.*;

public class HelloBot extends BaseBot {
  private Statement stmt ;


  public HelloBot() throws Exception {
    super();
  //  stmt = super.ConnecttoDB();
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
      //untuk percobaan query db jadi tulis /start <argument apapun>
      public Ability TestingDb()  {


          return Ability
                    .builder()
                    .name("start")
                    .info("says hello world!")
                    .input(0)
                    .locality(USER)
                    .privacy(ADMIN)
                    .action(ctx ->       {
                    //    String rst = "";
                        if(ctx.arguments().length == 0 ){

                              silent.send("start", ctx.chatId());
                        }
                        else{
                                //open db connection (it is a must before a query)
                                super.openDBConnection();
                                User user = Tables.USER.findFirst("id = ?", 1);
                                silent.send(" "+ user.get("username"), ctx.chatId());
                                //close db
                                super.closeDBConnection();
                        }

                     })
                    .build();
      }

      public Ability historyByUsername()  {
        return Ability
          .builder()
          .name("history_by_username")
          .info("Lihat semua riwayat status dari seorang user")
          .input(1)
          .locality(ALL)
          .privacy(PUBLIC)
          .action(ctx ->       {
            String arg = ctx.firstArg();
            try{
              super.openDBConnection();
              User user = Tables.USER.findFirst("username = ?", arg);
              int userID = Integer.parseInt(user.get("id").toString());
              List<History> history = Tables.HISTORY.where("id = ?", userID);
              String finalString = "Hai kaka, berikut riwayat status si " + arg + " : \n";
              for(int i = 0; i < history.size(); i++) {
                String tanggal = history.get(i).get("tanggal").toString();
                String status = history.get(i).get("status").toString();
                String alasan = history.get(i).get("reason").toString();
                finalString += "tanggal : " + tanggal + ", "+ status + " dengan alasan " + alasan +"\n";
              }
              silent.send(finalString, ctx.chatId());
              //close db
              super.closeDBConnection();
            } catch(Exception e){
              silent.send("Username " + arg + " tidak terdaftar", ctx.chatId());
            }
          }).build();
      }
}
