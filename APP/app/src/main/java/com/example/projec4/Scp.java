package com.example.projec4;
import android.util.Log;

import java.io.IOException;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.ConnectionInfo;
import ch.ethz.ssh2.SFTPv3Client;

public class Scp {
    private volatile static Scp scpInstance;
    private static final String ACTIVITY_TAG ="LogDemo";
    private String user;
    private String pass;
    private String host;
    private Connection connection;
    private ch.ethz.ssh2.SCPClient scpClient;
    private SFTPv3Client sftPv3Client;
    private Boolean isAuthed;
    private Scp(String user, String pass, String host){
        this.user = user;
        this.pass = pass;
        this.host = host;
    }

    public static Scp getScpUtilsInstance(String user, String pass, String host){

        if(scpInstance == null) {
            synchronized(Scp.class) {
                if(scpInstance == null) {
                    scpInstance = new Scp(user,pass,host);
                }
            }
        }
        return scpInstance;
    }


    public void connect(){
        connection = new Connection(host);
        try {
            connection.connect();
            isAuthed = connection.authenticateWithPassword(user,pass);
            // scp connection
            scpClient = connection.createSCPClient();
        } catch (IOException e) {
            Log.e(ACTIVITY_TAG, Log.getStackTraceString(e));
            e.printStackTrace();
            close();
        }
    }

    public void close(){
        connection.close();
        sftPv3Client.close();
    }

    public boolean getIsAuthed(){
        return isAuthed;
    }

    // copy file to server
    public void putFile(String filePath,String aimPath){
        try {
            if(scpClient != null){
                scpClient.put(filePath,aimPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
