package com.kol.jumhz.common.net;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * @ClassName: JWebSocketClient
 * @Description: 通信
 * @Author: Lanlnk
 * @CreateDate: 2020/4/10 20:52
 */
public class JWebSocketClient extends WebSocketClient {
    protected JWebSocketClient(URI serverUri) {
        super(serverUri, new Draft_6455());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.e("JWebSocketClient", "onOpen()");
    }

    @Override
    public void onMessage(String message) {
        // bundle=new Bundle();
        //bundle.putString("info",message);
        //Message mag=new Message();
        //mag.setData(bundle);
        //handler.sendMessage(mag);
        Log.e("JWebSocketClient", "onMessage()"+message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.e("JWebSocketClient", "onClose()");
    }

    @Override
    public void onError(Exception ex) {
        Log.e("JWebSocketClient", "onError():"+ex);
    }
}
