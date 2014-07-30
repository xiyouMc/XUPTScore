package com.xy.fy.main;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

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
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cardsui.example.MyCard;
import com.cardsui.example.MyPlayCard;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.mc.util.HttpUtilMc;
import com.mc.util.Util;
import com.mc.util.VersionUpdate;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.xy.fy.adapter.ChooseHistorySchoolExpandAdapter;
import com.xy.fy.adapter.ChooseSchoolExpandAdapter;
import com.xy.fy.adapter.ListViewAdapter;
import com.xy.fy.util.BitmapUtil;
import com.xy.fy.util.DownLoadThread;
import com.xy.fy.util.HttpUtil;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.HistoryCollege;
import com.xy.fy.view.ToolClass;

@SuppressLint("HandlerLeak")
/**
 * 第一次启动主界面就请求  查询成绩
 * @author Administrator
 * 2014-7-21
 */
public class MainActivity extends Activity {

	// 检测版本
	private static PopupWindow popupWindow;
	private static PopupWindow version_popupWindow;
	private View view;
	private TextView update_content_textview;

	public static SlidingMenu slidingMenu;// 侧滑组件
	private Button refresh;// 刷新按钮
	private Button chooseCollege;// 选择学校按钮
	private Button chooseMsgKind;// 选择说说种类
	private Button chooseMsgSort;// 选择说说排序方式
	private ProgressBar progress;// 刷新时候，刷新按钮变为progress
	// private CustomListView listView;// 说说列表，自己定义的ListView
	private CardUI mCardView;
	private TextView nickname;// 用户名
	private String name;
	private LinearLayout menuBang = null;// 成绩查询
	private LinearLayout menuMyBukao = null;// 补考查询
	private LinearLayout menuMyPaiming = null;// 我的排名
	private LinearLayout menuMyCollect = null;// 我收藏的
	private LinearLayout menuSetting = null;// 设置
	private LinearLayout menuAbout = null;// 关于
	private Button check_version = null;
	ArrayList<HashMap<String, Object>> listItem;// json解析之后的列表
	private String score_json;// json数据
	private ListViewAdapter listViewAdapter = null;

	private ProgressDialog dialog = null;

	private Bitmap bitmap = null;// 修改头像

	private static final int PIC = 11;// 图片
	private static final int PHO = 22;// 照相
	private static final int RESULT = 33;// 返回结果

	private int page = 0;

	private ChooseSchoolExpandAdapter adapter = new ChooseSchoolExpandAdapter(
			MainActivity.this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.activity_main);
		dialog = ViewUtil.getProgressDialog(MainActivity.this, "正在查询");

		/*CheckVersionAsyntask checkVersionAsyntask = new CheckVersionAsyntask();
		checkVersionAsyntask.execute();*/
		// 请求 获取 成绩
		GetScoreAsyntask getScoreAsyntask = new GetScoreAsyntask();
		dialog.show();
		getScoreAsyntask.execute();

		setMenuItemListener();

		// 当前Activity进栈
		StaticVarUtil.activities.add(MainActivity.this);

		
		// 找到ID
		slidingMenu = (SlidingMenu) findViewById(R.id.slidingMenu);

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

