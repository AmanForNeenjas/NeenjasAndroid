package com.hjq.demo.http.api;

import com.hjq.demo.http.model.GroupID;
import com.hjq.demo.http.model.GroupUser;
import com.hjq.http.config.IRequestApi;

import java.util.List;

/**
 * 获得组成及组用户
 */
public final class GetUserGroupApi implements IRequestApi {

    @Override
    public String getApi() {
        return "center/getgroupuser";
    }

    public final static class GroupBean {
        public GroupID group;
        public List<GroupUser> groupus;
    }
}