/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.utils.hat;

import com.privelt.pda.dataplatform.databox.DataboxPlatform;
import com.privelt.pda.dataplatform.generic.DataPlatform;
import com.privelt.pda.dataplatform.hat.HatPlatform;

public class AccountManager {

    private static AccountManager instance;
    private DataPlatform dataPlatform;
    private DataPlatformController controller;

    private AccountManager() {
    }

    public static AccountManager getInstance() {
        if (instance == null)
            instance = new AccountManager();
        return instance;
    }

    public DataPlatform getDataPlatform() {
        return dataPlatform;
    }

    public void setDataPlatform(DataPlatform dataPlatform) {
        this.dataPlatform = dataPlatform;

        // Set Controller:
        switch (dataPlatform.getClient().getType()) {
            case DataBox:
                controller = new DataboxController((DataboxPlatform) dataPlatform);
                break;

            case HAT:
                HatController hatController = new HatController((HatPlatform) dataPlatform);
                hatController.getPlatform().getCrud().setEndpoint("priveltcloud/test01");

                controller = hatController;
                break;
        }
    }

    public DataPlatformController getDataPlatformController() {
        return controller;
    }
}
