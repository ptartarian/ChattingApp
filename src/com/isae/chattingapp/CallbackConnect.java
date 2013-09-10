package com.isae.chattingapp;

import org.jivesoftware.smack.XMPPConnection;

// this method is used to be able to retrieve the result of the AsyncTask,which is ConnectionResult, while being
//able to view the progress dialog. If one uses the get() method the progress dialog won't appear.
public interface CallbackConnect {

  void run(XMPPConnection result);
}
