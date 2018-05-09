package com.mycompany.app.jobs;

import java.util.Arrays;

import com.mycompany.app.BaseBot;
import com.mycompany.app.Tables.*;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BroadcastJob implements Job {
	public static final String CHAT_IDS = "CHAT_IDS";
	public static final String REASON = "REASON";
	public static final String END_DATE = "END_DATE";
	public static final String ACTION = "ACTION";
	public static final String USER_ID = "USER_ID";
		public static final String USERNAME = "USERNAME";

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String[] chatIds = (String[]) dataMap.get(CHAT_IDS);
		int userId = dataMap.getInt(USER_ID);

		String baseUrl = "https://api.telegram.org/bot" + BaseBot.validToken();
		String sendMessageUrl = baseUrl + "/sendMessage";
		String getMemberUrl = baseUrl + "/getChatMember";

		OkHttpClient client = new OkHttpClient();
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");

		System.out.println(Arrays.toString(chatIds));
		for (String id : chatIds) {
			RequestBody body = RequestBody.create(JSON, getChatMemberPayload(id, userId));
			Request request = new Request.Builder().url(getMemberUrl).post(body).build();
			try (Response response = client.newCall(request).execute()) {
				System.out.println(response.body());
				System.out.println(response.isSuccessful());
				if (!response.isSuccessful()) {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			body = RequestBody.create(JSON, sendMessagePayload(id, buildMessage(dataMap)));
			request = new Request.Builder().url(sendMessageUrl).post(body).build();

			try (Response response = client.newCall(request).execute()) {
				System.out.println(response.body().string());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private String buildMessage(JobDataMap dataMap) {
		String action = dataMap.getString(ACTION);
		String message = "Teman - teman, hari ini @"+ dataMap.getString(USERNAME) +" izin " + action + " dulu";
		if (action.equals("remote")) {
			message += " karena " + dataMap.getString(REASON)
					+ ". Jika ada masalah yang terkait dengan dirinya, silahkan kontak saya langsung melalui telegram :)";
			;
		} else if (action.equals("cuti")) {
			message += " hingga tanggal " + dataMap.getString(END_DATE) + ".";
		} else {
			message = "Teman - teman, hari ini saya izin tidak masuk karena sedang sakit.";
		}
		return message;
	}

	private String getChatMemberPayload(String id, int userId) {
		return "{" + "\"chat_id\":\"" + id + "\"," + "\"user_id\":\"" + userId + "\"" + "}";
	}

	private String sendMessagePayload(String id, String message) {
		return "{" + "\"chat_id\":\"" + id + "\"," + "\"text\":\"" + message + "\"" + "}";
	}
}
