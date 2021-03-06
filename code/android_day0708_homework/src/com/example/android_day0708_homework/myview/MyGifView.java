package com.example.android_day0708_homework.myview;

import android.content.Context;
import android.graphics.Movie;
import android.view.View;
import android.graphics.Canvas;
import android.util.AttributeSet;
import com.example.android_day0708_homework.R;

/**
 * project:com.example.android_day0626_homework.MyView
 * user:VennUser
 * date:2015/6/28
 */
public class MyGifView extends View
{
	private long movieStart;
	private Movie movie;

	//此处必须重写该构造方法
	public MyGifView(Context context, AttributeSet attributeSet)
	{
		super(context, attributeSet);
//以文件流（InputStream）读取进gif图片资源
		movie = Movie.decodeStream(getResources().openRawResource(R.raw.loading));
	}

	protected void onDraw(Canvas canvas)
	{
		long curTime = android.os.SystemClock.uptimeMillis();
//第一次播放
		if (movieStart == 0)
		{
			movieStart = curTime;
		}
		if (movie != null)
		{
			int duraction = movie.duration();
			int relTime = (int) ((curTime - movieStart) % duraction);
			movie.setTime(relTime);
			movie.draw(canvas, 0, 0);
//强制重绘
			invalidate();
		}
		super.onDraw(canvas);
	}
}
