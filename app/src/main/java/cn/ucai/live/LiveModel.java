package cn.ucai.live;

import com.hyphenate.easeui.model.EasePreferenceManager;

/**
 * Created by clawpo on 2017/6/8.
 */

public class LiveModel {
    public LiveModel() {

    }

    /**
     * save current username
     * @param username
     */
    public void setCurrentUserName(String username){
        EasePreferenceManager.getInstance().setCurrentUserName(username);
    }

    public String getCurrentUsernName(){
        return EasePreferenceManager.getInstance().getCurrentUsername();
    }
}
