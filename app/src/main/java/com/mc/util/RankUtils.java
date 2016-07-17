package com.mc.util;

import com.xy.fy.main.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

public class RankUtils {

    private final static int DEFAULTITEMSUM = 300;
    public static ArrayList<HashMap<String, Object>> allRankArrayList = null;// ���е����
    public static ArrayList<HashMap<String, Object>> showRankArrayList = null;// Ӧ����ʾ�����
    public static List<BmobChatUser> userList;
    public static boolean isFirstListView = true;
    public static HashMap<String, String> allRankMap = new HashMap<String, String>();// ����ѧ���ѧ�ڵĳɼ�
    public static SimpleAdapter simpleAdapter;
    public static CustomRankListView allRankList;
    public static int lsitItemSum = DEFAULTITEMSUM;
    public static TextView rankText;
    public static TextView rankScoreText;
    public static String selectXn;
    public static String selectXq;

    /*
     *
     * @param result �ɼ��ַ�
     *
     * @param isFirst �Ƿ��ǵ�һ��
     */
    public static void refeshRank(String result, boolean isFirst, Context context) {
        try {
            if (RankUtils.allRankArrayList == null || RankUtils.showRankArrayList == null) {
                RankUtils.allRankArrayList = new ArrayList<HashMap<String, Object>>();
                RankUtils.showRankArrayList = new ArrayList<HashMap<String, Object>>();
            } else {
                RankUtils.allRankArrayList.clear();
                RankUtils.showRankArrayList.clear();
            }

            JSONObject jsonObject = new JSONObject(result);
            String rank = String.valueOf(jsonObject.getString("rank"));
            rankText.setText(rank);

            JSONArray jsonArray = (JSONArray) jsonObject.get("rankList");
            int rankId = 0;
            for (int i = jsonArray.length() - 1; i >= 0; i--) {
                JSONObject o = (JSONObject) jsonArray.get(i);
                HashMap<String, Object> map = new HashMap<String, Object>();
                rankId += 1;
                map.put("rankId", String.valueOf(rankId));
                map.put("name", o.get("name"));
                map.put("xh", o.get("xh"));
                map.put("score", o.get("score"));
                RankUtils.allRankArrayList.add(map);
                if (String.valueOf(rankId).equals(rank)) {
                    rankScoreText.setText(o.get("score").toString());// ��ʾ�ɼ�
                    Animation animation = AnimationUtils.loadAnimation(context, R.anim.textscore_translate);
                    rankScoreText.setAnimation(animation);

                }
            }
            // ��ȡ ֮ǰ��õĹ̶� �����item�� ��ֹ�����̫�󣬶��µĽ�������ݶ���ʾ������
            for (int i = 0; i < (lsitItemSum > RankUtils.allRankArrayList.size()
                    ? RankUtils.allRankArrayList.size() : lsitItemSum); i++) {
                RankUtils.showRankArrayList.add(RankUtils.allRankArrayList.get(i));
            }
            if (isFirst) {
                setListView(context);
                RankUtils.isFirstListView = false;
            } else {
                simpleAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // ��ʾ �����listview
    private static void setListView(Context context) {
        // TODO Auto-generated method stub

        simpleAdapter = new SimpleAdapter(context, RankUtils.showRankArrayList,
                R.layout.allrank_listitem, new String[]{"rankId", "name", "score"},
                new int[]{R.id.rankId, R.id.name, R.id.score});
        allRankList.setAdapter(simpleAdapter);

    }

}
