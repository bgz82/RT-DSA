package com.cisa.service;

import java.net.UnknownHostException;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.types.ObjectId;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import com.cisa.service.pojo.Image;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

@Path("/ocisa")
public class MyResource {

	@GET
	@Produces("text/plain")
	public String getIt() {
		return "Hi Bhargava!";
	}

	@GET
	@Path("No")
	@Produces("text/plain")
	public String getI() {
		return "Hi Gellaboina!";
	}

	@GET
	@Path("events")
	@Produces("text/plain")
	public String getEventsInfo() throws JSONException {
		MongoClient mongo;
		JSONObject spinnerArray;
		// JSONArray sArray;
		String result[] = new String[100];
		String concat = "";
		int i = 0, j = 0;
		try {
			mongo = new MongoClient("lasir.umkc.edu", 27017);
			DB db = mongo.getDB("umkc");
			DBCollection table = db.getCollection("oevents");
			DBCursor cursor = table.find();
			while (cursor.hasNext()) {
				spinnerArray = new JSONObject((cursor.next().toString()));
				result[i] = spinnerArray.get("name").toString();
				i++;
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		for (j = 0; j < i; j++) {
			concat = concat + result[j] + ";";
		}
		return concat;
	}

	@POST
	@Path("analysis")
	@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public String setAnalysisInfo(String data,
			@QueryParam(value = "sceneId") String sceneId,
			@QueryParam(value = "event") String event) {
		JSONArray jArrayRegion;
		JSONObject obj;

		try {
			obj = new JSONObject(data);
			System.out.println(obj.get("deviceId"));
			String deviceId = obj.get("deviceId").toString();
			String analysis = "Yes";
			jArrayRegion = new JSONArray(obj.getString("region"));
			System.out.println(jArrayRegion);

			if (updateAnalysisToMongo(sceneId, deviceId, jArrayRegion, event,
					analysis)) {
				return "200 OK";
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return "405";
	}

	/*
	 * @GET
	 * 
	 * @Path("analysis")
	 * 
	 * @Consumes({ MediaType.APPLICATION_JSON, "text/json" })
	 * 
	 * @Produces({ MediaType.APPLICATION_JSON, "text/json" }) public String
	 * getAnalysisInfo(@QueryParam(value = "sceneId") String sceneId,
	 * 
	 * @QueryParam(value = "event") String event) {
	 * System.out.println("Scene Id: " + sceneId);
	 * 
	 * // System.out.println(getAnalysisFromMongo(sceneId)); return
	 * getAnalysisFromMongo(sceneId, event);
	 * 
	 * }
	 */
	@POST
	@Path("/imagingdata")
	@Consumes({ MediaType.APPLICATION_JSON, "text/json" })
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public String uploadObserverData(Image scene,
			@QueryParam(value = "event") String event) {

		try {
			Mongo mongo = new Mongo("localhost", 27017);
			DBCollection collection = null;
			DB db = mongo.getDB("umkc");
			collection = db.getCollection("event1");
			// convert JSON to DBObject directly
			DBObject dbObject = (DBObject) JSON.parse(new Gson().toJson(scene));

			collection.insert(dbObject);

			DBCursor cursorDoc = collection.find();
			DBCursor c = collection.find();

			while (cursorDoc.hasNext()) {
				System.out.println(cursorDoc.next());
			}
			System.out.println("Done");
			return "0";
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return "1";
		} catch (MongoException e) {
			e.printStackTrace();
			return "1";
		}

	}

	@GET
	@Path("randomitem")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public Response sendRandomItem() {
		JSONObject result = getRandomItem();
		return Response.status(200).entity(result.toString()).build();
	}

	private JSONObject getRandomItem() {

		MongoClient mongo;
		JSONArray jArray = new JSONArray();
		JSONObject json = null;
		try {
			mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("umkc");
			DBCollection table = db.getCollection("cisa");

			long count = table.getCount();

			int limit = 1;
			long skip = Math.round((double) count / limit);

			DBCursor cursor = table.find().skip(
					(int) ((Math.random() * skip) % count));

			json = new JSONObject(cursor.next().toMap());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return json;
	}

	@GET
	@Path("itemslist")
	@Produces({ MediaType.APPLICATION_JSON, "text/json" })
	public Response sendItems(@QueryParam(value = "start") String startStr,
			@QueryParam(value = "limit") String limitStr,
			@QueryParam(value = "event") String event) {
		JsonObject result = new JsonObject();
		int start = Integer.parseInt(startStr);
		int limit = Integer.parseInt(limitStr);
		String eventType = event.toString();
		JSONArray jArray = getItems(start, limit, eventType);

		return Response.status(200).entity(jArray.toString()).build();
	}

	private JSONArray getItems(int start, int limit, String event) {

		MongoClient mongo;
		JSONArray jArray = new JSONArray();
		try {
			DBCollection table = null;
			mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("umkc");
			if (event.equals("Earthquake")) {
				table = db.getCollection("cisa");
			} else if (event.equals("Tornado")) {
				table = db.getCollection("tornado");
			} else if (event.equals("generic")) {
				table = db.getCollection("generic");
			} else if (event.equals("garden")) {
				table = db.getCollection("ggarden");
			} else {
			}
			DBCursor cursor = table.find().skip(start).limit(limit);

			while (cursor.hasNext()) {
				JSONObject json = new JSONObject(cursor.next().toMap());
				jArray.put(json);
			}
			table = null;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return jArray;
	}

	private boolean updateAnalysisToMongo(String sceneId, String deviceId,
			JSONArray jArrayRegion, String event, String analysis) {
		try {
			DBCollection collection = null;
			Mongo mongo = new Mongo("localhost", 27017);
			DB db = mongo.getDB("umkc");
			collection = db.getCollection("event1");

			BasicDBObject allQuery = new BasicDBObject("_id", new ObjectId(
					sceneId));

			DBCursor cursor = collection.find(allQuery);
			DBObject ob = cursor.next();

			/*
			 * while (cursor.hasNext()) { System.out.println(cursor.next()); }
			 */
			/*
			 * BasicDBObject query = new BasicDBObject(); query.put("_id", new
			 * ObjectId(sceneId));
			 * 
			 * BasicDBObject push = new BasicDBObject(); BasicDBObject anaData =
			 * new BasicDBObject(); anaData.append("deviceId", deviceId);
			 * anaData.append("region", jArrayRegion.toString());
			 * push.put("$push", new BasicDBObject("analysis",
			 * anaData.toString()));
			 * 
			 * collection.update(query, push);
			 */

			DBObject findQuery = new BasicDBObject("_id", new ObjectId(sceneId));

			DBObject listItem = new BasicDBObject("results", new BasicDBObject(
					"deviceId", deviceId).append("region",
					jArrayRegion.toString()));
			DBObject updateQuery = new BasicDBObject("$push", listItem);
			DBObject done = new BasicDBObject("Analyzed", "Yes");
			DBObject updateDone = new BasicDBObject("$apush", done);
			collection.update(findQuery, updateQuery);
			collection.update(findQuery, updateDone);

			// getAnalysisFromMongo(sceneId);
			System.out.println("Done");

			return true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		} catch (MongoException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getAnalysisFromMongo(String sceneId, String event) {
		String result = null;
		Map map = null;
		MongoClient mongo;
		JSONArray jArray = new JSONArray();
		try {
			DBCollection collection = null;
			mongo = new MongoClient("localhost", 27017);
			DB db = mongo.getDB("umkc");
			if (event.equals("Earthquake")) {
				collection = db.getCollection("cisa");
			} else if (event.equals("Tornado")) {
				collection = db.getCollection("tornado");
			} else if (event.equals("generic")) {
				collection = db.getCollection("generic");
			} else if (event.equals("garden")) {
				collection = db.getCollection("ggarden");
			} else {
				// Only three collections available
			}
			BasicDBObject allQuery = new BasicDBObject("_id", new ObjectId(
					sceneId));

			DBCursor cursor = collection.find(allQuery);
			while (cursor.hasNext()) {
				map = cursor.next().toMap();
			}

			System.out.println(map.get("results").toString());
			return map.get("results").toString();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return result;
	}

}
