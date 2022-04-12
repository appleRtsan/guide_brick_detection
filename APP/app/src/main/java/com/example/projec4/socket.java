package com.example.projec4;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class socket extends AsyncTask<Byte, Void, Integer>
{
    Socket S;
    DataOutputStream output;
    PrintWriter pw;
    BufferedReader input;
    String recv="failed";
    public interface AsyncTaskResult<Integer>
    {
        public void taskFinish(Integer result);
    }
    public AsyncTaskResult<Integer> connectionTestResult =null;

    public void setConnectionTestResult(AsyncTaskResult<Integer> connectionTestResult) {
        this.connectionTestResult = connectionTestResult;
    }

    @Override
    protected Integer doInBackground(Byte... packs)
    {

        int D;
        try {
            S = new Socket("172.20.28.106", 8878);
        }catch(IOException e)
        {
            Log.i("Socket create","failed");
            e.printStackTrace();
        }
        try
        {
            byte[] b = new byte[packs.length];

            int j=0;
            // Unboxing Byte values. (Byte[] to byte[])
            for(Byte bb: packs)
                b[j++] = bb.byteValue();


            output = new DataOutputStream(S.getOutputStream());
            pw = new PrintWriter(S.getOutputStream());
            Log.i("Socket sending size", Integer.toString(b.length));

//            output.writeInt(b.length);
            output.write(b, 0, b.length);
            pw.flush();
            pw.close();
            if(S.isClosed()) {
                Log.i("Socket", "closed");
                try {
                    S = new Socket("172.20.28.106", 8878);
                    Log.i("Socket create", "success");
                } catch (IOException e) {
                    Log.i("Socket create", "failed");
                    e.printStackTrace();
                }
            }
            try {
                int len = -1;
                byte[] d ;
                do {
                    DataInputStream input = new DataInputStream(S.getInputStream());
                    len = -1;
                    len = input.available();
                    d = new byte[len];
                    if (len > 0) {
                        input.readFully(d, 0, d.length);
                    }
                }while(len==0);
                Log.i("recv msgl", String.valueOf(len));
                Log.i("recv msglen", String.valueOf(d.length));
                Log.i("recv msg",d.toString());
                byte ex = d[0];
                D= ex;
                Log.i("recv msg", String.valueOf(D-48));//ascii
                return D-48;
            }catch (Exception e)
            {
                e.printStackTrace();
            }


        }
        catch(IOException e){
            e.printStackTrace();
        }

        return -1;
    }
    @Override
    public void onPostExecute( Integer result )
    {
        Log.i("result", String.valueOf(result));
        this.connectionTestResult.taskFinish( result );
    }


}
