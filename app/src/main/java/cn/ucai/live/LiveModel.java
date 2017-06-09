package cn.ucai.live;

import com.hyphenate.easeui.model.EasePreferenceManager;

import java.util.Map;

import cn.ucai.live.data.local.LiveDao;
import cn.ucai.live.data.model.Gift;

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

    public Map<Integer,Gift> getGiftList() {
        LiveDao dao = new LiveDao();
        return dao.getGiftList();
    }
}
