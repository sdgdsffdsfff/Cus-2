package com.suning.cus.event;

import com.suning.cus.bean.BackupFavData;
import com.suning.cus.json.JsonBackupFavorite;

import java.util.List;

/**
 * Created by 15010551 on 2015/4/18.
 */
public class GetFavEvent {

    public JsonBackupFavorite jsonBackupFavorite;

    public GetFavEvent(JsonBackupFavorite jsonBackupFavorite) {
        this.jsonBackupFavorite = jsonBackupFavorite;
    }

}
