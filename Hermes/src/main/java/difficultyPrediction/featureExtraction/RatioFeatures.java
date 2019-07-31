package difficultyPrediction.featureExtraction;

import analyzer.extension.StuckInterval;
import analyzer.extension.StuckPoint;

public interface RatioFeatures {
	public void setEditRatio(double editRatio) ;
	public double getDebugRatio() ;
	public void setDebugRatio(double newVal) ;
	public double getNavigationRatio();
	public void setNavigationRatio(double navigationRatio) ;
	public double getFocusRatio();
	public void setFocusRatio(double focusRatio) ;
	public double getRemoveRatio() ;
	public void setRemoveRatio(double removeRatio) ;
	public double getRemoveClassRatio() ;
	public void setRemoveClassRatio(double removeRatio) ;
	public double getExceptionsPerRun() ;
	public void setExceptionsPerRun(double exceptionsPerRun) ;
	public double getInsertionRatio() ;
	public void setInsertionRatio(double insertionRatio) ;
	public double getDeletionRatio() ;
	public void setDeletionRatio(double deletionRatio) ;
	public double getCommandRate() ;
	public void setCommandRate(double insertionRate) ;
	public double getEditRate() ;
	public void setEditRate(double insertionRate) ;
	public double getInsertionRate() ;
	public void setInsertionRate(double insertionRate) ;
//	public double getDeletionRate() ;
//	public void setDeletionRate(double deletionRate) ;
	public double getDebugRate() ;
	public void setDebugRate(double debugRate) ;
	public double getNavigationRate() ;
	public void setNavigationRate(double navigationRate);
	public double getFocusRate() ;
	public void setFocusRate(double focusRate);
	public double getRemoveRate() ;
	public void setRemoveRate(double removeRate) ;
	public long getSavedTimeStamp();
	public void setSavedTimeStamp(long savedTimeStamp) ;
	public double getEditRatio();
	public StuckPoint getStuckPoint();
	public void setStuckPoint(StuckPoint stuckPoint);
	public StuckInterval getStuckInterval();
	public void setStuckInterval(StuckInterval stuckInterval);
//	public double geOther1Feature() ;
//	public void setOther1Feature(double newVal) ;
//	public double geOther2Feature() ;
//	public void setOther2Feature(double newVal) ;
//	public double geOther3Feature() ;
//	public void setOther3Feature(double newVal) ;
//	public double geOther4Feature() ;
//	public void setOther4Feature(double newVal) ;
//	public double geOther5Feature() ;
//	public void setOther5Feature(double newVal) ;
//	public double geOther6Feature() ;
//	public void setOther6Feature(double newVal) ;
//	public double geOther7Feature() ;
//	public void setOther7Feature(double newVal) ;
//	public double geOther8Feature() ;
//	public void setOther8Feature(double newVal) ;
//	public double geOther9Feature() ;
//	public void setOther9Feature(double newVal) ;
	public Object getFeature(String aFeatureName);
	public void setFeature(String aFeatureName, Object newVal);
	public int getElapsedTime();
	public void setElapsedTime(int newVal);
	public String getCommandString();
	public void setCommandString(String newVal);
	void setEstimatedBusyTime(int newVal);
	int getEstimatedBusyTime();
	

}
