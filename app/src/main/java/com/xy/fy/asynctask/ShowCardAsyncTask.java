package com.xy.fy.asynctask;

import com.cardsui.example.MyPlayCard;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.ScoreUtil;
import com.mc.util.Util;
import com.xy.fy.main.R;
import com.xy.fy.main.ShowScoreActivity;
import com.xy.fy.util.StaticVarUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowCardAsyncTask extends AsyncTask<String, String, Boolean> {

    ArrayList<HashMap<String, Object>> listItem;// json����֮����б�,���������еĳɼ����
    private CardUI mCardView;
    private Resources resources;
    private boolean isFirst;
    private Activity mActivity;
    private String scoreJson;// json���

    private ProgressDialogUtil dialog;

    private ShowCardAsyncTask() {
    }

    public ShowCardAsyncTask(Activity mActivity, Resources resources, boolean isFirst,
                             CardUI mCardView, ArrayList<HashMap<String, Object>> listItem, String scoreJson) {
        this.mActivity = mActivity;
        this.resources = resources;
        this.isFirst = isFirst;
        this.mCardView = mCardView;
        this.listItem = listItem;
        this.scoreJson = scoreJson;

        dialog = ProgressDialogUtil.getInstance(mActivity);
    }

    @Override
    protected Boolean doInBackground(String... params) {
        // TODO Auto-generated method stub
        String[] xn = resources.getStringArray(R.array.xn);
        boolean result = false;
        for (int i = 0; i < xn.length; i++) {
            result = showCard(xn[i], isFirst);// �����ӳ�̫�󣬿��Կ���ֱ�Ӵ��ڴ�ȡ
            // ����ÿ�μ��㡣(�Ѿ��޸�Ϊ���ڴ���ȡֵ)
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);
        if (result) {
            mCardView.refresh();
        }
        if (!Util.isRecordLoginMessage(mActivity)) {// �Ƿ��Ѿ��ϴ����ֻ���Ϣ
            // δ�ϴ�,�򱣴��ֻ���Ϣ������
            Util.saveDeviceInfo(mActivity);
            // �ϴ�������Ϣ
            Util.uploadDevInfos(mActivity);
        }
        dialog.dismiss();
    }

    /*
     * ��ʾ��Ƭ
     */
    private boolean showCard(String xn, boolean isFirst) {
        String first_score = "";
        String second_score = "";

        if (isFirst || (ScoreUtil.mapScoreOne.isEmpty() && ScoreUtil.mapScoreTwo.isEmpty())) {
            first_score = getScore(xn, "1") == null ? "" : getScore(xn, "1").toString();
            second_score = getScore(xn, "2") == null ? "" : getScore(xn, "2").toString();
            ScoreUtil.mapScoreOne.put(xn, first_score);
            ScoreUtil.mapScoreTwo.put(xn, second_score);
        } else {
            first_score = ScoreUtil.mapScoreOne.get(xn) == null ? "" : ScoreUtil.mapScoreOne.get(xn);
            second_score = ScoreUtil.mapScoreTwo.get(xn) == null ? "" : ScoreUtil.mapScoreTwo.get(xn);
        }

        // add one card, and then add another one to the last stack.
        String xqs_str = "";
        if (!first_score.equals("")) {
            xqs_str += "��һѧ��,";
            CardStack stackPlay = new CardStack();
            stackPlay.setTitle(xn);
            mCardView.addStack(stackPlay);
            MyPlayCard _myPlayCard = new MyPlayCard("��һѧ��", first_score, "#33b6ea", "#33b6ea", true,
                    false);
            String[][] first_score_array = getScoreToArray(first_score);
            _myPlayCard.setOnClickListener(
                    new ScoreClass(first_score_array.length, first_score_array, xn + " ��һѧ��"));
            mCardView.addCard(_myPlayCard);
            // mCardView.addCardToLastStack(new
            // MyCard("By Androguide & GadgetCheck"));
        }

        if (!second_score.equals("")) {
            xqs_str += "�ڶ�ѧ��,";
            MyPlayCard myCard = new MyPlayCard("�ڶ�ѧ��", second_score, "#e00707", "#e00707", false, true);
            String[][] second_score_array = getScoreToArray(second_score);
            myCard.setOnClickListener(
                    new ScoreClass(second_score_array.length, second_score_array, xn + " �ڶ�ѧ��"));
            mCardView.addCardToLastStack(myCard);
        }
        if (xqs_str.length() != 0) {
            xqs_str = xqs_str.substring(0, xqs_str.length() - 1);
            StaticVarUtil.list_Rank_xnAndXq.put(xn, xqs_str);
        }

        return true;

    }

    /*
     * ���� ��ȡ�̶�ѧ�� �̶�ѧ�ڵĳɼ�
     */
    private StringBuilder getScore(String xn, String xq) {
        StringBuilder result = null;
        if (listItem != null) {
            result = new StringBuilder();
            // ����json
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject(scoreJson);
                JSONArray jsonArray = (JSONArray) jsonObject.get("liScoreModels");// ѧ��
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject o = (JSONObject) jsonArray.get(i);
                    if (o.get("xn").equals(xn)) {// ĳ��ѧ��
                        JSONArray jsonArray2 = (JSONArray) o.get("list_xueKeScore");
                        for (int j = 0; j < jsonArray2.length(); j++) {
                            JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
                            if (jsonObject2.get("xq").equals(xq)) {
                                // ��� �γ̴���->�γ���
                                StaticVarUtil.kcdmList.put(
                                        jsonObject2.get("kcmc") == null ? " " : jsonObject2.get("kcmc").toString(),
                                        jsonObject2.get("kcdm").toString() + "|" + xn + "|" + xq);
                                result.append(
                                        jsonObject2.get("kcmc") == null ? " " : jsonObject2.get("kcmc").toString());// �γ����
                                result.append("--"
                                        + jsonObject2.get("cj")// ���ճɼ�
                                        .toString()
                                        + (jsonObject2.get("bkcj").equals(" ") ? " "
                                        : ("(" + jsonObject2.get("bkcj").toString() + ")"))// �������ɼ������ճɼ�ͬʱ��ʾ��
                                );
                                result.append(jsonObject2.get("pscj")// ƽʱ�ɼ�
                                        .equals("") ? "/" : "--" + jsonObject2.get("pscj").toString());
                                result.append(jsonObject2.get("qmcj")// ��ĩ�ɼ�
                                        .equals("") ? "/" : "--" + jsonObject2.get("qmcj").toString());
                                result.append("\n");
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
        return result;
    }

    /*
     * �� �ɼ���ϳ� n��4�е����飬Ϊ�˿�����ʾ��tableҳ����
     *
     * @param score
     *
     * @return
     */
    private String[][] getScoreToArray(String score) {
        String[] s = score.split("\n");
        String[][] result = new String[s.length][4];// n�� 4�е�����
        for (int i = 0; i < result.length; i++) {
            result[i] = s[i].split("--");
        }
        return result;
    }

    /*
     * �����Ƭ��ת
     *
     * @author Administrator 2014-7-23
     */
    class ScoreClass implements OnClickListener {
        int col;// �ɼ������к�
        String[][] score;// �����гɼ����Ϊ��ά����
        String xn;

        public ScoreClass(int col, String[][] score, String xn) {
            this.col = col;
            this.score = score;
            this.xn = xn;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (Util.isFastDoubleClick()) {
                return;
            }
            Intent i = new Intent();
            i.setClass(mActivity, ShowScoreActivity.class);
            Bundle b = new Bundle();
            b.putString("col", String.valueOf(col));
            for (int j = 0; j < score.length; j++) {
                b.putStringArray("score" + j, score[j]);
            }
            b.putString("xn_and_xq", xn);
            i.putExtras(b);
            mActivity.startActivity(i);
        }

    }

}
