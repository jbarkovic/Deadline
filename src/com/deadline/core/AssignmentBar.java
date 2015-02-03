package com.deadline.core;

import java.util.Calendar;

import com.deadline.data.android.Data;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class AssignmentBar extends View {
	int width ,height;
	float x, y;
	int numItems = 0;
	Rect rect;
	Paint grad;
	Paint titleTextFormat;
	Data data = new Data ();
	Calendar viewStartDate = Calendar.getInstance ();
	Calendar viewEndDate = Calendar.getInstance ();
	
	TimeCalculator timeCalc = new TimeCalculator ();
	
	// Set-up parameters that influence the scale of this view
	// For example baseUnit=DAY & unitsExposed=7 => a visible window of 7 days (one week per screen)
	TimeUnit baseUnit = TimeUnit.DAY;
	int timeUnitsExposed = 7;
	
	public enum TimeUnit {
		DAY,WEEK,MONTH,YEAR,HOUR,MINUTE,SECOND
	}
	public AssignmentBar (Context context) {
		super(context);
		init();
	}
	
	public AssignmentBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	public AssignmentBar(Context context, AttributeSet attrs, int Style) {
		super(context, attrs);
		init();
	}	
	protected void loadData (Data assignmentData, Calendar viewStart, Calendar viewEnd, int timelineLength) {
		this.viewStartDate = viewStart;
		this.viewEndDate = viewEnd;
		this.timeUnitsExposed = timelineLength;	
		this.data = assignmentData;
	}
	
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		this.width = w;
		this.height = h;
		this.x = getX();
		this.y = getY();
		int [] barPoints = this.timeCalc.getTimelinePositionPoints(w, this.viewStartDate, this.viewEndDate, this.data.end);
		this.rect = new Rect((int) Math.max(this.x, barPoints[1]),0,(int) Math.min(this.x+this.width, barPoints[2]),this.height);
		this.grad.setShader(new LinearGradient(rect.left, rect.top, rect.right, rect.top, Color.BLUE, Color.WHITE, Shader.TileMode.MIRROR));
		System.out.println("BarPoints {"  + barPoints[0] + "," + barPoints[1] + "," + barPoints[2] + "," + barPoints[3] + "}");
	}
	
	private void init() {
		this.grad = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.grad.setStyle(Paint.Style.FILL);
		this.grad.setShader(new LinearGradient(600, 0, 0, 0, Color.BLUE, Color.WHITE, Shader.TileMode.MIRROR));
		this.grad.setDither(true);
		
		this.titleTextFormat = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.titleTextFormat.setStyle(Paint.Style.FILL);
		this.titleTextFormat.setFakeBoldText(true);
		this.titleTextFormat.setARGB(60, 0, 0, 0);
		this.titleTextFormat.setDither(true);
		
		this.timeCalc.MIN_BAR_SIZE = 10;
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawRect(rect, this.grad);
		canvas.drawText(this.data.name, (int) this.x, (int) this.y, this.titleTextFormat);
	}
}