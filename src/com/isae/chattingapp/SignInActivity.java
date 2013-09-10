package com.isae.chattingapp;

import org.jivesoftware.smack.XMPPConnection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.EditText;

public class SignInActivity extends Activity {
	
	private XMPPConnection XmppConnectionSignIn;
	SharedPreferences sharedPref;
	AlertClass alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in);
		alert = new AlertClass(this);
		XmppConnectionSignIn = XMPPLogic.getInstance().getConnection();
	}
	
	public void SignIn(View view){
		String username = ((EditText) findViewById(R.id.usernameIn)).getText().toString();
		String password = ((EditText) findViewById(R.id.passwordIn)).getText().toString();
		
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
		
		sharedPref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("username", username);
		editor.putString("password", password);
		editor.commit();
		finish();
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
