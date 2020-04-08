/*
 *  This Source Code Form is subject to the terms of the Mozilla Public
 *  * License, v. 2.0. If a copy of the MPL was not distributed with this
 *  * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package com.kent.university.privelt.model;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private String name;

    private List<String> permissions;

    public Application(String name) {
        this.name = name;
        this.permissions = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void addPermission(String permission) { permissions.add(permission); }
}
