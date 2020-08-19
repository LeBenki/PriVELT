package com.kent.university.privelt.utils.hat;

import com.kent.university.privelt.ui.settings.SettingsActivity;
import com.privelt.pda.dataplatform.hat.HatClient;
import com.privelt.pda.dataplatform.hat.HatPlatform;
import com.privelt.pda.dataplatform.hat.response.HatAuthenticationResponse;
import com.privelt.pda.util.SubjectObserver;

public class HatController extends DataPlatformController<HatPlatform> {

    public HatController() {
        super();
    }

    public HatController(HatPlatform dataPlatform) {
        super(dataPlatform);
    }

    public void authenticationActionAsync(HatClient hClient, SettingsActivity activity) {
        hClient.addAuthenticationListener(new SubjectObserver<HatAuthenticationResponse>() {

            @Override
            public void processSuccess(HatAuthenticationResponse hatAuthenticationResponse) {
                // Update UI:
                activity.runOnUiThread(() -> {
                    activity.setHatAuthenticationResult(hatAuthenticationResponse);
                });
            }

            @Override
            public void processFailed(String message) {
                activity.runOnUiThread(() -> {
                    activity.showMessage(message);
                });
            }
        });

        hClient.authenticate();
    }
}
