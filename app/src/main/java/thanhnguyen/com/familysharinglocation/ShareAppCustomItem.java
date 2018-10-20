package thanhnguyen.com.familysharinglocation;

import android.graphics.drawable.Drawable;

public class ShareAppCustomItem {
	public String name = null;
	public Drawable icon =  null;
	public String packageClassName =  null;
	public String className =  null;

	   
	public ShareAppCustomItem(String name, Drawable icon, String packageClassName, String className) {
		this.name = name;
		this.icon = icon;
		this.packageClassName = packageClassName;
		this.className = className;
	}
	   
	public String getName() {
		return name;
	}
	public Drawable getIcon() {
		return icon;
	}

	public String getPackageClassName() {
		return packageClassName;
	}

	public void setPackageClassName(String packageClassName) {
		this.packageClassName = packageClassName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}


}
