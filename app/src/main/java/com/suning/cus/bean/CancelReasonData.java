package com.suning.cus.bean;

import com.suning.cus.constants.ArrayData;

import java.util.HashMap;
import java.util.Map;

public class CancelReasonData {
	public static class DataModel {

		public String type;
		public String codeGroup;
		public String code;
		public String desc;

		public DataModel(String type, String codeGroup, String code, String desc) {
			this.type = type;
			this.codeGroup = codeGroup;
			this.code = code;
			this.desc = desc;
		}

	}

	String[][] mCancleData = ArrayData.mCancleData;
	public Map<String, Map<String, DataModel>> mMap = new HashMap<String, Map<String, DataModel>>();
	DataModel mData;

	public void init() {
		for (String i[] : mCancleData) {
			Map<String, DataModel> map = new HashMap<String, DataModel>();
			for (String j : i) {
				mData = new DataModel(j.split("-")[0], j.split("-")[1], j
						.split("-")[2], j.split("-")[3]);
				map.put(mData.desc, mData);
			}
			mMap.put(mData.type, map);
		}

	}

	public static void main(String[] args) {
		CancelReasonData t = new CancelReasonData();
		t.init();

		String type = "ZS01";
		
		Map<String, DataModel> l = t.mMap.get(type);
		
		for (DataModel m : l.values()) {
		}
		DataModel one = l.get("服务订单重复");
	}
}
