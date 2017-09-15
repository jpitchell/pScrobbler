package com.arn.scrobble.pref

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceFragment
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CollapsingToolbarLayout
import com.arn.scrobble.LFMRequester
import com.arn.scrobble.R
import com.arn.scrobble.Stuff

/**
 * Created by arn on 09/07/2017.
 */

class PrefFragment : PreferenceFragment()/*, SharedPreferences.OnSharedPreferenceChangeListener */{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null)
            setHasOptionsMenu(false)

        (activity.findViewById<CollapsingToolbarLayout>(R.id.toolbar_layout))?.title = getString(R.string.action_settings)
        val vg = activity.findViewById<AppBarLayout>(R.id.app_bar)
        vg?.setExpanded(false, true)

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences)
        val reauth = findPreference("reauth")
        reauth.title = reauth.title.toString() +": " + preferenceManager.sharedPreferences.getString("username", "nobody")
        reauth.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            //open browser or intent here
            preferenceManager.sharedPreferences
                    .edit()
                    .remove("sesskey")
                    .apply()
            LFMRequester(activity).execute(Stuff.CHECK_AUTH)
            true
        }

        var listOfApps = ""
        preferenceManager.sharedPreferences
                .getStringSet(Stuff.APP_WHITELIST, setOf())
                .forEach { listOfApps += it+", " }

        val appList = findPreference("open_app_list")
        appList.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            fragmentManager.beginTransaction()
                    .remove(this)
                    .add(R.id.frame, AppListFragment())
                    .addToBackStack(null)
                    .commit()
            true
        }

        val about = findPreference("about")
        val pName = activity.packageName
        try {
            val pInfo = activity.packageManager.getPackageInfo(pName, 0)
            val version = pInfo.versionName
            about.title = pName + " v" + version
            about.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW,
                        Uri.parse(about.summary.toString()))
                activity.startActivity(browserIntent)
                true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

    }
/*

    override fun onSharedPreferenceChanged(sp: SharedPreferences, key: String) {
        if (key == "master") {
            val `val` = sp.getBoolean("master", true)
            val mxm = findPreference("scrobble_mxmFloatingLyrics") as SwitchPreference
            val yt = findPreference("scrobble_youtube") as SwitchPreference
            mxm.isChecked = `val`
            yt.isChecked = `val`
        }
    }
*/
    override fun onResume() {
        super.onResume()
//        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)

    }

    override fun onPause() {
//        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

}
