package com.mycompany.app.jobs;

import com.mycompany.app.BaseBot;

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
	public static final Object CHAT_IDS = "chat_ids";
	public static final String MESSAGE = "message";

	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		String[] chatIds = (String[]) dataMap.get(CHAT_IDS);
		String message = dataMap.getString(MESSAGE);

		String url = "https://api.telegram.org/bot" + BaseBot.validToken() + "/sendMessage";
		OkHttpClient client = new OkHttpClient();
		MediaType JSON = MediaType.parse("application/json; charset=utf-8");

		for (String id : chatIds) {
			RequestBody body = RequestBody.create(JSON,
					"{" + "\"chat_id\":\"" + id + "\"," + "\"text\":\"" + message + "\"" + "}");
			Request request = new Request.Builder().url(url).post(body).build();

			try (Response response = client.newCall(request).execute()) {
				System.out.println(response.body().string());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}