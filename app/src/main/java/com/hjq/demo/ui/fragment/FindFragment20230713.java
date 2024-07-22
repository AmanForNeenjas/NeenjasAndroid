package com.hjq.demo.ui.fragment;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.hjq.base.BaseActivity;
import com.hjq.base.BaseDialog;
import com.hjq.demo.R;
import com.hjq.demo.aop.SingleClick;
import com.hjq.demo.app.TitleBarFragment;
import com.hjq.demo.http.api.AddUserGroupApi;
import com.hjq.demo.http.api.AdminDelGroupUserApi;
import com.hjq.demo.http.api.AdminRemoveGroupApi;
import com.hjq.demo.http.api.GetUserGroupApi;
import com.hjq.demo.http.api.SetUserGroupApi;
import com.hjq.demo.http.api.UserInfoApi;
import com.hjq.demo.http.api.UserRemoveGroupApi;
import com.hjq.demo.http.model.GroupID;
import com.hjq.demo.http.model.GroupUser;
import com.hjq.demo.http.model.HttpData;
import com.hjq.demo.http.model.UserInfo;
import com.hjq.demo.ui.activity.AddGroupUserActivity;
import com.hjq.demo.ui.activity.HomeActivity;
import com.hjq.demo.ui.adapter.GroupUserAdapter;
import com.hjq.demo.ui.dialog.InputDialog;
import com.hjq.demo.ui.dialog.MessageDialog;
import com.hjq.demo.util.MMKVUtils;
import com.hjq.http.EasyHttp;
import com.hjq.http.EasyLog;
import com.hjq.http.listener.HttpCallback;
import com.hjq.widget.layout.RoundBottomSettingBar;
import com.hjq.widget.layout.RoundTopSettingBar;
import com.tencent.mmkv.MMKV;

import java.util.List;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/AndroidProject
 *    time   : 2018/10/18
 *    desc   : 创建家庭，家庭内用户都可以查看设备，但不能删除设备
 */
