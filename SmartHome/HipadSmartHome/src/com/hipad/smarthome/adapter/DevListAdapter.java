package com.hipad.smarthome.adapter;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.hipad.smart.device.CommonDevice;
import com.hipad.smart.device.Device.Category;
import com.hipad.smart.kettle.v14.KettleStatusInfo;
import com.hipad.smart.kettle.v14.KettleStatusInfo.KettleState;
import com.hipad.smarthome.DeviceEditActivity;
import com.hipad.smarthome.DeviceListActivity;
import com.hipad.smarthome.R;
import com.hipad.smarthome.utils.CommonViewDevice;
import com.hipad.smarthome.utils.SmallTools;

/**
 * @author EthanChung
 */
public class DevListAdapter extends ArrayAdapter<CommonViewDevice> {

	Context context;
	int layoutResourceId;
	ArrayList<CommonViewDevice> data = null;
	LayoutInflater inflater;
	KettleHolder holder = null;
	Map<String, View> map;
	Timer timer = null;

	public DevListAdapter(Context context, int layoutResourceId,
			Map<String, View> map, ArrayList<CommonViewDevice> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
		inflater = ((Activity) context).getLayoutInflater();
		this.map = map;
	}

	@Override
	public View getView(final int position, View row, ViewGroup parent) {

		if (row == null) {
			row = inflater.inflate(layoutResourceId, parent, false);
			holder = new KettleHolder();
			holder.context = row.getContext();
			holder.imgIcon = (ImageView) row.findViewById(R.id.dev_icon);
			holder.name = (TextView) row.findViewById(R.id.dev_name);
			holder.status_network = (ImageView) row
					.findViewById(R.id.dev_net_sta_img);
			holder.status_operation = (TextView) row
					.findViewById(R.id.dev_opt_sta);
			holder.temperature = (TextView) row.findViewById(R.id.dev_temp);
			holder.temperature_unit = (ImageView) row
					.findViewById(R.id.dev_temp_unit);
			holder.edit = (ImageView) row.findViewById(R.id.dev_edit);
			holder.delete = (ImageView) row.findViewById(R.id.dev_delete);

			row.setTag(holder);
		} else
			holder = (KettleHolder) row.getTag();

		int color = Color.argb(40, 0, 0, 0);
		row.setBackgroundColor(color);

		final CommonViewDevice commonViewDevice = data.get(position);

		if (commonViewDevice != null) {
			if (DeviceListActivity.D)
				SmallTools.showInfoLog(context.getClass().getName(), "id:"
						+ commonViewDevice.getDeviceId() + ", name:"
						+ commonViewDevice.getName() + ", 网络状态:"
						+ commonViewDevice.getNetworkState());
			// if(imgs.get(itemInfo.getCategory()) != null){
			// gridItem.imgLayout.setBackgroundResource(R.drawable.circle_device_selector);
			/**
			 * device name
			 */
			holder.name.setText(commonViewDevice.getName());

			/**
			 * device category
			 */
			// TODO
			// Other device should be defined "Category"
			holder.imgIcon.setImageResource(R.drawable.device_kettle);

			/**
			 * device network status
			 */
			holder.status_network
					.setImageResource(commonViewDevice.getNetworkState() == CommonViewDevice.NETWORK_STATE_ONLINE_REMOTE ? R.drawable.network_remote
							: commonViewDevice.getNetworkState() == CommonViewDevice.NETWORK_STATE_ONLINE_LOCAL ? R.drawable.lan
									: R.drawable.network_offline);

			holder.edit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (commonViewDevice.getCommonDevice() == null) {
						((DeviceListActivity) context)
								.showToastShort("无法连接云端服务器!");
						return;
					}
					Bundle bundle = new Bundle();
					bundle.putParcelable(CommonDevice.EXTRA_DEVICE,
							commonViewDevice.getCommonDevice());
					Intent intent = new Intent();
					intent.setClass(context, DeviceEditActivity.class);
					intent.putExtras(bundle);
					((DeviceListActivity) context).startActivityForResult(
							intent, 0);
				}
			});

			holder.delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					if (commonViewDevice.getCommonDevice() == null) {
						((DeviceListActivity) context)
								.showToastShort("无法连接云端服务器!");
						return;
					}
					Dialog dialog = new UserDialog(context, R.style.MyDialog,
							commonViewDevice);
					dialog.setContentView(R.layout.dialog);
					dialog.show();
				}
			});

			KettleStatusInfo info;

			if (commonViewDevice.getNetworkState() == CommonViewDevice.NETWORK_STATE_OFFLINE) {
				holder.temperature.setVisibility(View.INVISIBLE);
				holder.temperature_unit.setVisibility(View.INVISIBLE);
				holder.status_operation.setVisibility(View.INVISIBLE);

			} else if ((info = commonViewDevice.getInfo()) != null) {
				if (DeviceListActivity.D)
					SmallTools.showInfoLog(context.getClass().getName(),
							"temperature=" + info.getCurrentTemperature()
									+ ", operation=" + info.getState());

				/**
				 * device temperature
				 */
				if (info.getState() == KettleState.ERROR_NTC
						|| info.getState() == KettleState.NOTIFY_HUNG) {
					holder.temperature.setVisibility(View.INVISIBLE);
					holder.temperature_unit.setVisibility(View.INVISIBLE);

				} else {
					holder.temperature.setVisibility(View.VISIBLE);
					holder.temperature_unit.setVisibility(View.VISIBLE);
					holder.temperature.setText(String.valueOf(info
							.getCurrentTemperature()));
					holder.temperature.setTextColor(info
							.getCurrentTemperature() < 31 ? context
							.getResources().getColor(R.color.temperature_low)
							: info.getCurrentTemperature() > 70 ? context
									.getResources().getColor(
											R.color.temperature_high) : context
									.getResources().getColor(
											R.color.temperature_meddle));
					holder.temperature_unit
							.setImageResource(info.getCurrentTemperature() < 31 ? R.drawable.ico_temp_c1
									: info.getCurrentTemperature() > 70 ? R.drawable.ico_temp_c3
											: R.drawable.ico_temp_c2);
				}
				/**
				 * device operation status
				 */
				holder.status_operation.setVisibility(View.VISIBLE);
				holder.status_operation
						.setText(info.getState() == KettleState.STATE_STANDBY ? "空闲"
								: info.getState() == KettleState.STATE_HEATING ? "煮水"
										: info.getState() == KettleState.STATE_BOILED_TO_KEEP_TEMPC ? "冷却"
												: info.getState() == KettleState.STATE_KEEP_TEMPC ? "保温"
														: info.getState() == KettleState.NOTIFY_HUNG ? "水壶提起"
																: info.getState() == KettleState.ERROR_NTC ? "设备故障"
																		: info.getState() == KettleState.WARN_BAD_WATER ? "水质低劣"
																				: info.getState() == KettleState.WARN_NO_WATER ? "缺水"
																						: "未知");
			}
			map.put(commonViewDevice.getDeviceId(), row);
		}
		return row;
	}
}