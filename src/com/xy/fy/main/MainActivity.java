package com.xy.fy.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.cardsui.example.MyPlayCard;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.mc.util.CircleImageView;
import com.mc.util.CustomRankListView;
import com.mc.util.CustomRankListView.OnAddFootListener;
import com.mc.util.CustomRankListView.OnFootLoadingListener;
import com.mc.util.HttpAssist;
import com.mc.util.HttpUtilMc;
import com.mc.util.LogcatHelper;
import com.mc.util.Passport;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.xy.fy.adapter.ChooseHistorySchoolExpandAdapter;
import com.xy.fy.adapter.ChooseSchoolExpandAdapter;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.TestArrayAdapter;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.HistoryCollege;
import com.xy.fy.view.ToolClass;

/**
 * 第一次启动主界面就请求 查询成绩
 * 
 * @author Administrator 2014-7-21
 */
@SuppressLint({ "ShowToast", "InflateParams" })
public class MainActivity extends Activity {

	// 保存成绩的map
	public static HashMap<String, String> mapScoreOne = null;// xn =1
	public static HashMap<String, String> mapScoreTwo = null;// xn = 2
	private static boolean isFirst = true;
	// 检测版本
	private static PopupWindow version_popupWindow;
	private static boolean is_show = false;
	private View view;
	private TextView update_content_textview;

	public static SlidingMenu slidingMenu;// 侧滑组件
	private Button chooseCollege;// 选择学校按钮
	private Button chooseMsgKind;// 选择说说种类
	private Button chooseMsgSort;// 选择说说排序方式
	private CardUI mCardView;
	private TextView nickname;// 用户名
	private String name;//
	private CircleImageView headPhoto;// 头像
	private LinearLayout menuBang = null;// 成绩查询
	private LinearLayout menuMyBukao = null;// 补考查询
	private LinearLayout menuMyPaiming = null;// 我的排名
	private LinearLayout menuMyCollect = null;// 我收藏的
	private LinearLayout menuSetting = null;// 设置
	private LinearLayout menuAbout = null;// 关于
	private Button check_version = null;
	ArrayList<HashMap<String, Object>> listItem;// json解析之后的列表,保存了所有的成绩数据
	// 排名
	private final static int DEFAULTITEMSUM = 100;
	private static int lsitItemSum = DEFAULTITEMSUM;// 通过计算屏幕高度，求得应该显示多少行数据在listview
	private CustomRankListView allRankList;
	private TextView rankText;
	private TextView nameText;
	private TextView rankScoreText;
	private HashMap<String, String> allRankMap = new HashMap<String, String>();// 所有学年和学期的成绩
	private ArrayList<HashMap<String, Object>> allRankArrayList;// 所有的数据
	private ArrayList<HashMap<String, Object>> showRankArrayList;// 应该显示的数据
	private AutoCompleteTextView search_edittext;
	private SimpleAdapter simpleAdapter;
	private String score_json;// json数据
	private static boolean isFirstListView = true;

	private ProgressDialog dialog = null;

	private Bitmap bitmap = null;// 修改头像

	private static final int PIC = 11;// 图片
	private static final int PHO = 22;// 照相
	private static final int RESULT = 33;// 返回结果

	private ChooseSchoolExpandAdapter adapter = new ChooseSchoolExpandAdapter(
			MainActivity.this);

