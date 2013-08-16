package com.money.manager.ex.core;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.money.manager.ex.notifications.RepeatingTransactionReceiver;
import com.money.manager.ex.preferences.PreferencesConstant;

public class MoneyManagerBootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			// compose intent
			Intent i = new Intent(context, RepeatingTransactionReceiver.class);
			PendingIntent pending = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
			// take hour to start
			String hour = PreferenceManager.getDefaultSharedPreferences(context).getString(PreferencesConstant.PREF_REPEATING_TRANSACTION_CHECK, "08:00");
			// take a calendar and current time
			Calendar calendar = Calendar.getInstance();
			Calendar currentCalendar = Calendar.getInstance();
			currentCalendar.setTimeInMillis(System.currentTimeMillis());
			// set time preferences
			calendar.add(Calendar.DAY_OF_YEAR, currentCalendar.get(Calendar.DAY_OF_YEAR));
			calendar.set(Calendar.SECOND, currentCalendar.get(Calendar.SECOND));
			calendar.set(Calendar.MILLISECOND, currentCalendar.get(Calendar.MILLISECOND));
			calendar.set(Calendar.DATE, currentCalendar.get(Calendar.DATE));
			calendar.set(Calendar.MONTH, currentCalendar.get(Calendar.MONTH));
			calendar.set(Calendar.YEAR, currentCalendar.get(Calendar.YEAR));
			calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.substring(0, 2)));
			calendar.set(Calendar.MINUTE, Integer.parseInt(hour.substring(3, 5)));
			// add one day if hour was passed
			if (calendar.getTimeInMillis() < currentCalendar.getTimeInMillis()) {
				calendar.add(Calendar.DATE, 1);
			}
			// cancel old pending intent
			alarmManager.cancel(pending);
			// start alarmanager			
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pending);
		} catch (Exception e) {
			Log.e(MoneyManagerBootReceiver.class.getSimpleName(), e.getMessage());
		}
	}

}
