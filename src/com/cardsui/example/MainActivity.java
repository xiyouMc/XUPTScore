package com.cardsui.example;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Toast;

import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.xy.fy.main.R;

public class MainActivity extends Activity {

	private CardUI mCardView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_main);

		// init CardView
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);

		CardStack stack2 = new CardStack();
		stack2.setTitle("REGULAR CARDS");
		mCardView.addStack(stack2);

		// add AndroidViews Cards
		mCardView.addCard(new MyCard("Get the CardsUI view"));//定义卡片 在这块可以设置 学期
		mCardView.addCardToLastStack(new MyCard("for Android at"));
		MyCard androidViewsCard = new MyCard("www.androidviews.net");
		androidViewsCard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse("http://www.androidviews.net/"));
				startActivity(intent);

			}
		});
		androidViewsCard.setOnLongClickListener(new OnLongClickListener() {    		
    		
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(v.getContext(), "This is a long click", Toast.LENGTH_SHORT).show();
				return true;
			}
		
		});
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("http://www.androidviews.net/"));

		mCardView.addCardToLastStack(androidViewsCard);

		CardStack stackPlay = new CardStack();
		stackPlay.setTitle("2013-2014");
		mCardView.addStack(stackPlay);

		// add one card, and then add another one to the last stack.
		mCardView.addCard(new MyCard("成绩"));
	//	mCardView.addCardToLastStack(new MyCard("By Androguide & GadgetCheck"));

		mCardView.addCardToLastStack(new MyPlayCard("第一学期",
				"This card mimics the new Google play cards look", "#33b6ea",
				"#33b6ea", true, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"第二学期",
						"The PlayCards allow you to easily set a menu overflow on your card.\nYou can also declare the left stripe's color in a String, like \"#33B5E5\" for the holo blue color, same for the title color.",
						"#e00707", "#e00707", false, true));

		// add one card
		mCardView
				.addCard(new MyPlayCard(
						"Different Colors for Title & Stripe",
						"You can set any color for the title and any other color for the left stripe",
						"#f2a400", "#9d36d0", false, false));

		mCardView
				.addCardToLastStack(new MyPlayCard(
						"Set Clickable or Not",
						"You can easily implement an onClickListener on any card, but the last boolean parameter of the PlayCards allow you to toggle the clickable background.",
						"#4ac925", "#222222", true, true));

		// draw cards
		mCardView.refresh();
	}

}