	@SuppressLint("ShowToast")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.activity_main);

		softDeclare();// 将部分 变量 定义为弱引用
		dialog = ViewUtil.getProgressDialog(MainActivity.this, "正在查询");
		// init map
		mapScoreOne = new HashMap<String, String>();// tm 指向同一块内存了 。
		mapScoreTwo = new HashMap<String, String>();
		setMenuItemListener();

		// 当前Activity进栈
		StaticVarUtil.activities.add(MainActivity.this);

		// 找到ID
		slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenuXyScore);
		// 打开sliding组件监听
		slidingMenu.setOnOpenListener(new OnOpenListener() {
			@Override
			public void onOpen() {
				// 读取当前菜单的选项
				int item = getCurrentMeunItem();
				if (item == 1) {
					setMenuItemState(menuBang, true, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 2) {
					setMenuItemState(menuBang, false, menuMyBukao, true,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 3) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, true, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
				} else if (item == 4) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, true,
							menuSetting, false, menuAbout, false);
				} else if (item == 5) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, true, menuAbout, false);
				} else if (item == 6) {
					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, false, menuMyCollect, false,
							menuSetting, false, menuAbout, true);
				}
			}
		});

		if (!Util.checkPWD(StaticVarUtil.student.getPassword())) {
			ViewUtil.showToast(getApplicationContext(), "密码不安全，请重新设置密码");
			setMenuItemState(menuBang, false, menuMyBukao, false,
					menuMyPaiming, false, menuMyCollect, false, menuSetting,
					true, menuAbout, false);
			setCurrentMenuItem(5);// 记录当前选项位置，并且跳转
			slidingMenu.toggle();// 页面跳转
			slidingMenu.setContent(R.layout.activity_setting);
			menuSetting();
		} else {
			// 请求 获取 成绩
			GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
			dialog.show();
			getScoreAsyntask.execute();
		}
	}

	private void softDeclare() {
		// TODO Auto-generated method stub

	}

	/**
	 * 第一个菜单项
	 */
	private void menu1() {
		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});
		if (mCardView != null) {
			isFirst = false;
		}
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		ShowCardAsyntask showCardAsyntask = new ShowCardAsyntask();
		showCardAsyntask.execute();
	}

	class ShowCardAsyntask extends AsyncTask<String, String, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			Resources resources = getResources();
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
			super.onPostExecute(result);
			if (result) {
				mCardView.refresh();
			}
			if (!Util.isRecordLoginMessage(getApplicationContext())) {// 是否已经上传了手机信息
				// 未上传,则保存手机信息到本地
				Util.saveDeviceInfo(getApplicationContext());
				// 上传各种信息
				Util.uploadDevInfos(getApplicationContext());
			}
		}

	}

	/**
	 * 显示卡片
	 */
	private boolean showCard(String xn, boolean isFirst) {
		String first_score = "";
		String second_score = "";

		if (isFirst || (mapScoreOne.isEmpty() && mapScoreTwo.isEmpty())) {
			first_score = getScore(xn, "1") == null ? "" : getScore(xn, "1")
					.toString();
			second_score = getScore(xn, "2") == null ? "" : getScore(xn, "2")
					.toString();
			mapScoreOne.put(xn, first_score);
			mapScoreTwo.put(xn, second_score);
		} else {
			first_score = mapScoreOne.get(xn) == null ? "" : mapScoreOne
					.get(xn);
			second_score = mapScoreTwo.get(xn) == null ? "" : mapScoreTwo
					.get(xn);
		}

		// add one card, and then add another one to the last stack.
		String xqs_str = "";
		if (!first_score.equals("")) {
			xqs_str += "第一学期,";
			CardStack stackPlay = new CardStack();
			stackPlay.setTitle(xn);
			mCardView.addStack(stackPlay);
			MyPlayCard _myPlayCard = new MyPlayCard("第一学期", first_score,
					"#33b6ea", "#33b6ea", true, false);
			String[][] first_score_array = getScoreToArray(first_score);
			_myPlayCard.setOnClickListener(new ScoreClass(
					first_score_array.length, first_score_array, xn + " 第一学期"));
			mCardView.addCard(_myPlayCard);
			// mCardView.addCardToLastStack(new
			// MyCard("By Androguide & GadgetCheck"));
		}

		if (!second_score.equals("")) {
			xqs_str += "第二学期,";
			MyPlayCard myCard = new MyPlayCard("第二学期", second_score, "#e00707",
					"#e00707", false, true);
			String[][] second_score_array = getScoreToArray(second_score);
			myCard.setOnClickListener(new ScoreClass(second_score_array.length,
					second_score_array, xn + " 第二学期"));
			mCardView.addCardToLastStack(myCard);
		}
		if (xqs_str.length() != 0) {
			xqs_str = xqs_str.substring(0, xqs_str.length() - 1);
			StaticVarUtil.list_Rank_xnAndXq.put(xn, xqs_str);
		}

		return true;

	}

	/**
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
			Intent i = new Intent();
			i.setClass(getApplicationContext(), ShowScoreActivity.class);
			Bundle b = new Bundle();
			b.putString("col", String.valueOf(col));
			for (int j = 0; j < score.length; j++) {
				b.putStringArray("score" + j, score[j]);
			}
			b.putString("xn_and_xq", xn);
			i.putExtras(b);
			startActivity(i);
		}

	}

	/**
	 * 将 成绩整合成 n行4列的数组，为了可以显示在table页面中
	 * 
	 * @param score
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

	/**
	 * 解析 获取固定学年 固定学期的成绩
	 */
	private StringBuilder getScore(String xn, String xq) {
		StringBuilder result = null;
		if (listItem != null) {
			result = new StringBuilder();
			// 解析json
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(score_json);
				JSONArray jsonArray = (JSONArray) jsonObject
						.get("liScoreModels");// 学年
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject o = (JSONObject) jsonArray.get(i);
					if (o.get("xn").equals(xn)) {// 某个学年
						JSONArray jsonArray2 = (JSONArray) o
								.get("list_xueKeScore");
						for (int j = 0; j < jsonArray2.length(); j++) {
							JSONObject jsonObject2 = (JSONObject) jsonArray2
									.get(j);
							if (jsonObject2.get("xq").equals(xq)) {
								result.append(jsonObject2.get("kcmc") == null ? " "
										: jsonObject2.get("kcmc").toString());// 课程名称
								result.append("--"
										+ jsonObject2.get("cj")// 最终成绩
												.toString()
										+ (jsonObject2.get("bkcj").equals(" ") ? " "
												: ("("
														+ jsonObject2.get(
																"bkcj")
																.toString() + ")"))// 将补考成绩和最终成绩同时显示。
								);
								result.append(jsonObject2.get("pscj")// 平时成绩
										.equals("") ? "/" : "--"
										+ jsonObject2.get("pscj").toString());
								result.append(jsonObject2.get("qmcj")// 期末成绩
										.equals("") ? "/" : "--"
										+ jsonObject2.get("qmcj").toString());
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

	/**
	 * 设置当前MenuItem的状态
	 * 
	 * @param item
	 *            MenuItem组件，flag代表组件状态
	 */
	private void setMenuItemState(LinearLayout item1, boolean flag1,
			LinearLayout item2, boolean flag2, LinearLayout item3,
			boolean flag3, LinearLayout item4, boolean flag4,
			LinearLayout item5, boolean flag5, LinearLayout item6, boolean flag6) {
		item1.setPressed(flag1);
		item2.setPressed(flag2);
		item3.setPressed(flag3);
		item4.setPressed(flag4);
		item5.setPressed(flag5);
		item6.setPressed(flag6);
	}

	/**
	 * 设置一些menuItem监听
	 */
	private void setMenuItemListener() {

		mCardView = null;
		nickname = (TextView) findViewById(R.id.nickname);// 用户名
		headPhoto = (CircleImageView) findViewById(R.id.headphoto);// 头像
		menuBang = (LinearLayout) findViewById(R.id.menu_bang);// 1.成绩查询
		menuMyBukao = (LinearLayout) findViewById(R.id.menu_my_bukao);// 2.补考查询
		menuMyPaiming = (LinearLayout) findViewById(R.id.menu_my_paiming);// 3.我的排名
		menuMyCollect = (LinearLayout) findViewById(R.id.menu_my_collect);// 4.我收藏的
		menuSetting = (LinearLayout) findViewById(R.id.menu_setting);// 5.设置
		menuAbout = (LinearLayout) findViewById(R.id.menu_about);

		LinearLayout menuQuit = (LinearLayout) findViewById(R.id.menu_quit);
		menuBang.setPressed(true);// 初始化默认是风云榜被按下
		setCurrentMenuItem(1);// 记录当前选项位置

		// 判断 头像文件夹中是否包含 该用户的头像
		File file = new File(StaticVarUtil.PATH + "/"
				+ StaticVarUtil.student.getAccount() + ".JPEG");
		if (file.exists()) {// 如果存在
			Bitmap bitmap = Util.convertToBitmap(StaticVarUtil.PATH + "/"
					+ StaticVarUtil.student.getAccount() + ".JPEG", 240, 240);
			headPhoto.setImageBitmap(bitmap);
		} else {// 如果文件夹中不存在这个头像。
			GetPicture getPicture = new GetPicture();
			getPicture.execute(new String[] { HttpUtilMc.BASE_URL
					+ "user_photo/" + StaticVarUtil.student.getAccount()
					+ ".JPEG" });
		}
		headPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated methodstub

				File file = new File(StaticVarUtil.PATH);
				if (!file.exists()) {
					file.mkdirs();// 创建文件
				}
				chooseHeadPhoto();// 改变头像
			}
		});

		menuBang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, true, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, false, menuAbout, false);
				setCurrentMenuItem(1);// 记录当前选项位置
				slidingMenu.toggle();// 页面跳转

				slidingMenu.setContent(R.layout.card_main);
				menu1();
			}
		});

		// 补考助手
		menuMyBukao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断如果没有头像的话，先让选择头像，并填写昵称
				// 暂且跳转到好友列表
				// showToast("程序猿们正在努力开发中，请持续关注...");

				setMenuItemState(menuBang, false, menuMyBukao, true,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, false, menuAbout, false);
				setCurrentMenuItem(2);// 记录当前选项位置
				slidingMenu.toggle();// 页面跳转

				slidingMenu.setContent(R.layout.activity_friend_list);
				friend_list();

			}
		});

		menuMyPaiming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StaticVarUtil.list_Rank_xnAndXq.size() != 0) {

					setMenuItemState(menuBang, false, menuMyBukao, false,
							menuMyPaiming, true, menuMyCollect, false,
							menuSetting, false, menuAbout, false);
					setCurrentMenuItem(3);// 记录当前选项位置
					slidingMenu.toggle();// 页面跳转

					slidingMenu.setContent(R.layout.activity_rank);
					rank();
				} else {
					ViewUtil.showToast(getApplicationContext(), "网络不稳定，请稍后查询");
				}

			}

		});

		menuMyCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ViewUtil.showToast(getApplicationContext(),
						"程序猿们正在努力开发中，请持续关注...");
			}
		});

		menuSetting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, false, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, true, menuAbout, false);
				setCurrentMenuItem(5);// 记录当前选项位置，并且跳转
				slidingMenu.toggle();// 页面跳转

				slidingMenu.setContent(R.layout.activity_setting);
				menuSetting();

			}
		});

		menuAbout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setMenuItemState(menuBang, false, menuMyBukao, false,
						menuMyPaiming, false, menuMyCollect, false,
						menuSetting, false, menuAbout, true);
				setCurrentMenuItem(6);// 记录当前选项位置，并且跳转
				slidingMenu.toggle();// 页面跳转

				slidingMenu.setContent(R.layout.activity_about);

				aboutListener();
			}
		});

		menuQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				quit(true);
			}
		});

	}

	private void aboutListener() {
		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});

		ImageView qrcode_imageview = (ImageView) findViewById(R.id.code);
		qrcode_imageview.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showDialogSaveQrcode();

			}
		});
		check_version = (Button) findViewById(R.id.checkversion);// 检测新版本按钮
		TextView version = (TextView) findViewById(R.id.version);
		version.setText(Util.getVersion(getApplicationContext()));
		check_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				CheckVersionAsyntask checkVersionAsyntask = new CheckVersionAsyntask();
				is_show = true;
				dialog.show();
				checkVersionAsyntask.execute();
			}
		});
	}

	/**
	 * 保存二维码
	 */
	private void showDialogSaveQrcode() {
		final CharSequence[] items = { "保存二维码" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (!new File(StaticVarUtil.PATH + Util.QRCODE_FILENAME)
						.exists()) {
					Bitmap bt = ((BitmapDrawable) getApplicationContext()
							.getResources().getDrawable(R.drawable.qrcode))
							.getBitmap();
					BitmapUtil.saveFileAndDB(getApplicationContext(), bt,
							Util.QRCODE_FILENAME);
				}
				ViewUtil.showToast(getApplicationContext(), "二维码已保存，请将其分享给同学！");

			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	String selectXn;
	String selectXq;
	// TODO Auto-generated method stub
	Spinner xnSpinner;
	Spinner xqSpinner;
	ArrayAdapter<String> xnAdapter;
	ArrayAdapter<String> xqAdapter;

	private void rank() {

		// menu出发 判断为第一次 为了初始化 listview
		isFirstListView = true;
		findviewById();
		rankScoreText.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!rankText.getText().equals("")) {
					allRankList.setSelection(Integer.parseInt(rankText
							.getText().toString()) - 1);
				}
			}
		});
		// search
		search_edittext.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (search_edittext.getText().toString().length() > 0) {
					// 定位
					for (HashMap<String, Object> map : showRankArrayList) {
						if ((map.get("name").toString()).equals(search_edittext
								.getText().toString())) {
							search_edittext.clearFocus();
							closeInputMethod();
							allRankList.setSelection(Integer.parseInt(map.get(
									"rankId").toString()) - 1);

						}
					}
				}
			}
		});
		search_edittext.setOnKeyListener(new OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (KeyEvent.KEYCODE_ENTER == keyCode
						&& event.getAction() == KeyEvent.ACTION_DOWN) {
					boolean isSearch = false;
					for (HashMap<String, Object> map : showRankArrayList) {
						if ((map.get("name").toString()).equals(search_edittext
								.getText().toString())) {
							allRankList.setSelection(Integer.parseInt(map.get(
									"rankId").toString()) - 1);
							isSearch = true;
						}
					}
					if (!isSearch) {
						ViewUtil.showToast(getApplicationContext(), "没有该学生信息");
					}
					search_edittext.clearFocus();
					closeInputMethod();
					return true;
				}
				return false;

			}
		});

		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});
		// 分享按钮
		Button share = (Button) findViewById(R.id.share);
		share.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ViewUtil.showShare(getApplicationContext());
			}
		});
		// rankListViewListener();

		nameText.setText(name);
		WindowManager wm = this.getWindowManager();
		@SuppressWarnings("deprecation")
		int width = wm.getDefaultDisplay().getWidth() / 4 + 10;
		xnSpinner = (Spinner) findViewById(R.id.xnSpinner);
		xnSpinner.setLayoutParams(new LinearLayout.LayoutParams(width,
				LinearLayout.LayoutParams.WRAP_CONTENT));

		// xnSpinner.setDropDownWidth(width);
		xqSpinner = (Spinner) findViewById(R.id.xqSpinner);
		xqSpinner.setLayoutParams(new LinearLayout.LayoutParams(width,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		String[] xns = new String[StaticVarUtil.list_Rank_xnAndXq.size()];
		int i = 0;

		Iterator<?> it = StaticVarUtil.list_Rank_xnAndXq.entrySet().iterator();
		while (it.hasNext()) {
			Entry<?, ?> entry = (Entry<?, ?>) it.next();
			xns[i] = String.valueOf(entry.getKey());// 返回与此项对应的键
			i++;
			// entry.getValue() 返回与此项对应的值
		}
		// 设置dropDownItem 宽度
		xnAdapter = new TestArrayAdapter(R.layout.list_item,
				getApplicationContext(), xns, width);// 配置Adapter
		String xq = StaticVarUtil.list_Rank_xnAndXq.get(xns[0]);// 默认第一学年的首个学期数组
		String[] xqs = xq.split("\\,");
		xqAdapter = new TestArrayAdapter(R.layout.list_item,
				getApplicationContext(), xqs, width);// 配置Adapter
		xnAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 选择下拉菜单样式
		xqAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // 选择下拉菜单样式
		xnSpinner.setAdapter(xnAdapter);
		xnSpinner.setSelection(0, false);// 这两个方法真变态
		xqSpinner.setAdapter(xqAdapter);
		xqSpinner.setSelection(0, false);
		listener(xns, width);
		String result = "";// 成绩的数据

		if (allRankMap.size() != 0) {
			for (String xnAndXq : allRankMap.keySet()) {// 由于是 menu触发的所以必须判断
				if (xnAndXq.equals(selectXn + selectXq)) {// 如果存在则直接 或者value
					result = allRankMap.get(xnAndXq);
					refeshRank(result, isFirstListView);
					break;
				}
			}
		} else {
			requestRankAsyntask();
		}
	}

	private void findviewById() {
		// TODO Auto-generated method stub
		allRankList = (CustomRankListView) findViewById(R.id.allRank);
		rankScoreText = (TextView) findViewById(R.id.score);
		search_edittext = (AutoCompleteTextView) findViewById(R.id.search);
		rankText = (TextView) findViewById(R.id.rank);
		nameText = (TextView) findViewById(R.id.name);
	}

	/**
	 * 取消软键盘
	 */
	private void closeInputMethod() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isOpen = imm.isActive();
		if (isOpen) {
			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 没有显示则显示
			// imm.hideSoftInputFromWindow(mobile_topup_num.getWindowToken(),
			// InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void onBackPressed() {
		ViewUtil.cancelToast();
		super.onBackPressed();
	}

	/**
	 * 下拉刷新
	 */
	@SuppressWarnings("unused")
	private void rankListViewListener() {
		// TODO Auto-generated method stub
		// 创建FootView
		final View footer = View.inflate(getApplicationContext(),
				R.layout.rank_footer, null);
		allRankList.setOnAddFootListener(new OnAddFootListener() {
			@Override
			public void addFoot() {
				allRankList.addFooterView(footer);
			}
		});
		allRankList.setOnFootLoadingListener(new OnFootLoadingListener() {
			@Override
			public void onFootLoading() {
				new AsyncTask<Void, Void, ArrayList<HashMap<String, Object>>>() {
					@Override
					protected ArrayList<HashMap<String, Object>> doInBackground(
							Void... params) {
						try {
							// 模拟从服务器获取数据的过程
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						// 再次读取10行数据
						ArrayList<HashMap<String, Object>> virtualData = new ArrayList<HashMap<String, Object>>();
						for (int i = lsitItemSum; i < allRankArrayList.size(); i++) {
							virtualData.add(allRankArrayList.get(i));
							lsitItemSum += 1;
						}
						// 设置新的大小
						return virtualData;
					}

					// 在doInBackground后面执行
					@Override
					protected void onPostExecute(
							ArrayList<HashMap<String, Object>> result) {
						showRankArrayList.addAll(result);// 这个是往后添加数据
						simpleAdapter.notifyDataSetChanged();
						allRankList.onFootLoadingComplete();// 完成上拉刷新,就是底部加载完毕,这个方法要调用
						// 移除footer,这个动作不能少
						allRankList.removeFooterView(footer);
						super.onPostExecute(result);
					}
				}.execute();
			}
		});
	}

	private void requestRankAsyntask() {
		// 默认
		selectXn = xnSpinner.getSelectedItem().toString();
		selectXq = xqSpinner.getSelectedItem().toString();
		rankRequestParmas(StaticVarUtil.student.getAccount() + "|"
				+ selectXn.split("\\-")[0] + "|"
				+ (selectXq.equals("第一学期") ? "1" : "2"));
		String result = "";
		// 首先查询内存中是否有该学期成绩
		for (String xnAndXq : allRankMap.keySet()) {
			if (xnAndXq.equals(selectXn + selectXq)) {// 如果存在则直接 或者value
				result = allRankMap.get(xnAndXq);
				refeshRank(result, isFirstListView);
				break;
			}
		}
		if (result.equals("")) {// 不存在，则请求
			GetRankAsyntask getRankAsyntask = new GetRankAsyntask();
			dialog.show();
			getRankAsyntask.execute();
		}

	}

	@SuppressWarnings("deprecation")
	private void rankRequestParmas(String data) {
		long time = System.currentTimeMillis();
		// String s = new char[]{3,2,3,4,3,8,3,8,3,2,3,2}.toString();
		try {
			String time_s = Passport.jiami(String.valueOf(time),
					String.valueOf(new char[] { 2, 4, 8, 8, 2, 2 }));
			String realData = Passport.jiami(data, String.valueOf(time));
			@SuppressWarnings("static-access")
			String imei = ((TelephonyManager) getApplicationContext()
					.getSystemService(getApplicationContext().TELEPHONY_SERVICE))
					.getDeviceId();
			imei = Passport.jiami(imei, String.valueOf(time));
			realData = URLEncoder.encode(realData);
			time_s = URLEncoder.encode(time_s);
			StaticVarUtil.data = realData;
			StaticVarUtil.viewstate = time_s;
			StaticVarUtil.content = imei;
			// 验证data ，因为服务器中出现错误
			String checkData = Util.checkRankRequestData(realData, time_s);
			if (!checkData.equals(data)) {
				rankRequestParmas(data);// 递归再次计算，直到计算出正确的
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	boolean isTouchXNSpinner = false;
	boolean isTouchXQSpinner = false;

	@SuppressLint("ClickableViewAccessibility")
	private void listener(final String[] xns, final int width) {
		// TODO Auto-generated method stub

		xnSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (isTouchXNSpinner) {
							lsitItemSum = DEFAULTITEMSUM;// 设置为默认
							// 将spinner上的选择答案显示在TextView上面
							selectXn = xnAdapter.getItem(arg2);
							// 自动适配 学期
							String xq = StaticVarUtil.list_Rank_xnAndXq
									.get(xns[arg2]);// 默认第一学年的首个学期数组
							String[] xqs = xq.split("\\,");
							xqAdapter = new TestArrayAdapter(
									R.layout.list_item,
									getApplicationContext(), xqs, width);// 配置Adapter
							xqSpinner.setAdapter(xqAdapter);
							requestRankAsyntask();
							isTouchXNSpinner = false;
						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});
		xnSpinner.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				isTouchXNSpinner = true;
				return false;
			}
		});
		xqSpinner
				.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {

					public void onItemSelected(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						// TODO Auto-generated method stub
						if (isTouchXQSpinner) {
							lsitItemSum = DEFAULTITEMSUM;// 设置为默认
							// 将spinner上的选择答案显示在TextView上面
							selectXq = xqAdapter.getItem(arg2);
							requestRankAsyntask();
							isTouchXQSpinner = false;
						}
					}

					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}

				});
		xqSpinner.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub

				isTouchXQSpinner = true;

				return false;
			}
		});
	}

	// 好友列表
	protected void friend_list() {
		// TODO Auto-generated method stub
		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});

		// 添加好友按钮
		Button add_friend = (Button) findViewById(R.id.add_friend);
		add_friend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(MainActivity.this, AddFriendListActivity.class);
				startActivity(i);
			}
		});

	}

	/**
	 * 修改个人信息，只能修改昵称，密码，头像
	 */
	protected void menuSetting() {

		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});

		if (StaticVarUtil.student == null) {
			ViewUtil.showToast(getApplicationContext(), "对不起，查看选项请先登录");
			return;
		}
		EditText etAccount = (EditText) findViewById(R.id.etAccount);
		etAccount.setText(StaticVarUtil.student.getAccount() + "");// 修改多显示一个0的问题
		etAccount.setEnabled(false);// 不可用
		final EditText etPassword1 = (EditText) findViewById(R.id.etPassword1);
		final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
		final EditText cofPassword2 = (EditText) findViewById(R.id.corfimPassword2);// 确认密码
		// 修改
		Button butAlter = (Button) findViewById(R.id.butAlter);
		butAlter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 控件值
				String password1 = etPassword1.getText().toString();
				String password2 = etPassword2.getText().toString().trim();
				String password3 = cofPassword2.getText().toString().trim();
				if (password1.equals("") && password2.equals("")
						&& bitmap == null && password3.equals("")) {
					ViewUtil.showToast(getApplicationContext(), "您没有信息需要修改");
					return;
				}

				// 密码
				if (password1.equals("") && password2.equals("")) {// 如果不修改

				} else {
					if (password1.equals(StaticVarUtil.student.getPassword())
							&& password2.equals("") == false) {
					} else {
						ViewUtil.showToast(getApplicationContext(),
								"旧密码不正确或者新密码不能为空,请您检查");
						return;
					}
				}
				/*
				 * File file = null; // 头像 if (bitmap != null) { file = new
				 * File(StaticVarUtil.PATH + "/headPhoto.JPEG"); } String
				 * account = StaticVarUtil.student.getAccount() + ""; // 修改
				 * alertStudent(account, password, file);
				 */

				if (password2.equals(password3)) {
					if (!Util.hasDigitAndNum(password2)) {
						ViewUtil.showToast(getApplicationContext(),
								"密码中必须包含数字和字母");
					} else {
						if (password2.length() < 6) {// 增加修改密码必须超过6位
							ViewUtil.showToast(getApplicationContext(),
									"密码必须超过6位");
						} else {
							ChangePwAsyntask changePwAsyntask = new ChangePwAsyntask();
							changePwAsyntask.execute(new String[] { password1,
									password2 });
						}

					}

				} else {
					ViewUtil.showToast(getApplicationContext(), "新密码不正确");

					return;
				}

			}
		});

	}

	/**
	 * 选择头像
	 * 
	 * @return
	 */
	protected void chooseHeadPhoto() {
		String[] items = new String[] { "选择本地图片", "拍照" };
		new AlertDialog.Builder(this)
				.setTitle("设置头像")
				.setItems(items, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:// 选择本地图片
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, PIC);
							break;
						case 1:// 拍照
							Intent intent2 = new Intent(
									MediaStore.ACTION_IMAGE_CAPTURE);
							Uri imageUri = Uri.fromFile(new File(
									StaticVarUtil.PATH, "temp.JPEG"));
							// 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
							intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
							startActivityForResult(intent2, PHO);
							break;
						}
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).show();
	}

	/**
	 * 取得回传的数据
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 如果结果码不是取消的时候
		if (resultCode != RESULT_CANCELED) {
			switch (requestCode) {
			case PHO:
				File tempFile = new File(StaticVarUtil.PATH + "/temp.JPEG");
				startPhotoZoom(Uri.fromFile(tempFile));
				break;
			case PIC:
				// 照片的原始资源地址
				Uri originalUri = data.getData();
				startPhotoZoom(originalUri);
				break;
			case RESULT:
				if (data != null) {
					Bundle extras = data.getExtras();
					if (extras != null) {
						bitmap = extras.getParcelable("data");
					}
					bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 240);// 把它变为240像素的图片
					BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
							StaticVarUtil.student.getAccount() + ".JPEG");
					headPhoto.setImageBitmap(bitmap);
					// 上传头像
					UploadFileAsytask uploadFileAsytask = new UploadFileAsytask();
					uploadFileAsytask.execute(new String[] { StaticVarUtil.PATH
							+ "/" + StaticVarUtil.student.getAccount()
							+ ".JPEG" });
				}
				break;
			default:
				break;
			}
		} else {
			bitmap = null;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 裁剪图片方法实现
	 * 
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {

		Intent intent = new Intent("com.android.camera.action.CROP");// 调用系统的截图功能。
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		intent.putExtra("return-data", true);
		startActivityForResult(intent, RESULT);
	}

	/**
	 * 判断是否是正确选择大学，-1代表出错，0代表所有，其他数字代表不同大学
	 */
	private int judgeCollegeId() {
		int collegeId = -1;
		try {
			// 如果是所有大学，直接返回0
			if (chooseCollege.getText().toString().equals("所有大学")) {
				return 0;// 直接返回所有大学
			}
			// 如果是某一大学，返回Id
			collegeId = Integer.parseInt(ToolClass.nameIdTreeMap
					.get(chooseCollege.getText().toString()));
		} catch (Exception e) {
			// 出错返回-1
			System.out.println("没有这个大学");
			collegeId = -1;
		}
		return collegeId;
	}

	/**
	 * 选择高校，高校中有选择历史
	 */
	protected void chooseCollege() {
		LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		View view = inflater.inflate(R.layout.choose_school, null);

		// 显示大学历史adapterHistory.notifyDataSetChanged();
		ArrayList<CharSequence> allCollege = readHistory2();
		HistoryCollege.initData(allCollege);// 初始化数据源
		ChooseHistorySchoolExpandAdapter adapterHistory = new ChooseHistorySchoolExpandAdapter(
				MainActivity.this);
		ExpandableListView expandHistory = (ExpandableListView) view
				.findViewById(R.id.expandHistory);
		expandHistory.setAdapter(adapterHistory);

		// 找到控件expandListView
		ExpandableListView expandListView = (ExpandableListView) view
				.findViewById(R.id.expandListView);
		expandListView.setAdapter(adapter);

		final Dialog dialog = new AlertDialog.Builder(MainActivity.this)
				.setView(view).create();
		dialog.setCancelable(true);
		dialog.show();

		// 选择大学监听
		expandListView.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String schoolName = ToolClass.schoolsList.get(groupPosition)
						.get(childPosition).toString();
				dialog.cancel();
				chooseCollege.setText(schoolName);
				return true;
			}
		});

		// 选择历史监听
		expandHistory.setOnChildClickListener(new OnChildClickListener() {
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String schoolName = HistoryCollege.allHistory
						.get(groupPosition).get(childPosition).toString();
				dialog.cancel();
				chooseCollege.setText(schoolName);
				return true;
			}
		});
	}

	private String new_version;// 最新版本
	private String update_content;// 更新内容
	private static String apk_url;// 下载地址
	private Button download_version;// 下载版本
	private Button cancle_check;// 取消

	/**
	 * 刷新
	 * 
	 * @param messageSort
	 *            0代表近期关注1代表七日关注2代表风云榜
	 */
	@SuppressLint("SimpleDateFormat")
	private void refresh(int collegeId, int messageKind, int messageSort) {
		// 保存搜索历史，保存搜索的学校（每次都保存），种类，排序方式（只保存当前这次，为了再次进入时候直接显示）
		saveHistory();
		// 开启新线程
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 6);
		StaticVarUtil.fileName = "jsonCache.txt";// 设置保存文件的名字
	}

	/**
	 * 保存过往历史
	 */
	private void saveHistory() {
		SharedPreferences share = getSharedPreferences("history", MODE_PRIVATE);
		Editor editor = share.edit();
		editor.putString("msgKind", chooseMsgKind.getText().toString());// 用于下次登录显示
		editor.putString("msgSort", chooseMsgSort.getText().toString());// 用于下次登录显示
		editor.putString("theLastCollege", chooseCollege.getText().toString());// 用于下次登录显示

		// 用于选择大学显示
		HashSet<String> set = (HashSet<String>) share.getStringSet("college",
				new HashSet<String>());
		if (judgeCollegeId() > 0) {
			if (!set.contains(chooseCollege.getText().toString())) {// 如果不包含就加入
				set.add(chooseCollege.getText().toString());
				System.out.print("加入历史：" + chooseCollege.getText().toString());
			}
		}
		editor.putStringSet("college", set);// 存入进去
		editor.commit();// 同步更改硬盘内容
	}

	/**
	 * 读取历史记录进行显示过往选择过的大学
	 */
	private ArrayList<CharSequence> readHistory2() {
		SharedPreferences share = getSharedPreferences("history", MODE_PRIVATE);
		// 显示最近一次记录,没有显示默认
		HashSet<String> set = (HashSet<String>) share.getStringSet("college",
				null);
		ArrayList<CharSequence> allHistory = null;// 要返回的数据
		if (set != null) {// 不为空的话，进行数据的显示
			allHistory = new ArrayList<CharSequence>();
			for (String string : set) {
				allHistory.add(string);
			}
		}
		return allHistory;
	}

	/**
	 * 对手机按钮的监听
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:// 如果是返回按钮,退出
			if (getCurrentMeunItem() != 1) {// 不在第一个页面,返回第一个页面
				menuBang.setPressed(true);// 初始化默认是风云榜被按下
				setCurrentMenuItem(1);// 记录当前选项位置
				slidingMenu.setContent(R.layout.card_main);
				menu1();
			} else
				quit(false);

			break;
		case KeyEvent.KEYCODE_MENU:// 如果是菜单按钮
			slidingMenu.toggle();
			break;
		default:
			break;
		}
		return true;
	}

	/**
	 * 清除内存块中的共享数据
	 */
	private void deleteCatch() {
		StaticVarUtil.list_Rank_xnAndXq.clear();
		allRankArrayList = null;
		mapScoreOne = null;
		mapScoreTwo = null;
		StaticVarUtil.quit();
		isFirstListView = true;
		// 清空成绩缓存
		isFirst = true;
	}

	/**
	 * 退出模块
	 * 
	 * @param logout
	 *            是否注销
	 */
	private void quit(final boolean logout) {
		Builder builder = new AlertDialog.Builder(MainActivity.this);

		if (logout) {
			builder.setMessage("你确定要注销吗？");
		} else {
			builder.setMessage("你确定要退出吗？");
		}
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				deleteCatch();
				LogcatHelper.getInstance(MainActivity.this).stop();
				if (logout) {
					Intent i = new Intent();
					i.setClass(getApplicationContext(), LoginActivity.class);
					startActivity(i);
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		Dialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 记录设置当前MenuItem的位置，1，2，3，4，5分别代表成绩查询，补考查询，我的排名，我收藏的，选项
	 * 
	 * @param menuItem
	 *            菜单的选项
	 */
	private void setCurrentMenuItem(int menuItem) {
		SharedPreferences preferences = getSharedPreferences("currentMenuItem",
				MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putInt("item", menuItem);
		editor.commit();
	}

	/**
	 * 取得当前MenuItem的位置
	 * 
	 * @return 当前的menu的菜单项 1，2，3，4，5分别代表成绩查询，补考查询，我的排名，我收藏的，选项,0代表没有这个
	 */
	private int getCurrentMeunItem() {
		SharedPreferences preferences = getSharedPreferences("currentMenuItem",
				MODE_PRIVATE);
		int flag = preferences.getInt("item", 0);
		return flag;
	}

	// 异步加载获取成绩
	class GetScoreAsyntask extends AsyncTask<Object, String, String> {

		@Override
		protected String doInBackground(Object... params) {
			// TODO Auto-generated method stub
			String url = "";
			String canshu = Util.getURL(StaticVarUtil.QUERY_SCORE);
			String[] can = canshu.split("&");
			String url_str = can[0];
			String xm = can[1];
			name = xm.split("=")[1];
			String gnmkdm = can[2];
			try {
				url = HttpUtilMc.BASE_URL
						+ "xscjcx.aspx?session="
						+ StaticVarUtil.session
						+ "&url="
						+ url_str
						+ "&xm="
						+ URLEncoder.encode(
								URLEncoder.encode(xm.split("=")[1], "utf8"),
								"utf8") + "&" + gnmkdm;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("url" + url);
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			System.out.println("=========================  " + result);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// 显示用户名
			nickname.setText(name);
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("error")) {
						/**
						 * 将字符串 写入xml文件中
						 */
						if (!result.equals("no_evaluation")) {
							score_json = result;
							listItem = new ArrayList<HashMap<String, Object>>();
							System.out.println("rrrr:" + result);
							JSONObject jsonObject = new JSONObject(result);
							JSONArray jsonArray = (JSONArray) jsonObject
									.get("liScoreModels");// 最外层的array
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject o = (JSONObject) jsonArray.get(i);
								HashMap<String, Object> map = new HashMap<String, Object>();
								map.put("xn", o.get("xn"));
								map.put("list_xueKeScore",
										o.get("list_xueKeScore"));
								listItem.add(map);
							}
						} else {
							Builder builder = new AlertDialog.Builder(
									MainActivity.this);

							builder.setMessage("请到官网进行教师评价后查询成绩！");

							builder.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											quit(true);
										}
									});
							builder.create().show();
						}
						menu1();
						dialog.cancel();
					} else {
						ViewUtil.showToast(getApplicationContext(), "查询失败");
					}

				} else {
					ViewUtil.showToast(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	/**
	 * 获取 成绩排名
	 * 
	 * @author Administrator 2015-2-8
	 */
	class GetRankAsyntask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "";
			url = HttpUtilMc.BASE_URL + "RankServlet.jsp?data="
					+ StaticVarUtil.data + "&viewstate="
					+ StaticVarUtil.viewstate + "&content="
					+ StaticVarUtil.content;
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			dialog.cancel();
			nickname.setText(name);
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("error")) {
						/**
						 * 将字符串 写入xml文件中
						 */
						if (!result.equals("")) {
							refeshRank(result, isFirstListView);
							allRankMap.put(selectXn + selectXq, result);// 将数据保存到内存中，下次就不用重复获取。
						}
					} else {
						ViewUtil.showToast(getApplicationContext(), "查询失败");
					}
				} else {
					ViewUtil.showToast(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	/**
	 * 
	 * @param result
	 *            成绩字符串
	 * @param isFirst
	 *            是否是第一次
	 */
	private void refeshRank(String result, boolean isFirst) {
		try {
			if (allRankArrayList == null & showRankArrayList == null) {
				allRankArrayList = new ArrayList<HashMap<String, Object>>();
				showRankArrayList = new ArrayList<HashMap<String, Object>>();
			} else {
				allRankArrayList.clear();
				showRankArrayList.clear();
			}

			JSONObject jsonObject = new JSONObject(result);
			System.out.println("result::" + result);
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
				allRankArrayList.add(map);
				if (String.valueOf(rankId).equals(rank)) {
					rankScoreText.setText(o.get("score").toString());// 显示成绩
					Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
							R.anim.textscore_translate);
					rankScoreText.setAnimation(animation);
				}
			}
			// 获取 之前求得的固定 个数的item。 防止数据量太大，而导致的将所有数据都显示出来。
			for (int i = 0; i < (lsitItemSum > allRankArrayList.size() ? allRankArrayList
					.size() : lsitItemSum); i++) {
				showRankArrayList.add(allRankArrayList.get(i));
			}
			if (isFirst) {
				setListView();
				isFirstListView = false;
			} else {
				simpleAdapter.notifyDataSetChanged();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 显示 排名的listview
	private void setListView() {
		// TODO Auto-generated method stub
		if (showRankArrayList.size() < DEFAULTITEMSUM) {
			showShareQrcodeDialog();
		}
		simpleAdapter = new SimpleAdapter(getApplicationContext(),
				showRankArrayList, R.layout.allrank_listitem, new String[] {
						"rankId", "name", "score" }, new int[] { R.id.rankId,
						R.id.name, R.id.score });
		allRankList.setAdapter(simpleAdapter);

	}

	private void showShareQrcodeDialog() {
		Builder builder = new AlertDialog.Builder(MainActivity.this);

		builder.setMessage("由于本专业使用人数较少,因此排名会有误差。\n若需查询准确排名，请分享给同学下载该软件！");

		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				/*
				 * menuAbout.setPressed(true); setCurrentMenuItem(6);//
				 * 记录当前选项位置，并且跳转
				 * slidingMenu.setContent(R.layout.activity_about);
				 * aboutListener();
				 */
				ViewUtil.showShare(getApplicationContext());
			}
		});
		builder.create().show();
	}

	// 异步改变密码
	class ChangePwAsyntask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "";
			String canshu = Util.getURL(StaticVarUtil.CHANGE_PW);
			/*
			 * String[] can = canshu.split("&"); String url_str = can[0]; String
			 * gnmkdm = can[1];
			 */
			url = HttpUtilMc.BASE_URL + "changepw.jsp?session="
					+ StaticVarUtil.session + "&url=" + canshu
					+ "&old_password=" + params[0] + "&new_password="
					+ params[1];
			System.out.println("url" + url);
			// 查询返回结果
			String result = HttpUtilMc.queryStringForPost(url);
			System.out.println("=========================  " + result);
			return result;

		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			// progress.cancel();
			// 显示用户名
			nickname.setText(name);
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					ViewUtil.showToast(getApplicationContext(),
							!result.equals("error") ? "修改成功,请重新登录" : "修改不成功");
					if (!result.equals("error")) {
						quit(true);// 注销重新登录
					}
				} else {
					ViewUtil.showToast(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION);
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	// 从网络获取头像
	class GetPicture extends AsyncTask<String, Bitmap, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			// TODO Auto-generated method stub
			return Util.getBitmap(params[0]);
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// TODO Auto-generated method stub
			super.onPostExecute(bitmap);
			if (bitmap != null) {
				headPhoto.setImageBitmap(bitmap);// 显示图片
				Util.saveBitmap2file(bitmap, StaticVarUtil.student.getAccount());
			}

		}

	}

	// 异步检测版本
	class CheckVersionAsyntask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpUtilMc.queryStringForPost(HttpUtilMc.BASE_URL
					+ "checkversion.jsp?version="
					+ Util.getVersion(getApplicationContext()));
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (is_show) {
				dialog.cancel();
			}
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("no")) {// 有最新版本
						String[] str = result.split("\\|");
						apk_url = str[0];
						new_version = str[1];
						update_content = str[2];
						VersionUpdate versionUpdate = new VersionUpdate(
								MainActivity.this);
						versionUpdate.apkUrl = HttpUtilMc.IP + apk_url;
						versionUpdate.updateMsg = new_version + "\n\n"
								+ update_content;
						versionUpdate.checkUpdateInfo();
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}
		}
	}

	// 检测版本
	@SuppressWarnings({ "unused", "deprecation" })
	private void check_version_showWindow(View parent) {

		if (version_popupWindow == null) {
			LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			view = layoutInflater.inflate(R.layout.check_apk_version, null);
			update_content_textview = (TextView) view
					.findViewById(R.id.update_content);
			download_version = (Button) view
					.findViewById(R.id.download_version);
			cancle_check = (Button) view.findViewById(R.id.cancle_check);
			// 创建一个PopuWidow对象
			version_popupWindow = new PopupWindow(view, getWindowManager()
					.getDefaultDisplay().getWidth(), 330);
		}
		// 使其聚集
		version_popupWindow.setFocusable(true);
		// 设置允许在外点击消失
		version_popupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		version_popupWindow.setBackgroundDrawable(new BitmapDrawable());
		// 设置弹出动画
		// popupWindow.setAnimationStyle(R.anim.push_bottom_out);
		version_popupWindow.setAnimationStyle(R.style.mystyle);
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		// 显示的位置为:屏幕的宽度的一半-PopupWindow的高度的一半
		/*
		 * int xPos = windowManager.getDefaultDisplay().getWidth() / 2 -
		 * popupWindow.getWidth() / 2;
		 */
		int xPos = windowManager.getDefaultDisplay().getHeight() / 2
				- version_popupWindow.getHeight() / 2;
		Log.i("coder", "xPos:" + xPos);

		version_popupWindow.showAsDropDown(parent,
				windowManager.getDefaultDisplay().getHeight()
						- version_popupWindow.getHeight(),
				windowManager.getDefaultDisplay().getHeight()
						- version_popupWindow.getHeight() - 74);
		update_content_textview.setText("最近版本:" + new_version + "\n"
				+ update_content);
		// 检测更新，下载软件
		download_version.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				version_popupWindow.dismiss();
				// 更新版本
				VersionUpdate versionUpdate = new VersionUpdate(
						MainActivity.this);
				versionUpdate.apkUrl = apk_url;
				versionUpdate.checkUpdateInfo();
			}
		});
		cancle_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				version_popupWindow.dismiss();
				// version_popupWindow = null;
			}
		});
	}

	/**
	 * 上传头像
	 * 
	 * @author mc 2014-4-28
	 */
	class UploadFileAsytask extends AsyncTask<String, String, String> {
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpAssist.uploadFile(new File(params[0]),
					StaticVarUtil.student.getAccount());
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				ViewUtil.showToast(
						getApplicationContext(),
						!HttpUtilMc.CONNECT_EXCEPTION.equals(result) ? !result
								.equals("error") ? "修改成功" : "修改失败"
								: HttpUtilMc.CONNECT_EXCEPTION);
			} catch (Exception e) {
				Log.i("LoginActivity", e.toString());
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		allRankMap = new HashMap<String, String>();
		showRankArrayList = new ArrayList<HashMap<String, Object>>();
		allRankArrayList = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		allRankMap = new HashMap<String, String>();
		showRankArrayList = new ArrayList<HashMap<String, Object>>();
		allRankArrayList = new ArrayList<HashMap<String, Object>>();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		allRankMap = new HashMap<String, String>();
		showRankArrayList = new ArrayList<HashMap<String, Object>>();
		allRankArrayList = new ArrayList<HashMap<String, Object>>();
	}

}
