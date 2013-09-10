package com.isae.chattingapp;

import java.util.Collection;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Login extends AsyncTask<Void,Void,XMPPConnection > {
	
	private Context LoginContext;
	private XMPPConnection ConnectionResult;
    
    CallbackConnect callback;
    private static String USERNAME;
    private static String PASSWORD;
    private ProgressDialog dialog;
    
    public Login(XMPPConnection conn, CallbackConnect callback, Context ctx, String username, String password){
    	this.callback = callback;
    	this.LoginContext = ctx;
    	this.USERNAME = username;
    	this.PASSWORD = password;
    	this.ConnectionResult = conn;
    	
    }
    
    @Override
    protected void onPreExecute() {
    	dialog = new ProgressDialog(LoginContext);
    	dialog.setMessage("Logging...");
        dialog.setTitle("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }	  

	@Override
	protected XMPPConnection doInBackground(Void... params) {
		try {
   		 ConnectionResult.login(USERNAME, PASSWORD);
	          Log.i("XMPPChatDemoActivity",  "Logged in as" + ConnectionResult.getUser());
	
	          // Set the status to available
	          Presence presence = new Presence(Presence.Type.available);
	          ConnectionResult.sendPacket(presence);
	
	          Roster roster = ConnectionResult.getRoster();
	          Collection<RosterEntry> entries = roster.getEntries();
	          for (RosterEntry entry : entries) {
	
	            Log.d("XMPPChatDemoActivity",  "--------------------------------------");
	            Log.d("XMPPChatDemoActivity", "RosterEntry " + entry);
	            Log.d("XMPPChatDemoActivity", "User: " + entry.getUser());
	            Log.d("XMPPChatDemoActivity", "Name: " + entry.getName());
	            Log.d("XMPPChatDemoActivity", "Status: " + entry.getStatus());
	            Log.d("XMPPChatDemoActivity", "Type: " + entry.getType());
	            Presence entryPresence = roster.getPresence(entry.getUser());
	
	            Log.d("XMPPChatDemoActivity", "Presence Status: "+ entryPresence.getStatus());
	            Log.d("XMPPChatDemoActivity", "Presence Type: " + entryPresence.getType());
	
	            Presence.Type type = entryPresence.getType();
	            if (type == Presence.Type.available)
	              Log.d("XMPPChatDemoActivity", "Presence AVAILABLE");
	              Log.d("XMPPChatDemoActivity", "Presence : " + entryPresence);
	            }
	          } catch (XMPPException ex) {
	          Log.e("XMPPChatDemoActivity", "Failed to log in as "+  USERNAME);
              Log.e("XMPPChatDemoActivity", ex.toString());
			}
		return ConnectionResult;
	}
	
	@Override
	protected void onPostExecute(XMPPConnection ConnectionResult) {
		dialog.dismiss();
		callback.run(ConnectionResult);
	}    

}
