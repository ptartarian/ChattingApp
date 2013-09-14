package com.isae.chattingapp;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends Activity {
	
	AlertClass alert;
	private XMPPConnection XmppConnectionSignUp;
	
	SharedPreferences sharedPref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_up);
		alert = new AlertClass(this);
		XmppConnectionSignUp = XMPPLogic.getInstance().getConnection();
	}
	
	public void SignUp(View view){
		String username = ((EditText) findViewById(R.id.usernameUp)).getText().toString();
		String password = ((EditText) findViewById(R.id.passwordUp)).getText().toString();
		String ConfirmPassword = ((EditText) findViewById(R.id.confirmUP)).getText().toString();
		
		if (username.length() == 0 || password.length() == 0 || ConfirmPassword.length() == 0){
			alert.display_alert("Tous les champs doivent être saisis.");
			return;
		}
		
		if (!password.equals( ConfirmPassword )){
			alert.display_alert("Mot de passe et confirmation sont differents");
			return;
		}
		
		AccountManager am = new AccountManager(XmppConnectionSignUp);
		
//		//Verifying if the username already exists
//		if (am.getAccountAttribute(username) != null) {
//			alert.display_alert("l'utilisateur "+username+" deja existant.");
//			return;
//		}
		
//		configure(ProviderManager.getInstance());
//		 UserSearchManager usm = new UserSearchManager(XmppConnectionSignUp);
//		 try {
//			 Form searchForm = usm.getSearchForm("search." + XmppConnectionSignUp.getServiceName());		
//	         Form answerForm = searchForm.createAnswerForm();
//	         answerForm.setAnswer("Username", true);
//	         answerForm.setAnswer("search", username);
//	         
//	         ReportedData data = usm.getSearchResults(answerForm, "search." + XmppConnectionSignUp.getServiceName());
//	         
//	         int count = 0;
//	         while(data.getRows().hasNext()){
//	        	 count++;
//	        	 System.out.println("user Exists :" + count + " times");
//       	 
//        }
//		} catch (XMPPException e) {
//			System.out.println("XMPPException tHrOoWWNnnnn :D");
//			return;
//		}
		 
		
     	Map<String, String> mp = new HashMap<String, String>();
     	mp.put("username", username);
     	mp.put("password", password);
     	mp.put("name", username);
     	mp.put("email", "paul_tartarian@hotmail.com");
     	
     	try {
			am.createAccount(username, password, mp);
			sharedPref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("username", username);
			editor.putString("password", password);
			editor.commit();
			finish();
     	}catch(Exception e){
     		Log.e("SignUpActivity","Error creating account");
     	}
     	  
	}

//	public void configure(ProviderManager pm) {
//		//  User Search
//			pm.addIQProvider("query","jabber:iq:search", new UserSearch.Provider());
//		}

}
