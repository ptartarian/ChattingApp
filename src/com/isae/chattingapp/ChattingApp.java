package com.isae.chattingapp;

import java.util.ArrayList;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ChattingApp extends Activity {

  public static String USERNAME;
  public static String PASSWORD;

  public XMPPConnection MainXmppConnection;
  private ArrayList<String> messages = new ArrayList<String>();
  private Handler mHandler = new Handler();

  private EditText recipient;
  
  private EditText textMessage;
  private ListView listview;
  
  private SharedPreferences sharedPref;
  
  Context ChattAppContext;
  Activity ChattAppActivity;
  
  AlertClass alert;
  
  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ChattAppContext = this;
    ChattAppActivity = this;
    alert = new AlertClass(this);
    Navigate();
  }
  
  @Override
  public void onRestart() {
  	super.onRestart();
//  	sharedPref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
//    USERNAME = sharedPref.getString("username", "");
//    
//    // If we're coming from the sign in activity and the user did not sign in then we should 
//    // exit the application. Or if we are not connected then exit.
//    if (!MainXmppConnection.isConnected()){
//    	GetOutOfHere("Incapable de se connecter au serveur");
//    }
//    
//    if (USERNAME.length() == 0) {
//    	GetOutOfHere("Incapable de se s'inscrire ");
//    } 
//    Navigate();
  }
//----------------------------------------------------------------------------------------------------------
  
  private void Navigate(){
	  MainXmppConnection = XMPPLogic.getInstance().getConnection();
	  if (!MainXmppConnection.isConnected()){
		  ConnectServer c = new ConnectServer(new  CallbackConnect() {public void run(XMPPConnection result){
		  // in the onPostExecute in ConnectServer class we call "callback.run(ConnectionResult);"
		  // meaning that the below code will be executed once the connection thread is done. 
		  MainXmppConnection = result;
	  	  if (!MainXmppConnection.isConnected()) {
	  		GetOutOfHere("Unable to connect to server");
	  		return;
	  	  }
	  		  
	  	  sharedPref = getSharedPreferences("CREDENTIALS", Context.MODE_PRIVATE);
	  	  USERNAME = sharedPref.getString("username", "");
	  	  PASSWORD = sharedPref.getString("password", "");
	  		  
	  	if (USERNAME.length() == 0){ 		
	  		XMPPLogic.getInstance().setConnection(MainXmppConnection);
	  		Intent signin = new Intent(ChattAppContext,SignInActivity.class);
	    	startActivity(signin);
	    	finish();
	  	}else{
	  		Login l = new Login(MainXmppConnection, new  CallbackConnect() {public void run(XMPPConnection result){
	  		// if could not login then exit.
	  		if (!MainXmppConnection.isConnected()) {
	  			GetOutOfHere("Unable to login with username and password");
	  		}
	  		
	  		MainXmppConnection = result;
	  		setContentView(R.layout.main);
		    recipient = (EditText) ChattAppActivity.findViewById(R.id.toET);
		    textMessage = (EditText) ChattAppActivity.findViewById(R.id.chatET);
		    listview = (ListView) ChattAppActivity.findViewById(R.id.listMessages);
		    setListAdapter();
		   // Set a listener to send a chat text message
		    Button send = (Button) ChattAppActivity.findViewById(R.id.sendBtn);
		    send.setOnClickListener(new View.OnClickListener() {
		      public void onClick(View view) {
		        String to = recipient.getText().toString();
		        String text = textMessage.getText().toString();          
		        Log.i("XMPPChatDemoActivity ", "Sending text " + text + " to " + to);
		        Message msg = new Message(to, Message.Type.chat);  
		        msg.setBody(text);
		        if (MainXmppConnection != null) {
		          MainXmppConnection.sendPacket(msg);
		          messages.add(MainXmppConnection.getUser() + ":");
		          messages.add(text);
		          setListAdapter();
		        }
		      }
		      });
	  		}}
	  		, ChattAppContext, USERNAME, PASSWORD);
	  		
	  		l.execute();
	  	  }
	  	  }},ChattAppContext);
	  	  c.execute();
	  }	  
  }
  

  /**
   * Called by Settings dialog when a connection is establised with 
   * the XMPP server
   */
  
  public void addGetMessagesListener(XMPPConnection connection){
	// Add a packet listener to get messages sent to us
	  PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
      connection.addPacketListener(new PacketListener() {
        @Override
        public void processPacket(Packet packet) {
          Message message = (Message) packet;
          if (message.getBody() != null) {
            String fromName = StringUtils.parseBareAddress(message.getFrom());
            Log.i("XMPPChatDemoActivity ", " Text Recieved " + message.getBody() + " from " +  fromName);
            messages.add(fromName + ":");
            messages.add(message.getBody());
            // Add the incoming message to the list view
            mHandler.post(new Runnable() {
              public void run() {
                setListAdapter();
              }
            });
          }
        }
      }, filter);
  }

  private void setListAdapter() {
    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.listitem, messages);
    listview.setAdapter(adapter);
  }
  
  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
	  menu.clear();
	  MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.sign_out, menu);
      return true;
  }
 
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
	  if (item.getItemId() == R.id.signout){
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("username", null);
			editor.putString("password", null);
			editor.commit();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
	  }
      return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      MainXmppConnection.disconnect();
    } catch (Exception e) {

    }
  }
  
  public void GetOutOfHere(String s){
	  alert.builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
          @Override
          public void onClick(DialogInterface dialog, int whichButton){
        	  Intent intent = new Intent(Intent.ACTION_MAIN);
        	  intent.addCategory(Intent.CATEGORY_HOME);
        	  intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	  startActivity(intent);
          }
      });
  	  alert.display_alert(s);
  }
}