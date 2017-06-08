package cn.ucai.live.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ucai.live.I;
import cn.ucai.live.LiveHelper;
import cn.ucai.live.R;
import cn.ucai.live.data.restapi.LiveException;
import cn.ucai.live.data.restapi.LiveManager;
import cn.ucai.live.utils.L;
import cn.ucai.live.utils.MD5;

import static cn.ucai.live.I.REQUESTCODE_CUTTING;
import static cn.ucai.live.I.REQUESTCODE_PICK;

public class RegisterActivity extends BaseActivity {

    @BindView(R.id.email)
    EditText userNameEditText;
    @BindView(R.id.password)
    EditText passwordEditText;
    @BindView(R.id.register)
    Button register;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nick)
    EditText mEtNickname;
    @BindView(R.id.password_confirm)
    EditText confirmPwdEditText;
    @BindView(R.id.iv_avatar)
    ImageView avatar;
    ProgressDialog pd;
    File file = null;
    String avatarName,username,nickname,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showDialog(){
        pd = new ProgressDialog(RegisterActivity.this);
        pd.setMessage("正在注册...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();
    }

    private void dismissDialog(){
        if (pd!=null && pd.isShowing()){
            pd.dismiss();
        }
    }

    @OnClick(R.id.register)
    public void onRegisterClick(){
        showDialog();
        if (checkInput()){
            registerAPPServer();
        }else{
            dismissDialog();
        }
    }

    private boolean checkInput() {
        username = userNameEditText.getText().toString().trim();
        nickname = mEtNickname.getText().toString().trim();
        password = passwordEditText.getText().toString().trim();
        String confirm_pwd = confirmPwdEditText.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            userNameEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(nickname)) {
            Toast.makeText(this, getResources().getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
            mEtNickname.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            passwordEditText.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Confirm_password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            confirmPwdEditText.requestFocus();
            return false;
        } else if (!password.equals(confirm_pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Two_input_password), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public void registerAPPServer(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (LiveManager.getInstance().register(username,nickname,
                            MD5.getMessageDigest(password),file)){
                        registerEMServer();
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissDialog();
                                showLongToast("注册失败");
                            }
                        });
                    }
                } catch (final LiveException e) {
                    try {
                        LiveManager.getInstance().unRegister(username);
                    } catch (LiveException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dismissDialog();
                            showLongToast("注册失败:" + e.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    public void registerEMServer(){
        try {
            EMClient.getInstance().createAccount(username, MD5.getMessageDigest(password));
            LiveHelper.getInstance().setCurrentUserName(username);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    showToast("注册成功");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                }
            });
        } catch (final HyphenateException e) {
            e.printStackTrace();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dismissDialog();
                    showLongToast("注册失败：" + e.getMessage());
                }
            });
        }
    }

    @OnClick(R.id.layout_avatar)
    public void onAvatarClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dl_title_upload_photo);
        builder.setItems(new String[]{getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload)},
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        switch (which) {
                            case 0:
                                Toast.makeText(RegisterActivity.this, getString(R.string.toast_no_support),
                                        Toast.LENGTH_SHORT).show();
                                break;
                            case 1:
                                Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
                                pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                                startActivityForResult(pickIntent, REQUESTCODE_PICK);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.create().show();
    }

    public void startPhotoZoom(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        intent.putExtra("noFaceDetection", true);
        startActivityForResult(intent, REQUESTCODE_CUTTING);
    }

    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            Drawable drawable = new BitmapDrawable(getResources(), photo);
            avatar.setImageDrawable(drawable);
//            uploadUserAvatar(Bitmap2Bytes(photo));
            saveBitmapFile(photo);
        }

    }
    public static String getAvatarPath(Context context, String path){
        File dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File folder = new File(dir,path);
        if(!folder.exists()){
            folder.mkdir();
        }
        return folder.getAbsolutePath();
    }

    private void saveBitmapFile(Bitmap bitmap) {
        if (bitmap != null) {
            String imagePath = getAvatarPath(RegisterActivity.this, I.AVATAR_TYPE)+"/"+getAvatarName()+".jpg";
            file = new File(imagePath);//将要保存图片的路径
            L.e("file path="+file.getAbsolutePath());
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getAvatarName() {
        avatarName = String.valueOf(System.currentTimeMillis());
        return avatarName;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUESTCODE_PICK:
                if (data == null || data.getData() == null) {
                    return;
                }
                startPhotoZoom(data.getData());
                break;
            case REQUESTCODE_CUTTING:
                if (data != null) {
                    setPicToView(data);
                }
                break;
            default:
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
