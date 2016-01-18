package com.xy.fy.asynctask;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mc.util.H5Dialog;
import com.mc.util.H5Toast;
import com.mc.util.HttpUtilMc;
import com.xy.fy.asynctask.LoginAsynctask.LoginResult;
import com.xy.fy.main.MainActivity;
import com.xy.fy.main.R;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.method.HideReturnsTransformationMethod;
import android.util.Log;
import android.view.View;

public class GetPicAsynctask extends AsyncTask<Object, String, String> {

  public Activity mActivity;
  private String account, password;
  private ProgressDialog progressDialog;
  private GetPic onResult;

  private boolean isClick = false;

  public interface GetPic {
    void onReturn(String result);
  }

  public GetPicAsynctask(Activity mActivity, String account, String password,
      ProgressDialog progressDialog, GetPicAsynctask.GetPic onResult) {
    // TODO Auto-generated constructor stub
    this.mActivity = mActivity;
    this.account = account;
    this.password = password;
    this.progressDialog = progressDialog;
    this.onResult = onResult;
  }

  @Override
  protected String doInBackground(Object... params) {
    StaticVarUtil.loginTimes++;
    // TODO Auto-generated method stub
    return HttpUtilMc.IsReachIP()
        ? HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL + "GetPic.jsp")
        : HttpUtilMc.CONNECT_EXCEPTION;
  }

  @Override
  protected void onPostExecute(String result) {
    // TODO Auto-generated method stub
    super.onPostExecute(result);
    // progress.cancel();
    try {
      if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
        if (!result.equals("error")) {
          if (!result.equals("ip warning!!!")) {
            JSONObject json = new JSONObject(result);
            final String session = json.getString("cookieSessionID");// session
            String picUrl = json.getString("picurl");
            System.out.println("picUrl:" + picUrl);
            final H5Dialog h5Dialog = new H5Dialog(this.mActivity);
            // sync image.
            GetImageSync getImageSync = new GetImageSync(picUrl, new GetImageSync.syncLintener() {
              @Override
              public void sync(Bitmap bitmap) {
                // TODO Auto-generated method stub
                // dialog.
                // password.setText("");
                if (progressDialog != null) {
                  progressDialog.dismiss();
                }
                if (bitmap == null) {
                  ViewUtil.showToast(mActivity, "程序员哥哥写了个bug,请重试。");
                  return;
                }
                h5Dialog.setMessage("验证码").setBitmap(bitmap)
                    .setPositiveButton(R.string.h5_default_confirm, new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    isClick = true;
                    h5Dialog.dismiss();
                    // password.setText("");
                    if (progressDialog != null) {
                      try {
                        progressDialog.show();
                      } catch (Exception e) {
                        // TODO: handle exception
                      }
                    }
                    if (h5Dialog.getPic().length() != 4) {
                        H5Toast.showToast(mActivity, "验证码错误");
                        if (progressDialog != null) {
                          try {
                            progressDialog.dismiss();
                          } catch (Exception e) {
                            // TODO: handle exception
                          }
                        }
                    } else {
                      request(session, h5Dialog.getPic());
                    }

                  }
                }).setNegativeButton(R.string.h5_default_cancel, new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    h5Dialog.dismiss();
                    // password.setText("");
                    if (progressDialog != null) {
                      progressDialog.dismiss();
                    }
                  }
                }).show();

              }
            });
            getImageSync.execute();
          }
        } else {
          ViewUtil.showToast(mActivity, "服务器维护中。。。");
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          // progressDialog.cancel();
        }
      } else {
        if (StaticVarUtil.loginTimes < 3) {
          GetPicAsynctask getPicAsyntask = new GetPicAsynctask(mActivity, account, password,
              progressDialog, onResult);
          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_REPEAT_EXCEPTION);
          getPicAsyntask.execute();
        } else {
          ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_EXCEPTION);
          if (progressDialog != null) {
            progressDialog.dismiss();
          }
          onResult.onReturn(HttpUtilMc.CONNECT_EXCEPTION);
          // progressDialog.cancel();
        }
      }
    } catch (Exception e) {
      // TODO: handle exception
      Log.i("LoginActivity", e.toString());
      if (progressDialog != null) {
        progressDialog.dismiss();
      }
    }
  }

  private synchronized void request(String session, final String pic) {
    StaticVarUtil.session = session;
    StaticVarUtil.loginTimes = 0;// 将登陆次数置零
    LoginAsynctask loginAsyntask = new LoginAsynctask(mActivity, account, password, pic,
        new LoginResult() {

          @Override
          public void onLogin(String result) {
            // TODO Auto-generated method stub

            try {
              if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                if (result.equals("error")) {
                  ViewUtil.showToast(mActivity, "密码错误");

                  onResult.onReturn("error");
                  // password.setText("");
                  if (progressDialog != null) {
                    progressDialog.dismiss();
                  }
                  // progressDialog.cancel();
                } else if (result.equals("errorCode")) {
                  ViewUtil.showToast(mActivity, "请检查密码和验证码是否正确。");

                  onResult.onReturn("error");
                  // password.setText("");
                  if (progressDialog != null) {
                    progressDialog.dismiss();
                  }
                } else {
                  if (result.equals("no_user")) {
                    ViewUtil.showToast(mActivity, "账号不存在");
                    onResult.onReturn("no_user");
                    if (progressDialog != null) {
                      progressDialog.dismiss();
                    }
                    // progressDialog.cancel();
                  } else {// 登录成功'

                    StaticVarUtil.listHerf = new ArrayList<HashMap<String, String>>();
                    JSONObject json = new JSONObject(result);
                    JSONArray jsonArray = (JSONArray) json.get("listHerf");
                    for (int i = 0; i < jsonArray.length(); i++) {
                      JSONObject o = (JSONObject) jsonArray.get(i);
                      HashMap<String, String> map = new HashMap<String, String>();
                      map.put("herf", o.getString("herf"));
                      map.put("tittle", o.getString("tittle"));
                      StaticVarUtil.listHerf.add(map);
                    }

                    StaticVarUtil.student.setAccount(account.trim());
                    StaticVarUtil.student.setPassword(password.trim());
                    Intent intent = new Intent();
                    intent.setClass(mActivity, MainActivity.class);
                    if (progressDialog != null) {
                      progressDialog.dismiss();
                    }
                    // progressDialog.cancel();
                    mActivity.startActivity(intent);
                    mActivity.finish();
                    StaticVarUtil.quit();

                  }

                }

              } else {

                // 重新登录
                if (StaticVarUtil.loginTimes < 3) {
                  LoginAsynctask loginAsyntask = new LoginAsynctask(mActivity, account, password,
                      pic, this, progressDialog);
                  ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_REPEAT_EXCEPTION);
                  loginAsyntask.execute();
                } else {
                  ViewUtil.showToast(mActivity, HttpUtilMc.CONNECT_EXCEPTION);
                  if (progressDialog != null) {
                    progressDialog.dismiss();
                  }
                  onResult.onReturn(HttpUtilMc.CONNECT_EXCEPTION);
                  // progressDialog.cancel();
                }
              }
            } catch (Exception e) {
              // TODO: handle exception
              Log.i("LoginActivity", e.toString());
              if (progressDialog != null) {
                progressDialog.dismiss();
              }
            }

          }
        }, progressDialog);
    loginAsyntask.execute();
  }
}
