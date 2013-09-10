package com.isae.chattingapp;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class ConnectServer extends AsyncTask<Void,Void,XMPPConnection > {
    
    private static final String HOST = "192.168.0.103";
    private static final int PORT = 5222;
    private XMPPConnection ConnectionResult;
    private ConnectionConfiguration connConfig;
    private ProgressDialog dialog;
    private Context ConnectServerContext;
    
    CallbackConnect callback;
    
    public ConnectServer(CallbackConnect callback, Context ctx){
    	this.callback = callback;
    	this.ConnectServerContext = ctx;
    	connConfig = new ConnectionConfiguration(HOST, PORT);
    	ConnectionResult = new XMPPConnection(connConfig);
    }   
    @Override
    protected void onPreExecute() {
    	dialog = new ProgressDialog(ConnectServerContext);
    	dialog.setMessage("Connecting to server...");
        dialog.setTitle("Please wait...");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
    }	  

@Override
protected XMPPConnection doInBackground(Void... params) {	 
     try {
    	 ConnectionResult.connect();
       Log.i("XMPPChatDemoActivity",  "[SettingsDialog] Connected to "+ConnectionResult.getHost());
     } catch (Exception ex) {
         Log.e("XMPPChatDemoActivity",  "[SettingsDialog] Failed to connect to "+ ConnectionResult.getHost());
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