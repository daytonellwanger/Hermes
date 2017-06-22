package analyzer;

public class AnalyzerFactory {
	static Analyzer singleton;
	public static void setSingleton(Analyzer singleton) {
		AnalyzerFactory.singleton = singleton;
	}
	public static Analyzer getSingleton() {
		if (singleton == null)
			singleton = new AnAnalyzer();
		return singleton;
	}

}
