package faye;

/**
 * Created by gurmail on 20/03/19.
 *
 * @author gurmail
 */
public interface FayeCallClientListener {

    public void onConnectedServer(FayeClient fc);
    public void onDisconnectedServer(FayeClient fc);
    public void onReceivedMessage(FayeClient fc, String msg, String channel);
    public void onPongReceived();
    public void onWebSocketError();
    public void onErrorReceived(FayeClient fc, String msg, String channel);
}
