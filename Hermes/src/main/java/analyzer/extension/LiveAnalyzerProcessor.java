package analyzer.extension;

import analyzer.ParticipantTimeLine;
import difficultyPrediction.statusManager.StatusListener;

public interface LiveAnalyzerProcessor extends RatioFileGenerator{
	ParticipantTimeLine getParticipantTimeLine();

}
