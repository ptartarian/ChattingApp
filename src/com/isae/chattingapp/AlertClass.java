package com.isae.chattingapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertClass{
	AlertDialog.Builder builder;
	AlertDialog alert;
	
	public AlertClass(Context ctx) {
		
		builder = new AlertDialog.Builder(ctx);
		builder.setTitle("Notification");
		builder.setCancelable(false);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int id) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}
		});
	}

	//------------------------------------------------------------------------------------------
    public void display_alert(String P_message){
    	builder.setMessage(P_message);
    	alert = builder.create();
    	alert.show();
    }
}
