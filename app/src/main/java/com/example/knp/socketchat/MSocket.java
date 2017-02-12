package com.example.knp.socketchat;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Created by knp on 2/8/17.
 */

public class MSocket {

    private static Socket socket;

    public void connect(String fAdr) throws URISyntaxException {
        socket = IO.socket(fAdr);
        socket.connect();
    }

    public Socket getSocket() {
        return socket;
    }

    public static void setSocket(Socket socket) {
        MSocket.socket = socket;
    }
}
