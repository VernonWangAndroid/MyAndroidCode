package com.example.android_day0626_fragmentmanager.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.android_day0626_fragmentmanager.R;

/**
 * project:com.example.android_day0626_fragmentmanager.fragment
 * user:VennUser
 * date:2015/6/26
 */
public class RegisterFragmentStatus extends Fragment
{
	public RegisterFragmentStatus()
	{

	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.registerstate_fragment, container, false);
	}
}