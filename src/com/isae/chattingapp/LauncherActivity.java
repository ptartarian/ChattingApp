package com.isae.chattingapp;

import org.jivesoftware.smack.XMPPConnection;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Window;
import android.view.WindowManager;

public class LauncherActivity extends Activity {
	
	private static String USERNAME;
	private static String PASSWORD;
	  
	private SharedPreferences sharedPref;
	  
	Context LauncherAppContext;
	
	private XMPPConnection LauncherXmppConnection;
	AlertClass alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_launcher);
		
		alert = new AlertClass(this);
		LauncherAppContext = this;
		
		// SLEEP 2 SECONDS HERE ...
	    Handler handler = new Handler(); 
	    handler.postDelayed(new Runnable() { 
	         public void run() { 
	        	 Navigate(); 
	         } 
	    }, 0); 
	}
	
	private void Navigate(){
		ConnectServer c = new ConnectServer(new  CallbackConnect() {public void run(XMPPConnection result){
			  // in the onPostExecute in ConnectServer class we call "callback.run(ConnectionResult);"
			  // meaning that the below code will be executed once the connection thread is done. 
			  LauncherXmppConnection = result;
		  	  if (!LauncherXmppConnection.isConnected()) {
		  		GetOutOfHere("Incapable de se connecter au serveur");
		  		return;
		  	  }
		  		  
		  	  sharedPref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
		  	  USERNAME = sharedPref.getString("username", "");
		  	  PASSWORD = sharedPref.getString("password", "");
		  		  
		  	if (USERNAME.length() == 0){ 		
		  		XMPPLogic.getInstance().setConnection(LauncherXmppConnection);
		  		Intent signin = new Intent(LauncherAppContext,SignInActivity.class);
		    	startActivity(signin);
		    	finish();
		  	}else{
		  		Login l = new Login(LauncherXmppConnection, new  CallbackConnect() {public void run(XMPPConnection result){
		  		LauncherXmppConnection = result;
		  		// if could not login then exit.
		  		if (LauncherXmppConnection.getUser() == null) {
		  			sharedPref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
		  			SharedPreferences.Editor editor = sharedPref.edit();
		  			editor.putString("username", "");
		  			editor.putString("password", "");
		  			editor.commit();
		  			GetOutOfHere("Utilisateur "+USERNAME+" inconnu");
		  			return;
		  		}else{
		  			XMPPLogic.getInstance().setConnection(LauncherXmppConnection);
		  			Intent chatt = new Intent(LauncherAppContext,ChattingApp.class);
			    	startActivity(chatt);
			    	finish();
		  		}
		  		}}
		  		, LauncherAppContext, USERNAME, PASSWORD);
		  		
		  		l.execute();
		  	  }
		  	  }},LauncherAppContext);
		  	  c.execute();
		
	}
	
	public void GetOutOfHere(String s){
		  alert.builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
	          @Override
	          public void onClick(DialogInterface dialog, int whichButton){
	        	  LauncherXmppConnection.disconnect();
	  	    	  XMPPLogic.getInstance().setConnection(null);
	        	  finish();
	          }
	      });
	  	  alert.display_alert(s);
	  }
	

}