public final class FindFragment20230713 extends TitleBarFragment<HomeActivity>
         {

    private TextView mAddNewGroupView;
    private RoundTopSettingBar mGroupNameView;
    private RoundBottomSettingBar mAddGroupUserView;
    private ListView mListView;
    private GroupUserAdapter mAdapter;
    private TextView mUserTitleView;

    private GroupID group;
    private List<GroupUser> items;
    private View mRoundTopViw;
    private View mRoundBottomView;
    private UserInfo userInfo;

    public static FindFragment20230713 newInstance() {
        return new FindFragment20230713();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.group_fragment;
    }

    @Override
    protected void initView() {
        mAddNewGroupView= findViewById(R.id.tv_add_new_group);
        mGroupNameView = findViewById(R.id.sb_group_name);
        mAddGroupUserView = findViewById(R.id.sb_add_group_user);
        mListView = findViewById(R.id.lv_group_user);
        mUserTitleView = findViewById(R.id.tv_user_title);
        mRoundTopViw = findViewById(R.id.iv_goup_user_top);
        mRoundBottomView = findViewById(R.id.iv_goup_user_bottom);
        setOnClickListener(mAddNewGroupView,mAddGroupUserView,mGroupNameView);
        mAdapter = new GroupUserAdapter();
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        // EasyLog.print("========================position：" + position);
                        if(position == 0){
                            // 只有管理员一人 ，则可以删除组
                            if(items.size() == 1 ){
                                // 提示是否删除组
                                new MessageDialog.Builder(getActivity())
                                        // 标题可以不用填写
                                        .setTitle(R.string.group_del)
                                        // 内容必须要填写
                                        .setMessage(R.string.group_del_tip)
                                        // 确定按钮文本
                                        .setConfirm(getString(R.string.common_confirm))
                                        // 设置 null 表示不显示取消按钮
                                        .setCancel(getString(R.string.common_cancel))
                                        // 设置点击按钮后不关闭对话框
                                        //.setAutoDismiss(false)
                                        .setListener(new MessageDialog.OnListener() {

                                            @Override
                                            public void onConfirm(BaseDialog dialog) {
                                                // 删除组
                                                DelGroup();

                                            }

                                            @Override
                                            public void onCancel(BaseDialog dialog) {


                                            }
                                        })
                                        .show();
                            }else{
                                GroupUser tempitem = items.get(position);
                                if(tempitem.isIsadmin()){
                                    // 提示管理员不能删除
                                    toast(R.string.admin_user_group_del_error);

                                }else if( userInfo.getUid() == group.getAdminuid()){
                                    // 管理员可以删除其他用户
                                    // 提示是否删除组
                                    new MessageDialog.Builder(getActivity())
                                            // 标题可以不用填写
                                            .setTitle(R.string.group_user_del)
                                            // 内容必须要填写
                                            .setMessage(R.string.group_user_del_tip)
                                            // 确定按钮文本
                                            .setConfirm(getString(R.string.common_confirm))
                                            // 设置 null 表示不显示取消按钮
                                            .setCancel(getString(R.string.common_cancel))
                                            // 设置点击按钮后不关闭对话框
                                            //.setAutoDismiss(false)
                                            .setListener(new MessageDialog.OnListener() {

                                                @Override
                                                public void onConfirm(BaseDialog dialog) {
                                                    // 删除组
                                                    DelGroupUserByAdmin(tempitem.getUid()+"", position);

                                                }

                                                @Override
                                                public void onCancel(BaseDialog dialog) {


                                                }
                                            })
                                            .show();

                                }else if(userInfo.getUid() == tempitem.getUid()){
                                    // 组成员可以退出组
                                    // 管理员可以删除其他用户
                                    // 提示是否删除组
                                    new MessageDialog.Builder(getActivity())
                                            // 标题可以不用填写
                                            .setTitle(R.string.group_user_del_me)
                                            // 内容必须要填写
                                            .setMessage(R.string.group_user_del_me_tip)
                                            // 确定按钮文本
                                            .setConfirm(getString(R.string.common_confirm))
                                            // 设置 null 表示不显示取消按钮
                                            .setCancel(getString(R.string.common_cancel))
                                            // 设置点击按钮后不关闭对话框
                                            //.setAutoDismiss(false)
                                            .setListener(new MessageDialog.OnListener() {

                                                @Override
                                                public void onConfirm(BaseDialog dialog) {
                                                    // 用户自己退出组
                                                    DelGroupUserByUser();

                                                }

                                                @Override
                                                public void onCancel(BaseDialog dialog) {


                                                }
                                            })
                                            .show();
                                }
                            }
                        }

                        else{
                            GroupUser tempitem = items.get(position);
                            if(tempitem.isIsadmin()){
                                // 提示管理员不能删除
                                toast(R.string.admin_user_group_del_error);

                            }else if( userInfo.getUid() == group.getAdminuid()){
                                // 管理员可以删除其他用户
                                // 提示是否删除组
                                new MessageDialog.Builder(getActivity())
                                        // 标题可以不用填写
                                        .setTitle(R.string.group_user_del)
                                        // 内容必须要填写
                                        .setMessage(R.string.group_user_del_tip)
                                        // 确定按钮文本
                                        .setConfirm(getString(R.string.common_confirm))
                                        // 设置 null 表示不显示取消按钮
                                        .setCancel(getString(R.string.common_cancel))
                                        // 设置点击按钮后不关闭对话框
                                        //.setAutoDismiss(false)
                                        .setListener(new MessageDialog.OnListener() {

                                            @Override
                                            public void onConfirm(BaseDialog dialog) {
                                                // 删除组
                                                DelGroupUserByAdmin(tempitem.getUid()+"", position);

                                            }

                                            @Override
                                            public void onCancel(BaseDialog dialog) {


                                            }
                                        })
                                        .show();

                            }else if(userInfo.getUid() == tempitem.getUid()){
                                // 组成员可以退出组
                                // 管理员可以删除其他用户
                                // 提示是否删除组
                                new MessageDialog.Builder(getActivity())
                                        // 标题可以不用填写
                                        .setTitle(R.string.group_user_del_me)
                                        // 内容必须要填写
                                        .setMessage(R.string.group_user_del_me_tip)
                                        // 确定按钮文本
                                        .setConfirm(getString(R.string.common_confirm))
                                        // 设置 null 表示不显示取消按钮
                                        .setCancel(getString(R.string.common_cancel))
                                        // 设置点击按钮后不关闭对话框
                                        //.setAutoDismiss(false)
                                        .setListener(new MessageDialog.OnListener() {

                                            @Override
                                            public void onConfirm(BaseDialog dialog) {
                                                // 用户自己退出组
                                                DelGroupUserByUser();

                                            }

                                            @Override
                                            public void onCancel(BaseDialog dialog) {


                                            }
                                        })
                                        .show();
                            }
                        }
                    }
                }
        );

    }

    @Override
    protected void initData() {
        //showGroup();
        /*
        // 显示圆形的 ImageView
        GlideApp.with(this)
                .load(R.drawable.update_app_top_bg)
                .transform(new MultiTransformation<>(new CenterCrop(), new CircleCrop()))
                .into(mCircleView);

        // 显示圆角的 ImageView
        GlideApp.with(this)
                .load(R.drawable.update_app_top_bg)
                .transform(new MultiTransformation<>(new CenterCrop(), new RoundedCorners((int) getResources().getDimension(R.dimen.dp_10))))
                .into(mCornerView);*/
        userInfo = getUserInfo();

    }

    @Override
    public void onResume() {
        super.onResume();
        showGroupV2();
    }

    private void showGroup(){

        userInfo = ((HomeActivity)getActivity()).getUserInfo();
        if(userInfo == null){
            userInfo = new UserInfo();
            userInfo.setLogined(false);
            userInfo.setGroudid(0);
            userInfo.setUid(0);
        }
        if(userInfo.isLogined()){

            getUserInfoNet();
        }else{
            mGroupNameView.setVisibility(View.GONE);
            mAddGroupUserView.setVisibility(View.GONE);
            mRoundBottomView.setVisibility(View.GONE);
            mRoundTopViw.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
            mUserTitleView.setVisibility(View.GONE);
        }

    }
    private void showGroupV2(){
        mGroupNameView.setVisibility(View.GONE);
        mAddGroupUserView.setVisibility(View.GONE);
        mRoundBottomView.setVisibility(View.GONE);
        mRoundTopViw.setVisibility(View.GONE);
        mListView.setVisibility(View.GONE);
        mUserTitleView.setVisibility(View.GONE);
        getUserInfoNetV2();
    }
    private void readNetData(){
        // 查询组及组用户数据
        EasyHttp.post(this)
                .api(new GetUserGroupApi())
                .request(new HttpCallback<HttpData<GetUserGroupApi.GroupBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<GetUserGroupApi.GroupBean> data) {
                        if(data.getCode() == 200){
                            group = data.getData().group;
                            items = data.getData().groupus;
                            ((HomeActivity)getActivity()).setLocalGroup(group);
                            ((HomeActivity)getActivity()).setLocalGroupUserList(items);

                           setShow();
                        }else{
                            readLocalData();
                        }

                    }

                    @Override
                    public void onFail(Exception e) {
                        readLocalData();
                    }
                });

    }
    private void readLocalData(){
        // 从本地读取数据


        group = ((HomeActivity)getActivity()).getLocalGroup();
        items = ((HomeActivity)getActivity()).getLocalGroupUserList();
        setShow();
        //mGroupNameView.setLeftText(group.getName());
        //mAdapter.setData(items);
    }
    private void setViewGone(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGroupNameView.setVisibility(View.GONE);
                mAddGroupUserView.setVisibility(View.GONE);
                mRoundBottomView.setVisibility(View.GONE);
                mRoundTopViw.setVisibility(View.GONE);
                mListView.setVisibility(View.GONE);
                mUserTitleView.setVisibility(View.GONE);
            }});

    }


    @SingleClick
    @Override
    public void onClick(View view) {
        if (view == mAddNewGroupView) {
            // 输入对话框
            new InputDialog.Builder(getContext())
                    // 标题可以不用填写
                    .setTitle(R.string.setting_newgroupname)
                    // 内容可以不用填写
                    .setContent("")
                    // 提示可以不用填写
                    .setHint(R.string.set_groupname_tip)
                    // 确定按钮文本
                    .setConfirm(getString(R.string.common_confirm))
                    // 设置 null 表示不显示取消按钮
                    .setCancel(getString(R.string.common_cancel))
                    // 设置点击按钮后不关闭对话框
                    //.setAutoDismiss(false)
                    .setListener(new InputDialog.OnListener() {

                        @Override
                        public void onConfirm(BaseDialog dialog, String content) {
                            if(content.isEmpty()){
                                toast(R.string.set_content_tip);
                            }else{
                                group =new GroupID();
                                group.setName(content);
                                AddGroup();
                            }

                        }

                        @Override
                        public void onCancel(BaseDialog dialog) {
                            toast(R.string.common_cancel);
                        }
                    })
                    .show();

        } else if(view == mGroupNameView){
            if(group!=null){
                // 输入对话框
                new InputDialog.Builder(getContext())
                        // 标题可以不用填写
                        .setTitle(R.string.setting_groupname)
                        // 内容可以不用填写
                        .setContent(group.getName())
                        // 提示可以不用填写
                        .setHint(R.string.set_groupname_tip)
                        // 确定按钮文本
                        .setConfirm(getString(R.string.common_confirm))
                        // 设置 null 表示不显示取消按钮
                        .setCancel(getString(R.string.common_cancel))
                        // 设置点击按钮后不关闭对话框
                        //.setAutoDismiss(false)
                        .setListener(new InputDialog.OnListener() {

                            @Override
                            public void onConfirm(BaseDialog dialog, String content) {
                                if(content.isEmpty()){
                                    toast(R.string.set_content_tip);
                                }else{
                                    group.setName(content);
                                    setGroupName();
                                }

                            }

                            @Override
                            public void onCancel(BaseDialog dialog) {
                                toast(R.string.common_cancel);
                            }
                        })
                        .show();
            }

        }else if(view == mAddGroupUserView){
            // 跳到新增成员界面
            //startActivity(AddGroupUserActivity.class);
            AddGroupUserActivity.start((BaseActivity) getContext(), new AddGroupUserActivity.OnAddGuserListener() {
                @Override
                public void onSucceed() {
                    readNetData();
                }
            });
        }
    }



    private void AddGroup() {
        // 添加组
        EasyHttp.post(this)
                .api(new AddUserGroupApi()
                        .setGroupname(group.getName()))
                .request(new HttpCallback<HttpData<GetUserGroupApi.GroupBean>>(this) {

                    @Override
                    public void onSucceed(HttpData<GetUserGroupApi.GroupBean> data) {
                        if(data.getCode() == 200){
                            //toast(R.string.success);
                            group = data.getData().group;
                            items = data.getData().groupus;
                            ((HomeActivity)getActivity()).setLocalGroup(group);
                            ((HomeActivity)getActivity()).setLocalGroupUserList(items);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAddNewGroupView.setVisibility(View.GONE);
                                    mGroupNameView.setVisibility(View.VISIBLE);
                                    mAddGroupUserView.setVisibility(View.VISIBLE);
                                    mListView.setVisibility(View.VISIBLE);
                                    mUserTitleView.setVisibility(View.VISIBLE);
                                    mRoundBottomView.setVisibility(View.VISIBLE);
                                    mRoundTopViw.setVisibility(View.VISIBLE);
                                    mGroupNameView.setLeftText(group.getName());
                                    mAdapter.setData(items);
                                }});

                        }
                    }
                });
    }

    private void DelGroup() {
                 // 删除组
                 EasyHttp.post(this)
                         .api(new AdminRemoveGroupApi()
                                 )
                         .request(new HttpCallback<HttpData<String>>(this) {

                             @Override
                             public void onSucceed(HttpData<String> data) {
                                 if(data.getCode() == 200){
                                     toast(R.string.success);
                                     group = null;
                                     items.clear();
                                     ((HomeActivity)getActivity()).setLocalGroup(group);
                                     ((HomeActivity)getActivity()).setLocalGroupUserList(items);
                                     getActivity().runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             mAddNewGroupView.setVisibility(View.VISIBLE);
                                             mGroupNameView.setVisibility(View.GONE);
                                             mAddGroupUserView.setVisibility(View.GONE);
                                             mRoundBottomView.setVisibility(View.GONE);
                                             mRoundTopViw.setVisibility(View.GONE);
                                             mListView.setVisibility(View.GONE);
                                             mUserTitleView.setVisibility(View.GONE);
                                         }});

                                 }
                             }
                         });
    }

             /**
              * 组管理员删除组成员
              * @param id
              * @param position
              */
             private void DelGroupUserByAdmin(String id, int position) {
                 // 删除组
                 EasyHttp.post(this)
                         .api(new AdminDelGroupUserApi()
                                 .setUserid(id)
                         )
                         .request(new HttpCallback<HttpData<String>>(this) {
                             @Override
                             public void onSucceed(HttpData<String> data) {
                                 if(data.getCode() == 200){
                                     toast(R.string.success);
                                     items.remove(position);
                                     ((HomeActivity)getActivity()).setLocalGroupUserList(items);
                                     getActivity().runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             mAdapter.setData(items);
                                         }});


                                 }
                             }
                         });
             }

             /**
              * 用户删除组成员自己
              */
             private void DelGroupUserByUser() {
                 // 删除组
                 EasyHttp.post(this)
                         .api(new UserRemoveGroupApi()
                         )
                         .request(new HttpCallback<HttpData<String>>(this) {
                             @Override
                             public void onSucceed(HttpData<String> data) {
                                 if(data.getCode() == 200){
                                     toast(R.string.success);
                                     toast(R.string.success);
                                     group = null;
                                     items.clear();
                                     ((HomeActivity)getActivity()).setLocalGroup(group);
                                     ((HomeActivity)getActivity()).setLocalGroupUserList(items);
                                     getActivity().runOnUiThread(new Runnable() {
                                         @Override
                                         public void run() {
                                             mAddNewGroupView.setVisibility(View.VISIBLE);
                                             mGroupNameView.setVisibility(View.GONE);
                                             mAddGroupUserView.setVisibility(View.GONE);
                                             mRoundBottomView.setVisibility(View.GONE);
                                             mRoundTopViw.setVisibility(View.GONE);
                                             mListView.setVisibility(View.GONE);
                                             mUserTitleView.setVisibility(View.GONE);
                                         }});


                                 }
                             }
                         });
             }

    // 调用API修改用户组名
    private void setGroupName(){
        // 修改呢称
        EasyHttp.post(this)
                .api(new SetUserGroupApi()
                        .setGroupname(group.getName()))
                .request(new HttpCallback<HttpData<String>>(this) {

                    @Override
                    public void onSucceed(HttpData<String> data) {
                        if(data.getCode() == 200){
                            toast(R.string.success);
                            ((HomeActivity)getActivity()).setLocalGroup(group);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mGroupNameView.setLeftText(group.getName());
                                }});

                        }
                    }
                });
    }

    @Override
    public boolean isStatusBarEnabled() {
        // 使用沉浸式状态栏
        return !super.isStatusBarEnabled();
    }

             public UserInfo getUserInfo(){
                 MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                 return mMmkv.decodeParcelable(MMKVUtils.USERINFO, UserInfo.class);
             }

             private void getUserInfoNet(){
                 // 刷新用户信息
                 EasyHttp.post(this)
                         .api(new UserInfoApi())
                         .request(new HttpCallback<HttpData<UserInfo>>(this) {

                             @Override
                             public void onSucceed(HttpData<UserInfo> data) {
                                 if(data.getCode() == 200){
                                     MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                     mMmkv.encode(MMKVUtils.USERINFO, data.getData());
                                     reFresh(data.getData());
                                 }else{
                                     toast(data.getMessage());
                                 }
                             }
                             @Override
                             public void onFail(Exception e) {
                                 super.onFail(e);
                                 toast(R.string.user_info_error);
                             }
                         });
             }
             private void getUserInfoNetV2(){
                 // 刷新用户信息
                 EasyHttp.post(this)
                         .api(new UserInfoApi())
                         .request(new HttpCallback<HttpData<UserInfo>>(this) {

                             @Override
                             public void onSucceed(HttpData<UserInfo> data) {
                                 if(data.getCode() == 200){
                                     MMKV mMmkv = MMKV.mmkvWithID(MMKVUtils.CACHID,MMKV.MULTI_PROCESS_MODE);
                                     mMmkv.encode(MMKVUtils.USERINFO, data.getData());
                                     reFresh(data.getData());
                                 }else{
                                     //toast(data.getMessage());
                                 }
                             }
                             @Override
                             public void onFail(Exception e) {
                                 super.onFail(e);
                                 //toast(R.string.user_info_error);
                                 if(userInfo == null){
                                     setViewGone();
                                 }else{
                                     if(userInfo.getGroudid() !=0){
                                        readLocalData();
                                     }else{
                                         setViewGone();
                                     }
                                 }
                             }
                         });
             }

             private void reFresh(UserInfo userInfo){
                 if(userInfo.getGroudid() ==0){

                     setViewGone();
                 }else {
                     getActivity().runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             mAddNewGroupView.setVisibility(View.GONE);
                         }});

                     // 从网络读取数据
                     //if(((HomeActivity)getActivity()).getNetworkStatus()){
                     readNetData();
                     //} else{
                         // 从本地读数据
                         //readLocalData();

                     //}
                 }
             }
             private void setShow(){
                 getActivity().runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         mAddNewGroupView.setVisibility(View.GONE);
                         mGroupNameView.setVisibility(View.VISIBLE);
                         mAddGroupUserView.setVisibility(View.VISIBLE);
                         mListView.setVisibility(View.VISIBLE);
                         mUserTitleView.setVisibility(View.VISIBLE);
                         mRoundBottomView.setVisibility(View.VISIBLE);
                         mRoundTopViw.setVisibility(View.VISIBLE);
                         if(group != null){
                             mGroupNameView.setLeftText(group.getName());
                         }
                         if(items != null){
                             mAdapter.setData(items);
                         }


                     }});
             }


         }