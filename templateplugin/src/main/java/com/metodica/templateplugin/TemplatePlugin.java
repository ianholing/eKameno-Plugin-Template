package com.metodica.templateplugin;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.metodica.nodeplugin.INodePluginV1;

public class TemplatePlugin extends Service {
    // PLUGIN VARIABLES
    static final String LOG_TAG = "TemplatePlugin";
    static final String PLUGIN_NAME = "TEMPLATEPLUGIN";
    static final String ACTION = "com.metodica.templateplugin";
    static final String CATEGORY = "com.metodica.nodeplugin.TEMPLATE_PLUGIN";

    private final INodePluginV1.Stub addBinder = new INodePluginV1.Stub() {

        // Function Name: 	run
        // Description: 	Uses the COMMAND and DATAs configured in "getXMLDefaults" to
        //					launch real actions.
        // Parameters:
        //					String command:
        //					String data1:
        //					String data2:
        // 					String data3
        //					String actionID:
        //
        // Return:			XML String

        public boolean runPlugin(String command, String data1, String data2, String data3, String actionID, int flagsInUse) throws RemoteException {
            // 	flagsInUse EXPLANATION:
            //	00000001	PLUGIN UP (NOT USED HERE)
            //	00000010	PLUGIN WORKING (NOT USED HERE)
            //	00000100	USING AN ACTIVITY
            //	00001000	USING CAMERA HARDWARE
            //	00RR0000	RESERVED FLAGS
            //	XX000000	CUSTOM FLAGS FOR YOUR PLUGINS

            // FIRST set WORKING flag ON
            setFlag(FLAG_WORKING);

            if (command.equalsIgnoreCase("TEMPLATECOMMANDDEMO")) {
                try {
                    // Use Configured Data to reply this action
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("EKAMENOPLUGINCONFIG", Context.MODE_PRIVATE);
                    String backgroundReturnMessage = sp.getString("TEMPLATEREPLY", getString(R.string.DemoReplyString));

                    // ErrorCodes Codification is just like in HTTP protocol
                    // For NOT ERROR Returns use errorCode 200
                    sendReturn(200, "SHOWTEXT", "LOW", backgroundReturnMessage, actionID);

                } catch (Throwable e) {
                    e.printStackTrace();
                    return false;
                }
            } else sendActionToActivity(command, data1, data2, data3, actionID);

            // FREE the plugin if it is not WORKING IN ACTIVITY
            // Unset Work Flag only if you don't launch any activity.. Otherwise do it in the activity
            unsetFlag(FLAG_WORKING);
            return true;
        }



        // Function Name: 	getXMLDefaults
        // Description: 	Each <DEFAULT> will be an action you can use from your eKameno Client
        //					and it needs a <COMMAND> (Which should be the ID of the action to do)
        //					and three <DATA>s which are variables to launch this COMMAND.
        // Parameters:		None
        // Return:			XML String (Take care to close everything you open)

        @Override
        public String getXMLDefaults() throws RemoteException {
            return 	"<DEFAULT>" +
                    "<NAME>" + getString(R.string.Action1) + "</NAME>" +
                    "<COMMAND>TEMPLATECOMMANDDEMO</COMMAND>" +
                    "<DATA1>BACKGROUND_DATA_1</DATA1>" +
                    "<DATA2>BACKGROUND_DATA_2</DATA2>" +
                    "<DATA3></DATA3>" +
                    "</DEFAULT>";
        }



        // Function Name: 	getXMLCustomOptions
        // Description: 	Not working in this version yet.
        // Parameters:		None
        // Return:			XML String

        @Override
        public String getXMLCustomOptions() throws RemoteException {
            return "";
        }



        // Function Name: 	initiate
        // Description: 	Some plugins need to initiate something before executing, this function
        //					executes when the plugin becomes active one only time (if it not crash).
        // Parameters:		None
        // Return:			None

        @Override
        public void initiate() throws RemoteException {
            // DO here whatever the plugin needs to initiate it
            // In TemplatePlugin case there is nothing to do
        }



        // Function Name: 	is_compatible
        // Description: 	Return True if the plugin can be executed in this platform and
        //					False if it is not.
        // Parameters:		None
        // Return:			Boolean

        @Override
        public boolean is_compatible() throws RemoteException {
            // The example works in every platform cause it do nothing
            return true;
        }


        // IF YOUR ACTION SHOULD BE MANAGED INTO AN ACTIVITY USE THIS FUNCION
        private void sendActionToActivity(String command, String data1, String data2, String data3, String actionID) {
            Intent dialogIntent = new Intent(TemplatePlugin.this, TemplateConfigActivity.class);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Log.d(LOG_TAG, actionID);
            dialogIntent.putExtra("ACTIONID", actionID);
            dialogIntent.putExtra("COMMAND", command);
            dialogIntent.putExtra("DATA1", data1);
            dialogIntent.putExtra("DATA2", data2);
            dialogIntent.putExtra("DATA3", data3);

            setFlag(FLAG_ACTIVITY);
            getApplication().startActivity(dialogIntent);
        }



























        //////////////////\\\\\\\\\\\\\\\\\\\
        /////							\\\\\
        /////    - DO NOT TOUCH PART -	\\\\\
        /////	   SYSTEM FUNCTIONS		\\\\\
        /////							\\\\\
        //////////////////\\\\\\\\\\\\\\\\\\\

        // DO NOT CROSS THIS LINE!!															\\
        // --------------------------------------------------------------------------------	\\

        @Override
        public String getPluginShowName() throws RemoteException {
            return getString(R.string.PluginShowName);
        }

        @Override
        public String getPluginName() throws RemoteException {
            return PLUGIN_NAME;
        }

        @Override
        public int getStatusFlag() throws RemoteException {
            return statusFlag;
        }

        @Override
        public boolean run(String command, String data1, String data2, String data3, String actionID, int flagsInUse) throws RemoteException {
            sendReturn(200, "VOID", "MEDIUM", command + " received", actionID);
            return runPlugin(command, data1, data2, data3, actionID, flagsInUse);
        }

        @Override
        public String getResource() throws RemoteException {
            // TODO Auto-generated method stub
            return null;
        }
    };