		// slidingMenu.setOnClosedListener(new OnClosedListener() {
		// @Override
		// public void onClosed() {
		// System.out.println("菜单关闭");
		// menu2();
		// }
		// });
	}

	/**
	 * 第二个菜单项
	 * 
	 * @param title
	 *            题目
	 * @param fileName
	 *            要保存的文件缓存名字
	 */
	private void menuMy(String title, final String fileName,
			final DownLoadThread downLoadThread) {
		// 标题
		Button butMy = (Button) findViewById(R.id.butMy);
		butMy.setText(title);

		// 放在这里，后面的功能不能用
		if (StaticVarUtil.student == null) {
			ViewUtil.toastShort("对不起，请先登录..", MainActivity.this);
			return;
		}

		page = 0;
		// 刷新按钮
		refresh = (Button) findViewById(R.id.butRefresh);
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 刷新的时候
				page = 0;
				// downLoadThread.setPageAndHanlder(page, handler);
				StaticVarUtil.fileName = fileName;
				StaticVarUtil.executorService.submit(downLoadThread);
			}
		});

	}

	/**
	 * 第一个菜单项
	 */
	private void menu1() {
		page = 0;// 点击过来重置
		// 菜单按钮
		Button menu = (Button) findViewById(R.id.butMenu);
		menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				slidingMenu.toggle();
			}
		});
		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		CardStack stack2 = new CardStack();
		stack2.setTitle("REGULAR CARDS");
		mCardView.addStack(stack2);

		// add AndroidViews Cards
		mCardView.addCard(new MyCard("By Mc"));// 定义卡片 在这块可以设置 学期
		mCardView.addCardToLastStack(new MyCard("for Xiyou"));
		MyCard androidViewsCard = new MyCard("www.xiyoumobile.com");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(Intent.ACTION_VIEW, Uri
						.parse("www.xiyoumobile.com"));
				it.setClassName("com.android.browser",
						"com.android.browser.BrowserActivity");
				startActivity(it);

			}
		});
		androidViewsCard.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "This is a long click",
						Toast.LENGTH_SHORT).show();
				return true;
			}

		});
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("www.xiyoumobile.com"));

		mCardView.addCardToLastStack(androidViewsCard);

		Resources resources = getResources();
		String[] xn = resources.getStringArray(R.array.xn);
		for (int i = 0; i < xn.length; i++) {
			showCard(xn[i]);
		}
		// draw cards
		mCardView.refresh();

	}

	/**
	 * 显示卡片
	 */
	private void showCard(String xn) {

		// add one card, and then add another one to the last stack.
		String first_score = getScore(xn, "1") == null ? "" : getScore(xn, "1")
				.toString();
		if (!first_score.equals("")) {
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

		String second_score = getScore(xn, "2") == null ? ""
				: getScore(xn, "2").toString();
		if (!second_score.equals("")) {
			MyPlayCard myCard = new MyPlayCard("第二学期", second_score, "#e00707",
					"#e00707", false, true);
			String[][] second_score_array = getScoreToArray(second_score);
			myCard.setOnClickListener(new ScoreClass(second_score_array.length,
					second_score_array, xn + " 第二学期"));
			mCardView.addCardToLastStack(myCard);
		}

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
	 * 改变字体颜色
	 * 
	 * @param str
	 * @return
	 */
	private String change_color(String str) {
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		// SpannableStringBuilder实现CharSequence接口
		style.setSpan(new ForegroundColorSpan(Color.RED), 0, str.length(),
				Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		/*
		 * style.setSpan(new ForegroundColorSpan(Color.YELLOW), 2,
		 * 4,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE ); style.setSpan(new
		 * ForegroundColorSpan(Color.GREEN), 4,
		 * 6,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
		 */
		return style.toString();
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
										: change_color(jsonObject2.get("kcmc")
												.toString()));
								// result.append(jsonObject2.get("kcxz")==null?" ":"--"+change_color(jsonObject2.get("kcxz").toString()));
								result.append(/* jsonObject2.get("cj")==null?" ": */"--"
										+ change_color(jsonObject2.get("cj")
												.toString()));
								// result.append(jsonObject2.get("xf")==null?" ":"--"+change_color(jsonObject2.get("xf").toString()));
								result.append(jsonObject2.get("pscj")
										.equals("") ? "/" : "--"
										+ change_color(jsonObject2.get("pscj")
												.toString()));
								result.append(jsonObject2.get("qmcj")
										.equals("") ? "/" : "--"
										+ change_color(jsonObject2.get("qmcj")
												.toString()));
								// result.append(jsonObject2.get("xymc")==null?" ":"--"+change_color(jsonObject2.get("xymc").toString()));
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

		nickname = (TextView) findViewById(R.id.nickname);// 用户名
		menuBang = (LinearLayout) findViewById(R.id.menu_bang);// 1.成绩查询
		menuMyBukao = (LinearLayout) findViewById(R.id.menu_my_bukao);// 2.补考查询
		menuMyPaiming = (LinearLayout) findViewById(R.id.menu_my_paiming);// 3.我的排名
		menuMyCollect = (LinearLayout) findViewById(R.id.menu_my_collect);// 4.我收藏的
		menuSetting = (LinearLayout) findViewById(R.id.menu_setting);// 5.设置
		menuAbout = (LinearLayout) findViewById(R.id.menu_about);

		LinearLayout menuQuit = (LinearLayout) findViewById(R.id.menu_quit);
		menuBang.setPressed(true);// 初始化默认是风云榜被按下
		setCurrentMenuItem(1);// 记录当前选项位置

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

		menuMyBukao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "程序猿们正在努力开发中，请持续关注...",
						2000).show();
			}
		});

		menuMyPaiming.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "程序猿们正在努力开发中，请持续关注...",
						2000).show();
			}
		});

		menuMyCollect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getApplicationContext(), "程序猿们正在努力开发中，请持续关注...",
						2000).show();
				/*
				 * setMenuItemState(menuBang, false, menuMyBukao, false,
				 * menuMyPaiming, false, menuMyCollect, true, menuSetting,
				 * false, menuAbout, false); setCurrentMenuItem(4);//
				 * 记录当前选项位置，并且跳转 slidingMenu.toggle();// 页面跳转
				 * 
				 * slidingMenu.setContent(R.layout.activity_my);
				 * StaticVarUtil.fileName = "jsonCacheMyCollect.txt";
				 * 
				 * progress = (ProgressBar) findViewById(R.id.proRefresh); //
				 * 菜单按钮 Button menu = (Button) findViewById(R.id.butMenu);
				 * menu.setOnClickListener(new OnClickListener() {
				 * 
				 * @Override public void onClick(View v) { slidingMenu.toggle();
				 * } });
				 * 
				 * // 不能是游客 if (StaticVarUtil.student == null) {
				 * ViewUtil.toastShort("请先登录，然后再查看...", MainActivity.this);
				 * return; }
				 * 
				 * menuMy("我收藏的", StaticVarUtil.fileName, new DownLoadThread(
				 * StaticVarUtil.student.getAccount(),
				 * HttpUtil.MY_COLLECT_MESSAGE));
				 */
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

				// 菜单按钮
				Button menu = (Button) findViewById(R.id.butMenu);
				menu.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						slidingMenu.toggle();
					}
				});
				
				check_version = (Button)findViewById(R.id.checkversion);//检测新版本按钮
				TextView version = (TextView) findViewById(R.id.version);
				version.setText(Util.getVersion(getApplicationContext()));
				check_version.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub

						CheckVersionAsyntask checkVersionAsyntask = new CheckVersionAsyntask();
						dialog.show();
						checkVersionAsyntask.execute();
					}
				});
			}
		});

		menuQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				quit(true);
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
			ViewUtil.toastShort("对不起，查看选项请先登录", MainActivity.this);
			return;
		}

	
		EditText etAccount = (EditText) findViewById(R.id.etAccount);
		etAccount.setText("0" + StaticVarUtil.student.getAccount() + "");
		etAccount.setEnabled(false);// 不可用
		final EditText etPassword1 = (EditText) findViewById(R.id.etPassword1);
		final EditText etPassword2 = (EditText) findViewById(R.id.etPassword2);
		final EditText cofPassword2 = (EditText) findViewById(R.id.corfimPassword2);// 确认密码
		// 修改
		Button butAlter = (Button) findViewById(R.id.butAlter);
		butAlter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 要传递的参数
				String password = null;
				// 控件值
				String password1 = etPassword1.getText().toString();
				String password2 = etPassword2.getText().toString().trim();
				String password3 = cofPassword2.getText().toString().trim();
				if (password1.equals("") && password2.equals("")
						&& bitmap == null && password3.equals("")) {
					ViewUtil.toastShort("您没有信息需要修改", MainActivity.this);
					return;
				}

				// 密码
				if (password1.equals("") && password2.equals("")) {// 如果不修改
					password = StaticVarUtil.student.getPassword();
				} else {
					if (password1.equals(StaticVarUtil.student.getPassword())
							&& password2.equals("") == false) {
						// 如果旧密码正确，新密码不为空,那么就是新密码
						password = password2;
					} else {
						ViewUtil.toastShort("旧密码不正确或者新密码不能为空,请您检查",
								MainActivity.this);
						return;
					}
				}
				/*
				 * File file = null; // 头像 if (bitmap != null) { file = new
				 * File(StaticVarUtil.PATH + "/headPhoto.JPEG"); } String
				 * account = StaticVarUtil.student.getAccount() + ""; // 修改
				 * //alertStudent(account, password, file);
				 */
				if (password2.equals(password3)) {
					ChangePwAsyntask changePwAsyntask = new ChangePwAsyntask();
					changePwAsyntask.execute(new String[] { password1,
							password2 });
				} else {
					ViewUtil.toastShort("新密码不正确", MainActivity.this);
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
						case 0:
							Intent intent = new Intent();
							intent.setType("image/*");
							intent.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intent, PIC);
							break;
						case 1:
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
					bitmap = BitmapUtil.resizeBitmapWidth(bitmap, 100);// 把它变为100像素的图片
					BitmapUtil.saveBitmapToFile(bitmap, StaticVarUtil.PATH,
							"headPhoto.JPEG");
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

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 320);
		intent.putExtra("outputY", 320);
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

	/**
	 * 设置什么时候可以加载图片
	 */
	OnScrollListener mScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			switch (scrollState) {
			case OnScrollListener.SCROLL_STATE_FLING:
				listViewAdapter.lock();
				break;
			case OnScrollListener.SCROLL_STATE_IDLE:
				listViewAdapter.unlock();
				break;
			case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
				listViewAdapter.unlock();
				break;
			default:
				break;
			}
			listViewAdapter.notifyDataSetChanged();
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {

		}
	};
	private String new_version;// 最新版本
	private String update_content;// 更新内容
	private String apk_url;// 下载地址
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
		// // 测试读取历史文件
		// FileCache fileCache = new FileCache();
		// String dataResource = fileCache.readHistoryJsonData();
		// // 设置adapter
		// listViewAdapter = new ListViewAdapter(dataResource,
		// MainActivity.this);
		// listView.setAdapter(listViewAdapter);
		// 开启新线程
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 6);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		int page = 0;
		StaticVarUtil.fileName = "jsonCache.txt";// 设置保存文件的名字
		/*
		 * StaticVarUtil.executorService.submit(new DownLoadThread(handler,
		 * collegeId, messageKind, messageSort, page, sdf.format(calendar
		 * .getTime())));
		 */
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
				StaticVarUtil.quit();
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

	// 异步加载登录
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
			dialog.cancel();
			// 显示用户名
			nickname.setText(name);
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (!result.equals("error")) {
						/**
						 * 将字符串 写入xml文件中
						 */
						if (!result.equals("")) {
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
							/*
							 * // 获取sdcard根目录 File sdCardDir = Environment
							 * .getExternalStorageDirectory(); // 然后就可以进行下面的步骤
							 * File saveFile = new File(sdCardDir,
							 * "xuptscore/score.xml"); if (!saveFile.exists()) {
							 * new File(sdCardDir, "xuptscore").mkdir();
							 * saveFile.createNewFile(); } FileOutputStream
							 * outStream = new FileOutputStream( saveFile);
							 * outStream.write(result.getBytes());
							 * outStream.close();
							 */
						}
						menu1();
					} else {
						Toast.makeText(getApplicationContext(), "登录失败", 1)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION, 1).show();
					// progress.cancel();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	// 异步加载登录
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
					if (!result.equals("error")) {
						Toast.makeText(getApplicationContext(), "修改成功,请重新登录",
								1000).show();
						// menu1();
						quit(true);// 注销重新登录
					} else {
						Toast.makeText(getApplicationContext(), "修改不成功", 1)
								.show();
					}

				} else {
					Toast.makeText(getApplicationContext(),
							HttpUtilMc.CONNECT_EXCEPTION, 1).show();
					// progress.cancel();
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				Log.i("LoginActivity", e.toString());
			}

		}

	}

	// 异步检测版本
	class CheckVersionAsyntask extends AsyncTask<String, String, String> {

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String url = "";
			/*
			 * String[] can = canshu.split("&"); String url_str = can[0]; String
			 * gnmkdm = can[1];
			 */
			url = HttpUtilMc.BASE_URL + "checkversion.jsp?version="
					+ Util.getVersion(getApplicationContext());
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
			dialog.cancel();
			// 显示用户名
			try {
				if (!HttpUtilMc.CONNECT_EXCEPTION.equals(result)) {
					if (result.equals("no")) {// 已经是最新版本
						Toast.makeText(getApplicationContext(), "已经是最新版本", 1000)
								.show();

					} else {// 有新版本
						String[] str = result.split("\\|");
						apk_url = str[0];
						new_version = str[1];
						update_content = str[2];
						/*check_version_showWindow(new View(
								getApplicationContext()));// 弹出窗口
*/
						VersionUpdate versionUpdate = new VersionUpdate(
								MainActivity.this);
						versionUpdate.apkUrl = HttpUtilMc.IP+apk_url;
						versionUpdate.updateMsg = new_version+"\n\n"+update_content;
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
				// version_popupWindow = null;
				Toast.makeText(getApplicationContext(), apk_url, 1000).show();
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
}
