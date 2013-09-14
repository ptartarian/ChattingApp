package com.isae.chattingapp;

import java.util.ArrayList;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
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

  private XMPPConnection MainXmppConnection;
  private ArrayList<String> messages = new ArrayList<String>();
  private Handler mHandler = new Handler();

  private EditText recipient;
  
  private EditText textMessage;
  private ListView listview;
  
  private SharedPreferences sharedPref;
  
  Context ChattAppContext;
  
  AlertClass alert;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ChattAppContext = this;
    alert = new AlertClass(this);
    MainXmppConnection = XMPPLogic.getInstance().getConnection();
    setContentView(R.layout.main);
    Prepare();
  }

//----------------------------------------------------------------------------------------------------------
  
  private void Prepare(){
	  
	  recipient = (EditText) findViewById(R.id.toET);
	    textMessage = (EditText) findViewById(R.id.chatET);
	    listview = (ListView) findViewById(R.id.listMessages);
	    setListAdapter();
	   // Set a listener to send a chat text message
	    Button send = (Button) findViewById(R.id.sendBtn);
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
	    
	    addGetMessagesListener(MainXmppConnection);
  }
  
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
		  sharedPref = getSharedPreferences("CREDENTIALS",Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putString("username", null);
			editor.putString("password", null);
			editor.commit();
			Presence offlinePres = new Presence(Presence.Type.unavailable, "", 1, Presence.Mode.away);
			XMPPLogic.getInstance().setConnection(MainXmppConnection);
	  		Intent signin = new Intent(ChattAppContext,SignInActivity.class);
	    	startActivity(signin);
	    	finish();
	  }
      return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    try {
      MainXmppConnection.disconnect();
      XMPPLogic.getInstance().setConnection(null);
    } catch (Exception e) {
    }
  }
  
  public void GetOutOfHere(String s){
	  alert.builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
          @Override
          public void onClick(DialogInterface dialog, int whichButton){
        	  MainXmppConnection.disconnect();
  	    	  XMPPLogic.getInstance().setConnection(null);
        	  finish();
          }
      });
  	  alert.display_alert(s);
  }
}