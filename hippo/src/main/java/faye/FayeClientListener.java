package faye;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */


public interface FayeClientListener {

    public void onConnectedServer(BaseSocketClient fc);
    public void onDisconnectedServer(BaseSocketClient fc);
    public void onReceivedMessage(BaseSocketClient fc, String msg, String channel);
    public void onPongReceived();
    public void onWebSocketError();
    public void onErrorReceived(BaseSocketClient fc, String msg, String channel);
    public void onNotConnected();
    public void onSubscriptionError();
}
