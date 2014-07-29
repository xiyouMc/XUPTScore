package com.xy.fy.view;

import java.util.ArrayList;

public class HistoryCollege {
	public static ArrayList<String> historyFather = new ArrayList<String>();
	public static ArrayList<ArrayList<String>> allHistory = new ArrayList<ArrayList<String>>();

	public static void initData(ArrayList<CharSequence> allCollege) {
		if (!historyFather.contains("所有大学")) {
			historyFather.add("所有大学");
		}

		if (allCollege != null) {
			System.out.println("选择大学时候读以往历史" + allCollege.toString());
			for (int i = 0; i < allCollege.size(); i++) {
				if (!historyFather.contains(allCollege.get(i).toString())) {
					historyFather.add(allCollege.get(i).toString());
				}
			}
		}
		allHistory.add(historyFather);
	}
}
