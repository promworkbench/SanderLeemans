package svn45crimes;

import java.io.File;
import java.io.IOException;

public interface Noise {

	public String getTitle();

	public String getLatexTitle();

	public void compute(File inputLogFile, Call call, File outputLogFile) throws IOException, Exception;

}
