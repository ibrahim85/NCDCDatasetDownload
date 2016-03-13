import java.io.IOException;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Main {


	public static void main(String[] args) throws IOException, UnirestException {
		String[] datasets = {"PRECIP_HLY", "NORMAL_MLY"};
		String[] dateRange = {"2010-01-01", "2010-01-05"};
		String token = "insert your NCDC web services token here";
		 
		DatasetDownloader datasetsDownloader = new DatasetDownloader(token, datasets, dateRange, true);
	}
	
}
