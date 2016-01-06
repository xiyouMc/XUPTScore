package com.xy.fy.asynctask;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cardsui.example.MyPlayCard;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.mc.util.ProgressDialogUtil;
import com.mc.util.ScoreUtil;
import com.mc.util.Util;
import com.xy.fy.main.R;
import com.xy.fy.main.ShowScoreActivity;
import com.xy.fy.util.StaticVarUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class ShowCardAsyncTask extends AsyncTask<String, String, Boolean> {

  private CardUI mCardView;
  private Resources resources;
  private boolean isFirst;
  private Activity mActivity;

  ArrayList<HashMap<String, Object>> listItem;// json解析之后的列表,保存了所有的成绩数据
  private String scoreJson;// json数据

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
    
    dialog =  ProgressDialogUtil.getInstance(mActivity);
  }

  @Override
  protected Boolean doInBackground(String... params) {
    // TODO Auto-generated method stub
    String[] xn = resources.getStringArray(R.array.xn);
    boolean result = false;
    for (int i = 0; i < xn.length; i++) {
      result = showCard(xn[i], isFirst);// 这里延迟太大，可以考虑直接从内存取
      // 不用每次计算。(已经修改为从内存中取值)
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
    if (!Util.isRecordLoginMessage(mActivity)) {// 是否已经上传了手机信息
      // 未上传,则保存手机信息到本地
      Util.saveDeviceInfo(mActivity);
      // 上传各种信息
      Util.uploadDevInfos(mActivity);
    }
      dialog.dismiss();
  }

  /*
   * 显示卡片
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
      xqs_str += "第一学期,";
      CardStack stackPlay = new CardStack();
      stackPlay.setTitle(xn);
      mCardView.addStack(stackPlay);
      MyPlayCard _myPlayCard = new MyPlayCard("第一学期", first_score, "#33b6ea", "#33b6ea", true,
          false);
      String[][] first_score_array = getScoreToArray(first_score);
      _myPlayCard.setOnClickListener(
          new ScoreClass(first_score_array.length, first_score_array, xn + " 第一学期"));
      mCardView.addCard(_myPlayCard);
      // mCardView.addCardToLastStack(new
      // MyCard("By Androguide & GadgetCheck"));
    }

    if (!second_score.equals("")) {
      xqs_str += "第二学期,";
      MyPlayCard myCard = new MyPlayCard("第二学期", second_score, "#e00707", "#e00707", false, true);
      String[][] second_score_array = getScoreToArray(second_score);
      myCard.setOnClickListener(
          new ScoreClass(second_score_array.length, second_score_array, xn + " 第二学期"));
      mCardView.addCardToLastStack(myCard);
    }
    if (xqs_str.length() != 0) {
      xqs_str = xqs_str.substring(0, xqs_str.length() - 1);
      StaticVarUtil.list_Rank_xnAndXq.put(xn, xqs_str);
    }

    return true;

  }

  /*
   * 点击卡片跳转
   * 
   * @author Administrator 2014-7-23
   */
  class ScoreClass implements OnClickListener {
    int col;// 成绩的总行号
    String[][] score;// 将所有成绩整合为二维数组
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

  /*
   * 解析 获取固定学年 固定学期的成绩
   */
  private StringBuilder getScore(String xn, String xq) {
    StringBuilder result = null;
    if (listItem != null) {
      result = new StringBuilder();
      // 解析json
      JSONObject jsonObject;
      try {
        jsonObject = new JSONObject(scoreJson);
        JSONArray jsonArray = (JSONArray) jsonObject.get("liScoreModels");// 学年
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject o = (JSONObject) jsonArray.get(i);
          if (o.get("xn").equals(xn)) {// 某个学年
            JSONArray jsonArray2 = (JSONArray) o.get("list_xueKeScore");
            for (int j = 0; j < jsonArray2.length(); j++) {
              JSONObject jsonObject2 = (JSONObject) jsonArray2.get(j);
              if (jsonObject2.get("xq").equals(xq)) {
                // 添加 课程代码->课程名
                StaticVarUtil.kcdmList.put(
                    jsonObject2.get("kcmc") == null ? " " : jsonObject2.get("kcmc").toString(),
                    jsonObject2.get("kcdm").toString() + "|" + xn + "|" + xq);
                result.append(
                    jsonObject2.get("kcmc") == null ? " " : jsonObject2.get("kcmc").toString());// 课程名称
                result.append("--"
                    + jsonObject2.get("cj")// 最终成绩
                        .toString()
                    + (jsonObject2.get("bkcj").equals(" ") ? " "
                        : ("(" + jsonObject2.get("bkcj").toString() + ")"))// 将补考成绩和最终成绩同时显示。
                );
                result.append(jsonObject2.get("pscj")// 平时成绩
                    .equals("") ? "/" : "--" + jsonObject2.get("pscj").toString());
                result.append(jsonObject2.get("qmcj")// 期末成绩
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
   * 将 成绩整合成 n行4列的数组，为了可以显示在table页面中
   * 
   * @param score
   * 
   * @return
   */
  private String[][] getScoreToArray(String score) {
    String[] s = score.split("\n");
    String[][] result = new String[s.length][4];// n行 4列的数组
    for (int i = 0; i < result.length; i++) {
      result[i] = s[i].split("--");
    }
    return result;
  }

}
