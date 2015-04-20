package com.application.material.bookmarkswallet.app.dbAdapter_old.sample;

import android.util.Log;
import com.application.material.bookmarkswallet.app.models.Bookmark;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by davide on 27/01/15.
 */
public class HTTPDbConnector {
    private static String TAG = "HTTPDbConnector";

    public void HTTPDbConnector() {

    }

    //TODO change this to JSON fetch data
    public static String fetchDataFromDb(int choicedDB) {
        try {
            //check if db is right
            if(choicedDB != Utils.LINKS_DB &&
                    choicedDB != Utils.USERS_DB) {
                Log.e("fetchDataFromDb_TAG", "NO DB FOUND - u must define the right database name");
                return null;
            }

            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("choicedDB",""+choicedDB));
            postParameters.add(new BasicNameValuePair("selectAllRowFromDB",""+ Utils.SELECT_ALL_ROW_FROM_DB));
            if(choicedDB== Utils.LINKS_DB) {
                //get my userId to fetch all liks I stored before

//     			if(userObj!=null)
//     				userIdTMP=userObj.getUserId();
                //TODO remove this userIdTmp
                int userIdTMP=1;
                postParameters.add(new BasicNameValuePair("userId",""+userIdTMP));
            }

//            return CustomHttpClient.executeHttpPost(SharedData.DBUrl, postParameters).toString();
        }catch (Exception e) {
            Log.e("fetchDataFromDb_TAG","Error in http connection!!" + e.toString());
        }
        return null;
    }

    /**GET LINKS LIST**/
    public static ArrayList<Bookmark> getLinksListFromJSONData(){
        //TODO set this fx visible only if user is logged in
        //TODO TEST values cahnge or rm
        boolean isDeletedLink=false;
        String delIconPathDb="";

        try{
            ArrayList<Bookmark> linksObjList=new ArrayList<Bookmark>();
            String JSONdata = fetchDataFromDb(Utils.LINKS_DB);
            if(JSONdata==null)
                return null;
            JSONArray jArray = new JSONArray(JSONdata);
            for(int i=0;i<jArray.length();i++){
                //get links data
                JSONObject json_data = jArray.getJSONObject(i);
//                Bookmark bookmarkObj =new Bookmark(json_data.getInt("link_id"),
//                        json_data.getString("iconPath"),
//                        null,
//                        json_data.getString("linkName"),
//                        json_data.getString("linkUrl"),
//                        json_data.getInt("links_user_id"),
//                        json_data.getInt("links_user_id"));
//                linksObjList.add(bookmarkObj);
                //get LINK icon from URL
//	            try{
//	            	URL linkURLObj=new URL(linkUrlDb);
//		            infoURL=linkURLObj.getUserInfo();
//	            }catch(Exception e){
//	            	Log.v("URL_TAG","error - "+e);
//				}

//	    		Log.d(TAG+"- getLinksListFromJSONData","id: "+linkObj.getLinkId()+
//	    				", iconPath: "+linkObj.linkIconPath+
//                        ", linkUrl: "+linkObj.getLinkUrl()+
//                        ", userId: "+linkObj.getLinkId()+
//                        ", linkName: "+linkObj.getLinkName()
//	    				);
            }
            return linksObjList;
        }catch(JSONException e){
            Log.e(TAG+"- getLinksListFromJSONData", "Error parsing data "+e.toString());
        }
        return null;
    }

    /**INSERT URL INTO DB**/
    //TODO change this to JSON fetch data
    public static boolean insertUrlEntryOnDb(int choicedDB,String urlString){
        if(Utils.isUserLoggedIn()){
            try{
                if(choicedDB!= Utils.LINKS_DB && choicedDB!= Utils.USERS_DB)
                    Log.e(TAG, "NO DB FOUND - u must define the right database name");

                //add choicedDB params
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("insertUrlOnDb",""+ Utils.INSERT_URL_ON_DB));
                postParameters.add(new BasicNameValuePair("choicedDB",""+choicedDB));

                if(choicedDB== Utils.LINKS_DB){
                    //get my userId to fetch all liks I stored before
                    int userIdTMP= Utils.getUserId();
                    postParameters.add(new BasicNameValuePair("userId",""+userIdTMP));

                    //get url to be stored
                    if(urlString!=null)
                        postParameters.add(new BasicNameValuePair("linkUrl",""+urlString));

                    String linkNameTMP= Utils.getLinkNameByUrl(urlString);
                    if(linkNameTMP!=null)
                        postParameters.add(new BasicNameValuePair("linkName",""+linkNameTMP));
                }

//                String response = CustomHttpClient.executeHttpPost(SharedData.DBUrl,postParameters);
                return true;

            }catch (Exception e) {
                Log.e("insertUrlEntryOnDb_TAG","Error in http connectionx!!" + e.toString());
                return false;
            }
        }
        return false;
    }

    /**DELETE ENTRY FROM DB**/
    //TODO change this to JSON fetch data
    public static boolean deleteUrlEntryFromDb(int choicedDB,int linkId){
        if(Utils.isUserLoggedIn()){
            try{
                //check if db is right
                if(choicedDB!= Utils.LINKS_DB && choicedDB!= Utils.USERS_DB)
                    Log.e(TAG, "NO DB FOUND - u must define the right database name");

                //add choicedDB params
                ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("deleteUrlFromDb",""+ Utils.DELETE_URL_FROM_DB));
                postParameters.add(new BasicNameValuePair("choicedDB",""+choicedDB));

                //get my userId to fetch all liks I stored before
                int userIdTMP= Utils.getUserId();
                if(userIdTMP== Utils.EMPTY_USERID)
                    return false;
                postParameters.add(new BasicNameValuePair("links_user_id",""+userIdTMP));

                //check linkId!=null
                if(linkId== Utils.EMPTY_LINKID)
                    return false;
                postParameters.add(new BasicNameValuePair("linkId",""+linkId));
//                String response = CustomHttpClient.executeHttpPost(SharedData.DBUrl,postParameters);

                Log.d(TAG,""+linkId);
                return true;
            }catch (Exception e) {
                Log.e(TAG+"- deleteUrlEntryFromDb_TAG","Error in http connection!!" + e.toString());
                return false;
            }
        }
        return false;
    }

    /**DELETE ENTRY FROM DB**/
    //TODO change this to JSON fetch data
    public static boolean updateLinkOnDb(int choicedDB,Bookmark bookmarkObj){
        if(Utils.isUserLoggedIn()){
            try{
                Log.e(TAG, "Update - still to be implemented on server side");
//		  		//check if db is right
//		  		if(choicedDB!=SharedData.LINKS_DB && choicedDB!=SharedData.USERS_DB)
//		  			Log.e(TAG, "NO DB FOUND - u must define the right database name");
//
//		  		//add choicedDB params
//		  		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
//		  		postParameters.add(new BasicNameValuePair("deleteUrlFromDb",""+SharedData.DELETE_URL_FROM_DB));
//		  		postParameters.add(new BasicNameValuePair("choicedDB",""+choicedDB));
//
//	 			//get my userId to fetch all liks I stored before
//	 			int userIdTMP=SharedData.getUser().getUserId();
//	 			if(userIdTMP==SharedData.EMPTY_USERID)
//	 				return false;
// 				postParameters.add(new BasicNameValuePair("links_user_id",""+userIdTMP));
//
//	 			//check linkId!=null
//	 			if(linkId==SharedData.EMPTY_LINKID)
//	 				return false;
// 				postParameters.add(new BasicNameValuePair("linkId",""+linkId));
//
//		  		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//		  	    StrictMode.setThreadPolicy(policy);
//
//		   		String response = CustomHttpClient.executeHttpPost(SharedData.DBUrl,postParameters);
//
//	 			Log.d(TAG,""+linkId);
//		   		Log.d(TAG,"this is the result" + response);

                return true;
            }catch (Exception e) {
                Log.e(TAG+"- deleteUrlEntryFromDb_TAG","Error in http connection!!" + e.toString());
                return false;
            }
        }
        return false;
    }

    public static class DataParser {
        private static String TAG = "DataParser";

        public void DataParser() {

        }

        public static int usersParserJSONData(String usernameTypedIn,String passwordTypedIn){
            try{
                JSONArray jArray = new JSONArray(fetchDataFromDb(Utils.USERS_DB));
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    int userIdTmp = json_data.getInt("user_id");
                    String usernameTmp = json_data.getString("username");
                    String passwordTmp = json_data.getString("password");

//	    		Log.i("usersParserJSONData_TAG","id: "+userIdDb+
//                        ", usrname: "+usernameDb+
//                        ", pswd: "+passwordDb
//	    				);
                    if(usernameTmp.compareTo(usernameTypedIn)==0 && passwordTmp.compareTo(passwordTypedIn)==0) {
                        return userIdTmp;
                    }
                    return userIdTmp;
                }
            }catch(JSONException e){
                Log.e(TAG+"- usersParserJSONData_TAG", "Error parsing data "+e.toString());
            }
            return Utils.USER_LOGIN_FAILED;
        }

    }

}
