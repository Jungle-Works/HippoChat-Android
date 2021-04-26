package faye;

/**
 * Created by gurmail on 19/01/19.
 *
 * @author gurmail
 */
public interface FayeServiceListener {

    public void onConnectedServer(FayeClient fc);
    public void onDisconnectedServer(FayeClient fc);
    public void onReceivedMessage(FayeClient fc, String msg, String channel);
    public void onWebSocketError();
    public void onErrorReceived(FayeClient fc, String msg, String channel);
}
