package com.xy.fy.main;

import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class ChangePwdActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_setting);

        EditText etAccount = (EditText) findViewById(R.id.etAccount);
        etAccount.setText(StaticVarUtil.student.getAccount() + "");// �޸Ķ���ʾһ��0������
        etAccount.setEnabled(false);// ������
        final EditText etPassword1 = (EditText) findViewById(R.id.etPassword1);
        final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
        final EditText cofPassword2 = (EditText) findViewById(R.id.corfimPassword2);// ȷ������
        // �޸�
        Button butAlter = (Button) findViewById(R.id.butAlter);
        butAlter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // �ؼ�ֵ
                String password1 = etPassword1.getText().toString();
                String password2 = etPassword2.getText().toString().trim();
                String password3 = cofPassword2.getText().toString().trim();
                if (password1.equals("") && password2.equals("") && password3.equals("")) {
                    ViewUtil.showToast(getApplicationContext(), "��û����Ϣ��Ҫ�޸�");
                    return;
                }

                // ����
                if (password1.equals("") && password2.equals("")) {
                    // ����޸�

                } else {
                    if (password1.equals(StaticVarUtil.student.getPassword())
                            && password2.equals("") == false) {
                        ;
                    } else {
                        ViewUtil.showToast(getApplicationContext(), "�����벻��ȷ���������벻��Ϊ��,������");
                        return;
                    }
                }
                if (password2.equals(password3)) {
                    if (!Util.hasDigitAndNum(password2)) {
                        ViewUtil.showToast(getApplicationContext(), "�����б�������ֺ���ĸ");
                    } else {
                        if (password2.length() < 6) {
                            // �����޸�������볬��6λ
                            ViewUtil.showToast(getApplicationContext(), "������볬��6λ");
                        } else {
                            ChangePwAsyntask changePwAsyntask = new ChangePwAsyntask();
                            changePwAsyntask.execute(new String[]{password1, password2});
                        }

                    }

                } else {
                    ViewUtil.showToast(getApplicationContext(), "�����벻��ȷ");

                    return;
                }

            }
        });
    }

    // �첽�ı�����
    class ChangePwAsyntask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String canshu = Util.getURL(StaticVarUtil.CHANGE_PW);
            return HttpUtilMc
                    .queryStringForPost(HttpUtilMc.BASE_URL + "changepw.jsp?session=" + StaticVarUtil.session
                            + "&url=" + canshu + "&old_password=" + params[0] + "&new_password=" + params[1]);

        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            // progress.cancel();
            // ��ʾ�û���

            Intent intent = new Intent();

            try {
                if (HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
                    ViewUtil.showToast(getApplicationContext(), HttpUtilMc.CONNECT_EXCEPTION);
                    return;
                }
                ViewUtil.showToast(getApplicationContext(),
                        !result.equals("error") ? "�޸ĳɹ�,�����µ�¼" : "�޸Ĳ��ɹ�");
                if (!result.equals("error")) {
                    intent.putExtra("isQuit", true);
                    setResult(Util.CHANGE_PWD_RESULT, intent);
                    finish();
                    // quit(true);// ע�����µ�¼
                }
            } catch (Exception e) {
                // TODO: handle exception
                e.printStackTrace();
                Log.i("LoginActivity", e.toString());
            }

        }

    }

}
