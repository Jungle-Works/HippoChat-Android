package faye;

import org.json.JSONObject;

/**
 * Created by gurmail on 25/06/19.
 *
 * @author gurmail
 */
public interface ConnectionError {
    void onError(JSONObject data);
}
