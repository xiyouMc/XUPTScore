package com.xy.fy.adapter;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.xy.fy.main.MainActivity;
import com.xy.fy.main.MessageDetailActivity;
import com.xy.fy.main.PictureActivity;
import com.xy.fy.main.R;
import com.xy.fy.singleton.Message;
import com.xy.fy.util.DownLoadThread;
import com.xy.fy.util.ExpressionUtil;
import com.xy.fy.util.ImageDownloader;
import com.xy.fy.util.StaticVarUtil;
import com.xy.fy.util.ViewUtil;

@SuppressLint("HandlerLeak")
public class ListViewAdapter extends BaseAdapter {

	private Context context;// 上下文
	private ArrayList<Message> allMessage = null;
	private LayoutInflater inflater;
	private boolean isAllowLoad = true;

	@Override
	public View getView(final int position, View view, ViewGroup parent) {

		//如果不加这行代码，很容易溢出
		if (view == null) {
			view = inflater.inflate(R.layout.custom_list_item, null);
		}

		final ViewHolder holder = new ViewHolder();
		holder.collect = (Button) view.findViewById(R.id.collect);
		holder.comment = (Button) view.findViewById(R.id.comment);
		holder.date = (TextView) view.findViewById(R.id.date);
		holder.nickname = (TextView) view.findViewById(R.id.nickname);
		holder.praise = (Button) view.findViewById(R.id.praise);
		holder.time = (TextView) view.findViewById(R.id.time);
		holder.headPhoto = (ImageView) view.findViewById(R.id.headPhoto);
		holder.pic = (ImageView) view.findViewById(R.id.pic);
		holder.text = (TextView) view.findViewById(R.id.text);

		final Message message = allMessage.get(position);
		String headPhotoUrl = message.getHeadPhoto();
		String text = message.getText();
		String picUrl = message.getSmaPic();

		holder.collect.setText(message.getColNum() + "");
		holder.comment.setText(message.getComNum() + "");
		holder.date.setText(message.getDate());
		holder.nickname.setText(message.getNickname());
		holder.praise.setText(message.getPraNum() + "");
		holder.time.setText(message.getTime());

		/**
		 * 头像
		 */
		if (headPhotoUrl != null && headPhotoUrl.equals("") == false) {// 如果不为空，异步加载
			if (isAllowLoad == true) {
				ImageDownloader downloader = ImageDownloader.getInstance(context);
				holder.headPhoto.setImageBitmap(downloader.getBitmap(holder.headPhoto, headPhotoUrl));
			}
		} else {
			holder.headPhoto.setImageResource(R.drawable.default_head_photo_small);
		}

		/**
		 * 点击评论
		 */
		holder.comment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StaticVarUtil.message = message;
				Intent intent = new Intent();
				intent.setClass(context, MessageDetailActivity.class);
				context.startActivity(intent);
			}
		});

		/**
		 * 说说文本
		 */
		if (text == null || text.equals("")) {// 如果为空，那么控件不显示
			holder.text.setVisibility(View.GONE);
		} else {
			holder.text.setVisibility(View.VISIBLE);
			setTextToPicture(holder.text, text);// 将说说内容以图片的形式展现出来
		}
		holder.text.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StaticVarUtil.message = message;
				Intent intent = new Intent();
				intent.setClass(context, MessageDetailActivity.class);
				context.startActivity(intent);
			}
		});
		/**
		 * 说说图片
		 */
		if (picUrl == null || picUrl.equals("")) {
			holder.pic.setVisibility(View.GONE);
		} else {
			holder.pic.setVisibility(View.VISIBLE);
			holder.pic.setImageResource(R.drawable.message_default);
			if (isAllowLoad == true) {// 如果允许加载的话
				ImageDownloader downloader = ImageDownloader.getInstance(context);
				holder.pic.setImageBitmap(downloader.getBitmap(holder.pic, picUrl));
			}
		}
		holder.pic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StaticVarUtil.largePicPath = message.getLarPic();// 得到大图片路径
				StaticVarUtil.smallPicPath = message.getSmaPic();// 小图片路径
				Intent intent = new Intent();
				intent.setClass(context, PictureActivity.class);
				context.startActivity(intent);
			}
		});
		/**
		 * 赞按钮
		 */
		if (StaticVarUtil.isPraised(message.getMsgId(), context)) {
			holder.praise.setBackgroundResource(R.drawable.message_praise_yes);
		} else {
			holder.praise.setBackgroundResource(R.drawable.message_praise_no);
		}
		holder.praise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int messagePraNum = message.getPraNum();
				if (StaticVarUtil.isPraised(message.getMsgId(), context)) {
					ViewUtil.toastShort("你已经赞过了", (MainActivity) context);
					return;
				}
				message.setPraNum(messagePraNum + 1);// 个数加一
				StaticVarUtil.praise(message.getMsgId(), context);
				holder.praise.setBackgroundResource(R.drawable.message_praise_yes);
				holder.praise.setText(message.getPraNum() + "");
				// 开启线程进行赞操作

				StaticVarUtil.executorService.submit(new DownLoadThread(praiseHandler, message.getMsgId()));
			}
		});

		/**
		 * 收藏按钮
		 */
		if (StaticVarUtil.isCollect(message.getMsgId(), context) == true) {
			holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
		} else {
			holder.collect.setBackgroundResource(R.drawable.message_collect_no);
		}
		holder.collect.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (StaticVarUtil.student == null) {
					ViewUtil.toastShort("对不起，收藏请先登录...", (MainActivity) context);
					return;
				}
				if (StaticVarUtil.isCollect(message.getMsgId(), context) == true) {
					ViewUtil.toastShort("您已经收藏过了", (MainActivity) context);
					holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
					return;
				}
				StaticVarUtil.collect(message.getMsgId(), context);// 收藏这个说说
				message.setColNum(message.getColNum() + 1);// 设置循环显示的内容
				holder.collect.setBackgroundResource(R.drawable.message_collect_yes);
				holder.collect.setText(message.getColNum() + "");

				StaticVarUtil.executorService.submit(new DownLoadThread(collectHandler, message.getMsgId(), StaticVarUtil.student.getAccount()));
			}
		});

		return view;
	}

	/**
	 * 根据返回值进行操作,这个是collect的handler
	 */
	private Handler collectHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case StaticVarUtil.START:
				break;
			case StaticVarUtil.END_SUCCESS:
				ViewUtil.toastShort("收藏成功", (MainActivity) context);
				break;
			case StaticVarUtil.END_FAIL:
				ViewUtil.toastShort("你已经收藏过了", (MainActivity) context);
				break;
			case StaticVarUtil.INTERNET_ERROR:
				ViewUtil.toastShort("网络异常", (MainActivity) context);
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
			Drawable d = context.getResources().getDrawable(id);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};

	/**
	 * @param dataResource
	 *        数据源
	 * @param context
	 */
	public ListViewAdapter(String dataResource, Context context) {
		this.context = context;
		this.allMessage = StaticVarUtil.getAllMessage(dataResource);
		this.inflater = LayoutInflater.from(context);
		// 每次记录最后一个说说的信息
		if (allMessage.size() > 0) {
			StaticVarUtil.lastMessageTime = allMessage.get(allMessage.size() - 1).getDate() + " " + allMessage.get(allMessage.size() - 1).getTime();
		}
	}

	/**
	 * 添加数据
	 */
	public void addData(String dataResource) {
		if (this.allMessage != null) {
			this.allMessage.addAll(StaticVarUtil.getAllMessage(dataResource));
		}
	}

	/**
	 * 清空数据
	 */
	public void clearData() {
		if (this.allMessage != null) {
			this.allMessage.clear();
		}
	}

	@Override
	public int getCount() {
		return allMessage.size();
	}

	@Override
	public Object getItem(int position) {
		return allMessage.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 锁住时不允许加载图片
	 */
	public void lock() {
		this.isAllowLoad = false;
	}

	/**
	 * 解锁时加载图片
	 */
	public void unlock() {
		this.isAllowLoad = true;
	}

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