    static final String PLUGINRESPONSE = "com.metodica.ekamenoserver.PLUGINRESPONSE";

    // 	FLAGS EXPLANATION:
    //	00000001	PLUGIN UP
    //	00000010	PLUGIN WORKING
    //	00000100	USING AN ACTIVITY
    //	00001000	USING CAMERA HARDWARE
    //	00RR0000	RESERVED FLAGS
    //	XX000000	CUSTOM FLAGS FOR YOUR PLUGINS
    public static final int FLAG_LINKED = 1 << 0;
    public static final int FLAG_WORKING = 1 << 1;
    public static final int FLAG_ACTIVITY = 1 << 2;
    public static final int FLAG_CAMERA = 1 << 3;
    public static final int FLAG_RESERVED1 = 1 << 4;
    public static final int FLAG_RESERVED2 = 1 << 5;
    public static final int FLAG_CUSTOM1 = 1 << 6;
    public static final int FLAG_CUSTOM2 = 1 << 7;

    private static int statusFlag;

    public void onStart(Intent intent, int startId) {
        Log.d(LOG_TAG, "onStart()");
        statusFlag = FLAG_LINKED;
        super.onStart( intent, startId );
    }

    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy()");
        super.onDestroy();
    }

    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind()");
        return addBinder;
    }


    //////////////////\\\\\\\\\\\\\\\\\\\
    /////		FLAGS WORKZONE		\\\\\
    //////////////////\\\\\\\\\\\\\\\\\\\

    public static synchronized void setFlag(int newFlag) {
        statusFlag |= newFlag;
    }

    public static synchronized void unsetFlag(int newFlag) {
        statusFlag &= ~newFlag;
    }

    public static synchronized boolean isFlag(int status, int newFlag) {
        return ((status & newFlag) == newFlag);
    }

    //////////////////\\\\\\\\\\\\\\\\\\\
    /////		SEND RETURNS		\\\\\
    //////////////////\\\\\\\\\\\\\\\\\\\

    private void sendReturn(int errorCode, String type, String criticity, String data, String _actionID) {
        // SEND IMAGE AS RETURN AND CLOSE
        Intent i = new Intent(TemplatePlugin.PLUGINRESPONSE);
        Bundle extras = new Bundle();

        if (_actionID != null) extras.putString("ACTIONID", _actionID);
        else extras.putString("ACTIONID", "");

        extras.putInt("ERRORCODE", errorCode);
        extras.putString("TYPE", type);
        extras.putString("DATA", data);
        extras.putString("CRITICITY", criticity);

        i.putExtras(extras);
        sendBroadcast(i);
    }
}
