package com.xy.fy.main;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xy.fy.adapter.CommentAdapter;
import com.xy.fy.singleton.Comment;
import com.xy.fy.singleton.Message;
import com.xy.fy.util.DownLoadThread;
import com.xy.fy.util.ExpressionUtil;
import com.xy.fy.util.ImageDownloader;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;
import com.xy.fy.view.CustomListView;
import com.xy.fy.view.CustomListView.OnMoreButtonListener;

/**
 * 说说详情，大部分是ListViewAdapter中的代码复用，除了ListView那块
 * 
 * @author Administrator
 * 
 */
@SuppressLint("HandlerLeak")
public class MessageDetailActivity extends Activity {

	private ViewHolder holder = null;
	private CustomListView listView = null;
	private Message message = null;
	private CommentAdapter adapter = null;
	private int page = 0;
	private ProgressBar progress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_message_detail);

		message = StaticVarUtil.message;

		String headPhotoUrl = message.getHeadPhoto();
		String text = message.getText();
		String picUrl = message.getSmaPic();

		holder = new ViewHolder();
		holder.collect = (Button) findViewById(R.id.collect);
		holder.comment = (Button) findViewById(R.id.comment);
		holder.date = (TextView) findViewById(R.id.date);
		holder.nickname = (TextView) findViewById(R.id.nickname);
		holder.praise = (Button) findViewById(R.id.praise);
		holder.time = (TextView) findViewById(R.id.time);
		holder.headPhoto = (ImageView) findViewById(R.id.headPhoto);
		holder.pic = (ImageView) findViewById(R.id.pic);
		holder.text = (TextView) findViewById(R.id.text);

		listView = (CustomListView) findViewById(R.id.commentListView);

		progress = (ProgressBar) findViewById(R.id.progress);

		holder.collect.setText(message.getColNum() + "");
		holder.comment.setText(message.getComNum() + "");
		holder.date.setText(message.getDate());
		holder.nickname.setText(message.getNickname());
		holder.praise.setText(message.getPraNum() + "");
		holder.time.setText(message.getTime());

		/**
		 * 返回键
		 */
		Button back = (Button) findViewById(R.id.butBack);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		/**
		 * 头像
		 */
		if (headPhotoUrl != null && headPhotoUrl.equals("") == false) {// 如果不为空，异步加载
			ImageDownloader downloader = ImageDownloader.getInstance(MessageDetailActivity.this);
			holder.headPhoto.setImageBitmap(downloader.getBitmap(holder.headPhoto, headPhotoUrl));
		} else {
			holder.headPhoto.setImageResource(R.drawable.default_head_photo_small);
		}

		/**
		 * 说说文本
		 */
		if (text == null || text.equals("")) {// 如果为空，那么控件不显示
			holder.text.setVisibility(View.GONE);
		} else {
			holder.text.setVisibility(View.VISIBLE);
			setTextToPicture(holder.text, text);// 将说说内容以图片的形式展现出来
		}

		/**
		 * 说说图片
		 */
		if (picUrl == null || picUrl.equals("")) {
			holder.pic.setVisibility(View.GONE);
		} else {
			holder.pic.setVisibility(View.VISIBLE);
			holder.pic.setImageResource(R.drawable.message_default);
			ImageDownloader downloader = ImageDownloader.getInstance(MessageDetailActivity.this);
			holder.pic.setImageBitmap(downloader.getBitmap(holder.pic, picUrl));
		}
		holder.pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StaticVarUtil.largePicPath = message.getLarPic();// 得到大图片路径
				StaticVarUtil.smallPicPath = message.getSmaPic();// 小图片路径
				Intent intent = new Intent();
				intent.setClass(MessageDetailActivity.this, PictureActivity.class);
				startActivity(intent);
			}
		});

		/**
		 * 赞按钮
		 */
		if (StaticVarUtil.isPraised(message.getMsgId(), MessageDetailActivity.this)) {
			holder.praise.setBackgroundResource(R.drawable.message_praise_yes);
		} else {
			holder.praise.setBackgroundResource(R.drawable.message_praise_no);
		}
		holder.praise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int messagePraNum = message.getPraNum();
				if (StaticVarUtil.isPraised(message.getMsgId(), MessageDetailActivity.this)) {
					ViewUtil.toastShort("你已经赞过了", MessageDetailActivity.this);
					return;
				}
				message.setPraNum(messagePraNum + 1);// 个数加一
				StaticVarUtil.praise(message.getMsgId(), MessageDetailActivity.this);
				holder.praise.setBackgroundResource(R.drawable.message_praise_yes);
				holder.praise.setText(message.getPraNum() + "");
				// 开启线程进行赞操作

				StaticVarUtil.executorService.submit(new DownLoadThread(praiseHandler, message.getMsgId()));
			}
		});

		/**
		 * 收藏按钮
		 */
		if (StaticVarUtil.isCollect(message.getMsgId(), MessageDetailActivity.this) == true) {
			holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
		} else {
			holder.collect.setBackgroundResource(R.drawable.message_collect_no);
		}
		holder.collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StaticVarUtil.student == null) {
					ViewUtil.toastShort("对不起，收藏请先登录...", MessageDetailActivity.this);
					return;
				}
				if (StaticVarUtil.isCollect(message.getMsgId(), MessageDetailActivity.this) == true) {
					ViewUtil.toastShort("您已经收藏过了", MessageDetailActivity.this);
					holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
					return;
				}
				StaticVarUtil.collect(message.getMsgId(), MessageDetailActivity.this);// 收藏这个说说
				message.setColNum(message.getColNum() + 1);// 设置循环显示的内容
				holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
				holder.collect.setText(message.getColNum() + "");

				StaticVarUtil.executorService.submit(new DownLoadThread(collectHandler, message.getMsgId(), StaticVarUtil.student.getAccount()));
			}
		});

		final EditText etComment = (EditText) findViewById(R.id.etComment);

		/**
		 * 确定发送消息按钮
		 */
		Button butConfirm = (Button) findViewById(R.id.butConfirm);
		butConfirm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StaticVarUtil.student == null) {
					ViewUtil.toastShort("对不起，评论请先登录...", MessageDetailActivity.this);
					return;
				}
				if (etComment.getText().toString() == null || etComment.getText().toString().equals("")) {
					ViewUtil.toastShort("评论不能为空！", MessageDetailActivity.this);
					return;
				}
				// 评论这个说说
				// 另起线程发表评论
				String comment = etComment.getText().toString();
				int account = StaticVarUtil.student.getAccount();
				int messageId = message.getMsgId();

				etComment.setText("");

				StaticVarUtil.executorService.submit(new DownLoadThread(handler, messageId, account, comment));

			}
		});

		listView.setOnMoreListener(new OnMoreButtonListener() {
			@Override
			public void onClick(View v) {
				listView.start();
				page++;
				StaticVarUtil.executorService.submit(new DownLoadThread(handlerCommentMore, message.getMsgId() + "", page + ""));
			}
		});

		StaticVarUtil.executorService.submit(new DownLoadThread(handlerComment, message.getMsgId() + "", page + ""));
	}

	/**
	 * 返回更多评论的handler
	 */
	private Handler handlerCommentMore = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				listView.start();
				break;
			case StaticVarUtil.END_SUCCESS:
				listView.finish();
				adapter.addComments(StaticVarUtil.response);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				listView.setSelection(page * 20);
				break;
			case StaticVarUtil.END_FAIL:
				listView.finish();
				ViewUtil.toastShort("返回评论失败", MessageDetailActivity.this);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				listView.finish();
				ViewUtil.toastShort("网络异常", MessageDetailActivity.this);
				break;
			default:
				break;
			}
		};
	};
	/**
	 * 返回评论的handler
	 */
	private Handler handlerComment = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				progress.setVisibility(View.VISIBLE);
				break;
			case StaticVarUtil.END_SUCCESS:
				progress.setVisibility(View.GONE);
				ArrayList<Comment> allComments = StaticVarUtil.getAllComments(StaticVarUtil.response);
				adapter = new CommentAdapter(allComments, MessageDetailActivity.this);
				listView.setAdapter(adapter);
				adapter.notifyDataSetChanged();
				break;
			case StaticVarUtil.END_FAIL:
				progress.setVisibility(View.GONE);
				ViewUtil.toastShort("返回评论失败", MessageDetailActivity.this);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				progress.setVisibility(View.GONE);
				ViewUtil.toastShort("网络异常", MessageDetailActivity.this);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 评论的handler
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				break;
			case StaticVarUtil.END_SUCCESS:
				ViewUtil.toastShort("评论成功", MessageDetailActivity.this);
				page = 0;
				StaticVarUtil.executorService.submit(new DownLoadThread(handlerComment, message.getMsgId() + "", page + ""));
				break;
			case StaticVarUtil.END_FAIL:
				ViewUtil.toastShort("评论失败", MessageDetailActivity.this);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				ViewUtil.toastShort("网络异常", MessageDetailActivity.this);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 根据返回值进行操作,这个是collect的handler
	 */
	private Handler collectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				break;
			case StaticVarUtil.END_SUCCESS:
				ViewUtil.toastShort("收藏成功", MessageDetailActivity.this);
				break;
			case StaticVarUtil.END_FAIL:
				ViewUtil.toastShort("你已经收藏过了", MessageDetailActivity.this);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				ViewUtil.toastShort("网络异常", MessageDetailActivity.this);
				break;
			default:
				break;
			}
		};
	};

	/**
	 * 根据返回值进行操作,这个是praise的handler
	 */
	private Handler praiseHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
		};
	};

	/**
	 * 将文字转换为图片
	 * 
	 * @param textView
	 * @param text
	 */
	private void setTextToPicture(TextView textView, String text) {
		textView.setText(text);
		for (int i = 1; i <= 140; i++) {
			while (text.contains("[expression" + i + "]")) {
				int start = text.indexOf("[expression" + i + "]");// 13个字母
				int end = 0;
				if (i < 10) {
					end = start + 13;
				} else if (i >= 10 && i < 100) {
					end = start + 14;
				} else if (i >= 100) {
					end = start + 15;
				}
				text = text.substring(0, start) + "<img src=\"" + ExpressionUtil.all.get("expression" + i) + "\" />" + text.substring(end);
			}
		}
		textView.setText(Html.fromHtml(text, imageGetter, null));
	}

	/**
	 * 根据source转变为图片的类
	 */
	private ImageGetter imageGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.valueOf(source);
			Drawable d = MessageDetailActivity.this.getResources().getDrawable(id);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};

	/**
	 * 变量类，将所有列表中的每一个Item都包含在内
	 */
	class ViewHolder {
		ImageView headPhoto;// 头像
		TextView nickname;// 昵称
		TextView date;// 日期
		TextView time;// 时间
		TextView text;// 文本信息
		ImageView pic;// 图片信息
		Button praise;// 赞
		Button comment;// 评论
		Button collect;// 收藏
	}
}
