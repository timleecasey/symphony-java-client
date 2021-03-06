/*
 *
 * Copyright 2016 The Symphony Software Foundation
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.symphony.clients.model;

import org.symphonyoss.symphony.pod.model.Avatar;
import org.symphonyoss.symphony.pod.model.AvatarList;
import org.symphonyoss.symphony.pod.model.UserV2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by frank.tarsillo on 9/8/2016.
 */
public class SymUser {

    private Long id;

    private String emailAddress;

    private String firstName;

    private String lastName;

    private String displayName;

    private String title;

    private String company;

    private String username;

    private String location;

    private List<SymAvatar> avatars;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public List<SymAvatar> getAvatars() {
        return avatars;
    }

    public void setAvatars(List<SymAvatar> avatars) {
        this.avatars = avatars;
    }

    public static UserV2 toUserV2(SymUser symUser) {
        UserV2 userV2 = new UserV2();


        userV2.setCompany(symUser.getCompany());
        userV2.setDisplayName(symUser.getDisplayName());
        userV2.setEmailAddress(symUser.getEmailAddress());
        userV2.setFirstName(symUser.getFirstName());
        userV2.setLastName(symUser.getLastName());
        userV2.setId(symUser.getId());
        userV2.setLocation(symUser.getLocation());
        userV2.setTitle(symUser.getTitle());
        userV2.setUsername(symUser.getUsername());

        AvatarList avatars = new AvatarList();
        for (SymAvatar symAvatar : symUser.getAvatars()) {
            Avatar avatar = new Avatar();
            avatar.setSize(symAvatar.getSize());
            avatar.setUrl(symAvatar.getUrl());
            avatars.add(avatar);
        }
        userV2.setAvatars(avatars);

        return userV2;
    }


    public static SymUser toSymUser(UserV2 user) {


        SymUser symUser = new SymUser();


        symUser.setCompany(user.getCompany());
        symUser.setDisplayName(user.getDisplayName());
        symUser.setEmailAddress(user.getEmailAddress());
        symUser.setFirstName(user.getFirstName());
        symUser.setLastName(user.getLastName());
        symUser.setId(user.getId());
        symUser.setLocation(user.getLocation());
        symUser.setTitle(user.getTitle());
        symUser.setUsername(user.getUsername());

        List<SymAvatar> avatars = new ArrayList<>();
        for (Avatar avatar : user.getAvatars()) {
            SymAvatar symAvatar = new SymAvatar();
            symAvatar.setSize(avatar.getSize());
            symAvatar.setUrl(avatar.getUrl());
            avatars.add(symAvatar);
        }
        symUser.setAvatars(avatars);


        return symUser;

    }
}
