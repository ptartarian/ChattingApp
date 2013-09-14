package com.isae.chattingapp;

import org.jivesoftware.smack.XMPPConnection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends Activity {
	
	private XMPPConnection XmppConnectionSignIn;
	SharedPreferences sharedPref;
	AlertClass alert;
	Context SignInContext;
	String username;
	String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		alert = new AlertClass(this);
		SignInContext = this;
		XmppConnectionSignIn = XMPPLogic.getInstance().getConnection();
	}
	
	public void SignIn(View view){
		username = ((EditText) findViewById(R.id.usernameIn)).getText().toString();
		password = ((EditText) findViewById(R.id.passwordIn)).getText().toString();
		
		if (username.length() == 0 || password.length() == 0){
			alert.display_alert("Tous les champs doivent être saisis.");
			return;
		}

//		AccountManager am = new AccountManager(XmppConnectionSignIn);
//		//Verifying if the username does not exists
//		if (am.getAccountAttribute(username) == null) {
//			alert.display_alert("l'utilisateur "+username+" est inconnu");
//			return;
//		}
		
		Login l = new Login(XmppConnectionSignIn, new  CallbackConnect() {public void run(XMPPConnection result){
			XmppConnectionSignIn = result;
			if (XmppConnectionSignIn.getUser() == null) {
				alert.builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
			          @Override
			          public void onClick(DialogInterface dialog, int whichButton){
			        	  XmppConnectionSignIn.disconnect();
			  	    	  XMPPLogic.getInstance().setConnection(null);
			        	  finish();
			          }
			      });
			  	  alert.display_alert("Utilisateur "+username+" inconnu");
	  			return;
	  		}else{
	  			sharedPref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
	  			SharedPreferences.Editor editor = sharedPref.edit();
	  			editor.putString("username", username);
	  			editor.putString("password", password);
	  			editor.commit();
	  			XMPPLogic.getInstance().setConnection(XmppConnectionSignIn);
	  			Intent main = new Intent(SignInContext, ChattingApp.class);
	  	    	startActivity(main);
	  			finish();
	  		}
		}}, SignInContext, username, password);
		l.execute();
	}	
	
	public void GoToSignUp(View view){
		Intent signup = new Intent(this,SignUpActivity.class);
    	startActivity(signup);
//		XmppConnectionParameter conn = new XmppConnectionParameter(XmppConnectionSignIn);
//		Intent signup = new Intent(this, SignUpActivity.class);
//		signup.putExtra("ConnectionParam", conn);
//    	startActivityForResult(signup, 0);
//    	finish();
	}
}
